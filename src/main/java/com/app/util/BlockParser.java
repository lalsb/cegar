package com.app.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlockParser {

	private String block;
	private List<String> transitions;

	public BlockParser(String block) {
		this.block = block;
		this.parse();
	}

	public List<String> transitions(){
		return transitions;
	}

	public void pass() {
		for(String line: transitions) {
			TransitionParser t = new TransitionParser(line);
			System.out.println("C = " + t.atoms().toString() + " A = " + t.actionSubstring());
		}
	}

	private void parse() {

		transitions = Arrays.stream(block.split("\\r?\\n")) // split by new line, trim and filter empty line
				.map(x -> x.trim())
				.filter(x -> x.length() > 0)
				.collect(Collectors.toList());
	}

}
