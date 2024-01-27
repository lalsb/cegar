package com.app.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.app.model.framework.Tuple;

public class SetUtils {
	
	public static Set<Set<Set<Tuple>>> cartesianProduct(List<Set<Set<Tuple>>> sets) {
	    if (sets.size() < 2)
	        throw new IllegalArgumentException(
	                "Can't have a product of fewer than two sets (got " +
	                sets.size() + ")");

	    return _cartesianProduct(0, sets);
	}

	private static Set<Set<Set<Tuple>>> _cartesianProduct(int index, List<Set<Set<Tuple>>> sets) {
	    Set<Set<Set<Tuple>>> ret = new HashSet<Set<Set<Tuple>>>();
	    if (index == sets.size()) {
	        ret.add(new HashSet<Set<Tuple>>());
	    } else {
	        for (Set<Tuple> obj : sets.get(index)) {
	            for (Set<Set<Tuple>> set : _cartesianProduct(index+1, sets)) {
	                set.add(obj);
	                ret.add(set);
	            }
	        }
	    }
	    return ret;
	}

	public static Set<Set<Object>> cartesianProduct(Set<?>... sets) {
	    if (sets.length < 2)
	        throw new IllegalArgumentException(
	                "Can't have a product of fewer than two sets (got " +
	                sets.length + ")");

	    return _cartesianProduct(0, sets);
	}
	
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

}