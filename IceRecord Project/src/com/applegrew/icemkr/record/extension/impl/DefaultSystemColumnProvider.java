package com.applegrew.icemkr.record.extension.impl;

import java.util.Set;

import com.applegrew.icemkr.record.IceRecordConstants;
import com.applegrew.icemkr.record.extension.ISystemColumnProvider;
import com.applegrew.icemkr.record.field.FieldMeta;

public class DefaultSystemColumnProvider implements ISystemColumnProvider {

    @Override
    public FieldMeta[] getSystemFields(Set<FieldMeta> fields) {
        boolean hasPrimary = false;
        for (FieldMeta fm : fields) {
            if (fm.getFieldType().isPrimary()) {
                hasPrimary = true;
                break;
            }
        }

        if (hasPrimary)
            return new FieldMeta[0];
        else
            return new FieldMeta[] {
                    FieldMeta.createWithFieldNameAndFieldType(IceRecordConstants.FieldNames.SYS_ID,
                            IceRecordConstants.CoreFieldTypeNames.PRIMARY).build() };
    }

}
