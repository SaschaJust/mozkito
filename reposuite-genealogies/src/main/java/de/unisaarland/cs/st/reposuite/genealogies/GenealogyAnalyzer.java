package de.unisaarland.cs.st.reposuite.genealogies;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
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
		
		String callType = JavaMethodCall.class.getCanonicalName();
		String methodDefType = JavaMethodDefinition.class.getCanonicalName();
		
		ChangeType dependingChangeType = depending.getChangeType();
		ChangeType parentChangeType = parent.getChangeType();
		
		String dependingElementType = depending.getChangedElementLocation().getElement().getElementType();
		String parentElementType = parent.getChangedElementLocation().getElement().getElementType();
		
		if(dependingElementType.equals(methodDefType)){
			if(parentElementType.equals(callType)){
				return null;
			}
			if(dependingChangeType.equals(ChangeType.Deleted)){
				if(parentChangeType.equals(ChangeType.Deleted)){
					return null;
				}
				return GenealogyEdgeType.DeletedDefinitionOnDefinition;
			}
			if(parentChangeType.equals(ChangeType.Deleted)){
				return GenealogyEdgeType.DefinitionOnDeletedDefinition;
			}
			return GenealogyEdgeType.DefinitionOnDefinition;
		}else if (dependingElementType.equals(callType)){
			if(dependingChangeType.equals(ChangeType.Deleted)){
				if(parentElementType.equals(callType)){
					if(parentChangeType.equals(ChangeType.Deleted)){
						return null;
					}
					return GenealogyEdgeType.DeletedCallOnCall;
				}else if(parentElementType.equals(methodDefType)){
					if(parentChangeType.equals(ChangeType.Deleted)){
						return GenealogyEdgeType.DeletedCallOnDeletedDefinition;
					}
					return null;
				}
			}else{
				if(parentElementType.equals(methodDefType) && (!parentChangeType.equals(ChangeType.Deleted))){
					return GenealogyEdgeType.CallOnDefinition;
				}
				return null;
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
		if ((previousDefinition != null)
				&& (!(operation.getChangeType().equals(ChangeType.Deleted) && (javaElement.getElementType().equals(
						JavaMethodCall.class.getCanonicalName()) && (!previousDefinition.getChangeType().equals(
								ChangeType.Deleted)))))) {
			result.add(previousDefinition);
		}
		return result;
	}
	
}
