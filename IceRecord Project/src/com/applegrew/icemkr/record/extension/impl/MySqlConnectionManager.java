package com.applegrew.icemkr.record.extension.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.applegrew.icemkr.record.dialect.IceRecordException;
import com.applegrew.icemkr.record.extension.IConnectionManager;
import com.mysql.cj.core.util.StringUtils;
import com.mysql.cj.jdbc.MysqlDataSource;

public class MySqlConnectionManager implements IConnectionManager {
    private DBConfig config;

    private boolean hasError;

    private IceRecordException lastError;

    public MySqlConnectionManager(DBConfig conf) {
        this.config = conf;
    }

    @Override
    public boolean hasError() {
        return hasError;
    }

    @Override
    public IceRecordException getLastError() {
        try {
            return lastError;
        } finally {
            hasError = false;
            lastError = null;
        }
    }

    @Override
    public DataSource getDataSource() {
        return getDS(true);
    }

    public DataSource getSchemalessDataSource() {
        return getDS(false);
    }

    private DataSource getDS(boolean withSchema) {
        MysqlDataSource mysqlDS = new MysqlDataSource();
        StringBuffer b = new StringBuffer("jdbc:mysql://");
        b.append(config.host).append(":").append(config.port);
        if (withSchema)
            b.append("/").append(config.dbName);
        mysqlDS.setUrl(b.toString());
        mysqlDS.setUser(config.userName);
        if (!StringUtils.isNullOrEmpty(config.password))
            mysqlDS.setPassword(config.password);
        return mysqlDS;
    }

    @Override
    public String getDBName() {
        return config.dbName;
    }

    @Override
    public boolean isSchemaExists(String dbName) {
        try {
            DatabaseMetaData dmd = getSchemalessDataSource().getConnection().getMetaData();
            ResultSet rs = dmd.getCatalogs();
            while (rs.next())
                if (dbName.equalsIgnoreCase(rs.getString("TABLE_CAT")))
                    return true;
            rs.close();
        } catch (SQLException e) {
            hasError = true;
            lastError = IceRecordException.wrap(e);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isSchemaExists() {
        return isSchemaExists(getDBName());
    }

    @Override
    public boolean isTableExists(String tableName) {
        try {
            DatabaseMetaData dmd = getSchemalessDataSource().getConnection().getMetaData();
            ResultSet rs = dmd.getTables(getDBName(), null, tableName, null);
            while (rs.next())
                if (tableName.equalsIgnoreCase(rs.getString("TABLE_NAME")))
                    return true;
            rs.close();
        } catch (SQLException e) {
            hasError = true;
            lastError = IceRecordException.wrap(e);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public RunningDBFlavor getDBFlavor() {
        return RunningDBFlavor.MYSQL;
    }

    public static class DBConfig {
        public String host;

        public int port;

        public String dbName;

        public String userName;

        public String password;
    }
}
