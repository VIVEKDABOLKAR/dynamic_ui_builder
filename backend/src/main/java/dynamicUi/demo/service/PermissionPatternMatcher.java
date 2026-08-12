package dynamicUi.demo.service;

/**
 * Matches a required permission (e.g. "gate.checkin") against a role's
 * granted pattern (e.g. "*", "gate.*", "*.view", or an exact permission).
 *
 * Supported pattern shapes (per the Security & Authorization Proposal,
 * section 6):
 *   "*"          -> matches everything
 *   "gate.*"     -> matches "gate.checkin", "gate.checkin.create", etc.
 *   "*.view"     -> matches anything ending in ".view"
 *   "gate.checkin" -> exact match only
 */
public final class PermissionPatternMatcher {

    private PermissionPatternMatcher() {
    }

    public static boolean matches(String pattern, String permission) {
        if (pattern == null || permission == null) {
            return false;
        }

        String trimmedPattern = pattern.trim();
        String trimmedPermission = permission.trim();

        if (trimmedPattern.equals("*")) {
            return true;
        }

        if (trimmedPattern.endsWith(".*")) {
            String prefix = trimmedPattern.substring(0, trimmedPattern.length() - 1); // keep trailing dot
            return trimmedPermission.startsWith(prefix)
                    || trimmedPermission.equals(prefix.substring(0, prefix.length() - 1));
        }

        if (trimmedPattern.startsWith("*.")) {
            String suffix = trimmedPattern.substring(1); // keep leading dot
            return trimmedPermission.endsWith(suffix);
        }

        return trimmedPattern.equalsIgnoreCase(trimmedPermission);
    }
}
