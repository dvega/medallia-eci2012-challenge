package challenge.run;

import challenge.lib.Classifier;
import challenge.lib.ClassifierBuilder;
import challenge.lib.TaggedReview;
import challenge.lib.utils.Utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Main {

	private static final int NFOLD = 5;

	private static double trainAndValidate(Iterable<TaggedReview> trainIterable, Iterable<TaggedReview> validateIterable) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
		ClassifierBuilder cb = (ClassifierBuilder) Class.forName(Utils.CLASSIFIER_NAME).newInstance();
		Classifier classifier = cb.training(trainIterable);
		return Utils.computeMSE(classifier, validateIterable);
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
		double sumMse = 0;

		// n-fold cross validation
		for (int i=0; i< NFOLD; i++) {
			sumMse += trainAndValidate(buildTrainIterable(i), buildValidationIterable(i));
		}

		double mse = sumMse / NFOLD;
		System.out.printf("Mean Square Error=%f%n", mse);
	}

	private static Iterable<TaggedReview> buildTrainIterable(int mark) {
		return buildNFoldIterable(mark, buildSampleIterable());
	}

	private static Iterable<TaggedReview> buildValidationIterable(int mark) {
		return buildNFoldIterable(-mark - 1, buildSampleIterable());
	}

	private static Iterable<TaggedReview> buildSampleIterable() {
		return Utils.buildReviewsIterable("reviews-sample.csv");
	}

	/** @see #buildNFoldIterable(int, Iterable) */
	private static <E> Iterator<E> buildNFoldIterator(final int mark, final Iterator<E> delegate) {
		final int pmark = mark >= 0 ? mark : -mark - 1;

		return new Iterator<E>() {
			E nextElement;
			int fold;

			@Override
			public boolean hasNext() {
				E next = nextElement;

				if (next != null) return true;

				while (delegate.hasNext()) {
					next = delegate.next();
					fold = (fold + 1) % NFOLD;
					if (fold == pmark ^ mark >= 0)
						break;
				}

				nextElement = next;
				return next != null;
			}

			@Override
			public E next() {
				if (!hasNext())
					throw new NoSuchElementException();

				E result = nextElement;
				nextElement = null;
				return result;
			}

			@Override
			public void remove() {
				delegate.remove();
			}
		};
	}

	/**
	 * Builds an {@link Iterable} that will skip some elements of another Iterable according to {@code mark} argument
	 *
	 * @param mark if {@code 0 <= mark < NFOLD} the Iterable will skip every item such as {@code index % NFOLD == mark}.
	 *             To reverse the behaviour (skip the other items) you must call this method with {@code -mark -1}
	 *
	 * @param delegate The base {@link Iterable} used to get the elements
	 * @return A skipping {@link Iterable}
	 */
	private static <E> Iterable<E> buildNFoldIterable(final int mark, final Iterable<E> delegate) {
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return buildNFoldIterator(mark, delegate.iterator());
			}
		};
	}
}
