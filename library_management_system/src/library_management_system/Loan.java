import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int MAX_LOAN_DAYS = 14;
    private static final double FINE_PER_DAY = 2.00;

    private String memberId;
    private String itemId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;

    public Loan(String memberId, String itemId) {
        this.memberId = memberId;
        this.itemId = itemId;
        this.borrowDate = LocalDate.now();
        this.dueDate = borrowDate.plusDays(MAX_LOAN_DAYS);
        this.returnDate = null;
        this.fine = 0;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getItemId() {
        return itemId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public double calculateFine() {
        LocalDate date = isReturned() ? returnDate : LocalDate.now();

        long overdueDays = ChronoUnit.DAYS.between(dueDate, date);

        if (overdueDays > 0) {
            return overdueDays * FINE_PER_DAY;
        }

        return 0;
    }

    public void completeReturn() {
        returnDate = LocalDate.now();
        fine = calculateFine();
    }

    public void displayLoan() {
        System.out.println("\nMember ID: " + memberId);
        System.out.println("Item ID: " + itemId);
        System.out.println("Borrow Date: " + borrowDate);
        System.out.println("Due Date: " + dueDate);

        if (isReturned()) {
            System.out.println("Return Date: " + returnDate);
            System.out.printf("Fine: PHP %.2f%n", fine);
            System.out.println("Status: Returned");
        } else {
            System.out.println("Status: Borrowed");
            System.out.printf("Current Fine: PHP %.2f%n",
                    calculateFine());
        }
    }
}
