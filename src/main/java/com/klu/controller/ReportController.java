package com.klu.controller;

import com.klu.service.ReportService;
import com.klu.dto.*;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public SalesReportDto getSalesReport(
            @RequestParam String from,
            @RequestParam String to) {

        return reportService.getSalesReport(
                LocalDate.parse(from),
                LocalDate.parse(to));
    }

    @GetMapping("/top-products")
    public List<TopProductDto> getTopProducts(
            @RequestParam(defaultValue = "5") int limit) {

        return reportService.getTopProducts(limit);
    }

    @GetMapping("/customer/{customerId}/purchases")
    public CustomerPurchaseDto getCustomerHistory(
            @PathVariable Long customerId) {

        return reportService.getCustomerPurchaseHistory(customerId);
    }

    @GetMapping("/revenue-by-category")
    public Map<String, Double> getRevenueByCategory() {
        return reportService.getRevenueByCategory();
    }

    @GetMapping("/daily-sales")
    public double getDailySales(@RequestParam String date) {
        return reportService.getDailySales(
                LocalDate.parse(date));
    }
}
