package com.applegrew.icemkr.record.dialect;

import com.applegrew.icemkr.record.dialect.Table.TableMeta;
import com.applegrew.icemkr.record.field.Field;
import com.applegrew.icemkr.record.field.FieldMeta;
import com.applegrew.icemkr.record.field.FieldType.ScalarType;
import com.applegrew.icemkr.record.field.UniqueIdField;

public class IceRecordInsert extends AIceRecordBase {
    protected boolean isDefaulted;

    IceRecordInsert(TableMeta meta) {
        super(meta);
        checkValid();
    }

    IceRecordInsert(TableMeta meta, TableHandler handler) {
        super(meta, handler);
        checkValid();
    }

    @Override
    protected boolean onFieldInstanceInit(String fieldName, Field<?> f, ScalarType st) {
        if (f.getFieldMeta().getFieldType().isPrimary() && f instanceof UniqueIdField)
            ((UniqueIdField) f).setGeneratedUUID();
        return true;
    }

    private void checkValid() {
        if (meta != null)
            isValid = true;
    }

    public void defaultFieldValues() {
        if (!isValid)
            return;
        if (isDefaulted)
            return;
        // Makes sure that this field is instantiated and initialized from
        // onFieldInstanceInit.
        getPrimaryFieldValue();

        // Looping over all fields and setting default values if they have one.
        for (FieldMeta fm : meta.getFieldsMeta()) {
            String d = fm.getDefaultValueExpression();
            if (d != null && !d.isEmpty()) {
                Field<?> f = getFieldInstance(fm.getFieldName());
                if (f.getValue() == null) {
                    // TODO use script engine to eval it if has expression like
                    // Javascript:...
                    f.setValueFromDefault(fm.getDefaultValueExpression());
                }
            }
        }
        isDefaulted = true;
    }

    public String insert() {
        if (!isValid)
            return null;
        defaultFieldValues();
        String sysId = getPrimaryFieldValue();

        TableHandler h = getHandler();
        int count = h.insertRecord(meta, fieldsMap.values());
        if (count == 1) {
            return sysId;
        }
        return null;
    }

}
