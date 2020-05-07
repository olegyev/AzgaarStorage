package by.azgaar.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.*;

import java.util.Calendar;

@Entity
@Table(name = "maps")
@Data
@EqualsAndHashCode(callSuper = false)
public class Map extends AzgaarStorageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String fileId;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updated;

    @Column(nullable = false)
    private String version;

}