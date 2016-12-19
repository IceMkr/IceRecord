package com.applegrew.icemkr.record.field;

import com.applegrew.icemkr.record.IceRecordConstants;
import com.applegrew.icemkr.record.IceRecordEnv;
import com.applegrew.icemkr.record.extension.IUniqueIdGenerator;
import com.applegrew.icemkr.record.field.FieldType.ScalarType;

@FieldRequirements(scalarType = ScalarType.STRING, minLength = IceRecordConstants.UNIQUEID_LENGTH)
public class UniqueIdField extends StringField {
    protected IUniqueIdGenerator generator;

    protected String value;

    public UniqueIdField() {
        generator = IceRecordEnv.getExtComponent().createUniqueIdGenerator();
    }

    public boolean setGeneratedUUID() {
        String uuid = generator.generateNewUniqueId();
        return setValue(uuid);
    }
}
