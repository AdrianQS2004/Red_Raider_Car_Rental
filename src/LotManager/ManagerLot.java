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

    // This method manages and creates lot files
    // It loads the lot file and adds the vehicles to the lot
    // If the given name of the lot is not found it creates a new lot text file
    // If the removePlate argument exists it removes the vehicle from the lot if it exists
    // Then it saves the changes to the lot file

    public void manageLot(String lotName, int sedans, int suvs, int vans, String removePlate) throws IOException {
        List<Vehicle> vehicles = FileManager.loadLotFile(lotName);

        //Adds the vehicles to the lot
        vehicles = FileManager.addVehicles(vehicles, "SEDAN", sedans, plateGenerator);
        vehicles = FileManager.addVehicles(vehicles, "SUV", suvs, plateGenerator);
        vehicles = FileManager.addVehicles(vehicles, "VAN", vans, plateGenerator);

        //If the removePlate argument exists it passes the condition and removes the vehicle from the lot if it exists
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

        //Saves the changes to the lot file
        FileManager.saveLotFile(lotName, vehicles);
        System.out.println("Lot " + lotName + " updated. Total vehicles: " + vehicles.size());
    }
}

