package RentalShops;
import java.io.*;
import java.util.*;
import LotManager.Vehicle;
import SharedFiles.FileManager;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class RentalShop {
    private String location;
    private int spaces;
    private String[] accessibleLots;
    private List<Vehicle> availableVehicles;
    private List<Vehicle> rentedVehicles;
    private Random random;
    private static final String SHARED_FILES_DIR = "src/SharedFiles";
    private static final String RENTED_CARS_FILE = "rented_cars.txt";

    public RentalShop(String location, int spaces, String[] accessibleLots) {
        this.location = location;
        this.spaces = spaces;
        this.accessibleLots = accessibleLots;
        this.availableVehicles = new ArrayList<>();
        this.rentedVehicles = new ArrayList<>();
        this.random = new Random();
        loadRentedVehicles();
    }

    private void loadRentedVehicles() {
        try {
            File rentedFile = new File(SHARED_FILES_DIR, RENTED_CARS_FILE);
            if (rentedFile.exists()) {
                List<String> lines = Files.readAllLines(rentedFile.toPath());
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        rentedVehicles.add(new Vehicle(parts[0], parts[1], Integer.parseInt(parts[2])));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading rented vehicles: " + e.getMessage());
        }
    }

    private void saveRentedVehicles() {
        try {
            File rentedFile = new File(SHARED_FILES_DIR, RENTED_CARS_FILE);
            Files.createDirectories(rentedFile.getParentFile().toPath());
            
            // Load existing rented vehicles
            Set<String> existingPlates = new HashSet<>();
            if (rentedFile.exists()) {
                List<String> lines = Files.readAllLines(rentedFile.toPath());
                for (String line : lines) {
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        existingPlates.add(parts[0]);
                    }
                }
            }
            
            // Append new rented vehicles
            try (BufferedWriter writer = Files.newBufferedWriter(rentedFile.toPath(), 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                for (Vehicle vehicle : rentedVehicles) {
                    if (!existingPlates.contains(vehicle.getLicensePlate())) {
                        writer.write(vehicle.toString());
                        writer.newLine();
                        existingPlates.add(vehicle.getLicensePlate());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving rented vehicles: " + e.getMessage());
        }
    }

    public boolean rent(String vehicleType) throws IOException {
        // First, search in available vehicles
        Optional<Vehicle> vehicleOpt = availableVehicles.stream()
            .filter(v -> v.getType().equalsIgnoreCase(vehicleType))
            .findFirst();

        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            availableVehicles.remove(vehicle);
            rentedVehicles.add(vehicle);
            saveRentedVehicles();
            return true;
        }

        // If not found in available vehicles, search in accessible lots
        for (String lotName : accessibleLots) {
            List<Vehicle> lotVehicles = FileManager.loadLotFile(lotName.trim());
            Optional<Vehicle> lotVehicleOpt = lotVehicles.stream()
                .filter(v -> v.getType().equalsIgnoreCase(vehicleType))
                .findFirst();

            if (lotVehicleOpt.isPresent()) {
                Vehicle vehicle = lotVehicleOpt.get();
                // Remove from lot
                lotVehicles.remove(vehicle);
                FileManager.saveLotFile(lotName.trim(), lotVehicles);
                
                // Add to rented vehicles
                rentedVehicles.add(vehicle);
                saveRentedVehicles();
                return true;
            }
        }

        return false;
    }

    public void returnAllVehicles() throws IOException {
        // Only return available vehicles to random lots
        for (Vehicle vehicle : availableVehicles) {
            // Select a random lot
            String randomLot = accessibleLots[random.nextInt(accessibleLots.length)];
            
            // Load the lot's vehicles
            List<Vehicle> lotVehicles = FileManager.loadLotFile(randomLot.trim());
            
            // Add the vehicle to the lot
            lotVehicles.add(vehicle);
            
            // Save the updated lot
            FileManager.saveLotFile(randomLot.trim(), lotVehicles);
        }
        
        // Clear available vehicles list
        availableVehicles.clear();
    }

    public List<Vehicle> getAvailableVehicles() {
        return new ArrayList<>(availableVehicles);
    }

    public List<Vehicle> getRentedVehicles() {
        return new ArrayList<>(rentedVehicles);
    }

    public String getLocation() {
        return location;
    }

    public int getSpaces() {
        return spaces;
    }

    public String[] getAccessibleLots() {
        return accessibleLots;
    }
} 