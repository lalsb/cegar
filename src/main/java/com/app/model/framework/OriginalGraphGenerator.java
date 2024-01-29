package com.app.model.framework;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.stream.SourceBase;

public class OriginalGraphGenerator extends SourceBase
implements Generator {

	/**
	 * Map of variable ids as keys with corresponding values e.g. a 'state'
	 */
	Tuple current;

	Iterator<Tuple> i;

	Set<Tuple> audited;
	Set<Tuple> unaudited;
	Set<Tuple> found;
	Set<Tuple> added;

	public OriginalGraphGenerator() {
		super();
		// this.addSink(new ConsoleSink());

		audited = new HashSet<Tuple>();
		unaudited = new HashSet<Tuple>();
		found = new HashSet<Tuple>();
		added = new HashSet<Tuple>();
	}

	public void begin() {
		current = new Tuple(); // Create state
		
		ModelManager.getvariablesMap().forEach((k,v) -> {
			current.put(k, v.getValue());
			});
		
		
		unaudited.add(current);
		
		System.out.println("\nGenerating original graph, starting with current: " + current + " and unaudited: " + unaudited + "\n");
	}

	public boolean nextEvents() {

		i = unaudited.iterator();

		if(!i.hasNext()) {
			return false;
		}
		
		current = i.next();
		System.out.println("Current: " + current);
		System.out.println("Audited: " + audited);
		
		for(Tuple result: ModelManager.getImage(current)){
			
			found.add(result);
			addNode(result);
			addEdge(current, result);
		}

		audited.add(current); // Current audit complete
		unaudited.remove(current);
		
		
		found.removeAll(audited);
		unaudited.addAll(found);
		
		System.out.println("Found: " + found);
		System.out.println("Unaudited: " + unaudited);
		System.out.println("********************************");

		
		found.clear();
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
			sendNodeAdded(sourceId, state + "");

			for (var entry : state.entrySet()) {
				this.sendNodeAttributeAdded(sourceId, state + "", entry.getKey(), entry.getValue());
			}

			ModelManager.nodeId++;
			added.add(state);
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
			this.sendEdgeAdded(sourceId, Integer.toString(ModelManager.edgeId), from + "", to + "", true);

			ModelManager.edgeId++;
		}
	}
}