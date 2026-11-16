public class AdminMenu {

    private Library library;

    public AdminMenu(Library library) {
        this.library = library;
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println("\n===== ADMIN DASHBOARD =====");
            System.out.println("1. Display Library Items");
            System.out.println("2. Add Book");
            System.out.println("3. Add Magazine");
            System.out.println("4. Edit Library Item");
            System.out.println("5. Remove Library Item");
            System.out.println("6. Member Management");
            System.out.println("7. Search Library");
            System.out.println("8. Borrowing Records");
            System.out.println("9. Library Statistics");
            System.out.println("10. Rename Library");
            System.out.println("11. Logout");

            int choice = InputHelper.getNumber("Enter choice: ");

            switch (choice) {

                case 1:
                    library.displayItems();
                    break;

                case 2:
                    addBook();
                    break;

                case 3:
                    addMagazine();
                    break;

                case 4:
                    editItem();
                    break;

                case 5:
                    String removeId =
                            InputHelper.getText("Item ID: ");

                    library.removeItem(removeId);
                    break;

                case 6:
                    memberManagement();
                    break;

                case 7:
                    String keyword =
                            InputHelper.getText("Search keyword: ");

                    library.searchItems(keyword);
                    break;

                case 8:
                    library.displayLoans();
                    break;

                case 9:
                    library.displayStatistics();
                    break;

                case 10:
                    String newName =
                            InputHelper.getText("New Library Name: ");

                    library.setLibraryName(newName);
                    System.out.println("Library name updated.");
                    break;

                case 11:
                    running = false;
                    System.out.println("Admin logged out.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

            LibraryStorage.save(library);
        }
    }

    private void addBook() {

        String id = InputHelper.getText("Book ID: ");
        String title = InputHelper.getText("Title: ");
        String author = InputHelper.getText("Author: ");
        String isbn = InputHelper.getText("ISBN: ");
        String category = InputHelper.getText("Category: ");

        int year = InputHelper.getNumber("Publication Year: ");
        int copies = InputHelper.getNumber("Number of Copies: ");

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()
                || isbn.isEmpty() || category.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }

        library.addItem(new Book(
                id, title, author, year, copies, isbn, category
        ));
    }

    private void addMagazine() {

        String id = InputHelper.getText("Magazine ID: ");
        String title = InputHelper.getText("Title: ");
        String publisher = InputHelper.getText("Publisher: ");

        int year = InputHelper.getNumber("Publication Year: ");
        int copies = InputHelper.getNumber("Number of Copies: ");
        int issue = InputHelper.getNumber("Issue Number: ");

        if (id.isEmpty() || title.isEmpty() || publisher.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }

        library.addItem(new Magazine(
                id, title, publisher, year, copies, issue
        ));
    }

    private void editItem() {

        String id = InputHelper.getText("Item ID: ");

        LibraryItem item = library.findItem(id);

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        String title = InputHelper.getText("New Title: ");

        if (!title.isEmpty()) {
            item.setTitle(title);
        }

        int year = InputHelper.getNumber("New Publication Year: ");
        item.setYear(year);

        int copies = InputHelper.getNumber("New Total Copies: ");
        item.setTotalCopies(copies);

        if (item instanceof Book) {

            Book book = (Book) item;

            String author = InputHelper.getText("New Author: ");
            String isbn = InputHelper.getText("New ISBN: ");
            String category = InputHelper.getText("New Category: ");

            if (!author.isEmpty()) {
                book.setAuthor(author);
            }

            if (!isbn.isEmpty()) {
                book.setIsbn(isbn);
            }

            if (!category.isEmpty()) {
                book.setCategory(category);
            }

        } else if (item instanceof Magazine) {

            Magazine magazine = (Magazine) item;

            String publisher =
                    InputHelper.getText("New Publisher: ");

            int issue =
                    InputHelper.getNumber("New Issue Number: ");

            if (!publisher.isEmpty()) {
                magazine.setPublisher(publisher);
            }

            magazine.setIssueNumber(issue);
        }

        System.out.println("Item updated successfully.");
    }

    private void memberManagement() {

        boolean running = true;

        while (running) {

            System.out.println("\n===== MEMBER MANAGEMENT =====");
            System.out.println("1. Register Member");
            System.out.println("2. Display Members");
            System.out.println("3. Search Member");
            System.out.println("4. Edit Member");
            System.out.println("5. Activate / Deactivate Member");
            System.out.println("6. Reset Member Password");
            System.out.println("7. Delete Member");
            System.out.println("8. Back");

            int choice = InputHelper.getNumber("Enter choice: ");

            switch (choice) {

                case 1:
                    registerMember();
                    break;

                case 2:
                    library.displayMembers();
                    break;

                case 3:
                    searchMember();
                    break;

                case 4:
                    editMember();
                    break;

                case 5:
                    changeMemberStatus();
                    break;

                case 6:
                    resetMemberPassword();
                    break;

                case 7:
                    deleteMember();
                    break;

                case 8:
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

            LibraryStorage.save(library);
        }
    }

    private void registerMember() {

        String id = InputHelper.getText("Member ID: ");
        String name = InputHelper.getText("Full Name: ");
        String password = InputHelper.getText("Create Password: ");

        if (id.isEmpty() || name.isEmpty()) {
            System.out.println("Member ID and name are required.");
            return;
        }

        if (password.length() < 6) {
            System.out.println("Password must have at least 6 characters.");
            return;
        }

        MembershipType type = InputHelper.getMembershipType();

        library.registerMember(new Member(id, name, password, type));
    }

    private void searchMember() {

        String id = InputHelper.getText("Member ID: ");

        Member member = library.findMember(id);

        if (member != null) {
            member.displayMember();
        } else {
            System.out.println("Member not found.");
        }
    }

    private void editMember() {

        String id = InputHelper.getText("Member ID: ");

        Member member = library.findMember(id);

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        member.displayMember();

        String name = InputHelper.getText("New Name: ");

        if (!name.isEmpty()) {
            member.setName(name);
        }

        System.out.println("\n1. Regular");
        System.out.println("2. Premium");
        System.out.println("3. Keep Current Membership");

        int choice = InputHelper.getNumber("Membership: ");

        if (choice == 1) {
            member.setMembershipType(MembershipType.REGULAR);
        } else if (choice == 2) {
            member.setMembershipType(MembershipType.PREMIUM);
        }

        System.out.println("Member updated successfully.");
    }

    private void changeMemberStatus() {

        String id = InputHelper.getText("Member ID: ");

        Member member = library.findMember(id);

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        member.setActive(!member.isActive());

        System.out.println("Account status: " +
                (member.isActive() ? "Active" : "Inactive"));
    }

    private void resetMemberPassword() {

        String id = InputHelper.getText("Member ID: ");

        Member member = library.findMember(id);

        if (member == null) {
            System.out.println("Member not found.");
            return;
        }

        String password =
                InputHelper.getText("New Password: ");

        if (password.length() < 6) {
            System.out.println("Password must have at least 6 characters.");
            return;
        }

        member.setPassword(password);

        System.out.println("Password reset successfully.");
    }

    private void deleteMember() {

        String id = InputHelper.getText("Member ID: ");

        String confirmation =
                InputHelper.getText("Type DELETE to confirm: ");

        if (confirmation.equals("DELETE")) {
            library.deleteMember(id);
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
}
