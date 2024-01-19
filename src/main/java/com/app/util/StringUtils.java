package com.app.util;

import java.util.Arrays;
import java.util.Set;

public class StringUtils {

	public StringUtils() {
		// TODO Auto-generated constructor stub
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
