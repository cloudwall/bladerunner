package cloudwall.appconfig.example.hello;

import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Example plugin which renders Hello, World in red.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class AnsiGreetingRenderer implements GreetingRenderer {
    @Override
    public void renderGreeting(String rawGreeting) {
        try {
            AnsiConsole.systemInstall();
            System.out.println(ansi().eraseScreen().fg(RED).a(rawGreeting).reset().toString());
        } finally {
            AnsiConsole.systemUninstall();
        }
    }
}
