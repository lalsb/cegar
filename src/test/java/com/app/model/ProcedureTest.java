package com.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.graphstream.graph.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.License;

import com.app.model.framework.AtomicFormula;
import com.app.model.framework.FormulaCluster;
import com.app.model.framework.InitialAbstractionGenerator;
import com.app.model.framework.OriginalGraphGenerator;
import com.app.model.framework.TransitionBlock;
import com.app.model.framework.TransitionLine;
import com.app.model.framework.Tuple;
import com.app.model.framework.ModelManager;
import com.app.model.framework.Variable;
import com.app.model.graph.ConsoleSink;
import com.app.model.graph.KripkeStruct;

import static java.util.Map.entry;

class ProcedureTest {


	List<Variable> variableList;
	Variable x;
	Variable y;
	Variable r;
	ModelManager manager;

	@BeforeAll
	static void license() {
		License.iConfirmNonCommercialUse("Linus");
	}

	@BeforeEach
	void generateVariableList() {

		TransitionLine lx1 = new TransitionLine("x", "r=0&x<y", "x+1");
		TransitionLine lx2 = new TransitionLine("x", "r=1", "x-1");	
		TransitionLine ly1 = new TransitionLine("y", "x=y|x<y", "y+1");
		TransitionLine lr1 = new TransitionLine("r", "x=y", "1");
		TransitionLine lr2 = new TransitionLine("r", "y=2", "0");

		TransitionBlock bx = new TransitionBlock("x", lx1, lx2);
		TransitionBlock by = new TransitionBlock("y", ly1);
		TransitionBlock br = new TransitionBlock("r", lr1, lr2);

		x = new Variable("x", new HashSet<Double>(Arrays.asList(0d)), new HashSet<Double>(Arrays.asList(0d, 1d, 2d)), bx);
		y = new Variable("y", new HashSet<Double>(Arrays.asList(1d)), new HashSet<Double>(Arrays.asList(0d, 1d, 2d)), by);
		r = new Variable("r", new HashSet<Double>(Arrays.asList(0d)), new HashSet<Double>(Arrays.asList(0d, 1d)), br);

		Arrays.asList(0d, 1d, 2d);

		variableList = new ArrayList<>();

		variableList.add(x);
		variableList.add(y);
		variableList.add(r);

		manager = new ModelManager();
		manager.load(variableList.toArray(new Variable[0]));	
		
		ModelManager.nodeId = 0;
		ModelManager.edgeId = 0;
	}

	@Test
	void testOriginalGraphGenerator() {

		int i = 0;
		
		// Generating visualization
		KripkeStruct graph = new KripkeStruct("Initial Abstraction");
		graph.setStrict(true);
		OriginalGraphGenerator gen = new OriginalGraphGenerator();
		gen.addSink(graph);
		gen.begin();
		while(gen.nextEvents() && i < 20) {i++;}
		gen.end();

		List<Node> actual = graph.nodes().collect(Collectors.toList());
		
		Map<Tuple, Node> actualsMap = new HashMap<Tuple, Node>();
		actual.forEach(node -> {
			actualsMap.put((Tuple) node.getAttribute("value"), node);
		});
		
		Tuple exp0 = new Tuple(Map.of("x", 0d, "y", 1d, "r", 0d));
		Tuple exp1 = new Tuple(Map.of("x", 1d, "y", 1d, "r", 0d));
		Tuple exp2 = new Tuple(Map.of("x", 0d, "y", 2d, "r", 0d));
		Tuple exp3 = new Tuple(Map.of("x", 1d, "y", 2d, "r", 0d));		
		Tuple exp4 = new Tuple(Map.of("x", 1d, "y", 1d, "r", 1d));	
		Tuple exp5 = new Tuple(Map.of("x", 2d, "y", 2d, "r", 0d));	
		Tuple exp6 = new Tuple(Map.of("x", 0d, "y", 1d, "r", 1d));
		Tuple exp7 = new Tuple(Map.of("x", 1d, "y", 2d, "r", 1d));
		Tuple exp8 = new Tuple(Map.of("x", 2d, "y", 2d, "r", 1d));
		Tuple exp9 = new Tuple(Map.of("x", 0d, "y", 2d, "r", 1d));

		List<Tuple> expected = new ArrayList<Tuple>(Arrays.asList(exp0, exp1, exp2, exp3, exp4, exp5, exp6, exp7, exp8, exp9));
		
		// Check states
		Assertions.assertTrue(!actualsMap.isEmpty());
		Assertions.assertEquals(expected.size(), actualsMap.keySet().size());
		for(Tuple exp: expected) {
			Assertions.assertTrue(actualsMap.containsKey(exp), "Graph is missing tuple " + exp);
		}
		
		// Check initial states by sampling
		Assertions.assertTrue(exp0.isInitial(), "Initial tuple not marked as such.");
		Assertions.assertFalse(exp7.isInitial(), "Regular tuple marked as initial tuple.");
		
		// Check edges
		graph.removeEdge(actualsMap.get(exp0), actualsMap.get(exp1));
		graph.removeEdge(actualsMap.get(exp0), actualsMap.get(exp2));
		
		graph.removeEdge(actualsMap.get(exp1), actualsMap.get(exp3));
		graph.removeEdge(actualsMap.get(exp1), actualsMap.get(exp4));
		
		graph.removeEdge(actualsMap.get(exp2), actualsMap.get(exp3));
		
		graph.removeEdge(actualsMap.get(exp3), actualsMap.get(exp5));
		
		graph.removeEdge(actualsMap.get(exp4), actualsMap.get(exp6));
		graph.removeEdge(actualsMap.get(exp4), actualsMap.get(exp7));
		
		graph.removeEdge(actualsMap.get(exp5), actualsMap.get(exp8));
		
		graph.removeEdge(actualsMap.get(exp6), actualsMap.get(exp9));
		
		graph.removeEdge(actualsMap.get(exp7), actualsMap.get(exp3));
		graph.removeEdge(actualsMap.get(exp7), actualsMap.get(exp9));
		
		graph.removeEdge(actualsMap.get(exp8), actualsMap.get(exp5));
		graph.removeEdge(actualsMap.get(exp8), actualsMap.get(exp7));
		
		graph.removeEdge(actualsMap.get(exp9), actualsMap.get(exp2));
	}


	@Test
	void testInitialAbstractionGenerator() {

		// Variables loaded correctly
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
		graph.addSink(new ConsoleSink(graph));
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
		
		List<String> realFinitePath = new ArrayList<String>();
		
		
		Tuple exp0 = new Tuple(Map.of("x", 0d, "y", 1d, "r", 0d));
		Tuple exp1 = new Tuple(Map.of("x", 1d, "y", 1d, "r", 0d));
		Tuple exp2 = new Tuple(Map.of("x", 0d, "y", 2d, "r", 0d));
		Tuple exp3 = new Tuple(Map.of("x", 1d, "y", 2d, "r", 0d));		
		Tuple exp4 = new Tuple(Map.of("x", 1d, "y", 1d, "r", 1d));	
		Tuple exp5 = new Tuple(Map.of("x", 2d, "y", 2d, "r", 0d));	
		Tuple exp6 = new Tuple(Map.of("x", 0d, "y", 1d, "r", 1d));
		Tuple exp7 = new Tuple(Map.of("x", 1d, "y", 2d, "r", 1d));
		Tuple exp8 = new Tuple(Map.of("x", 2d, "y", 2d, "r", 1d));
		Tuple exp9 = new Tuple(Map.of("x", 0d, "y", 2d, "r", 1d));
		
		realFinitePath.add(graph.getNode(exp0).getId());
		realFinitePath.add(graph.getNode(exp3).getId());
		realFinitePath.add(graph.getNode(exp5).getId());
	
		Assertions.assertTrue(manager.isValid(realFinitePath, graph));
	}


	@Test
	void testgetImage() {

		Set<Tuple> input = new HashSet<Tuple>();
		Tuple test = new Tuple();
		test.put("x", 0d);
		test.put("y", 1d);
		test.put("r", 0d);
		input.add(test);

		Set<Tuple> expected = new HashSet<Tuple>();
		Tuple v1 = new Tuple();
		v1.put("x", 1d);
		v1.put("y", 1d);
		v1.put("r", 0d);
		expected.add(v1);
		Tuple v2 = new Tuple();
		v2.put("x", 0d);
		v2.put("y", 2d);
		v2.put("r", 0d);
		expected.add(v2);
		Tuple v3 = new Tuple();
		v3.put("x", 1d);
		v3.put("y", 2d);
		v3.put("r", 0d);
		expected.add(v3);

		Assertions.assertEquals(expected, ModelManager.getImage(input));

	}

}