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
            // Save all rented vehicles
            FileManager.saveLotFile(RENTED_CARS_FILE, rentedVehicles);
        } catch (IOException e) {
            System.err.println("Error saving rented vehicles: " + e.getMessage());
        }
    }

    public void rent(String vehicleType) throws IOException {
        System.out.println("\nAttempting to rent a " + vehicleType + "...");
        
        // First, search in available vehicles
        Optional<Vehicle> vehicleOpt = availableVehicles.stream()
            .filter(v -> v.getType().equalsIgnoreCase(vehicleType))
            .findFirst();

        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            System.out.println("Found " + vehicleType + " in available vehicles: " + vehicle.getLicensePlate());
            availableVehicles.remove(vehicle);
            rentedVehicles.add(vehicle);
            saveRentedVehicles();
            System.out.println("Successfully rented " + vehicle.getLicensePlate() + " from available vehicles!");
            return;
        }

        System.out.println("No " + vehicleType + " found in available vehicles. Searching in accessible lots...");
        
        // If not found in available vehicles, search in accessible lots
        for (String lotName : accessibleLots) {
            System.out.println("Checking lot: " + lotName);
            List<Vehicle> lotVehicles = FileManager.loadLotFile(lotName.trim());
            Optional<Vehicle> lotVehicleOpt = lotVehicles.stream()
                .filter(v -> v.getType().equalsIgnoreCase(vehicleType))
                .findFirst();

            if (lotVehicleOpt.isPresent()) {
                Vehicle vehicle = lotVehicleOpt.get();
                System.out.println("Found " + vehicleType + " in lot " + lotName + ": " + vehicle.getLicensePlate());
                
                // Remove from lot
                lotVehicles.remove(vehicle);
                FileManager.saveLotFile(lotName.trim(), lotVehicles);
                System.out.println("Removed " + vehicle.getLicensePlate() + " from lot " + lotName);
                
                // Add to rented vehicles
                rentedVehicles.add(vehicle);
                saveRentedVehicles();
                System.out.println("Successfully rented " + vehicle.getLicensePlate() + " from lot " + lotName + "!");
                return;
            }
            System.out.println("No " + vehicleType + " found in lot " + lotName);
        }

        System.out.println("No " + vehicleType + " available in any accessible lot.");
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

    public void returnVehicle(String licensePlate, int kilometers) throws IOException {
        System.out.println("\nAttempting to return vehicle with plate: " + licensePlate);
        
        // Load all rented vehicles
        List<Vehicle> rentedVehicles = FileManager.loadLotFile(RENTED_CARS_FILE);
        
        // Search for the vehicle with the given license plate
        Optional<Vehicle> vehicleOpt = rentedVehicles.stream()
            .filter(v -> v.getLicensePlate().equals(licensePlate))
            .findFirst();
            
        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            System.out.println("Found vehicle: " + vehicle.getType() + " - " + vehicle.getLicensePlate());
            
            // Remove from rented vehicles
            rentedVehicles.remove(vehicle);
            System.out.println("Removed vehicle from rented cars list");
            
            // Update kilometers
            vehicle.setKilometers(kilometers);
            System.out.println("Updated kilometers to: " + kilometers);
            
            // Add to available vehicles
            availableVehicles.add(vehicle);
            System.out.println("Added vehicle to available vehicles list");
            
            // Save changes to rented cars file
            FileManager.saveLotFile(RENTED_CARS_FILE, rentedVehicles);
            System.out.println("Successfully saved changes to rented cars file");
        } else {
            System.out.println("No vehicle found with license plate: " + licensePlate);
        }
    }
} 