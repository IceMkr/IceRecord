package com.applegrew.icemkr.record.field;

import com.applegrew.icemkr.record.field.FieldType.ScalarType;
import com.applegrew.icemkr.record.field.scalartype.IIntScalar;

@FieldRequirements(scalarType = ScalarType.INTEGER)
public class IntegerField extends Field<Integer> implements IIntScalar {

    @Override
    public Integer getScalarValue() {
        return value;
    }

    @Override
    public Integer getDefaultValue() {
        return null;
    }

    @Override
    public boolean setScalarValue(Object value, boolean isLoadedValue) {
        if (value == null) {
            this.value = null;
        } else if (value instanceof Integer) {
            this.value = (Integer) value;
        } else
            return false;
        if (isLoadedValue)
            this.loadedValue = this.value;
        return true;
    }

}
