package com.app.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.License;
import org.mariuszgromada.math.mxparser.mXparser;

class MXParserTest {
	
	// Vorteil: Kann Konjunktoren und insb. Relationen
	
	@BeforeAll
	static void license() {
		License.iConfirmNonCommercialUse("Linus");
	}

	@Test
	void simpleCalculation() {
		
		Expression e = new Expression("2+1");
		
		mXparser.consolePrintln("Res: " + e.getExpressionString() + " = " + e.calculate());		
		Assertions.assertEquals(3, e.calculate());
	}

	@Test
	void variablesCalculation() {
		
		Argument longname = new Argument("longname");
		Argument y = new Argument("y",1);
		
		longname.setArgumentValue(1);
		Expression e = new Expression("2longname+3y",longname,y);
		
		mXparser.consolePrintln("Res: " + e.getExpressionString() + " = " + e.calculate());
		Assertions.assertEquals(5, e.calculate());
	}
	
	@Test
	void simpleRelationCalculation(){
		Expression e = new Expression("2<3");
		
		mXparser.consolePrintln("Res: " + e.getExpressionString() + " = " + e.calculate());
		// 1.0d equals "true"
		Assertions.assertEquals(1, e.calculate());
	}
	
	@Test
	void RelationWithVariablesCalculation(){
		
		Argument x = new Argument("x");
		Argument y = new Argument("y",1);
		
		x.setArgumentValue(2);
		Expression e = new Expression("2x<3y & y=1",x,y);
		
		mXparser.consolePrintln("Res: " + e.getExpressionString() + " = " + e.calculate());
		// 0.0d equals "true"
		Assertions.assertEquals(0, e.calculate());
		// Change variable so expression becomes "false"
		x.setArgumentValue(1);
		Assertions.assertEquals(1, e.calculate());
		// Change variable so expression becomes "true"
		y.setArgumentValue(2);
		Assertions.assertEquals(0, e.calculate());
	}
	

}
