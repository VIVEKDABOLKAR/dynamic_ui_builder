package dynamicUi.demo.service;

import dynamicUi.demo.dto.UILookupDTO;
import dynamicUi.demo.entity.UILookup;
import dynamicUi.demo.entity.UILookupMaster;
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

    @Override
    public List<UILookupDTO> getLookupsByType(String lookupType) {
        return uiLookupRepository.findByLookupTypeAndIsActiveTrueOrderBySequenceNoAsc(lookupType).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UILookupDTO> getAllLookups() {
        return uiLookupRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public UILookupDTO getLookupById(Long id) {

        UILookup lookup = uiLookupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lookup not found"));

        return mapToDto(lookup);
    }

    @Override
    public UILookupDTO createLookup(UILookupDTO dto) {

        UILookupMaster master = uiLookupMasterRepository
                .findById(dto.getLookupMasterId())
                .orElseThrow(() -> new RuntimeException("Lookup Master not found"));

        UILookup lookup = UILookup.builder()
                .lookupType(dto.getLookupType())
                .lookupValue(dto.getLookupValue())
                .displayValue(dto.getDisplayValue())
                .sequenceNo(dto.getSequenceNo())
                .isActive(dto.getIsActive())
                .uiLookupMaster(master)
                .build();

        return mapToDto(uiLookupRepository.save(lookup));
    }

    @Override
    public UILookupDTO updateLookup(Long id, UILookupDTO dto) {

        UILookup lookup = uiLookupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lookup not found"));

        lookup.setLookupType(dto.getLookupType());
        lookup.setLookupValue(dto.getLookupValue());
        lookup.setDisplayValue(dto.getDisplayValue());
        lookup.setSequenceNo(dto.getSequenceNo());
        lookup.setIsActive(dto.getIsActive());

        if (dto.getLookupMasterId() != null) {

            UILookupMaster master = uiLookupMasterRepository
                    .findById(dto.getLookupMasterId())
                    .orElseThrow(() -> new RuntimeException("Lookup Master not found"));

            lookup.setUiLookupMaster(master);
        }

        return mapToDto(uiLookupRepository.save(lookup));
    }

    @Override
    public void deleteLookup(Long id) {

        if (!uiLookupRepository.existsById(id)) {
            throw new RuntimeException("Lookup not found");
        }

        uiLookupRepository.deleteById(id);
    }

    private UILookupDTO mapToDto(UILookup lookup) {
        return UILookupDTO.builder()
                .id(lookup.getId())
                .lookupType(lookup.getLookupType())
                .lookupValue(lookup.getLookupValue())
                .displayValue(lookup.getDisplayValue())
                .sequenceNo(lookup.getSequenceNo())
                .lookupMasterId(lookup.getUiLookupMaster().getId())
                .isActive(lookup.getIsActive())
                .build();
    }


}
