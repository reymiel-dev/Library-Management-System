import java.io.Serializable;

public abstract class LibraryItem implements Serializable, Borrowable {

    private static final long serialVersionUID = 1L;

    private String itemId;
    private String title;
    private int year;
    private int totalCopies;
    private int availableCopies;

    public LibraryItem(String itemId, String title, int year, int copies) {
        setItemId(itemId);
        setTitle(title);
        setYear(year);
        setTotalCopies(copies);
    }

    public String getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setItemId(String itemId) {
        if (itemId != null && !itemId.trim().isEmpty()) {
            this.itemId = itemId.trim();
        }
    }

    public void setTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
    }

    public void setYear(int year) {
        if (year >= 0) {
            this.year = year;
        }
    }

    public void setTotalCopies(int copies) {
        if (copies >= 0) {
            int borrowedCopies = totalCopies - availableCopies;

            if (copies >= borrowedCopies) {
                totalCopies = copies;
                availableCopies = copies - borrowedCopies;
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    @Override
    public void borrowCopy() {
        if (isAvailable()) {
            availableCopies--;
        }
    }

    @Override
    public void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public abstract String getItemType();

    public abstract void displayItem();
}
