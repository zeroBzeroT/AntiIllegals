package org.zeroBzeroT.antiillegals.result;

public enum ItemState {

    EMPTY, CLEAN, WAS_FIXED, ILLEGAL, IS_SHULKER_WITH_BOOKS, IS_BOOK;

    public boolean shouldCache() {
        return this == CLEAN || wasReverted();
    }

    public boolean wasReverted() {
        return this == WAS_FIXED || this == ILLEGAL;
    }

}
