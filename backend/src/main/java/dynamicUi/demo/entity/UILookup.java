package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ui_lookup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UILookup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lookup_type")
    private String lookupType;

    @Column(name = "lookup_value")
    private String lookupValue;

    @Column(name = "display_value")
    private String displayValue;

    @Column(name = "sequence_no")
    private Integer sequenceNo;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "lookup_master_id")
    private UILookupMaster uiLookupMaster;
}
