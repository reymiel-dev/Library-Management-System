public interface Borrowable {
    boolean isAvailable();
    void borrowCopy();
    void returnCopy();
}
