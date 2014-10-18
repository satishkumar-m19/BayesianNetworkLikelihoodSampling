package com.satish.DataAnalysisModelling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;


public class ReadFile {
	private Scanner InputFile;
	
	//Object to store the property of each Node
	ArrayList<Nodes> NodesList = new ArrayList<Nodes>(); 
	public String QueryString = "";
	public int TotalNumberOfNodes = 0;
	public String filename = "";
	
	public void openFile() {
	    try {
			System.out.println("The File should be placed in the path C:\'Filename.txt . The Extension of the filename should be .TXT");
			System.out.println("Please Enter the filename that has to be processed, without the extension.");
			//Code to read the file name from the console
	        BufferedReader bufferRead =
	            new BufferedReader(new InputStreamReader(System.in));
	        filename = bufferRead.readLine();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		try {
			InputFile = new Scanner(new File("C:\\"+ filename +".txt"));
		}
		catch (Exception e) {
			System.out.println("Could not find the file");
		}
	}
	
//	@SuppressWarnings("null")
	public void readFile() {
		int ProcessingNode = 1;
		String StringArrayEachNode[] = new String[100];
		int NumberOfLinesToBeProcessedForEachNode = 0;
		
		while (InputFile.hasNext()){
			/*Read Each Line and then remove the extra spaces*/
			String LineValue = InputFile.nextLine();
			LineValue=LineValue.replaceAll("\\s", " ");
			
			//Split each line into array of objects, based on the nodes
			String EachLineString[] = LineValue.split(",");
			String FirstTimeExucutionVariable = "Y" ;
			
			//if the first line is read, assign the value of the total number of nodes that is being processed
			if (FirstTimeExucutionVariable.equalsIgnoreCase("Y")){
				FirstTimeExucutionVariable= "N";
				TotalNumberOfNodes =  EachLineString.length;
			}
			
			/*Processing for each Node*/
			if (TotalNumberOfNodes > ProcessingNode){
				if (!EachLineString[ProcessingNode].equalsIgnoreCase("U") ){
						
						//Distinct number of output variable and create a node object.
						String[] OutputDiscreteValuesEachNode = {"","","","","","","","","",""};
						Nodes ProcessingNodeObj = new Nodes();
						//Variable to determine the distinct output value for each node
						String PrevOutputValue,CurrOutputValue;
						PrevOutputValue="";
						
						//index for the number of output values for each node
						int OutputValueCount = 0; 
						String StringConcatenateDifferentOutputValues="";
						
						//Number of lines to be processed for each node
						for (int k =0 ; k<= NumberOfLinesToBeProcessedForEachNode -1  ; k++){
							String EachLineString_1 = StringArrayEachNode[k];
							String EachLineString_2[] = StringArrayEachNode[k].split(",");
								CurrOutputValue = EachLineString_2[ProcessingNode - 1].substring(0, EachLineString_2[ProcessingNode - 1].indexOf(":"));
								
								//Determines the distinct output values for each node, if the output value is repeated then i will not add it again.
								if (CurrOutputValue != PrevOutputValue){
									if (!StringConcatenateDifferentOutputValues.contains(CurrOutputValue)){
										OutputDiscreteValuesEachNode[OutputValueCount]=CurrOutputValue;	
										OutputValueCount++;	
										StringConcatenateDifferentOutputValues=StringConcatenateDifferentOutputValues.concat(CurrOutputValue);
									}
								}
								PrevOutputValue=CurrOutputValue;
								
								//Determines the key values, if there are dependency nodes then remove the "," and "U" from the string
								String HashmapKeyValue = EachLineString_1.substring(0, EachLineString_1.indexOf(":"));
								HashmapKeyValue=HashmapKeyValue.replace(",", "");
								HashmapKeyValue=HashmapKeyValue.replace("U", "");
								
								//Determine the Probability or hash map value for the corresponding key
								String ProbabilityValue = EachLineString_2[ProcessingNode - 1].substring(EachLineString_2[ProcessingNode - 1].indexOf(":")+1,EachLineString_2[ProcessingNode - 1].length());
								ProcessingNodeObj.put(HashmapKeyValue,ProbabilityValue);
						}
						//Create a string to determine the dependent nodes for the current node and then concatenate and store it in a string, this will be used in the main method while retrieving the probability values
						String DependentNode = "";
						String EachLineString_2[] = StringArrayEachNode[ProcessingNode-1].split(",");
						for (int i = 0; i<=ProcessingNode-2; i++) {
							if (!EachLineString_2[i].equalsIgnoreCase("U")) {
								DependentNode = DependentNode.concat(String.valueOf(i));	
							}
						}
						
						//Assign all the values to the Node object and the add the node object
						ProcessingNodeObj.setDependentNodes(DependentNode);
						ProcessingNodeObj.setNumberofOUtputValues(OutputValueCount);
						ProcessingNodeObj.setDiscreteOutputValues(OutputDiscreteValuesEachNode);
						NodesList.add(ProcessingNodeObj);
						
						//Re initialize for the next node
						NumberOfLinesToBeProcessedForEachNode=0;
						StringArrayEachNode[NumberOfLinesToBeProcessedForEachNode] = LineValue;
						NumberOfLinesToBeProcessedForEachNode++;
						ProcessingNode++;
				}
				else{
					//Still rows to be read for the current node, End of the current node is not yet arrived.
					StringArrayEachNode[NumberOfLinesToBeProcessedForEachNode] = LineValue;
					NumberOfLinesToBeProcessedForEachNode++;
				}
			}
			else{
				//Still rows to be read for the current node, End of the current node is not yet arrived.
				StringArrayEachNode[NumberOfLinesToBeProcessedForEachNode] = LineValue;
				NumberOfLinesToBeProcessedForEachNode++;
			}
		}
		
		/*Processing for the last node, The logic is same as that of the above.*/
		if (TotalNumberOfNodes == ProcessingNode){
				String[] OutputDiscreteValuesEachNode = {"","","","","","","","","",""};
					Nodes ProcessingNodeObj = new Nodes();
					String PrevOutputValue,CurrOutputValue;
					PrevOutputValue="";
					int OutputValueCount = 0; 
					String StringConcatenateDifferentOutputValues="";
					for (int k =0 ; k< NumberOfLinesToBeProcessedForEachNode - 1; k++){
						String EachLineString_1 = StringArrayEachNode[k];//.split(",");
						String EachLineString_2[] = StringArrayEachNode[k].split(",");
							CurrOutputValue = EachLineString_2[ProcessingNode - 1].substring(0, EachLineString_2[ProcessingNode - 1].indexOf(":"));
							if (CurrOutputValue != PrevOutputValue){
								if (!StringConcatenateDifferentOutputValues.contains(CurrOutputValue)){
									OutputDiscreteValuesEachNode[OutputValueCount]=CurrOutputValue;	
									OutputValueCount++;	
									StringConcatenateDifferentOutputValues=StringConcatenateDifferentOutputValues.concat(CurrOutputValue);
								}
							}
							PrevOutputValue=CurrOutputValue;
							String HashmapKeyValue = EachLineString_1.substring(0, EachLineString_1.indexOf(":"));
							HashmapKeyValue=HashmapKeyValue.replace(",", "");
							HashmapKeyValue=HashmapKeyValue.replace("U", "");
							String ProbabilityValue = EachLineString_2[ProcessingNode - 1].substring(EachLineString_2[ProcessingNode - 1].indexOf(":")+1,EachLineString_2[ProcessingNode - 1].length());
							ProcessingNodeObj.put(HashmapKeyValue,ProbabilityValue);
					}
					String DependentNode = "";
					String EachLineString_2[] = StringArrayEachNode[0].split(",");
					for (int i = 0; i<ProcessingNode-1; i++) {
						System.out.println("inside for : dependedntnode " + DependentNode +"   "+EachLineString_2[i]);
						if (!EachLineString_2[i].equalsIgnoreCase("U")) {
							System.out.println ("inside if");
							DependentNode = DependentNode.concat(String.valueOf(i));	
						}
					}
					ProcessingNodeObj.setDependentNodes(DependentNode);
					ProcessingNodeObj.setNumberofOUtputValues(OutputValueCount);
					ProcessingNodeObj.setDiscreteOutputValues(OutputDiscreteValuesEachNode);
					NodesList.add(ProcessingNodeObj);
					
					QueryString = StringArrayEachNode[NumberOfLinesToBeProcessedForEachNode-1];
			}

		/*for loop to list the nodes and its values in the console*/
		for (int i=0; i < NodesList.size(); i++) {
			String TempDependentNode = NodesList.get(i).getDependentNodes();
	        System.out.println("Node : " + i + " NumberofOutputValues  : "+ NodesList.get(i).getNumberofOutputValues() +" DependentNodes " + TempDependentNode);
	        Iterator HashmapValuesiterator = NodesList.get(i).ProbabilityValues.keySet().iterator();  
	        while (HashmapValuesiterator.hasNext()) {  
	           String key = HashmapValuesiterator.next().toString();  
	           String value = NodesList.get(i).ProbabilityValues.get(key).toString();  
	           System.out.println(key + " " + value);  
	        }  
	    }
		System.out.println("QueryString   : " + QueryString);
	}
	public void closeFile() {
		InputFile.close();
	}

}
