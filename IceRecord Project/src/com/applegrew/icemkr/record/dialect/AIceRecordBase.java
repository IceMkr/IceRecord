package com.applegrew.icemkr.record.dialect;

import java.util.HashMap;
import java.util.Map;

import com.applegrew.icemkr.record.IceRecordConstants;
import com.applegrew.icemkr.record.dialect.Table.TableMeta;
import com.applegrew.icemkr.record.field.Field;
import com.applegrew.icemkr.record.field.FieldMeta;
import com.applegrew.icemkr.record.field.FieldType.ScalarType;

public abstract class AIceRecordBase {

    private TableHandler handler;

    protected Map<String, Field<?>> fieldsMap = new HashMap<>();

    protected TableMeta meta;

    protected boolean isValid;

    private boolean hasError;

    private IceRecordException lastError;

    AIceRecordBase(TableMeta meta) {
        this.meta = meta;
    }

    AIceRecordBase(TableMeta meta, TableHandler handler) {
        this.meta = meta;
        this.handler = handler;
    }

    public boolean hasError() {
        return hasError;
    }

    public IceRecordException getLastError() {
        try {
            return lastError;
        } finally {
            hasError = false;
            lastError = null;
        }
    }

    void setLastError(IceRecordException ex) {
        hasError = true;
        lastError = ex;
    }

    public boolean isValid() {
        return this.isValid;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String fieldName, Class<T> clazz) {
        Field<?> f = getFieldInstance(fieldName);
        if (f != null) {
            return (T) f.getValue();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public boolean setValue(String fieldName, Object value) {
        @SuppressWarnings("rawtypes")
        Field f = getFieldInstance(fieldName);
        if (f != null) {
            // Do not let change primary fields for existing records
            if (!f.getFieldMeta().getFieldType().isPrimary())
                return f.setValue(value);
        }
        return false;
    }

    public Object getValue(String fieldName) {
        return getValue(fieldName, Object.class);
    }

    public String getStringValue(String fieldName) {
        return getValue(fieldName, String.class);
    }

    public Boolean getBoolValue(String fieldName) {
        return getValue(fieldName, Boolean.class);
    }

    protected FieldMeta getFieldMeta(String fieldName) {
        if (isValid)
            return meta.getFieldMeta(fieldName);
        return null;
    }

    protected Field<?> getFieldInstance(String fieldName) {
        Field<?> f = fieldsMap.get(fieldName);
        if (f == null) {
            FieldMeta fm = getFieldMeta(fieldName);
            if (fm != null) {
                ScalarType st = fm.getFieldType().getScalarType();
                f = fm.getFieldInstance();
                if (!this.onFieldInstanceInit(fieldName, f, st))
                    throw new IllegalStateException("Field init failed for " + f);

                fieldsMap.put(fieldName, f);
                return f;
            }
            return null;
        } else
            return f;
    }

    protected Field<?> getPrimaryField() {
        Field<?> f = getFieldInstance(IceRecordConstants.FieldNames.SYS_ID);
        if (f != null && f.getFieldMeta().getFieldType().isPrimary())
            return f;
        FieldMeta primaryFieldMeta = meta.getPrimaryFieldMeta();
        if (primaryFieldMeta == null)
            throw new IllegalStateException(
                    " Cannot insert! Given table does not have any primary field! Tabel :"
                            + meta.getTableName());
        return getFieldInstance(primaryFieldMeta.getFieldName());
    }

    public String getPrimaryFieldValue() {
        Field<?> f = getPrimaryField();
        if (f != null)
            return (String) f.getValue();
        return null;
    }

    protected TableHandler getHandler() {
        if (handler == null)
            handler = new TableHandler();
        return handler;
    }

    abstract protected boolean onFieldInstanceInit(String fieldName, Field<?> f, ScalarType st);

}
