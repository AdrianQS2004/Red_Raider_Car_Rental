package LotManager;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import SharedFiles.FileManager;

public class ManagerLot {
    private final PlateGenerator plateGenerator;

    public ManagerLot() throws IOException {
        this.plateGenerator = new PlateGenerator();
        plateGenerator.loadExistingPlates(FileManager.getAllExistingPlates());
    }

    public void manageLot(String lotName, int sedans, int suvs, int vans, String removePlate) throws IOException {
        List<Vehicle> vehicles = FileManager.loadLotFile(lotName);

        vehicles = FileManager.addVehicles(vehicles, "SEDAN", sedans, plateGenerator);
        vehicles = FileManager.addVehicles(vehicles, "SUV", suvs, plateGenerator);
        vehicles = FileManager.addVehicles(vehicles, "VAN", vans, plateGenerator);
        if (removePlate != null && !removePlate.isEmpty()) {
            boolean removed = vehicles.removeIf(v -> v.getLicensePlate().equals(removePlate));
            if (removed) {
                // Update plate generator with remaining plates
                plateGenerator.loadExistingPlates(vehicles.stream()
                    .map(Vehicle::getLicensePlate)
                    .collect(Collectors.toSet()));
                System.out.println("Vehicle with plate " + removePlate + " was successfully removed.");
            } else {
                System.out.println("Vehicle with plate " + removePlate + " was not found in the lot.");
            }
        }

        FileManager.saveLotFile(lotName, vehicles);
        System.out.println("Lot " + lotName + " updated. Total vehicles: " + vehicles.size());
    }
}

