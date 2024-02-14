package com.app.model;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.junit.jupiter.api.Test;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.License;

import com.app.model.framework.AtomicFormula;
import com.app.model.framework.InitialAbstractionGenerator;
import com.app.model.framework.OriginalGraphGenerator;
import com.app.model.framework.TransitionBlock;
import com.app.model.framework.TransitionLine;
import com.app.model.framework.Tuple;
import com.app.model.framework.ModelManager;
import com.app.model.framework.Variable;
import com.app.model.graph.KripkeStruct;

import javafx.util.Pair;

class SequentialIncrementTest {


	// Reusable attributes
	List<Variable> variables;
	List<Tuple> exp;
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

		TransitionBlock br = new TransitionBlock("r", 
				new TransitionLine("r", "1", Arrays.asList("0", "1")));

		TransitionBlock bx = new TransitionBlock("x", 
				new TransitionLine("x", "r=1", Arrays.asList("0")),
				new TransitionLine("x", "x<y", Arrays.asList("x+1")),
				new TransitionLine("x", "x=y", Arrays.asList("0")),
				new TransitionLine("x", "1", Arrays.asList("x")));

		TransitionBlock by = new TransitionBlock("y",
				new TransitionLine("y", "r=1", Arrays.asList("0")),
				new TransitionLine("y", "x=y & ~(y=2)", Arrays.asList("y+1")),
				new TransitionLine("y", "x=y", Arrays.asList("0")),
				new TransitionLine("y", "1", Arrays.asList("y")));

		r = new Variable("r", new HashSet<Double>(Arrays.asList(0d)),
				new HashSet<Double>(Arrays.asList(0d, 1d)),
				br);

		x = new Variable("x", 
				new HashSet<Double>(Arrays.asList(0d)),
				new HashSet<Double>(Arrays.asList(0d, 1d, 2d)),
				bx);

		y = new Variable("y",
				new HashSet<Double>(Arrays.asList(1d)),
				new HashSet<Double>(Arrays.asList(0d, 1d, 2d)),
				by);

		variables = new ArrayList<>();
		variables.add(x);
		variables.add(y);
		variables.add(r);

		manager = new ModelManager();
		manager.load(variables.toArray(new Variable[0]));	

		ModelManager.nodeId = 0;
		ModelManager.edgeId = 0;

		// Total of 12 tuples to be expected in reachable graph
		exp = Arrays.asList(
				new Tuple(Map.of("r", 0d, "x", 0d, "y", 1d)),
				new Tuple(Map.of("r", 0d, "x", 1d, "y", 1d)),
				new Tuple(Map.of("r", 0d, "x", 0d, "y", 2d)),
				new Tuple(Map.of("r", 1d, "x", 0d, "y", 2d)),
				new Tuple(Map.of("r", 0d, "x", 1d, "y", 2d)),
				new Tuple(Map.of("r", 1d, "x", 1d, "y", 2d)),
				new Tuple(Map.of("r", 0d, "x", 2d, "y", 2d)),
				new Tuple(Map.of("r", 1d, "x", 2d, "y", 2d)),
				new Tuple(Map.of("r", 1d, "x", 0d, "y", 0d)),
				new Tuple(Map.of("r", 1d, "x", 1d, "y", 1d)),
				new Tuple(Map.of("r", 1d, "x", 0d, "y", 1d)),
				new Tuple(Map.of("r", 0d, "x", 0d, "y", 0d))
				);
	}

	@Test
	void testOriginalGraphGenerator() {

		KripkeStruct graph = new KripkeStruct("Initial Abstraction");
		OriginalGraphGenerator gen = new OriginalGraphGenerator();

		gen.addSink(graph);
		gen.begin();
		while(gen.nextEvents());
		gen.end();

		List<Node> nodes = graph.nodes().collect(Collectors.toList());
		Map<Tuple, Node> actual = new HashMap<Tuple, Node>();
		nodes.forEach(node -> {
			actual.put((Tuple) node.getAttribute("value"), node);
		});

		// Verify states
		Assertions.assertTrue(!actual.isEmpty());
		Assertions.assertEquals(exp.size(), actual.keySet().size());
		for(Tuple tuple: exp) {
			Assertions.assertTrue(
					actual.containsKey(tuple), "Graph is missing tuple " + tuple);
		}

		// Verify initial states by sampling
		Assertions.assertTrue(
				new Tuple(Map.of("r", 0d, "x", 0d, "y", 1d)).
				isInitial(), "Initial tuple not marked as such.");
		Assertions.assertFalse(
				new Tuple(Map.of("r", 1d, "x", 1d, "y", 2d))
				.isInitial(), "Regular tuple marked as initial tuple.");


		// Verify edges
		List<int[]> edg = Arrays.asList(

				new int[]{0, 1}, /*001 to 011*/
				new int[]{0, 9},
				new int[]{1, 2},
				new int[]{1, 3},
				new int[]{2, 4},
				new int[]{2, 5},
				new int[]{3, 8},
				new int[]{3, 11},
				new int[]{4, 6},
				new int[]{4, 7},
				new int[]{5, 8},
				new int[]{5, 11},
				new int[]{6, 8},
				new int[]{6, 11},
				new int[]{7, 8},
				new int[]{7, 11},
				new int[]{8, 8},
				new int[]{8, 11},
				new int[]{9, 8},
				new int[]{9, 11},
				new int[]{10, 8},
				new int[]{10, 11},
				new int[]{11, 0},
				new int[]{11, 10}
				);

		for(int[] pair: edg) {
			assertThat(graph.getNode(exp.get(pair[0]))
					.hasEdgeToward(graph.getNode(exp.get(pair[1]))))
			.as("Couldn't find any edge from tuple" + exp.get(pair[0]) + " towards tuple " + exp.get(pair[1]) + " as is required.")
			.isTrue();
		}
	}


	@Test
	void testInitialAbstractionGraphGenerator() {

		// Generating visualization
		KripkeStruct graph = new KripkeStruct("Initial Abstraction");
		InitialAbstractionGenerator gen = new InitialAbstractionGenerator();

		gen.addSink(graph);
		gen.begin();
		while(gen.nextEvents());
		gen.end();


		// Verify clusters e.g. states
		List<int[]> cluster = Arrays.asList(

				new int[]{0},
				new int[]{1, 11},
				new int[]{2, 4},
				new int[]{3, 5},
				new int[]{6},
				new int[]{7},
				new int[]{8, 9},
				new int[]{10}
				);

		assertThat(graph.nodes().count()).isEqualTo(10); // Only 8 reachable

		for(int[] c0: cluster) {

			assertThat(graph.getNode(exp.get(c0[0])))
			.as("Tuple " + exp.get(c0[0]) + " does not have a coresponding abstract state.")
			.isNotNull();

			for(int i=1; i < c0.length; i++) {
				assertThat(graph.getNode(exp.get(c0[0])))
				.as("Tuple " + exp.get(c0[0]) + " and " + exp.get(c0[i]) + "do not have the same abstract state as required.")
				.isEqualTo(graph.getNode(exp.get(c0[i])));
			}
		}

		// Verify edges
		List<Node> nds = cluster.stream().
				map(c0 -> graph.getNode(exp.get(c0[0]))).collect(Collectors.toList());

		List<Node[]> edg = Arrays.asList(
				new Node[] {nds.get(0), nds.get(1), nds.get(6)},
				new Node[] {nds.get(1), nds.get(2), nds.get(3), nds.get(7), nds.get(0)},
				new Node[] {nds.get(2), nds.get(2), nds.get(4), nds.get(5)},
				new Node[] {nds.get(3), nds.get(6), nds.get(1)},
				new Node[] {nds.get(4), nds.get(6), nds.get(1)},
				new Node[] {nds.get(5), nds.get(6), nds.get(1)},
				new Node[] {nds.get(6), nds.get(6), nds.get(1)},
				new Node[] {nds.get(7), nds.get(6), nds.get(1)}
				);

		assertThat(graph.edges().count()).isEqualTo(24); // Only 19 reachable

		for(Node[] n: edg) {
			for(int i = 1; i < n.length; i++) {
				assertThat(n[0].hasEdgeToward(n[i]))
				.as("Couldn't find any edge from tuple" + n[0] + " towards tuple " +  n[i] + " as is required.").isTrue();
			}
		}

		// Verify path validation
		List<List<Integer>> valIds = Arrays.asList(	
				Arrays.asList(0, 1, 7, 6, 1, 0, 1),
				Arrays.asList(0, 1, 2, 2, 2, 2, 2, 3, 1),
				Arrays.asList(0, 1, 2, 4, 1, 0),
				Arrays.asList(0, 1, 2, 4, 1, 0)
				);	 // Valid paths
		assertThat(toIds(valIds, nds)).allSatisfy(pth -> {
			assertThat(manager.isValid(pth, graph)).isTrue();
		});

		List<List<Integer>> invalIds = Arrays.asList(	
				Arrays.asList(0, 1, 7, 6, 1, 0, 1, 6),
				Arrays.asList(0, 1, 2, 2, 2, 2, 2, 3, 1, 5),
				Arrays.asList(0, 1, 2, 4, 5, 1 , 0),
				Arrays.asList(0, 1, 3, 3, 6)
				); // Invalid path
		assertThat(toIds(invalIds, nds)).allSatisfy(pth -> {
			assertThat(manager.isValid(pth, graph)).isFalse();
		});

		// Load graph
		manager.setAbstractionGraph(graph);

		// Verify path split path algorithm
		List<List<Integer>> valcIds = Arrays.asList(	
				Arrays.asList(0, 1, 2, 2, 4, 1, 0, 6),
				Arrays.asList(0, 1, 3, 6, 1),
				Arrays.asList(0, 6, 1, 7, 1, 0),
				Arrays.asList(0, 1, 2, 2, 5)
				); // Real counterexamples
		assertThat(toIds(valcIds, nds)).allSatisfy(pth -> {
			assertThat(manager.isValid(pth, graph)).isTrue();
			assertThat(manager.splitPath(pth)).isNull();
		});
		
		List<List<Integer>> invalcIds = Arrays.asList(	
				Arrays.asList(0, 1, 2, 4, 1, 0, 6),
				Arrays.asList(0, 6, 1, 3),
				Arrays.asList(0, 1, 2, 5)
				); // Spurious counterexamples
		assertThat(toIds(invalcIds, nds)).allSatisfy(pth -> {
			assertThat(manager.isValid(pth, graph)).isTrue();
			assertThat(manager.splitPath(pth)).isNotNull();
		});

		assertThat(manager.splitPath(toIds(invalcIds, nds).get(0)).getKey()).isEqualTo(nds.get(2).getId());
		assertThat(manager.splitPath(toIds(invalcIds, nds).get(0)).getValue()).containsExactlyInAnyOrder(exp.get(2));

		assertThat(manager.splitPath(toIds(invalcIds, nds).get(1)).getKey()).isEqualTo(nds.get(1).getId());
		assertThat(manager.splitPath(toIds(invalcIds, nds).get(1)).getValue()).containsExactlyInAnyOrder(exp.get(11));

		assertThat(manager.splitPath(toIds(invalcIds, nds).get(4)).getKey()).isEqualTo(nds.get(2).getId());
		assertThat(manager.splitPath(toIds(invalcIds, nds).get(2)).getValue()).containsExactlyInAnyOrder(exp.get(2));

		// Verify refinement algorithm
		for(int i: new int[]{2}) {
			Pair<String, Set<Tuple>> ret = manager.splitPath(toIds(invalcIds, nds).get(i));
			manager.refine(ret.getKey(), ret.getValue());
		}




	}

	private List<List<String>> toIds(List<List<Integer>> indices, List<Node> nodeTable){

		return indices.stream()
				.map(list -> list.stream().map(id -> nodeTable.get(id).getId())
						.collect(Collectors.toList()))
				.collect(Collectors.toList());
	}

	@Test
	void testLoadVariables() {

		// Verify variables are being loaded properly
		Assertions.assertEquals(x,
				ModelManager.getVariable("x"), "failed to load variable x correctly.");
		Assertions.assertEquals(y,
				ModelManager.getVariable("y"), "failed to load variable y correctly.");
		Assertions.assertEquals(r,
				ModelManager.getVariable("r"), "failed to load variable r correctly.");

		Map<String, Variable> exp = Map.of("r", r, "x", x, "y", y);

		Assertions.assertEquals(exp,
				ModelManager.getvariablesMap(), "failed to load variable map correctly.");


		assertThat(ModelManager.getTransitionBlockMap().keySet()).containsExactlyInAnyOrder("r", "x", "y");

		// Verify initial states are set
		assertThat(ModelManager.getInitialTuples()).containsExactlyInAnyOrder(new Tuple(Map.of("r", 0d, "x", 0d, "y", 1d)));

		// Verify formula clusters are generated properly

		InitialAbstractionGenerator gen = new InitialAbstractionGenerator();
		gen.begin();
		AtomicFormula[] expf = {
				new AtomicFormula(new Expression("x=y")),
				new AtomicFormula(new Expression("x<y")),
				new AtomicFormula(new Expression("y=2")),
				new AtomicFormula(new Expression("r=1")),
		};

		int cc = gen.getFormulaClusters().size();

		Assertions.assertEquals(2, cc);

		for(int i: Arrays.asList(0,1))
			assertThat(gen.getFormulaClusters().get(i)).
			extracting(formulacluster -> formulacluster.getAtomicFormulas()).satisfiesAnyOf(
					list -> assertThat(list).containsExactlyInAnyOrder(expf[0], expf[1], expf[2]),
					list -> assertThat(list).containsExactlyInAnyOrder(expf[3])
					);
	}

	@Test
	void testgetImage() {
		
		Set<Tuple> input = new HashSet<Tuple>();
		Tuple test = new Tuple();
		test.put("r", 0d);
		test.put("x", 0d);
		test.put("y", 1d);
		input.add(test);

		Set<Tuple> expected = new HashSet<Tuple>();
		Tuple v1 = new Tuple();
		v1.put("r", 0d);
		v1.put("x", 1d);
		v1.put("y", 1d);
		expected.add(v1);
		Tuple v2 = new Tuple();
		v2.put("r", 1d);
		v2.put("x", 1d);
		v2.put("y", 1d);
		expected.add(v2);

		assertThat(expected).isEqualTo(ModelManager.getImage(input));
	}

}
