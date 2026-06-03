package dynamicUi.demo.service;

import dynamicUi.demo.entity.UIPageAction;
import dynamicUi.demo.repoistory.UIPageActionRepository;
import dynamicUi.demo.service.inter.UIPageActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UIPageActionServiceImp implements UIPageActionService {

    private final UIPageActionRepository repository;

    @Override
    public UIPageAction create(UIPageAction uiPageAction) {
        return repository.save(uiPageAction);
    }

    @Override
    public UIPageAction update(Long id, UIPageAction uiPageAction) {

        UIPageAction existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("UIPageAction not found with id: " + id));

        existing.setUiPage(uiPageAction.getUiPage());
        existing.setActionName(uiPageAction.getActionName());
        existing.setActionType(uiPageAction.getActionType());
        existing.setProperties(uiPageAction.getProperties());

        return repository.save(existing);
    }

    @Override
    public UIPageAction getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("UIPageAction not found with id: " + id));
    }

    @Override
    public List<UIPageAction> getAll() {
        return repository.findAll();
    }

    @Override
    public List<UIPageAction> getByPageId(Long pageId) {
        return repository.findByUiPageId(pageId);
    }

    @Override
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("UIPageAction not found with id: " + id);
        }

        repository.deleteById(id);
    }
}
