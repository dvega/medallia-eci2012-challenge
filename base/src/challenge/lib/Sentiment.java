package challenge.lib;

public enum Sentiment {
	NEGATIVE,
	NEUTRAL,
	POSITIVE;

	/**
	 * The double value. Useful for building the classifier and training.
	 *
	 * @return {@link #NEGATIVE} => -1.0 , {@link #NEUTRAL} => 0.0 , {@link #POSITIVE} => +1.0
	 */
	public double getValue() {
		return ordinal() - 1;
	}
}
