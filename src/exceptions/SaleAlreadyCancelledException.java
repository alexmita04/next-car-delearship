package exceptions;

public class SaleAlreadyCancelledException extends Exception {
    public SaleAlreadyCancelledException(int saleId) {
        super("Sale with ID " + saleId + " is already cancelled.");
    }
}
