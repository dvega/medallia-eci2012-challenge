package challenge.server;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {

	public static final OutputStream INSTANCE = new NullOutputStream();

	private NullOutputStream() {
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (len > 0 && (off < 0 || off + len > b.length))
			throw new IndexOutOfBoundsException();
	}

	@Override
	public void write(int b) throws IOException {
	}


}
