package com.rest_erp.backend_bi_rest_erp.service.hr;

import com.rest_erp.backend_bi_rest_erp.dto.hr.HrKpiResponse;
import com.rest_erp.backend_bi_rest_erp.repository.CommonRepository;
import com.rest_erp.backend_bi_rest_erp.repository.hr.HrKpiRepository;
import com.rest_erp.backend_bi_rest_erp.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HrKpiService {

    private final HrKpiRepository hrKpiRepository;
    private final CommonRepository commonRepository;

    public HrKpiResponse getHrKpis(LocalDate startDate, LocalDate endDate) {

        Integer companyKey = TenantContext.getCompanyKey();
        String currency = commonRepository.getCompanyCurrency(companyKey);

        Long totalEmployees = hrKpiRepository.getTotalEmployees(companyKey);
        Long activeEmployees = hrKpiRepository.getActiveEmployees(companyKey);

        Long inactiveEmployees = Math.max(
                0L,
                (totalEmployees != null ? totalEmployees : 0L)
                        - (activeEmployees != null ? activeEmployees : 0L)
        );

        Long onboardingEmployees = hrKpiRepository.getOnboardingEmployees(companyKey, startDate, endDate);
        Long offboardingEmployees = hrKpiRepository.getOffboardingEmployees(companyKey, startDate, endDate);

        BigDecimal attritionRate = BigDecimal.ZERO;

        if (activeEmployees != null && activeEmployees > 0) {
            attritionRate = BigDecimal.valueOf(offboardingEmployees)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(activeEmployees), 2, RoundingMode.HALF_UP);
        }

        BigDecimal averageTenureDays = hrKpiRepository.getAverageTenureDays(companyKey);

        BigDecimal averageTenureYears = averageTenureDays
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);

        BigDecimal presenceRate = hrKpiRepository.getPresenceRate(companyKey, startDate, endDate);
        BigDecimal absenceRate = hrKpiRepository.getAbsenceRate(companyKey, startDate, endDate);
        Long lateCheckins = hrKpiRepository.getLateCheckins(companyKey, startDate, endDate);
        BigDecimal overtimeHours = hrKpiRepository.getOvertimeHours(companyKey, startDate, endDate);

        BigDecimal totalPayroll = hrKpiRepository.getTotalPayroll(companyKey, startDate, endDate);
        BigDecimal averageSalary = hrKpiRepository.getAverageSalary(companyKey, startDate, endDate);

        BigDecimal averageCostPerEmployee = BigDecimal.ZERO;
        if (activeEmployees != null && activeEmployees > 0) {
            averageCostPerEmployee = totalPayroll.divide(
                    BigDecimal.valueOf(activeEmployees),
                    2,
                    java.math.RoundingMode.HALF_UP
            );
        }

        Long activeJobOffers = hrKpiRepository.getActiveJobOffers(companyKey, startDate, endDate);
        Long totalApplications = hrKpiRepository.getTotalApplications(companyKey, startDate, endDate);
        Long hiredApplications = hrKpiRepository.getHiredApplications(companyKey, startDate, endDate);

        BigDecimal conversionRate = BigDecimal.ZERO;
        if (totalApplications != null && totalApplications > 0) {
            conversionRate = BigDecimal.valueOf(hiredApplications)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalApplications), 2, RoundingMode.HALF_UP);
        }

        BigDecimal absenteeismVolatilityIndex =
                hrKpiRepository.getAbsenteeismVolatilityIndex(companyKey, startDate, endDate);

        BigDecimal lateArrivalPenalty = BigDecimal.ZERO;
        if (lateCheckins != null && lateCheckins > 0) {
            lateArrivalPenalty = BigDecimal.valueOf(lateCheckins)
                    .multiply(BigDecimal.valueOf(10));
        }

        String applicationQuality = "No Data";

        if (totalApplications != null && totalApplications > 0) {
            if (conversionRate.compareTo(BigDecimal.valueOf(20)) >= 0) {
                applicationQuality = "High";
            } else if (conversionRate.compareTo(BigDecimal.valueOf(10)) >= 0) {
                applicationQuality = "Medium";
            } else {
                applicationQuality = "Low";
            }
        }

        BigDecimal efficiencyIndex = BigDecimal.ZERO;

        if (conversionRate != null && conversionRate.compareTo(BigDecimal.ZERO) > 0) {
            efficiencyIndex = conversionRate
                    .divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP);

            if (efficiencyIndex.compareTo(BigDecimal.TEN) > 0) {
                efficiencyIndex = BigDecimal.TEN;
            }
        }



        return HrKpiResponse.builder()
                .totalEmployees(totalEmployees != null ? totalEmployees : 0L)
                .activeEmployees(activeEmployees != null ? activeEmployees : 0L)
                .inactiveEmployees(inactiveEmployees)
                .onboardingEmployees(onboardingEmployees != null ? onboardingEmployees : 0L)
                .offboardingEmployees(offboardingEmployees != null ? offboardingEmployees : 0L)
                .attritionRate(attritionRate)
                .averageTenure(averageTenureYears)
                .presenceRate(presenceRate != null ? presenceRate : BigDecimal.ZERO)
                .absenceRate(absenceRate != null ? absenceRate : BigDecimal.ZERO)
                .lateCheckins(lateCheckins != null ? lateCheckins : 0L)
                .overtimeHours(overtimeHours != null ? overtimeHours : BigDecimal.ZERO)
                .totalPayroll(totalPayroll != null ? totalPayroll : BigDecimal.ZERO)
                .averageSalary(averageSalary != null ? averageSalary : BigDecimal.ZERO)
                .averageCostPerEmployee(averageCostPerEmployee)
                .activeJobOffers(activeJobOffers != null ? activeJobOffers : 0L)
                .totalApplications(totalApplications != null ? totalApplications : 0L)
                .conversionRate(conversionRate)
                .absenteeismVolatilityIndex(absenteeismVolatilityIndex != null ? absenteeismVolatilityIndex : BigDecimal.ZERO)
                .hiredApplications(hiredApplications != null ? hiredApplications : 0L)
                .lateArrivalPenalty(lateArrivalPenalty)
                .applicationQuality(applicationQuality)
                .efficiencyIndex(efficiencyIndex)
                .currency(currency)
                .build();
    }

    public List<Map<String, Object>> getHeadcountTrend(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = hrKpiRepository.getHeadcountTrend(companyKey, startDate, endDate);

        return rows.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();

                    map.put("period", row[0]);
                    map.put("headcount", row[1] != null ? ((Number) row[1]).longValue() : 0);

                    return map;
                })
                .toList();
    }

    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.AttendanceTrendItem> getAttendanceTrend(
            LocalDate startDate,
            LocalDate endDate
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        java.util.List<Object[]> rows = hrKpiRepository.getAttendanceTrend(companyKey, startDate, endDate);

        java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.AttendanceTrendItem> result =
                new java.util.ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    com.rest_erp.backend_bi_rest_erp.dto.hr.AttendanceTrendItem.builder()
                            .label(row[0] != null ? row[0].toString() : "")
                            .presenceRate(row[1] != null ? new java.math.BigDecimal(row[1].toString()) : java.math.BigDecimal.ZERO)
                            .absenceRate(row[2] != null ? new java.math.BigDecimal(row[2].toString()) : java.math.BigDecimal.ZERO)
                            .build()
            );
        }

        return result;
    }

    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.TenureDistributionItem> getTenureDistribution() {

        Integer companyKey = TenantContext.getCompanyKey();

        java.util.List<Object[]> rows = hrKpiRepository.getTenureDistribution(companyKey);

        java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.TenureDistributionItem> result =
                new java.util.ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    com.rest_erp.backend_bi_rest_erp.dto.hr.TenureDistributionItem.builder()
                            .label(row[0] != null ? row[0].toString() : "")
                            .value(row[1] != null ? ((Number) row[1]).longValue() : 0L)
                            .build()
            );
        }

        return result;
    }

    public List<Map<String, Object>> getEmployeesByDepartment(
            LocalDate startDate,
            LocalDate endDate
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = hrKpiRepository.getEmployeesByDepartment(
                companyKey,
                startDate,
                endDate
        );

        return rows.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();

                    map.put("department", row[0]);
                    map.put("count", row[1] != null ? ((Number) row[1]).longValue() : 0);

                    return map;
                })
                .toList();
    }

    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.SalaryBenchmarkItem> getSalaryBenchmarking(
            LocalDate startDate,
            LocalDate endDate
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        java.util.List<Object[]> rows =
                hrKpiRepository.getSalaryBenchmarking(companyKey, startDate, endDate);

        java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.SalaryBenchmarkItem> result =
                new java.util.ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    com.rest_erp.backend_bi_rest_erp.dto.hr.SalaryBenchmarkItem.builder()
                            .department(row[0] != null ? row[0].toString() : "Unknown Department")
                            .averageSalary(row[1] != null ? new java.math.BigDecimal(row[1].toString()) : java.math.BigDecimal.ZERO)
                            .maximumSalary(row[2] != null ? new java.math.BigDecimal(row[2].toString()) : java.math.BigDecimal.ZERO)
                            .build()
            );
        }

        return result;
    }

    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.HiringFunnelItem> getHiringFunnel(
            LocalDate startDate,
            LocalDate endDate
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        java.util.List<Object[]> rows =
                hrKpiRepository.getHiringFunnel(companyKey, startDate, endDate);

        java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.HiringFunnelItem> result =
                new java.util.ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    com.rest_erp.backend_bi_rest_erp.dto.hr.HiringFunnelItem.builder()
                            .stage(row[0] != null ? row[0].toString() : "")
                            .count(row[1] != null ? ((Number) row[1]).longValue() : 0L)
                            .build()
            );
        }

        return result;
    }

    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.UpcomingBirthdayItem> getUpcomingBirthdays() {

        Integer companyKey = TenantContext.getCompanyKey();

        java.util.List<Object[]> rows = hrKpiRepository.getUpcomingBirthdays(companyKey);

        java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.UpcomingBirthdayItem> result =
                new java.util.ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    com.rest_erp.backend_bi_rest_erp.dto.hr.UpcomingBirthdayItem.builder()
                            .employee(row[0] != null ? row[0].toString() : "Unknown Employee")
                            .department(row[1] != null ? row[1].toString() : "Unknown Department")
                            .remainingDays(row[2] != null ? ((Number) row[2]).longValue() : 0L)
                            .build()
            );
        }

        return result;
    }




}