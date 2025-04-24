import RentalShops.RentalShop;
import java.io.IOException;


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
            rentalShop.loadRandomVehicles(spaces / 2);
            // Example usage of the rent method
            //rentalShop.rent("SEDAN");
            rentalShop.returnVehicle("XIW-810", 100);
            rentalShop.TransactionHistory();

            rentalShop.returnAllVehicles();

        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
