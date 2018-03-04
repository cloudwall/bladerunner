package cloudwall.appconfig.impl;

import cloudwall.appconfig.LoggingInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

/**
 * Default logging initialization which sets up console logging with log4j2.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class AnsiConsoleLoggingInitializer implements LoggingInitializer {
    @Override
    public void initLogging() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setStatusLevel(Level.INFO);
        AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").addAttribute("target",
            ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(builder.newLayout("PatternLayout")
            .addAttribute("pattern", "%d %highlight{%p} %C{1.} [%t] %style{%m}{bold,green}%n"));
        builder.add(appenderBuilder);
        builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef("Stdout")));

        Configurator.initialize(builder.build());
    }
}
