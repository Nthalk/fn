package com.iodesystems.fn.logic;

public class WhereNotNull implements Where<Object> {
    @Override
    public boolean is(Object o) {
        return o != null;
    }
}
