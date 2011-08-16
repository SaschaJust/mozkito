package de.unisaarland.cs.st.reposuite.genealogies;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.ppa.utils.PPAUtils;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;

public class GenealogyAnalyzer {
	
	public static GenealogyEdgeType getEdgeTypeForDependency(final JavaChangeOperation depending,
			final JavaChangeOperation parent) {
		if (depending.getChangedElementLocation().getElement().getElementType()
				.equals(JavaMethodDefinition.class.getCanonicalName())) {
			switch (parent.getChangeType()) {
				case Added:
				case Modified:
				case Renamed:
					switch (depending.getChangeType()) {
						case Added:
							return GenealogyEdgeType.DefinitionOnDefinition;
						case Modified:
							return GenealogyEdgeType.DefinitionOnDefinition;
						case Deleted:
							return GenealogyEdgeType.DeletedDefinitionOnDefinition;
						case Renamed:
							return GenealogyEdgeType.DeletedDefinitionOnDefinition;
						default:
							if (Logger.logWarn()) {
								Logger.warn("Found unexpected operation of type: "
										+ depending.getChangeType().toString() + ". Ignoring!");
							}
							break;
					}
					break;
				case Deleted:
					switch (depending.getChangeType()) {
						case Added:
							return GenealogyEdgeType.DefinitionOnDeletedDefinition;
						case Modified:
							if (Logger.logWarn()) {
								Logger.warn("Modified definition cannot  depend on deleted definition. This should never occur. Ignoring!");
							}
							break;
						case Deleted:
							if (Logger.logWarn()) {
								Logger.warn("Deleted definition cannot  depend on deleted definition. This should never occur. Ignoring!");
							}
							break;
						case Renamed:
							if (Logger.logWarn()) {
								Logger.warn("Renamed definition cannot  depend on deleted definition. This should never occur. Ignoring!");
							}
							break;
						default:
							if (Logger.logWarn()) {
								Logger.warn("Found unexpected operation of type: "
										+ depending.getChangeType().toString() + ". Ignoring!");
							}
							break;
					}
					break;
				default:
					if (Logger.logWarn()) {
						Logger.warn("Found unexpected previousDefinition operation of type: "
								+ parent.getChangeType().toString() + ". Ignoring!");
					}
					break;
			}
		} else {
			switch (parent.getChangeType()) {
				case Added:
				case Modified:
				case Renamed:
					switch (depending.getChangeType()) {
						case Added:
							return GenealogyEdgeType.CallOnDefinition;
						case Modified:
							return GenealogyEdgeType.CallOnDefinition;
						case Deleted:
							return GenealogyEdgeType.DeletedCallOnDeletedDefinition;
						case Renamed:
							return GenealogyEdgeType.DeletedCallOnDeletedDefinition;
						default:
							if (Logger.logWarn()) {
								Logger.warn("Found unexpected operation of type: "
										+ depending.getChangeType().toString() + ". Ignoring!");
							}
							break;
					}
					break;
				case Deleted:
					switch (depending.getChangeType()) {
						case Added:
							if (Logger.logWarn()) {
								Logger.warn("Added call cannot  depend on deleted definition. This should never occur. Ignoring!");
							}
							break;
						case Modified:
							return GenealogyEdgeType.DeletedCallOnDeletedDefinition;
						case Deleted:
							return GenealogyEdgeType.DeletedCallOnDeletedDefinition;
						case Renamed:
							return GenealogyEdgeType.DeletedCallOnDeletedDefinition;
						default:
							if (Logger.logWarn()) {
								Logger.warn("Found unexpected operation of type: "
										+ depending.getChangeType().toString() + ". Ignoring!");
							}
							break;
					}
					break;
				default:
					if (Logger.logWarn()) {
						Logger.warn("Found unexpected previousDefinition operation of type: "
								+ parent.getChangeType().toString() + ". Ignoring!");
					}
					break;
			}
		}
		return GenealogyEdgeType.UNKNOWN;
	}
	
	private final Repository repository;
	
	public GenealogyAnalyzer(final Repository repository){
		this.repository = repository;
	}
	
	/**
	 * Will detect the JavaChangeOperations the given JavaChangeOperation
	 * <code>operaion</code> depends on (outgoing edges).
	 * 
	 * @param operation
	 *            the operation
	 * @return the dependencies
	 */
	@NoneNull
	public Collection<JavaChangeOperation> getDependencies(final JavaChangeOperation operation,
			final PersistenceUtil persistenceUtil) {
		
		Set<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		
		JavaElementLocation location = operation.getChangedElementLocation();
		JavaElement javaElement = location.getElement();
		
		if ((operation.getChangeType().equals(ChangeType.Deleted) && (javaElement.getElementType()
				.equals(JavaMethodCall.class.getCanonicalName())))) {
			//possible DeletedCallOnCall
			
			JavaChangeOperation previousCall = PPAUtils.findPreviousCall(persistenceUtil, repository, operation);
			if (previousCall != null) {
				result.add(previousCall);
			}
		}
		//check all other possibilities
		JavaChangeOperation previousDefinition = PPAUtils
		        .findPreviousDefinition(persistenceUtil, repository, operation);
		if (previousDefinition != null) {
			result.add(previousDefinition);
		}
		return result;
	}
	
}
