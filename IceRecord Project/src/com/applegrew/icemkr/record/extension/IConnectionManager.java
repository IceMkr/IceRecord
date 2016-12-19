package com.applegrew.icemkr.record.extension;

import javax.sql.DataSource;

import com.applegrew.icemkr.record.dialect.IceRecordException;

public interface IConnectionManager {

    DataSource getSchemalessDataSource();

    DataSource getDataSource();

    String getDBName();

    boolean isSchemaExists();
    
    boolean isSchemaExists(String dbName);

    boolean isTableExists(String tableName);

    RunningDBFlavor getDBFlavor();

    boolean hasError();

    IceRecordException getLastError();

    public static enum RunningDBFlavor {
        MYSQL
    }
}
