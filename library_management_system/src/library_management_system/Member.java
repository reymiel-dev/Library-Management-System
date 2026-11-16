import java.io.Serializable;

public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    private String memberId;
    private String name;
    private String password;
    private MembershipType membershipType;
    private boolean active;
    private double unpaidFines;

    public Member(String memberId, String name, String password,
                  MembershipType membershipType) {

        this.memberId = memberId;
        this.name = name;
        this.password = password;
        this.membershipType = membershipType;
        this.active = true;
        this.unpaidFines = 0;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public boolean isActive() {
        return active;
    }

    public double getUnpaidFines() {
        return unpaidFines;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public void setPassword(String password) {
        if (password != null && password.length() >= 6) {
            this.password = password;
        }
    }

    public void setMembershipType(MembershipType membershipType) {
        if (membershipType != null) {
            this.membershipType = membershipType;
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addFine(double amount) {
        if (amount > 0) {
            unpaidFines += amount;
        }
    }

    public void payFine(double amount) {
        if (amount > 0 && amount <= unpaidFines) {
            unpaidFines -= amount;
        }
    }

    public void displayMember() {
        System.out.println("\nMember ID: " + memberId);
        System.out.println("Name: " + name);
        System.out.println("Membership: " + membershipType);
        System.out.println("Borrowing Limit: " +
                membershipType.getBorrowingLimit());
        System.out.println("Status: " +
                (active ? "Active" : "Inactive"));

        System.out.printf("Unpaid Fines: PHP %.2f%n", unpaidFines);
    }
}

