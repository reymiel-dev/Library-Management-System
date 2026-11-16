public class Book extends LibraryItem {

    private static final long serialVersionUID = 1L;

    private String author;
    private String isbn;
    private String category;

    public Book(String itemId, String title, String author,
                int year, int copies, String isbn, String category) {

        super(itemId, title, year, copies);

        this.author = author;
        this.isbn = isbn;
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setAuthor(String author) {
        if (author != null && !author.trim().isEmpty()) {
            this.author = author.trim();
        }
    }

    public void setIsbn(String isbn) {
        if (isbn != null && !isbn.trim().isEmpty()) {
            this.isbn = isbn.trim();
        }
    }

    public void setCategory(String category) {
        if (category != null && !category.trim().isEmpty()) {
            this.category = category.trim();
        }
    }

    @Override
    public String getItemType() {
        return "Book";
    }

    @Override
    public void displayItem() {
        System.out.println("\nItem ID: " + getItemId());
        System.out.println("Title: " + getTitle());
        System.out.println("Author: " + author);
        System.out.println("ISBN: " + isbn);
        System.out.println("Category: " + category);
        System.out.println("Publication Year: " + getYear());
        System.out.println("Total Copies: " + getTotalCopies());
        System.out.println("Available Copies: " + getAvailableCopies());
    }
}
