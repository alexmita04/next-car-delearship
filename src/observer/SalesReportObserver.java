package observer;

import model.Sale;
import model.SalesReport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SalesReportObserver implements SaleObserver {
    private final List<Sale> completedSales = new ArrayList<>();

    @Override
    public void onSaleCompleted(Sale sale) {
        completedSales.add(sale);
        System.out.printf(
                "[SalesReport] Sale #%d recorded: %s %s — %.2f%n",
                sale.getId(),
                sale.getCar().getBrand(),
                sale.getCar().getModel(),
                sale.getFinalPrice()
        );
    }

    public SalesReport generateLiveReport(LocalDate startDate, LocalDate endDate) {
        List<Sale> periodSales = completedSales.stream()
                .filter(s -> !s.getDate().isBefore(startDate) && !s.getDate().isAfter(endDate))
                .filter(s -> !s.isCancelled())
                .collect(Collectors.toList());

        double totalRevenue = periodSales.stream()
                .mapToDouble(Sale::getFinalPrice)
                .sum();

        return new SalesReport(startDate, endDate, totalRevenue, periodSales.size(), periodSales);
    }

    public List<Sale> getCompletedSales() {
        return List.copyOf(completedSales);
    }
}
