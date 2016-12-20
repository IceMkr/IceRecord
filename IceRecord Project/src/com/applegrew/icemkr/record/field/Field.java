package com.applegrew.icemkr.record.field;

import com.applegrew.icemkr.record.field.FieldType.ScalarType;
import com.applegrew.icemkr.record.field.scalartype.IScalarBase;

public abstract class Field<V> implements IScalarBase {
    protected FieldMeta fieldMeta;
    //
    // protected TableMeta tableMeta;

    protected V value;

    protected V loadedValue;

    // public Field(String fieldName) {
    // this.fieldMeta = new FieldMeta(fieldName);
    // }

    // public Field(String fieldName, String typeName) {
    // this.fieldMeta = new FieldMeta(fieldName);
    // this.fieldMeta.fieldType = FieldType.lookup(typeName);
    // }

    // public void setFieldType(String typeName) {
    // this.fieldMeta.fieldType = FieldType.lookup(typeName);
    // }

    public V getValue() {
        return value;
    }

    public boolean setValue(V value) {
        this.value = value;
        return true;
    }

    void setLoadedValue(V value) {
        this.loadedValue = value;
    }

    public boolean hasChanges() {
        if (loadedValue != null)
            return !loadedValue.equals(value);
        if (value != null)
            return !value.equals(loadedValue);
        return false;
    }

    public String getFieldName() {
        return this.fieldMeta.fieldName;
    }

    // public TableMeta getTableMeta() {
    // return tableMeta;
    // }
    //
    // void setTableMeta(TableMeta tableMeta) {
    // this.tableMeta = tableMeta;
    // }

    public FieldMeta getFieldMeta() {
        return fieldMeta;
    }

    void setFieldMeta(FieldMeta fieldMeta) {
        this.fieldMeta = fieldMeta;
    }

    abstract public void setValueFromDefault(String defaultValueExpression);

    @Override
    public int hashCode() {
        return this.fieldMeta.fieldName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Field))
            return false;
        @SuppressWarnings("rawtypes")
        Field f = (Field) o;
        return f.fieldMeta.fieldName.equals(fieldMeta.fieldName);
    }

    public static FieldRequirementSpec getFieldRequirements(Class<? extends Field<?>> f) {
        FieldRequirements fa = f.getAnnotation(FieldRequirements.class);
        if (fa != null) {
            FieldRequirementSpec fr = new FieldRequirementSpec();
            fr.minLength = fa.minLength();
            fr.scalarType = fa.scalarType();
            return fr;
        }
        return null;
    }

    public static class FieldRequirementSpec {
        public int minLength;

        public ScalarType scalarType;
    }
}
