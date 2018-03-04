package cloudwall.appconfig.example.hello;

import cloudwall.appconfig.Blade;
import com.typesafe.config.Config;
import dagger.Component;

import javax.inject.Inject;

/**
 * Bare-bones example of Bladerunner, taking all defaults.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class HelloWorld implements Blade {
    private final GreetingRenderer renderer;
    private String greeting;

    @Inject
    public HelloWorld(GreetingRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void configure(Config config) {
        this.greeting = config.getString("greeting");
    }

    @Override
    public void run() {
        renderer.renderGreeting(greeting);
    }

    @Component(modules=HelloWorldModule.class)
    public interface HelloWorldComponent {
        HelloWorld helloWorld();
    }
}
