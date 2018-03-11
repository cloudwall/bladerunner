package cloudwall.appconfig.example.kitchensink;

import cloudwall.appconfig.Blade;
import cloudwall.appconfig.ConfigModule;
import com.typesafe.config.Config;
import dagger.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

/**
 * One of two components to run inside a BladeRunner.
 *
 * @author <a href="mailto:kyle.downey@gmail.com">Kyle F. Downey</a>
 */
public class KitchenSink implements Blade {
    private static final Logger log = LogManager.getLogger(KitchenSink.class);

    private final Executor executor;
    private String id;

    @Inject
    public KitchenSink(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void configure(Config config) {
        this.id = config.getString("id");
        log.info("Configuring KitchenSink[" + id + "]");
    }

    @Override
    public void run() {
        log.info("Running KitchenSink[" + id + "]");
        int numTasks = 4;
        CountDownLatch latch = new CountDownLatch(numTasks);
        for (int i = 0; i < numTasks; i++) {
            int taskNum = i+1;
            executor.execute(() -> {
                log.info("Executing task #" + taskNum);
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    return;
                }
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            // fall through and return
        }
    }

    @Override
    public void shutdown() {
        log.info("Shutting down KitchenSink[" + id + "]");
    }

    @Component(modules={KitchenSinkModule.class, ConfigModule.class})
    public interface KitchenSinkComponent {
        KitchenSink kitchenSink();
    }
}
