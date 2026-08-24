package dynamicUi.demo.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import dynamicUi.demo.entity.UIPageVersion;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PageVersionDTO {

    private Long id;
    private Integer versionNumber;
    private String changeSummary;
    private LocalDateTime createdAt;

    // Only populated for the single-version "get" endpoint (preview/restore).
    // Left null in the list/history endpoint to keep that response light.
    @JsonRawValue
    private String jsonSchema;

    public static PageVersionDTO summary(UIPageVersion version) {
        PageVersionDTO dto = new PageVersionDTO();
        dto.setId(version.getId());
        dto.setVersionNumber(version.getVersionNumber());
        dto.setChangeSummary(version.getChangeSummary());
        dto.setCreatedAt(version.getCreatedAt());
        return dto;
    }

    public static PageVersionDTO full(UIPageVersion version) {
        PageVersionDTO dto = summary(version);
        dto.setJsonSchema(version.getJsonSchema());
        return dto;
    }
}
