package com.applegrew.icemkr.record.field;

public class PositiveIntegerField extends IntegerField {

    @Override
    public boolean setValue(Integer value) {
        if (value == null || value >= 0)
            return super.setValue(value);
        return false;
    }

}
