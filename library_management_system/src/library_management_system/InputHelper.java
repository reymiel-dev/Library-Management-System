import java.util.Scanner;

public class InputHelper {

    private static final Scanner input = new Scanner(System.in);

    public static String getText(String message) {
        System.out.print(message);
        return input.nextLine().trim();
    }

    public static int getNumber(String message) {

        while (true) {
            try {
                System.out.print(message);

                int number = Integer.parseInt(input.nextLine());

                if (number >= 0) {
                    return number;
                }

                System.out.println("Enter a non-negative number.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    public static double getAmount(String message) {

        while (true) {
            try {
                System.out.print(message);

                double amount = Double.parseDouble(input.nextLine());

                if (Double.isFinite(amount) && amount >= 0) {
                    return amount;
                }

                System.out.println("Enter a valid non-negative amount.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid amount.");
            }
        }
    }

    public static MembershipType getMembershipType() {

        while (true) {
            System.out.println("\n1. Regular");
            System.out.println("2. Premium");

            int choice = getNumber("Membership: ");

            if (choice == 1) {
                return MembershipType.REGULAR;
            }

            if (choice == 2) {
                return MembershipType.PREMIUM;
            }

            System.out.println("Invalid choice.");
        }
    }
}

