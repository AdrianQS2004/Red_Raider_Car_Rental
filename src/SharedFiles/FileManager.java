package SharedFiles;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import LotManager.Vehicle;
import LotManager.PlateGenerator;

public class FileManager {
    private static final String LOTS_DIR = "src/SharedFiles/lots";

    public static List<Vehicle> loadLotFile(String lotName) throws IOException {
        Path path = Paths.get(LOTS_DIR, lotName + ".txt");
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

    public static void saveLotFile(String lotName, List<Vehicle> vehicles) throws IOException {
        Path path = Paths.get(LOTS_DIR, lotName + ".txt");
        Files.createDirectories(path.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Vehicle v : vehicles) {
                writer.write(v.toString());
                writer.newLine();
            }
        }
    }

    public static List<Vehicle> addVehicles(List<Vehicle> vehicles, String type, int count, PlateGenerator plateGenerator) {
        List<Vehicle> newVehicles = new ArrayList<>(vehicles);
        for (int i = 0; i < count; i++) {
            String plate = plateGenerator.generateUniquePlate();
            newVehicles.add(new Vehicle(plate, type.toUpperCase(), 0));
        }
        return newVehicles;
    }

    public static Set<String> getAllExistingPlates() throws IOException {
        Set<String> allPlates = new HashSet<>();
        Path lotsDir = Paths.get(LOTS_DIR);
        
        if (Files.exists(lotsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(lotsDir, "*.txt")) {
                for (Path lotFile : stream) {
                    List<Vehicle> vehicles = loadLotFile(lotFile.getFileName().toString().replace(".txt", ""));
                    for (Vehicle v : vehicles) {
                        allPlates.add(v.getLicensePlate());
                    }
                }
            }
        }
        return allPlates;
    }
} 