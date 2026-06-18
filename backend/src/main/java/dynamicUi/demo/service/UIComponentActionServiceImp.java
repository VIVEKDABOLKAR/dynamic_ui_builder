package dynamicUi.demo.service;

import dynamicUi.demo.entity.UIComponentAction;
import dynamicUi.demo.repoistory.UIComponentActionRepository;
import dynamicUi.demo.service.inter.UIComponentActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UIComponentActionServiceImp implements UIComponentActionService {

    private final UIComponentActionRepository uiComponentActionRepository;

    @Override
    public UIComponentAction create(UIComponentAction action) {
        // Default condition to "true" if not provided
        if (action.getConditionExpr() == null || action.getConditionExpr().isBlank()) {
            action.setConditionExpr("true");
        }
        // Default sequence to 0 if not provided
        if (action.getSequenceNo() == null) {
            action.setSequenceNo(0);
        }
        return uiComponentActionRepository.save(action);
    }

    @Override
    public UIComponentAction update(Long id, UIComponentAction action) {
        UIComponentAction existing = uiComponentActionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UIComponentAction not found: " + id));

        existing.setEvent(action.getEvent());
        existing.setActionRef(action.getActionRef());
        existing.setConditionExpr(
                action.getConditionExpr() != null && !action.getConditionExpr().isBlank()
                        ? action.getConditionExpr()
                        : "true");
        existing.setSequenceNo(action.getSequenceNo() != null ? action.getSequenceNo() : 0);

        return uiComponentActionRepository.save(existing);
    }

    @Override
    public UIComponentAction getById(Long id) {
        return uiComponentActionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UIComponentAction not found: " + id));
    }

    @Override
    public List<UIComponentAction> getByComponentId(Long componentId) {
        return uiComponentActionRepository.findByComponentIdOrderBySequenceNoAsc(componentId);
    }

    @Override
    public List<UIComponentAction> getByPageCode(String pageCode) {
        return uiComponentActionRepository.findByPageCodeOrderByComponentIdAscSequenceNoAsc(pageCode);
    }

    @Override
    public void delete(Long id) {
        if (!uiComponentActionRepository.existsById(id)) {
            throw new RuntimeException("UIComponentAction not found: " + id);
        }
        uiComponentActionRepository.deleteById(id);
    }

    @Override
    public void deleteByComponentId(Long componentId) {
        uiComponentActionRepository.deleteByComponentId(componentId);
    }
}