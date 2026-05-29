package interfaces;

import exceptions.InvalidPromotionException;

public interface Discountable {
    void applyDiscount(double percent) throws InvalidPromotionException;

    double getDiscountedPrice();

    double getDiscountPercent();
}
