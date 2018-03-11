package cloudwall.appconfig.example.kitchensink;

import com.typesafe.config.Config;
import dagger.Module;
import dagger.Provides;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple pluggable Executor to demonstrate the kitchen sink example.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
@SuppressWarnings("WeakerAccess")
@Module
public class KitchenSinkModule {
    @Provides
    public Executor taskExecutor(Config config) {
        return Executors.newFixedThreadPool(config.getInt("numThreads"));
    }
}
