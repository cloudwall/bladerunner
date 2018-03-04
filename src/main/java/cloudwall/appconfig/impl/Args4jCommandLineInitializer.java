package cloudwall.appconfig.impl;

import cloudwall.appconfig.CommandLineInitializer;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.Setter;

import javax.annotation.Nonnull;

/**
 * Default implementation of the CommandLineInitializer hook which
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class Args4jCommandLineInitializer implements CommandLineInitializer {
    private final CmdLineParser parser;

    public Args4jCommandLineInitializer(@Nonnull CmdLineParser parser) {
        this.parser = parser;
    }

    @Override
    public void addOption(@Nonnull Setter setter, @Nonnull Option o) {
        parser.addOption(setter, o);
    }
}
