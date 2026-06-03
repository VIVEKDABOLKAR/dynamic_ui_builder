package dynamicUi.demo.service;

import dynamicUi.demo.dto.UILookupDTO;
import dynamicUi.demo.entity.UILookup;
import dynamicUi.demo.repoistory.UILookupMasterRepository;
import dynamicUi.demo.repoistory.UILookupRepository;
import dynamicUi.demo.service.inter.UILookupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UILookupServiceImp implements UILookupService {

    private final UILookupRepository uiLookupRepository;
    private final UILookupMasterRepository uiLookupMasterRepository;

    public UILookupServiceImp(UILookupRepository uiLookupRepository, UILookupMasterRepository uiLookupMasterRepository) {
        this.uiLookupRepository = uiLookupRepository;
        this.uiLookupMasterRepository = uiLookupMasterRepository;
    }

    @Override
    public List<UILookupDTO> getLookupsByMaster(Long lookupMasterId) {
        return uiLookupRepository.findByUiLookupMaster_Id(lookupMasterId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UILookupDTO> getLookupsByComponent(Long componentId) {
        return uiLookupMasterRepository.findByComponentId(componentId)
                .map(m -> uiLookupRepository.findByUiLookupMaster_Id(m.getId()).stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList()))
                .orElse(List.of());
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
}
