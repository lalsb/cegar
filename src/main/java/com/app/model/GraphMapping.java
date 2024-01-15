package com.app.model;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.graphstream.graph.Node;

public class GraphMapping {

	private Map<Node, Node> mapping;

    public GraphMapping() {
        this.mapping = new HashMap<>();
    }

    public void map(Node originalNode, Node abstractNode) {
        mapping.put(originalNode, abstractNode);
    }

    public Set<Node> getInverseImage(Node abstractNode) {
        Set<Node> originalNodes = new HashSet<>();
        for (Map.Entry<Node, Node> entry : mapping.entrySet()) {
            if (entry.getValue().equals(abstractNode)) {
                originalNodes.add(entry.getKey());
            }
        }
        return originalNodes;
    }
}