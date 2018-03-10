package cloudwall.appconfig.example.hello;

import dagger.Module;
import dagger.Provides;

/**
 * A plain vanilla module.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
@Module
public class HelloWorldModule1 {
    @Provides
    GreetingRenderer greetingRenderer() {
        return new PlaintextGreetingRenderer();
    }
}
