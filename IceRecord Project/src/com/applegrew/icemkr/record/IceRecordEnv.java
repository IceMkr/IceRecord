package com.applegrew.icemkr.record;

public class IceRecordEnv {
    private IceRecordEnv() {
    }

    private static IceRecordExtensionComponent extComponent;

    public static IceRecordExtensionComponent getExtComponent() {
        return extComponent;
    }

    public static void setExtComponent(IceRecordExtensionComponent extComponent) {
        IceRecordEnv.extComponent = extComponent;
    }

}
