package cloudwall.appconfig;

import cloudwall.appconfig.example.hello.HelloWorld;
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
    public static void main(String[] args) throws Exception {
        Config config = ConfigFactory.load(args[0]);
        Config bladerunnerConfig = config.getConfig("bladerunner");
        List<? extends Config> bladeConfigs = bladerunnerConfig.getConfigList("blades");
        for (Config bladeConfig : bladeConfigs) {
            String clazzName = bladeConfig.getString("component");
            String configRef = bladeConfig.getString("config");

            clazzName = clazzName.replace('$', '_');
            int lastNdxDot = clazzName.lastIndexOf('.');
            if (lastNdxDot < 0) {
                clazzName = "Dagger" + clazzName;
            } else {
                clazzName = clazzName.substring(0, lastNdxDot) + ".Dagger" + clazzName.substring(lastNdxDot + 1);
            }

            Class componentClazz = Class.forName(clazzName);
            Method createMethod = componentClazz.getMethod("create");
            Object component = createMethod.invoke(null, new Object[0]);

            Method[] methods = component.getClass().getMethods();
            Method bladeMethod = null;

            for (Method m : methods) {
                if (Blade.class.isAssignableFrom(m.getReturnType())) {
                    bladeMethod = m;
                    break;
                }
            }

            if (bladeMethod == null) {
                throw new IllegalArgumentException("component class has more than one public method");
            }

            Blade blade = (Blade)bladeMethod.invoke(component);
            blade.configure(config.getConfig(configRef));
            blade.run();
            blade.preShutdown();
            blade.postShutdown();
        }
    }
}
