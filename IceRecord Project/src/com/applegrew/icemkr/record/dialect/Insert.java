package com.applegrew.icemkr.record.dialect;

public class Insert {
    private Insert() {
    }

    public static IceRecordInsert into(String tableName) {
        TableHandler h = new TableHandler();
        return new IceRecordInsert(h.getTableMeta(tableName), h);
    }

}
