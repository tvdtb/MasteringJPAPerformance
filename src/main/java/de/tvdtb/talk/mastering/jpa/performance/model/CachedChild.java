package de.tvdtb.talk.mastering.jpa.performance.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CachedChild {

	@Id
	long id;

	@ManyToOne
	CachedEntity parent;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CachedEntity getParent() {
		return parent;
	}

	public void setParent(CachedEntity parent) {
		this.parent = parent;
	}
	
	
}
