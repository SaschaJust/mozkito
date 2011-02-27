package org.se2010.emine.artifacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * The part element of the Problem Artifact in the Tree Viewer.
 * @author Marie
 *
 */

public class ProblemArtifactTypeList implements List 
{
	private List<ProblemArtifact> myProblemArtifacts;
	private String myType;

	public ProblemArtifactTypeList()
	{
		 this.myProblemArtifacts = new ArrayList<ProblemArtifact>();
	}
	
	public ProblemArtifactTypeList(String type, List<ProblemArtifact> list) 
	{
		myType = type;
		myProblemArtifacts = list;
	}
	
	/**This function adds all artifacts with the correct type from a given ProblemArtifact list*/
	public synchronized void addFromList (List <ProblemArtifact> list)
	{
		for(final ProblemArtifact artifact : list)
		{
			this.add(artifact);
//			artifact.setTypeList(this);
		}
	}
	
	
	@Override
	public boolean add(Object e) {
		if (e instanceof ProblemArtifact
				&& ((ProblemArtifact) e).getMap().get("Type")
						.equalsIgnoreCase(myType))
			return myProblemArtifacts.add((ProblemArtifact) e);
		return false;
	}

	@Override
	public void add(int index, Object element) {
		if (element instanceof ProblemArtifact
				&& ((ProblemArtifact) element).getMap().get("Type")
						.equalsIgnoreCase(myType))
			myProblemArtifacts.add(index, (ProblemArtifact) element);
	}

	@Override
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		myProblemArtifacts.clear();

	}

	@Override
	public boolean contains(Object o) {
		return myProblemArtifacts.contains(o);
	}

	@Override
	public boolean containsAll(Collection c) {
		return false;
	}

	@Override
	public Object get(int index) {
		return myProblemArtifacts.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return myProblemArtifacts.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return myProblemArtifacts.isEmpty();
	}

	@Override
	public Iterator iterator() {
		return myProblemArtifacts.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return myProblemArtifacts.lastIndexOf(o);
	}

	@Override
	public ListIterator listIterator() {
		return myProblemArtifacts.listIterator();
	}

	@Override
	public ListIterator listIterator(int index) {
		return myProblemArtifacts.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		return myProblemArtifacts.remove(o);
	}

	@Override
	public Object remove(int index) {
		return myProblemArtifacts.remove(index);
	}

	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object set(int index, Object element) {
		return myProblemArtifacts.set(index, (ProblemArtifact)element);
	}

	@Override
	public int size() {
		return myProblemArtifacts.size();
	}

	@Override
	public List subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		return myProblemArtifacts.toArray(); 
	}

	@Override
	public Object[] toArray(Object[] a) {
		return myProblemArtifacts.toArray(a);
	}
	public String getType(){
		return myType;
	}
	public List<ProblemArtifact> getMyArtifacts(){
		return myProblemArtifacts;
	}
	
	@Override
	public String toString(){
		return getType();
	}
}
