package com.iodesystems.fn.logic;

public interface Where<A> {

  Where<?> NOT_NULL = new WhereNotNull();

  boolean is(A a);
}
