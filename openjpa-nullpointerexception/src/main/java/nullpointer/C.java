package nullpointer;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class C {
	
	private long id;
	
	private B    a;
	
	public C() {
		
	}
	
	/**
	 * @return the a
	 */
	@ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = A.class)
	public B getA() {
		return this.a;
	}
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	public long getId() {
		return this.id;
	}
	
	/**
	 * @param a the a to set
	 */
	public void setA(final B a) {
		this.a = a;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(final long id) {
		this.id = id;
	}
}
