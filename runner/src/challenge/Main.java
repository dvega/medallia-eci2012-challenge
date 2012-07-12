package challenge;

import java.util.ArrayList;

import challenge.lib.Classifier;
import challenge.lib.ClassifierBuilder;
import challenge.lib.Sentiment;
import challenge.lib.TaggedReview;

/**
 * This is your main class. This class will be loaded and the training method invoked.
 */
public class Main {
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		try {
			ClassifierBuilder cb = (ClassifierBuilder) Class.forName("com.medallia.eci.EciClassifierBuilder").newInstance();
			ArrayList<TaggedReview> data = new ArrayList<>();
			data.add(new TaggedReview("test positive", Sentiment.POSITIVE));
			data.add(new TaggedReview("test negative", Sentiment.NEGATIVE));
			data.add(new TaggedReview("test neutral", Sentiment.NEUTRAL));
			Classifier classifier = cb.training(data);
			String review = "sample Positive";
			System.out.println(classifier.classify(review));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	

}
