package exceptions;

public class InvalidPromotionException extends Exception {
    public InvalidPromotionException(double percent) {
        super("Discount of " + percent + "% is invalid. It must be between 0 and 100.");
    }
}
