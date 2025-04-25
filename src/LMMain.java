import LotManager.*;
import java.io.IOException;

public class LMMain {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java LMMain --lot-name=lotName -add-sedan=numSedans -add-suv=numSUVs -add-van=numVans [--remove-vehicle=plateToRemove]");
            System.out.println("Example: java LMMain --lot-name=Airport Lot -add-sedan=8 -add-suv=5 -add-van=8 --remove-vehicle=WYI-170");
            return;
        }

        String lotName = null;
        int sedans = 0;
        int suvs = 0;
        int vans = 0;
        String plateToRemove = null;

        // Parse named arguments
        for (String arg : args) {
            if (arg.startsWith("--lot-name=")) {
                lotName = arg.substring("--lot-name=".length());
            } else if (arg.startsWith("-add-sedan=")) {
                sedans = Integer.parseInt(arg.substring("-add-sedan=".length()));
            } else if (arg.startsWith("-add-suv=")) {
                suvs = Integer.parseInt(arg.substring("-add-suv=".length()));
            } else if (arg.startsWith("-add-van=")) {
                vans = Integer.parseInt(arg.substring("-add-van=".length()));
            } else if (arg.startsWith("--remove-vehicle=")) {
                plateToRemove = arg.substring("--remove-vehicle=".length());
            }
        }

        // Validate required arguments
        if (lotName == null) {
            System.out.println("Error: --lot-name is required");
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

