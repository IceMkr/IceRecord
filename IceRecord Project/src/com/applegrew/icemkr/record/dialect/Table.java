package com.applegrew.icemkr.record.dialect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;

import com.applegrew.icemkr.record.IceRecordEnv;
import com.applegrew.icemkr.record.IceRecordUtil;
import com.applegrew.icemkr.record.extension.ISystemColumnProvider;
import com.applegrew.icemkr.record.field.FieldMeta;

public class Table {
    private Table() {
    }

    public static SysTableCreator setupSysTablesIfNeeded() {
        return new SysTableCreator().setup();
    }

    public static CreateTable createIfNotExists(String tableName) {
        return new CreateTable(tableName);
    }

    public static AlterTable alter(String tableName) {
        return new AlterTable(tableName);
    }

    public static class SysTableCreator {
        private TableHandler handler = new TableHandler();

        private SysTableCreator setup() {
            handler.setupSysTablesIfNeeded();
            return this;
        }

        public boolean hasError() {
            return handler.hasError();
        }

        public IceRecordException getLastError() {
            return handler.getLastError();
        }
    }

    public static class CreateTable {
        private ISystemColumnProvider sysColProvider;

        private TableMeta meta;

        private TableHandler handler = new TableHandler();

        private CreateTable(String tableName) {
            meta = new TableMeta();
            meta.setTableName(tableName);

            sysColProvider = IceRecordEnv.getExtComponent().createSystemColumnProvider();
        }

        public CreateTable withFields(FieldMeta... fieldMetas) {
            meta.addUniqueFieldsMeta(fieldMetas);
            return this;
        }

        public CreateTable thatExtends(String tableName) {
            meta.parentTableName = tableName;
            return this;
        }

        CreateTable finalizeMeta() {
            meta.addUniqueFieldsMeta(sysColProvider.getSystemFields(meta.fields));
            return this;
        }

        public CreateTable commit() {
            finalizeMeta();
            handler.createTable(meta);
            return this;
        }

        public boolean hasError() {
            return handler.hasError();
        }

        public IceRecordException getLastError() {
            return handler.getLastError();
        }
    }

    public static class AlterTable {
        private TableMeta fMeta;

        private AlterTable(String tableName) {
            fMeta = new TableMeta();
            fMeta.tableName = tableName;
            loadTableMeta();
        }

        private void loadTableMeta() {

        }
    }

    public static class TableMeta {
        private String tableName;

        String tableLabel;

        private Set<FieldMeta> fields = new HashSet<FieldMeta>();

        String parentTableName;

        void addUniqueFieldsMeta(FieldMeta[] fields) {
            if (fields == null)
                return;
            for (FieldMeta f : fields)
                this.fields.add(f);
        }

        void addUniqueFieldMeta(FieldMeta field) {
            if (field == null)
                return;
            this.fields.add(field);
        }

        public FieldMeta getFieldMeta(String fieldName) {
            for (FieldMeta f : fields) {
                if (f.getFieldName().equals(fieldName))
                    return f;
            }
            return null;
        }

        void setTableName(String tableName) {
            checkNotNull(tableName);
            this.tableName = tableName;
            this.tableLabel = IceRecordUtil.nameToLabel(tableName);
        }

        public String getTableName() {
            return tableName;
        }

        public Set<FieldMeta> getFieldsMeta() {
            return fields;
        }

        public String getParentTableName() {
            return parentTableName;
        }

        public String getTableLabel() {
            return tableLabel;
        }

        public FieldMeta getPrimaryFieldMeta() {
            for (FieldMeta fm : getFieldsMeta()) {
                if (fm.getFieldType().isPrimary()) {
                    return fm;
                }
            }
            return null;
        }

    }

}
