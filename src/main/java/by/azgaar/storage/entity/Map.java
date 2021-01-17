package by.azgaar.storage.entity;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "maps")
@Data
@EqualsAndHashCode(callSuper = false)
public class Map extends AzgaarStorageEntity {

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