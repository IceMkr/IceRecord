package com.applegrew.icemkr.record.field;

import com.applegrew.icemkr.record.field.FieldType.ScalarType;
import com.applegrew.icemkr.record.field.scalartype.IStringScalar;

@FieldRequirements(scalarType = ScalarType.STRING)
public class StringField extends Field<String> implements IStringScalar {

    @Override
    public String getDefaultValue() {
        return null; // TODO get default value after evaling expression in meta
    }

    @Override
    public String getScalarValue() {
        return value;
    }

    @Override
    public boolean setScalarValue(Object value, boolean isLoadedValue) {
        if (value == null) {
            this.value = null;
        } else if (value instanceof String) {
            this.value = (String) value;
        } else
            return false;
        if (isLoadedValue)
            this.loadedValue = this.value;
        return true;
    }

}