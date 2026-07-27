package dynamicUi.demo.mapper;

import dynamicUi.demo.dto.UIPageRequestDTO;
import dynamicUi.demo.dto.UIPageResponseDTO;
import dynamicUi.demo.entity.UIPage;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = UIRouteMapper.class
)
public interface UIPageMapper {

    @Mapping(target = "route", ignore = true)
    UIPage toEntity(UIPageRequestDTO dto);

    UIPageResponseDTO toResponse(UIPage entity);

    @Mapping(target = "route", ignore = true)
    void updateEntity(
            UIPageRequestDTO dto,
            @MappingTarget UIPage entity
    );

    /**
     * Keep both sides of @OneToOne synchronized.
     */
    @AfterMapping
    default void linkRoute(
            @MappingTarget UIPage page
    ) {

        if (page.getRoute() != null) {
            page.getRoute().setPage(page);
        }

    }

}