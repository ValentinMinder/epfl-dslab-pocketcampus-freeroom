package org.pocketcampus.shared.plugin.map;
import java.io.Serializable;
import java.util.TreeSet;


public class Railway implements Serializable {
	private static final long serialVersionUID = 2923390064131621586L;
	private TreeSet<RailwayNode> nodes_;
	
	public Railway() {
		nodes_ = new TreeSet<RailwayNode>();
	}

	public void addNode(RailwayNode currentNode) {
		nodes_.add(currentNode);
	}
	
	@Override
	public String toString() {
		return nodes_.toString();
	}

	public TreeSet<RailwayNode> getNodes() {
		return nodes_;
	}
}
