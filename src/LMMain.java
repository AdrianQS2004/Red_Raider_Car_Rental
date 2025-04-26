import LotManager.*;
import java.io.IOException;

public class LMMain {
    public static void main(String[] args) {
        // Check if we have at least the required arguments (lot name and at least one vehicle type)
        if (args.length < 3) {
            System.out.println("Usage: java LMMain --lot-name=lotName [-add-sedan=numSedans] [-add-suv=numSUVs] [-add-van=numVans] [--remove-vehicle=plateToRemove]");
            System.out.println("Example: java LMMain --lot-name=Airport Lot -add-sedan=8 -add-suv=5 -add-van=8 --remove-vehicle=WYI-170");
            return;
        }

        String lotName = null;
        int sedans = 0;
        int suvs = 0;
        int vans = 0;
        String plateToRemove = null;
        boolean allArgsFound = true;

        // Makes sure the arguments follow the correct format
        for (String arg : args) {
            if (arg.startsWith("--lot-name=")) {
                lotName = arg.substring("--lot-name=".length());
            } else if (arg.startsWith("-add-sedan=")) {
                try {
                    sedans = Integer.parseInt(arg.substring("-add-sedan=".length()));
                } catch (NumberFormatException e) {
                    System.out.println("Error: -add-sedan must be a number");
                    allArgsFound = false;
                }
            } else if (arg.startsWith("-add-suv=")) {
                try {
                    suvs = Integer.parseInt(arg.substring("-add-suv=".length()));
                } catch (NumberFormatException e) {
                    System.out.println("Error: -add-suv must be a number");
                    allArgsFound = false;
                }
            } else if (arg.startsWith("-add-van=")) {
                try {
                    vans = Integer.parseInt(arg.substring("-add-van=".length()));
                } catch (NumberFormatException e) {
                    System.out.println("Error: -add-van must be a number");
                    allArgsFound = false;
                }
            } else if (arg.startsWith("--remove-vehicle=")) {
                plateToRemove = arg.substring("--remove-vehicle=".length());
            } else {
                System.out.println("Error: Unknown argument format: " + arg);
                //System.out.println("Valid arguments are: --lot-name=<name>, -add-sedan=<n>, -add-suv=<n>, -add-van=<n>, [--remove-vehicle=<plate>]");
                allArgsFound = false;
            }
        }

        // Check if all of the required arguments were found
        if (!allArgsFound || lotName == null) {
            System.out.println("Error: All required arguments must be provided correctly");
            System.out.println("Valid arguments are: --lot-name=<name>, -add-sedan=<n>, -add-suv=<n>, -add-van=<n>, [--remove-vehicle=<plate>]");
            System.out.println("Example: --lot-name=Airport Lot -add-sedan=8 -add-suv=5 -add-van=8 --remove-vehicle=WYI-170");
            return;
        }

        try {
            ManagerLot lotManager = new ManagerLot();
            lotManager.manageLot(lotName, sedans, suvs, vans, plateToRemove);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}

