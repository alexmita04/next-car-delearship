package model;

import interfaces.Reportable;

import java.time.LocalDate;
import java.util.List;

public class SalesReport implements Reportable {
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalRevenue;
    private int saleCount;
    private List<Sale> sales;

    public SalesReport(LocalDate startDate, LocalDate endDate, double totalRevenue,
                       int saleCount, List<Sale> sales) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalRevenue = totalRevenue;
        this.saleCount = saleCount;
        this.sales = sales;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public List<Sale> getSales() {
        return sales;
    }

    @Override
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Sales Report ===\n");
        sb.append(String.format("Period: %s — %s\n", startDate, endDate));
        sb.append(String.format("Total revenue: %.2f\n", totalRevenue));
        sb.append(String.format("Number of sales: %d\n", saleCount));
        sb.append("--- Sale details ---\n");
        for (Sale sale : sales) {
            sb.append("  ").append(sale).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return generateReport();
    }
}
