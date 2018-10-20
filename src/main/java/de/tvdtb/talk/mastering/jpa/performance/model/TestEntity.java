package de.tvdtb.talk.mastering.jpa.performance.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

@Entity
public class TestEntity {

	@Id
	private Long id;

	@Basic(fetch = FetchType.LAZY)
	private String value;

	private Long version;

	public String doSomething() {
		return "Hello from TestEntity " + id;
	}

	/**************************************************************************/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
