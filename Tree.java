import java.util.*;
public class Tree{
	public ArrayList<ArrayList<Integer> > adjList;
	public ArrayList<String> labels;
	public HashMap<String, Integer> labelAttributes;
	public String root;
	public int counter;
	public Tree(){
		adjList = new ArrayList<ArrayList<Integer>>();
		labels = new ArrayList<String>();
		labelAttributes = new HashMap<String, Integer>();
		counter = 0;
	}
	public int addNode(String u){
		adjList.add(new ArrayList<Integer>());
		labels.add(u);
		return counter++;
	}
	public int addAttribute(String attr){
		int c = addNode(attr);
		labelAttributes.put(attr, c);
		return c;
	}
	public void addEdge(int u, int v){
		adjList.get(u).add(v);
	}
	public String toString(){
		String r = "{";
		int n;
		for(int i=0; i<adjList.size(); i++){
			n = adjList.get(i).size();
			if(n!=0){
				r+=labels.get(i)+"=[";
				for(int j=0; j<n-1; j++)
					r+=labels.get(adjList.get(i).get(j)) + ", ";
				r+=labels.get(adjList.get(i).get(n-1));
				r+="]";
				if(i<adjList.size()-2)
					r+=", ";
			}
		}
		r+="}";
		return "Root:" + root + " " +r +" " +labelAttributes; 
	}
}
