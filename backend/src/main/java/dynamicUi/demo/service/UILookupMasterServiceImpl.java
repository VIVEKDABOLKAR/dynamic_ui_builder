package dynamicUi.demo.service;

import dynamicUi.demo.dto.UILookupMasterDTO;
import dynamicUi.demo.entity.UILookupMaster;
import dynamicUi.demo.repoistory.UILookupMasterRepository;
import dynamicUi.demo.service.inter.UILookupMasterService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UILookupMasterServiceImpl implements UILookupMasterService {

    private final UILookupMasterRepository repository;

    public UILookupMasterServiceImpl(UILookupMasterRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UILookupMasterDTO> getAll() {

        return repository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public UILookupMasterDTO getById(Long id) {

        return mapToDto(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lookup Master not found")));
    }

    @Override
    public UILookupMasterDTO create(UILookupMasterDTO dto) {

        UILookupMaster master = new UILookupMaster();

        master.setLookupName(dto.getLookupName());
        master.setDescription(dto.getDescription());
        master.setActive(dto.getIsActive());
        master.setComponentId(dto.getComponentId());

        return mapToDto(repository.save(master));
    }

    @Override
    public UILookupMasterDTO update(Long id, UILookupMasterDTO dto) {

        UILookupMaster master = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lookup Master not found"));

        master.setLookupName(dto.getLookupName());
        master.setDescription(dto.getDescription());
        master.setActive(dto.getIsActive());
        master.setComponentId(dto.getComponentId());

        return mapToDto(repository.save(master));
    }

    @Override
    public void delete(Long id) {

        repository.deleteById(id);
    }

    private UILookupMasterDTO mapToDto(UILookupMaster entity) {

        return UILookupMasterDTO.builder()
                .id(entity.getId())
                .lookupName(entity.getLookupName())
                .description(entity.getDescription())
                .isActive(entity.getActive())
                .componentId(entity.getComponentId())
                .build();
    }
}