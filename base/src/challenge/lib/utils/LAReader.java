package challenge.lib.utils;

import java.io.IOException;
import java.io.Reader;

/**
 * <p>Look-ahead reader</p>
 *
 * <p>A reader that lets you peep the next chars</p>
 */
public class LAReader extends Reader {
	private final Reader in;
	private final char[] buff = new char[128];
	private int p;
	private int count;


	public LAReader(Reader in) {
		this.in = in;
	}

	private void consume(int n) {
		assert count >= n;

		count -= n;

		if (count <= 0) {
			p = 0;
			return;
		}

		p += n;
		if (p >= buff.length)
			p -= buff.length;
	}


	private int bufferRead(char[] cbuf, int off, int len) {
		final int blen = buff.length;

		int nread = 0;

		while (count > 0 && nread < len) {
			int n = Math.min(Math.min(count, len), blen - p);
			System.arraycopy(buff, p, cbuf, off, n);
			nread += n;
			off += n;
			consume(n);
		}

		return nread;
	}

	@Override
	public int read() throws IOException {
		int ch = la();
		if (ch != -1)
			consume(1);
		return ch;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		final int olen = len;

		while (len > 0) {
			if (count == 0 && len < buff.length)
				fill(buff.length);

			int n;
			if (count > 0)
				n = bufferRead(cbuf, off, len);
			else
				n = in.read(cbuf, off, len);

			if (n == -1) {
				if (olen != len) break;
				return -1;
			}

			len -= n;
			off += n;
		}

		return olen - len;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	private void fill(int num) throws IOException {
		if (num > buff.length)
			throw new IllegalArgumentException("LA too large: " + num);

		while (num > count) {
			int p1 = p + count;
			int n;

			if (p1 >= buff.length) {
				p1 -= buff.length;
				n = p - p1;
			} else {
				n = buff.length - p1;
			}

			int nread = in.read(buff, p1, n);

			if (nread < 0)
				return;

			count += nread;
		}
	}

	/** Lookahead */
	public int la(int offset) throws IOException {
		fill(offset+1);

		if (count <= offset)
			return -1;

		int p1 = p + offset;
		if (p1 >= buff.length)
			p1 -= buff.length;

		return buff[p1];
	}

	/** Lookahead */
	public int la() throws IOException {
		return la(0);
	}
}
