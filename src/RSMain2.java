import RentalShops.RentalShop;
import java.io.IOException;
import java.util.Scanner;

public class RSMain2 {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java RSMain2 <location> <spaces> <lotNames>");
            System.out.println("Example: java RSMain2 \"Los Angeles\" \"20\" \"central,airport,city park\"");
            return;
        }

        String location = args[0];
        int spaces = Integer.parseInt(args[1]);
        String[] accessibleLots = args[2].split(",");

        // Create a new rental shop with the specified parameters
        RentalShop rentalShop = new RentalShop(location, spaces, accessibleLots);
        rentalShop.loadRandomVehicles(spaces / 2);
        // Print available commands
        System.out.println("\nAvailable Commands:");
        System.out.println("RENT <Vehicle Type> - Example: RENT SUV");
        System.out.println("RETURN <LICENSE PLATE> <KILOMETERS> - Example: RETURN XIW-810 100");
        System.out.println("TRANSACTIONS - Shows transaction history");
        System.out.println("LIST - Shows the current state of the shop");
        System.out.println("Exit - Exits the program");
        
        Scanner scanner = new Scanner(System.in);
        String input;
        
        while (true) {
            System.out.print("\nEnter command: ");
            input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("Exit")) {
                System.out.println("Exiting program...");
                break;
            }
            
            String[] parts = input.split("\\s+");
            
            if (parts.length == 0) {
                System.out.println("Invalid command format. Please try again.");
                continue;
            }
            
            switch (parts[0].toUpperCase()) {
                case "RENT":
                    if (parts.length != 2) {
                        System.out.println("Invalid RENT format. Use: RENT <Vehicle Type>");
                        continue;
                    }
                    try {
                        rentalShop.rent(parts[1]);
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                    
                case "RETURN":
                    if (parts.length != 3) {
                        System.out.println("Invalid RETURN format. Use: RETURN <LICENSE PLATE> <KILOMETERS>");
                        continue;
                    }
                    try {
                        int kilometers = Integer.parseInt(parts[2]);
                        rentalShop.returnVehicle(parts[1], kilometers);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid kilometers format. Please enter a number.");
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                    
                case "TRANSACTIONS":
                    rentalShop.TransactionHistory();
                    break;

                case "LIST":
                    rentalShop.ListStateOfShop();
                    break;
                    
                default:
                    System.out.println("Invalid command. Available commands are: RENT, RETURN, TRANSACTIONS, Exit");
            }
        }
        
        scanner.close();
    }
}
