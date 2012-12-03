/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.mozkito.causeeffect.kripke;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mozkito.causeeffect.ctl.CTLFormula;
import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.utils.VertexSelector;

/**
 * Instances of this class represent Kripke structures. Basically, Kripke structure is a nondeterministic finite state
 * machine with a labeling function. The labeling function maps each state to a set of properties that hold in that
 * state.
 * 
 * The main intent of the CTL project is to generate CTL formulas that are true for object usage models. The problem
 * with this approach is that object usage models have anonymous states and properties are not defined. My solution is
 * to transform them into Kripke structures, where each state will be labeled by the event that happened most recently
 * (e.g., a method call, a field access, etc.). Thus, the set of properties is essentially a set of all events that
 * occur in the object usage model. Kripke structures can then be used for model checking.
 *
 * @param <V> the value type
 * @author Andrzej Wasylkowski
 */
public class KripkeStructure<V> {
	
	// Important invariants:
	// 1. state2successors has the same set of keys as state2labels,
	// trueFormulas, and false formulas,
	// 2. initialStates is a nonempty subset of the set of keys in
	// state2successors
	// 3. each state has at least one successor
	// 4. state2predecessors has the same set of keys as state2successors and
	// is consistent with it
	
	/**
	 * This factory method creates a Kripke structure out of a given object usage model.
	 *
	 * @param <T> the generic type
	 * @param changeGenealogy the change genealogy
	 * @param rootVertex the root vertex
	 * @param labelGenerator the label generator
	 * @param selector the selector
	 * @return Kripke structure created from the given object usage model.
	 */
	public static <T> KripkeStructure<T> createFrom(final ChangeGenealogy<T> changeGenealogy,
	                                                final T rootVertex,
	                                                final LabelGenerator<T> labelGenerator,
	                                                final VertexSelector<T> selector) {
		// This method creates a Kripke structure from the given OUM by
		// following the method described in the following paper:
		// Jonsson, Bengt, Ahmed Hussain Khan, and Joachim Parrow. 1990.
		// Implementing a model checking algorithm by adapting existing
		// automated tools. In Proceedings of the International Workshop on
		// Automatic Verification Methods for Finite State Systems, 179-188.
		// Lecture Notes in Computer Science 407. Berlin: Springer-Verlag
		
		final KripkeStructure<T> kripkeStruct = new KripkeStructure<T>();
		final Set<State> finalTransitionStates = new HashSet<State>();
		
		// Create states in the Kripke structure.
		final State initialState = kripkeStruct.createNewState(null);
		
		kripkeStruct.markStateAsInitial(initialState);
		
		final HashMap<T, State> vertices2States = new HashMap<T, State>();
		
		final LinkedList<T> verticesToProcess = new LinkedList<>();
		verticesToProcess.add(rootVertex);
		while (!verticesToProcess.isEmpty()) {
			final T vertex = verticesToProcess.poll();
			for (final T dependent : changeGenealogy.getAllDependants(vertex)) {
				if (selector.selectVertex(dependent)) {
					verticesToProcess.add(dependent);
				}
			}
			vertices2States.put(vertex, kripkeStruct.createNewState(vertex));
			final State state = vertices2States.get(vertex);
			final Collection<T> allDependents = changeGenealogy.getAllDependants(vertex);
			if (allDependents.isEmpty()) {
				// if there are no outgoing edges, the state is a final state
				finalTransitionStates.add(state);
				kripkeStruct.addState(state, kripkeStruct.finalState);
			}
			if (!kripkeStruct.getInitialStates().contains(state)) {
				// add file path as labels
				final Collection<Label> labels = labelGenerator.getLabels(vertex);
				for (final Label label : labels) {
					kripkeStruct.addLabelToState(state, label);
				}
			}
		}
		
		for (final T from : vertices2States.keySet()) {
			for (final T to : changeGenealogy.getAllDependants(from)) {
				if (selector.selectVertex(to)) {
					final State fromState = vertices2States.get(from);
					final State toState = vertices2States.get(to);
					// TODO this is a quick fix. We need to ensure this property on the genealogy graph
					if ((from != null) && (to != null)) {
						kripkeStruct.addState(fromState, toState);
					}
				}
			}
		}
		
		return kripkeStruct;
	}
	
	/** Set of initial states of this Kripke structure. */
	private final Set<State>                  initialStates;
	
	/** Final state of this Kripke structure. */
	private final State                       finalState;
	
	/** Transition relation of this Kripke structure. */
	private final Map<State, Set<State>>      state2successors;
	
	/** Inverted transition relation of this Kripke structure. */
	private final Map<State, Set<State>>      state2predecessors;
	
	/** Labeling of the states. */
	private final Map<State, Set<Label>>      state2labels;
	
	/** Mapping from states to CTL formulas that hold in those states. */
	private final Map<State, Set<CTLFormula>> trueFormulas;
	
	/** Mapping from states to CTL formulas that do not hold in those states. */
	private final Map<State, Set<CTLFormula>> falseFormulas;
	
	/** Set of formulas that were evaluated on all states in this structure. */
	private final Set<CTLFormula>             evaluatedFormulas;
	
	/** The states2vertices. */
	private final HashMap<State, V>           states2vertices;
	
	/**
	 * Creates a new, empty Kripke structure. This constructor should not be used directly. Use the
	 * <code>createFrom</code> factory method instead.
	 */
	private KripkeStructure() {
		this.initialStates = new HashSet<State>();
		this.state2successors = new HashMap<State, Set<State>>();
		this.state2predecessors = new HashMap<State, Set<State>>();
		this.state2labels = new HashMap<State, Set<Label>>();
		this.trueFormulas = new HashMap<State, Set<CTLFormula>>();
		this.falseFormulas = new HashMap<State, Set<CTLFormula>>();
		this.evaluatedFormulas = new HashSet<CTLFormula>();
		this.states2vertices = new HashMap<State, V>();
		
		this.finalState = this.createNewState(null);
		
		this.addState(this.finalState, this.finalState);
	}
	
	/**
	 * Adds a given label to the given state.
	 * 
	 * @param state
	 *            State to add a label to.
	 * @param label
	 *            Label to add.
	 */
	public void addLabelToState(final State state,
	                            final Label label) {
		assert this.state2labels.containsKey(state);
		this.state2labels.get(state).add(label);
	}
	
	/**
	 * Adds a transition between the two given states.
	 * 
	 * @param from
	 *            Initial state of the transition.
	 * @param to
	 *            Final state of the transition.
	 */
	public void addState(final State from,
	                     final State to) {
		assert this.state2successors.containsKey(from);
		assert this.state2successors.containsKey(to);
		assert this.state2predecessors.containsKey(from);
		assert this.state2predecessors.containsKey(to);
		this.state2successors.get(from).add(to);
		this.state2predecessors.get(to).add(from);
	}
	
	/**
	 * Clear.
	 *
	 * @author Kim Herzig <kim@mozkito.org>
	 */
	public void clear() {
		for (final Set<CTLFormula> m : this.trueFormulas.values()) {
			m.clear();
		}
		for (final Set<CTLFormula> m : this.falseFormulas.values()) {
			m.clear();
		}
		this.evaluatedFormulas.clear();
	}
	
	/**
	 * Contains vertex for state.
	 *
	 * @param state the state
	 * @return true, if successful
	 */
	public boolean containsVertexForState(final State state) {
		return this.states2vertices.containsKey(state);
	}
	
	/**
	 * Creates a new, unique state, and adds it to this Kripke structure. This method should not be used directly,
	 * because it violates important Kripke structure invariants that have to be repaired later.
	 *
	 * @param vertex the vertex
	 * @return The state that was created.
	 */
	public State createNewState(final V vertex) {
		final State state = new State();
		this.state2successors.put(state, new HashSet<State>());
		this.state2predecessors.put(state, new HashSet<State>());
		this.state2labels.put(state, new HashSet<Label>());
		this.trueFormulas.put(state, new HashSet<CTLFormula>());
		this.falseFormulas.put(state, new HashSet<CTLFormula>());
		this.states2vertices.put(state, vertex);
		return state;
	}
	
	/**
	 * Returns the set of all labels used in this Kripke structure.
	 * 
	 * @return Set of all labels in this Kripke structure.
	 */
	public Set<Label> getAllLabels() {
		final Set<Label> labels = new HashSet<Label>();
		for (final Set<Label> stateLabels : this.state2labels.values()) {
			labels.addAll(stateLabels);
		}
		return Collections.unmodifiableSet(labels);
	}
	
	/**
	 * Returns the set of all states in this Kripke structure.
	 * 
	 * @return Set of all states in this Kripke structure.
	 */
	public Set<State> getAllStates() {
		return Collections.unmodifiableSet(this.state2successors.keySet());
	}
	
	/**
	 * Returns the final state of this Kripke structure.
	 * 
	 * @return Final state of this Kripke structure.
	 */
	public State getFinalState() {
		return this.finalState;
	}
	
	/**
	 * Returns the set of all initial states in this Kripke structure.
	 * 
	 * @return Set of all initial states in this Kripke structure.
	 */
	public Set<State> getInitialStates() {
		return Collections.unmodifiableSet(this.initialStates);
	}
	
	/**
	 * Returns predecessors of a given state.
	 * 
	 * @param state
	 *            State, whose predecessors are to be returned.
	 * @return Predecessors of a given state.
	 */
	public Set<State> getPredecessors(final State state) {
		return Collections.unmodifiableSet(this.state2predecessors.get(state));
	}
	
	/**
	 * Returns the set of labels associated with the given state.
	 * 
	 * @param state
	 *            State to find labels for.
	 * @return Labels associated with the given set.
	 */
	public Set<Label> getStateLabels(final State state) {
		return Collections.unmodifiableSet(this.state2labels.get(state));
	}
	
	/**
	 * Partitions the states of this Kripke structure into equivalence classes, where each class is uniquely defined by
	 * states' indegree, outdegree, and set of labels.
	 * 
	 * @return Mapping from equivalence class definition to its content.
	 */
	private Map<Triple<Integer, Integer, Set<Label>>, Set<State>> getStatesClasses() {
		// calculate all states' indegree
		final Map<State, Integer> state2indegree = new HashMap<State, Integer>();
		for (final State state : this.state2successors.keySet()) {
			state2indegree.put(state, 0);
		}
		for (final Set<State> statesSet : this.state2successors.values()) {
			for (final State state : statesSet) {
				state2indegree.put(state, state2indegree.get(state) + 1);
			}
		}
		
		// calculate all states' outdegree
		final Map<State, Integer> state2outdegree = new HashMap<State, Integer>();
		for (final State state : this.state2successors.keySet()) {
			state2outdegree.put(state, this.state2successors.get(state).size());
		}
		
		// partition the space of states into equivalence classes
		final Map<Triple<Integer, Integer, Set<Label>>, Set<State>> result = new HashMap<Triple<Integer, Integer, Set<Label>>, Set<State>>();
		for (final State state : this.state2labels.keySet()) {
			final Integer indegree = state2indegree.get(state);
			final Integer outdegree = state2outdegree.get(state);
			final Set<Label> labels = this.state2labels.get(state);
			final Triple<Integer, Integer, Set<Label>> equivClass = new Triple<Integer, Integer, Set<Label>>(indegree,
			                                                                                                 outdegree,
			                                                                                                 labels);
			if (!result.containsKey(equivClass)) {
				result.put(equivClass, new HashSet<State>());
			}
			result.get(equivClass).add(state);
		}
		return result;
	}
	
	/**
	 * Gets the states formula is true.
	 *
	 * @param formula the formula
	 * @return the states formula is true
	 */
	public Set<State> getStatesFormulaIsTrue(final CTLFormula formula) {
		final Set<State> result = new HashSet<State>();
		for (final State state : this.trueFormulas.keySet()) {
			if (this.trueFormulas.get(state).contains(formula)) {
				result.add(state);
			}
		}
		return result;
	}
	
	/**
	 * Returns successors of a given state.
	 * 
	 * @param state
	 *            State, whose successors are to be returned.
	 * @return Successors of a given state.
	 */
	public Set<State> getSuccessors(final State state) {
		return Collections.unmodifiableSet(this.state2successors.get(state));
	}
	
	/**
	 * Gets the vertex for state.
	 *
	 * @param state the state
	 * @return the vertex for state
	 */
	public V getVertexForState(final State state) {
		return this.states2vertices.get(state);
	}
	
	/**
	 * Checks if this Kripke structure is isomorphic with the given one, provided the mapping from states of this
	 * structure to states of other structure. This method assumes that both structures have the same number of states,
	 * the mapping is both left- and right-total, and that the mapping maps only pairs of states with the same labels.
	 *
	 * @param formula the formula
	 * @return <code>true</code> if both structures are isomorphic; <code>false</code> otherwise.
	 */
	// private boolean isomorphicWith(KripkeStructure other,
	// Map<State, State> this2other) {
	// // construct a complementary mapping for convenience
	// Map<State, State> other2this = new HashMap<State, State>();
	// for (State thisState : this2other.keySet()) {
	// State otherState = this2other.get(thisState);
	// other2this.put(otherState, thisState);
	// }
	//
	// // check that initial states are preserved
	// for (State thisState : this2other.keySet()) {
	// State otherState = this2other.get(thisState);
	// if (this.initialStates.contains(thisState)) {
	// if (!other.initialStates.contains(otherState)) {
	// return false;
	// }
	// } else {
	// if (other.initialStates.contains(otherState)) {
	// return false;
	// }
	// }
	// }
	//
	// // check that the mapping preserves the transition relation
	// // (first check this -> other)
	// for (State thisState : this2other.keySet()) {
	// State otherState = this2other.get(thisState);
	// for (State thisSucc : this.state2successors.get(thisState)) {
	// State otherSucc = this2other.get(thisSucc);
	// if (!other.state2successors.get(otherState).contains(otherSucc)) {
	// return false;
	// }
	// }
	// }
	//
	// // check that the mapping preserves the transition relation
	// // (now check other -> this)
	// for (State otherState : other2this.keySet()) {
	// State thisState = other2this.get(otherState);
	// for (State otherSucc : other.state2successors.get(otherState)) {
	// State thisSucc = other2this.get(otherSucc);
	// if (!this.state2successors.get(thisState).contains(thisSucc)) {
	// return false;
	// }
	// }
	// }
	//
	// return true;
	// }
	
	public Set<V> getVerticesFormulaIsTrue(final CTLFormula formula) {
		final Set<State> states = this.getStatesFormulaIsTrue(formula);
		final Set<V> result = new HashSet<V>();
		for (final State state : states) {
			result.add(this.states2vertices.get(state));
		}
		return result;
	}
	
	/**
	 * Checks if the given CTL formula does not hold in the given state. This method simply consults the cache; it does
	 * not actually evaluate the formula. If the formula was not evaluated, <code>false</code> will be returned.
	 * 
	 * @param state
	 *            State to check in.
	 * @param formula
	 *            Formula to check for.
	 * @return <code>true</code> if the given CTL formula does not hold in the given state; <code>false</code> if the
	 *         formula holds or was not evaluated at the given state.
	 */
	public boolean isFormulaFalse(final State state,
	                              final CTLFormula formula) {
		return this.falseFormulas.get(state).contains(formula);
	}
	
	/**
	 * Checks if the given CTL formula holds in the given state. This method simply consults the cache; it does not
	 * actually evaluate the formula. If the formula was not evaluated, <code>false</code> will be returned.
	 * 
	 * @param state
	 *            State to check in.
	 * @param formula
	 *            Formula to check for.
	 * @return <code>true</code> if the given CTL formula holds in the given state; <code>false</code> if the formula
	 *         does not hold or was not evaluated at the given state.
	 */
	public boolean isFormulaTrue(final State state,
	                             final CTLFormula formula) {
		return this.trueFormulas.get(state).contains(formula);
	}
	
	/**
	 * Checks if this Kripke structure is isomorphic with the given one.
	 * 
	 * @param other
	 *            Kripke structure to check isomorphism with.
	 * @return <code>true</code> if both structures are isomorphic; <code>false</code> otherwise.
	 */
	public boolean isomorphicWith(final KripkeStructure<V> other) {
		// first check that the numbers of states (total and initial) are equal
		if (this.state2successors.size() != other.state2successors.size()) {
			return false;
		}
		if (this.initialStates.size() != other.initialStates.size()) {
			return false;
		}
		
		// prepare sets of potentially equivalent states for both Kripke
		// structures; partition based on in- and out-degree, and on labels
		final Map<Triple<Integer, Integer, Set<Label>>, Set<State>> thisClasses = this.getStatesClasses();
		final Map<Triple<Integer, Integer, Set<Label>>, Set<State>> otherClasses = other.getStatesClasses();
		if (thisClasses.size() != otherClasses.size()) {
			return false;
		}
		if (!thisClasses.keySet().equals(otherClasses.keySet())) {
			return false;
		}
		
		// make sure all classes in both Kripke structures have the same size
		// remove classes of size one and create mappings for them
		final List<List<State>> thisClassesList = new ArrayList<List<State>>();
		final List<List<State>> otherClassesList = new ArrayList<List<State>>();
		final Map<State, State> this2other = new HashMap<State, State>();
		for (final Triple<Integer, Integer, Set<Label>> properties : new HashSet<Triple<Integer, Integer, Set<Label>>>(
		                                                                                                               thisClasses.keySet())) {
			final Set<State> thisClass = thisClasses.get(properties);
			final Set<State> otherClass = otherClasses.get(properties);
			if (thisClass.size() != otherClass.size()) {
				return false;
			}
			if (thisClass.size() == 1) {
				final State thisState = thisClass.iterator().next();
				final State otherState = otherClass.iterator().next();
				this2other.put(thisState, otherState);
				thisClasses.remove(properties);
				otherClasses.remove(properties);
			} else {
				thisClassesList.add(new ArrayList<State>(thisClass));
				otherClassesList.add(new ArrayList<State>(otherClass));
			}
		}
		
		// iterate through all combinations of states in larger classes
		// and check if there is any that results in an isomorphism
		// PermutationsSet<State> permSet = new PermutationsSet<State>(
		// otherClassesList);
		// for (List<List<State>> otherClassesPerms : permSet) {
		// for (int classIndex = 0; classIndex < otherClassesPerms.size();
		// classIndex++) {
		// List<State> thisStateList = thisClassesList.get(classIndex);
		// List<State> otherStateList = otherClassesPerms.get(classIndex);
		// for (int stateIndex = 0; stateIndex < otherStateList.size();
		// stateIndex++) {
		// State thisState = thisStateList.get(stateIndex);
		// State otherState = otherStateList.get(stateIndex);
		// this2other.put(thisState, otherState);
		// }
		// }
		// if (this.isomorphicWith(other, this2other)) {
		// return true;
		// }
		// }
		return false;
	}
	
	/**
	 * Marks given formula as evaluated for all states.
	 * 
	 * @param formula
	 *            Formula to mark as evaluated.
	 */
	public void markEvaluatedFormula(final CTLFormula formula) {
		this.evaluatedFormulas.add(formula);
	}
	
	/**
	 * Called to inform this Kripke structure that the given formula was evaluated for the given state and that its
	 * truth value was evaluated to be as given.
	 * 
	 * @param state
	 *            State, in which the formula was evaluted.
	 * @param formula
	 *            Formula that was evaluated.
	 * @param truthValue
	 *            Truth value of the formula in that state.
	 */
	public void markEvaluatedFormula(final State state,
	                                 final CTLFormula formula,
	                                 final boolean truthValue) {
		if (truthValue) {
			this.trueFormulas.get(state).add(formula);
		} else {
			this.falseFormulas.get(state).add(formula);
		}
	}
	
	/**
	 * Marks the given state as an initial one.
	 *
	 * @param state the state
	 * @state State to be marked as an initial one.
	 */
	public void markStateAsInitial(final State state) {
		assert this.state2successors.containsKey(state);
		assert this.state2predecessors.containsKey(state);
		this.initialStates.add(state);
	}
	
	/**
	 * Removes the given state from this Kripke structure.
	 * 
	 * @param state
	 *            State to remove from this Kripke structure.
	 */
	@SuppressWarnings ("unused")
	private void removeState(final State state) {
		if (this.initialStates.contains(state)) {
			System.err.println("Can't remove initial state.");
			throw new InternalError();
		}
		this.falseFormulas.remove(state);
		this.state2labels.remove(state);
		this.state2predecessors.remove(state);
		for (final State s : this.state2predecessors.keySet()) {
			this.state2predecessors.get(s).remove(state);
		}
		this.state2successors.remove(state);
		for (final State s : this.state2successors.keySet()) {
			this.state2successors.get(s).remove(state);
		}
		this.trueFormulas.remove(state);
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return this.states2vertices.size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// assign unique ids to states so that we can output anything
		final Map<State, Integer> state2id = new HashMap<State, Integer>();
		for (final State state : this.state2successors.keySet()) {
			state2id.put(state, state2id.size() + 1);
		}
		final Map<Integer, State> id2state = new HashMap<Integer, State>();
		for (final State state : this.state2successors.keySet()) {
			id2state.put(state2id.get(state), state);
		}
		final List<Integer> ids = new ArrayList<Integer>(id2state.keySet());
		Collections.sort(ids);
		
		// output the states and their labels + mark initial states
		final StringBuffer result = new StringBuffer();
		for (final Integer id : ids) {
			final State state = id2state.get(id);
			result.append("State ").append(state2id.get(state));
			if (this.initialStates.contains(state)) {
				result.append(" (initial state)");
			}
			result.append(":\n");
			for (final Label label : this.state2labels.get(state)) {
				result.append("    ").append(label.toString());
				result.append('\n');
			}
		}
		
		// output the transition relation
		for (final Integer id : ids) {
			final State state = id2state.get(id);
			for (final State succ : this.state2successors.get(state)) {
				result.append("Transition ").append(state2id.get(state));
				result.append(" -> ").append(state2id.get(succ));
				result.append('\n');
			}
		}
		
		return result.toString();
	}
	
	/**
	 * Checks if the given formula was evaluated in all the states.
	 * 
	 * @param formula
	 *            Formula to check.
	 * @return <code>true</code> if the formula was evaluated in all the states; <code>false</code> otherwise.
	 */
	public boolean wasFormulaEvaluated(final CTLFormula formula) {
		return this.evaluatedFormulas.contains(formula);
	}
	
	/**
	 * Checks if the given CTL formula was evaluated for the given state.
	 * 
	 * @param state
	 *            State to check in.
	 * @param formula
	 *            Formula to check for.
	 * @return <code>true</code> if the given CTL formula was evaluated for the given state; <code>false</code>
	 *         otherwise.
	 */
	public boolean wasFormulaEvaluated(final State state,
	                                   final CTLFormula formula) {
		return this.trueFormulas.get(state).contains(formula) || this.falseFormulas.get(state).contains(formula);
	}
}
