package com.app.model.framework;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.stream.SourceBase;

public class KripkeGraphGenerator extends SourceBase
implements Generator {

	int nodeID = 0;
	int edgeID = 0;

	/**
	 * Map of variable id and variable e.g. look-up-table for variables by id
	 */
	public Map<String, Variable> vars;
	/**
	 * Map of variable ids as keys with corresponding values e.g. a 'state'
	 */
	Tuple current;

	Iterator<Tuple> i;

	Set<Tuple> audited;
	Set<Tuple> unaudited;
	Set<Tuple> found;
	Set<Tuple> added;

	public KripkeGraphGenerator(Map<String, Variable> vars) {
		super();
		this.vars = vars;
		// this.addSink(new ConsoleSink());
		System.out.println("KripkeGraphGenerator:" + vars);

		audited = new HashSet<Tuple>();
		unaudited = new HashSet<Tuple>();
		found = new HashSet<Tuple>();
		added = new HashSet<Tuple>();
	}

	public void begin() {
		current = new Tuple(); // Create state
		vars.forEach((k,v) -> current.put(k, v.getValue())); // Add initial values
		System.out.println("Begin = " + current.toString());
		unaudited.add(current);
	}

	public boolean nextEvents() {

		i = unaudited.iterator();

		if(!i.hasNext()) {
			return false;
		}

		current = i.next();
		System.out.println("Current = " + current.toString());
		addNode(current); // Send node to graph

		for(Variable var: vars.values()) {
			for(Tuple s : new TransitionBlock(var).audit(current)) {

				// State has to be created again so the node count stays intact 
				Tuple s2 = new Tuple();
				s.forEach((k,v) -> s2.put(k,v));		
				found.add(s2);
				System.out.println("---------------->Found: " + s2);
				addNode(s2);
				System.out.println("Adding Edge from " + current+ " to " +  s2);
				addEdge(current, s2);
			}
			
			
			
		}

		audited.add(current); // Current audit complete
		unaudited.remove(current);
		found.removeAll(audited);
		unaudited.addAll(found);

		System.out.println("********************************");
		System.out.println("Audited: " + audited);
		System.out.println("Found: " + found);
		System.out.println("Unaudited: " + unaudited);
		System.out.println("********************************");

		return true;
	}

	public void end() {
		// Nothing to do
	}

	/**
	 * Sends a node to the generator sink (graph)
	 * @param state
	 */
	protected void addNode(Tuple state) {

		if(!added.contains(state)) {
			//state.setId(Integer.toString(nodeID)); // set ID because nodes from founds have id: ""
			sendNodeAdded(sourceId, state + "");

			for (var entry : state.entrySet()) {
				this.sendNodeAttributeAdded(sourceId, state + "", entry.getKey(), entry.getValue());
			}

			nodeID++;
			added.add(state);
			System.out.println("Adding node " + state.toString());
		}
	}


	/**
	 * Sends an edge to the generator sink (graph)
	 * @param from
	 * @param to
	 */
	private void addEdge(Tuple from, Tuple to) {

		for(Tuple s: added) {
			if(s.equals(to)) {
				to = s;
			}
		}
		
		if(added.contains(from) && added.contains(to) && !from.equals(to)) {
			this.sendEdgeAdded(sourceId, Integer.toString(edgeID), from + "", to + "", true);

			edgeID++;
		}
	}
}