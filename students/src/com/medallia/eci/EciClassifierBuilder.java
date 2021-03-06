package com.medallia.eci;

import challenge.lib.Classifier;
import challenge.lib.ClassifierBuilder;
import challenge.lib.Sentiment;
import challenge.lib.TaggedReview;
import challenge.run.Main;

/**
 * This class will be loaded by the execution framework via reflection. You must not change the name or the package of
 * this class. This class must implement the {@link ClassifierBuilder} interface.
 * <p>
 * This class must have a no-argument constructor. If you do not put any constructor, Java will create a no-arg
 * constructor for you.
 * <p>
 * With the provided execution framework, the {@link #training(Iterable)} method will be called several times with
 * different subset of the sample set. You should not keep information on static variable about previous sets.
 * This is pointless because during the competition this method will be called just once.
 * <p>
 * The code here must not access the filesystem or the network. It will be run with restricted security permission,
 * similar to a Java Applet in a browser.
 */
public class EciClassifierBuilder implements ClassifierBuilder {

	/**
	 * Startup method for the classifier. This method receives the sample tagged reviews. The code must learn from
	 * these examples, and build a classifier
	 *
	 * @param data Sample tagged reviews
	 * @return  A classifier built on the sample data
	 */
	@Override
	public Classifier training(Iterable<TaggedReview> data) {

		for (TaggedReview taggedReview : data) {
			// Do the training
			// Learn from the sample data
		}

		// Build the classifier
		return new Classifier() {
			/** @see Classifier#classify(String) */
			@Override
			public double classify(String review) {
				// Put your classifier here

				// Replace this dummy return value with your prediction
				return Sentiment.values()[review.length() % 3].getValue();
			}
		};
	}

	/**
	 * This main method is here for your convenience to ease debugging this class from an IDE.
	 *
	 * DO NOT PUT ANY CODE HERE!!! THIS METHOD IS NOT USED DURING THE COMPETITION !!!!
	 */
	public static void main(String[] args) throws Exception {
		Main.main(args);
	}

}
