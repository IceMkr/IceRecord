package com.applegrew.icemkr.record.dialect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;

import com.applegrew.icemkr.record.dialect.Table.TableMeta;
import com.applegrew.icemkr.record.field.Field;
import com.applegrew.icemkr.record.field.FieldMeta;
import com.applegrew.icemkr.record.field.FieldType.ScalarType;

public class IceRecord {
    protected Map<String, Field<?>> fieldsMap = new HashMap<>();

    protected ResultSet resultSet;

    protected String whereClauseSql; // Could be null if isValid is false

    protected TableMeta meta;

    protected boolean isEmpty;

    protected boolean isValid;

    protected TableHandler handler;

    protected ResultSetHandler<Void> rsHandler = new ResultSetHandler<Void>() {
        @Override
        public Void handle(ResultSet rs) throws SQLException {
            resultSet = rs;
            isEmpty = !resultSet.isBeforeFirst();
            isValid = true;
            return null;
        }
    };

    private boolean hasError;

    private IceRecordException lastError;

    IceRecord(TableMeta meta, String whereClauseSql) {
        this.meta = meta;
        this.whereClauseSql = whereClauseSql;
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

    ResultSetHandler<Void> getRSHandler() {
        return rsHandler;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public boolean next() {
        if (!isValid)
            return false;
        try {
            return resultSet.next();
        } catch (SQLException e) {
            setLastError(IceRecordException.wrap(e));
            e.printStackTrace();
        }
        return false;
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
        return meta.getFieldMeta(fieldName);
    }

    protected Field<?> getFieldInstance(String fieldName) {
        Field<?> f = fieldsMap.get(fieldName);
        if (f == null) {
            FieldMeta fm = getFieldMeta(fieldName);
            if (fm != null) {
                try {
                    ScalarType st = fm.getFieldType().getScalarType();
                    f = fm.getFieldInstance();
                    boolean ret;
                    if (st.equals(ScalarType.FLOAT)) {
                        // FIXME Replace by real col names when we have concept
                        // of virtual col names
                        ret = f.setScalarValue(resultSet.getFloat(fieldName), true);
                    } else if (st.equals(ScalarType.INTEGER)) {
                        ret = f.setScalarValue(resultSet.getInt(fieldName), true);
                    } else if (st.equals(ScalarType.STRING)) {
                        ret = f.setScalarValue(resultSet.getString(fieldName), true);
                    } else if (st.equals(ScalarType.BOOLEAN)) {
                        ret = f.setScalarValue(resultSet.getBoolean(fieldName), true);
                    } else
                        throw new IllegalArgumentException("Unsupported scalar type. Give: " + st);
                    if (!ret)
                        throw new IllegalStateException("Could not set scalar value for " + st);

                    fieldsMap.put(fieldName, f);
                    return f;
                } catch (SQLException e) {
                    setLastError(IceRecordException.wrap(e));
                    e.printStackTrace();
                }
            }
            return null;
        } else
            return f;
    }

    public boolean update() {
        if (!isValid)
            return false;
        boolean hasChanges = false;
        for (String fieldName : fieldsMap.keySet()) {
            Field<?> f = fieldsMap.get(fieldName);
            if (f.hasChanges()) {
                try {
                    ScalarType st = f.getFieldMeta().getFieldType().getScalarType();
                    if (st.equals(ScalarType.FLOAT)) {
                        // FIXME Replace by real col names when we have concept
                        // of virtual col names
                        resultSet.updateFloat(fieldName, (Float) f.getScalarValue());
                    } else if (st.equals(ScalarType.INTEGER)) {
                        resultSet.updateInt(fieldName, (Integer) f.getScalarValue());
                    } else if (st.equals(ScalarType.STRING)) {
                        resultSet.updateString(fieldName, (String) f.getScalarValue());
                    } else if (st.equals(ScalarType.BOOLEAN)) {
                        resultSet.updateBoolean(fieldName, (Boolean) f.getScalarValue());
                    } else
                        throw new IllegalArgumentException("Unsupported scalar type. Give: " + st);
                    hasChanges = true;
                } catch (SQLException e) {
                    setLastError(IceRecordException.wrap(e));
                    e.printStackTrace();
                }
            }
        }
        if (hasChanges)
            try {
                resultSet.updateRow();
                return true;
            } catch (SQLException e) {
                setLastError(IceRecordException.wrap(e));
                e.printStackTrace();
            }
        return false;
    }

    private TableHandler getHandler() {
        if (handler == null)
            handler = new TableHandler();
        return handler;
    }

    public int updateMultiple() {
        if (!isValid)
            return 0;
        TableHandler h = getHandler();
        int count = h.updateMultipleRecords(meta, whereClauseSql, fieldsMap.values());
        if (count == 0) {
            if (h.hasError())
                setLastError(h.getLastError());
        }
        return count;
    }

}
