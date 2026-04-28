package com.rest_erp.backend_bi_rest_erp.controller.sales;


import com.rest_erp.backend_bi_rest_erp.dto.sales.PipelineItem;
import com.rest_erp.backend_bi_rest_erp.dto.sales.RetentionItem;
import com.rest_erp.backend_bi_rest_erp.dto.sales.RevenueByProductItem;
import com.rest_erp.backend_bi_rest_erp.dto.sales.SalesKpiResponse;
import com.rest_erp.backend_bi_rest_erp.service.sales.SalesKpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bi/sales")
@RequiredArgsConstructor
public class SalesKpiController {

    private final SalesKpiService salesKpiService;

    @GetMapping("/kpis")
    public SalesKpiResponse getSalesKpis(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return salesKpiService.getSalesKpis(startDate, endDate);
    }
    @GetMapping("/revenue-trend")
    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.SalesRevenueTrendItem> getRevenueTrend(
            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate startDate,

            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate endDate
    ) {
        return salesKpiService.getRevenueTrend(startDate, endDate);
    }

    @GetMapping("/pipeline")
    public List<PipelineItem> getPipeline() {
        return salesKpiService.getPipelineDistribution();
    }

    @GetMapping("/recent-orders")
    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.RecentSalesOrderItem> getRecentSalesOrders(
            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate startDate,

            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate endDate
    ) {
        return salesKpiService.getRecentSalesOrders(startDate, endDate);
    }

    @GetMapping("/top-salespersons")
    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.TopSalespersonItem> getTopSalespersons(
            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate startDate,

            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate endDate
    ) {
        return salesKpiService.getTopSalespersons(startDate, endDate);
    }

    @GetMapping("/revenue-by-customer")
    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.RevenueByCustomerItem> getRevenueByCustomer(
            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate startDate,

            @RequestParam(required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            java.time.LocalDate endDate
    ) {
        return salesKpiService.getRevenueByCustomer(startDate, endDate);
    }

    @GetMapping("/revenue-by-product")
    public List<RevenueByProductItem> getRevenueByProduct(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return salesKpiService.getRevenueByProduct(startDate, endDate);
    }

    @GetMapping("/customer-retention")
    public List<RetentionItem> getCustomerRetention() {
        return salesKpiService.getCustomerRetention();
    }

    @GetMapping("/high-value-deals")
    public List<Map<String, Object>> getHighValueDeals() {
        return salesKpiService.getHighValueDeals();
    }
}
