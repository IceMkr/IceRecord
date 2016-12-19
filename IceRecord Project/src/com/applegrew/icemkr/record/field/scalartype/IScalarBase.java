package com.applegrew.icemkr.record.field.scalartype;

public interface IScalarBase {

    Object getScalarValue();

    boolean setScalarValue(Object value, boolean isLoadedValue);

    Object getDefaultValue();

}
