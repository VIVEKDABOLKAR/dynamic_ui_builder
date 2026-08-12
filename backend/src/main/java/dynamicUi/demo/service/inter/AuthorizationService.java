package dynamicUi.demo.service.inter;

import dynamicUi.demo.entity.UIPage;

/**
 * Single centralized place for dynamic-page permission checks
 * (Security & Authorization Proposal, section 11).
 *
 * Scope: this is deliberately limited to the JSON-driven dynamic pages
 * (page open / route resolve). It does not touch the existing hardcoded
 * business APIs (/api/gate-checkins, /api/job-orders, etc.) or facility
 * scoping (UserFacilityAccess / FacilityRouteAccess), which remain as-is.
 */
public interface AuthorizationService {

    /**
     * @return true if the currently authenticated user's role grants
     * the given permission string (e.g. "gate.checkin").
     */
    boolean hasPermission(String permission);

    /**
     * Throws 403 if the currently authenticated user's role does not
     * grant the given permission.
     */
    void requirePermission(String permission);

    /**
     * Resolves the permission string for a page: uses UIPage.permissionCode
     * if explicitly set, otherwise derives it from the page code
     * (GATE_CHECKIN -> gate.checkin).
     */
    String resolvePagePermission(UIPage page);

    /**
     * Convenience: resolves the page's permission and enforces it. No-op
     * (allowed) if the page has no resolvable permission.
     */
    void requirePagePermission(UIPage page);
}
