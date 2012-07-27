package challenge.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ThreadLocalRandom;

public class Streams {

	private Streams() {
	}

	public static PrintStream nullPrintStream() {
		return INSTANCE;
	}

	public static class LockFile implements AutoCloseable {
		private final File lockFile;

		private LockFile(File lockFile) {
			this.lockFile = lockFile;
		}

		@Override
		public void close() {
			lockFile.delete();
		}
	}

	public static LockFile tryLockFile(File file) throws IOException {
		File lck = new File(file.getPath() + ".lock");
		if (!lck.createNewFile()) return null;
		return new LockFile(lck);
	}

	public static LockFile lockFile(File file) throws IOException {
		ThreadLocalRandom random = ThreadLocalRandom.current();

		long millis = 100;
		do {
			LockFile lck = tryLockFile(file);
			if (lck != null) return lck;
			millis += random.nextInt(100);
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				throw new IOException(e);
			}
		} while (true);

	}


	private static final PrintStream INSTANCE = new PrintStream(NullOutputStream.INSTANCE);
}
