package model;

public class NewCar extends Car {
    private final int warrantyYears;

    public NewCar(String brand, String model, int year, double price, String condition, int warrantyYears) {
        super(brand, model, year, price, condition);
        this.warrantyYears = warrantyYears;
    }

    public int getWarrantyYears() {
        return warrantyYears;
    }

    public int getKilometers() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format(
                "NewCar{id=%d, brand='%s', model='%s', year=%d, price=%.2f, warranty=%d years, km=0}",
                getId(), getBrand(), getModel(), getYear(), getPrice(), warrantyYears
        );
    }
}
