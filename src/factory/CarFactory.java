package factory;

import builder.CarBuilder;
import model.Car;
import model.NewCar;
import model.UsedCar;

public final class CarFactory {
    private CarFactory() {
    }

    public static Car create(CarType type) {
        if (type == CarType.NEW) {
            return new NewCar("Prototype", "Prototype", 2024, 1, "New", 3);
        }
        return new UsedCar("Prototype", "Prototype", 2020, 1, "Used", 0, 1);
    }

    public static Car create(CarType type, CarBuilder builder) {
        return switch (type) {
            case NEW -> new NewCar(
                    builder.getBrand(),
                    builder.getModel(),
                    builder.getYear(),
                    builder.getPrice(),
                    builder.getCondition(),
                    builder.getWarrantyYears()
            );
            case USED -> new UsedCar(
                    builder.getBrand(),
                    builder.getModel(),
                    builder.getYear(),
                    builder.getPrice(),
                    builder.getCondition(),
                    builder.getKilometers(),
                    builder.getNumberOfOwners()
            );
        };
    }
}
