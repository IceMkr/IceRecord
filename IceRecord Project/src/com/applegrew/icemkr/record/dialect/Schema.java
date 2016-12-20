package com.applegrew.icemkr.record.dialect;

import java.sql.SQLException;
import java.util.function.Function;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;

import com.applegrew.icemkr.record.IceRecordEnv;
import com.applegrew.icemkr.record.extension.IConnectionManager;

public class Schema {
    private Schema() {
    }

    public static SchemaHandler select(String dbName) {
        return new SchemaHandler(dbName);
    }

    public static class SchemaHandler {
        private IConnectionManager connManager = IceRecordEnv.getExtComponent()
                .createConnectionManager();

        private DataSource schemalessDataSource = connManager.getSchemalessDataSource();

        private String dbName;

        private boolean hasError;

        private IceRecordException lastError;

        SchemaHandler(String dbName) {
            this.dbName = dbName;
        }

        public SchemaHandler deleteIfExists() {
            if (connManager.isSchemaExists(dbName)) {
                QueryRunner runner = new QueryRunner(schemalessDataSource);
                try {
                    runner.update("DROP DATABASE " + dbName + ";");
                } catch (SQLException e) {
                    hasError = true;
                    lastError = IceRecordException.wrap(e);
                    e.printStackTrace();
                }
            }
            return this;
        }

        public SchemaHandler createIfNotExists() {
            if (!connManager.isSchemaExists(dbName)) {
                QueryRunner runner = new QueryRunner(schemalessDataSource);
                try {
                    runner.update("CREATE DATABASE " + dbName + ";");
                } catch (SQLException e) {
                    hasError = true;
                    lastError = IceRecordException.wrap(e);
                    e.printStackTrace();
                }
            }
            return this;
        }

        public SchemaHandlerElse butIfFailedCall(Function<IceRecordException, Void> cb) {
            SchemaHandlerElse e = new SchemaHandlerElse();
            e.hadError = hasError;
            if (hasError) {
                if (cb != null)
                    cb.apply(lastError);
                lastError = null;
                hasError = false;
            }
            return e;
        }

        public class SchemaHandlerElse {
            private boolean hadError;

            public SchemaHandler elseCall(Runnable cb) {
                if (!hadError && cb != null) {
                    cb.run();
                }
                return SchemaHandler.this;
            }
        }
    }
}
