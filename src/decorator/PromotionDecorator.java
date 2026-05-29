package decorator;

import model.Car;

public class PromotionDecorator extends CarDecorator {
    public PromotionDecorator(Car car) {
        super(car);
    }

    public String formatPriceDisplay() {
        if (getDiscountPercent() > 0) {
            return String.format(
                    "%.2f (reduced from %.2f, -%.0f%% promo)",
                    getDiscountedPrice(),
                    getPrice(),
                    getDiscountPercent()
            );
        }
        return String.format("%.2f", getPrice());
    }

    @Override
    public String toString() {
        return String.format(
                "%s | Promo price: %s",
                car.toString(),
                formatPriceDisplay()
        );
    }
}
