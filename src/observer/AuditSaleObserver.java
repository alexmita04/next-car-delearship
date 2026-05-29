package observer;

import model.Sale;
import service.AuditService;

public class AuditSaleObserver implements SaleObserver {
    @Override
    public void onSaleCompleted(Sale sale) {
        AuditService.getInstance().logAction("REGISTER_SALE");
    }
}
