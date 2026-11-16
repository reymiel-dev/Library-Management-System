public class Magazine extends LibraryItem {

    private static final long serialVersionUID = 1L;

    private String publisher;
    private int issueNumber;

    public Magazine(String itemId, String title, String publisher,
                    int year, int copies, int issueNumber) {

        super(itemId, title, year, copies);

        this.publisher = publisher;
        this.issueNumber = issueNumber;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public void setPublisher(String publisher) {
        if (publisher != null && !publisher.trim().isEmpty()) {
            this.publisher = publisher.trim();
        }
    }

    public void setIssueNumber(int issueNumber) {
        if (issueNumber >= 0) {
            this.issueNumber = issueNumber;
        }
    }

    @Override
    public String getItemType() {
        return "Magazine";
    }

    @Override
    public void displayItem() {
        System.out.println("\nItem ID: " + getItemId());
        System.out.println("Title: " + getTitle());
        System.out.println("Publisher: " + publisher);
        System.out.println("Issue Number: " + issueNumber);
        System.out.println("Publication Year: " + getYear());
        System.out.println("Total Copies: " + getTotalCopies());
        System.out.println("Available Copies: " + getAvailableCopies());
    }
}
