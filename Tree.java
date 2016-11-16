import java.util.*;
public class Tree{
	public HashMap<String, ArrayList<String> > adjList;
	public String root;
	public Tree(){
		adjList = new HashMap();
		root = null;
	}
	public void addNode(String u){
		ArrayList<String> adjNodes = new ArrayList<String>();
		adjList.put(u, adjNodes);
	}
	public void addEdge(String u, String v){
		ArrayList<String> tmp = (ArrayList)adjList.get(u);
		tmp.add(v);
	}
	public String toString(){
		return "Root:" + root + " "+adjList.toString(); 
	}
}