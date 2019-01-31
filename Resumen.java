package keel.Algorithms.ImbalancedClassification.FuzzyImb;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;



public class Resumen {

	public static void showFiles(File[] files) {
	    for (File file : files) {
	        if (file.isDirectory()) {
	            System.out.println("Directory: " + file.getName());
	            showFiles(file.listFiles()); // Calls same method again.
	        } else {
	            System.out.println("File: " + file.getName());
	        }
	    }
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0){
			args = new String[1];
			args[0] = "C:\\Users\\Anonymized\\Documents\\_FS\\av-w6-t25\\results";
		}
		PrintWriter pw = new PrintWriter(new File(args[0] + "\\resume.csv"));
		StringBuilder sb = new StringBuilder();
		File[] folders = new File(args[0]).listFiles();
		for (File folder : folders) {
	        if (folder.isDirectory()) {
	        	sb.append(folder.getName());
	        	File[] files = folder.listFiles();
	            for (File file : files) {
	            	sb.append(",");
	            	String[] parsed = Files.readAllLines(file.toPath()).get(0).split(" "); 
	            	sb.append(parsed[3]);
	            	sb.append(",");
	            	sb.append(parsed[5]);
	            }
	        }
	        sb.append("\n");
	    }
		pw.write(sb.toString());
        pw.close();
		
		/*Path pOutTst = FileSystems.getDefault().getPath(args[1] + ".tst");
		Path pOutTra = FileSystems.getDefault().getPath(args[1] + ".tra");
		Path pOutOrd = FileSystems.getDefault().getPath(args[1] + ".ord");
		DirectoryStream<Path> ds = path.newDirectoryStream();
		OutputStream osTst = new BufferedOutputStream(pOutTst.newOutputStream(CREATE, WRITE));
		OutputStream osTra = new BufferedOutputStream(pOutTra.newOutputStream(CREATE, WRITE));
		OutputStream osOrd = new BufferedOutputStream(pOutOrd.newOutputStream(CREATE, WRITE));
		
		for (Path p : path.newDirectoryStream()) {
			double meanTst = 0;
			double meanTra = 0;
			for (int i = 0; i < 5; i++){
				Path pInTst = FileSystems.getDefault().getPath(p.toString() + "\\result" + i + ".tst");
				BufferedReader inputStreamTst = null;
				InputStream isTst = pInTst.newInputStream();
				
				Path pInTra = FileSystems.getDefault().getPath(p.toString() + "\\result" + i + ".tra");
				BufferedReader inputStreamTra = null;
//				InputStream isTra = pInTra.newInputStream();
				
				try {
					inputStreamTst = new BufferedReader(new InputStreamReader(isTst));
					String line  = inputStreamTst.readLine();
					double d = new Double(line.split(":")[1].trim());
					meanTst += d;
					
//					inputStreamTra = new BufferedReader(new InputStreamReader(isTra));
//					line  = inputStreamTra.readLine();
//					d = new Double(line.split(":")[1].trim());
//					meanTra += d;
				} finally {
					if (inputStreamTst != null) {
						inputStreamTst.close();
					}
				}
			}
			String line = new Double(meanTst / 5.0).toString() + "\n";
			osTst.write(line.getBytes(), 0, line.length());
			line = new Double(meanTra / 5.0).toString() + "\n";
			osTra.write(line.getBytes(), 0, line.length());
			line = p.toString() + "\n";
			osOrd.write(line.getBytes(), 0, line.length());
		}
		
		if (osTst != null) {
			osTst.flush();
			osTst.close();
		}
		
		if (osTra != null) {
			osTra.flush();
			osTra.close();
		}
		
		if (osOrd != null) {
			osOrd.flush();
			osOrd.close();
		}*/
	}

}

