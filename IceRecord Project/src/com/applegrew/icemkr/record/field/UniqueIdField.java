package com.applegrew.icemkr.record.field;

import com.applegrew.icemkr.record.IceRecordConstants;
import com.applegrew.icemkr.record.IceRecordEnv;
import com.applegrew.icemkr.record.extension.IUniqueIdGenerator;
import com.applegrew.icemkr.record.field.FieldType.ScalarType;

@FieldRequirements(scalarType = ScalarType.STRING, minLength = IceRecordConstants.UNIQUEID_LENGTH)
public class UniqueIdField extends StringField {
    protected IUniqueIdGenerator generator;

    public UniqueIdField() {
        generator = IceRecordEnv.getExtComponent().createUniqueIdGenerator();
    }

    public boolean setGeneratedUUID() {
        String uuid = generator.generateNewUniqueId();
        System.out.println("uuid=" + uuid);
        return setValue(uuid);
    }

    @Override
    public String getScalarValue() {
        return value;
    }
}
