package com.app.model.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormulaCluster {
	
	public int nodeID;
	
	private List<AtomicFormula> atomicFormulas;
	private List<String> variableIds;
	private Map<String, List<Double>> variableValues;
	
	public FormulaCluster() {
		atomicFormulas = new ArrayList<AtomicFormula>();
		variableIds = new ArrayList<String>();
	}
	
	public boolean addFormula(AtomicFormula formula) {
		
		if(!atomicFormulas.contains(formula)) {
			atomicFormulas.add(formula);
		}
		
		for(String id: formula.getAllVariableIds()) {
			if(!variableIds.contains(id)) {
				variableIds.add(id);
			}
		}
		
		return true;
	}
	
	public List<String> getAllVariableIds(){
		return variableIds;
	}
	
	public List<AtomicFormula> getAtomicFormulas(){
		return atomicFormulas;
	}

	public Set<Set<Tuple>> generatePartition() {
		System.out.println("\nGenerating partition for cluster: " + variableIds + " with formulas: " + atomicFormulas);
		
		// Generate Map
		variableValues = new HashMap<>();
		
		for(String id: variableIds) {
			variableValues.put(id, ModelManager.getVariable(id).getDomain());
		}
		
		// Generate all tuples
		Set<Tuple> allTuples = generateTuples();
		
		// Partition all tuples
		Map<List<Boolean>, Set<Tuple>> partitionMap = new HashMap<List<Boolean>, Set<Tuple>>();
		
		for(Tuple current: allTuples) {
			
			List<Boolean> result = new ArrayList<Boolean>();
			atomicFormulas.forEach(x -> result.add(x.audit(current)));
			
			if (partitionMap.containsKey(result)) {
				partitionMap.get(result).add(current);
			} else {
				partitionMap.put(result, new HashSet<Tuple>(Arrays.asList(current)));
			}
		}
		
		return new HashSet<Set<Tuple>>(partitionMap.values());
		
	}

	/**
	 * Generates tuples representing all possible combinations of double values for the given variables.
	 * @return a set of tuples, where each tuple represents a combination of double values for the variables.
	 */
	private Set<Tuple> generateTuples() {
		
        Set<Tuple> tuples = new HashSet<>();

        if (variableIds.isEmpty()) {
            return tuples;
        }

        List<String> sortedVariableIds = new ArrayList<>(variableIds);
        Collections.sort(sortedVariableIds); // Sorting variable IDs for consistency

        int[] indices = new int[sortedVariableIds.size()];

        do {
            Tuple tuple = new Tuple();
            for (int i = 0; i < sortedVariableIds.size(); i++) {
                String variableId = sortedVariableIds.get(i);
                List<Double> valuesForVariable = variableValues.get(variableId);
                if (valuesForVariable != null && indices[i] < valuesForVariable.size()) {
                    tuple.put(variableId, valuesForVariable.get(indices[i]));
                }
            }
            tuples.add(tuple);
        } while (incrementIndices(indices));

        return tuples;
    }
	
	public String toString() {
		
		String result = "[ ";
		for(AtomicFormula a: atomicFormulas) {
			result += a.toString() + ", ";
		}
		result += "]";
		return result;
	}

	/**
    * Increments the indices array, simulating counting with carry for variable value combinations.
    *
    * @param indices the array of indices to be incremented.
    * @return true if the indices were successfully incremented, false if all indices have reached their maximum values.
    */
    private boolean incrementIndices(int[] indices) {
        int i = indices.length - 1;
        while (i >= 0 && indices[i] == variableValues.get(variableIds.get(i)).size() - 1) {
            indices[i] = 0;
            i--;
        }

        if (i < 0) {
            return false; // All indices have reached their maximum values
        }

        indices[i]++;
        return true;
    }

}
