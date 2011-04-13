package nullpointer;

public class APK {
	
	private String first;
	private String second;
	
	public APK() {
		
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
		if (!(obj instanceof APK)) {
			return false;
		}
		APK other = (APK) obj;
		if (getFirst() == null) {
			if (other.getFirst() != null) {
				return false;
			}
		} else if (!getFirst().equals(other.getFirst())) {
			return false;
		}
		if (getSecond() == null) {
			if (other.getSecond() != null) {
				return false;
			}
		} else if (!getSecond().equals(other.getSecond())) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the first
	 */
	public String getFirst() {
		return this.first;
	}
	
	/**
	 * @return the second
	 */
	public String getSecond() {
		return this.second;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getFirst() == null)
		                                               ? 0
		                                               : getFirst().hashCode());
		result = prime * result + ((getSecond() == null)
		                                                ? 0
		                                                : getSecond().hashCode());
		return result;
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
