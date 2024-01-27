package com.app.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.License;

import com.app.model.framework.AtomicFormula;
import com.app.model.framework.FormulaCluster;
import com.app.model.framework.InitialAbstractionGenerator;
import com.app.model.framework.ModelManager;
import com.app.model.framework.Variable;
import com.app.model.graph.KripkeStruct;

class InitialAbstractionTest {
	
	@BeforeAll
	static void license() {
		License.iConfirmNonCommercialUse("Linus");
	}

	@Test
	void test() {
		List<Variable> variableList = new ArrayList<>();	
		Variable x = new Variable("x", 0, 0, 2, "r=0&x<y:x+1\n" + "r=1:x-1");
		Variable y = new Variable("y", 1, 0, 2, "x=y|x<y:y+1");
		Variable r = new Variable("r", 0, 0, 1, "x=y:1\n" + "y=2:0");
		
		variableList.add(x);
		variableList.add(y);
		variableList.add(r);
		
		
		ModelManager manager = new ModelManager();
		
		// Check loading variables
		manager.load(variableList.toArray(new Variable[0]));	
		Assertions.assertEquals(x, ModelManager.getVariable("x"), "failed to load variable x correctly");
		Assertions.assertEquals(y, ModelManager.getVariable("y"), "failed to load variable y correctly");
		Assertions.assertEquals(r, ModelManager.getVariable("r"), "failed to load variable r correctly");
		
		Map<String, Variable> variablesMap = new HashMap<String, Variable>();
		variablesMap.put("x", x);
		variablesMap.put("y", y);
		variablesMap.put("r", r);
		
		Assertions.assertEquals(variablesMap,  ModelManager.getvariablesMap(), "failed to load variable map correctly");
		
		Assertions.assertTrue(ModelManager.getTransitionBlockMap().containsKey("x"), "missing transition block for x");
		Assertions.assertTrue(ModelManager.getTransitionBlockMap().containsKey("y"), "missing transition block for y");
		Assertions.assertTrue(ModelManager.getTransitionBlockMap().containsKey("r"), "missing transition block for r");
		
		Assertions.assertEquals(3, ModelManager.getTransitionBlockMap().size(), "more transition blocks than expected");
		
		
		
		// Generating initial abstraction
		KripkeStruct graph = new KripkeStruct("Initial Abstraction");
		InitialAbstractionGenerator gen = new InitialAbstractionGenerator();
		gen.addSink(graph);
		gen.begin();
		
		
		AtomicFormula a1 = new AtomicFormula(new Expression("x=y"));
		AtomicFormula a2 = new AtomicFormula(new Expression("x<y"));
		AtomicFormula a3 = new AtomicFormula(new Expression("y=2"));	
	
		AtomicFormula a4 = new AtomicFormula(new Expression("r=1"));
		AtomicFormula a5 = new AtomicFormula(new Expression("r=0"));

		Assertions.assertTrue(gen.getFormulaClusters().get(0).getAtomicFormulas().contains(a1));
		Assertions.assertTrue(gen.getFormulaClusters().get(0).getAtomicFormulas().contains(a2));
		Assertions.assertTrue(gen.getFormulaClusters().get(0).getAtomicFormulas().contains(a3));
		
		Assertions.assertTrue(gen.getFormulaClusters().get(1).getAtomicFormulas().contains(a4));
		Assertions.assertTrue(gen.getFormulaClusters().get(1).getAtomicFormulas().contains(a5));
		
		gen.nextEvents();
		gen.end();
		
	}

}
