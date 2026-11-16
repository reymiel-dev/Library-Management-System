import java.io.*;

public class LibraryStorage {

    private static final String DATA_FILE = "library_data.ser";

    public static void save(Library library) {

        try (ObjectOutputStream output =
                     new ObjectOutputStream(
                             new FileOutputStream(DATA_FILE))) {

            output.writeObject(library);

        } catch (IOException e) {
            System.out.println("Error saving library data: " +
                    e.getMessage());
        }
    }

    public static Library load() {

        File file = new File(DATA_FILE);

        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(
                             new FileInputStream(DATA_FILE))) {

            return (Library) input.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading library data: " +
                    e.getMessage());

            return null;
        }
    }
}
