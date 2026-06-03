package dynamicUi.demo.service;

import dynamicUi.demo.dto.UIComponentDTO;
import dynamicUi.demo.dto.UIEntityMappingDTO;
import dynamicUi.demo.dto.UILookupDTO;
import dynamicUi.demo.entity.*;
import dynamicUi.demo.repoistory.*;
import dynamicUi.demo.service.inter.UIPageJsonService;
import dynamicUi.demo.service.inter.UiComponentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UiComponentServiceImp implements UiComponentService {

    private static final List<String> ALLOWED_PARENT_TYPES = List.of("card", "layout");

    private final UiComponentRepository uiComponentRepository;
    private final UIPageRepository uiPageRepository;
    private final UILookupRepository uiLookupRepository;
    private final UILookupMasterRepository uiLookupMasterRepository;
    private final UIPageJsonService uiPageJsonService;
    private  final UIEntityMappingRepository uiEntityMappingRepository;

    public UiComponentServiceImp(
            UiComponentRepository uiComponentRepository,
            UIPageRepository uiPageRepository,
            UILookupRepository uiLookupRepository,
            UILookupMasterRepository uiLookupMasterRepository,
            UIPageJsonService uiPageJsonService, UIEntityMappingRepository uiEntityMappingRepository
    ) {
        this.uiComponentRepository = uiComponentRepository;
        this.uiPageRepository = uiPageRepository;
        this.uiLookupRepository = uiLookupRepository;
        this.uiLookupMasterRepository = uiLookupMasterRepository;
        this.uiPageJsonService = uiPageJsonService;
        this.uiEntityMappingRepository = uiEntityMappingRepository;
    }

    @Override
    public UIComponentDTO createComponent(UIComponentDTO dto, UIEntityMappingDTO mappingDTO) {

        UIPage uiPage = uiPageRepository.findByPageCode(dto.getPageCode())
                .orElseThrow(() -> new RuntimeException("Page not found"));

        Long parentComponentId = validateAndResolveParent(dto.getParentComponentId(), uiPage.getPageCode(), null);

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
            .parentComponentId(parentComponentId)
                .build();

        UIComponent saved = uiComponentRepository.save(component);

        if (dto.getLookupValues() != null && !dto.getLookupValues().isEmpty()) {
            // admin should supply lookupValues; we create or associate a lookup master and link it to component
            saveLookupValues(saved, dto.getLookupValues(), null);
        }

        // SAVE COLUMN MAPPING
        // SAVE ENTITY MAPPING
        if (mappingDTO != null) {

            UIEntityMapping mapping = UIEntityMapping.builder()
                    .uiComponent(saved)
                    .mappingType(mappingDTO.getMappingType())
                    .source(mappingDTO.getSource())
                    .responsePath(mappingDTO.getResponsePath())
                    .tableName(mappingDTO.getTableName())
                    .columnName(mappingDTO.getColumnName())
                    .attributeName(mappingDTO.getAttributeName())
                    .displayName(mappingDTO.getDisplayName())
                    .isRequired(mappingDTO.getIsRequired())
                    .isFilterable(mappingDTO.getIsFilterable())
                    .build();

            uiEntityMappingRepository.save(mapping);
        }


        dto.setId(saved.getId());
        dto.setParentComponentId(saved.getParentComponentId());
        uiPageJsonService.syncPageJson(dto.getPageCode());

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
    public UIComponentDTO updateComponent(Long id, UIComponentDTO dto, UIEntityMappingDTO mappingDTO) {

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
        component.setParentComponentId(validateAndResolveParent(dto.getParentComponentId(), component.getUiPage().getPageCode(), id));
        // lookup master id removed from DTO; relation handled via UILookupMaster association

        UIComponent updated = uiComponentRepository.save(component);

        if (mappingDTO != null) {
                UIEntityMapping mapping = uiEntityMappingRepository.findByUiComponent_Id(updated.getId())
                    .orElseGet(UIEntityMapping::new);
            mapping.setUiComponent(updated);
            mapping.setMappingType(mappingDTO.getMappingType());
            mapping.setSource(mappingDTO.getSource());
            mapping.setResponsePath(mappingDTO.getResponsePath());
            mapping.setTableName(mappingDTO.getTableName());
            mapping.setColumnName(mappingDTO.getColumnName());
            mapping.setAttributeName(mappingDTO.getAttributeName());
            mapping.setDisplayName(mappingDTO.getDisplayName());
            mapping.setIsRequired(mappingDTO.getIsRequired());
            mapping.setIsFilterable(mappingDTO.getIsFilterable());
            uiEntityMappingRepository.save(mapping);
        }

        if (dto.getLookupValues() != null) {
            if (updated.getUiLookupMaster() != null) {
                uiLookupRepository.deleteByUiLookupMaster_Id(updated.getUiLookupMaster().getId());
            }
            if (!dto.getLookupValues().isEmpty()) {
                saveLookupValues(updated, dto.getLookupValues(), null);
            }
        }

        uiPageJsonService.syncPageJson(component.getUiPage().getPageCode());

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

        uiPageJsonService.syncPageJson(pageCode);
    }

    private UIComponentDTO mapToDto(UIComponent component) {

        UIComponentDTO.UIComponentDTOBuilder builder = UIComponentDTO.builder()
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
                .parentComponentId(component.getParentComponentId())
                .lookupValues(component.getUiLookupMaster() != null
                    ? uiLookupRepository.findByUiLookupMaster_Id(component.getUiLookupMaster().getId()).stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList())
                    : List.of());

        uiEntityMappingRepository.findByUiComponent_Id(component.getId()).ifPresent(mapping -> {
            builder.mappingType(mapping.getMappingType());
            builder.source(mapping.getSource());
            builder.responsePath(mapping.getResponsePath());
            builder.tableName(mapping.getTableName());
            builder.columnName(mapping.getColumnName());
            builder.attributeName(mapping.getAttributeName());
            builder.displayName(mapping.getDisplayName());
            builder.mappingRequired(mapping.getIsRequired());
            builder.isFilterable(mapping.getIsFilterable());
        });

        return builder.build();
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

    private Long validateAndResolveParent(Long parentComponentId, String pageCode, Long currentComponentId) {
        if (parentComponentId == null) {
            return null;
        }

        if (currentComponentId != null && parentComponentId.equals(currentComponentId)) {
            throw new RuntimeException("Component cannot be parent of itself");
        }

        UIComponent parent = uiComponentRepository.findById(parentComponentId)
                .orElseThrow(() -> new RuntimeException("Parent component not found"));

        if (!Boolean.TRUE.equals(parent.getIsActive())) {
            throw new RuntimeException("Parent component is inactive");
        }

        String parentPageCode = parent.getUiPage() != null ? parent.getUiPage().getPageCode() : null;
        if (parentPageCode == null || !parentPageCode.equals(pageCode)) {
            throw new RuntimeException("Parent component must belong to the same page");
        }

        if (parent.getComponentType() == null || !ALLOWED_PARENT_TYPES.contains(parent.getComponentType().toLowerCase())) {
            throw new RuntimeException("Parent component type must be card or layout");
        }

        ensureNoCycle(parentComponentId, currentComponentId);
        return parentComponentId;
    }

    private void ensureNoCycle(Long parentComponentId, Long currentComponentId) {
        if (currentComponentId == null) {
            return;
        }

        Long cursor = parentComponentId;
        int safetyCounter = 0;
        while (cursor != null && safetyCounter < 100) {
            if (cursor.equals(currentComponentId)) {
                throw new RuntimeException("Invalid parent relationship: cycle detected");
            }
            UIComponent node = uiComponentRepository.findById(cursor).orElse(null);
            if (node == null) {
                break;
            }
            cursor = node.getParentComponentId();
            safetyCounter++;
        }
    }
}
