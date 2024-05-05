package org.zeroBzeroT.antiillegals.result;

public enum ItemState {

    empty, clean, wasFixed, illegal, isShulkerWithBooks, isBook;

    public boolean shouldCache() {
        return this == clean || wasReverted();
    }

    public boolean wasReverted() {
        return this == wasFixed || this == illegal;
    }

}
