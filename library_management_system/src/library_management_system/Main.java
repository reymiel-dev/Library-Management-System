import javax.swing.SwingUtilities;

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


        Library finalLibrary = library;
        SwingUtilities.invokeLater(() -> new LoginFrame(finalLibrary).setVisible(true));

    }
}
