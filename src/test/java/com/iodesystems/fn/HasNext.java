package com.iodesystems.fn;

public class HasNext {
    private final int value;
    private final HasNext sibiling;

    public HasNext(int value, HasNext sibiling) {
        this.value = value;
        this.sibiling = sibiling;
    }

    @Override
    public String toString() {
        return "HasNext{" +
                "value=" + value +
                '}';
    }

    public HasNext getNext() {
        return sibiling;
    }

    public Integer getValue() {
        return value;
    }
}
