package by.azgaar.storage.entity;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "click_counter")
@Data
@EqualsAndHashCode(callSuper = false)
public class ClickEvent extends AzgaarStorageEntity {

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar clickTimestamp;
	
}