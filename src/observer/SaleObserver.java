package observer;

import model.Sale;

public interface SaleObserver {
    void onSaleCompleted(Sale sale);
}
