package cloudwall.appconfig.example.hello;

import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Example plugin which renders Hello, World in red.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class AnsiGreetingRenderer implements GreetingRenderer {
    static {
        AnsiConsole.systemInstall();
    }

    @Override
    public void renderGreeting(String rawGreeting) {
        System.out.println(ansi().fg(GREEN).a(rawGreeting).reset().toString());
    }
}
