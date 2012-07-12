package challenge;

import challenge.lib.Classifier;
import challenge.lib.ClassifierBuilder;

/**
 * This is your main class. This class will be loaded and the training method invoked.
 */
public class Main {
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		try {
			ClassifierBuilder cb = (ClassifierBuilder) Class.forName(args[0]).newInstance();
			Classifier classifier = cb.training(null);
			String review = "sample";
			classifier.classify(review);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	

}
