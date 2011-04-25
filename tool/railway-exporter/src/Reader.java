import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import org.pocketcampus.shared.plugin.transport.Railway;
import org.pocketcampus.shared.plugin.transport.RailwayMember;
import org.pocketcampus.shared.plugin.transport.RailwayNd;
import org.pocketcampus.shared.plugin.transport.RailwayNode;
import org.pocketcampus.shared.plugin.transport.RailwayWay;


public class Reader {
	public static void main(String[] args) {
		Railway path = new Railway();

		try{
			FileInputStream fstream = new FileInputStream("full.xml");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			int memberNum = 0;
			int ndNum = 0;

			RailwayNode currentNode = null;
			RailwayWay currentWay = null;

			while ((line = br.readLine()) != null)   {

				// NODES
				if(line.contains("<node")) {
					if(currentNode != null) path.addNode(currentNode.getRef(), currentNode);
					currentNode = new RailwayNode();

					int idStart = line.indexOf("id=\"") + 4;
					int idEnd = line.indexOf("\" lat=\"");
					String idStr = line.substring(idStart, idEnd);
					int id = Integer.parseInt(idStr);
					currentNode.setRef(id);

					int latStart = line.indexOf("lat=\"") + 5;
					int latEnd = line.indexOf("\" lon=\"");
					String latStr = line.substring(latStart, latEnd);
					double lat = Double.parseDouble(latStr);
					currentNode.setLat(lat);

					latEnd = line.indexOf("\" lon=\"") + 7;
					int lonEnd = line.indexOf("\" version=\"");
					String lonStr = line.substring(latEnd, lonEnd);
					double lon = Double.parseDouble(lonStr);
					currentNode.setLon(lon);
				}

				// NODE TAGS
				if(line.contains("<tag")) {
					int nameStart = line.indexOf("k=\"") + 3;
					int nameEnd = line.indexOf(" v=\"") - 1;
					String name = line.substring(nameStart, nameEnd);

					nameEnd = line.indexOf(" v=\"") + 4;
					int tagEnd = line.indexOf("/>") - 1;
					String value = line.substring(nameEnd, tagEnd);

					currentNode.addTag(name, value);
					
					if(name.equals("uic_ref")) {
						currentNode.setUicRef(value);
					}
				}
				
				// MEMBERS (=relation's parts)
				if(line.contains("<member")) {
					RailwayMember member = new RailwayMember();

					int typeStart = line.indexOf("type=\"") + 6;
					int typeEnd = line.indexOf("\" ref=\"");
					String type = line.substring(typeStart, typeEnd);
					member.setType(type);
					
					int refStart = line.indexOf("ref=\"") + 5;
					int refEnd = line.indexOf("\" role=\"");
					String refStr = line.substring(refStart, refEnd);
					int ref = Integer.parseInt(refStr);
					member.setRef(ref);

					int roleEnd = line.indexOf("/>")-1;
					String role = line.substring(refEnd+8, roleEnd);
					member.setRole(role);
					
					member.setNum(memberNum);
					
					path.addMember(member);
					memberNum++;
				}
				
				// WAYS
				if(line.contains("<way")) {
					ndNum = 0;
					if(currentWay != null) path.addWay(currentWay.getNum(), currentWay);
					currentWay = new RailwayWay();

					int idStart = line.indexOf("id=\"") + 4;
					int idEnd = line.indexOf("\" visible=\"");
					String idStr = line.substring(idStart, idEnd);
					int id = Integer.parseInt(idStr);
					currentWay.setNum(id);
				}
				
				// ND (way's nodes)
				if(line.contains("<nd")) {
					RailwayNd nd = new RailwayNd();
					int idStart = line.indexOf("ref=\"") + 5;
					int idEnd = line.indexOf("/>")-1;
					String idStr = line.substring(idStart, idEnd);
					int id = Integer.parseInt(idStr);
					nd.setRef(id);
					
					nd.setNum(ndNum);
					ndNum++;
					
					currentWay.addNd(nd);
				}
				
			}

			// add the last ones
			if(currentNode != null) path.addNode(currentNode.getRef(), currentNode);
			if(currentWay != null) path.addWay(currentWay.getNum(), currentWay);
			
			in.close();
		}catch (Exception e){
			e.printStackTrace();
			return;
		}
		
//		path.createRailway();
//		System.out.println(path);
		
		int num = 1;
		RailwayNode n1 = path.getStopNodes().get(8530749); //renens
		n1.setNum(num);
		
		RailwayNode closest = null;
		double minDist = 0;
		
		while(minDist!=100.0) {
			num++;
			minDist = 100.0;
			
			for(RailwayNode n2 : path.getNodes().values()) {
				double dist = n1.distTo(n2);
				
				if(dist<minDist && n1!=n2 && n2.getNum()==0) {
					closest = n2;
					minDist = dist;
				}
			}
			
			if(minDist!=100.0) {
				closest.setNum(num);
				closest.setDistFromPrevious(minDist);
				closest.setPreviousRef(n1.getRef());
			}
			
			n1 = closest;
		}
		
		System.out.println(path.getNodes());
		
		try{
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("railway_m1.dat"));
			outputStream.writeObject(path);
			outputStream.flush();
			outputStream.close();
			System.out.println("Done!");

		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

}



















