package challenge.lib;

/**
 * Startup challenge interface. You main class must implement this interface and have a no-argument constructor..
 * <p>
 * With the provided execution framework, the {@link #training(Iterable)} method will be called several times with
 * different subset of the sample set. You should not keep information on static variable about previous sets.
 * This is pointless because during the competition this method will be called just once.
 * <p>
 * The implementation must not access the filesystem or the network. It will be run with restricted security permission,
 * similar to a Java Applet in a browser.
 */
public interface ClassifierBuilder {
	/**
	 * Startup method for the classifier. This method receives the sample tagged reviews. The code must learn from
	 * these examples, and build a classifier
	 *
	 * @param data Sample tagged reviews
	 * @return  A classifier built on the sample data
	 */
	Classifier training(Iterable<TaggedReview> data);
}
