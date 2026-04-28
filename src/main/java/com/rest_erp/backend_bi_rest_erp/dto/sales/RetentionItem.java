package com.rest_erp.backend_bi_rest_erp.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetentionItem {
    private String label;
    private Double value;
}
