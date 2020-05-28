package by.azgaar.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User extends AzgaarStorageEntity {

    @Id
    @Column(nullable = false, unique = true)
    @NonNull
    @JsonIgnore
    private String id;

    @Column(nullable = false)
    @NonNull
    private String name;

    @NonNull
    private String email;

    @Column(nullable = false)
    private int memorySlotsNum = 3;

    @Column(nullable = false)
    @NonNull
    private LocalDateTime firstVisit;

    @Column(nullable = false)
    private LocalDateTime lastVisit;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Map> maps = new ArrayList<>(0);

}