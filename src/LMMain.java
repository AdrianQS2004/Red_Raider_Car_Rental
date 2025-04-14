import LotManager.*;
import java.io.IOException;

public class LMMain {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java Main <lotName> <numSedans> <numSUVs> <numVans> [optional: plateToRemove]");
            return;
        }

        String lotName = args[0];
        int sedans = Integer.parseInt(args[1]);
        int suvs = Integer.parseInt(args[2]);
        int vans = Integer.parseInt(args[3]);
        String plateToRemove = args.length >= 5 ? args[4] : null;

        try {
            ManagerLot lotManager = new ManagerLot();
            lotManager.manageLot(lotName, sedans, suvs, vans, plateToRemove);
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}

