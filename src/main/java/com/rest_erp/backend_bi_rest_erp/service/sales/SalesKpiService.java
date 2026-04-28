package com.rest_erp.backend_bi_rest_erp.service.sales;

import com.rest_erp.backend_bi_rest_erp.dto.sales.PipelineItem;
import com.rest_erp.backend_bi_rest_erp.dto.sales.RetentionItem;
import com.rest_erp.backend_bi_rest_erp.dto.sales.RevenueByProductItem;
import com.rest_erp.backend_bi_rest_erp.dto.sales.SalesKpiResponse;
import com.rest_erp.backend_bi_rest_erp.repository.sales.SalesKpiRepository;
import com.rest_erp.backend_bi_rest_erp.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalesKpiService {

    private final SalesKpiRepository salesKpiRepository;

    public SalesKpiResponse getSalesKpis(LocalDate startDate, LocalDate endDate) {

        Integer companyKey = TenantContext.getCompanyKey();

        BigDecimal totalRevenue = salesKpiRepository.getTotalRevenue(companyKey, startDate, endDate);
        Long numberOfDeals = salesKpiRepository.getNumberOfDeals(companyKey, startDate, endDate);
        BigDecimal winRate = salesKpiRepository.getWinRate(companyKey, startDate, endDate);
        BigDecimal averageDealValue = salesKpiRepository.getAverageDealValue(companyKey, startDate, endDate);
        Long salesOrdersCount = salesKpiRepository.getSalesOrdersCount(companyKey, startDate, endDate);
        BigDecimal outstandingReceivables = salesKpiRepository.getOutstandingReceivables(companyKey, startDate, endDate);
        Long pipelineDealsCount = salesKpiRepository.getPipelineDealsCount(companyKey);
        BigDecimal pipelineValue = salesKpiRepository.getPipelineValue(companyKey);

        Long activeCustomers = salesKpiRepository.getActiveCustomers(companyKey, startDate, endDate);
        Long totalCustomers = salesKpiRepository.getTotalCustomers(companyKey);

        Long inactiveCustomers = Math.max(
                0L,
                (totalCustomers != null ? totalCustomers : 0L) - (activeCustomers != null ? activeCustomers : 0L)
        );

        BigDecimal averageCustomerValue = BigDecimal.ZERO;
        if (activeCustomers != null && activeCustomers > 0) {
            averageCustomerValue = totalRevenue.divide(
                    BigDecimal.valueOf(activeCustomers),
                    2,
                    java.math.RoundingMode.HALF_UP
            );
        }

        BigDecimal conversionRate = salesKpiRepository.getConversionRate(companyKey, startDate, endDate);

        return SalesKpiResponse.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .numberOfDeals(numberOfDeals != null ? numberOfDeals : 0L)
                .winRate(winRate != null ? winRate : BigDecimal.ZERO)
                .averageDealValue(averageDealValue != null ? averageDealValue : BigDecimal.ZERO)
                .salesOrdersCount(salesOrdersCount != null ? salesOrdersCount : 0L)
                .outstandingReceivables(outstandingReceivables != null ? outstandingReceivables : BigDecimal.ZERO)
                .pipelineDealsCount(pipelineDealsCount != null ? pipelineDealsCount : 0L)
                .pipelineValue(pipelineValue != null ? pipelineValue : BigDecimal.ZERO)
                .activeCustomers(activeCustomers != null ? activeCustomers : 0L)
                .inactiveCustomers(inactiveCustomers)
                .averageCustomerValue(averageCustomerValue)
                .conversionRate(conversionRate != null ? conversionRate : BigDecimal.ZERO)
                .build();
    }

    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.SalesRevenueTrendItem> getRevenueTrend(LocalDate startDate, LocalDate endDate) {

        Integer companyKey = TenantContext.getCompanyKey();

        java.util.List<Object[]> rows = salesKpiRepository.getRevenueTrend(companyKey, startDate, endDate);

        java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.SalesRevenueTrendItem> result = new java.util.ArrayList<>();

        for (Object[] row : rows) {
            String label = row[0] != null ? row[0].toString() : "";
            BigDecimal value = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;

            result.add(
                    com.rest_erp.backend_bi_rest_erp.dto.sales.SalesRevenueTrendItem.builder()
                            .label(label)
                            .value(value)
                            .build()
            );
        }

        return result;
    }

    public List<PipelineItem> getPipelineDistribution() {

        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getPipelineDistribution(companyKey);

        List<PipelineItem> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    PipelineItem.builder()
                            .label(row[0] != null ? row[0].toString() : "")
                            .value(row[1] != null ? ((Number) row[1]).longValue() : 0L)
                            .build()
            );
        }

        return result;
    }

    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.RecentSalesOrderItem> getRecentSalesOrders(
            LocalDate startDate,
            LocalDate endDate
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        java.util.List<Object[]> rows = salesKpiRepository.getRecentSalesOrders(companyKey, startDate, endDate);

        java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.RecentSalesOrderItem> result = new java.util.ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    com.rest_erp.backend_bi_rest_erp.dto.sales.RecentSalesOrderItem.builder()
                            .id(row[0] != null ? row[0].toString() : "")
                            .customer(row[1] != null ? row[1].toString() : "Unknown Customer")
                            .date(row[2] != null ? java.time.LocalDate.parse(row[2].toString()) : null)
                            .amount(row[3] != null ? new java.math.BigDecimal(row[3].toString()) : java.math.BigDecimal.ZERO)
                            .status(row[4] != null ? row[4].toString() : "")
                            .build()
            );
        }

        return result;
    }

    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.TopSalespersonItem> getTopSalespersons(
            LocalDate startDate,
            LocalDate endDate
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        java.util.List<Object[]> rows = salesKpiRepository.getTopSalespersons(companyKey, startDate, endDate);

        java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.TopSalespersonItem> result =
                new java.util.ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    com.rest_erp.backend_bi_rest_erp.dto.sales.TopSalespersonItem.builder()
                            .name(row[0] != null ? row[0].toString() : "Unknown Salesperson")
                            .amount(row[1] != null ? new java.math.BigDecimal(row[1].toString()) : java.math.BigDecimal.ZERO)
                            .build()
            );
        }

        return result;
    }

    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.RevenueByCustomerItem> getRevenueByCustomer(
            LocalDate startDate,
            LocalDate endDate
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        java.util.List<Object[]> rows = salesKpiRepository.getRevenueByCustomer(companyKey, startDate, endDate);

        java.util.List<com.rest_erp.backend_bi_rest_erp.dto.sales.RevenueByCustomerItem> result =
                new java.util.ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    com.rest_erp.backend_bi_rest_erp.dto.sales.RevenueByCustomerItem.builder()
                            .name(row[0] != null ? row[0].toString() : "Unknown Customer")
                            .amount(row[1] != null ? new java.math.BigDecimal(row[1].toString()) : java.math.BigDecimal.ZERO)
                            .build()
            );
        }

        return result;
    }

    public List<RevenueByProductItem> getRevenueByProduct(
            LocalDate startDate,
            LocalDate endDate
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getRevenueByProduct(companyKey, startDate, endDate);

        List<RevenueByProductItem> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    RevenueByProductItem.builder()
                            .name(row[0] != null ? row[0].toString() : "Unknown")
                            .amount(row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO)
                            .build()
            );
        }

        return result;
    }

    public List<RetentionItem> getCustomerRetention() {

        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getCustomerRetention(companyKey);

        List<RetentionItem> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    RetentionItem.builder()
                            .label(row[0].toString())
                            .value(row[1] != null ? ((Number) row[1]).doubleValue() : 0.0)
                            .build()
            );
        }

        return result;
    }

    public List<Map<String, Object>> getHighValueDeals() {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getHighValueDeals(companyKey);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "Deal #" + row[0]);
            item.put("value", row[1]);
            result.add(item);
        }

        return result;
    }
}