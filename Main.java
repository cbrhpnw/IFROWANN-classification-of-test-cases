package keel.Algorithms.ImbalancedClassification.FuzzyImb;

import java.io.IOException;

import keel.Algorithms.ImbalancedClassification.FEIFRO.FEIFRO;


public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		FuzzyImb srsf;
		if (args.length != 1)
			System.err.println("Error. A parameter is only needed");
		else {
			srsf = new FuzzyImb(args[0]);
			srsf.execute();
		}

	} // end-method

}