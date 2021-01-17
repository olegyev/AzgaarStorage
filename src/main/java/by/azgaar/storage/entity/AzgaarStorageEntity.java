package by.azgaar.storage.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;

@MappedSuperclass
public abstract class AzgaarStorageEntity implements Serializable, Cloneable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
    @JsonIgnore
    protected long id;
	
}