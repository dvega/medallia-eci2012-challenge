package com.medallia.eci;

import java.util.Random;

import challenge.lib.Classifier;
import challenge.lib.ClassifierBuilder;
import challenge.lib.Sentiment;
import challenge.lib.TaggedReview;

public class EciClassifierBuilder extends ClassifierBuilder {
	

	/**
	 * Startup method for the classifier. This method receives the sample tagged reviews. The code must learn from
	 * these examples, and build a classifier
	 *
	 * @param data Sample tagged reviews
	 * @return  A classifier built on the sample data
	 */
	public Classifier training(Iterable<TaggedReview> data) {
		for (TaggedReview taggedReview : data) {
			// Do the training
		}

		// Build the classifier
		return new Classifier() {
			Random r = new Random();
			@Override
			public double classify(String review) {
				// Put your classifier here
				return Sentiment.values()[r.nextInt(3)].getValue();
			}
		};
	}

}
