package dynamicUi.demo.mapper;

import dynamicUi.demo.dto.RouteRequestDTO;
import dynamicUi.demo.dto.RouteResponseDTO;
import dynamicUi.demo.entity.UIRoute;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UIRouteMapper {

    UIRoute toEntity(RouteRequestDTO dto);

    RouteResponseDTO toResponse(UIRoute entity);

    void updateEntity(
            RouteRequestDTO dto,
            @MappingTarget UIRoute entity
    );

}