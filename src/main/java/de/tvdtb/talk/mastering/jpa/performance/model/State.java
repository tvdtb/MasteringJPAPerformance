package de.tvdtb.talk.mastering.jpa.performance.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.TableGenerator;

@Entity
@NamedQueries({ //
		@NamedQuery(name = "State.deleteAll", query = "DELETE FROM State") //
})
public class State implements TechnicalId {

	@Id
// TODO Optimization Step 1
//	@GeneratedValue
	@TableGenerator(name = "state", initialValue = 1000, allocationSize = Constants.ALLOCATION_SIZE, table = "sequences", pkColumnName = "name", valueColumnName = "value")
	@GeneratedValue(generator = "state")
//--
	long id;

	String name;
	
	String lorem;

	public State() {

	}

	public State(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "State [id=" + id + ", name=" + name + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLorem() {
		return lorem;
	}

	public void setLorem(String lorem) {
		this.lorem = lorem;
	}

}
