package cloudwall.appconfig.example.hello;

import cloudwall.appconfig.Blade;
import cloudwall.appconfig.ConfigModule;
import com.typesafe.config.Config;
import dagger.Component;

import javax.inject.Inject;

/**
 * Example of injecting a ConfigModule to allow configuration of provided services.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class HelloWorld2 implements Blade {
    private final GreetingRenderer renderer;
    private String greeting;

    @Inject
    public HelloWorld2(GreetingRenderer renderer) {
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

    @Component(modules={HelloWorldModule2.class, ConfigModule.class})
    public interface HelloWorldComponent {
        HelloWorld2 helloWorld();
    }
}
