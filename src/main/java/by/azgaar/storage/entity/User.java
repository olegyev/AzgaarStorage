package by.azgaar.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends AzgaarStorageEntity {

    @Id
    @Column(nullable = false, unique = true)
    @JsonIgnore
    private String id;

    @Column(nullable = false)
    private String name;

    private String email;

    @Column(nullable = false)
    private int memorySlotsNum = 3;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar firstVisit;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar lastVisit;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Map> maps = new ArrayList<>(0);

}