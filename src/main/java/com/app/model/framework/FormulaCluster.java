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
		variableIds.addAll(formula.getAllVariableIds());	
		return atomicFormulas.add(formula);
	}
	
	public List<String> getAllVariableIds(){
		return variableIds;
	}

	public Set<Set<Tuple>> generatePartition() {
		
		// Generate Map
		variableValues = new HashMap<>();
		System.out.println(variableIds);
		
		for(String id: variableIds) {
			variableValues.put(id, ModelManager.getVariable(id).getDomain());
		}

		// Generate all tuples
		Set<Tuple> allTuples = generateTuples();
		
		System.out.println("All Tuples:");
		for(Tuple t: allTuples) {
			System.out.println(t);
		}
		
		
		// Partition all tuples
		Map<Boolean[], Set<Tuple>> partitionMap = new HashMap<Boolean[], Set<Tuple>>();
		
		for(Tuple current: allTuples) {
			
			Boolean[] result = Arrays.stream(atomicFormulas.toArray()).map(formula -> ((AtomicFormula) formula).audit(current)).toArray(Boolean[]::new);
			
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
