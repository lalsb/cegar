package com.app.util;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.app.model.framework.AtomicFormula;
import com.app.model.framework.TransitionBlock;
import com.app.model.framework.TransitionLine;
import com.app.model.framework.Variable;

class ParserTest {

	@Test
	void simpleTransitionTest() {
		
		TransitionLine p = new TransitionLine("x", "y!=x|y=5&x=5", "0");
		String condition = p.conditionSubstring();
		String action = p.actionSubstring();
		
		Assertions.assertEquals("y!=x|y=5&x=5", condition);
		Assertions.assertEquals("0", action);
		Assertions.assertTrue(p.atoms().toString().contains("y!=x"));

	}
	
	@Test
	void simpleBlockTest() {
		
		TransitionLine lx1 = new TransitionLine("x", "r=0&x<y", "x+1");
		TransitionLine lx2 = new TransitionLine("x", "r=1", "x-1");	
		TransitionLine ly1 = new TransitionLine("y", "x=y|x<y", "y+1");
		TransitionLine lr1 = new TransitionLine("r", "x=y", "1");
		TransitionLine lr2 = new TransitionLine("r", "y=2", "0");
		
		TransitionBlock bx = new TransitionBlock("x", lx1, lx2);
		TransitionBlock by = new TransitionBlock("y", ly1);
		TransitionBlock br = new TransitionBlock("r", lr1, lr2);
		
		Variable x = new Variable("x", new HashSet<Double>(Arrays.asList(0d)), new HashSet<Double>(Arrays.asList(0d, 1d, 2d)), bx);
		Variable y = new Variable("y", new HashSet<Double>(Arrays.asList(1d)), new HashSet<Double>(Arrays.asList(0d, 1d, 2d)), by);
		Variable r = new Variable("r", new HashSet<Double>(Arrays.asList(0d)), new HashSet<Double>(Arrays.asList(0d, 1d)), br);
		
		Assertions.assertEquals(bx, x.getTransitionBlock());
		Assertions.assertEquals(by, y.getTransitionBlock());
		Assertions.assertEquals(br, r.getTransitionBlock());
	}
}
