package nullpointer;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance (strategy = InheritanceType.TABLE_PER_CLASS)
public class B extends A {
	
	private String third;
	
	public B() {
		
	}
	
	public B(final String first, final String second, final String third) {
		super(first, second);
		setThird(third);
	}
	
	@Override
	public void doSomething() {
		System.err.println(this.third);
	}
	
	/**
	 * @return the third
	 */
	@Basic
	public String getThird() {
		return this.third;
	}
	
	/**
	 * @param third the third to set
	 */
	public void setThird(final String third) {
		this.third = third;
	}
	
}
