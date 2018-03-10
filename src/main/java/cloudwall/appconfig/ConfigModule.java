package cloudwall.appconfig;

import com.typesafe.config.Config;
import dagger.Module;
import dagger.Provides;

/**
 * Standard module for injecting an instance of the
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
@Module
public class ConfigModule {
    private final Config config;

    public ConfigModule(Config config) {
        this.config = config;
    }

    @Provides
    public Config config() {
        return config;
    }
}
