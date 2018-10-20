package de.tvdtb.talk.mastering.jpa.performance.model;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cacheable(value=true)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class CachedEntity implements TechnicalId {
	public static AtomicInteger getNameCounter = new AtomicInteger();
	
	@Id
	long id;
	String name;

	public CachedEntity() {

	}

	public CachedEntity(String name) {
		super();
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
