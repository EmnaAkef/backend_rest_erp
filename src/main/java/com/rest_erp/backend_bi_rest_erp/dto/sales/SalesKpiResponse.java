package com.rest_erp.backend_bi_rest_erp.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesKpiResponse {

    private BigDecimal totalRevenue;
    private Long numberOfDeals;
    private BigDecimal winRate;
    private BigDecimal averageDealValue;
    private Long salesOrdersCount;
    private BigDecimal outstandingReceivables;
    private Long pipelineDealsCount;
    private BigDecimal pipelineValue;
    private Long activeCustomers;
    private Long inactiveCustomers;
    private BigDecimal averageCustomerValue;
    private BigDecimal conversionRate;
}