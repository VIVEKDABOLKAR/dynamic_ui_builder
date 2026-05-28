package dynamicUi.demo.service;

import dynamicUi.demo.dto.UIComponentDTO;
import dynamicUi.demo.dto.UILookupDTO;
import dynamicUi.demo.entity.UIComponent;
import dynamicUi.demo.entity.UILookup;
import dynamicUi.demo.entity.UILookupMaster;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.repoistory.UiComponentRepository;
import dynamicUi.demo.repoistory.UILookupMasterRepository;
import dynamicUi.demo.repoistory.UILookupRepository;
import dynamicUi.demo.repoistory.UIPageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UiComponentServiceImp implements UiComponentService {

    private final UiComponentRepository uiComponentRepository;
    private final UIPageRepository uiPageRepository;
    private final UILookupRepository uiLookupRepository;
    private final UILookupMasterRepository uiLookupMasterRepository;
    private final UIPageJsonService uiPageJsonService;

    public UiComponentServiceImp(
            UiComponentRepository uiComponentRepository,
            UIPageRepository uiPageRepository,
            UILookupRepository uiLookupRepository,
            UILookupMasterRepository uiLookupMasterRepository,
            UIPageJsonService uiPageJsonService
    ) {
        this.uiComponentRepository = uiComponentRepository;
        this.uiPageRepository = uiPageRepository;
        this.uiLookupRepository = uiLookupRepository;
        this.uiLookupMasterRepository = uiLookupMasterRepository;
        this.uiPageJsonService = uiPageJsonService;
    }

    @Override
    public UIComponentDTO createComponent(UIComponentDTO dto) {

        UIPage uiPage = uiPageRepository.findByPageCode(dto.getPageCode())
                .orElseThrow(() -> new RuntimeException("Page not found"));

        UIComponent component = UIComponent.builder()
                .uiPage(uiPage)
                .componentName(dto.getComponentName())
                .componentType(dto.getComponentType())
                .labelName(dto.getLabelName())
                .placeholder(dto.getPlaceholder())
                .properties(dto.getProperties())
                .sequenceNo(dto.getSequenceNo())
                .isRequired(dto.getIsRequired())
                .isVisible(dto.getIsVisible())
                .isDisabled(dto.getIsDisabled())
                .isActive(Boolean.TRUE.equals(dto.getIsActive()) || dto.getIsActive() == null)
                .build();

        UIComponent saved = uiComponentRepository.save(component);

        if (dto.getLookupValues() != null && !dto.getLookupValues().isEmpty()) {
            // admin should supply lookupValues; we create or associate a lookup master and link it to component
            saveLookupValues(saved, dto.getLookupValues(), null);
        }

        // NEW: Update JSON schema
        List<UILookup> lookups = saved.getUiLookupMaster() != null
            ? uiLookupRepository.findByUiLookupMaster_Id(saved.getUiLookupMaster().getId())
            : List.of();
        uiPageJsonService.addComponentToJson(dto.getPageCode(), saved, lookups);


        dto.setId(saved.getId());

        return dto;
    }

    @Override
    public List<UIComponentDTO> getComponentsByPage(String pageCode) {

        List<UIComponent> components =
            uiComponentRepository.findByUiPage_PageCodeAndIsActiveTrueOrderBySequenceNo(pageCode);

        return components.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UIComponentDTO getComponentById(Long id) {

        UIComponent component = uiComponentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Component not found"));

        return mapToDto(component);
    }

    @Override
    public UIComponentDTO updateComponent(Long id, UIComponentDTO dto) {

        UIComponent component = uiComponentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Component not found"));

        component.setComponentName(dto.getComponentName());
        component.setComponentType(dto.getComponentType());
        component.setLabelName(dto.getLabelName());
        component.setPlaceholder(dto.getPlaceholder());
        component.setProperties(dto.getProperties());
        component.setSequenceNo(dto.getSequenceNo());
        component.setIsRequired(dto.getIsRequired());
        component.setIsVisible(dto.getIsVisible());
        component.setIsDisabled(dto.getIsDisabled());
        component.setIsActive(dto.getIsActive() == null ? component.getIsActive() : dto.getIsActive());
        // lookup master id removed from DTO; relation handled via UILookupMaster association

        UIComponent updated = uiComponentRepository.save(component);

        if (dto.getLookupValues() != null) {
            if (updated.getUiLookupMaster() != null) {
                uiLookupRepository.deleteByUiLookupMaster_Id(updated.getUiLookupMaster().getId());
            }
            if (!dto.getLookupValues().isEmpty()) {
                saveLookupValues(updated, dto.getLookupValues(), null);
            }
        }


        // NEW: Update JSON schema
        List<UILookup> lookups = updated.getUiLookupMaster() != null
            ? uiLookupRepository.findByUiLookupMaster_Id(updated.getUiLookupMaster().getId())
            : List.of();
        uiPageJsonService.updateComponentInJson(dto.getPageCode(), updated, lookups);


        return mapToDto(updated);
    }

    @Override
    public void deleteComponent(Long id) {
        UIComponent component = uiComponentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Component not found"));

        // Soft delete: mark inactive instead of hard delete.
        String pageCode = component.getUiPage().getPageCode();
        component.setIsActive(false);
        component.setIsVisible(false);
        uiComponentRepository.save(component);

        // NEW: Remove from JSON schema
        uiPageJsonService.removeComponentFromJson(pageCode, id);
    }

    private UIComponentDTO mapToDto(UIComponent component) {

        return UIComponentDTO.builder()
                .id(component.getId())
                .pageCode(component.getUiPage().getPageCode())
                .componentName(component.getComponentName())
                .componentType(component.getComponentType())
                .labelName(component.getLabelName())
                .placeholder(component.getPlaceholder())
                .properties(component.getProperties())
                .sequenceNo(component.getSequenceNo())
                .isRequired(component.getIsRequired())
                .isVisible(component.getIsVisible())
                .isDisabled(component.getIsDisabled())
                .isActive(component.getIsActive())
                
                .lookupValues(component.getUiLookupMaster() != null
                    ? uiLookupRepository.findByUiLookupMaster_Id(component.getUiLookupMaster().getId()).stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList())
                    : List.of())
                .build();
    }

    private UILookupDTO mapToDto(UILookup lookup) {
        return UILookupDTO.builder()
                .id(lookup.getId())
                .lookupType(lookup.getLookupType())
                .lookupValue(lookup.getLookupValue())
                .displayValue(lookup.getDisplayValue())
                .sequenceNo(lookup.getSequenceNo())
                .isActive(lookup.getIsActive())
                .build();
    }

    private void saveLookupValues(UIComponent component, List<UILookupDTO> lookupValues, Long lookupMasterId) {
        if (lookupValues == null || lookupValues.isEmpty()) {
            return;
        }

        UILookupMaster lookupMaster;
        if (lookupMasterId != null) {
            lookupMaster = uiLookupMasterRepository.findById(lookupMasterId)
                .orElseThrow(() -> new RuntimeException("Lookup master not found"));
            // ensure master references this component
            lookupMaster.setComponentId(component.getId());
            lookupMaster = uiLookupMasterRepository.save(lookupMaster);
        } else if (component.getUiLookupMaster() != null) {
            lookupMaster = component.getUiLookupMaster();
            lookupMaster.setComponentId(component.getId());
            lookupMaster = uiLookupMasterRepository.save(lookupMaster);
        } else {
            lookupMaster = new UILookupMaster();
            lookupMaster.setLookupName(component.getComponentName() + "_lookup");
            lookupMaster.setActive(true);
            lookupMaster.setComponentId(component.getId());
            lookupMaster = uiLookupMasterRepository.save(lookupMaster);
            component.setUiLookupMaster(lookupMaster);
            uiComponentRepository.save(component);
        }

        UILookupMaster finalLookupMaster = lookupMaster;
        List<UILookup> lookups = lookupValues.stream()
            .filter(Objects::nonNull)
            .map(dto -> UILookup.builder()
                .lookupType(dto.getLookupType())
                .lookupValue(dto.getLookupValue())
                .displayValue(dto.getDisplayValue())
                .sequenceNo(dto.getSequenceNo())
                .isActive(dto.getIsActive())
                .uiLookupMaster(finalLookupMaster)
                .build())
            .collect(Collectors.toList());

        uiLookupRepository.saveAll(lookups);
    }
}
