package LotManager;

public class Vehicle {
    private String licensePlate;
    private String type; // SEDAN, SUV, VAN
    private int kilometers;

    public Vehicle(String licensePlate, String type, int kilometers) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.kilometers = kilometers;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getType() {
        return type;
    }

    public int getKilometers() {
        return kilometers;
    }

    public void setKilometers(int kilometers) {
        this.kilometers = kilometers;
    }

    @Override
    public String toString() {
        return licensePlate + "," + type + "," + kilometers;
    }
}
