//package dynamicUi.demo.service;
//
//import dynamicUi.demo.dto.UIEntityMappingDTO;
//import dynamicUi.demo.dto.UILookupDTO;
//import dynamicUi.demo.entity.UIEntityMapping;
//import dynamicUi.demo.repoistory.UIEntityMappingRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class UIEntityMappingServiceImp implements UIEntityMappingService{
//
//    private final UIEntityMappingRepository uiEntityMappingRepository;
//
//    public UIEntityMappingServiceImp(UIEntityMappingRepository uiEntityMappingRepository) {
//        this.uiEntityMappingRepository = uiEntityMappingRepository;
//    }
//
//
//    @Override
//    public List<UIEntityMappingDTO> getEntityMappingByComponentId(Long componentId) {
//            return uiEntityMappingRepository.findByComponentId(componentId)
//
//    }
//
//    private UIEntityMappingDTO mapToDto(UIEntityMapping uiEntityMapping) {
//
//        return UIEntityMappingDTO.builder()
//                .
//    }
//}
