package com.rest_erp.backend_bi_rest_erp.controller.hr;

import com.rest_erp.backend_bi_rest_erp.dto.hr.HrKpiResponse;
import com.rest_erp.backend_bi_rest_erp.service.hr.HrKpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bi/hr")
@RequiredArgsConstructor
public class HrKpiController {

    private final HrKpiService hrKpiService;

    @GetMapping("/kpis")
    public HrKpiResponse getHrKpis(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return hrKpiService.getHrKpis(startDate, endDate);
    }

    @GetMapping("/headcount-trend")
    public ResponseEntity<List<Map<String, Object>>> getHeadcountTrend(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        return ResponseEntity.ok(hrKpiService.getHeadcountTrend(startDate, endDate));
    }



    @GetMapping("/attendance-trend")
    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.AttendanceTrendItem> getAttendanceTrend(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return hrKpiService.getAttendanceTrend(startDate, endDate);
    }

    @GetMapping("/tenure-distribution")
    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.TenureDistributionItem> getTenureDistribution() {
        return hrKpiService.getTenureDistribution();
    }

    @GetMapping("/employees-by-department")
    public ResponseEntity<List<Map<String, Object>>> getEmployeesByDepartment(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                hrKpiService.getEmployeesByDepartment(startDate, endDate)
        );
    }

    @GetMapping("/salary-benchmarking")
    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.SalaryBenchmarkItem> getSalaryBenchmarking(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return hrKpiService.getSalaryBenchmarking(startDate, endDate);
    }

    @GetMapping("/hiring-funnel")
    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.HiringFunnelItem> getHiringFunnel(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return hrKpiService.getHiringFunnel(startDate, endDate);
    }

    @GetMapping("/upcoming-birthdays")
    public java.util.List<com.rest_erp.backend_bi_rest_erp.dto.hr.UpcomingBirthdayItem> getUpcomingBirthdays() {
        return hrKpiService.getUpcomingBirthdays();
    }
}