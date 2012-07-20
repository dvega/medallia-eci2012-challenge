package challenge.lib.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Reads CSV files.</p>
 *
 * <p>You can use:</p>
 * <ul>
 *     <li>{@link #readCell()} to read a cell at a time</li>
 *     <li>{@link #readRow()} to read a row at a time</li>
 *     <li>{@link #readSheet()} to read the whole sheet</li>
 * </ul>
 *
 * <p>Instances of this class are not thread-safe. That is, you can not use the same instance of this class from
 * different threads without some synchronization between threads</p>
 */
public class CsvReader implements Closeable {
	private final LAReader lar;
	private final List<String> list = new ArrayList<>();
	private final StringBuilder word = new StringBuilder();
	private final char fs;
	private boolean eol;

	/**
	 * Creates a CsvReader using ',' (comma) as field separator
	 * @param lar {@link LAReader} used as input
	 */
	public CsvReader(LAReader lar) {
		this(lar, ',');
	}

	/**
	 * Creates a CsvReader
	 * @param lar {@link LAReader} used as input
	 * @param fieldSeparator The character used as field separator
	 */
	public CsvReader(LAReader lar, char fieldSeparator) {
		this.lar = lar;
		this.fs = fieldSeparator;
	}

	private boolean readEscaped() throws IOException {
		int c = lar.read();
		assert c == (int)'"';

		while (true) {
			c = lar.read();
			if (c == -1) break;
			char ch = (char) c;
			if (ch == '"') {
				if (lar.la() != (int)'"')
					break;
				lar.read();
			}
			word.append(ch);
		}

		while (true) {
			c = lar.read();
			if (c == (int)fs) return true;
			if (c == -1 || c == (int) '\n') break;
			if (c == (int)'\r') {
				if (lar.la() == (int)'\n')
					lar.read();
				break;
			}
		}
		return false;
	}

	private boolean readNonEscaped() throws IOException {
		while (true) {
			int c = lar.read();
			if (c == -1) break;
			char ch = (char) c;
			if (ch == fs) return true;
			if (ch == '\n') break;
			if (ch == '\r') {
				if (lar.la() == (int)'\n')
					lar.read();
				break;
			}
			word.append(ch);
		}
		return false;
	}

	/**
	 * Reset the CsvReader to start reading the next line
	 * @return false on EOF, true otherwise
	 * @throws IOException on I/O error
	 * @see #readCell()
	 */
	public boolean nextRow() throws IOException {
		while (readCell() != null);
		eol = false;
		return lar.la() != -1;
	}

	/**
	 * <p>Reads the next cell in the row</p>
	 * <p>If there is no more cell in this row, null is returned. In that case you must call {@link #nextRow()} to
	 * advance to the next row.</p>
	 * @return The value of the next cell, or null at end of row
	 * @throws IOException on I/O error
	 * @see #nextRow()
	 */
	public String readCell() throws IOException {
		if (eol) return null;
		int c = lar.la();
		if (c == -1) return null;

		boolean more;
		word.setLength(0);
		if (c == (int)'"') {
			more = readEscaped();
		} else {
			more = readNonEscaped();
		}

		String result = word.toString();
		word.setLength(0);
		if (!more) eol = true;
		return result;
	}

	/**
	 * <p>Reads a whole row</p>
	 *
	 * <p>If rows are big and memory constraints ar tight, you can read a cell at a time using {@link #readCell()}
	 * instead</p>
	 * @return The next row or null at EOF
	 * @throws IOException on I/O error
	 * @see #readCell()
	 */
	public String[] readRow() throws IOException {
		list.clear();

		String cell;
		while ((cell = readCell()) != null) {
			list.add(cell);
		}
		nextRow();

		if (list.isEmpty()) return null;

		String[] result = new String[list.size()];
		list.toArray(result);
		list.clear();
		return result;
	}

	/**
	 * <p>Reads the whole sheet</p>
	 *
	 * <p>If you do not have enough memory to load the whole sheet, you can use {@link #readRow()} or
	 * {@link #readCell()} instead</p>
	 *
	 * @return The whole Sheet
	 * @throws IOException on I/O error
	 * @see #readRow()
	 * @see #readCell()
	 */
	public String[][] readSheet() throws IOException {
		List<String[]> sheet = new ArrayList<>();

		String[] row;

		while ((row = readRow()) != null) {
			sheet.add(row);
		}

		String[][] result = new String[sheet.size()][];
		sheet.toArray(result);
		return result;
	}

	@Override
	public void close() throws IOException {
		lar.close();
	}
}
