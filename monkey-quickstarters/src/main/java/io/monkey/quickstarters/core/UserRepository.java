package io.monkey.quickstarters.core;

import io.ebean.EbeanServer;
import io.monkey.orm.AbstractRepository;

import javax.inject.Inject;

public class UserRepository extends AbstractRepository<Long, User> {

    @Inject
    protected UserRepository(EbeanServer server) {
        super(User.class, server);
    }
}
