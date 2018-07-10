package com.iodesystems.fn;

import java.util.List;

class SizedIndexAccessor {

  private final List<Integer> wrapped;

  public SizedIndexAccessor(List<Integer> wrapped) {
    this.wrapped = wrapped;
  }

  public int getSize() {
    return wrapped.size();
  }

  public Integer get(Integer index) {
    return wrapped.get(index);
  }
}
