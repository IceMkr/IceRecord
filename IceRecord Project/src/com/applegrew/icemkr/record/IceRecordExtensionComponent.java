package com.applegrew.icemkr.record;

import javax.inject.Singleton;

import com.applegrew.icemkr.record.extension.IConnectionManager;
import com.applegrew.icemkr.record.extension.ILookupFieldTypes;
import com.applegrew.icemkr.record.extension.ISystemColumnProvider;
import com.applegrew.icemkr.record.extension.IUniqueIdGenerator;

import dagger.Component;

@Singleton
@Component(modules = { IceRecordExtensionsModule.class })
public interface IceRecordExtensionComponent {

    ISystemColumnProvider createSystemColumnProvider();

    IUniqueIdGenerator createUniqueIdGenerator();
    
    IConnectionManager createConnectionManager();
    
    ILookupFieldTypes getLookupFieldTypes();
}
