package com.rest_erp.backend_bi_rest_erp.repository.hr;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class HrKpiRepository {

    @PersistenceContext

    private EntityManager entityManager;

    public Long getTotalEmployees(Integer companyKey) {
        String sql = """
            SELECT COUNT(DISTINCT u.user_id)
            FROM dim_user u
            WHERE u.company_key = :companyKey
              AND u.type = 'EMPLOYEE'
              AND u.is_current = true
        """;

        Object result = entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public Long getActiveEmployees(Integer companyKey) {
        String sql = """
            SELECT COUNT(DISTINCT u.user_id)
            FROM dim_user u
            WHERE u.company_key = :companyKey
              AND u.type = 'EMPLOYEE'
              AND u.active = true
              AND u.is_current = true
        """;

        Object result = entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public Long getOnboardingEmployees(Integer companyKey, LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT f.user_key)
            FROM fact_employee_hr f
            JOIN dim_date d ON d.date_key = f.date_key
            WHERE f.company_key = :companyKey
              AND f.onboarding_flag = 1
        """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public Long getOffboardingEmployees(Integer companyKey, LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(DISTINCT f.user_key)
            FROM fact_employee_hr f
            JOIN dim_date d ON d.date_key = f.date_key
            WHERE f.company_key = :companyKey
              AND f.offboarding_flag = 1
        """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public BigDecimal getAverageTenureDays(Integer companyKey) {
        String sql = """
            SELECT COALESCE(AVG(f.tenure_days), 0)
            FROM fact_employee_hr f
            WHERE f.company_key = :companyKey
              AND f.is_employee_flag = 1
              AND f.is_active_flag = 1
        """;

        Object result = entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public BigDecimal getPresenceRate(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT 
            CASE 
                WHEN COALESCE(SUM(f.scheduled_shift_count), 0) = 0 THEN 0
                ELSE ROUND(
                    COALESCE(SUM(f.present_shift_count), 0) * 100.0 
                    / COALESCE(SUM(f.scheduled_shift_count), 0),
                    2
                )
            END
        FROM fact_attendance_shift f
        JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public BigDecimal getAbsenceRate(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT 
            CASE 
                WHEN COALESCE(SUM(f.scheduled_shift_count), 0) = 0 THEN 0
                ELSE ROUND(
                    COALESCE(SUM(f.absent_shift_count), 0) * 100.0 
                    / COALESCE(SUM(f.scheduled_shift_count), 0),
                    2
                )
            END
        FROM fact_attendance_shift f
        JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public Long getLateCheckins(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.late_checkin_count), 0)
        FROM fact_attendance_shift f
        JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public BigDecimal getOvertimeHours(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.overtime_hours), 0)
        FROM fact_attendance_shift f
        JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public BigDecimal getTotalPayroll(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.final_salary), 0)
        FROM fact_employee_hr f
        JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
          AND f.is_paid_flag = 1
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public BigDecimal getAverageSalary(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(AVG(f.final_salary), 0)
        FROM fact_employee_hr f
        JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
          AND f.final_salary IS NOT NULL
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }

    public List<Object[]> getHeadcountTrend(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT 
            CONCAT(d.month_name, ' ', d.year) AS period_label,
            SUM(f.employee_count) AS total_headcount
        FROM fact_employee_hr f
        JOIN dim_date d 
            ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        sql.append("""
        GROUP BY d.year, d.month, d.month_name
        ORDER BY d.year, d.month
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        return query.getResultList();
    }

    public java.util.List<Object[]> getAttendanceTrend(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            TO_CHAR(DATE_TRUNC('month', d.full_date), 'Mon YYYY') AS label,
            CASE
                WHEN SUM(f.scheduled_shift_count) = 0 THEN 0
                ELSE SUM(f.present_shift_count) * 100.0 / SUM(f.scheduled_shift_count)
            END AS presence_rate,
            CASE
                WHEN SUM(f.scheduled_shift_count) = 0 THEN 0
                ELSE SUM(f.absent_shift_count) * 100.0 / SUM(f.scheduled_shift_count)
            END AS absence_rate
        FROM fact_attendance_shift f
        JOIN dim_date d ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        sql.append("""
        GROUP BY DATE_TRUNC('month', d.full_date)
        ORDER BY DATE_TRUNC('month', d.full_date)
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        return query.getResultList();
    }

    public java.util.List<Object[]> getTenureDistribution(Integer companyKey) {

        String sql = """
        SELECT
            CASE
                WHEN f.tenure_days < 365 THEN '0-1 year'
                WHEN f.tenure_days < 1095 THEN '1-3 years'
                WHEN f.tenure_days < 1825 THEN '3-5 years'
                ELSE '5+ years'
            END AS tenure_group,
            COUNT(DISTINCT f.user_key) AS employee_count
        FROM fact_employee_hr f
        WHERE f.company_key = :companyKey
          AND f.tenure_days IS NOT NULL
        GROUP BY tenure_group
        ORDER BY 
            CASE
                WHEN CASE
                    WHEN f.tenure_days < 365 THEN '0-1 year'
                    WHEN f.tenure_days < 1095 THEN '1-3 years'
                    WHEN f.tenure_days < 1825 THEN '3-5 years'
                    ELSE '5+ years'
                END = '0-1 year' THEN 1
                WHEN CASE
                    WHEN f.tenure_days < 365 THEN '0-1 year'
                    WHEN f.tenure_days < 1095 THEN '1-3 years'
                    WHEN f.tenure_days < 1825 THEN '3-5 years'
                    ELSE '5+ years'
                END = '1-3 years' THEN 2
                WHEN CASE
                    WHEN f.tenure_days < 365 THEN '0-1 year'
                    WHEN f.tenure_days < 1095 THEN '1-3 years'
                    WHEN f.tenure_days < 1825 THEN '3-5 years'
                    ELSE '5+ years'
                END = '3-5 years' THEN 3
                ELSE 4
            END
    """;

        return entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getResultList();
    }

    public java.util.List<Object[]> getEmployeesByDepartment(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT 
            COALESCE(d.department_name, 'Unknown Department') AS department,
            COUNT(DISTINCT u.user_id) AS employee_count
        FROM dim_user u
        LEFT JOIN dim_department d 
            ON d.department_key = u.department_key
        WHERE u.company_key = :companyKey
          AND UPPER(u.type) = 'EMPLOYEE'
    """);

        if (startDate != null && endDate != null) {
            sql.append("""
            AND u.effective_from::date <= :endDate
            AND (
                u.effective_to IS NULL 
                OR u.effective_to::date >= :startDate
            )
        """);
        } else {
            sql.append("""
            AND u.is_current = true
        """);
        }

        sql.append("""
        GROUP BY d.department_name
        ORDER BY employee_count DESC
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }

        return query.getResultList();
    }

    public Long getActiveJobOffers(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.job_offers_count), 0)
        FROM fact_job_offer f
        JOIN dim_date d ON d.date_key = f.posting_date_key
        WHERE f.company_key = :companyKey
          AND f.status IN ('OPEN', 'ACTIVE', 'PUBLISHED')
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public Long getTotalApplications(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.applications_count), 0)
        FROM fact_job_application f
        JOIN dim_date d ON d.date_key = f.submission_date_key
        WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public Long getHiredApplications(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        SELECT COALESCE(SUM(f.applications_count), 0)
        FROM fact_job_application f
        JOIN dim_date d ON d.date_key = f.submission_date_key
        WHERE f.company_key = :companyKey
          AND f.is_hired_flag = 1
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? ((Number) result).longValue() : 0L;
    }

    public BigDecimal getAbsenteeismVolatilityIndex(Integer companyKey, LocalDate startDate, LocalDate endDate) {

        StringBuilder sql = new StringBuilder("""
        WITH monthly_absence AS (
            SELECT
                DATE_TRUNC('month', d.full_date) AS month_date,
                CASE
                    WHEN COALESCE(SUM(f.scheduled_shift_count), 0) = 0 THEN 0
                    ELSE
                        COALESCE(SUM(f.absent_shift_count), 0) * 100.0
                        / COALESCE(SUM(f.scheduled_shift_count), 0)
                END AS absence_rate_pct
            FROM fact_attendance_shift f
            JOIN dim_date d ON d.date_key = f.date_key
            WHERE f.company_key = :companyKey
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        sql.append("""
            GROUP BY DATE_TRUNC('month', d.full_date)
        )
        SELECT COALESCE(VAR_POP(absence_rate_pct), 0)
        FROM monthly_absence
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        Object result = query.getSingleResult();

        return result != null ? new BigDecimal(result.toString()) : BigDecimal.ZERO;
    }
    public java.util.List<Object[]> getSalaryBenchmarking(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            COALESCE(dep.department_name, 'Unknown Department') AS department,

            COALESCE(
                ROUND(
                    SUM(f.final_salary)::numeric 
                    / NULLIF(SUM(f.payroll_count), 0),
                    2
                ),
                0
            ) AS average_salary,

            COALESCE(
                ROUND(
                    MAX(
                        CASE 
                            WHEN f.payroll_count > 0 
                            THEN f.final_salary::numeric / f.payroll_count
                            ELSE f.final_salary::numeric
                        END
                    ),
                    2
                ),
                0
            ) AS maximum_salary

        FROM fact_employee_hr f
        LEFT JOIN dim_department dep 
            ON dep.department_key = f.department_key
        JOIN dim_date d 
            ON d.date_key = f.date_key
        WHERE f.company_key = :companyKey
          AND f.final_salary IS NOT NULL
          AND f.payroll_count IS NOT NULL
          AND f.payroll_count > 0
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        sql.append("""
        GROUP BY dep.department_name
        ORDER BY average_salary DESC
        LIMIT 10
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        return query.getResultList();
    }

    public java.util.List<Object[]> getHiringFunnel(
            Integer companyKey,
            LocalDate startDate,
            LocalDate endDate
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT stage, SUM(count_value) AS total_count
        FROM (
            SELECT
                'Submitted' AS stage,
                COALESCE(SUM(f.applications_count), 0) AS count_value,
                1 AS stage_order
            FROM fact_job_application f
            JOIN dim_date d ON d.date_key = f.submission_date_key
            WHERE f.company_key = :companyKey
              AND UPPER(f.application_status) IN ('SUBMITTED', 'APPLICATIONS')
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        sql.append("""
            UNION ALL

            SELECT
                'Screening' AS stage,
                COALESCE(SUM(f.applications_count), 0) AS count_value,
                2 AS stage_order
            FROM fact_job_application f
            JOIN dim_date d ON d.date_key = f.submission_date_key
            WHERE f.company_key = :companyKey
              AND UPPER(f.application_status) IN ('SCREENING', 'SHORTLISTED')
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        sql.append("""
            UNION ALL

            SELECT
                'Technical' AS stage,
                COALESCE(SUM(f.applications_count), 0) AS count_value,
                3 AS stage_order
            FROM fact_job_application f
            JOIN dim_date d ON d.date_key = f.submission_date_key
            WHERE f.company_key = :companyKey
              AND UPPER(f.application_status) IN ('TECHNICAL', 'TECHNICAL_INTERVIEW', 'INTERVIEW')
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        sql.append("""
            UNION ALL

            SELECT
                'Accepted' AS stage,
                COALESCE(SUM(f.applications_count), 0) AS count_value,
                4 AS stage_order
            FROM fact_job_application f
            JOIN dim_date d ON d.date_key = f.submission_date_key
            WHERE f.company_key = :companyKey
              AND UPPER(f.application_status) IN ('ACCEPTED', 'OFFER', 'OFFER_SENT', 'PROPOSED')
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        sql.append("""
            UNION ALL

            SELECT
                'Hired' AS stage,
                COALESCE(SUM(f.is_hired_flag), 0) AS count_value,
                5 AS stage_order
            FROM fact_job_application f
            JOIN dim_date d ON d.date_key = f.submission_date_key
            WHERE f.company_key = :companyKey
              AND (
                    UPPER(f.application_status) IN ('HIRED')
                    OR f.is_hired_flag = 1
                  )
    """);

        if (startDate != null) {
            sql.append(" AND d.full_date >= :startDate");
        }

        if (endDate != null) {
            sql.append(" AND d.full_date <= :endDate");
        }

        sql.append("""
        ) funnel
        GROUP BY stage, stage_order
        ORDER BY stage_order
    """);

        var query = entityManager.createNativeQuery(sql.toString())
                .setParameter("companyKey", companyKey);

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        return query.getResultList();
    }

    public java.util.List<Object[]> getUpcomingBirthdays(Integer companyKey) {

        String sql = """
        WITH birthdays AS (
            SELECT
                u.user_name AS employee,
                COALESCE(d.department_name, 'Unknown Department') AS department,

                CASE
                    WHEN make_date(
                        EXTRACT(YEAR FROM CURRENT_DATE)::int,
                        EXTRACT(MONTH FROM u.birthdate)::int,
                        EXTRACT(DAY FROM u.birthdate)::int
                    ) >= CURRENT_DATE
                    THEN make_date(
                        EXTRACT(YEAR FROM CURRENT_DATE)::int,
                        EXTRACT(MONTH FROM u.birthdate)::int,
                        EXTRACT(DAY FROM u.birthdate)::int
                    )

                    ELSE make_date(
                        EXTRACT(YEAR FROM CURRENT_DATE)::int + 1,
                        EXTRACT(MONTH FROM u.birthdate)::int,
                        EXTRACT(DAY FROM u.birthdate)::int
                    )
                END AS next_birthday
            FROM dim_user u
            LEFT JOIN dim_department d ON d.department_key = u.department_key
            WHERE u.company_key = :companyKey
              AND u.birthdate IS NOT NULL
              AND u.is_current = true
              AND u.active = true
              AND u.type = 'EMPLOYEE'
        )
        SELECT
            employee,
            department,
            (next_birthday - CURRENT_DATE) AS remaining_days
        FROM birthdays
        WHERE (next_birthday - CURRENT_DATE) BETWEEN 0 AND 30
        ORDER BY remaining_days ASC
        LIMIT 5
    """;

        return entityManager.createNativeQuery(sql)
                .setParameter("companyKey", companyKey)
                .getResultList();
    }
}