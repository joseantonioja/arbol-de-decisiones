import java.util.*;
import java.lang.*;
import java.io.*;
class FactTable{
	private ArrayList< ArrayList<Integer> > attrOcurrences;//Almacena las ocurrencias de la j-esima instancia del i-esimo atributo
	private ArrayList< ArrayList<String> > attrInstances;//Almacena la j-esima instancia del i-esimo atributo
	private ArrayList< ArrayList<Double> > branchEntropies;//Almacena las entropias de cada nodo
	private String headers[];//Almacena los nombres de los atributos a considerar
	private double entropies[];//Almacena la entropia general de cada atributo o header
	private double gains[];//Almacena la ganancia general de cada atributo o header
	private int facts[][];//Almacena la tabla mapeada con indices que identifican a cada instancia
	private int nRows;//Numero de hechos dentro de la tabla
	private int nFilters;//Numero de instancias de la variable que se va a estudiar
	private int generalAttribute;//Identificador del atributo que se estudiara
	private int particularOcurrences[][][];//Tabla de frequencias respecto al atributo vs el atributo que se estudiara
	/*Constructor de la clase. Se le pasan el numero de atributos y el numero de filas de la tabla*/
	public FactTable(int nAtributes, int nRows){
		facts = new int[nRows][nAtributes];
		entropies = new double[nAtributes];
		gains = new double[nAtributes];
		this.nRows = nRows;
		attrOcurrences = new ArrayList();
		attrInstances = new ArrayList();
		branchEntropies = new ArrayList();
	}
	/*Pone los nombres de los atributos dentro de la tabla*/
	public void setAttributes(String headers[]){
		this.headers = headers;
		for(int i=0; i<headers.length; i++){
			ArrayList<Integer> tmpI = new ArrayList();
			ArrayList<String> tmpS = new ArrayList();
			attrOcurrences.add(tmpI);
			attrInstances.add(tmpS);
		}
	}
	/*Agrega la i-esima fila a la tabla facts mapeando los strings de las instancias de atributos por identificadores enteros*/
	public void addFact(int i, String attributes[]){
		for(int j=0; j<attributes.length; j++){
			int found = attrInstances.get(j).indexOf(attributes[j]);
			if(found==-1){
				attrInstances.get(j).add(attributes[j]);
				attrOcurrences.get(j).add(1);
				facts[i][j] = attrInstances.get(j).size() - 1;
			}
			else{
				attrOcurrences.get(j).set(found, attrOcurrences.get(j).get(found) + 1);
				facts[i][j] = found;
			}
		}
	}
	/*Imprime la tabla de hechos mapeada en Strings*/
	public void impFact(){
		for(int j=0; j<headers.length; j++){
			System.out.print(headers[j] + "\t");
		}
		System.out.println("");
		for(int i=0; i<nRows; i++){
			for(int j=0; j<headers.length; j++){
				System.out.print(facts[i][j]+"\t");
			}
			System.out.println("");
		}
	}
	/*Imprime las ocurrencias de las instancias de cada atributo o header*/
	public void impOcurrences(){
		for(int i=0; i<headers.length; i++){
			System.out.println(headers[i]+":");
			for(int j=0; j<attrInstances.get(i).size(); j++){
				System.out.println("\t" + attrInstances.get(i).get(j) + ":" + attrOcurrences.get(i).get(j));
			}
		}
	}
	/*Imprime la tabla de frequencias del header vs la variable a considerar*/
	public void impFrequencyTables(){
		System.out.println("Ocurrencias generales de "+headers[generalAttribute]);
		for(int i=0; i<nFilters; i++){
			System.out.println(attrInstances.get(generalAttribute).get(i)+": "+attrOcurrences.get(generalAttribute).get(i));
		}
		System.out.println("Ocurrencias particulares respecto a "+headers[generalAttribute]);
		for(int i=0; i<headers.length; i++){
			if(i!=generalAttribute){
				System.out.println("De: " +headers[i]);
				System.out.print("          ");
				for(int k=0; k<nFilters; k++){
					System.out.print(attrInstances.get(generalAttribute).get(k)+"\t");
				}
				System.out.println("");
				for(int j=0; j<attrInstances.get(i).size(); j++){
					System.out.print(attrInstances.get(i).get(j)+"\t");
					for(int k=0; k<nFilters; k++){
						System.out.print(particularOcurrences[i][j][k]+"\t");
					}
					System.out.println("");
				}
			}	
		}
	}
	/*Llena la tabla de frequencias del header vs la variable a considerar que se identifica por column*/
	public void findFrequencies(int column){
		generalAttribute = column;
		nFilters = attrInstances.get(column).size();
		int nMaxInstances = 0;
		for(int i=0; i<headers.length; i++){
			if(attrInstances.get(i).size()>nMaxInstances)
				nMaxInstances = attrInstances.get(i).size();
		}
		particularOcurrences = new int[headers.length][nMaxInstances][nFilters];
		for(int i=0; i<nRows; i++){
			for(int j=0; j<headers.length; j++){
				if(j!=column)
					particularOcurrences[j][facts[i][j]][facts[i][column]]++;
			}
		}		
	}
	/*Determina todas las entropias necesarias*/
	public void findEntropies(){
		findGeneralEntropy();
		findParticularEntropies();
	}
	/*Encuentra la entropia del atributo que se va a estudiar*/
	private void findGeneralEntropy(){
		double prob, entropy = 0;
		for(int i=0; i<attrOcurrences.get(generalAttribute).size(); i++){
			prob = attrOcurrences.get(generalAttribute).get(i)/(float)nRows;
			entropy -= log2(prob)*prob;
		}
		entropies[generalAttribute] = entropy;
	}
	/*Encuentra las entropias de los headers vs la variable a estudiar*/
	private void findParticularEntropies(){
		double tmp;
		System.out.println("Branch entropies: ");
		for(int i=0; i<headers.length; i++){
			ArrayList<Double> arrayTmp = new ArrayList();
			branchEntropies.add(arrayTmp);
			if(i!=generalAttribute){
				System.out.println("  "+headers[i]);
				entropies[i] = 0;
				for(int j=0; j<attrOcurrences.get(i).size(); j++){
					double tmpTotal = (double)attrOcurrences.get(i).get(j);
					tmp = entropy(particularOcurrences[i][j], tmpTotal);
					branchEntropies.get(i).add(tmp);
					System.out.println("\tE("+attrInstances.get(i).get(j) + "):"+branchEntropies.get(i).get(j));
					entropies[i]+=(tmpTotal/(double)nRows)*tmp;
				}
			}
		}
	}
	/*Determina la entropia de una serie de numeros*/
	public double entropy( int frequencies[], double total){
		double prob, ent=0;
		for(int i=0; i<nFilters; i++){
			if(frequencies[i]!=0){
				prob = frequencies[i]/total;
				ent -= prob*log2(prob);
			}
		}
		return ent;
	}	
	/*Determina la ganancia de la informacion por cada header o atributo*/
	public void findGains(){
		System.out.println("Gains:");
		for(int i=0; i<headers.length; i++){
			if(i!=generalAttribute){
				gains[i] = entropies[generalAttribute] - entropies[i];
				System.out.println("\t"+headers[i] + ": "+gains[i]);
			}
		}
	}
	/*Determina el logaritmo base 2 de p*/
	private double log2(double p){
		return Math.log(p)/Math.log(2);
	}
	/*Regresa el arbol generado por el algoritmo ID3 respecto a la variable a considerar identificada por column*/
	public Tree id3(int column){
		impFact();
		impOcurrences();
		findFrequencies(column);
		impFrequencyTables();
		findEntropies();
		findGains();
		int headersVisited[] = new int[headers.length];
		headersVisited[column] = 1;
		Tree t = new Tree();
		t.addNode("Outlook");
		t.addEdge("Outlook", "Rainy");
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
			FactTable ft = new FactTable(nAttr, nFacts);
			line = bf.readLine();
			ft.setAttributes(line.split("\t"));
			for(int i=0; i<nFacts; i++){
				line = bf.readLine();
				ft.addFact(i, line.split("\t"));
			}
			bf.close();
			ft.id3(4);
		}
		catch(Exception e){
			e.printStackTrace();
		}		
	}
}
