package challenge.server;

import challenge.lib.Classifier;
import challenge.lib.ClassifierBuilder;
import challenge.lib.TaggedReview;
import challenge.lib.utils.Utils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;


public class Runner {
	private static final PrintStream svOut = System.out;
	private static final PrintStream svErr = System.err;
	private static final InputStream svIn = System.in;

	private static final int TRAIN_COUNT = 5000;


	public static void main(String[] args) {
		if (args.length != 2)
			return;

		try {
			// Redirect standard IO to null to avoid filling filesystem with logs attacks.
			System.setOut(Streams.nullPrintStream());
			System.setErr(Streams.nullPrintStream());
			System.setIn(NullInputStream.INSTANCE);

			executeJar(new File(args[0]), new File(args[1]));
		}
		finally {
			System.setOut(svOut);
			System.setErr(svErr);
			System.setIn(svIn);
		}


	}

	public static void log(String msg) {
		svOut.printf("%tF %<tT.%<tL: %s%n", System.currentTimeMillis(), msg);
	}

	private static void executeJar(File jar, File resultsFile) {
		Thread workThread = new Thread(jarRunnable(jar, resultsFile), "jar-runner");
		workThread.setDaemon(true);

		workThread.start();

		// Wait 30 minutes
		waitFinished(workThread, 1_800_000);
		if (!workThread.isAlive()) return;

		log("Execution timeout! Killing thread");
		workThread.interrupt();
		waitFinished(workThread, 5_000);
		if (!workThread.isAlive()) return;

		log("trying harder...");
		workThread.stop();
		waitFinished(workThread, 5_000);
		if (!workThread.isAlive()) return;

		log("trying harder...");
		workThread.stop(dummyException());
		waitFinished(workThread, 5_000);
		if (!workThread.isAlive()) return;

		log("panic! kill failed!!!");
	}

	private static Exception dummyException() {
		return new Exception() { };
	}

	private static void sendMail(String address, Throwable t) throws MessagingException, UnsupportedEncodingException {
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", "mail.medallia.com");
		Session session = Session.getInstance(props);
		MimeMessage message = new MimeMessage(session);
		InternetAddress fromAddress = new InternetAddress("ecichallenge@medallia.com", "ECI Challenge");
		message.setFrom(fromAddress);
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(address));
		message.setRecipient(Message.RecipientType.BCC, fromAddress);
		message.setSubject("[Coding Challenge] Error executing JAR");
		String stackTrace = makeStackTrace(t);
		message.setText(stackTrace);
		Transport.send(message);
	}

	private static String makeStackTrace(Throwable t) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(bos);
		t.printStackTrace(printStream);
		printStream.flush();
		return new String(bos.toByteArray());
	}

	private static String getEmailAddress(File jar) {
		String jarName = jar.getName();
		int p = jarName.lastIndexOf('-');
		int c = jarName.indexOf('-');
		if (c < 0 || p < 0) return null;
		return jarName.substring(c + 1, p);
	}

	private static Runnable jarRunnable(final File jar, final File resultsFile) {
		return new Runnable() {
			@Override
			public void run() {
				double mse;

				try {
					Iterable<TaggedReview> reviews = buildValidationIterable();
					Classifier classifier = buildClassifier(reviews, jar);
					mse = computeMSE(reviews, classifier);
				} catch (Throwable e) {
					e.printStackTrace(svErr);
					sendMail(jar, e);
					return;
				}

				try {
					writeResults(mse, jar.getName(), resultsFile);
					log("Success! MSE=" + mse);
				} catch (IOException e) {
					e.printStackTrace(svErr);
				}
			}
		};
	}

	private static void sendMail(File jar, Throwable e) {
		String emailAddress = getEmailAddress(jar);
		if (emailAddress == null) return;

		log("Sending email to " + emailAddress);

		try {
			sendMail(emailAddress, e);
		} catch (MessagingException | UnsupportedEncodingException e1) {
			log("Error sending email: " + e1.getMessage());
		}
	}

	private static double computeMSE(Iterable<TaggedReview> reviews, Classifier classifier) {
		Iterable<TaggedReview> tailIterable = buildTailIterable(TRAIN_COUNT, reviews);
		return Utils.computeMSE(classifier, tailIterable);
	}

	private static Classifier buildClassifier(Iterable<TaggedReview> reviews, File jar) throws ClassNotFoundException, MalformedURLException, InstantiationException, IllegalAccessException {
		ClassifierBuilder builder = loadBuilder(jar);
		Iterable<TaggedReview> headIterable = buildHeadIterable(TRAIN_COUNT, reviews);
		return builder.training(headIterable);
	}

	private static ClassifierBuilder loadBuilder(File jar) throws ClassNotFoundException, MalformedURLException, InstantiationException, IllegalAccessException {
		Class<?> classifierClass = Class.forName(Utils.CLASSIFIER_NAME, true, buildClassLoader(jar));
		return (ClassifierBuilder)classifierClass.newInstance();
	}

	private static URLClassLoader buildClassLoader(File jar) throws MalformedURLException {
		return new URLClassLoader(new URL[]{jar.toURI().toURL()});
	}

	private static void writeResults(double mse, String jarName, File resultsFile) throws IOException {
		try (Streams.LockFile lockFile = Streams.lockFile(resultsFile);
				OutputStream stream = new FileOutputStream(resultsFile, true)) {
			Writer writer = new OutputStreamWriter(stream);
			String line = String.format("%f %s%n", mse, jarName);
			writer.write(line);
			writer.flush();
			writer.close();
		}
	}

	private static Iterable<TaggedReview> buildValidationIterable() {
		return Utils.buildReviewsIterable("reviews-validate.csv");
	}

	private static <E> Iterable<E> buildHeadIterable(final int num, final Iterable<E> delegate) {
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				Iterator<E> itr = AccessController.doPrivileged(new PrivilegedAction<Iterator<E>>() {
					@Override
					public Iterator<E> run() {
						return delegate.iterator();
					}
				});
				return buildHeadIterator(num, itr);
			}
		};
	}

	private static <E> Iterator<E> buildHeadIterator(final int num, final Iterator<E> delegate) {
		return new Iterator<E>() {
			int count = num;
			@Override
			public boolean hasNext() {
				return count > 0 && delegate.hasNext();
			}

			@Override
			public E next() {
				if (!hasNext())
					throw new NoSuchElementException();

				E result = delegate.next();
				count--;
				return result;
			}

			@Override
			public void remove() {
				delegate.remove();
			}
		};
	}

	private static <E> Iterable<E> buildTailIterable(final int num, final Iterable<E> delegate) {
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				Iterator<E> itr = delegate.iterator();
				for (int n=0; n<num && itr.hasNext(); n++)
					itr.next();
				return itr;
			}
		};
	}


	private static void waitFinished(Thread workThread, int millis) {
		try {
			workThread.join(millis);
		} catch (InterruptedException e) {
			e.printStackTrace(svErr);
		}
	}


}
