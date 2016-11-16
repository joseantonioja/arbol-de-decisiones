import java.lang.*;
import java.io.*;
import java.util.*;
public class TreeGenerator{
	/*Determina la instancia mas frecuente de attribute en table*/
	private static String mostFrequent(ArrayList< ArrayList<String>> table , int attribute){
		Map<String, Integer> hm = new HashMap<String, Integer>();
		for(int i=0; i<table.size(); i++){
			if(hm.containsKey(table.get(i).get(attribute)))
				hm.put(table.get(i).get(attribute), hm.get(table.get(i).get(attribute)) + 1);
			else
				hm.put(table.get(i).get(attribute), 1);
		}
		String mostF = "";
		int max = 0;
		for(Map.Entry<String, Integer> ent:hm.entrySet()){
			if(ent.getValue()>max){
				max = ent.getValue();
				mostF = ent.getKey();
			}
		}
		return mostF;
	}
	/*Determina el logaritmo base 2 de p*/
	private static double log2(double p){
		return Math.log(p)/Math.log(2);
	}
	/*Determina la entropia de un ArrayList de enteros*/
	private static double getEntropy(ArrayList<Integer> l){
		double total = 0;
		double ent = 0;
		for(int i=0; i<l.size(); i++){
			total += l.get(i);
		}
		for(int i=0; i<l.size(); i++){
			if(l.get(i)!=0)
				ent -= (l.get(i)/total)*log2(l.get(i)/total);
		}
		return ent;
	}
	/*Obtiene los enteros almacenados dentro de un HashMap con indices en forma de cadena*/
	private static ArrayList<Integer> getMapValues(HashMap<String, Integer> hm){
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		for(Map.Entry e:hm.entrySet())
			tmp.add((Integer)e.getValue());
		return tmp;
	}
	/*Obtiene las cadenas que sirven como llave en un HashMap*/
	private static ArrayList<String> getMapKeys(HashMap<String, Integer> hm){
		ArrayList<String> tmp = new ArrayList<String>();
		for(Map.Entry e:hm.entrySet())
			tmp.add((String)e.getKey());
		return tmp;
	}
	/*Iguala a cero los valores almacenados dentro de un mapa*/
	private static void clearMap(HashMap<String, Integer> hm){
		for(Map.Entry e:hm.entrySet())
			hm.put((String)e.getKey(), 0);
	}
	/*Obtiene la ganancia de los atributos que no han sido visitados dentro de table respecto a indexAttr con un numero maximo de atributos nAttr*/
	private static double[] getGains(int nAttr, ArrayList<ArrayList<String>> table, int indexAttr, int visitedAttributes[]){
		double gains[] = new double[nAttr];
		double entropy[] = new double[nAttr];
		int n = table.size();
		HashMap<String, Integer> hmIndexAttr = new HashMap<String, Integer>();
		for(int i=0; i<n; i++){
			if(hmIndexAttr.containsKey(table.get(i).get(indexAttr)))
				hmIndexAttr.put(table.get(i).get(indexAttr), hmIndexAttr.get(table.get(i).get(indexAttr)) + 1);
			else
				hmIndexAttr.put(table.get(i).get(indexAttr), 1);
		}
		//Entropia respecto a indexAttr
		entropy[indexAttr] = getEntropy(getMapValues(hmIndexAttr));
		gains[indexAttr] = 0;
		//Se determinan los valores diferentes dentro de la columna indexAttr
		double tmp = 0, particularEntropy = 0;
		for(int i=0; i<nAttr; i++){
			entropy[i] = 0;
			if(i != indexAttr && visitedAttributes[i]==0){
				HashMap<String, Integer> hmParticular = new HashMap<String, Integer>();
				for(int j=0; j<n; j++){
					String key = table.get(j).get(i);
					if(hmParticular.containsKey(key))
						hmParticular.put(key, hmParticular.get(key) + 1);
					else
						hmParticular.put(key, 1);
				}
				for(Map.Entry e:hmParticular.entrySet()){
					tmp = 0;
					particularEntropy = 0;
					clearMap(hmIndexAttr);
					String key = (String)e.getKey();
					for(int j=0; j<n; j++){
						if(key.equals(table.get(j).get(i))){
							String interestInstance = table.get(j).get(indexAttr);
							hmIndexAttr.put(interestInstance, hmIndexAttr.get(interestInstance) + 1);
						}
					}
					particularEntropy = (hmParticular.get(key)/(double)n)*getEntropy(getMapValues(hmIndexAttr));
					entropy[i] += particularEntropy;					
				}
				gains[i] = entropy[indexAttr] - entropy[i];
			}
		}
		return gains;
	}
	/*Construye el arbol resultado de id3*/
	private static void id3(ArrayList<String> input, int visitedAttributes[], String output, ArrayList< ArrayList <String> > table, Tree t, String father){
		int indexO = input.indexOf(output);
		boolean equalO = true;
		//Si la tabla es vacia
		if(table.size()==0){
			System.out.println("Failure");
			t.addEdge(father, "Failure");
			return;
		}
		//Si todos los registros tienen el mismo valor para el atributo output
		for(int i=1; i<table.size(); i++)
			equalO = equalO && (table.get(i).get(indexO).equals(table.get(i-1).get(indexO)));
		if(equalO){
			t.addEdge(father, table.get(0).get(indexO));
			return;
		}
		//Si ya no hay atributos a analizar
		int counter = 0;
		for(int i=0; i<visitedAttributes.length; i++)
			counter += visitedAttributes[i];
		if(counter==visitedAttributes.length-1){
			t.addEdge(father, mostFrequent(table, indexO));
			return;
		}
		//Si no se cumplen las anteriores, se determinan las ganancias respecto a atributo en la posicion indexO
		double gains[] = getGains(input.size(), table, indexO, visitedAttributes);
		System.out.println("Gains:");
		for(int i=0; i<input.size(); i++)
			if(visitedAttributes[i]==0)
				System.out.println(input.get(i) + ":" + gains[i]);
		int indexMaxGain = 0;
		double maxGain = gains[0];
		for(int i=0; i<gains.length; i++){
			if(gains[i]>maxGain){
				indexMaxGain = i;
				maxGain = gains[i];
			}
		}
		/*xAttr es el atributo con la ganancia maxima*/
		String xAttr = input.get(indexMaxGain);
		/*Se marca como visitado*/
		visitedAttributes[indexMaxGain]=1;
		/*Se determinan las instancias de los atributos de xAttr*/
		ArrayList<String> visited = new ArrayList<String>();
		for(int j=0; j<table.size(); j++){
			String tmp = table.get(j).get(indexMaxGain);
			if(visited.indexOf(tmp)<0)
				visited.add(tmp);
		}
		/*Se agrega un nodo al arbol t con etiqueta xAttr*/
		t.addNode(xAttr);
		if(father==null)
			t.root = xAttr;
		else
			t.addEdge(father, xAttr);
		System.out.println(xAttr+" fue seleccionado");
		/*Se particiona la informacion en tablas mas pequeñas de acuerdo a las instancias de xAttr*/
		for(int j=0; j<visited.size(); j++){
			String instance = visited.get(j);
			ArrayList<ArrayList<String>> newTable = new ArrayList<ArrayList<String>>();
			for(int i=0; i<table.size(); i++){
				if(table.get(i).get(indexMaxGain).equals(instance))
					newTable.add(table.get(i));
			}
			/*Se agrega un nodo al arbol t*/
			t.addNode(instance);
			/*Se agrega una arista del atributo a su instancia*/
			t.addEdge(xAttr, instance);
			/*Se llama de nuevo a id3 con esa nueva tabla*/
			id3(input, visitedAttributes, output, newTable, t, instance);
		}
	}
	/*Regresa el arbol resultado de correr id3 con los atributos attr, el atributo de interes(que sera clasificado) sobre una tabla de hechos*/
	public static Tree runId3(ArrayList<String> attr, String output, ArrayList<ArrayList<String>> table){
		Tree t = new Tree();
		int visitedAttributes[] = new int[attr.size()];
		id3(attr, visitedAttributes, output, table, t, null);
		return t;
	}
	public static void main(String[] args){
		int nAttr = 0;
		int nFacts = 0;
		String fileName = "tabla.txt";
		String line = null;
		try{
			BufferedReader bf = new BufferedReader(new FileReader(fileName));
			int counter = 0;
			line = bf.readLine();
			nAttr = Integer.parseInt(line);
			line = bf.readLine();
			nFacts = Integer.parseInt(line);
			ArrayList<ArrayList<String>> facts = new ArrayList<ArrayList<String>>();
			ArrayList<String> attr = new ArrayList<String>();
			line = bf.readLine();
			for(String s:line.split("\t"))
				attr.add(s);
			for(int i=0; i<nFacts; i++){
				line = bf.readLine();
				ArrayList<String> tmp = new ArrayList<String>();
				for(String s:line.split("\t"))
					tmp.add(s);	
				facts.add(tmp);
			}
			String interestAttribute = bf.readLine();
			bf.close();
			System.out.println(attr);
			for(ArrayList<String> a:facts)
				System.out.println(a);
			System.out.println("Atributo de interes "+interestAttribute);
			Tree t = TreeGenerator.runId3(attr, interestAttribute, facts);
			System.out.println(t);
		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}
}