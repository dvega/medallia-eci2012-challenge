package challenge.lib;

public interface Classifier {
	/**
	 * Classify the given review
	 *
	 * @param review text of the review
	 *
	 * @return The predicted Sentiment magnitude. -1.0 for {@link Sentiment#NEGATIVE}, 0.0 for {@link Sentiment#NEUTRAL},
	 *         +1.0 for {@link Sentiment#POSITIVE} . Any in between values means in between prediction.
	 *         <p>
	 *         For example +0.25 means most probably {@link Sentiment#NEUTRAL}, but perhaps {@link Sentiment#POSITIVE}
	 */
	double classify(String review);
}
