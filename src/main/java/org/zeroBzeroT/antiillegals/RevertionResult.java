package org.zeroBzeroT.antiillegals;

public class RevertionResult {

    private final int books;
    private final boolean wasReverted;

    public RevertionResult(final int books, final boolean wasReverted) {
        this.books = books;
        this.wasReverted = wasReverted;
    }

    public int books() {
        return books;
    }

    public boolean wasReverted() {
        return wasReverted;
    }

}
