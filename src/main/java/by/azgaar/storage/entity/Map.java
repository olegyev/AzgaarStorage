package by.azgaar.storage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

import java.util.Calendar;

@Entity
@Table(name = "maps")
@Data
@EqualsAndHashCode(callSuper = false)
public class Map extends AzgaarStorageEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updated;

    @Column(nullable = false)
    private String version;

}