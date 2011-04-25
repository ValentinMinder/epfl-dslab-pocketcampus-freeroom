package org.pocketcampus.shared.plugin.transport;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;


public class Railway implements Serializable {
	private static final long serialVersionUID = 2923390064131621586L;
	private HashMap<Integer, RailwayNode> nodes_;
	private HashMap<Integer, RailwayWay> ways_;
	private TreeSet<RailwayMember> members_;
	private TreeSet<RailwayNode> railway_;
	private HashMap<Integer, RailwayNode> stopNodes_;
	
	public Railway() {
		railway_ = new TreeSet<RailwayNode>();
		nodes_ = new HashMap<Integer, RailwayNode>();
		stopNodes_ = new HashMap<Integer, RailwayNode>();
		members_ = new TreeSet<RailwayMember>();
		ways_ = new HashMap<Integer, RailwayWay>();
	}

	public void addNode(int num, RailwayNode currentNode) {
		nodes_.put(currentNode.getRef(), currentNode);
		stopNodes_.put(currentNode.getUicRef(), currentNode);
	}
	
	@Override
	public String toString() {
		//return members_.toString() + "\n" + nodes_.toString();
		return railway_.toString();
		//return "";
	}
	
	public HashMap<Integer, RailwayNode> getStopNodes() {
		return stopNodes_;
	}
	
	public void setStopNodes_(HashMap<Integer, RailwayNode> stopNodes) {
		this.stopNodes_ = stopNodes;
	}
	
	public HashMap<Integer, RailwayNode> getNodes() {
		return nodes_;
	}
	
	public TreeSet<RailwayMember> getMembers() {
		return members_;
	}
	
	public void addMember(RailwayMember member) {
		members_.add(member);
	}
	
	public void createRailway() {
		for(RailwayMember member : members_) {
			if(member.getType().equals("way")) {
				RailwayWay way = ways_.get(member.getRef());
				
				for(RailwayNd nd : way.getNds()) {
					int ref = nd.getRef();
					RailwayNode toAdd = nodes_.get(ref);
					
					if(railway_.size()>0) {
						RailwayNode last = railway_.last();
						
						double distFromLast = (toAdd.getLon()-last.getLon())*(toAdd.getLon()-last.getLon()) + (toAdd.getLat()-last.getLat())*(toAdd.getLat()-last.getLat());
						distFromLast = Math.sqrt(distFromLast);
						toAdd.setDistFromPrevious(distFromLast);
					}
					
					
					railway_.add(toAdd);
				}
			}
			
		}
	}

	public void addWay(int num, RailwayWay currentWay) {
		ways_.put(num, currentWay);
	}

	public TreeSet<RailwayNode> getRailway() {
		return railway_;
	}
}





















