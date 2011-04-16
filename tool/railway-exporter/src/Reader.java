import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import org.pocketcampus.shared.plugin.map.Railway;
import org.pocketcampus.shared.plugin.map.RailwayNode;


public class Reader {
	public static void main(String[] args) {
		Railway path = new Railway();

		try{
			FileInputStream fstream = new FileInputStream("full.xml");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;

			RailwayNode currentNode = null;

			while ((line = br.readLine()) != null)   {

				if(line.contains("<node")) {
					if(currentNode != null) path.addNode(currentNode);
					currentNode = new RailwayNode();

					int idStart = line.indexOf("id=\"") + 4;
					int idEnd = line.indexOf("\" lat=\"");
					String idStr = line.substring(idStart, idEnd);
					int id = Integer.parseInt(idStr);
					//currentNode.setNum(id);

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

				if(line.contains("<tag")) {
					int nameStart = line.indexOf("k=\"") + 3;
					int nameEnd = line.indexOf(" v=\"") - 1;
					String name = line.substring(nameStart, nameEnd);

					nameEnd = line.indexOf(" v=\"") + 4;
					int tagEnd = line.indexOf("/>") - 1;
					String value = line.substring(nameEnd, tagEnd);

					currentNode.addTag(name, value);
				}

			}

			in.close();
		}catch (Exception e){
			e.printStackTrace();
			return;
		}

		
		int num = 1;
		RailwayNode n1 = path.getNodes().first();
		n1.setNum(num);
		
		RailwayNode closest = null;
		double minDist = 0;
		
		while(minDist != 100.0) {
			num++;
			minDist = 100.0;
			
			for(RailwayNode n2 : path.getNodes()) {
				double dist = n1.distTo(n2);
				
				if(dist<minDist && n1!=n2 && n2.getNum()==0) {
					closest = n2;
					minDist = dist;
				}
			}
			
			if(minDist != 100.0) {
				closest.setNum(num);
			}
			
			n1 = closest;
			
			System.out.println(minDist);
		}
		
		
		
//		RailwayNode curNode = path.getNodes().first();
//		RailwayNode closestNode = path.getNodes().last();
//		int num = 0;
//		
//		while(closestNode != null) {
//			double shortestDist = 1000.0;
//			
//			if(closestNode.getNum()==0) {
//				closestNode = null;
//			}
//			
//			for(RailwayNode cur2 : path.getNodes()) {
//				if(curNode!=cur2 && cur2.getNum()==0) {
//					double dist = curNode.distTo(cur2);
//					
//					if(dist < shortestDist) {
//						closestNode = cur2;
//						shortestDist = dist;
//					}
//				}
//			}
//			
//			curNode.setNum(num);
//			num++;
//			System.out.println(num);
//		}
		
//		System.out.println(path);
		
//		RailwayNode prev = null;
//		double dist;
//		double cumul = 0;
//		
//		for(RailwayNode cur : path.getNodes()) {
//			
//			if(prev != null) {
//				dist = cur.distTo(prev);
//				cumul += dist;
//				System.out.println(cumul);
//				
//			}
//			
//			prev = cur;
//		}
		
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



















