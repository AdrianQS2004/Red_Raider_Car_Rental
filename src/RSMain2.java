import RentalShops.RentalShop;
import java.io.IOException;
import java.util.Scanner;

public class RSMain2 {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java RSMain2 <location> <spaces> <lotNames>");
            System.out.println("Valid arguments are: -location=<name>, --spaces-available=<n>, --lots=<name1,name2,...>");
            System.out.println("Example: -location=Los Angeles --spaces-available=10 --lots=Airport Lot,central");
            return;
        }

        //Makes sure the arguments follow the correct format
        String location = null;
        int spaces = 0;
        String lotsArg = null;

        // Parse named arguments
        for (String arg : args) {

            if (arg.startsWith("-location=")) {
                location = arg.substring("-location=".length());
            } else if (arg.startsWith("--spaces-available=")) {
                try {
                    spaces = Integer.parseInt(arg.substring("--spaces-available=".length()));
                } catch (NumberFormatException e) {
                    System.out.println("Error: --spaces-available must be a number");
                    return;
                }
            } else if (arg.startsWith("--lots=")) {
                lotsArg = arg.substring("--lots=".length());
            } else {
                System.out.println("Error: Unknown argument format: " + arg);
                System.out.println("Valid arguments are: -location=<name>, --spaces-available=<n>, --lots=<name1,name2,...>");
                return;
            }
        }

        // Validate that all required arguments are present
        if (location == null || spaces == 0 || lotsArg == null) {
            System.out.println("Error: All arguments are required: -location, --spaces-available, --lots");
            System.out.println("Example: -location=Los Angeles --spaces-available=10 --lots=Airport Lot,central");
            return;
        }

        // If named arguments are used, update args array
        args = new String[]{location, String.valueOf(spaces), lotsArg};

        // Check if any of the specified lots exist
        String[] requestedLots = args[2].split(",");
        StringBuilder validLots = new StringBuilder();
        boolean foundValidLot = false;

        for (String lot : requestedLots) {
            String lotFileName = "src/SharedFiles/lots/" + lot.trim() + ".txt";
            java.io.File lotFile = new java.io.File(lotFileName);
            
            if (lotFile.exists()) {
                if (foundValidLot) {
                    validLots.append(",");
                }
                validLots.append(lot.trim());
                foundValidLot = true;
            } else {
                System.out.println("Warning: Lot '" + lot.trim() + "' does not exist");
            }
        }

        if (!foundValidLot) {
            System.out.println("Error: None of the specified lots exist. Program will exit.");
            return;
        }

        // Replace args[2] with only valid lots
        args[2] = validLots.toString();
        String[] accessibleLots = args[2].split(",");

        // Create a new rental shop with the specified parameters
        RentalShop rentalShop = new RentalShop(location, spaces, accessibleLots);
        // Loads random vehicles into the shop
        rentalShop.loadRandomVehicles(spaces / 2);
        
        // Print available commands
        System.out.println("\nAvailable Commands:");
        System.out.println("RENT <Vehicle Type> - Example: RENT SUV");
        System.out.println("RETURN <LICENSE PLATE> <KILOMETERS> - Example: RETURN XIW-810 100");
        System.out.println("TRANSACTIONS - Shows transaction history");
        System.out.println("LIST - Shows the current state of the shop");
        System.out.println("Exit - Exits the program");
        
        // Creates a scanner to read the user's input
        Scanner scanner = new Scanner(System.in);
        String input;
        

        // The following loops acts as the Command Line Interface of the program
        while (true) {
            System.out.print("\nEnter command: ");
            input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("Exit")) {
                System.out.println("Exiting program...");
                rentalShop.returnAllVehicles();
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
