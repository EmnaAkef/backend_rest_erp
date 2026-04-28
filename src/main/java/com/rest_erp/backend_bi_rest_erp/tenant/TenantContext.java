package com.rest_erp.backend_bi_rest_erp.tenant;

public class TenantContext {

    private static final ThreadLocal<Long> CURRENT_COMPANY_ID = new ThreadLocal<>();

    private TenantContext() {

    }
    public static Integer getCompanyKey() {
        return 1;
    }

    public static void setCompanyId(Long companyId) {
        CURRENT_COMPANY_ID.set(companyId);
    }

    public static Long getCompanyId() {
        return CURRENT_COMPANY_ID.get();
    }

    public static void clear() {
        CURRENT_COMPANY_ID.remove();
    }
}
