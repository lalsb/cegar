package com.app.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class SetUtils {

	// By https://stackoverflow.com/a/714256
	public static Set<Set<Object>> cartesianProduct(Set<?>... sets) {
	    if (sets.length < 2)
	        throw new IllegalArgumentException(
	                "Can't have a product of fewer than two sets (got " +
	                sets.length + ")");

	    return _cartesianProduct(0, sets);
	}
	
	// By https://stackoverflow.com/a/714256
	private static Set<Set<Object>> _cartesianProduct(int index, Set<?>... sets) {
	    Set<Set<Object>> ret = new HashSet<Set<Object>>();
	    if (index == sets.length) {
	        ret.add(new HashSet<Object>());
	    } else {
	        for (Object obj : sets[index]) {
	            for (Set<Object> set : _cartesianProduct(index+1, sets)) {
	                set.add(obj);
	                ret.add(set);
	            }
	        }
	    }
	    return ret;
	}
	
	// Utility method to convert Set to String
    public static <T> String setToString(Set<T[]> set) {
    	
        StringBuilder result = new StringBuilder("[");
        for (T[] element : set) {
            result.append(Arrays.deepToString(element)).append(", ");
        }
        if (!set.isEmpty()) {
            result.setLength(result.length() - 2); // Remove the trailing comma and space
        }
        result.append("]");
        return result.toString();
    }

}