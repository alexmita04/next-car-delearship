package model;

public class UsedCar extends Car {
    private int kilometers;
    private int numberOfOwners;

    public UsedCar(String brand, String model, int year, double price, String condition,
                   int kilometers, int numberOfOwners) {
        super(brand, model, year, price, condition);
        this.kilometers = kilometers;
        this.numberOfOwners = numberOfOwners;
    }

    public int getKilometers() {
        return kilometers;
    }

    public void setKilometers(int kilometers) {
        this.kilometers = kilometers;
    }

    public int getNumberOfOwners() {
        return numberOfOwners;
    }

    public void setNumberOfOwners(int numberOfOwners) {
        this.numberOfOwners = numberOfOwners;
    }

    @Override
    public String toString() {
        return String.format(
                "UsedCar{id=%d, brand='%s', model='%s', year=%d, price=%.2f, km=%d, owners=%d}",
                getId(), getBrand(), getModel(), getYear(), getPrice(), kilometers, numberOfOwners
        );
    }
}
