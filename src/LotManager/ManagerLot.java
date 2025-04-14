package LotManager;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ManagerLot {
    private final PlateGenerator plateGenerator;
    private static final String LOTS_DIR = "src/LotManager/lots";

    public ManagerLot() throws IOException {
        this.plateGenerator = new PlateGenerator();
        loadAllExistingPlates();
    }

    private void loadAllExistingPlates() throws IOException {
        Set<String> allPlates = new HashSet<>();
        Path lotsDir = Paths.get(LOTS_DIR);
        
        if (Files.exists(lotsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(lotsDir, "*.txt")) {
                for (Path lotFile : stream) {
                    List<Vehicle> vehicles = loadLotFile(lotFile);
                    for (Vehicle v : vehicles) {
                        allPlates.add(v.getLicensePlate());
                    }
                }
            }
        }
        
        plateGenerator.loadExistingPlates(allPlates);
    }

    public void manageLot(String lotName, int sedans, int suvs, int vans, String removePlate) throws IOException {
        Path lotPath = Paths.get(LOTS_DIR, lotName + ".txt");
        List<Vehicle> vehicles = loadLotFile(lotPath);

        addVehicles(vehicles, "SEDAN", sedans);
        addVehicles(vehicles, "SUV", suvs);
        addVehicles(vehicles, "VAN", vans);

        // I need to implement a method to remove a vehicle from a parking lot by its license plate. When a vehicle is removed:
        // The vehicle should be removed from the lot's list of vehicles
        // The system should track that the removed vehicle's plate is no longer in use
        // The plate should become available for reuse in the future
        // The system should only update the plate tracking if a vehicle was actually removed
        // The lot file should be saved with the updated vehicle list

        if (removePlate != null && !removePlate.isEmpty()) {
            boolean removed = vehicles.removeIf(v -> v.getLicensePlate().equals(removePlate));
            if (removed) {
                // Update plate generator with remaining plates
                plateGenerator.loadExistingPlates(vehicles.stream()
                    .map(Vehicle::getLicensePlate)
                    .collect(Collectors.toSet()));
            }
        }

        saveLotFile(lotPath, vehicles);
        System.out.println("Lot " + lotName + " updated. Total vehicles: " + vehicles.size());
    }

    private List<Vehicle> loadLotFile(Path path) throws IOException {
        List<Vehicle> vehicles = new ArrayList<>();
        if (Files.exists(path)) {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    vehicles.add(new Vehicle(parts[0], parts[1], Integer.parseInt(parts[2])));
                }
            }
        }
        return vehicles;
    }

    private void saveLotFile(Path path, List<Vehicle> vehicles) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Vehicle v : vehicles) {
                writer.write(v.toString());
                writer.newLine();
            }
        }
    }

    private void addVehicles(List<Vehicle> vehicles, String type, int count) {
        for (int i = 0; i < count; i++) {
            String plate = plateGenerator.generateUniquePlate();
            vehicles.add(new Vehicle(plate, type.toUpperCase(), 0));
        }
    }
}

