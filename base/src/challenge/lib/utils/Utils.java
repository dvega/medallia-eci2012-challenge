package challenge.lib.utils;

import challenge.lib.Classifier;
import challenge.lib.Sentiment;
import challenge.lib.TaggedReview;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Utils {

	public static final String CLASSIFIER_NAME = "com.medallia.eci.EciClassifierBuilder";

	private Utils() { }

	public static CsvReader openCsvResource(String resourceName) {
		try {
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
			Reader reader = new InputStreamReader(is, "UTF8");
			LAReader lar = new LAReader(reader);
			return new CsvReader(lar);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static Iterable<TaggedReview> buildReviewsIterable(final String resourceName) {

		return new Iterable<TaggedReview>() {
			@Override
			public Iterator<TaggedReview> iterator() {
				return buildReviewsIterator(resourceName);
			}
		};
	}

	private static Iterator<TaggedReview> buildReviewsIterator(final String resourceName) {
		return new Iterator<TaggedReview>() {
			CsvReader csvr = openCsvResource(resourceName);
			String[] nextRow;

			@Override
			public boolean hasNext() {
				if (csvr == null) return false;

				String[] next = nextRow;

				if (next == null) {
					try {
						nextRow = next = csvr.readRow();

						if (next == null) {
							csvr.close();
							csvr = null;
						}
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
				}

				return next != null;
			}

			@Override
			public TaggedReview next() {
				if (!hasNext())
					throw new NoSuchElementException();

				String[] result = nextRow;
				nextRow = null;
				return new TaggedReview(Sentiment.valueOf(result[0]), result[1]);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static double computeMSE(Classifier classifier, Iterable<TaggedReview> testIterable) {
		double sumSquareError = 0;
		int num = 0;

		for (TaggedReview taggedReview : testIterable) {
			double predicted = classifier.classify(taggedReview.review);
			// Trunc the range
			if (predicted > +1) predicted = +1;
			else if (predicted< -1) predicted = -1;
			double delta = (taggedReview.sentiment.getValue() - predicted);
			sumSquareError += delta*delta;
			num++;
		}

		return sumSquareError / num;
	}
}
