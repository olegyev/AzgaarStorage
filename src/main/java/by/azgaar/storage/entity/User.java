package by.azgaar.storage.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends AzgaarStorageEntity {

	@Column(name = "oauth2_id")
	private String OAuth2Id;

	@Column(nullable = false)
	private String name;

	private String email;

	@Column(nullable = false)
	private int memorySlotsNum = 3;

	@Column(name = "s3_key", nullable = false)
	private String s3Key;

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