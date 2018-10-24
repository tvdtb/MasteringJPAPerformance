package de.tvdtb.talk.mastering.jpa.performance.model;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;

@Entity
@NamedQueries({ //
		@NamedQuery(name = "City.deleteAll", query = "DELETE FROM City"), //
		@NamedQuery(name = "City.byName", query = "SELECT e FROM City e WHERE e.name=:name"),
		@NamedQuery(name = "City.byName.joined", query = "SELECT e FROM City e JOIN FETCH e.state WHERE e.name=:name") //
})
public class City implements TechnicalId {

	@Id
// TODO Optimization Step 1
	@GeneratedValue
//	@TableGenerator(name = "city", initialValue = 1000, allocationSize = Constants.ALLOCATION_SIZE, table = "sequences", pkColumnName = "name", valueColumnName = "value")
//	@GeneratedValue(generator = "city")
//--
	long id;

	String name;

	@ManyToOne //(fetch = FetchType.EAGER)
	State state;

	@OneToMany(mappedBy = "city")
	Collection<PostalCode> postalCodes;

	String lorem;
	
	public City() {

	}

	public City(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", state=" + state + "]";
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

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Collection<PostalCode> getPostalCodes() {
		return postalCodes;
	}

	public void setPostalCodes(Collection<PostalCode> postalCodes) {
		this.postalCodes = postalCodes;
	}

	public String getLorem() {
		return lorem;
	}

	public void setLorem(String lorem) {
		this.lorem = lorem;
	}
}
