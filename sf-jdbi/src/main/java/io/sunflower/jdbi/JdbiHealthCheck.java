package io.sunflower.jdbi;

import com.codahale.metrics.health.HealthCheck;
import io.sunflower.db.TimeBoundHealthCheck;
import io.sunflower.util.DirectExecutorService;
import io.sunflower.util.Duration;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.concurrent.ExecutorService;

public class JdbiHealthCheck extends HealthCheck {
    private final Jdbi jdbi;
    private final String validationQuery;
    private final TimeBoundHealthCheck timeBoundHealthCheck;

    public JdbiHealthCheck(ExecutorService executorService, Duration duration, Jdbi jdbi, String validationQuery) {
        this.jdbi = jdbi;
        this.validationQuery = validationQuery;
        this.timeBoundHealthCheck = new TimeBoundHealthCheck(executorService, duration);
    }

    public JdbiHealthCheck(Jdbi jdbi, String validationQuery) {
        this(new DirectExecutorService(), Duration.seconds(0), jdbi, validationQuery);
    }

    @Override
    protected Result check() throws Exception {
        return timeBoundHealthCheck.check(() -> {
                try (Handle handle = jdbi.open()) {
                    handle.execute(validationQuery);
                    return Result.healthy();
                }
            }
        );
    }
}
