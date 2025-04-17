import RentalShops.RentalShop;
import LotManager.Vehicle;
import SharedFiles.FileManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RSMain2 {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java RSMain2 <location> <spaces> <lotNames>");
            System.out.println("Example: java RSMain2 \"Los Angeles\" \"20\" \"central,airport,city park\"");
            return;
        }

        RentalShop rentalShop = null;
        try {
            String location = args[0];
            int spaces = Integer.parseInt(args[1]);
            String[] accessibleLots = args[2].split(",");

            // Create a new rental shop with the specified parameters
            rentalShop = new RentalShop(location, spaces, accessibleLots);
            
            // Example usage of the rent method
            System.out.println("Attempting to rent a SEDAN...");
            if (rentalShop.rent("SEDAN")) {
                System.out.println("Successfully rented a SEDAN!");
            } else {
                System.out.println("No SEDAN available in any accessible lot.");
            }
            
            // Display available and rented vehicles
            System.out.println("\nAvailable vehicles at " + rentalShop.getLocation() + ":");
            for (Vehicle v : rentalShop.getAvailableVehicles()) {
                System.out.println(v.getType() + " - " + v.getLicensePlate() + " (Kilometers: " + v.getKilometers() + ")");
            }
            
            System.out.println("\nRented vehicles:");
            for (Vehicle v : rentalShop.getRentedVehicles()) {
                System.out.println(v.getType() + " - " + v.getLicensePlate() + " (Kilometers: " + v.getKilometers() + ")");
            }
            
        } catch (IOException e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format for spaces: " + args[1]);
        } finally {
            // Return all vehicles to random lots when the program exits
            if (rentalShop != null) {
                try {
                    rentalShop.returnAllVehicles();
                } catch (IOException e) {
                    System.err.println("Error returning vehicles: " + e.getMessage());
                }
            }
        }
    }
}
