package challenge.server;

import java.io.PrintStream;

public class Streams {

	private Streams() {
	}

	public static PrintStream nullPrintStream() {
		return INSTANCE;
	}

	private static final PrintStream INSTANCE = new PrintStream(NullOutputStream.INSTANCE);
}
