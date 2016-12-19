package com.applegrew.icemkr.record;

import com.google.common.base.Splitter;

public class IceRecordUtil {

    public static String nameToLabel(String name) {
        StringBuffer b = new StringBuffer();
        for (String n : Splitter.on('_').omitEmptyStrings().split(name)) {
            n = n.toLowerCase();
            char f = n.charAt(0);
            b.append(Character.toUpperCase(f)).append(n.substring(1)).append(' ');
        }
        return b.toString().trim();
    }
}
