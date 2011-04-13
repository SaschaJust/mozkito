package nullpointer;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

@Entity
@MappedSuperclass
@Inheritance (strategy = InheritanceType.TABLE_PER_CLASS)
@IdClass (APK.class)
public abstract class A {
	
	private String first;
	private String second;
	
	public A() {
		
	}
	
	public A(final String first, final String second) {
		setFirst(first);
		setSecond(second);
	}
	
	public abstract void doSomething();
	
	/**
	 * @return the first
	 */
	@Id
	public String getFirst() {
		return this.first;
	}
	
	/**
	 * @return the second
	 */
	@Id
	public String getSecond() {
		return this.second;
	}
	
	/**
	 * @param first the first to set
	 */
	public void setFirst(final String first) {
		this.first = first;
	}
	
	/**
	 * @param second the second to set
	 */
	public void setSecond(final String second) {
		this.second = second;
	}
	
}
