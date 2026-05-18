package com.rest_erp.backend_bi_rest_erp.dto.overview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverviewCashSummaryItem {

    private BigDecimal cashBalance;
    private BigDecimal inflow;
    private BigDecimal outflow;
    private BigDecimal netCashFlow;
}