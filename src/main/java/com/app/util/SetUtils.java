package com.app.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.app.model.framework.Tuple;


public class SetUtils {

	// By https://stackoverflow.com/a/714256
	public static Set<Set<Object>> cartesianProduct(Set<?>... sets) {


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

	public static <T> boolean intersect(Set<T> set1, Set<T> set2) {
		// Iterate over the elements of set1 and check if any element is present in set2
		for (T element : set1) {
			if (set2.contains(element)) {
				return true; // Intersection found
			}
		}
		return false; // No intersection found
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