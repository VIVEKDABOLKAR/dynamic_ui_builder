package dynamicUi.demo.service;

import dynamicUi.demo.entity.Facility;
import dynamicUi.demo.repoistory.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<Facility> findAll() {
        return facilityRepository.findAll();
    }

    public List<Facility> findAccessibleFacilities(String username) {
        return facilityRepository.findAccessibleFacilitiesByUsername(username);
    }
}