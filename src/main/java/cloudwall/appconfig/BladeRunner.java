package cloudwall.appconfig;

import cloudwall.appconfig.impl.Log4j2LoggingInitializer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Main entry point for running Blade-based applications. The general command line pattern is
 * BladeRunner [options] [configFileResourceOrPath], with the default being to run the first application.conf
 * found in the CLASSPATH.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class BladeRunner {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: " + BladeRunner.class.getName() + " fileOrResource.conf");
        }

        Config config = ConfigFactory.load(args[0]);
        if (!config.hasPath("bladerunner")) {
            System.err.println(args[0] + " does not exist or is invalid (missing bladerunner block)");
        }

        Config bladerunnerConfig = config.getConfig("bladerunner");
        initBladeRunnerCore(bladerunnerConfig);

        List<Blade> blades = new ArrayList<>();
        List<CompletableFuture<Void>> bladeFutures = new ArrayList<>();
        List<? extends Config> bladeConfigs = bladerunnerConfig.getConfigList("blades");
        for (Config bladeConfig : bladeConfigs) {
            String clazzName = bladeConfig.getString("component");
            String configRef = bladeConfig.getString("config");
            Config componentConfig = config.getConfig(configRef);

            try {
                Blade blade = initBlade(clazzName, componentConfig);
                blade.configure(componentConfig);

                blades.add(blade);
                bladeFutures.add(CompletableFuture.runAsync(blade));
            } catch (ClassNotFoundException e) {
                System.err.println("Blade class does not exist: " + clazzName);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                System.err.println("Reflection error loading Blade class: " + clazzName);
                System.err.println(e);
            }
        }

        // wait for all futures to complete
        try {
            CompletableFuture.allOf(bladeFutures.toArray(new CompletableFuture[bladeFutures.size()])).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("unexpected termination", e);
        }

        // initiate shutdown
        for (Blade blade : blades) {
            blade.shutdown();
        }
        System.exit(0);
    }

    @SuppressWarnings("unchecked")
    private static void initBladeRunnerCore(Config bladerunnerConfig) {
        if (bladerunnerConfig.hasPath("logConfig")) {
            Class<? extends LoggingInitializer> logInitializerClazz;
            if (bladerunnerConfig.hasPath("logInitializerClass")) {
                String logInitializerClassName = bladerunnerConfig.getString("logInitializerClass");
                try {
                    logInitializerClazz = (Class<? extends LoggingInitializer>) Class.forName(logInitializerClassName);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("");
                }
            } else {
                logInitializerClazz = Log4j2LoggingInitializer.class;
            }
            try {
                LoggingInitializer logInitializer = logInitializerClazz.getConstructor().newInstance();
                String logConfigPath = bladerunnerConfig.getString("logConfig");
                logInitializer.initLogging(logConfigPath);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalStateException("failed to initialize " + logInitializerClazz.getName(), e);
            }
        } else {
            initDefaultLogConfig();
        }
    }

    private static void initDefaultLogConfig() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.INFO);
        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").addAttribute("target",
            ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout")
            .addAttribute("pattern", "%d %highlight{%p} %C{1.} [%t] %style{%m}{bold,green}%n"));
        builder.add(appenderBuilder);
        builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("Stdout")));

        Configurator.initialize(builder.build());
    }

    @SuppressWarnings("unchecked")
    private static Blade initBlade(String clazzName, Config componentConfig)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        clazzName = clazzName.replace('$', '_');
        int lastNdxDot = clazzName.lastIndexOf('.');
        if (lastNdxDot < 0) {
            clazzName = "Dagger" + clazzName;
        } else {
            clazzName = clazzName.substring(0, lastNdxDot) + ".Dagger" + clazzName.substring(lastNdxDot + 1);
        }

        Class componentClazz = Class.forName(clazzName);
        Class builderClazz = Class.forName(clazzName + "$Builder");

        Object component;
        Method configModuleProvider;
        try {
            configModuleProvider = builderClazz.getMethod("configModule", ConfigModule.class);
            Object builder = componentClazz.getMethod("builder").invoke(null);
            builder = configModuleProvider.invoke(builder, new ConfigModule(componentConfig));
            component = builderClazz.getMethod("build").invoke(builder);
        } catch (NoSuchMethodException e) {
            Method createMethod = componentClazz.getMethod("create");
            component = createMethod.invoke(null);
        }

        Method[] methods = component.getClass().getMethods();
        Method bladeMethod = null;

        for (Method m : methods) {
            if (Blade.class.isAssignableFrom(m.getReturnType())) {
                bladeMethod = m;
                break;
            }
        }

        if (bladeMethod == null) {
            throw new IllegalArgumentException("component class missing public factory method");
        }

        return (Blade)bladeMethod.invoke(component);
    }
}
