package com.satish.DataAnalysisModelling;

import java.util.HashMap;

/*This class maintain the Node values, one object is created for each node*/
public class Nodes {

	private String DependentNodes;
	private String DiscreteOutputValues[] ;
	public HashMap<String, String> ProbabilityValues = new HashMap<String, String>(); 
	private int NumberofOutputValues;
	
	public int getNumberofOutputValues(){
		return this.NumberofOutputValues;
	}
	public void setNumberofOUtputValues(int NumberofOutputValues){
		this.NumberofOutputValues = NumberofOutputValues;
	}
	public String getDependentNodes(){
		return this.DependentNodes;		
	}
	public String[] getDiscreteOutputValues(){
		return this.DiscreteOutputValues;
	}
	public void setDependentNodes(String DependentNodeParam){
		this.DependentNodes=DependentNodeParam;
	}
	public void setDiscreteOutputValues(String[] DiscreteOutputParam){
		this.DiscreteOutputValues=DiscreteOutputParam;
	}
	public void put(String hashmapKeyValue, String probabilityValue) {
		this.ProbabilityValues.put(hashmapKeyValue, probabilityValue);
		
	}
	public String get(String KeyValue) {
		return ProbabilityValues.get(KeyValue);
	}
}
