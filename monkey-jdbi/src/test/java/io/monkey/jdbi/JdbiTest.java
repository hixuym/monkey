package io.monkey.jdbi;

import com.codahale.metrics.jdbi3.strategies.TimedAnnotationNameStrategy;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.monkey.datasource.DataSourceFactory;
import io.monkey.lifecycle.LifeCycle;
import io.monkey.logging.BootstrapLogging;
import io.monkey.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class JdbiTest {
    static {
        BootstrapLogging.bootstrap();
    }

    private Environment environment;

    private Jdbi dbi;
    private GameDao dao;

    @Before
    public void setUp() throws Exception {

        environment = new Environment();

        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.setUrl("jdbc:h2:mem:jdbi3-test");
        dataSourceFactory.setUser("sa");
        dataSourceFactory.setDriverClass("org.h2.Driver");
        dataSourceFactory.setAutoCommitByDefault(true);
        dataSourceFactory.asSingleConnectionPool();

        dbi = new JdbiFactory(new TimedAnnotationNameStrategy()).build(environment, dataSourceFactory);
        dbi.useTransaction(h -> {
            h.createScript(Resources.toString(Resources.getResource("schema.sql"), Charsets.UTF_8)).execute();
            h.createScript(Resources.toString(Resources.getResource("data.sql"), Charsets.UTF_8)).execute();
        });
        dao = dbi.onDemand(GameDao.class);
        for (LifeCycle lc : environment.lifecycle().getManagedObjects()) {
            lc.start();
        }
    }

    @After
    public void tearDown() throws Exception {
        for (LifeCycle lc : environment.lifecycle().getManagedObjects()) {
            lc.stop();
        }
    }

    @Test
    public void fluentQueryWorks() {
        dbi.useHandle(h -> assertThat(h.createQuery("SELECT id FROM games " +
            "WHERE home_scored>visitor_scored " +
            "AND played_at > :played_at")
            .bind("played_at", LocalDate.of(2016, 2, 15))
            .mapTo(Integer.class)
            .collect(Collectors.toList())).containsOnly(2, 5));
    }

    @Test
    public void canAcceptOptionalParams() {
        assertThat(dao.findHomeTeamByGameId(Optional.of(4))).contains("Dallas Stars");
        assertThat(environment.metrics().timer("game-dao.findHomeTeamByGameId").getCount()).isEqualTo(1);
    }

    @Test
    public void canAcceptEmptyOptionalParams() {
        assertThat(dao.findHomeTeamByGameId(Optional.empty())).isEmpty();
        assertThat(environment.metrics().timer("game-dao.findHomeTeamByGameId").getCount()).isEqualTo(1);
    }

    @Test
    public void canReturnImmutableLists() {
        assertThat(dao.findGameIds()).containsExactly(1, 2, 3, 4, 5);
        assertThat(environment.metrics().timer("game-dao.findGameIds").getCount()).isEqualTo(1);
    }

    @Test
    public void canReturnImmutableSets() {
        assertThat(dao.findAllUniqueHomeTeams()).containsOnly("NY Rangers", "Toronto Maple Leafs", "Dallas Stars");
        assertThat(environment.metrics().timer("game-dao.findAllUniqueHomeTeams").getCount()).isEqualTo(1);
    }

    @Test
    public void canReturnOptional() {
        assertThat(dao.findIdByTeamsAndDate("NY Rangers", "Vancouver Canucks",
            LocalDate.of(2016, 5, 14))).contains(2);
        assertThat(environment.metrics().timer("game-dao.findIdByTeamsAndDate").getCount()).isEqualTo(1);
    }

    @Test
    public void canReturnEmptyOptional() {
        assertThat(dao.findIdByTeamsAndDate("Vancouver Canucks", "NY Rangers",
            LocalDate.of(2016, 5, 14))).isEmpty();
        assertThat(environment.metrics().timer("game-dao.findIdByTeamsAndDate").getCount()).isEqualTo(1);
    }

    @Test
    public void worksWithDates() {
        assertThat(dao.getFirstPlayedSince(LocalDate.of(2016, 3, 1)))
            .isEqualTo(LocalDate.of(2016, 2, 15));
        assertThat(environment.metrics().timer("game-dao.getFirstPlayedSince").getCount()).isEqualTo(1);
    }

    @Test
    public void worksWithOptionalDates() {
        Optional<LocalDate> date = dao.getLastPlayedDateByTeams("Toronto Maple Leafs", "Anaheim Ducks");
        assertThat(date).contains(LocalDate.of(2016, 2, 11));
        assertThat(environment.metrics().timer("game-dao.last-played-date").getCount()).isEqualTo(1);
    }

    @Test
    public void worksWithAbsentOptionalDates() {
        assertThat(dao.getLastPlayedDateByTeams("Vancouver Canucks", "NY Rangers")).isEmpty();
        assertThat(environment.metrics().timer("game-dao.last-played-date").getCount()).isEqualTo(1);
    }
}
