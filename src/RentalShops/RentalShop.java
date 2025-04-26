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
    }

    //This method searches if a car is available in the shop or accessible lots
    //If found, it removes it from the available vehicles and adds it to the rented vehicles
    //If not found it loads a random vehicle from the accessible lots and makes sure to toggle the discount to ture
    //Then it adds the vehicle to the rented vehicles and saves the rented vehicles

    public void rent(String vehicleType) throws IOException {
        System.out.println("\nAttempting to rent a " + vehicleType + "...");
        loadRentedVehicles();
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
                System.out.println("No available vehicles in the shop");
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
 
    // This method returns a vehicle to the shop / the accessible lots list
    // It loads all of the rented vehicles and searches for the vehicle with the given license plate
    // If found, it removes it from the rented vehicles and updates the kilometers
    // Then it calculates the money made by the store and saves the rented vehicles
    // It also makes sure to toggle off the discount after the return
 
    // This method was AI helped

    public void returnVehicle(String licensePlate, int kilometers) throws IOException {
        System.out.println("\nAttempting to return vehicle with plate: " + licensePlate);
        
        // Load all rented vehicles
        List<Vehicle> rentedVehicles = FileManager.loadLotFile(RENTED_CARS_FILE);
        
        // Searches for the vehicle with the given license plate

        /*
         * Create a condition so that we check if the vehicle given in the parameter is in the rentedVehicles list
         * It should then print a confirmation message
         * 
         */
        Optional<Vehicle> vehicleOpt = rentedVehicles.stream() //
            .filter(v -> v.getLicensePlate().equals(licensePlate)) //
            .findFirst(); //
            
        if (vehicleOpt.isPresent()) { //
            Vehicle vehicle = vehicleOpt.get();  //
            System.out.println("Found vehicle: " + vehicle.getType() + " - " + vehicle.getLicensePlate()); //
        

            // Remove from rented vehicles
            rentedVehicles.remove(vehicle);
            
            // Updates the kilometers of the vehicle
            vehicle.AddKilometers(kilometers);
            System.out.println("Updated kilometers to: " + vehicle.getKilometers());
            
            //Calculates and saves the Money made by the store
            DoTransaction(vehicle, kilometers);
            //Makes sure to toggle off the discount after the return
            if (vehicle.getDiscount()) {
                vehicle.toggleDiscount();
            } 

            // Add to available vehicles
            availableVehicles.add(vehicle);
            System.out.println("Added vehicle to available vehicles list");
    

            // Check if we only have one space left before reaching capacity
            if (availableVehicles.size() == spaces - 1) {
                returnLeastUsedVehicle();
            }

            // Save changes to rented cars file
            FileManager.saveLotFile(RENTED_CARS_FILE, rentedVehicles);
        } else {
            System.out.println("No vehicle found with license plate: " + licensePlate);
        }
    }

    // This method lists the state of the shop
    // It prints the location, the number of spaces used, the number of empty spaces, and the available vehicles
    // It also prints the total money made by the store

    public void ListStateOfShop() {
        System.out.println("\nState of the shop: \n");
        System.out.println("Location: " + location);
        System.out.println("Spaces Used: " + availableVehicles.size());
        System.out.println("Empty Spaces: " + (spaces - availableVehicles.size()));
        System.out.println("Available Vehicles: ");
        for (Vehicle vehicle : availableVehicles) {
            System.out.println(vehicle.getType() + " - " + vehicle.getLicensePlate());
        }
        System.out.println("Total Money Made: $" + Money);
        
    }

    // This method prints the transaction history
    // It prints all of the cars returned to the store, and the price of each transaction
    // It also prints the total money lost by discounts and the total money made by the store

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

    // This method is used to safely return the cars taken from the accessible lots at the beggining of the program
    // If this mehtod is not called before the program ends, the data of the cars taken from the accessible lots will be lost

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

    // This method loads a given number of vehicles into the shop
    // It loads all of the vehicles from the accessible lots and randomly selects a certain number of them
    // Then it adds them to the available vehicles list

    public void loadRandomVehicles(int vehiclesToLoad) throws IOException {
        System.out.println("\nLoading " + vehiclesToLoad + " random vehicles from accessible lots...");
        
        // Get all vehicles from accessible lots
        List<Vehicle> allVehicles = new ArrayList<>();
        Map<String, List<Vehicle>> lotVehiclesMap = new HashMap<>();
        
        for (String lotName : accessibleLots) {
            List<Vehicle> lotVehicles = FileManager.loadLotFile(lotName.trim());
            lotVehiclesMap.put(lotName, lotVehicles);
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
            
            // Remove the vehicle from its original lot
            for (Map.Entry<String, List<Vehicle>> entry : lotVehiclesMap.entrySet()) {
                List<Vehicle> lotVehicles = entry.getValue();
                if (lotVehicles.removeIf(v -> v.getLicensePlate().equals(selectedVehicle.getLicensePlate()))) {
                    // Save the updated lot
                    FileManager.saveLotFile(entry.getKey().trim(), lotVehicles);
                    break;
                }
            }
        }
        
        System.out.println("Successfully loaded " + availableVehicles.size() + " vehicles into the shop.");
    }

    // This method returns the least used vehicle to a random lot
    // It finds the vehicle with the least kilometers and returns it to a random lot
    // Then it removes it from the available vehicles list

    private void returnLeastUsedVehicle() throws IOException {

        System.out.println("\nThere is only one space left, returning the least used vehicle to a random lot");

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
        
        System.out.println("Successfully returned vehicle to lot " + leastUsedLot + "\n");
    }

    // This method calculates the price of the vehicle based on the kilometers and the discount
    // It also updates the total money made by the store and the total money lost by discounts
    // It also creates a new Vehicle object with the same properties and adds it to the vehiclePrices map
    // The vehiclePrices map saves the price of each transaction made to the store

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
        System.out.println("The store charged " + price + " dollars for the return of the car");
        
        // Create a new Vehicle object with the same properties
        Vehicle transactionVehicle = new Vehicle(CurrentVehicle.getLicensePlate(), CurrentVehicle.getType(), CurrentVehicle.getKilometers());
        transactionVehicle.setDiscount(CurrentVehicle.getDiscount());
        vehiclePrices.put(transactionVehicle, price);

    }
    
    // The following two methods simply are called to load and save the rented vehicles

    private void loadRentedVehicles() {
        try {
           rentedVehicles = FileManager.loadLotFile(RENTED_CARS_FILE);
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
} 