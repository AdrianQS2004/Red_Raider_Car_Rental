package RentalShops;
import java.io.*;
import java.util.*;
import LotManager.Vehicle;
import SharedFiles.FileManager;

public class RentalShop {
    private String location;
    private int spaces;
    private String[] accessibleLots;
    private List<Vehicle> availableVehicles;
    private List<Vehicle> rentedVehicles;
    private Random random;
    private static final String RENTED_CARS_FILE = "rented_cars";

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
            List<Vehicle> loadedVehicles = FileManager.loadLotFile(RENTED_CARS_FILE);
            rentedVehicles.addAll(loadedVehicles);
        } catch (IOException e) {
            System.err.println("Error loading rented vehicles: " + e.getMessage());
        }
    }

    private void saveRentedVehicles() {
        try {
            // Load existing rented vehicles
            List<Vehicle> allRentedVehicles = FileManager.loadLotFile(RENTED_CARS_FILE);
            
            // Add new rented vehicles that aren't already in the list
            for (Vehicle vehicle : rentedVehicles) {
                if (!allRentedVehicles.contains(vehicle)) {
                    allRentedVehicles.add(vehicle);
                }
            }
            
            // Save all rented vehicles
            FileManager.saveLotFile(RENTED_CARS_FILE, allRentedVehicles);
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