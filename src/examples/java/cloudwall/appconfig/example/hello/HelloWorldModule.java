package cloudwall.appconfig.example.hello;

import dagger.Module;
import dagger.Provides;

/**
 * TODO: add comment
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
@Module
public class HelloWorldModule {
    @Provides
    GreetingRenderer greetingRenderer() {
        return new AnsiGreetingRenderer();
    }
}
