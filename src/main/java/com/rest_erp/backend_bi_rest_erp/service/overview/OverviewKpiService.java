package com.rest_erp.backend_bi_rest_erp.service.overview;

import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewKpiResponse;
import com.rest_erp.backend_bi_rest_erp.repository.overview.OverviewKpiRepository;
import com.rest_erp.backend_bi_rest_erp.tenant.TenantContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewFinancialTrendItem;
import java.util.List;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewFinancialTrendItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewCashSummaryItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewPipelineFunnelItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewDealStatusItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewTopSalespersonItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewAttendanceTrendItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewDepartmentDistributionItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewCustomerRetentionItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewTopCustomerItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewOperationalAlertItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewExecutiveLedgerItem;
@Service
public class OverviewKpiService {

    private final OverviewKpiRepository overviewKpiRepository;

    public OverviewKpiService(OverviewKpiRepository overviewKpiRepository) {
        this.overviewKpiRepository = overviewKpiRepository;
    }

    public OverviewKpiResponse getOverviewKpis(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        Integer totalEmployees = overviewKpiRepository.getTotalEmployees(companyKey);

        BigDecimal presenceRate = overviewKpiRepository.getPresenceRate(
                companyKey,
                startDateKey,
                endDateKey
        );

        BigDecimal totalRevenue = overviewKpiRepository.getTotalRevenue(
                companyKey,
                startDateKey,
                endDateKey
        );

        BigDecimal totalExpenses = overviewKpiRepository.getTotalExpenses(
                companyKey,
                startDateKey,
                endDateKey
        );

        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        BigDecimal winRate = overviewKpiRepository.getWinRate(
                companyKey,
                startDateKey,
                endDateKey
        );

        BigDecimal pipelineValue = overviewKpiRepository.getPipelineValue(companyKey);

        String currency = overviewKpiRepository.getCompanyCurrency(companyKey);

        return new OverviewKpiResponse(
                totalEmployees,
                presenceRate,
                totalRevenue,
                netProfit,
                winRate,
                pipelineValue,
                currency
        );
    }

    private Integer toDateKey(LocalDate date) {
        return date.getYear() * 10000
                + date.getMonthValue() * 100
                + date.getDayOfMonth();
    }

    public List<OverviewFinancialTrendItem> getFinancialTrend(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return overviewKpiRepository.getFinancialTrend(companyKey, startDateKey, endDateKey);
    }
    public OverviewCashSummaryItem getCashSummary(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return overviewKpiRepository.getCashSummary(companyKey, startDateKey, endDateKey);
    }
    public List<OverviewPipelineFunnelItem> getSalesPipelineFunnel(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        return overviewKpiRepository.getSalesPipelineFunnel(companyKey);
    }
    public List<OverviewDealStatusItem> getDealStatus(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        return overviewKpiRepository.getDealStatus(companyKey);
    }

    public List<OverviewTopSalespersonItem> getTopSalesPerformers(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        return overviewKpiRepository.getTopSalesPerformers(companyKey);
    }

    public List<OverviewAttendanceTrendItem> getAttendanceTrend(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return overviewKpiRepository.getAttendanceTrend(
                companyKey,
                startDateKey,
                endDateKey
        );
    }

    public List<OverviewDepartmentDistributionItem> getDepartmentDistribution() {
        Integer companyKey = TenantContext.getCompanyKey();

        return overviewKpiRepository.getDepartmentDistribution(companyKey);
    }
    public OverviewCustomerRetentionItem getCustomerRetention() {
        Integer companyKey = TenantContext.getCompanyKey();

        return overviewKpiRepository.getCustomerRetention(companyKey);
    }
    public List<OverviewTopCustomerItem> getTopCustomersByRevenue(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return overviewKpiRepository.getTopCustomersByRevenue(
                companyKey,
                startDateKey,
                endDateKey
        );
    }
    public List<OverviewOperationalAlertItem> getOperationalAlerts(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return overviewKpiRepository.getOperationalAlerts(
                companyKey,
                startDateKey,
                endDateKey
        );
    }
    public List<OverviewExecutiveLedgerItem> getExecutiveLedger(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return overviewKpiRepository.getExecutiveLedger(
                companyKey,
                startDateKey,
                endDateKey
        );
    }
}