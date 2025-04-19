package LotManager;

public class Vehicle {
    private String licensePlate;
    private String type; // SEDAN, SUV, VAN
    private int kilometers;
    private boolean discount;

    public Vehicle(String licensePlate, String type, int kilometers) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.kilometers = kilometers;
        this.discount = false;
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

    public void AddKilometers(int kilometers) {
        this.kilometers = kilometers + this.kilometers;
    }

    public void setKilometers(int kilometers) {
        this.kilometers = kilometers;
    }

    public boolean getDiscount() {
        return discount;
    }

    public void toggleDiscount() {
        discount = !discount;
    }

    public void setDiscount(boolean value) {
        discount = value;
    }

    @Override
    public String toString() {
        return licensePlate + "," + type + "," + kilometers + "," + discount;
    }
}
