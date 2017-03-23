package com.applegrew.icemkr.record.dialect;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.ResultSetHandler;

import com.applegrew.icemkr.record.dialect.Table.TableMeta;
import com.applegrew.icemkr.record.field.Field;
import com.applegrew.icemkr.record.field.FieldType.ScalarType;

public class IceRecord extends AIceRecordBase {
    protected ResultSet resultSet;

    protected String whereClauseSql; // Could be null if isValid is false

    protected boolean isEmpty;

    protected boolean isRSClosed;

    protected ResultSetHandler<Void> rsHandler = new ResultSetHandler<Void>() {
        @Override
        public Void handle(ResultSet rs) throws SQLException {
            resultSet = rs;
            isEmpty = !resultSet.isBeforeFirst();
            isValid = true;
            return null;
        }
    };

    IceRecord(TableMeta meta, String whereClauseSql) {
        super(meta);
        this.whereClauseSql = whereClauseSql;
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
        if (isRSClosed)
            return false;
        try {
            return resultSet.next();
        } catch (SQLException e) {
            setLastError(IceRecordException.wrap(e));
            e.printStackTrace();
        }
        return false;
    }

    protected void selectFirstRecord() {
        if (!isValid || isRSClosed)
            return;
        try {
            if (!resultSet.isBeforeFirst())
                return;
            this.next();
        } catch (SQLException e) {
            setLastError(IceRecordException.wrap(e));
            e.printStackTrace();
        }
    }

    protected void close() {
        isRSClosed = true;
        try {
            if (!isRSClosed)
                DbUtils.close(resultSet);
        } catch (SQLException e) {
            setLastError(IceRecordException.wrap(e));
            e.printStackTrace();
        }
    }

    protected boolean onFieldInstanceInit(String fieldName, Field<?> f, ScalarType st) {
        try {
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
            return ret;
        } catch (SQLException e) {
            setLastError(IceRecordException.wrap(e));
            e.printStackTrace();
        }
        return false;
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

    public int updateMultiple() {
        if (!isValid)
            return 0;
        close();
        TableHandler h = getHandler();
        int count = h.updateMultipleRecords(meta, whereClauseSql, fieldsMap.values());
        if (count == 0) {
            if (h.hasError())
                setLastError(h.getLastError());
        }
        return count;
    }

    public boolean delete() {
        if (!isValid)
            return false;
        try {
            resultSet.deleteRow();
            return true;
        } catch (SQLException e) {
            setLastError(IceRecordException.wrap(e));
            e.printStackTrace();
        }
        return false;
    }

    public int deleteMultiple() {
        if (!isValid)
            return 0;
        close();
        TableHandler h = getHandler();
        int count = h.deleteMultipleRecords(meta, whereClauseSql);
        if (count == 0) {
            if (h.hasError())
                setLastError(h.getLastError());
        }
        return count;
    }
}
