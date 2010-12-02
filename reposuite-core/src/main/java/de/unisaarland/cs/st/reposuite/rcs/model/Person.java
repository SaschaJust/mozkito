/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;
import de.unisaarland.cs.st.reposuite.utils.specification.NotAllNull;
import de.unisaarland.cs.st.reposuite.utils.specification.NotEmpty;
import de.unisaarland.cs.st.reposuite.utils.specification.NotNull;
import de.unisaarland.cs.st.reposuite.utils.specification.ParameterConditions;
import de.unisaarland.cs.st.reposuite.utils.specification.Return;
import de.unisaarland.cs.st.reposuite.utils.specification.Trimmed;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@javax.persistence.Table (name = "person")
public class Person implements Annotated {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8598414850294255203L;
	
	/**
	 * @return the simple class name
	 */
	@Transient
	public static String getHandle() {
		return Person.class.getSimpleName();
	}
	
	/**
	 * @param keeper
	 * @param collisions
	 * @return
	 */
	@NoneNull (spec = "When merging multiple Person entities into one person, neither the target person nor the persons under suspect may be null.")
	@Return (checks = ParameterConditions.NotNull)
	public static Person merge(final Person keeper,
			@NotEmpty ("Merging with an empty collection makes no sense.") final Collection<Person> collisions) {
		Condition.notNull(keeper);
		Condition.noneNull(collisions);
		Condition.notEmpty(collisions);
		
		for (Person merged : collisions) {
			merge(keeper, merged);
		}
		
		return keeper;
	}
	
	/**
	 * @param keeper
	 * @param from
	 * @return
	 */
	@NoneNull (spec = "When merging two Person entities, neither target nor merged person may be null.")
	public static Person merge(final Person keeper,
			final Person from) {
		Condition.notNull(keeper);
		Condition.notNull(from);
		
		keeper.addAllEmails(from.getEmailAddresses());
		keeper.addAllFullnames(from.getFullnames());
		keeper.addAllUsernames(from.getUsernames());
		keeper.addAllTransactions(from.getTransactions());
		
		return keeper;
	}
	
	private long                generatedId;
	
	private Set<String>         usernames      = new TreeSet<String>();
	private Set<String>         emailAddresses = new TreeSet<String>();
	private Set<String>         fullnames      = new TreeSet<String>();
	
	private Set<RCSTransaction> transactions   = new TreeSet<RCSTransaction>();
	
	/**
	 * Default constructor used by Hibernate
	 */
	protected Person() {
	}
	
	/**
	 * @param username
	 * @param fullname
	 * @param email
	 */
	@NotAllNull ("Creating a person with only (null) values makes no sense.")
	public Person(@Trimmed final String username, @Trimmed final String fullname, @Trimmed final String email) {
		Condition.check((username != null) || (fullname != null) || (email != null));
		
		if (fullname != null) {
			Condition.equals(fullname.trim(), fullname);
		}
		
		if (username != null) {
			Condition.equals(username.trim(), username);
		}
		
		if (email != null) {
			Condition.equals(email.trim(), email);
		}
		
		addUsername(username);
		addFullname(fullname);
		addEmail(email);
	}
	
	/**
	 * @param emails
	 */
	@Transient
	public void addAllEmails(@NotNull final Set<String> emails) {
		Condition.notNull(emails);
		
		this.emailAddresses.addAll(emails);
	}
	
	/**
	 * @param fullnames
	 */
	@Transient
	public void addAllFullnames(@NotNull final Set<String> fullnames) {
		Condition.notNull(fullnames);
		
		getFullnames().addAll(fullnames);
	}
	
	/**
	 * @param transactions
	 */
	@Transient
	public void addAllTransactions(@NotNull final Set<RCSTransaction> transactions) {
		Condition.notNull(transactions);
		
		getTransactions().addAll(transactions);
	}
	
	/**
	 * @param usernames
	 */
	@Transient
	public void addAllUsernames(@NotNull final Set<String> usernames) {
		Condition.notNull(usernames);
		
		getUsernames().addAll(usernames);
	}
	
	/**
	 * @param email
	 */
	@Transient
	public void addEmail(final String email) {
		if (email != null) {
			this.getEmailAddresses().add(email);
		}
	}
	
	/**
	 * @param fullname
	 */
	@Transient
	public void addFullname(final String fullname) {
		if (fullname != null) {
			this.fullnames.add(fullname);
		}
	}
	
	/**
	 * @param username
	 */
	@Transient
	public void addUsername(final String username) {
		if (username != null) {
			this.usernames.add(username);
		}
	}
	
	/**
	 * Assign transaction. Is automatically called from the constructor of
	 * RCSTransaction
	 * 
	 * @param transaction
	 *            the transaction
	 */
	@Transient
	protected void assignTransaction(@NotNull final RCSTransaction transaction) {
		Condition.notNull(transaction);
		
		this.transactions.add(transaction);
	}
	
	public void clearTransaction(){
		this.transactions.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Person)) {
			return false;
		}
		Person other = (Person) obj;
		
		return this.hashCode() == other.hashCode();
	}
	
	/**
	 * @return
	 */
	@ElementCollection
	public Set<String> getEmailAddresses() {
		return this.emailAddresses;
	}
	
	/**
	 * @return the firstCommit
	 */
	@Transient
	public RCSTransaction getFirstCommit() {
		return this.transactions.iterator().next();
	}
	
	/**
	 * @return the name
	 */
	@ElementCollection
	public Set<String> getFullnames() {
		return this.fullnames;
	}
	
	/**
	 * @return the generatedId
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	private long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * @return the latestCommit
	 */
	@Transient
	public RCSTransaction getLatestCommit() {
		RCSTransaction rcsTransaction = null;
		Iterator<RCSTransaction> iterator = this.transactions.iterator();
		while (iterator.hasNext()) {
			rcsTransaction = iterator.next();
		}
		return rcsTransaction;
	}
	
	/**
	 * @return the transactions
	 */
	@ManyToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Set<RCSTransaction> getTransactions() {
		return this.transactions;
	}
	
	/**
	 * @return
	 */
	@ElementCollection
	public Set<String> getUsernames() {
		return this.usernames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.emailAddresses.isEmpty())
				? 0
						: this.emailAddresses.iterator().next().hashCode());
		result = prime * result + ((this.fullnames.isEmpty())
				? 0
						: this.fullnames.iterator().next().hashCode());
		result = prime * result + ((this.usernames.isEmpty())
				? 0
						: this.usernames.iterator().next().hashCode());
		// result = prime * result + (int) (this.generatedId ^ (this.generatedId
		// >>> 32));
		return result;
	}
	
	/**
	 * @param person
	 * @return
	 */
	@Transient
	public boolean matches(@NotNull final Person person) {
		Condition.notNull(person);
		
		if (!CollectionUtils.intersection(getEmailAddresses(), person.getEmailAddresses()).isEmpty()) {
			return true;
		} else if (!CollectionUtils.intersection(getUsernames(), person.getUsernames()).isEmpty()) {
			return true;
		} else {
			// foo, null, bleh
			// null, bar, bleh
			
			// foo, null, bleh
			// null, null, bleh
			
			if ((getUsernames().isEmpty() && person.getEmailAddresses().isEmpty())
					|| (person.getUsernames().isEmpty() && getEmailAddresses().isEmpty())) {
				return !CollectionUtils.intersection(getFullnames(), person.getFullnames()).isEmpty();
			} else {
				return false;
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param emailAddresses
	 */
	@SuppressWarnings ("unused")
	private void setEmailAddresses(final Set<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
	
	/**
	 * @param fullnames
	 *            the fullnames to set
	 */
	protected void setFullnames(final Set<String> fullnames) {
		this.fullnames = fullnames;
	}
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	protected void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * @param transactions
	 *            the transactions to set
	 */
	protected void setTransactions(final Set<RCSTransaction> transactions) {
		this.transactions = new TreeSet<RCSTransaction>(transactions);
	}
	
	/**
	 * @param usernames
	 */
	@SuppressWarnings ("unused")
	private void setUsernames(final Set<String> usernames) {
		this.usernames = usernames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Person [generatedId=");
		builder.append(getGeneratedId());
		builder.append(", usernames=");
		builder.append(this.usernames);
		builder.append(", emailAddresses=");
		builder.append(this.emailAddresses);
		builder.append(", fullnames=");
		builder.append(this.fullnames);
		builder.append(", transactions=");
		builder.append(this.transactions);
		builder.append(", hashcode=");
		builder.append(hashCode());
		builder.append("]");
		return builder.toString();
	}

}
