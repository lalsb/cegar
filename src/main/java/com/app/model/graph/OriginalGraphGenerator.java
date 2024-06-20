package com.app.model.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OriginalGraphGenerator implements KStructGenerator<Tuple> {

	
	OriginalStruct struct;
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
		audited = new HashSet<Tuple>();
		unaudited = new HashSet<Tuple>();
		found = new HashSet<Tuple>();
		added = new HashSet<Tuple>();
	}
	
	
	public OriginalStruct generateStruct() {
		
		struct = new OriginalStruct();
		begin();
		while(nextEvents());
		System.out.println("generated original graph");
		return struct;
	}

	public void begin() {
		
		unaudited.addAll(ModelManager.getInitialTuples()); // Get initial tuples
		
	}

	public boolean nextEvents() {

		i = unaudited.iterator();

		if(!i.hasNext()) {
			return false;
		}
		
		current = i.next();
		addNode(current);
		
		for(Tuple result: ModelManager.getImage(current)){
			
			found.add(result);
			addNode(result);
			addEdge(current, result);
		}

		audited.add(current); // Current audit complete
		unaudited.remove(current);
		
		
		found.removeAll(audited);
		unaudited.addAll(found);
		
		found.clear();
		
		return true;
	}


	/**
	 * Sends a node to the generator sink (graph)
	 * @param tuple
	 */
	protected void addNode(Tuple tuple) {

		if(!added.contains(tuple) && !tuple.isEmpty()) {
			tuple.setId(Integer.toString(ModelManager.nodeId++));
			struct.insertVertex(tuple);
			added.add(tuple);
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
		
		if(added.contains(from) && added.contains(to)) {
			struct.insertEdge(from, to,  Integer.toString(ModelManager.edgeId++));
		}
	}
}