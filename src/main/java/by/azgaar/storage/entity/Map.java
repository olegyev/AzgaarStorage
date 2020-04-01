package by.azgaar.storage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.*;

import java.util.Calendar;

@Entity
@Table(name = "maps")
@Data
@EqualsAndHashCode(callSuper = false)
public class Map extends by.azgaar.storage.entity.Entity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String filename;

    private String description;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar created;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updated;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar deleted;

}