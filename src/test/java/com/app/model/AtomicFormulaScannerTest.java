package com.app.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

import com.app.model.framework.AtomicFormulaScanner;

class AtomicFormulaScannerTest {
	
	
	@Test
	void SequentialIncrementTest() {
	
		assertThat(AtomicFormulaScanner.scan("r=1"))
		 .containsExactlyInAnyOrder("r=1");
		
		assertThat(AtomicFormulaScanner.scan("x<y"))
		 .containsExactlyInAnyOrder("x<y");
		
		 assertThat(AtomicFormulaScanner.scan("x=y & ~ (y=2)"))
		 .containsExactlyInAnyOrder("x=y", "y=2");
		 
		 assertThat(AtomicFormulaScanner.scan("1"))
		 .containsExactlyInAnyOrder();

	}
	
	@Test
	void TrafficLightTest() {
		// TODO: Add test
	}
	
	@Test
	void MicrowaveTest() {

		assertThat(AtomicFormulaScanner.scan("t=r | t=g | t=y"))
		 .containsExactlyInAnyOrder("t=r", "t=g", "t=y");
	}
	
	@Test
	void nestedConditionTest() {
		
		assertThat(AtomicFormulaScanner.scan("(x= y ∧ (x<5 ∨ ¬ (x=10 ∧ 1) ∨ y<6) ∧ ¬ b = 5) | 0"))
		 .containsExactlyInAnyOrder("x=y", "x<5", "x=10", "b=5", "y<6");
	}
	
	@Test
	void exceptionTest() {

		assertThat(Arrays.asList("", " ", "1=1", "()", "1<", "0=1")).allSatisfy(element -> {
			assertThat(AtomicFormulaScanner.scan(element)).isEmpty();});
	}

}
