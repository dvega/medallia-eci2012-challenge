package challenge;

import challenge.lib.Classifier;
import challenge.lib.Sentiment;
import challenge.lib.TaggedReview;

/**
 * This is your main class. This class will be loaded and the training method invoked.
 */
public class Main {

	/**
	 * Startup method for the classifier. This method receives the sample tagged reviews. The code must learn from
	 * these examples, and build a classifier
	 *
	 * @param data Sample tagged reviews
	 * @return  A classifier built on the sample data
	 */
	public static Classifier training(Iterable<TaggedReview> data) {
		for (TaggedReview taggedReview : data) {
			// Do the training
		}

		// Build the classifier
		return new Classifier() {
			@Override
			public double classify(String review) {
				// Put your classifier here
				return Sentiment.NEUTRAL.getValue();
			}
		};
	}
}
