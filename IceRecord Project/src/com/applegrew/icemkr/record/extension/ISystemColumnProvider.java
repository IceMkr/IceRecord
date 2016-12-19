package com.applegrew.icemkr.record.extension;

import java.util.Set;

import com.applegrew.icemkr.record.field.FieldMeta;

public interface ISystemColumnProvider {

    FieldMeta[] getSystemFields(Set<FieldMeta> existingFieldsMeta);

}
