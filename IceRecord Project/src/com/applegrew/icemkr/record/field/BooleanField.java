package com.applegrew.icemkr.record.field;

import com.applegrew.icemkr.record.field.FieldType.ScalarType;
import com.applegrew.icemkr.record.field.scalartype.IBoolScalar;

@FieldRequirements(scalarType = ScalarType.BOOLEAN)
public class BooleanField extends Field<Boolean> implements IBoolScalar {

    @Override
    public Boolean getScalarValue() {
        return value;
    }

    @Override
    public boolean setScalarValue(Object value, boolean isLoadedValue) {
        if (value == null) {
            this.value = false;
            if (isLoadedValue)
                this.loadedValue = this.value;
            return true;
        } else if (value instanceof Boolean) {
            this.value = value.equals(Boolean.TRUE);
            if (isLoadedValue)
                this.loadedValue = this.value;
            return true;
        } else if (value instanceof Integer) {
            this.value = value.equals(new Integer(1));
            if (isLoadedValue)
                this.loadedValue = this.value;
            return true;
        }
        return false;
    }

    @Override
    public void setValueFromDefault(String defaultValueExpression) {
        if (defaultValueExpression != null && !defaultValueExpression.isEmpty()
                && defaultValueExpression.equalsIgnoreCase("true"))
            value = true;
        else
            value = false;
    }

}
