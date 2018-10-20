package de.tvdtb.talk.mastering.jpa.performance.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * @author brt
 *
 */
@Entity
@Table(name = "postalcode")
@NamedQueries({ //
		@NamedQuery(name = "PostalCode.deleteAll", query = "DELETE FROM PostalCode"),
		@NamedQuery(name = "PostalCode.byCodes", query = "SELECT e FROM PostalCode e WHERE e.code IN (:codes)"),
		@NamedQuery(name = "PostalCode.byCodesOptimized.prefetch", query = "SELECT e.city.state FROM PostalCode e  WHERE e.code IN (:codes)"),
		@NamedQuery(name = "PostalCode.byCodesOptimized", query = "SELECT e FROM PostalCode e JOIN FETCH e.city  WHERE e.code IN (:codes)") //
})
public class PostalCode implements TechnicalId {
	@Id
// TODO Optimization Step 1	
//	@GeneratedValue
	@TableGenerator(name = "postalcode", initialValue = 1000, allocationSize = Constants.ALLOCATION_SIZE, table = "sequences", pkColumnName = "name", valueColumnName = "value")
	@GeneratedValue(generator = "postalcode")
//--	
	long id;

	String code;

	@ManyToOne
	City city;

	String lorem;
	
	public PostalCode() {

	}

	public PostalCode(String code) {
		super();
		this.code = code;
	}

	@Override
	public String toString() {
		return "PostalCode [id=" + id + ", code=" + code + ", city=" + city + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getLorem() {
		return lorem;
	}

	public void setLorem(String lorem) {
		this.lorem = lorem;
	}

}
