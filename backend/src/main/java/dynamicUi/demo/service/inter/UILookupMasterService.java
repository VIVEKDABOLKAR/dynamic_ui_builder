package dynamicUi.demo.service.inter;

import dynamicUi.demo.dto.UILookupMasterDTO;

import java.util.List;

public interface UILookupMasterService {

    List<UILookupMasterDTO> getAll();

    UILookupMasterDTO getById(Long id);

    UILookupMasterDTO create(UILookupMasterDTO dto);

    UILookupMasterDTO update(Long id, UILookupMasterDTO dto);

    void delete(Long id);
}