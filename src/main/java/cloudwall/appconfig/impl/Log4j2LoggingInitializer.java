package cloudwall.appconfig.impl;

import cloudwall.appconfig.LoggingInitializer;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.*;

/**
 * Default logging initialization which sets up console logging with log4j2.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class Log4j2LoggingInitializer implements LoggingInitializer {
    @Override
    public void initLogging(String logConfigPath) {
        InputStream in = getClass().getResourceAsStream(logConfigPath);
        if (in == null) {
            try {
                in = new FileInputStream(new File(logConfigPath));
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("bad logConfig: " + logConfigPath);
            }
        }

        ConfigurationSource source;
        try {
            source = new ConfigurationSource(in);
        } catch (IOException e) {
            throw new IllegalStateException("unable to init log4j2 from " + logConfigPath, e);
        }
        Configurator.initialize(null, source);
    }
}
