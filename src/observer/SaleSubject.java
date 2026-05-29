package observer;

import model.Sale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SaleSubject {
    private final List<SaleObserver> observers = new ArrayList<>();

    public void subscribe(SaleObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(SaleObserver observer) {
        observers.remove(observer);
    }

    public void notifySaleCompleted(Sale sale) {
        for (SaleObserver observer : observers) {
            observer.onSaleCompleted(sale);
        }
    }

    public List<SaleObserver> getObservers() {
        return Collections.unmodifiableList(observers);
    }
}
