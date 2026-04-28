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
public class RevenueByProductItem {
    private String name;
    private BigDecimal amount;
}
