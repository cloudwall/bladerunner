package cloudwall.appconfig.example.hello;

/**
 * Plugin which renders a greeting to the console.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class PlaintextGreetingRenderer implements GreetingRenderer {
    @Override
    public void renderGreeting(String rawGreeting) {
        System.out.println(rawGreeting);
    }
}
