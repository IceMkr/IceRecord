package com.applegrew.icemkr.record.dialect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

public class Select {
    private Select() {
    }

    public static FromTable from(String tableName) {
        return new FromTable(tableName);
    }

    public static class FromTable {
        private String tableName;

        public FromTable(String tableName) {
            this.tableName = tableName;
        }

        public IceRecord get(String sysId) {
            checkNotNull(sysId);
            TableHandler h = new TableHandler();
            return h.queryTableByPrimaryField(tableName, sysId);
        }

        public IceRecord get(String fieldName, Object value) {
            checkNotNull(fieldName);
            checkNotNull(value);
            TableHandler h = new TableHandler();
            return h.queryTableByUniqueField(tableName, fieldName, value);
        }

        public WhereClause where(String where) {
            checkNotNull(where);
            return new WhereClause(this, where);
        }

        String getTableName() {
            return tableName;
        }
    }

    public static class WhereClause {
        private TableHandler handler = new TableHandler();

        private FromTable fromInfo;

        private String clause;

        private Map<String, Object> params = new HashMap<>();

        WhereClause(FromTable fromInfo, String clause) {
            this.fromInfo = fromInfo;
            this.clause = clause;
        }

        public WhereClause andWhere(String where) {
            checkNotNull(where);
            this.clause = "(" + this.clause + ") AND (" + where + ")";
            return this;
        }

        public WhereClause orWhere(String where) {
            checkNotNull(where);
            this.clause = "(" + this.clause + ") OR (" + where + ")";
            return this;
        }

        /**
         * 
         * @param field
         *            Qualified/unqualified field name. Qualification must use
         *            alias instead of table name, if alias was used.
         * @param value
         * @return
         */
        public WhereClause addParamValue(String field, Object value) {
            this.params.put(field, value);
            return this;
        }

        public IceRecord query() {
            return this.handler.queryTable(this);
        }

        FromTable getFromInfo() {
            return this.fromInfo;
        }

        Map<String, Object> getParams() {
            return params;
        }

        String getClause() {
            return clause;
        }
    }
}
