package io.monkey.jdbi;

import com.codahale.metrics.jdbi3.InstrumentedTimingCollector;
import com.codahale.metrics.jdbi3.strategies.SmartNameStrategy;
import com.codahale.metrics.jdbi3.strategies.StatementNameStrategy;
import io.monkey.datasource.ManagedDataSource;
import io.monkey.datasource.PooledDataSourceFactory;
import io.monkey.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.SqlStatements;
import org.jdbi.v3.core.statement.TemplateEngine;
import org.jdbi.v3.guava.GuavaPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class JdbiFactory {
    private final StatementNameStrategy nameStrategy;

    public JdbiFactory() {
        this(new SmartNameStrategy());
    }

    public JdbiFactory(StatementNameStrategy nameStrategy) {
        this.nameStrategy = nameStrategy;
    }

    /**
     * Build a fully configured {@link Jdbi} instance managed by the DropWizard lifecycle
     * with the configured health check; this method should not be overridden
     * (instead, override {@link #newInstance(ManagedDataSource)} and
     * {@link #configure(Jdbi)})
     *
     * @param environment
     * @param configuration
     * @return A fully configured {@link Jdbi} object using a managed data source
     * based on the specified environment and configuration
     * @see #build(Environment, PooledDataSourceFactory, ManagedDataSource,
     * String)
     */
    public Jdbi build(Environment environment,
                      PooledDataSourceFactory configuration) {
        final ManagedDataSource dataSource = configuration.build(environment.metrics(), environment.healthChecks());
        return build(environment, configuration, dataSource);
    }

    /**
     * Build a fully configured {@link Jdbi} instance managed by the DropWizard lifecycle
     * with the configured health check; this method should not be overridden
     * (instead, override {@link #newInstance(ManagedDataSource)} and
     * {@link #configure(Jdbi)})
     *
     * @param environment
     * @param configuration
     * @param dataSource
     * @return A fully configured {@link Jdbi} object
     */
    public Jdbi build(Environment environment,
                      PooledDataSourceFactory configuration,
                      ManagedDataSource dataSource) {

        // Create the instance
        final Jdbi jdbi = newInstance(dataSource);

        // Manage the data source that created this instance.
        environment.lifecycle().manage(dataSource);

        // Setup the timing collector
        jdbi.setTimingCollector(new InstrumentedTimingCollector(environment.metrics(), nameStrategy));

        if (configuration.isAutoCommentsEnabled()) {
            final TemplateEngine original = jdbi.getConfig(SqlStatements.class).getTemplateEngine();
            jdbi.setTemplateEngine(new NamePrependingTemplateEngine(original));
        }

        configure(jdbi);

        return jdbi;
    }

    /**
     * This creates a vanilla {@link Jdbi} instance based on the specified data source;
     * this can be overridden if required
     *
     * @param dataSource
     * @return
     */
    protected Jdbi newInstance(final ManagedDataSource dataSource) {
        return Jdbi.create(dataSource);
    }

    /**
     * Overridable function to allow extra customization of the created {@link Jdbi}
     * instance.
     *
     * <p>
     * If this is overridden it is strongly recommend that
     * {@code super.configure(jdbi, configuration)} is invoked before any other
     * changes are made if you intend to use the default as a base so that the
     * customized settings will supersede the defaults
     * </p>
     *
     * @param jdbi
     */
    protected void configure(final Jdbi jdbi) {
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.installPlugin(new GuavaPlugin());
    }
}
