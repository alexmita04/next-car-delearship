package exceptions;

public class CarNotFoundException extends Exception {
    public CarNotFoundException(int carId) {
        super("Car with ID " + carId + " was not found in inventory.");
    }
}
