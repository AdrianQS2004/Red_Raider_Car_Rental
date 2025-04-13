package LotManager;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ManagerLot {
    private final PlateGenerator plateGenerator;
    private static final String LOTS_DIR = "src/LotManager/lots";

    public ManagerLot() {
        this.plateGenerator = new PlateGenerator();
    }

    public void manageLot(String lotName, int sedans, int suvs, int vans, String removePlate) throws IOException {
        Path lotPath = Paths.get(LOTS_DIR, lotName + ".txt");
        List<Vehicle> vehicles = loadLotFile(lotPath);
        Set<String> existingPlates = new HashSet<>();
        for (Vehicle v : vehicles) existingPlates.add(v.getLicensePlate());
        plateGenerator.loadExistingPlates(existingPlates);

        addVehicles(vehicles, "SEDAN", sedans);
        addVehicles(vehicles, "SUV", suvs);
        addVehicles(vehicles, "VAN", vans);

        if (removePlate != null && !removePlate.isEmpty()) {
            vehicles.removeIf(v -> v.getLicensePlate().equals(removePlate));
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

