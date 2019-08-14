package com.iodesystems.fn.data;

public interface FlakeyFrom<A, B> {

  B from(A data) throws Exception;
}
