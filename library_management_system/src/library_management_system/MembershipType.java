public enum MembershipType {
    REGULAR(3),
    PREMIUM(5);

    private final int borrowingLimit;

    MembershipType(int borrowingLimit) {
        this.borrowingLimit = borrowingLimit;
    }

    public int getBorrowingLimit() {
        return borrowingLimit;
    }
}
