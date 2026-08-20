package dynamicUi.demo.service;

import dynamicUi.demo.entity.UIComponentAction;
import dynamicUi.demo.repoistory.UIComponentActionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UIComponentActionServiceImp}.
 */
@ExtendWith(MockitoExtension.class)
class UIComponentActionServiceImpTest {

    @Mock
    private UIComponentActionRepository uiComponentActionRepository;

    @InjectMocks
    private UIComponentActionServiceImp service;

    // ── create ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("create defaults conditionExpr to 'true' and sequenceNo to 0 when not provided")
    void createAppliesDefaultsWhenMissing() {
        UIComponentAction action = UIComponentAction.builder()
                .componentId(1L).pageCode("GATE_CHECK_IN").event("onClick").actionRef("saveHome")
                .conditionExpr(null)
                .sequenceNo(null)
                .build();
        when(uiComponentActionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UIComponentAction result = service.create(action);

        assertThat(result.getConditionExpr()).isEqualTo("true");
        assertThat(result.getSequenceNo()).isEqualTo(0);
    }

    @Test
    @DisplayName("create defaults blank conditionExpr to 'true' too")
    void createAppliesDefaultForBlankConditionExpr() {
        UIComponentAction action = UIComponentAction.builder()
                .componentId(1L).pageCode("GATE_CHECK_IN").event("onClick").actionRef("saveHome")
                .conditionExpr("   ")
                .sequenceNo(3)
                .build();
        when(uiComponentActionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UIComponentAction result = service.create(action);

        assertThat(result.getConditionExpr()).isEqualTo("true");
        assertThat(result.getSequenceNo()).isEqualTo(3); // provided value preserved
    }

    @Test
    @DisplayName("create preserves explicitly provided conditionExpr and sequenceNo")
    void createPreservesProvidedValues() {
        UIComponentAction action = UIComponentAction.builder()
                .componentId(1L).pageCode("GATE_CHECK_IN").event("onClick").actionRef("saveHome")
                .conditionExpr("form.isValid == true")
                .sequenceNo(5)
                .build();
        when(uiComponentActionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UIComponentAction result = service.create(action);

        assertThat(result.getConditionExpr()).isEqualTo("form.isValid == true");
        assertThat(result.getSequenceNo()).isEqualTo(5);
    }

    // ── update ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("update overwrites event/actionRef/conditionExpr/sequenceNo on the existing row")
    void updateOverwritesFields() {
        UIComponentAction existing = UIComponentAction.builder()
                .id(1L).componentId(1L).pageCode("GATE_CHECK_IN")
                .event("onClick").actionRef("old").conditionExpr("true").sequenceNo(0)
                .build();
        UIComponentAction payload = UIComponentAction.builder()
                .event("onChange").actionRef("newRef").conditionExpr("form.dirty").sequenceNo(2)
                .build();

        when(uiComponentActionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(uiComponentActionRepository.save(existing)).thenReturn(existing);

        UIComponentAction result = service.update(1L, payload);

        assertThat(result.getEvent()).isEqualTo("onChange");
        assertThat(result.getActionRef()).isEqualTo("newRef");
        assertThat(result.getConditionExpr()).isEqualTo("form.dirty");
        assertThat(result.getSequenceNo()).isEqualTo(2);
    }

    @Test
    @DisplayName("update defaults conditionExpr and sequenceNo when payload leaves them blank/null")
    void updateAppliesDefaultsWhenPayloadFieldsMissing() {
        UIComponentAction existing = UIComponentAction.builder()
                .id(1L).componentId(1L).pageCode("GATE_CHECK_IN")
                .event("onClick").actionRef("old").conditionExpr("form.dirty").sequenceNo(5)
                .build();
        UIComponentAction payload = UIComponentAction.builder()
                .event("onChange").actionRef("newRef").conditionExpr(null).sequenceNo(null)
                .build();

        when(uiComponentActionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(uiComponentActionRepository.save(existing)).thenReturn(existing);

        UIComponentAction result = service.update(1L, payload);

        assertThat(result.getConditionExpr()).isEqualTo("true");
        assertThat(result.getSequenceNo()).isEqualTo(0);
    }

    @Test
    @DisplayName("update throws when the action id does not exist")
    void updateThrowsWhenNotFound() {
        when(uiComponentActionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, UIComponentAction.builder().build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("UIComponentAction not found: 99");

        verify(uiComponentActionRepository, never()).save(any());
    }

    // ── getById ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById returns the action when found")
    void getByIdReturnsWhenFound() {
        UIComponentAction action = UIComponentAction.builder().id(1L).build();
        when(uiComponentActionRepository.findById(1L)).thenReturn(Optional.of(action));

        assertThat(service.getById(1L)).isEqualTo(action);
    }

    @Test
    @DisplayName("getById throws when not found")
    void getByIdThrowsWhenNotFound() {
        when(uiComponentActionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("UIComponentAction not found: 99");
    }

    // ── getByComponentId / getByPageCode ─────────────────────────────────

    @Test
    @DisplayName("getByComponentId delegates to the repository, ordered by sequence")
    void getByComponentIdDelegates() {
        List<UIComponentAction> actions = List.of(UIComponentAction.builder().id(1L).componentId(5L).build());
        when(uiComponentActionRepository.findByComponentIdOrderBySequenceNoAsc(5L)).thenReturn(actions);

        assertThat(service.getByComponentId(5L)).isEqualTo(actions);
    }

    @Test
    @DisplayName("getByPageCode delegates to the repository, ordered by component then sequence")
    void getByPageCodeDelegates() {
        List<UIComponentAction> actions = List.of(UIComponentAction.builder().id(1L).pageCode("GATE_CHECK_IN").build());
        when(uiComponentActionRepository.findByPageCodeOrderByComponentIdAscSequenceNoAsc("GATE_CHECK_IN"))
                .thenReturn(actions);

        assertThat(service.getByPageCode("GATE_CHECK_IN")).isEqualTo(actions);
    }

    // ── delete ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete removes the action when it exists")
    void deleteRemovesWhenExists() {
        when(uiComponentActionRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(uiComponentActionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws when the action does not exist")
    void deleteThrowsWhenNotFound() {
        when(uiComponentActionRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("UIComponentAction not found: 99");

        verify(uiComponentActionRepository, never()).deleteById(any());
    }

    // ── deleteByComponentId ──────────────────────────────────────────────

    @Test
    @DisplayName("deleteByComponentId delegates to the repository unconditionally")
    void deleteByComponentIdDelegates() {
        service.deleteByComponentId(5L);

        verify(uiComponentActionRepository).deleteByComponentId(5L);
    }
}