package dynamicUi.demo.mapper;

import dynamicUi.demo.dto.RouteRequestDTO;
import dynamicUi.demo.dto.RouteResponseDTO;
import dynamicUi.demo.entity.UIRoute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UIRouteMapper {

    UIRoute toEntity(RouteRequestDTO dto);

    @Mapping(target = "pageCode", source = "page.pageCode")
    @Mapping(target = "pageName", source = "page.pageName")
    RouteResponseDTO toResponse(UIRoute entity);

    void updateEntity(
            RouteRequestDTO dto,
            @MappingTarget UIRoute entity
    );

}