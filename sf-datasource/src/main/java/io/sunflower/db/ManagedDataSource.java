package io.sunflower.db;

import io.sunflower.lifecycle.Managed;

import javax.sql.DataSource;

public interface ManagedDataSource extends DataSource, Managed {

}
