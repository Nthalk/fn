package com.iodesystems.fn.logic;

public interface FlakeyHandler<T> {

  void handle(T data) throws Exception;
}
