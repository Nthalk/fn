package com.iodesystems.fn;

public class HasNext {

  private final int value;
  private final HasNext sibling;

  public HasNext(int value, HasNext sibling) {
    this.value = value;
    this.sibling = sibling;
  }

  @Override
  public String toString() {
    return "HasNext{" + "value=" + value + '}';
  }

  public HasNext getNext() {
    return sibling;
  }

  public Integer getValue() {
    return value;
  }
}
