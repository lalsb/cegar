package com.app.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class PredicateSplitterTest {
	
	
	@Test
	void SequentialIncrementTest() {
	
		assertThat(PredicateSplitter.splitPredicate("r=1"))
		 .containsExactlyInAnyOrder("r=1");
		
		assertThat(PredicateSplitter.splitPredicate("x<y"))
		 .containsExactlyInAnyOrder("x<y");
		
		 assertThat(PredicateSplitter.splitPredicate("x=y & ~ (y=2)"))
		 .containsExactlyInAnyOrder("x=y", "y=2");
		 
		 assertThat(PredicateSplitter.splitPredicate("1"))
		 .containsExactlyInAnyOrder();

	}
	
	@Test
	void TrafficLightTest() {
		// TODO: Add test
	}
	
	@Test
	void MicrowaveTest() {

		assertThat(PredicateSplitter.splitPredicate("t=r | t=g | t=y"))
		 .containsExactlyInAnyOrder("t=r", "t=g", "t=y");
	}
	
	@Test
	void nestedConditionTest() {
		
		assertThat(PredicateSplitter.splitPredicate("(x= y ∧ (x<5 ∨ ¬ (x=10 ∧ 1) ∨ y<6) ∧ ¬ b = 5) | 0"))
		 .containsExactlyInAnyOrder("x=y", "x<5", "x=10", "b=5", "y<6");
	}

}
