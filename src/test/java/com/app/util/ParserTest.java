package com.app.util;

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
		
		StringBuilder sb = new StringBuilder();
        sb.append("y!=x");
        sb.append("|");
        sb.append("y=5");
        sb.append("&");
        sb.append("  "); // whitespace
        sb.append("x=5");
        sb.append(":");
        sb.append("0");
        
        String transition = sb.toString();
		
		TransitionLine p = new TransitionLine(transition, new Variable("", 0, 0, 0, ""));
		String action = p.actionSubstring();
		
		Assertions.assertEquals("0", action);
		
		for(AtomicFormula a: p.atoms()) {
			//System.out.println(a);
		}
		
		Assertions.assertTrue(p.atoms().toString().contains("y!=x"));

	}
	
	@Test
	void simpleBlockTest() {
		
		StringBuilder sb = new StringBuilder();
        sb.append("y!=x|y=5&   x=5:0\n");
        sb.append("\n"); // newline
        sb.append("y=0&x=0:x+3");

        String block = sb.toString();
        Variable var = new Variable("x", 0, 0, 0, block);
        
        TransitionBlock b = new TransitionBlock(var);
	}
}
