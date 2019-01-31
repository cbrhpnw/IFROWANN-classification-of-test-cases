package keel.Algorithms.ImbalancedClassification.FuzzyImb;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.StringTokenizer;

import keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets.FastVector;
import keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets.Instance;
import keel.Algorithms.ImbalancedClassification.Resampling.SMOTE_RSB.Rough_Sets.Instances;
import keel.Algorithms.Preprocess.Basic.Metodo;
import keel.Algorithms.Preprocess.Basic.OutputIS;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.InstanceSet;

import org.core.Fichero;

public class Particioner  {
	 /*Path and names of I/O files*/
	  protected String ficheroTraining;
	  protected String ficheroValidation;
	  protected String ficheroTest;
	  protected String ficheroSalida[];
	
	 
	
	
	 public Particioner(String fileconfig) throws IOException {
	leerConfiguracion(fileconfig);
					
	 }
	
	    

	
	  public void execute() throws IOException{
		  String header = "@relation Blosum50\n@attribute longitude real\n@attribute alignBlos50 real\n@attribute blosum50_3 real\n" +
		  	"@attribute blosum50_5 real\n@attribute blosum50_7 real\n@attribute lcb real\n@attribute inparanoid7.0 {0,1}\n" + 
		  	"@attribute geneDB {0,1}\n@attribute claseInterseccion {0,1}\n@attribute claseUnion {0,1}\n@DATA\n";
		  int pos = 0;
		  int neg = 0;
		  double control = 0.1;
		  int folds = 10;
		  String negFile = "negFile.dat";
		  String posFile = "posFile.dat";
		  String conFile = "control.dat";
		  Path pathNeg = FileSystems.getDefault().getPath(negFile);
		  Path pathPos = FileSystems.getDefault().getPath(posFile);
		  OutputStream osNeg = new BufferedOutputStream(pathNeg.newOutputStream(CREATE, WRITE));
		  OutputStream osPos = new BufferedOutputStream(pathPos.newOutputStream(CREATE, WRITE));
		  OutputStream osCon = new BufferedOutputStream(FileSystems.getDefault().getPath(conFile).newOutputStream(CREATE, WRITE));
		  osCon.write(header.getBytes(), 0, header.length());
		  
		  BufferedReader r = new BufferedReader(new FileReader(ficheroTraining));
		  String line = r.readLine();
		  while (line.charAt(0) == '@')
		  {
			  line = r.readLine();
		  }
		  while (line != null)
		  {
			  if (line.charAt(line.length() - 1) == '0'){
				  line += "\n";
				  osNeg.write(line.getBytes(), 0, line.length());
				  neg++;
			  } else {
				  line += "\n";
				  osPos.write(line.getBytes(), 0, line.length());
				  pos++;
			  }
			  line = r.readLine();
		  }
		  osNeg.close();
		  osPos.close();
		  
		  BufferedReader inputStreamNeg = new BufferedReader(new InputStreamReader(FileSystems.getDefault().getPath(negFile).newInputStream()));
		  BufferedReader inputStreamPos = new BufferedReader(new InputStreamReader(FileSystems.getDefault().getPath(posFile).newInputStream()));
		  
		  for (int i = 0; i < neg * control; i++){
			  line = inputStreamNeg.readLine();
			  line += "\n";
			  osCon.write(line.getBytes(), 0, line.length());
		  }
		  for (int i = 0; i < pos * control; i++){
			  line = inputStreamPos.readLine();
			  line += "\n";
			  osCon.write(line.getBytes(), 0, line.length());
		  }
		  
		  System.out.println(pos);
		  System.out.println(neg);
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

		  
	  }
	
	
}
