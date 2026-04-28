package com.rest_erp.backend_bi_rest_erp.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentSalesOrderItem {
    private String id;
    private String customer;
    private LocalDate date;
    private BigDecimal amount;
    private String status;
}
