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

        try {
            String location = args[0];
            int spaces = Integer.parseInt(args[1]);
            String[] lotNames = args[2].split(",");

            // Create a new rental shop with the specified parameters
            // Use location as both name and location for simplicity
            RentalShop rentalShop = new RentalShop(location, location);
            
            // Load vehicles from all specified lots
            List<Vehicle> allVehicles = new ArrayList<>();
            for (String lotName : lotNames) {
                List<Vehicle> vehicles = FileManager.loadLotFile(lotName.trim());
                allVehicles.addAll(vehicles);
            }
            
            // Add vehicles to the shop (up to the space limit)
            int vehiclesToAdd = Math.min(allVehicles.size(), spaces);
            rentalShop.addVehicles(allVehicles.subList(0, vehiclesToAdd));
            
            // Display available vehicles
            System.out.println("Available vehicles at " + rentalShop.getName() + ":");
            for (Vehicle v : rentalShop.getAvailableVehicles()) {
                System.out.println(v.getType() + " - " + v.getLicensePlate() + " (Kilometers: " + v.getKilometers() + ")");
            }
            
            // Save the shop data
            rentalShop.saveShopData();
            
        } catch (IOException e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format for spaces: " + args[1]);
        }
    }
}
