package cloudwall.appconfig;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Main entry point for running Blade-based applications. The general command line pattern is
 * BladeRunner [options] [configFileResourceOrPath], with the default being to run the first application.conf
 * found in the CLASSPATH.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class BladeRunner {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Config config = ConfigFactory.load(args[0]);
        Config bladerunnerConfig = config.getConfig("bladerunner");
        List<? extends Config> bladeConfigs = bladerunnerConfig.getConfigList("blades");
        for (Config bladeConfig : bladeConfigs) {
            String clazzName = bladeConfig.getString("component");
            String configRef = bladeConfig.getString("config");
            Config componentConfig = config.getConfig(configRef);

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

            Blade blade = (Blade)bladeMethod.invoke(component);
            blade.configure(componentConfig);
            blade.run();
            blade.preShutdown();
            blade.postShutdown();
        }
    }
}
