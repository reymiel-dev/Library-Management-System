public class Main {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {

        Library library = LibraryStorage.load();

        if (library == null) {

            library = new Library("Java Library");

            library.addItem(new Book(
                    "B001",
                    "The Hobbit",
                    "J.R.R. Tolkien",
                    1937,
                    3,
                    "9780547928227",
                    "Fantasy"
            ));

            library.addItem(new Book(
                    "B002",
                    "Dune",
                    "Frank Herbert",
                    1965,
                    3,
                    "9780441172719",
                    "Science Fiction"
            ));

            library.addItem(new Book(
                    "B003",
                    "The Lord of the Rings",
                    "J.R.R. Tolkien",
                    1954,
                    4,
                    "9780544003415",
                    "Fantasy"
            ));

            library.addItem(new Book(
                    "B004",
                    "Harry Potter",
                    "J.K. Rowling",
                    1997,
                    5,
                    "9780590353427",
                    "Fantasy"
            ));

            library.addItem(new Book(
                    "B005",
                    "The Martian",
                    "Andy Weir",
                    2011,
                    2,
                    "9780553418026",
                    "Science Fiction"
            ));

            library.addItem(new Magazine(
                    "M001",
                    "National Geographic",
                    "National Geographic",
                    2024,
                    5,
                    120
            ));

            library.addItem(new Magazine(
                    "M002",
                    "Science Today",
                    "Science Publications",
                    2023,
                    3,
                    45
            ));

            LibraryStorage.save(library);
        }

        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("       " + library.getLibraryName());
            System.out.println("================================");
            System.out.println("1. Admin Login");
            System.out.println("2. Member Login");
            System.out.println("3. Exit");

            int choice = InputHelper.getNumber("Enter choice: ");

            switch (choice) {

                case 1:
                    adminLogin(library);
                    break;

                case 2:
                    memberLogin(library);
                    break;

                case 3:
                    LibraryStorage.save(library);

                    running = false;

                    System.out.println("Library data saved.");
                    System.out.println("System closed.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void adminLogin(Library library) {

        String username =
                InputHelper.getText("Admin Username: ");

        String password =
                InputHelper.getText("Admin Password: ");

        if (username.equals(ADMIN_USERNAME)
                && password.equals(ADMIN_PASSWORD)) {

            System.out.println("Admin login successful.");

            AdminMenu adminMenu = new AdminMenu(library);
            adminMenu.start();

        } else {
            System.out.println("Incorrect admin credentials.");
        }
    }

    private static void memberLogin(Library library) {

        String id = InputHelper.getText("Member ID: ");
        String password = InputHelper.getText("Password: ");

        Member member = library.findMember(id);

        if (member == null) {
            System.out.println("Member account not found.");
            return;
        }

        if (!member.checkPassword(password)) {
            System.out.println("Incorrect password.");
            return;
        }

        if (!member.isActive()) {
            System.out.println("Your account is inactive.");
            return;
        }

        System.out.println("Member login successful.");

        MemberMenu memberMenu = new MemberMenu(library, member);
        memberMenu.start();
    }
}
