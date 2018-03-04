package cloudwall.appconfig;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.Setter;

import javax.annotation.Nonnull;

/**
 * Thin wrapper around args4j that limits Blades to being able to add options
 * to the command line parser.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public interface CommandLineInitializer {
    void addOption(@Nonnull Setter setter, @Nonnull Option o);
}
