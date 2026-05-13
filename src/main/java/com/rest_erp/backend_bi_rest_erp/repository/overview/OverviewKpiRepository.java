package com.rest_erp.backend_bi_rest_erp.repository.overview;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewFinancialTrendItem;
import java.util.List;
import java.math.BigDecimal;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewExecutiveLedgerItem;
@Repository
public class OverviewKpiRepository {

    private final JdbcTemplate jdbcTemplate;

    public OverviewKpiRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private BigDecimal queryForBigDecimal(String sql, Object... params) {
        BigDecimal result = jdbcTemplate.queryForObject(sql, BigDecimal.class, params);
        return result != null ? result : BigDecimal.ZERO;
    }

    private Integer queryForInteger(String sql, Object... params) {
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, params);
        return result != null ? result : 0;
    }

    public Integer getTotalEmployees(Integer companyKey) {
        String sql = """
            SELECT COUNT(*)
            FROM dim_user
            WHERE company_key = ?
              AND is_current = true
              AND COALESCE(active, true) = true
            """;

        return queryForInteger(sql, companyKey);
    }

    public BigDecimal getPresenceRate(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
        SELECT 
            CASE
                WHEN COALESCE(SUM(scheduled_shift_count), 0) = 0 THEN 0
                ELSE ROUND(
                    (
                        COALESCE(SUM(present_shift_count), 0) * 100.0
                    ) / COALESCE(SUM(scheduled_shift_count), 0),
                    2
                )
            END
        FROM fact_attendance_shift
        WHERE company_key = ?
          AND date_key BETWEEN ? AND ?
        """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getTotalRevenue(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(total), 0)
            FROM fact_invoice
            WHERE company_key = ?
              AND date_key BETWEEN ? AND ?
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getTotalExpenses(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT COALESCE(SUM(total), 0)
            FROM fact_bill
            WHERE company_key = ?
              AND date_key BETWEEN ? AND ?
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getWinRate(Integer companyKey, Integer startDateKey, Integer endDateKey) {
        String sql = """
            SELECT
                CASE
                    WHEN COUNT(CASE WHEN f.close_date_key IS NOT NULL THEN 1 END) = 0 THEN 0
                    ELSE ROUND(
                        (
                            COUNT(
                                CASE
                                    WHEN f.close_date_key IS NOT NULL
                                     AND LOWER(ws.status_label) IN ('win', 'done')
                                    THEN 1
                                END
                            ) * 100.0
                        ) / COUNT(CASE WHEN f.close_date_key IS NOT NULL THEN 1 END),
                        2
                    )
                END
            FROM fact_deal f
            LEFT JOIN dim_workstatus ws
                ON ws.workstatus_key = f.workstatus_key
            LEFT JOIN dim_date d
                ON d.date_key = f.close_date_key
            WHERE f.company_key = ?
              AND d.full_date >= TO_DATE(CAST(? AS TEXT), 'YYYYMMDD')
              AND d.full_date <= TO_DATE(CAST(? AS TEXT), 'YYYYMMDD')
            """;

        return queryForBigDecimal(sql, companyKey, startDateKey, endDateKey);
    }

    public BigDecimal getPipelineValue(Integer companyKey) {
        String sql = """
            SELECT COALESCE(SUM(f.deal_value), 0)
            FROM fact_deal f
            LEFT JOIN dim_workstatus ws
                ON ws.workstatus_key = f.workstatus_key
            WHERE f.company_key = ?
              AND COALESCE(f.is_archived, false) = false
              AND LOWER(COALESCE(ws.status_label, '')) NOT IN ('win', 'done', 'lost', 'closed')
            """;

        return queryForBigDecimal(sql, companyKey);
    }

    public List<OverviewFinancialTrendItem> getFinancialTrend(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
        WITH revenue_by_month AS (
            SELECT
                d.year,
                d.month,
                MIN(d.full_date) AS month_date,
                COALESCE(SUM(fi.total), 0) AS revenue
            FROM dim_date d
            LEFT JOIN fact_invoice fi
                ON fi.date_key = d.date_key
               AND fi.company_key = ?
            WHERE d.date_key BETWEEN ? AND ?
            GROUP BY d.year, d.month
        ),
        expenses_by_month AS (
            SELECT
                d.year,
                d.month,
                COALESCE(SUM(fb.total), 0) AS expenses
            FROM dim_date d
            LEFT JOIN fact_bill fb
                ON fb.date_key = d.date_key
               AND fb.company_key = ?
            WHERE d.date_key BETWEEN ? AND ?
            GROUP BY d.year, d.month
        )
        SELECT
            TO_CHAR(r.month_date, 'Mon YYYY') AS period,
            r.revenue,
            e.expenses,
            r.revenue - e.expenses AS net_profit
        FROM revenue_by_month r
        JOIN expenses_by_month e
            ON r.year = e.year
           AND r.month = e.month
        ORDER BY r.year, r.month
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new OverviewFinancialTrendItem(
                        rs.getString("period"),
                        rs.getBigDecimal("revenue"),
                        rs.getBigDecimal("expenses"),
                        rs.getBigDecimal("net_profit")
                ),
                companyKey,
                startDateKey,
                endDateKey,
                companyKey,
                startDateKey,
                endDateKey
        );
    }
    public OverviewCashSummaryItem getCashSummary(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
        WITH cash_balance AS (
            SELECT
                COALESCE(SUM(f.close_balance_debit - f.close_balance_credit), 0) AS cash_balance
            FROM fact_chart_balance_snapshot f
            JOIN dim_chart_account a
                ON f.chart_account_key = a.chart_key
            WHERE f.company_key = ?
              AND f.date_key <= ?
              AND a.is_current = true
              AND LOWER(a.account_type) IN (
                    'cash and cash equivalents',
                    'bank balance'
              )
        ),
        cash_flow AS (
            SELECT
                COALESCE(SUM(f.debit), 0) AS inflow,
                COALESCE(SUM(f.credit), 0) AS outflow
            FROM fact_cash_movement f
            JOIN dim_chart_account a
                ON f.chart_account_key = a.chart_key
            WHERE f.company_key = ?
              AND f.date_key BETWEEN ? AND ?
              AND a.is_current = true
              AND LOWER(a.account_type) IN (
                    'cash and cash equivalents',
                    'bank balance'
              )
        )
        SELECT
            cash_balance.cash_balance,
            cash_flow.inflow,
            cash_flow.outflow
        FROM cash_balance, cash_flow
        """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new OverviewCashSummaryItem(
                        rs.getBigDecimal("cash_balance"),
                        rs.getBigDecimal("inflow"),
                        rs.getBigDecimal("outflow")
                ),
                companyKey,
                endDateKey,
                companyKey,
                startDateKey,
                endDateKey
        );
    }
    public List<OverviewPipelineFunnelItem> getSalesPipelineFunnel(Integer companyKey) {
        String sql = """
        SELECT
            COALESCE(ws.status_label, 'Unknown') AS stage,
            COALESCE(SUM(f.deal_count), 0) AS deal_count,
            COALESCE(SUM(f.deal_value), 0) AS pipeline_value
        FROM fact_deal f
        LEFT JOIN dim_workstatus ws
            ON ws.workstatus_key = f.workstatus_key
        WHERE f.company_key = ?
          AND COALESCE(f.is_archived, false) = false
        GROUP BY COALESCE(ws.status_label, 'Unknown')
        ORDER BY
            CASE COALESCE(ws.status_label, 'Unknown')
                WHEN 'Generated' THEN 1
                WHEN 'Initial Contact' THEN 2
                WHEN 'Win' THEN 3
                WHEN 'Lost' THEN 4
                ELSE 5
            END
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> OverviewPipelineFunnelItem.builder()
                        .stage(rs.getString("stage"))
                        .dealCount(rs.getLong("deal_count"))
                        .pipelineValue(rs.getBigDecimal("pipeline_value"))
                        .build(),
                companyKey
        );
    }
    public List<OverviewDealStatusItem> getDealStatus(Integer companyKey) {
        String sql = """
        WITH grouped_deals AS (
            SELECT
                CASE
                    WHEN ws.status_label = 'Win' THEN 'Won'
                    WHEN ws.status_label = 'Lost' THEN 'Lost'
                    WHEN ws.status_label IN ('Generated', 'Initial Contact') THEN 'Progress'
                    ELSE 'Other'
                END AS status,
                COALESCE(SUM(f.deal_count), 0) AS deal_count
            FROM fact_deal f
            LEFT JOIN dim_workstatus ws
                ON ws.workstatus_key = f.workstatus_key
            WHERE f.company_key = ?
              AND COALESCE(f.is_archived, false) = false
            GROUP BY
                CASE
                    WHEN ws.status_label = 'Win' THEN 'Won'
                    WHEN ws.status_label = 'Lost' THEN 'Lost'
                    WHEN ws.status_label IN ('Generated', 'Initial Contact') THEN 'Progress'
                    ELSE 'Other'
                END
        ),
        total_deals AS (
            SELECT COALESCE(SUM(deal_count), 0) AS total_count
            FROM grouped_deals
        )
        SELECT
            g.status,
            g.deal_count,
            CASE
                WHEN t.total_count = 0 THEN 0
                ELSE ROUND((g.deal_count * 100.0 / t.total_count), 2)
            END AS percentage
        FROM grouped_deals g
        CROSS JOIN total_deals t
        WHERE g.status IN ('Won', 'Progress', 'Lost')
        ORDER BY
            CASE g.status
                WHEN 'Won' THEN 1
                WHEN 'Progress' THEN 2
                WHEN 'Lost' THEN 3
                ELSE 4
            END
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> OverviewDealStatusItem.builder()
                        .status(rs.getString("status"))
                        .dealCount(rs.getLong("deal_count"))
                        .percentage(rs.getBigDecimal("percentage"))
                        .build(),
                companyKey
        );
    }
    public List<OverviewTopSalespersonItem> getTopSalesPerformers(Integer companyKey) {
        String sql = """
        SELECT
            COALESCE(u.user_name, 'Unknown Salesperson') AS salesperson_name,
            COALESCE(SUM(f.deal_value), 0) AS total_revenue,
            COALESCE(SUM(f.deal_count), 0) AS deals_count
        FROM fact_deal f
        LEFT JOIN dim_user u
            ON u.user_key = f.owner_user_key
        WHERE f.company_key = ?
          AND COALESCE(f.is_archived, false) = false
        GROUP BY COALESCE(u.user_name, 'Unknown Salesperson')
        ORDER BY total_revenue DESC
        LIMIT 4
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> OverviewTopSalespersonItem.builder()
                        .salespersonName(rs.getString("salesperson_name"))
                        .totalRevenue(rs.getBigDecimal("total_revenue"))
                        .dealsCount(rs.getLong("deals_count"))
                        .build(),
                companyKey
        );
    }
    public List<OverviewAttendanceTrendItem> getAttendanceTrend(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
        SELECT
            TO_CHAR(DATE_TRUNC('month', d.full_date), 'Mon YYYY') AS period,

            CASE
                WHEN COALESCE(SUM(f.scheduled_shift_count), 0) = 0 THEN 0
                ELSE ROUND(
                    COALESCE(SUM(f.present_shift_count), 0) * 100.0
                    / COALESCE(SUM(f.scheduled_shift_count), 0),
                    2
                )
            END AS presence_rate,

            COALESCE(SUM(f.late_checkin_count), 0) AS late_checkins,

            CASE
                WHEN COALESCE(SUM(f.scheduled_shift_count), 0) = 0 THEN 0
                ELSE ROUND(
                    (
                        COALESCE(SUM(f.present_shift_count), 0)
                        - COALESCE(SUM(f.late_checkin_count), 0)
                    ) * 100.0
                    / COALESCE(SUM(f.scheduled_shift_count), 0),
                    2
                )
            END AS on_time_rate

        FROM fact_attendance_shift f
        JOIN dim_date d
            ON d.date_key = f.date_key
        WHERE f.company_key = ?
          AND f.date_key BETWEEN ? AND ?
        GROUP BY DATE_TRUNC('month', d.full_date)
        ORDER BY DATE_TRUNC('month', d.full_date)
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> OverviewAttendanceTrendItem.builder()
                        .period(rs.getString("period"))
                        .presenceRate(rs.getBigDecimal("presence_rate"))
                        .lateCheckins(rs.getLong("late_checkins"))
                        .onTimeRate(rs.getBigDecimal("on_time_rate"))
                        .build(),
                companyKey,
                startDateKey,
                endDateKey
        );
    }

    public List<OverviewDepartmentDistributionItem> getDepartmentDistribution(Integer companyKey) {
        String sql = """
        SELECT
            COALESCE(d.department_name, 'Unknown Department') AS department_name,
            COUNT(DISTINCT u.user_id) AS employee_count
        FROM dim_user u
        LEFT JOIN dim_department d
            ON d.department_key = u.department_key
        WHERE u.company_key = ?
          AND u.type = 'EMPLOYEE'
          AND u.is_current = true
          AND COALESCE(u.active, true) = true
        GROUP BY COALESCE(d.department_name, 'Unknown Department')
        ORDER BY employee_count DESC
        LIMIT 5
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> OverviewDepartmentDistributionItem.builder()
                        .departmentName(rs.getString("department_name"))
                        .employeeCount(rs.getLong("employee_count"))
                        .build(),
                companyKey
        );
    }
    public OverviewCustomerRetentionItem getCustomerRetention(Integer companyKey) {
        String sql = """
        SELECT
            COUNT(DISTINCT c.customer_id) AS total_customers,

            COUNT(DISTINCT c.customer_id) FILTER (
                WHERE UPPER(COALESCE(c.status, '')) = 'ACTIVE'
            ) AS active_customers,

            COUNT(DISTINCT c.customer_id) FILTER (
                WHERE UPPER(COALESCE(c.status, '')) = 'INACTIVE'
            ) AS inactive_customers,

            CASE
                WHEN COUNT(DISTINCT c.customer_id) = 0 THEN 0
                ELSE ROUND(
                    COUNT(DISTINCT c.customer_id) FILTER (
                        WHERE UPPER(COALESCE(c.status, '')) = 'ACTIVE'
                    ) * 100.0 / COUNT(DISTINCT c.customer_id),
                    2
                )
            END AS retention_rate

        FROM dim_customer c
        WHERE c.company_key = ?
          AND c.is_current = true
        """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> OverviewCustomerRetentionItem.builder()
                        .totalCustomers(rs.getLong("total_customers"))
                        .activeCustomers(rs.getLong("active_customers"))
                        .inactiveCustomers(rs.getLong("inactive_customers"))
                        .retentionRate(rs.getBigDecimal("retention_rate"))
                        .build(),
                companyKey
        );
    }
    public List<OverviewTopCustomerItem> getTopCustomersByRevenue(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
    SELECT
        COALESCE(c.contact_name, 'Unknown Customer') AS customer_name,
        COALESCE(SUM(f.allocated_amount), 0) + COALESCE(SUM(f.invoice_total), 0) AS revenue
    FROM fact_sales_financials f
    LEFT JOIN dim_customer c
        ON c.customer_key = f.customer_key
    WHERE f.company_key = ?
      AND f.date_key BETWEEN ? AND ?
    GROUP BY COALESCE(c.contact_name, 'Unknown Customer')
    ORDER BY revenue DESC
    LIMIT 5
    """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> OverviewTopCustomerItem.builder()
                        .customerName(rs.getString("customer_name"))
                        .revenue(rs.getBigDecimal("revenue"))
                        .build(),
                companyKey,
                startDateKey,
                endDateKey
        );
    }

    public List<OverviewOperationalAlertItem> getOperationalAlerts(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        List<OverviewOperationalAlertItem> alerts = new ArrayList<>();

        BigDecimal overdueInvoices = getOverdueInvoicesAmount(companyKey, endDateKey);
        BigDecimal unscheduledLate = getUnscheduledLateRate(companyKey, startDateKey, endDateKey);
        BigDecimal leadDropOffRate = getLeadDropOffRate(companyKey);
        BigDecimal operationalBurn = getOperationalBurn(companyKey, startDateKey, endDateKey);

        alerts.add(OverviewOperationalAlertItem.builder()
                .category("Finance")
                .status(overdueInvoices.compareTo(BigDecimal.ZERO) > 0 ? "Critical" : "Normal")
                .title("Overdue Invoices")
                .value(overdueInvoices)
                .valueSuffix("")
                .color(overdueInvoices.compareTo(BigDecimal.ZERO) > 0 ? "red" : "green")
                .build());

        alerts.add(OverviewOperationalAlertItem.builder()
                .category("HR")
                .status(unscheduledLate.compareTo(BigDecimal.ZERO) > 0 ? "Warning" : "Normal")
                .title("Unscheduled Late")
                .value(unscheduledLate)
                .valueSuffix("% Avg")
                .color(unscheduledLate.compareTo(BigDecimal.ZERO) > 0 ? "orange" : "green")
                .build());

        alerts.add(OverviewOperationalAlertItem.builder()
                .category("Sales")
                .status(leadDropOffRate.compareTo(BigDecimal.ZERO) > 0 ? "Warning" : "Normal")
                .title("Lead Drop-off Rate")
                .value(leadDropOffRate)
                .valueSuffix("% Weekly")
                .color(leadDropOffRate.compareTo(BigDecimal.ZERO) > 0 ? "orange" : "green")
                .build());

        alerts.add(OverviewOperationalAlertItem.builder()
                .category("Finance")
                .status("Normal")
                .title("Operational Burn")
                .value(operationalBurn)
                .valueSuffix(" / Daily")
                .color("green")
                .build());

        return alerts;
    }
    private BigDecimal getOverdueInvoicesAmount(Integer companyKey, Integer endDateKey) {
        String sql = """
        SELECT COALESCE(SUM(fi.total - COALESCE(fi.partial_paid_amount, 0)), 0)
        FROM fact_invoice fi
        JOIN dim_invoice_status s
            ON s.status_key = fi.status_key
        WHERE fi.company_key = ?
          AND fi.due_date_key < ?
          AND UPPER(s.status_code) NOT IN ('PAID', 'REFUNDED', 'CREDIT_MEMO')
        """;

        BigDecimal result = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                companyKey,
                endDateKey
        );

        return result != null ? result : BigDecimal.ZERO;
    }

    private BigDecimal getUnscheduledLateRate(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
        SELECT
            CASE
                WHEN COALESCE(SUM(scheduled_shift_count), 0) = 0 THEN 0
                ELSE ROUND(
                    COALESCE(SUM(late_checkin_count), 0) * 100.0
                    / COALESCE(SUM(scheduled_shift_count), 0),
                    2
                )
            END
        FROM fact_attendance_shift
        WHERE company_key = ?
          AND date_key BETWEEN ? AND ?
        """;

        BigDecimal result = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                companyKey,
                startDateKey,
                endDateKey
        );

        return result != null ? result : BigDecimal.ZERO;
    }

    private BigDecimal getLeadDropOffRate(Integer companyKey) {
        String sql = """
        WITH deals AS (
            SELECT
                COALESCE(SUM(f.deal_count), 0) AS total_deals,
                COALESCE(SUM(f.deal_count) FILTER (
                    WHERE ws.status_label = 'Lost'
                ), 0) AS lost_deals
            FROM fact_deal f
            LEFT JOIN dim_workstatus ws
                ON ws.workstatus_key = f.workstatus_key
            WHERE f.company_key = ?
              AND COALESCE(f.is_archived, false) = false
        )
        SELECT
            CASE
                WHEN total_deals = 0 THEN 0
                ELSE ROUND(lost_deals * 100.0 / total_deals, 2)
            END
        FROM deals
        """;

        BigDecimal result = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                companyKey
        );

        return result != null ? result : BigDecimal.ZERO;
    }

    private BigDecimal getOperationalBurn(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
        WITH expenses AS (
            SELECT
                COALESCE(SUM(total), 0) AS total_expenses,
                COUNT(DISTINCT date_key) AS active_days
            FROM fact_bill
            WHERE company_key = ?
              AND date_key BETWEEN ? AND ?
        )
        SELECT
            CASE
                WHEN active_days = 0 THEN 0
                ELSE ROUND(total_expenses / active_days, 2)
            END
        FROM expenses
        """;

        BigDecimal result = jdbcTemplate.queryForObject(
                sql,
                BigDecimal.class,
                companyKey,
                startDateKey,
                endDateKey
        );

        return result != null ? result : BigDecimal.ZERO;
    }
    public List<OverviewExecutiveLedgerItem> getExecutiveLedger(
            Integer companyKey,
            Integer startDateKey,
            Integer endDateKey
    ) {
        String sql = """
        WITH periods AS (
            SELECT
                d.year,
                d.month,
                MIN(d.full_date) AS period_date,
                TO_CHAR(MIN(d.full_date), 'Mon YYYY') AS period
            FROM dim_date d
            WHERE d.date_key BETWEEN ? AND ?
            GROUP BY d.year, d.month
        ),

        revenue_by_period AS (
            SELECT
                d.year,
                d.month,
                COALESCE(SUM(fi.total), 0) AS revenue
            FROM dim_date d
            LEFT JOIN fact_invoice fi
                ON fi.date_key = d.date_key
               AND fi.company_key = ?
            WHERE d.date_key BETWEEN ? AND ?
            GROUP BY d.year, d.month
        ),

        expenses_by_period AS (
            SELECT
                d.year,
                d.month,
                COALESCE(SUM(fb.total), 0) AS expenses
            FROM dim_date d
            LEFT JOIN fact_bill fb
                ON fb.date_key = d.date_key
               AND fb.company_key = ?
            WHERE d.date_key BETWEEN ? AND ?
            GROUP BY d.year, d.month
        ),

        attendance_by_period AS (
            SELECT
                d.year,
                d.month,
                CASE
                    WHEN COALESCE(SUM(fa.scheduled_shift_count), 0) = 0 THEN 0
                    ELSE ROUND(
                        COALESCE(SUM(fa.present_shift_count), 0) * 100.0
                        / COALESCE(SUM(fa.scheduled_shift_count), 0),
                        2
                    )
                END AS presence_rate
            FROM dim_date d
            LEFT JOIN fact_attendance_shift fa
                ON fa.date_key = d.date_key
               AND fa.company_key = ?
            WHERE d.date_key BETWEEN ? AND ?
            GROUP BY d.year, d.month
        ),

        customers_by_period AS (
            SELECT
                d.year,
                d.month,
                COUNT(DISTINCT fs.customer_key) AS customers
            FROM dim_date d
            LEFT JOIN fact_sales_financials fs
                ON fs.date_key = d.date_key
               AND fs.company_key = ?
            WHERE d.date_key BETWEEN ? AND ?
            GROUP BY d.year, d.month
        ),

        deals_won_by_period AS (
            SELECT
                d.year,
                d.month,
                COALESCE(SUM(fd.deal_count), 0) AS deals_won
            FROM fact_deal fd
            JOIN dim_workstatus ws
                ON ws.workstatus_key = fd.workstatus_key
            JOIN dim_date d
                ON d.date_key = fd.close_date_key
            WHERE fd.company_key = ?
              AND d.date_key BETWEEN ? AND ?
              AND COALESCE(fd.is_archived, false) = false
              AND ws.status_label = 'Win'
            GROUP BY d.year, d.month
        ),

        pipeline_snapshot AS (
            SELECT
                COALESCE(SUM(fd.deal_value), 0) AS pipeline
            FROM fact_deal fd
            WHERE fd.company_key = ?
              AND COALESCE(fd.is_archived, false) = false
        ),

        employee_snapshot AS (
            SELECT
                COUNT(DISTINCT u.user_id) AS employees
            FROM dim_user u
            WHERE u.company_key = ?
              AND u.is_current = true
              AND COALESCE(u.active, true) = true
        )

        SELECT
            p.period,
            COALESCE(r.revenue, 0) AS revenue,
            COALESCE(e.expenses, 0) AS expenses,
            COALESCE(r.revenue, 0) - COALESCE(e.expenses, 0) AS net_profit,
            COALESCE(dw.deals_won, 0) AS deals_won,
            ps.pipeline,
            es.employees,
            COALESCE(a.presence_rate, 0) AS presence_rate,
            COALESCE(c.customers, 0) AS customers

        FROM periods p
        LEFT JOIN revenue_by_period r
            ON r.year = p.year AND r.month = p.month
        LEFT JOIN expenses_by_period e
            ON e.year = p.year AND e.month = p.month
        LEFT JOIN attendance_by_period a
            ON a.year = p.year AND a.month = p.month
        LEFT JOIN customers_by_period c
            ON c.year = p.year AND c.month = p.month
        LEFT JOIN deals_won_by_period dw
            ON dw.year = p.year AND dw.month = p.month
        CROSS JOIN pipeline_snapshot ps
        CROSS JOIN employee_snapshot es

        ORDER BY p.period_date DESC
        LIMIT 6
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> OverviewExecutiveLedgerItem.builder()
                        .period(rs.getString("period"))
                        .revenue(rs.getBigDecimal("revenue"))
                        .expenses(rs.getBigDecimal("expenses"))
                        .netProfit(rs.getBigDecimal("net_profit"))
                        .dealsWon(rs.getLong("deals_won"))
                        .pipeline(rs.getBigDecimal("pipeline"))
                        .employees(rs.getLong("employees"))
                        .presenceRate(rs.getBigDecimal("presence_rate"))
                        .customers(rs.getLong("customers"))
                        .build(),

                startDateKey,
                endDateKey,

                companyKey,
                startDateKey,
                endDateKey,

                companyKey,
                startDateKey,
                endDateKey,

                companyKey,
                startDateKey,
                endDateKey,

                companyKey,
                startDateKey,
                endDateKey,

                companyKey,
                startDateKey,
                endDateKey,

                companyKey,
                companyKey
        );
    }
    public String getCompanyCurrency(Integer companyKey) {
        String sql = """
        SELECT COALESCE(currency, 'USD')
        FROM dim_company
        WHERE company_key = ?
          AND is_current = true
        LIMIT 1
        """;

        String result = jdbcTemplate.queryForObject(
                sql,
                String.class,
                companyKey
        );

        return result != null ? result : "USD";
    }
}