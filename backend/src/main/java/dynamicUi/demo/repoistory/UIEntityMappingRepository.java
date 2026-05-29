package dynamicUi.demo.repoistory;


import dynamicUi.demo.entity.UIEntityMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UIEntityMappingRepository extends JpaRepository<UIEntityMapping, Long> {
    java.util.Optional<UIEntityMapping> findByUiComponent_Id(Long componentId);
}