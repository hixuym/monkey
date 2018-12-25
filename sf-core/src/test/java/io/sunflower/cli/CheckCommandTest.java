package io.sunflower.cli;

import io.sunflower.Application;
import io.sunflower.Configuration;
import io.sunflower.setup.Bootstrap;
import io.sunflower.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CheckCommandTest {

    private static class MyApplication extends Application<Configuration> {

        @Override
        public void run(Configuration configuration, Environment environment) {
        }
    }

    private final MyApplication application = new MyApplication();
    private final CheckCommand<Configuration> command = new CheckCommand<>(application);

    @SuppressWarnings("unchecked")
    private final Bootstrap<Configuration> bootstrap = mock(Bootstrap.class);
    private final Namespace namespace = mock(Namespace.class);
    private final Configuration configuration = mock(Configuration.class);

    @Test
    public void hasAName() throws Exception {
        assertThat(command.getName())
                .isEqualTo("check");
    }

    @Test
    public void hasADescription() throws Exception {
        assertThat(command.getDescription())
                .isEqualTo("Parses and validates the configuration file");
    }

    @Test
    public void doesNotInteractWithAnything() throws Exception {
        command.run(bootstrap, namespace, configuration);

        verifyZeroInteractions(bootstrap, namespace, configuration);
    }
}
