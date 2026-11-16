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

    public String addItem(LibraryItem item) {
        if (item == null || findItem(item.getItemId()) != null) {
            return "Invalid item or duplicate ID.";
        }

        items.add(item);
        return "Item added successfully.";
    }

    public String registerMember(Member member) {
        if (member == null || findMember(member.getMemberId()) != null) {
            return "Invalid member or duplicate ID.";
        }

        members.add(member);
        return "Member registered successfully.";
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

    public String borrowItem(String memberId, String itemId) {

        Member member = findMember(memberId);
        LibraryItem item = findItem(itemId);

        if (member == null || item == null) {
            return "Member or item not found.";
        }

        if (!member.isActive()) {
            return "Account is inactive.";
        }

        if (member.getUnpaidFines() > 0) {
            return "Pay unpaid fines before borrowing.";
        }

        if (countActiveLoans(memberId) >=
                member.getMembershipType().getBorrowingLimit()) {

            return "Borrowing limit reached.";
        }

        if (!item.isAvailable()) {
            return "No available copies.";
        }

        item.borrowCopy();
        loans.add(new Loan(memberId, itemId));

        return "Item borrowed successfully. \nDue Date: " +
                java.time.LocalDate.now().plusDays(14);
    }

    public String returnItem(String memberId, String itemId) {

        for (Loan loan : loans) {

            if (loan.getMemberId().equalsIgnoreCase(memberId)
                    && loan.getItemId().equalsIgnoreCase(itemId)
                    && !loan.isReturned()) {

                LibraryItem item = findItem(itemId);
                Member member = findMember(memberId);

                if (item == null || member == null) {
                    return "Record not found.";
                }

                loan.completeReturn();
                item.returnCopy();

                double fine = loan.calculateFine();
                member.addFine(fine);

                System.out.printf("Fine: PHP %.2f%n", fine);

                return "Item returned successfully." + String.format("Fine: PHP %.2f%n", fine);
            }
        }

        return "Active loan not found.";
    }

    public String removeItem(String itemId) {

        LibraryItem item = findItem(itemId);

        if (item == null) {
            return "Item not found.";
        }

        if (hasActiveLoanForItem(itemId)) {
            return "Item has active loans.";
        }

        items.remove(item);
        return "Item removed successfully.";
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
