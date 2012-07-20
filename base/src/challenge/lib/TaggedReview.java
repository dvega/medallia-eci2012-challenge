package challenge.lib;

import java.util.regex.Pattern;

public class TaggedReview {
	public final Sentiment sentiment;
	public final String review;

	public TaggedReview(Sentiment sentiment, String review) {
		this.review = review;
		this.sentiment = sentiment;
	}

	/**
	 * Utility method. Gets the review words
	 * @return Array of review words
	 */
	public String[] getReviewWords() {
		return splitWords(review);
	}

	@Override
	public String toString() {
		return "TaggedReview{sentiment=" + sentiment + ", review='" + review + "\'}";
	}

	/**
	 * Utility method. Splits a given {@link String} into words
	 *
	 * @param text String to be split
	 * @return Array of words
	 */
	public static String[] splitWords(String text) {
		return NONWORD_PATTERN.split(text);
	}

	private static final Pattern NONWORD_PATTERN = Pattern.compile("[^\\p{Alpha}]+");
}
