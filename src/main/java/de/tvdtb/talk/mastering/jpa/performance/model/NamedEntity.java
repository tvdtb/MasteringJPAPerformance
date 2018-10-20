package de.tvdtb.talk.mastering.jpa.performance.model;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Access(AccessType.PROPERTY)
public class NamedEntity implements TechnicalId {
	public static AtomicInteger getNameCounter = new AtomicInteger();
	
	long id;
	String name;

	public NamedEntity() {

	}

	public NamedEntity(String name) {
		super();
		this.name = name;
	}

	@Id
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		NamedEntity.getNameCounter.incrementAndGet();
		System.out.println("TestEntity2.getName id="+id+" name="+name);
		return name;
	}

	public void setName(String name) {
		System.out.println("TestEntity2.setName id="+id+" newName="+name);
		this.name = name;
	}

}
