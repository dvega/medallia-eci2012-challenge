package challenge.server;

import java.io.IOException;
import java.io.InputStream;

public class NullInputStream extends InputStream {

	private NullInputStream() {
	}

	@Override
	public int read() throws IOException {
		return -1;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (len > 0 && (off < 0 || off + len > b.length))
			throw new IndexOutOfBoundsException();
		return -1;
	}

	public static InputStream INSTANCE = new NullInputStream();
}
