package dynamicUi.demo.mapper;

import dynamicUi.demo.dto.UIPageRequestDTO;
import dynamicUi.demo.dto.UIPageResponseDTO;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIRoute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * UIPage and UIRoute are two separate aggregates joined only by
 * UIRoute.page_id (UIRoute owns the relationship). UIPage never held a
 * reference back to its route in the DB — toResponse() takes the route
 * as a second argument so the caller (the service) decides where it
 * came from (freshly built, or looked up via UIRouteRepository).
 */
@Mapper(
        componentModel = "spring",
        uses = UIRouteMapper.class
)
public interface UIPageMapper {

    UIPage toEntity(UIPageRequestDTO dto);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "route", source = "route")
    UIPageResponseDTO toResponse(UIPage entity, UIRoute route);

    void updateEntity(
            UIPageRequestDTO dto,
            @MappingTarget UIPage entity
    );

}
