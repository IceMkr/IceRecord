package com.applegrew.icemkr.record.extension.impl;

import com.applegrew.icemkr.record.extension.IUniqueIdGenerator;
import com.fasterxml.uuid.Generators;

public class DefaultUniqueIdGenerator implements IUniqueIdGenerator {

    @Override
    public String generateNewUniqueId() {
        return Generators.timeBasedGenerator().generate().toString();
    }

    @Override
    public int getIdMaxLength() {
        return 36;
    }

}
