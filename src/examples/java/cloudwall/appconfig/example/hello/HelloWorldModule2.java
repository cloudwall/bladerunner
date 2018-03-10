package cloudwall.appconfig.example.hello;

import com.typesafe.config.Config;
import dagger.Module;
import dagger.Provides;

/**
 * An example of a configurable module.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
@Module
public class HelloWorldModule2 {
    @Provides
    GreetingRenderer greetingRenderer(Config config) {
        if (config.getBoolean("ansi")) {
            return new AnsiGreetingRenderer();
        } else {
            return new PlaintextGreetingRenderer();
        }
    }
}
