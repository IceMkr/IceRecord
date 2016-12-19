package com.applegrew.icemkr.record;

import javax.inject.Singleton;

import com.applegrew.icemkr.record.extension.IConnectionManager;
import com.applegrew.icemkr.record.extension.ILookupFieldTypes;
import com.applegrew.icemkr.record.extension.ISystemColumnProvider;
import com.applegrew.icemkr.record.extension.IUniqueIdGenerator;
import com.applegrew.icemkr.record.extension.impl.DefaultSystemColumnProvider;
import com.applegrew.icemkr.record.extension.impl.DefaultUniqueIdGenerator;
import com.applegrew.icemkr.record.extension.impl.MySqlConnectionManager;
import com.applegrew.icemkr.record.field.FieldType;

import dagger.Module;
import dagger.Provides;

@Module
public class IceRecordExtensionsModule {
    private MySqlConnectionManager.DBConfig config;

    public IceRecordExtensionsModule() {
    }

    public IceRecordExtensionsModule(MySqlConnectionManager.DBConfig config) {
        this.config = config;
    }

    @Provides
    ISystemColumnProvider provideSystemColumnProvider() {
        return new DefaultSystemColumnProvider();
    }

    @Provides
    IUniqueIdGenerator provideUniqueIdGenerator() {
        return new DefaultUniqueIdGenerator();
    }

    @Provides
    IConnectionManager provideConnectionManager() {
        return new MySqlConnectionManager(config);
    }

    @Provides
    @Singleton
    ILookupFieldTypes provideLookupFieldTypes() {
        return new FieldType.StaticLookup();
    }
}
