package cloudwall.appconfig;

import com.typesafe.config.Config;

/**
 * An independently-runnable, configurable application component with a simple lifecycle.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public interface Blade extends Runnable {
    default void configure(CommandLineInitializer parser, Config config) { }
    default void preShutdown() { }
    default void postShutdown() { }
}