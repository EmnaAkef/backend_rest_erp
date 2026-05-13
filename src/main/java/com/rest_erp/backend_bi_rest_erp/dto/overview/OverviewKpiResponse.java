package com.rest_erp.backend_bi_rest_erp.dto.overview;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverviewKpiResponse {

    private Integer totalEmployees;
    private BigDecimal presenceRate;
    private BigDecimal totalRevenue;
    private BigDecimal netProfit;
    private BigDecimal winRate;
    private BigDecimal pipelineValue;
    private String currency;
}