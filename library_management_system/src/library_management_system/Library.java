import java.io.Serializable;
import java.util.ArrayList;

public class Library implements Serializable {

    private static final long serialVersionUID = 1L;

    private String libraryName;

    private ArrayList<LibraryItem> items;
    private ArrayList<Member> members;
    private ArrayList<Loan> loans;

    public Library(String libraryName) {
        this.libraryName = libraryName;
        items = new ArrayList<>();
        members = new ArrayList<>();
        loans = new ArrayList<>();
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            libraryName = name.trim();
        }
    }

    public ArrayList<LibraryItem> getItems() {
        return new ArrayList<>(items);
    }

    public ArrayList<Member> getMembers() {
        return new ArrayList<>(members);
    }

    public ArrayList<Loan> getLoans() {
        return new ArrayList<>(loans);
    }

    public void addItem(LibraryItem item) {
        if (item == null || findItem(item.getItemId()) != null) {
            System.out.println("Invalid item or duplicate ID.");
            return;
        }

        items.add(item);
        System.out.println("Item added successfully.");
    }

    public void registerMember(Member member) {
        if (member == null || findMember(member.getMemberId()) != null) {
            System.out.println("Invalid member or duplicate ID.");
            return;
        }

        members.add(member);
        System.out.println("Member registered successfully.");
    }

    public LibraryItem findItem(String id) {
        for (LibraryItem item : items) {
            if (item.getItemId().equalsIgnoreCase(id)) {
                return item;
            }
        }

        return null;
    }

    public Member findMember(String id) {
        for (Member member : members) {
            if (member.getMemberId().equalsIgnoreCase(id)) {
                return member;
            }
        }

        return null;
    }

    public void searchItems(String keyword) {
        boolean found = false;

        for (LibraryItem item : items) {
            boolean matches = item.getTitle().toLowerCase()
                    .contains(keyword.toLowerCase());

            if (item instanceof Book) {
                Book book = (Book) item;

                matches = matches
                        || book.getAuthor().toLowerCase()
                        .contains(keyword.toLowerCase())
                        || book.getIsbn().toLowerCase()
                        .contains(keyword.toLowerCase())
                        || book.getCategory().toLowerCase()
                        .contains(keyword.toLowerCase());
            }

            if (matches) {
                item.displayItem();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching items found.");
        }
    }

    public int countActiveLoans(String memberId) {
        int count = 0;

        for (Loan loan : loans) {
            if (loan.getMemberId().equalsIgnoreCase(memberId)
                    && !loan.isReturned()) {
                count++;
            }
        }

        return count;
    }

    public boolean hasActiveLoanForItem(String itemId) {
        for (Loan loan : loans) {
            if (loan.getItemId().equalsIgnoreCase(itemId)
                    && !loan.isReturned()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasActiveLoansForMember(String memberId) {
        return countActiveLoans(memberId) > 0;
    }

    public void borrowItem(String memberId, String itemId) {

        Member member = findMember(memberId);
        LibraryItem item = findItem(itemId);

        if (member == null || item == null) {
            System.out.println("Member or item not found.");
            return;
        }

        if (!member.isActive()) {
            System.out.println("Account is inactive.");
            return;
        }

        if (member.getUnpaidFines() > 0) {
            System.out.println("Pay unpaid fines before borrowing.");
            return;
        }

        if (countActiveLoans(memberId) >=
                member.getMembershipType().getBorrowingLimit()) {

            System.out.println("Borrowing limit reached.");
            return;
        }

        if (!item.isAvailable()) {
            System.out.println("No available copies.");
            return;
        }

        item.borrowCopy();
        loans.add(new Loan(memberId, itemId));

        System.out.println("Item borrowed successfully.");
        System.out.println("Due Date: " +
                java.time.LocalDate.now().plusDays(14));
    }

    public void returnItem(String memberId, String itemId) {

        for (Loan loan : loans) {

            if (loan.getMemberId().equalsIgnoreCase(memberId)
                    && loan.getItemId().equalsIgnoreCase(itemId)
                    && !loan.isReturned()) {

                LibraryItem item = findItem(itemId);
                Member member = findMember(memberId);

                if (item == null || member == null) {
                    System.out.println("Record not found.");
                    return;
                }

                loan.completeReturn();
                item.returnCopy();

                double fine = loan.calculateFine();
                member.addFine(fine);

                System.out.println("Item returned successfully.");
                System.out.printf("Fine: PHP %.2f%n", fine);

                return;
            }
        }

        System.out.println("Active loan not found.");
    }

    public void removeItem(String itemId) {

        LibraryItem item = findItem(itemId);

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        if (hasActiveLoanForItem(itemId)) {
            System.out.println("Item has active loans.");
            return;
        }

        items.remove(item);
        System.out.println("Item removed successfully.");
    }

    public void deleteMember(String memberId) {

        Member member = findMember(memberId);

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        if (hasActiveLoansForMember(memberId)) {
            System.out.println("Member has active loans.");
            System.out.println("Return all borrowed items first.");
            return;
        }

        members.remove(member);
        System.out.println("Member deleted successfully.");
    }

    public void displayItems() {

        if (items.isEmpty()) {
            System.out.println("No library items registered.");
            return;
        }

        for (LibraryItem item : items) {
            System.out.println("\nType: " + item.getItemType());
            item.displayItem();
        }
    }

    public void displayMembers() {

        if (members.isEmpty()) {
            System.out.println("No registered members.");
            return;
        }

        for (Member member : members) {
            member.displayMember();
        }
    }

    public void displayLoans() {

        if (loans.isEmpty()) {
            System.out.println("No borrowing records.");
            return;
        }

        for (Loan loan : loans) {
            loan.displayLoan();
        }
    }

    public void displayMemberLoans(String memberId) {

        boolean found = false;

        for (Loan loan : loans) {
            if (loan.getMemberId().equalsIgnoreCase(memberId)) {
                loan.displayLoan();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No borrowing history found.");
        }
    }

    public void displayStatistics() {

        int books = 0;
        int magazines = 0;
        int totalCopies = 0;
        int availableCopies = 0;
        int activeLoans = 0;
        int overdueLoans = 0;

        for (LibraryItem item : items) {

            totalCopies += item.getTotalCopies();
            availableCopies += item.getAvailableCopies();

            if (item instanceof Book) {
                books++;
            } else if (item instanceof Magazine) {
                magazines++;
            }
        }

        for (Loan loan : loans) {
            if (!loan.isReturned()) {
                activeLoans++;

                if (loan.calculateFine() > 0) {
                    overdueLoans++;
                }
            }
        }

        System.out.println("\n===== LIBRARY STATISTICS =====");
        System.out.println("Library: " + libraryName);
        System.out.println("Books: " + books);
        System.out.println("Magazines: " + magazines);
        System.out.println("Members: " + members.size());
        System.out.println("Total Copies: " + totalCopies);
        System.out.println("Available Copies: " + availableCopies);
        System.out.println("Borrowed Copies: " +
                (totalCopies - availableCopies));
        System.out.println("Active Loans: " + activeLoans);
        System.out.println("Overdue Loans: " + overdueLoans);
    }
}
