/**
@author Written by Anonymized 10/11/2013 
*/
package keel.Algorithms.ImbalancedClassification.FuzzyImb;

import java.util.StringTokenizer;
import org.core.Fichero;
import keel.Algorithms.Preprocess.Basic.Metodo;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import java.io.IOException;
import java.util.Arrays;

public class FuzzyImb extends Metodo{
	String outputTr, outputTst, ficheroresult;
	int contador=0;
	boolean[] allfeaturesin;
	int nPos = 0;
    int nNeg = 0;
    int posID, negID;
    private int  tnormname;
    private int weight_Strategy;
    private PosProb[] posProbsTrain = null;
    private PosProb[] posProbsTest = null;
    double[] sdi;
	double sdo;
    
    public FuzzyImb(String fileconfig) throws IOException {
		super(fileconfig);
		outputTr = ficheroSalida[0];
	    outputTst = ficheroSalida[1];
	}
    
    
    public void execute(){
    	/*test*/
    	int TP_test = 0;
    	int FP_test = 0;
    	int FN_test = 0;
    	int TN_test = 0;
    	int npos_test = 0;
    	int nneg_test = 0;
    	
    	
    	double clasesS[];
		int tmp;
		clasesS = new double[clasesTrain.length];
		for (int j = 0; j < datosTrain.length; j++) {
			clasesS[j] = clasesTrain[j];
		}
		 /*Count of number of positive and negative examples en training*/
		for (int i=0; i<clasesTrain.length; i++) {
		      if (clasesTrain[i] == 0)
		        nPos++;
		      else
		        nNeg++;
		    }
		
		 /*Count of number of positive and negative examples en test*/
		for (int i=0; i<clasesTest.length; i++) {
		      if (clasesTest[i] == 0)
		        npos_test++;
		      else
		        nneg_test++;
		    }
		
		    if (nPos > nNeg) {
		      tmp = nPos;
		      nPos = nNeg;
		      nNeg = tmp;
		      posID = 1;
		      negID = 0;
		    } else {
		      posID = 0;
		      negID = 1;
		    }
		    
		allfeaturesin = new boolean[realTrain[0].length];
		sdi = new double[realTrain[0].length];
		for (int i = 0; i < allfeaturesin.length; i++)
			allfeaturesin[i] = true;

		for (int i = 0; i < allfeaturesin.length; i++)
			sdi[i] = sd(i);
			sdo = sdoutput();
			
//		/*for training compute Anonymized's AUC*/
		posProbsTrain = new PosProb[realTrain.length]; 
		for (int i =0; i< datosTrain.length;i++){
			double pert,  B_Nmay, B_min = 0;
			B_Nmay = 1-get_PosRegion(realTrain,i, realTrain, allfeaturesin, clasesS, sdi, negID);
			B_min = get_PosRegion(realTrain,i, realTrain, allfeaturesin, clasesS, sdi, posID);
			pert = (B_Nmay+B_min)/2;
			boolean ispositive =  (clasesTrain[i] == posID);
			posProbsTrain[i] = new PosProb(ispositive, pert);
		}
		
		
		
		/*for test classification and compute Anonymized's AUC */
		//System.out.println(weight_Strategy);
						posProbsTest = new PosProb[realTest.length]; 
						for (int i =0; i< datosTest.length;i++){
							double pert_min,pert_may,B_Nmay, B_min = 0;
							B_Nmay = get_PosRegion(realTest,i, realTrain, allfeaturesin, clasesS, sdi, negID);
							B_min = get_PosRegion(realTest,i, realTrain, allfeaturesin, clasesS, sdi, posID);
							pert_min = (1-B_Nmay+B_min)/2;
							boolean ispositive =  (clasesTest[i] == posID);
							posProbsTest[i] = new PosProb(ispositive, pert_min);
						}
						
						double auc_tst = CalculateAUC.calculate(posProbsTest); 
				        double auc_tra = CalculateAUC.calculate(posProbsTrain);
				        
				      		       
				      //  System.out.println("AUC_tra's Anonymized = "+auc_tra);
				        System.out.println("AUC_tst's Anonymized = "+auc_tst);
				       
				        Fichero.AnadirtoFichero(ficheroSalida[0], "AUC in tra: " + auc_tra + "\n");
				        Fichero.AnadirtoFichero(ficheroSalida[1],"AUC in tst: " + auc_tst + "\n");
				        
				        
}
  
    
    public double get_PosRegion(double[][]dataclass, int i, double[][]trainData, boolean[] features, double[] trainOutput, double[] sdi, int a){
		if (weight_Strategy == 0)
			return getPosRegion_Original(dataclass, i, trainData, features, trainOutput, sdi, a);
		if (weight_Strategy == 1)
			return  getPosRegion_OWA_W1(dataclass, i, trainData, features, trainOutput, sdi, a);
		if (weight_Strategy == 4)
			return getPosRegion_OWA_W2(dataclass, i, trainData, features, trainOutput, sdi, a);
		if (weight_Strategy == 2){
			if (a == posID)
				return getPosRegion_OWA_W1(dataclass, i, trainData, features, trainOutput, sdi, a);
			else
				return getPosRegion_OWA_W2(dataclass, i, trainData, features, trainOutput, sdi, a);
			}
		if (weight_Strategy == 3){
			if (a == posID)
				return getPosRegion_OWA_W2(dataclass, i, trainData, features, trainOutput, sdi, a);
			else
				return getPosRegion_OWA_W1(dataclass, i, trainData, features, trainOutput, sdi, a);
			}
		if (weight_Strategy == 7){
				return getPosRegion7(dataclass, i, trainData, features, trainOutput, sdi, a);
			}
		if (weight_Strategy == 5){
			if (a == posID)
				return getPosRegionMIN(dataclass, i, trainData, features, trainOutput, sdi, a, (float) 0.1);
			else
				return getPosRegion_OWA_W1(dataclass, i, trainData, features, trainOutput, sdi, a);
			}
		else{
			if (a == posID)
				return getPosRegionMIN(dataclass,i, trainData, features, trainOutput, sdi, a, (float) 0.1);
			else
				return getPosRegionMay(dataclass, i, trainData, features, trainOutput, sdi, a);
			}
    	
    }
    public double getPosRegion_Original(double[][]dataclass, int i, double[][]trainData, boolean[] features, double[] trainOutput, double[] sdi, int a){
        double inf = Double.MAX_VALUE;
        for(int y=0;y<trainData.length;y++){
                double implication = implication(similar(dataclass,trainData,i,y,features,sdi),similarityOutput(a, y, trainOutput));
                if(implication<inf)
                    inf=implication;
        }
        return inf;
    }
    public double getPosRegion_OWA_W1(double[][]dataclass, int i, double[][]trainData, boolean[] features, double[] trainOutput, double[] sdi, int a){
        ToSort[] toSort = new ToSort[datosTrain.length];
         double currentresult_lower =0;
         for(int y=0;y<trainData.length;y++){
                 double implication = implication(similar(dataclass,trainData,i,y,features,sdi),similarityOutput(a, y, trainOutput));
             	boolean ispositive =  (clasesTrain[y] == 0);
                 toSort[y] = new ToSort(ispositive, implication);
         }
         Arrays.sort(toSort);
         double[] weights=  getWeights(toSort,a);
         for(int i1=0;i1<trainData.length;i1++){
             currentresult_lower = currentresult_lower + weights[i1]*toSort[i1].getsim();
         }
         return currentresult_lower;
     }
    
    public double getPosRegion_OWA_W2(double[][]dataclass, int i, double[][]trainData, boolean[] features, double[] trainOutput, double[] sdi, int a){
        ToSort[] toSort = new ToSort[datosTrain.length];
         double currentresult_lower =0;
         for(int y=0;y<trainData.length;y++){
                 double implication = implication(similar(dataclass,trainData,i,y,features,sdi),similarityOutput(a, y, trainOutput));
             	boolean ispositive =  (clasesTrain[y] == 0);
                 toSort[y] = new ToSort(ispositive, implication);
         }
         Arrays.sort(toSort);
         double[] weights=  getWeights_fast(toSort,a);
         for(int i1=0;i1<trainData.length;i1++){
             currentresult_lower = currentresult_lower + weights[i1]*toSort[i1].getsim();
         }
         return currentresult_lower;
     }
   
    
    public double getPosRegionMIN(double[][]dataclass, int i, double[][]trainData, boolean[] features, double[] trainOutput, double[] sdi, int a, float r){
       ToSort[] toSort = new ToSort[datosTrain.length];
        double currentresult_lower =0;
        for(int y=0;y<trainData.length;y++){
                double implication = implication(similar(dataclass,trainData,i,y,features,sdi),similarityOutput(a, y, trainOutput));
            	boolean ispositive =  (clasesTrain[y] == 0);
                toSort[y] = new ToSort(ispositive, implication);
        }
        Arrays.sort(toSort);
        double[] weights=  getWeights_baseline2P(toSort,r);
        for(int i1=0;i1<trainData.length;i1++){
            currentresult_lower = currentresult_lower + weights[i1]*toSort[i1].getsim();
        }
        return currentresult_lower;
    }
    
    
    public double getPosRegion7(double[][]dataclass, int i, double[][]trainData, boolean[] features, double[] trainOutput, double[] sdi, int a){
        ToSort[] toSort = new ToSort[datosTrain.length];
         double currentresult_lower =0;
         for(int y=0;y<trainData.length;y++){
                 double implication = implication(similar(dataclass,trainData,i,y,features,sdi),similarityOutput(a, y, trainOutput));
             	boolean ispositive =  (clasesTrain[y] == 0);
                 toSort[y] = new ToSort(ispositive, implication);
         }
         Arrays.sort(toSort);
         double[] weights=  getWeights_7(toSort);
         for(int i1=0;i1<trainData.length;i1++){
             currentresult_lower = currentresult_lower + weights[i1]*toSort[i1].getsim();
         }
         return currentresult_lower;
     }
    
    public double getPosRegionMay(double[][]dataclass, int i, double[][]trainData, boolean[] features, double[] trainOutput, double[] sdi, int a){
        ToSort[] toSort = new ToSort[datosTrain.length];
         double currentresult_lower =0;
         for(int y=0;y<trainData.length;y++){
                 double implication = implication(similar(dataclass,trainData,i,y,features,sdi),similarityOutput(a, y, trainOutput));
             	boolean ispositive =  (clasesTrain[y] == 0);
                 toSort[y] = new ToSort(ispositive, implication);
         }
         Arrays.sort(toSort);
         double[] weights=  getWeights_fast(toSort, a);
         for(int i1=0;i1<trainData.length;i1++){
             currentresult_lower = currentresult_lower + weights[i1]*toSort[i1].getsim();
         }
         return currentresult_lower;
     }
    
    
    private double[] getWeights(ToSort[] tosort, int classindex) { //W1
       double[] weights = new double[datosTrain.length];
       int cantidad =0;
       if (classindex==posID)
    	   cantidad = nPos;
        else 
    	   cantidad = nNeg;
       int n= datosTrain.length-cantidad;
       for (int i=0;i < cantidad;i++){
    	   weights[i] = 0;
       }
       for (int i=cantidad; i<datosTrain.length;i++){
    	   weights[i] = (double)(2*(i-cantidad+1))/(n*(n+1)); 
       }
        return weights;
    }
   
   
   private double[] getWeights_fast(ToSort[] tosort, int classindex) { //W2
	   double[] weights = new double[datosTrain.length];
       int cantidad =0;
       if (classindex==posID)
    	   cantidad = nPos;
        else 
    	   cantidad = nNeg;
	    int n= datosTrain.length-cantidad; 
	    if (n<1024){
	       for (int i=0;i < cantidad;i++){
	    	   weights[i] = 0;
	       }
	       for (int i=cantidad; i<datosTrain.length;i++){
	    	   weights[i] = (double)Math.pow(2, (i-cantidad))/(Math.pow(2, n)-1);
	    	  }
	        return weights;
       }
       else{
    	   for (int i=0;i < cantidad;i++){
	    	   weights[i] = 0;
	       }
	       weights[datosTrain.length-1] = 0.5;
	       for (int i=datosTrain.length-2; i>=cantidad-1;i--){
	    	   weights[i] = weights[i+1] / 2.0;
	    	 
	       }
	        return weights; 
      }   
       
  }
   
  
   private double[] getWeights_baseline2P(ToSort[] tosort, float gamma) { //gamma
       double[] weights = new double[datosTrain.length];
       int r = Math.round(nPos + gamma * (nNeg - nPos));
       for (int i=0;i<datosTrain.length-r;i++){
    	   weights[i]=0;
       }
        for (int i=0;i < r;i++){
    	   weights[datosTrain.length-r+i] = (double)(2*(i+1))/(r*(r+1)); 
       }
        return weights;
    }
   
   
   private double[] getWeights_7(ToSort[] tosort) { //gamma
       double[] weights = new double[datosTrain.length];         
        for (int i=0;i < weights.length;i++){
    	   weights[i] = (double)(1)/(datosTrain.length); 
       }
        return weights;
    }

    private double implication(double x, double y){
		return Math.min(1,1-x+y);
	}
	
	 public double similar(double[][]dataclass, double[][]trainData, int i, int y,  boolean[] features,double[] sdi) {
			if (tnormname == 1){
				double tnorm=0;
				for (int f = 0; f < features.length; f++) {
					if(features[f]){
				         double max = Attributes.getAttribute(f).getMaxAttribute();
				         double min = Attributes.getAttribute(f).getMinAttribute();
				         if (!(max==min)){
				        	 double	simf = 1.0 	- (Double) (Math.abs(dataclass[i][f]- trainData[y][f])) / (max - min);
				        	 tnorm +=  simf;
						 	}
						}
			       }
			return (double)tnorm/features.length;
			}
			else{ 
				double tnorm=1;
				for (int f = 0; f < features.length; f++) {
					if(features[f]){
					         double max = Attributes.getAttribute(f).getMaxAttribute();
					         double min = Attributes.getAttribute(f).getMinAttribute();
					         if (!(max==min)){
					        	 double	simf = 1.0 	- (Double) (Math.abs(dataclass[i][f]- trainData[y][f])) / (max - min);
					        	 tnorm = tnormcalculate(tnorm, simf, tnormname);
							 	}
							}
				       }
				return (double)tnorm;
			}
		}
	 
	 
	 
	 private double tnormcalculate(double x, double y, int type){
		 if (type == 0)
		 	return Math.max(0,x+y-1);
		 else 
			 return Math.min(x,y);
		 
	 }

		
	 
	 public double similarityOutput(int clas, int i2, double[] outPut) {
			if (Attributes.getOutputAttribute(0).getType() == Attribute.NOMINAL) {
				if (clas == outPut[i2])
					return 1;
				else
					return 0;
			} else {
				double ai1 = outPut[clas];
				double ai2 = outPut[i2];
				return Math.max(Math.min((ai2 - ai1 + sdo) / sdo, (ai1 - ai2 + sdo)
						/ sdo), 0);
			}

		}
	 private double sd(int i) {
			double mean = 0;
			for (int j = 0; j < realTrain.length; j++) {
				mean = mean + realTrain[j][i];
			}
			mean = mean / realTrain.length;
			double sd = 0;
			for (int j = 0; j < realTrain.length; j++) {
				sd = sd + (realTrain[j][i] - mean) * (realTrain[j][i] - mean);
			}
			return Math.sqrt(sd / realTrain.length);
		}
		
		private double sdoutput() {
			double mean = 0;
			for (int j = 0; j < clasesTrain.length; j++) {
				mean = mean + clasesTrain[j];
			}
			mean = mean / clasesTrain.length;
			double sd = 0;
			for (int j = 0; j < clasesTrain.length; j++) {
				sd = sd + (clasesTrain[j] - mean) * (clasesTrain[j] - mean);
			}
			return Math.sqrt(sd / clasesTrain.length);
		}
    
		public void leerConfiguracion(String ficheroScript) {

			String fichero, linea, token;
		    StringTokenizer lineasFichero, tokens;
		    byte line[];
		    int i, j;

		    ficheroSalida = new String[2];

		    fichero = Fichero.leeFichero (ficheroScript);
		    lineasFichero = new StringTokenizer (fichero,"\n\r");

		    lineasFichero.nextToken();
		    linea = lineasFichero.nextToken();

		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    token = tokens.nextToken();

		    /*Getting the names of the training and test files*/
	          line = token.getBytes();
				for (i = 0; line[i] != '\"'; i++)
					;
				i++;
				for (j = i; line[j] != '\"'; j++)
					;
				ficheroTraining = new String(line, i, j - i);
				for (i = j + 1; line[i] != '\"'; i++)
					;
				i++;
				for (j = i; line[j] != '\"'; j++)
					;
				String nothing = new String(line, i, j - i);
	                     for (i = j + 1; line[i] != '\"'; i++)
					;
				i++;
				for (j = i; line[j] != '\"'; j++)
					;
				ficheroTest = new String(line, i, j - i);

		    /*Getting the path and base name of the results files*/
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    token = tokens.nextToken();

		    /*Getting the names of output files*/
		    line = token.getBytes();
		    for (i=0; line[i]!='\"'; i++);
		    i++;
		    for (j=i; line[j]!='\"'; j++);
		    ficheroSalida[0] = new String (line,i,j-i);
		    for (i=j+1; line[i]!='\"'; i++);
		    i++;
		    for (j=i; line[j]!='\"'; j++);
		    ficheroSalida[1] = new String (line,i,j-i);
		    
		    //Getting the tnorm used
		    linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    token = tokens.nextToken();
		    token = token.substring(1);
		    if (token.equalsIgnoreCase("Lukasiewicz")) tnormname = 0;
		    else if (token.equalsIgnoreCase("Average")) tnormname = 1;
		    else tnormname = 2;
		    
		    //Getting the weight strategy used
	        linea = lineasFichero.nextToken();
		    tokens = new StringTokenizer (linea, "=");
		    tokens.nextToken();
		    token = tokens.nextToken();
		    token = token.substring(1);
		    if (token.equalsIgnoreCase("None"))  weight_Strategy = 0;
		    else if (token.equalsIgnoreCase("W1"))  weight_Strategy = 1;
		    else if (token.equalsIgnoreCase("W2"))  weight_Strategy = 2;
		    else if (token.equalsIgnoreCase("W3"))  weight_Strategy = 3;
		    else if (token.equalsIgnoreCase("W4"))  weight_Strategy = 4;
		    else if (token.equalsIgnoreCase("W5"))  weight_Strategy = 5;
		    else if (token.equalsIgnoreCase("W7"))  weight_Strategy = 7;
		    else weight_Strategy = 6;
		}   
    
    
    
}
