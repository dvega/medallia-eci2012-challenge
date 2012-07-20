package challenge.lib.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

public class CsvWriter implements Closeable {
	private final Writer out;
	private final char fs;

	public CsvWriter(Writer out) {
		this(out, ',');
	}

	public CsvWriter(Writer out, char fs) {
		this.out = out;
		this.fs = fs;
	}

	private void writeValue(String value) throws IOException {
		boolean quoting = false;
		for (int i=0, len=value.length(); i<len; i++) {
			final char ch = value.charAt(i);
			// checking if needs quoting
			if (!quoting) {
				if (ch=='"' || ch=='\r' || ch == '\n'|| ch == fs) {
	                quoting = true;
					out.write('"');
					out.write(value, 0, i);
				}
			}

			// Quoting confirmed
			if (quoting) {
				if (ch == '"') out.write('"');
				out.write(ch);
			}
		}

		if (quoting)
			out.write('"');
		else
			out.write(value);
	}

	public void writeCvsLine(String... values) throws IOException {
		for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
			if (i>0) out.write(fs);
			writeValue(values[i]);
		}
		// RFC 4180 defines line-separator as CRLF
		out.write("\r\n");
	}

	@Override
	public void close() throws IOException {
		out.close();
	}
}
