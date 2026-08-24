package dynamicUi.demo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PermissionPatternMatcher}.
 *
 * Covers the four pattern shapes from the Security & Authorization
 * Proposal: "*", "module.*", "*.action" and exact match.
 */
class PermissionPatternMatcherTest {

    @Test
    @DisplayName("wildcard '*' matches any permission")
    void wildcardMatchesAnything() {
        assertThat(PermissionPatternMatcher.matches("*", "gate.checkin")).isTrue();
        assertThat(PermissionPatternMatcher.matches("*", "anything.at.all")).isTrue();
    }

    @Test
    @DisplayName("prefix pattern 'gate.*' matches nested and exact-prefix permissions")
    void prefixPatternMatchesNested() {
        assertThat(PermissionPatternMatcher.matches("gate.*", "gate.checkin")).isTrue();
        assertThat(PermissionPatternMatcher.matches("gate.*", "gate.checkin.create")).isTrue();
        // exact "gate" (prefix without trailing dot) is also treated as a match
        assertThat(PermissionPatternMatcher.matches("gate.*", "gate")).isTrue();
    }

    @Test
    @DisplayName("prefix pattern 'gate.*' does not match a different module")
    void prefixPatternRejectsOtherModules() {
        assertThat(PermissionPatternMatcher.matches("gate.*", "dock.assignment")).isFalse();
        assertThat(PermissionPatternMatcher.matches("gate.*", "gateway.checkin")).isFalse();
    }

    @Test
    @DisplayName("suffix pattern '*.view' matches anything ending in .view")
    void suffixPatternMatchesEnding() {
        assertThat(PermissionPatternMatcher.matches("*.view", "gate.checkin.view")).isTrue();
        assertThat(PermissionPatternMatcher.matches("*.view", "dashboard.view")).isTrue();
    }

    @Test
    @DisplayName("suffix pattern '*.view' rejects a different suffix")
    void suffixPatternRejectsOtherSuffix() {
        assertThat(PermissionPatternMatcher.matches("*.view", "gate.checkin.edit")).isFalse();
    }

    @Test
    @DisplayName("exact pattern matches only the same permission, case-insensitively")
    void exactPatternMatchesCaseInsensitive() {
        assertThat(PermissionPatternMatcher.matches("gate.checkin", "gate.checkin")).isTrue();
        assertThat(PermissionPatternMatcher.matches("GATE.CHECKIN", "gate.checkin")).isTrue();
        assertThat(PermissionPatternMatcher.matches("gate.checkin", "gate.checkout")).isFalse();
    }

    @Test
    @DisplayName("pattern and permission are trimmed before comparison")
    void trimsWhitespace() {
        assertThat(PermissionPatternMatcher.matches("  gate.checkin  ", "gate.checkin")).isTrue();
        assertThat(PermissionPatternMatcher.matches("gate.*", "  gate.checkin  ")).isTrue();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("null pattern never matches")
    void nullPatternNeverMatches(String pattern) {
        assertThat(PermissionPatternMatcher.matches(pattern, "gate.checkin")).isFalse();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("null permission never matches")
    void nullPermissionNeverMatches(String permission) {
        assertThat(PermissionPatternMatcher.matches("*", permission)).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "gate.*, gate.checkin, true",
            "gate.*, dock.assignment, false",
            "*.view, dashboard.view, true",
            "*.view, dashboard.edit, false",
            "dashboard, dashboard, true",
            "dashboard, other, false",
    })
    @DisplayName("pattern/permission combinations resolve as expected")
    void combinations(String pattern, String permission, boolean expected) {
        assertThat(PermissionPatternMatcher.matches(pattern, permission)).isEqualTo(expected);
    }
}
