import java.util.*;
public class Tree{
	public ArrayList<ArrayList<Integer> > adjList;
	public ArrayList<String> labels;
	public HashMap<String, Integer> labelAttributes;
	public String root;
	public int counter;
	public String interestAttr;
	public Tree(String interestAttr){
		adjList = new ArrayList<ArrayList<Integer>>();
		labels = new ArrayList<String>();
		labelAttributes = new HashMap<String, Integer>();
		this.interestAttr = interestAttr;
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
		return "Root:" + root + " " +r; 
	}
	public void addPalabra(ArrayList<String> a, int n){
		if(n==0)
			return;
		System.out.println("LLega con "+n);
		if(n%2==0)
			a.add("par");
		else
			a.add(" impar ");
		addPalabra(a, n-1);
	}
	void DFS(ArrayList<String> r, int indexAttr, String rule){
		if(adjList.get(indexAttr).size()==0){
			r.add(rule + " ) then "+ interestAttr + "="+ labels.get(indexAttr));
		}
		else{
			if(rule == "")
				rule += "if( ";
			else
				rule += " and ";
			rule += labels.get(indexAttr);
			for(int i=0; i<adjList.get(indexAttr).size(); i++){
				int indexInstance = adjList.get(indexAttr).get(i);
				String tmp = rule + "=" + labels.get(indexInstance);
				DFS(r, adjList.get(indexInstance).get(0), tmp);
			}
		}
	}
	public ArrayList<String> getRules(){
		ArrayList<String> r = new ArrayList<String>();
		DFS(r, 0, "");
		return r;
	}
}
