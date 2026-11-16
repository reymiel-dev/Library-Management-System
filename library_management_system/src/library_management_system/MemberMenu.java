public class MemberMenu {

    private Library library;
    private Member member;

    public MemberMenu(Library library, Member member) {
        this.library = library;
        this.member = member;
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println("\n===== MEMBER DASHBOARD =====");
            System.out.println("Welcome, " + member.getName());
            System.out.println("1. View Profile");
            System.out.println("2. Edit Profile");
            System.out.println("3. Change Password");
            System.out.println("4. Browse Library Items");
            System.out.println("5. Search Library");
            System.out.println("6. Borrow Item");
            System.out.println("7. Return Item");
            System.out.println("8. Borrowing History");
            System.out.println("9. View Unpaid Fines");
            System.out.println("10. Pay Fines");
            System.out.println("11. Logout");

            int choice = InputHelper.getNumber("Enter choice: ");

            switch (choice) {

                case 1:
                    member.displayMember();
                    break;

                case 2:
                    editProfile();
                    break;

                case 3:
                    changePassword();
                    break;

                case 4:
                    library.displayItems();
                    break;

                case 5:
                    String keyword =
                            InputHelper.getText("Search keyword: ");

                    library.searchItems(keyword);
                    break;

                case 6:
                    String borrowId =
                            InputHelper.getText("Item ID: ");

                    library.borrowItem(member.getMemberId(), borrowId);
                    break;

                case 7:
                    String returnId =
                            InputHelper.getText("Item ID: ");

                    library.returnItem(member.getMemberId(), returnId);
                    break;

                case 8:
                    library.displayMemberLoans(member.getMemberId());
                    break;

                case 9:
                    System.out.printf("Unpaid Fines: PHP %.2f%n",
                            member.getUnpaidFines());
                    break;

                case 10:
                    payFines();
                    break;

                case 11:
                    running = false;
                    System.out.println("Member logged out.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

            LibraryStorage.save(library);
        }
    }

    private void editProfile() {

        member.displayMember();

        String name = InputHelper.getText("New Name: ");

        if (name.isEmpty()) {
            System.out.println("Name was not changed.");
            return;
        }

        member.setName(name);

        System.out.println("Profile updated successfully.");
    }

    private void changePassword() {

        String currentPassword =
                InputHelper.getText("Current Password: ");

        if (!member.checkPassword(currentPassword)) {
            System.out.println("Incorrect current password.");
            return;
        }

        String newPassword =
                InputHelper.getText("New Password: ");

        String confirmPassword =
                InputHelper.getText("Confirm New Password: ");

        if (newPassword.length() < 6) {
            System.out.println("Password must have at least 6 characters.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        member.setPassword(newPassword);

        System.out.println("Password changed successfully.");
    }

    private void payFines() {

        System.out.printf("Current Balance: PHP %.2f%n",
                member.getUnpaidFines());

        double amount =
                InputHelper.getAmount("Payment Amount: ");

        if (amount <= 0 || amount > member.getUnpaidFines()) {
            System.out.println("Invalid payment amount.");
            return;
        }

        member.payFine(amount);

        System.out.println("Payment recorded successfully.");

        System.out.printf("Remaining Balance: PHP %.2f%n",
                member.getUnpaidFines());
    }
}
