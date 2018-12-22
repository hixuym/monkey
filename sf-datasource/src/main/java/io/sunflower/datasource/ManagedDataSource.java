package io.sunflower.datasource;

import io.sunflower.lifecycle.Managed;

import javax.sql.DataSource;

public interface ManagedDataSource extends DataSource, Managed {

}
