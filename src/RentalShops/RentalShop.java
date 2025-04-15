package RentalShops;
import java.io.*;
import java.util.*;
import LotManager.Vehicle;
import SharedFiles.FileManager;

public class RentalShop {
    private String name;
    private String location;
    private List<Vehicle> availableVehicles;
    private List<Vehicle> rentedVehicles;
    private static final String RENTALS_DIR = "src/SharedFiles/rentals";

    public RentalShop(String name, String location) throws IOException {
        this.name = name;
        this.location = location;
        this.availableVehicles = new ArrayList<>();
        this.rentedVehicles = new ArrayList<>();
        loadShopData();
    }

    private void loadShopData() throws IOException {
        // Load available vehicles from the shop's file
        availableVehicles = FileManager.loadLotFile(name + "_available");
        
        // Load rented vehicles from the shop's file
        rentedVehicles = FileManager.loadLotFile(name + "_rented");
    }

    public void saveShopData() throws IOException {
        // Save available vehicles
        FileManager.saveLotFile(name + "_available", availableVehicles);
        
        // Save rented vehicles
        FileManager.saveLotFile(name + "_rented", rentedVehicles);
    }

    public boolean rentVehicle(String plateNumber, String customerName, int rentalDays) {
        // Find the vehicle in available vehicles
        Optional<Vehicle> vehicleOpt = availableVehicles.stream()
            .filter(v -> v.getLicensePlate().equals(plateNumber))
            .findFirst();

        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            // Remove from available
            availableVehicles.remove(vehicle);
            // Add to rented
            rentedVehicles.add(vehicle);
            return true;
        }
        return false;
    }

    public boolean returnVehicle(String plateNumber, int kilometersDriven) {
        // Find the vehicle in rented vehicles
        Optional<Vehicle> vehicleOpt = rentedVehicles.stream()
            .filter(v -> v.getLicensePlate().equals(plateNumber))
            .findFirst();

        if (vehicleOpt.isPresent()) {
            Vehicle vehicle = vehicleOpt.get();
            // Update kilometers
            vehicle.setKilometers(vehicle.getKilometers() + kilometersDriven);
            // Remove from rented
            rentedVehicles.remove(vehicle);
            // Add back to available
            availableVehicles.add(vehicle);
            return true;
        }
        return false;
    }

    public void addVehicles(List<Vehicle> vehicles) {
        availableVehicles.addAll(vehicles);
    }

    public List<Vehicle> getAvailableVehicles() {
        return new ArrayList<>(availableVehicles);
    }

    public List<Vehicle> getRentedVehicles() {
        return new ArrayList<>(rentedVehicles);
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
} 