package SharedFiles;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import LotManager.Vehicle;
import LotManager.PlateGenerator;

public class FileManager {
    private static final String LOTS_DIR = "src/SharedFiles/lots";

    //This method was AI helped 
    /*
     * Create method that loads a lot file by reading a string called lotName 
     * which has vehicles objects in it and it should return a list of vehicles. 
     * Make sure to read from the directory as saved in the LOTS_DIR variable. 
     * Make sure to also place the correct data into the all of the new vehicles objects.
     * 
     */
    public static List<Vehicle> loadLotFile(String lotName) throws IOException { //
        Path path = Paths.get(LOTS_DIR, lotName + ".txt"); //
        List<Vehicle> vehicles = new ArrayList<>(); //
        
        if (Files.exists(path)) { //
            List<String> lines = Files.readAllLines(path); //
            for (String line : lines) { //
                String[] parts = line.split(","); //
                if (parts.length >= 3) { //
                    Vehicle vehicle = new Vehicle(parts[0], parts[1], Integer.parseInt(parts[2])); //
                    // Set discount based on the fourth part
                    if (parts.length >= 4) { //
                        vehicle.setDiscount(Boolean.parseBoolean(parts[3])); //
                    }
                    vehicles.add(vehicle); //
                }
            }
        }
        return vehicles; //
    }


    //This method was AI helped 
    /*
     * Create method that saves a a list of vehicles to a text file with the name given in the lotName parameter
     * Make sure to create a new text file if one with the given name does not exist.
     * Make sure to write to the directory as saved in the LOTS_DIR variable.
     * And make sure to write the vehicles to the file in the correct format.
     * 
     */

    public static void saveLotFile(String lotName, List<Vehicle> vehicles) throws IOException {
        Path path = Paths.get(LOTS_DIR, lotName + ".txt"); //
        Files.createDirectories(path.getParent()); //
        try (BufferedWriter writer = Files.newBufferedWriter(path)) { //
            for (Vehicle v : vehicles) { //
                writer.write(v.toString()); //
                writer.newLine(); //
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

    //This method was AI helped 
    /*
     * Create a method that returns a set of all the license plates in all the lots.
     * Make sure to read from the directory as saved in the LOTS_DIR variable.
     * 
     */
    public static Set<String> getAllExistingPlates() throws IOException {
        Set<String> allPlates = new HashSet<>();
        Path lotsDir = Paths.get(LOTS_DIR);
        
        if (Files.exists(lotsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(lotsDir, "*.txt")) { //
                for (Path lotFile : stream) { //
                    List<Vehicle> vehicles = loadLotFile(lotFile.getFileName().toString().replace(".txt", "")); //
                    for (Vehicle v : vehicles) { //
                        allPlates.add(v.getLicensePlate()); //
                    }
                }
            }
        }
        return allPlates;
    }
} 