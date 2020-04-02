package by.azgaar.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User implements Serializable, Cloneable {

    @Id
    @Column(nullable = false)
    @JsonIgnore
    private String id;

    @Column(nullable = false)
    private String name;

    private String email;

    private String location;

    @Column(nullable = false)
    private LocalDateTime lastVisit;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Map> maps;

}