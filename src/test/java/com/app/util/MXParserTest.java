package com.app.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.License;
import org.mariuszgromada.math.mxparser.mXparser;
import org.mariuszgromada.math.mxparser.parsertokens.KeyWord;

import com.app.model.framework.AtomicFormula;

class MXParserTest {
	
	// Vorteil: Kann Konjunktoren und insb. Relationen
	
	@BeforeAll
	static void license() {
		License.iConfirmNonCommercialUse("Linus");
	}

	@Test
	void simpleCalculation() {
		
		Expression e = new Expression("2+1");
		Assertions.assertEquals(3, e.calculate());
	}

	@Test
	void variablesCalculation() {
		
		Argument longname = new Argument("longname");
		Argument y = new Argument("y",1);
		
		longname.setArgumentValue(1);
		Expression e = new Expression("2longname+3y",longname,y);
		Assertions.assertEquals(5, e.calculate());
	}
	
	@Test
	void simpleRelationCalculation(){
		Expression e = new Expression("¬(2<3)");	
		// 1.0d equals "true"
		Assertions.assertEquals(0, e.calculate());
	}

	
	@Test
	void RelationWithVariablesCalculation(){
		
		Argument x = new Argument("x");
		Argument y = new Argument("y",1);
		
		x.setArgumentValue(2);
		Expression e = new Expression("2x<3y & y=1",x,y);
		// 0.0d equals "true"
		Assertions.assertEquals(0, e.calculate());
		// Change variable so expression becomes "false"
		x.setArgumentValue(1);
		Assertions.assertEquals(1, e.calculate());
		// Change variable so expression becomes "true"
		y.setArgumentValue(2);
		Assertions.assertEquals(0, e.calculate());
	}
	
	@Test
	void getArguments(){
		
		Expression e = new Expression("2*x<3*y & reset=1");
		String[] expected = {"x", "y","reset"};
		
		e.disableImpliedMultiplicationMode();
		e.defineArguments(e.getMissingUserDefinedArguments());
		
		int argumentcount = e.getArgumentsNumber();
		String[] result = new String[e.getArgumentsNumber()];
		
		for(int i=0; i < argumentcount; i++) {
			result[i] = e.getArgument(i).getArgumentName();
		}
		
		Assertions.assertEquals(Arrays.deepToString(expected), Arrays.deepToString(result));
		
		
		int keycount = e.getKeyWords("typeid=101").size();
		String[] keys = new String[keycount];
		
		for(int i=0; i < keycount; i++) {
			keys[i] = e.getKeyWords("typeid=101").get(i).wordString;
		}
		
		Assertions.assertEquals(Arrays.deepToString(expected),Arrays.deepToString(keys));
	}
}
