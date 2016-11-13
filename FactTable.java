import java.util.*;
import java.lang.*;
import java.io.*;
class FactTable{
	private ArrayList< ArrayList<Integer> > attrOcurrences;
	private ArrayList< ArrayList<String> > attrInstances;
	private ArrayList< ArrayList<Double> > branchEntropies;
	private String headers[];
	private double entropies[];
	private double gains[];
	private int facts[][];
	private int nRows;
	private int nFilters;
	private int generalAttribute;
	private int particularOcurrences[][][];
	public FactTable(int nAtributes, int nRows){
		facts = new int[nRows][nAtributes];
		entropies = new double[nAtributes];
		gains = new double[nAtributes];
		this.nRows = nRows;
		attrOcurrences = new ArrayList();
		attrInstances = new ArrayList();
		branchEntropies = new ArrayList();
	}
	public void setAttributes(String headers[]){
		this.headers = headers;
		for(int i=0; i<headers.length; i++){
			ArrayList<Integer> tmpI = new ArrayList();
			ArrayList<String> tmpS = new ArrayList();
			attrOcurrences.add(tmpI);
			attrInstances.add(tmpS);
		}
	}
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
	public void impOcurrences(){
		for(int i=0; i<headers.length; i++){
			System.out.println(headers[i]+":");
			for(int j=0; j<attrInstances.get(i).size(); j++){
				System.out.println("\t" + attrInstances.get(i).get(j) + ":" + attrOcurrences.get(i).get(j));
			}
		}
	}
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
	public void findEntropies(){
		findGeneralEntropy();
		findParticularEntropies();
	}
	private void findGeneralEntropy(){
		double prob, entropy = 0;
		for(int i=0; i<attrOcurrences.get(generalAttribute).size(); i++){
			prob = attrOcurrences.get(generalAttribute).get(i)/(float)nRows;
			entropy -= log2(prob)*prob;
		}
		entropies[generalAttribute] = entropy;
	}
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
	public void findGains(){
		System.out.println("Gains:");
		for(int i=0; i<headers.length; i++){
			if(i!=generalAttribute){
				gains[i] = entropies[generalAttribute] - entropies[i];
				System.out.println("\t"+headers[i] + ": "+gains[i]);
			}
		}
	}
	public static void impAttributes(String[] attr){
		for(int i=0; i<attr.length; i++)
			System.out.print(attr[i] + "\t");
		System.out.println("");
	}
	private double log2(double p){
		return Math.log(p)/Math.log(2);
	}
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
