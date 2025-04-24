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
    private Map<Vehicle, Integer> vehiclePrices = new HashMap<>();
    private int Money;
    private int MoneyLost;
    private Random random;
    private static final String RENTED_CARS_FILE = "rented_cars";

    public RentalShop(String location, int spaces, String[] accessibleLots) {
        this.location = location;
        this.spaces = spaces; 
        this.Money = 0;  
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
            if (availableVehicles.size() == 0) {
                System.out.println("No available vehicles, loading a random vehicle...");
                loadRandomVehicles(1);
            }
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
                
                // Add to rented vehicles and add the Discount
                vehicle.toggleDiscount();
                rentedVehicles.add(vehicle);
                saveRentedVehicles();
                System.out.println("Successfully rented " + vehicle.getLicensePlate() + " from lot " + lotName + "!");
                System.out.println("A 10% discount has been applied to the rental price because of the delay in delivery.");
                return;
            }
            System.out.println("No " + vehicleType + " found in lot " + lotName);
        }

        System.out.println("No " + vehicleType + " available in any accessible lot.");
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
            vehicle.AddKilometers(kilometers);
            System.out.println("Updated kilometers to: " + kilometers);
            
            // Add to available vehicles
            availableVehicles.add(vehicle);
            System.out.println("Added vehicle to available vehicles list");
            
            //Calculates and saves the Money made by the store
            DoTransaction(vehicle, kilometers);

            // Check if we only have one space left before reaching capacity
            if (availableVehicles.size() == spaces - 1) {
                returnLeastUsedVehicle();
            }
            // Save changes to rented cars file
            FileManager.saveLotFile(RENTED_CARS_FILE, rentedVehicles);
            System.out.println("Successfully saved changes to rented cars file");
        } else {
            System.out.println("No vehicle found with license plate: " + licensePlate);
        }
    }

    public void TransactionHistory() {
        System.out.println("\nTransaction History: \n");
        for (Vehicle vehicle : vehiclePrices.keySet()) {
            System.out.println(vehicle.getType() + " - " + vehicle.getLicensePlate() + 
                             " - Discount: " + vehicle.getDiscount() + 
                             " - Price: $" + vehiclePrices.get(vehicle));
        }
        System.out.println("Total Money Lost: $" + MoneyLost);
        System.out.println("Total Money Made: $" + Money);
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

    public void loadRandomVehicles(int vehiclesToLoad) throws IOException {
        System.out.println("\nLoading " + vehiclesToLoad + " random vehicles from accessible lots...");
        
        // Get all vehicles from accessible lots
        List<Vehicle> allVehicles = new ArrayList<>();
        for (String lotName : accessibleLots) {
            List<Vehicle> lotVehicles = FileManager.loadLotFile(lotName.trim());
            allVehicles.addAll(lotVehicles);
        }
        
        if (allVehicles.isEmpty()) {
            System.out.println("No vehicles found in any accessible lot.");
            return;
        }
        
        // Randomly select vehicles
        for (int i = 0; i < vehiclesToLoad && !allVehicles.isEmpty(); i++) {
            int randomIndex = random.nextInt(allVehicles.size());
            Vehicle selectedVehicle = allVehicles.remove(randomIndex);
            availableVehicles.add(selectedVehicle);
            System.out.println("Added vehicle: " + selectedVehicle.getType() + " - " + selectedVehicle.getLicensePlate());
        }
        
        System.out.println("Successfully loaded " + availableVehicles.size() + " vehicles into the shop.");
    }

    private void returnLeastUsedVehicle() throws IOException {
        if (availableVehicles.isEmpty()) {
            System.out.println("No vehicles available to return.");
            return;
        }

        // Find vehicle with fewest kilometers
        Vehicle leastUsedVehicle = availableVehicles.stream()
            .min((v1, v2) -> Integer.compare(v1.getKilometers(), v2.getKilometers()))
            .get();
            
        System.out.println("Found least used vehicle: " + leastUsedVehicle.getType() + " - " + 
            leastUsedVehicle.getLicensePlate() + " (Kilometers: " + leastUsedVehicle.getKilometers() + ")");

        // Find lot with least vehicles
        String leastUsedLot = null;
        int minVehicles = Integer.MAX_VALUE;
        
        for (String lotName : accessibleLots) {
            List<Vehicle> lotVehicles = FileManager.loadLotFile(lotName.trim());
            if (lotVehicles.size() < minVehicles) {
                minVehicles = lotVehicles.size();
                leastUsedLot = lotName;
            }
        }
        
        System.out.println("Found lot with least vehicles: " + leastUsedLot + " (Vehicles: " + minVehicles + ")");
        
        // Add vehicle to the lot
        List<Vehicle> lotVehicles = FileManager.loadLotFile(leastUsedLot.trim());
        lotVehicles.add(leastUsedVehicle);
        FileManager.saveLotFile(leastUsedLot.trim(), lotVehicles);
        
        // Remove from available vehicles
        availableVehicles.remove(leastUsedVehicle);
        
        System.out.println("Successfully returned vehicle to lot " + leastUsedLot);
    }


    private void DoTransaction(Vehicle CurrentVehicle, int kilometers) {
        int price = 0;

        if (CurrentVehicle.getDiscount()) {
            price = (int)(kilometers * 0.9);
            MoneyLost = MoneyLost + (int)(kilometers * 0.1);
            System.out.println("The car was returned with a discount");
        } else {
            price = kilometers;
        }
        
        Money = Money + price;
        System.out.println("The store charged " + Money + " dollars for the return of the car");

        vehiclePrices.put(CurrentVehicle, price);
    
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