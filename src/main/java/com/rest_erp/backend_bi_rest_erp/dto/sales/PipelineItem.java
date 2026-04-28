package com.rest_erp.backend_bi_rest_erp.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PipelineItem {
    private String label;
    private Long value;
}
