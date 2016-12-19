package com.applegrew.icemkr.record.dialect;

public class IceRecordException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IceRecordException(Throwable e) {
        super(e);
    }

    public IceRecordException(String msg) {
        super(msg);
    }

    public static IceRecordException wrap(Throwable e) {
        return new IceRecordException(e);
    }
}
