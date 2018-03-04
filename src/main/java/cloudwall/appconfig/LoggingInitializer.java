package cloudwall.appconfig;

/**
 * Optional hook that lets you customize logging library initialization.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public interface LoggingInitializer {
    void initLogging();
}
