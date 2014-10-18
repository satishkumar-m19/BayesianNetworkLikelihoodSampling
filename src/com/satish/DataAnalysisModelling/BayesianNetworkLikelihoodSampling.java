package com.satish.DataAnalysisModelling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class BayesianNetworkLikelihoodSampling {
	static ReadFile InputFile = new ReadFile();
	public static int HiddenOrEvidenceVariable = 1;
	public static int QueryVariableNodeNumber;
	public static float LikelihoodWeighting;
	public static float likelihoodTotalWight100Samples;
	public static int NumberofIterations = 30;
	public static int SampleSize = 100;
	public static double ZValueFor95ConfidenceInterval = 1.96;
	public static float ArrayThirtyIterationFor100Samples[][] = new float[NumberofIterations][10];
	
	public static void main (String[] args) throws IOException {
		InputFile.openFile();
		InputFile.readFile();
		InputFile.closeFile();
		
		/*Processing for each iteration of n nodes*/
		String LikelihoodQueryString = InputFile.QueryString;
		String[] ArrayQueryString = LikelihoodQueryString.split(",");
		String[] ArrayDiscreteValuesForEachNode = {"","","","","","","","","",""};
		
		/*Total 30 iterations of 100 Samples*/
		for (int Iterator30=0; Iterator30 < NumberofIterations; Iterator30++){
			
			/*Stores the sum of the probability values for each output values of a query variable*/
			float ArrayProbabilitySum[] = new float[10];
			likelihoodTotalWight100Samples = 0;
			
			/*Total 100 Samples to be run*/
			for(int a = 0 ; a < SampleSize ; a++){
				LikelihoodWeighting = 1;
				
				/*For Each N Tuple*/
				for (int i=0 ; i < InputFile.TotalNumberOfNodes ; i++){
					switch (ArrayQueryString[i]) {
					case "Q":
						
						//if the processing node is a query variable then generate the random number;
						HiddenOrEvidenceVariable = 1;
						QueryVariableNodeNumber = i;
						
						//Call to generate the random variable and determine the corresponding probability or the N tuple value
						ArrayDiscreteValuesForEachNode[i] = DetermineRandomNumberValue(i,ArrayDiscreteValuesForEachNode,ArrayQueryString);
						break;
					case "H":
						
						//if the processing node is a Hidden variable then generate the random number;
						HiddenOrEvidenceVariable = 1;
						
						//Call to generate the random variable and determine the corresponding probability or the N tuple value
						ArrayDiscreteValuesForEachNode[i] = DetermineRandomNumberValue(i,ArrayDiscreteValuesForEachNode,ArrayQueryString);
						break;
					default:
						
						//if the processing node is a Evidence variable then generate the random number;
						HiddenOrEvidenceVariable = 2;
						
						//Call to generate the random variable and determine the corresponding probability or the N tuple value
						ArrayDiscreteValuesForEachNode[i] = DetermineRandomNumberValue(i,ArrayDiscreteValuesForEachNode,ArrayQueryString);
						
						//if the variable is a evidence variable then the value is fixed, so we are cheating here. so we need to take the weight of it and multiply the same
						LikelihoodWeighting = LikelihoodWeighting * Float.valueOf(ArrayDiscreteValuesForEachNode[i]);
						break;
					}
				}
				
				/*For the acquired query variable value, check what is the index in the output values field and then store in the same index in the sum variable*/
				String temp[] = InputFile.NodesList.get(QueryVariableNodeNumber).getDiscreteOutputValues();
				for (int p = 0 ; p < InputFile.NodesList.get(QueryVariableNodeNumber).getNumberofOutputValues();p++){
					if (ArrayDiscreteValuesForEachNode[QueryVariableNodeNumber] == temp[p]){
						
						//Summing the value corresponding to the Output value of a Query variable
						ArrayProbabilitySum[p] += LikelihoodWeighting ;
						
						//Summing for total weight, this variable is used to calculate the probability of each outcome/totalweight
						likelihoodTotalWight100Samples += LikelihoodWeighting;
					}
				}
			}
			
			//String temp[] = InputFile.NodesList.get(QueryVariableNodeNumber).getDiscreteOutputValues();
			for (int p = 0 ; p < InputFile.NodesList.get(QueryVariableNodeNumber).getNumberofOutputValues();p++){
				
				//Storing the probability value for each 100 samples, Total 30 values will be stored for each outcome value (True/False, 0/1/2/3 and so on)of a query variable
				ArrayThirtyIterationFor100Samples[Iterator30][p]=	(ArrayProbabilitySum[p]/likelihoodTotalWight100Samples);
			}
		}
		
		/*Once we have formed the array with 30(iteration count) Probability values for each outcome. we call this function*/
		ConfidenceInterval(ArrayThirtyIterationFor100Samples);
	}
	
	
	private static void ConfidenceInterval(
			float[][] arrayThirtyIterationFor100Samples2) throws IOException {
		  
		/*To create the Output File*/
		File file = new File("C:\\"+InputFile.filename+"_output.txt");
		if (!file.exists()) {
	      file.createNewFile();
		}
		  FileWriter fw = new FileWriter(file.getAbsoluteFile());
		  BufferedWriter bw = new BufferedWriter(fw);
		  String temp[] = InputFile.NodesList.get(QueryVariableNodeNumber).getDiscreteOutputValues();
		  /*For each outcome value(True/False or 0/1/2 and so on) generate the mean, Standard deviation and error for the confidence interval of 95%*/
		  for (int j = 0 ; j < InputFile.NodesList.get(QueryVariableNodeNumber).getNumberofOutputValues();j++){
			//Calculate Mean
			double mean = stdMean(arrayThirtyIterationFor100Samples2,j);
			
			//Calculate Standard Deviation
			double sDev = stdDev(arrayThirtyIterationFor100Samples2,j,mean);
			
			//Calculate Standard Error
			double StandardError = (sDev/(Math.sqrt(NumberofIterations)));
			
			//Calculate confidence interval
			double ConfidenceIntervalError = StandardError * ZValueFor95ConfidenceInterval;
			
			//Write to the output file
		    bw.write(temp[j]+","+ mean +","+ ConfidenceIntervalError);
		    bw.newLine();
            System.out.println(temp[j]+","+ mean +","+ ConfidenceIntervalError);
		}
		
		//Write the query string to the output file
		System.out.println(InputFile.QueryString);
		bw.write(InputFile.QueryString);
		bw.newLine();
                bw.close();
		
	}

	//Calculate Mean Value
	public static double stdMean(float[][] arrayThirtyIterationFor100Samples2,int j)
    {
       double sum =  0;
       for(int i = 0; i < NumberofIterations; i++)
       {
           sum = arrayThirtyIterationFor100Samples2[i][j] + sum;
        }
       return sum/NumberofIterations;
        
   }
	
	//Calculate Standard Deviation
   public static double stdDev(float[][] arrayThirtyIterationFor100Samples2,int j,double mean)
   {
       double sum = 0;
        for(int i = 0; i < NumberofIterations; i++)
       {
           double value = arrayThirtyIterationFor100Samples2[i][j] - mean;
           sum += Math.pow(value, 2);
       }
       double variance = sum/NumberofIterations;
       double stdDev = Math.sqrt(variance);
       return stdDev;
   }
   
   //Determine the probability value(True/False or 0/1/2 and so on) or the weight(Probability say 0.01/0.99 and so on) for the N tuple
	public static String DetermineRandomNumberValue(int i,String ArrayDiscreteValuesForEachNode[],String ArrayQueryString[]){
		Random random = new Random();
		float randomnumber1 = 0 ;
		randomnumber1 = random.nextFloat();
		String DependentNodes = "";
		
		//Get the dependent nodes for the current node
		DependentNodes = InputFile.NodesList.get(i).getDependentNodes();
		String temp[] = InputFile.NodesList.get(i).getDiscreteOutputValues();
		
		/*if the node doesn't have a dependent value*/
		if (DependentNodes == "" ){
			float ComparedValue = 0 ;
			
			//if the current node is a Hidden then we need to fetch the outcome value either True/False that is 0/1 or if the the number of output value is 3 then either 1/2/3(any output values) and so on
			if (HiddenOrEvidenceVariable == 1){
				for (int k =0 ; k <  InputFile.NodesList.get(i).getNumberofOutputValues(); k++){
					String HashmapKeyValueTarget = "";
					HashmapKeyValueTarget = temp[k];
					ComparedValue = ComparedValue + Float.valueOf(InputFile.NodesList.get(i).get(HashmapKeyValueTarget));
					if (randomnumber1 <= ComparedValue) {
						return String.valueOf(temp[k]);
					}
				}
			}
			
			//If the current node is a Evidence variable then we need to take the weight of the value, Since we are cheating in this case
			else {
				String HashmapKeyValueTarget = "";
				HashmapKeyValueTarget = ArrayQueryString[i];
				return String.valueOf(InputFile.NodesList.get(i).get(HashmapKeyValueTarget));
			}
		}
		
		/*If the node has a dependent values, we need to construct the string to fetch the value from the hashmap, this string will again be concatenated and used as a key value for hashmap*/
		else {
			String HashmapKepValue = "";
		for (int j = 0; j < DependentNodes.length(); j++){
			String index;
			index = DependentNodes.substring(j,(j+1));
			HashmapKepValue=HashmapKepValue.concat(ArrayDiscreteValuesForEachNode[Integer.valueOf(index)]);
			}
		float ComparedValue = 0 ;
		
		//if the current node is a Hidden then we need to fetch the outcome value either True/False that is 0/1 or if the the number of output value is 3 then either 1/2/3(any output values) and so on
		if (HiddenOrEvidenceVariable == 1){
			for (int k =0 ; k <  InputFile.NodesList.get(i).getNumberofOutputValues(); k++){
				String HashmapKeyValueTarget = HashmapKepValue;
				HashmapKeyValueTarget = HashmapKeyValueTarget.concat(temp[k]);
				ComparedValue = ComparedValue + Float.valueOf(InputFile.NodesList.get(i).get(HashmapKeyValueTarget));
				if (randomnumber1 <= ComparedValue) {
					return String.valueOf(temp[k]);
				}
			}
		}
		
		//If the current node is a Evidence variable then we need to take the weight of the value, Since we are cheating in this case
		else {
			String HashmapKeyValueTarget = HashmapKepValue;
			HashmapKeyValueTarget = HashmapKeyValueTarget.concat(ArrayQueryString[i]);
			return String.valueOf(InputFile.NodesList.get(i).get(HashmapKeyValueTarget));
		}
	}
		return "a";
	}
}
