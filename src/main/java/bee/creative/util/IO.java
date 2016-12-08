package bee.creative.util;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;
import bee.creative.array.ByteArray;
import bee.creative.array.ByteArraySection;
import bee.creative.array.CharacterArray;
import bee.creative.array.CharacterArraySection;
import bee.creative.array.CompactByteArray;
import bee.creative.data.ArrayDataSource;
import bee.creative.data.ArrayDataTarget;
import bee.creative.data.BaseDataSource;
import bee.creative.data.BaseDataTarget;
import bee.creative.data.DataSource;
import bee.creative.data.DataTarget;
import bee.creative.data.FileDataSource;
import bee.creative.data.FileDataTarget;

/** Diese Klasse implementiert Methoden zur Erzeugung von Ein- und Ausgabe.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IO {

	/** Diese Methode erzeugt aus dem gegebenen Objekt eine {@link DataSource} und gibt diese zurück.<br>
	 * Hierbei werden folgende Datentypen für {@code object} unterstützt:
	 * <dl>
	 * <dt>{@link DataSource}</dt>
	 * <dd>Die gegebene {@link DataSource} wird geliefert.</dd>
	 * <dt>{@link File}, {@link RandomAccessFile}</dt>
	 * <dd>Es wird eine die gegebene Datei lesende {@link FileDataSource} geliefert.</dd>
	 * <dt>{@code byte[]}, {@link ByteArray}, {@link ByteArraySection}</dt>
	 * <dd>Es wird eine das gegebenen Array lesende {@link ArrayDataSource} geliefert.</dd>
	 * <dt>{@link DataInput}</dt>
	 * <dd>Es wird eine den gegebenen {@link DataInput} lesende {@link DataSource} geliefert, welche keine Navigation bzw. Größenänderung unterstützt.</dd>
	 * <dt>{@link InputStream}</dt>
	 * <dd>Es wird eine den gegebenen {@link InputStream} lesende {@link DataSource} geliefert, welche keine Navigation bzw. Größenänderung unterstützt.</dd>
	 * <dt>{@link ByteBuffer}</dt>
	 * <dd>Es wird eine den gegebenen {@link ByteBuffer} lesende {@link DataSource} geliefert, welche keine Größenänderung unterstützt.</dd>
	 * </dl>
	 *
	 * @param object Objekt.
	 * @return {@link DataSource}.
	 * @throws IOException Wenn der {@link DataSource} nicht erzeugt werden kann. */
	public static DataSource inputDataFrom(final Object object) throws IOException {
		if (object instanceof DataSource) return (DataSource)object;
		if (object instanceof File) return IO._inputDataFrom_((File)object);
		if (object instanceof RandomAccessFile) return IO._inputDataFrom_((RandomAccessFile)object);
		if (object instanceof ByteArraySection) return IO._inputDataFrom_((ByteArraySection)object);
		if (object instanceof ByteArray) return IO._inputDataFrom_((ByteArray)object);
		if (object instanceof byte[]) return IO._inputDataFrom_((byte[])object);
		if (object instanceof DataInput) return IO._inputDataFrom_((DataInput)object);
		if (object instanceof InputStream) return IO._inputDataFrom_((InputStream)object);
		if (object instanceof ByteBuffer) return IO._inputDataFrom_((ByteBuffer)object);
		throw new IOException();
	}

	@SuppressWarnings ("javadoc")
	static DataSource _inputDataFrom_(final ByteBuffer object) {
		return new BaseDataSource() {

			@Override
			public Object data() {
				return object;
			}

			@Override
			public void readFully(final byte[] b, final int off, final int len) throws IOException {
				object.get(b, off, len);
			}

			@Override
			public void seek(final long index) throws IOException {
				try {
					object.position((int)index);
				} catch (final IllegalArgumentException cause) {
					throw new IOException(cause);
				}
			}

			@Override
			public long index() throws IOException {
				return object.position();
			}

			@Override
			public long length() throws IOException {
				return object.limit();
			}

		};
	}

	@SuppressWarnings ("javadoc")
	static DataSource _inputDataFrom_(final byte[] object) {
		return new ArrayDataSource(object);
	}

	@SuppressWarnings ("javadoc")
	static DataSource _inputDataFrom_(final ByteArray object) {
		return new ArrayDataSource(object.section());
	}

	@SuppressWarnings ("javadoc")
	static DataSource _inputDataFrom_(final ByteArraySection object) {
		return new ArrayDataSource(object);
	}

	@SuppressWarnings ("javadoc")
	static DataSource _inputDataFrom_(final File object) throws IOException {
		return new FileDataSource(object);
	}

	@SuppressWarnings ("javadoc")
	static DataSource _inputDataFrom_(final RandomAccessFile object) throws IOException {
		return new FileDataSource(object);
	}

	@SuppressWarnings ("javadoc")
	static DataSource _inputDataFrom_(final DataInput object) {
		return new BaseDataSource() {

			@Override
			public Object data() {
				return object;
			}

			@Override
			public void readFully(final byte[] b, final int off, final int len) throws IOException {
				object.readFully(b, off, len);
			}

			@Override
			public int skipBytes(final int n) throws IOException {
				return object.skipBytes(n);
			}

		};
	}

	@SuppressWarnings ("javadoc")
	static DataSource _inputDataFrom_(final InputStream object) {
		return new BaseDataSource() {

			@Override
			public Object data() {
				return object;
			}

			@Override
			public void readFully(final byte[] b, int off, int len) throws IOException {
				while (len != 0) {
					final int cnt = object.read(b, off, len);
					off += cnt;
					len -= cnt;
				}
			}

			@Override
			public int skipBytes(final int n) throws IOException {
				return (int)object.skip(n);
			}

		};
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link ByteBuffer} zum Lesen und gibt diesen zurück.<br>
	 * Hierbei werden folgende Datentypen für {@code object} unterstützt:
	 * <dl>
	 * <dt>{@link ByteBuffer}</dt>
	 * <dd>Der gegebene {@link ByteBuffer} wird geliefert.</dd>
	 * <dt>{@link File}, {@link FileChannel}, {@link RandomAccessFile}</dt>
	 * <dd>Es wird ein zur gegebenen Datei {@link FileChannel#map(MapMode, long, long) erzeugter} {@link ByteBuffer} geliefert.</dd>
	 * <dt>{@code byte[]}, {@link ByteArray}, {@link ByteArraySection}</dt>
	 * <dd>Es wird ein zum gegebenen Array {@link ByteBuffer#wrap(byte[], int, int) erzeugter} {@link ByteBuffer} geliefert.</dd>
	 * </dl>
	 *
	 * @param object Objekt.
	 * @return {@link ByteBuffer}.
	 * @throws IOException Wenn der {@link ByteBuffer} nicht erzeugt werden kann. */
	public static ByteBuffer inputBufferFrom(final Object object) throws IOException {
		if (object instanceof ByteBuffer) return (ByteBuffer)object;
		if (object instanceof File) return IO._inputBufferFrom_((File)object);
		if (object instanceof FileChannel) return IO._inputBufferFrom_((FileChannel)object);
		if (object instanceof RandomAccessFile) return IO._inputBufferFrom_((RandomAccessFile)object);
		if (object instanceof ByteArraySection) return IO._inputBufferFrom_((ByteArraySection)object);
		if (object instanceof ByteArray) return IO._inputBufferFrom_((ByteArray)object);
		if (object instanceof byte[]) return IO._inputBufferFrom_((byte[])object);
		throw new IOException();
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _inputBufferFrom_(final byte[] object) {
		return ByteBuffer.wrap(object);
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _inputBufferFrom_(final ByteArray object) {
		return IO._inputBufferFrom_(object.section());
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _inputBufferFrom_(final ByteArraySection object) {
		return ByteBuffer.wrap(object.array(), object.startIndex(), object.size());
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _inputBufferFrom_(final File object) throws IOException {
		try (final RandomAccessFile file = new RandomAccessFile(object, "r")) {
			return IO._inputBufferFrom_(file);
		}
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _inputBufferFrom_(final FileChannel object) throws IOException {
		return object.map(MapMode.READ_ONLY, 0, object.size());
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _inputBufferFrom_(final RandomAccessFile object) throws IOException {
		return IO._inputBufferFrom_(object.getChannel());
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link InputStream} und gibt diesen zurück.<br>
	 * Hierbei werden folgende Datentypen für {@code object} unterstützt:
	 * <dl>
	 * <dt>{@link InputStream}</dt>
	 * <dd>Der gegebene {@link InputStream} wird geliefert.</dd>
	 * <dt>{@link File}</dt>
	 * <dd>Es wird ein aus der gegebenen Datei lesender {@link FileInputStream} geliefert.</dd>
	 * <dt>{@code byte[]}, {@link ByteArray}, {@link ByteArraySection}</dt>
	 * <dd>Es wird ein das gegebene Array lesender {@link InputStream} geliefert.</dd>
	 * </dl>
	 *
	 * @param object Objekt.
	 * @return {@link InputStream}.
	 * @throws IOException Wenn der {@link InputStream} nicht erzeugt werden kann. */
	public static InputStream inputStreamFrom(final Object object) throws IOException {
		if (object instanceof InputStream) return (InputStream)object;
		if (object instanceof File) return IO._inputStreamFrom_((File)object);
		if (object instanceof ByteArraySection) return IO._inputStreamFrom_((ByteArraySection)object);
		if (object instanceof ByteArray) return IO._inputStreamFrom_((ByteArray)object);
		if (object instanceof byte[]) return IO._inputStreamFrom_((byte[])object);
		throw new IOException();
	}

	@SuppressWarnings ("javadoc")
	static InputStream _inputStreamFrom_(final File object) throws IOException {
		return new FileInputStream(object);
	}

	@SuppressWarnings ("javadoc")
	static InputStream _inputStreamFrom_(final byte[] object) {
		return new ByteArrayInputStream(object);
	}

	@SuppressWarnings ("javadoc")
	static InputStream _inputStreamFrom_(final ByteArray object) {
		return IO._inputStreamFrom_(object.section());
	}

	@SuppressWarnings ("javadoc")
	static InputStream _inputStreamFrom_(final ByteArraySection object) {
		return new ByteArrayInputStream(object.array(), object.startIndex(), object.size());
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link Reader} und gibt diesen zurück.<br>
	 * Hierbei werden folgende Datentypen für {@code object} unterstützt:
	 * <dl>
	 * <dt>{@link Reader}</dt>
	 * <dd>Der gegebene {@link Reader} wird geliefert.</dd>
	 * <dt>{@link File}</dt>
	 * <dd>Es wird ein zur gegebenen Datei erzeugter {@link FileReader} geliefert.</dd>
	 * <dt>{@link InputStream}</dt>
	 * <dd>Es wird ein zum gegebenen Eingabestrom erzeugter {@link InputStreamReader} geliefert.</dd>
	 * <dt>{@code char[]}, {@link CharacterArray}, {@link CharacterArraySection}</dt>
	 * <dd>Es wird ein zum gegebenen Array erzeugter {@link CharArrayReader} geliefert.</dd>
	 * <dt>{@link String}</dt>
	 * <dd>Es wird ein zur gegebenen Zeichenkette erzeugter {@link StringReader} geliefert.</dd>
	 * </dl>
	 *
	 * @param object Objekt.
	 * @return {@link Reader}.
	 * @throws IOException Wenn der {@link Reader} nicht erzeugt werden kann. */
	public static Reader inputReaderFrom(final Object object) throws IOException {
		if (object instanceof Reader) return (Reader)object;
		if (object instanceof File) return IO._inputReaderFrom_((File)object);
		if (object instanceof InputStream) return IO._inputReaderFrom_((InputStream)object);
		if (object instanceof CharacterArraySection) return IO._inputReaderFrom_((CharacterArraySection)object);
		if (object instanceof CharacterArray) return IO._inputReaderFrom_((CharacterArray)object);
		if (object instanceof char[]) return IO._inputReaderFrom_((char[])object);
		if (object instanceof String) return IO._inputReaderFrom_((String)object);
		throw new IOException();
	}

	@SuppressWarnings ("javadoc")
	static Reader _inputReaderFrom_(final File object) throws IOException {
		return new FileReader(object);
	}

	@SuppressWarnings ("javadoc")
	static Reader _inputReaderFrom_(final char[] object) throws IOException {
		return new CharArrayReader(object);
	}

	@SuppressWarnings ("javadoc")
	static Reader _inputReaderFrom_(final String object) throws IOException {
		return new StringReader(object);
	}

	@SuppressWarnings ("javadoc")
	static Reader _inputReaderFrom_(final InputStream object) throws IOException {
		return new InputStreamReader(object);
	}

	@SuppressWarnings ("javadoc")
	static Reader _inputReaderFrom_(final CharacterArray object) throws IOException {
		return IO._inputReaderFrom_(object.section());
	}

	@SuppressWarnings ("javadoc")
	static Reader _inputReaderFrom_(final CharacterArraySection object) throws IOException {
		return new CharArrayReader(object.array(), object.startIndex(), object.size());
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt ein {@link DataTarget} und gibt dieses zurück.<br>
	 * Hierbei werden folgende Datentypen für {@code object} unterstützt:
	 * <dl>
	 * <dt>{@link DataTarget}</dt>
	 * <dd>Das gegebene {@link DataTarget} wird geliefert.</dd>
	 * <dt>{@link File}, {@link RandomAccessFile}</dt>
	 * <dd>Es wird ein in die gegebenen Datei schreibendes {@link FileDataTarget} geliefert.</dd>
	 * <dt>{@link CompactByteArray}</dt>
	 * <dd>Es wird ein in das gegebenen {@link CompactByteArray} schreibendes {@link ArrayDataTarget} geliefert.</dd>
	 * <dt>{@link ByteArray}</dt>
	 * <dd>Es wird ein in das gegebenen {@link ByteArray} schreibendes {@link DataTarget} geliefert, welches keine Navigation unterstützt.</dd>
	 * <dt>{@link DataOutput}</dt>
	 * <dd>Es wird ein in den gegebenen {@link DataOutput} schreibendes {@link DataTarget} geliefert, welches keine Navigation bzw. Größenänderung
	 * unterstützt.</dd>
	 * <dt>{@link OutputStream}</dt>
	 * <dd>Es wird ein in den gegebenen {@link OutputStream} schreibendes {@link DataTarget} geliefert, welches keine Navigation bzw. Größenänderung
	 * unterstützt.</dd>
	 * <dt>{@link ByteBuffer}</dt>
	 * <dd>Es wird ein in den gegebenen {@link ByteBuffer} schreibendes {@link DataTarget} geliefert, welches keine Größenänderung unterstützt.</dd>
	 * </dl>
	 *
	 * @param object Objekt.
	 * @return {@link DataTarget}.
	 * @throws IOException Wenn der {@link DataTarget} nicht erzeugt werden kann. */
	public static DataTarget outputDataFrom(final Object object) throws IOException {
		if (object instanceof DataTarget) return (DataTarget)object;
		if (object instanceof File) return IO._outputDataFrom_((File)object);
		if (object instanceof RandomAccessFile) return IO._outputDataFrom_((RandomAccessFile)object);
		if (object instanceof CompactByteArray) return IO._outputDataFrom_((CompactByteArray)object);
		if (object instanceof ByteArray) return IO._outputDataFrom_((ByteArray)object);
		if (object instanceof DataOutput) return IO._outputDataFrom_((DataOutput)object);
		if (object instanceof OutputStream) return IO._outputDataFrom_((OutputStream)object);
		if (object instanceof ByteBuffer) return IO._outputDataFrom_((ByteBuffer)object);
		throw new IOException();
	}

	@SuppressWarnings ("javadoc")
	static DataTarget _outputDataFrom_(final File object) throws IOException {
		return new FileDataTarget(object);
	}

	@SuppressWarnings ("javadoc")
	static DataTarget _outputDataFrom_(final RandomAccessFile object) throws IOException {
		return new FileDataTarget(object);
	}

	@SuppressWarnings ("javadoc")
	static DataTarget _outputDataFrom_(final CompactByteArray object) throws IOException {
		return new ArrayDataTarget(object);
	}

	@SuppressWarnings ("javadoc")
	static DataTarget _outputDataFrom_(final ByteArray object) {
		return new BaseDataTarget() {

			@Override
			public Object data() {
				return object;
			}

			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				object.add(ByteArraySection.from(b, off, off + len));
			}

		};
	}

	@SuppressWarnings ("javadoc")
	static DataTarget _outputDataFrom_(final ByteBuffer object) {
		return new BaseDataTarget() {

			@Override
			public Object data() {
				return object;
			}

			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				object.put(b, off, len);
			}

			@Override
			public void seek(final long index) throws IOException {
				try {
					object.position((int)index);
				} catch (final IllegalArgumentException cause) {
					throw new IOException(cause);
				}
			}

			@Override
			public long index() throws IOException {
				return object.position();
			}

			@Override
			public long length() throws IOException {
				return object.limit();
			}

		};
	}

	@SuppressWarnings ("javadoc")
	static DataTarget _outputDataFrom_(final DataOutput object) {
		return new BaseDataTarget() {

			@Override
			public Object data() {
				return object;
			}

			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				object.write(b, off, len);
			}

		};
	}

	@SuppressWarnings ("javadoc")
	static DataTarget _outputDataFrom_(final OutputStream object) {
		return new BaseDataTarget() {

			@Override
			public Object data() {
				return object;
			}

			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				object.write(b, off, len);
			}

			@Override
			public void close() throws IOException {
				object.close();
			}

		};
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link ByteBuffer} zum Schreiben und gibt diesen zurück.<br>
	 * Hierbei werden folgende Datentypen für {@code object} unterstützt:
	 * <dl>
	 * <dt>{@link ByteBuffer}</dt>
	 * <dd>Der gegebene {@link ByteBuffer} wird geliefert.</dd>
	 * <dt>{@link File}, {@link FileChannel}, {@link RandomAccessFile}</dt>
	 * <dd>Es wird ein zur gegebenen Datei {@link FileChannel#map(MapMode, long, long) erzeugter} {@link ByteBuffer} geliefert.</dd>
	 * <dt>{@code byte[]}, {@link ByteArray}, {@link ByteArraySection}</dt>
	 * <dd>Es wird ein zum gegebenen Array {@link ByteBuffer#wrap(byte[], int, int) erzeugter} {@link ByteBuffer} geliefert.</dd>
	 * </dl>
	 *
	 * @param object Objekt.
	 * @return {@link ByteBuffer}.
	 * @throws IOException Wenn der {@link ByteBuffer} nicht erzeugt werden kann. */
	public static ByteBuffer outputBufferFrom(final Object object) throws IOException {
		if (object instanceof ByteBuffer) return (ByteBuffer)object;
		if (object instanceof File) return IO._outputBufferFrom_((File)object);
		if (object instanceof FileChannel) return IO._outputBufferFrom_((FileChannel)object);
		if (object instanceof RandomAccessFile) return IO._outputBufferFrom_((RandomAccessFile)object);
		if (object instanceof ByteArraySection) return IO._outputBufferFrom_((ByteArraySection)object);
		if (object instanceof ByteArray) return IO._outputBufferFrom_((ByteArray)object);
		if (object instanceof byte[]) return IO._outputBufferFrom_((byte[])object);
		throw new IOException();
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _outputBufferFrom_(final File object) throws IOException {
		try (final RandomAccessFile file = new RandomAccessFile(object, "rw")) {
			return IO._outputBufferFrom_(file);
		}
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _outputBufferFrom_(final FileChannel object) throws IOException {
		return object.map(MapMode.READ_WRITE, 0, object.size());
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _outputBufferFrom_(final RandomAccessFile object) throws IOException {
		return IO._outputBufferFrom_(object.getChannel());
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _outputBufferFrom_(final byte[] object) {
		return ByteBuffer.wrap(object);
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _outputBufferFrom_(final ByteArray object) {
		return IO._outputBufferFrom_(object.section());
	}

	@SuppressWarnings ("javadoc")
	static ByteBuffer _outputBufferFrom_(final ByteArraySection object) {
		return ByteBuffer.wrap(object.array(), object.startIndex(), object.size());
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link OutputStream} und gibt diesen zurück.<br>
	 * Hierbei werden folgende Datentypen für {@code object} unterstützt:
	 * <dl>
	 * <dt>{@link OutputStream}</dt>
	 * <dd>Der gegebene {@link OutputStream} wird geliefert.</dd>
	 * <dt>{@link File}</dt>
	 * <dd>Es wird ein in die gegebenen Datei schreibender {@link FileOutputStream} geliefert.</dd>
	 * <dt>{@link ByteArray}</dt>
	 * <dd>Es wird ein in das gegebenen {@link ByteArray} schreibender {@link OutputStream} geliefert.</dd>
	 * </dl>
	 *
	 * @param object Objekt.
	 * @return {@link OutputStream}.
	 * @throws IOException Wenn der {@link OutputStream} nicht erzeugt werden kann. */
	public static OutputStream outputStreamFrom(final Object object) throws IOException {
		if (object instanceof OutputStream) return (OutputStream)object;
		if (object instanceof File) return IO._outputStreamFrom_((File)object);
		if (object instanceof ByteArray) return IO._outputStreamFrom_((ByteArray)object);
		throw new IOException();
	}

	@SuppressWarnings ("javadoc")
	static OutputStream _outputStreamFrom_(final File object) throws IOException {
		return new FileOutputStream(object);
	}

	@SuppressWarnings ("javadoc")
	static OutputStream _outputStreamFrom_(final ByteArray object) throws IOException {
		return new OutputStream() {

			@Override
			public void write(final int b) throws IOException {
				object.add((byte)b);
			}

			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				object.add(ByteArraySection.from(b, off, len));
			}

		};
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link Writer} und gibt diesen zurück.<br>
	 * Hierbei werden folgende Datentypen für {@code object} unterstützt:
	 * <dl>
	 * <dt>{@link Writer}</dt>
	 * <dd>Der gegebene {@link Writer} wird geliefert.</dd>
	 * <dt>{@link File}</dt>
	 * <dd>Es wird ein in die gegebenen Datei schreibender {@link FileWriter} geliefert.</dd>
	 * <dt>{@link OutputStream}</dt>
	 * <dd>Es wird ein in den gegebenen {@link OutputStream} schreibender {@link OutputStreamWriter} geliefert.</dd>
	 * <dt>{@link CharacterArray}</dt>
	 * <dd>Es wird ein in das gegebenen {@link CharacterArray} schreibender {@link Writer} geliefert.</dd>
	 * <dt>{@link StringBuilder}</dt>
	 * <dd>Es wird ein in den gegebenen {@link StringBuilder} schreibender {@link Writer} geliefert.</dd>
	 * </dl>
	 *
	 * @param object Objekt.
	 * @return {@link OutputStream}.
	 * @throws IOException Wenn der {@link OutputStream} nicht erzeugt werden kann. */
	public static Writer outputWriterFrom(final Object object) throws IOException {
		if (object instanceof Writer) return (Writer)object;
		if (object instanceof File) return IO._outputWriterFrom_((File)object);
		if (object instanceof OutputStream) return IO._outputWriterFrom_((OutputStream)object);
		if (object instanceof CharacterArray) return IO._outputWriterFrom_((CharacterArray)object);
		if (object instanceof StringBuilder) return IO._outputWriterFrom_((StringBuilder)object);
		throw new IOException();
	}

	@SuppressWarnings ("javadoc")
	static Writer _outputWriterFrom_(final File object) throws IOException {
		return new FileWriter(object);
	}

	@SuppressWarnings ("javadoc")
	static Writer _outputWriterFrom_(final OutputStream object) throws IOException {
		return new OutputStreamWriter(object);
	}

	@SuppressWarnings ("javadoc")
	static Writer _outputWriterFrom_(final CharacterArray object) {
		return new Writer() {

			@Override
			public void write(final char[] cbuf, final int off, final int len) throws IOException {
				object.add(CharacterArraySection.from(cbuf, off, len));
			}

			@Override
			public void flush() throws IOException {
			}

			@Override
			public void close() throws IOException {
			}

		};
	}

	@SuppressWarnings ("javadoc")
	static Writer _outputWriterFrom_(final StringBuilder object) {
		return new Writer() {

			@Override
			public void write(final char[] cbuf, final int off, final int len) throws IOException {
				object.append(CharacterArraySection.from(cbuf, off, len));
			}

			@Override
			public void flush() throws IOException {
			}

			@Override
			public void close() throws IOException {
			}

		};
	}

	/** Diese Methode gibt ein {@link Iterable} über die gegebenen Dateien und Verzeichnisse sowie ggf. derer in {@link File#listFiles() Unterverzeichnissen}
	 * zurück.
	 *
	 * @see File#listFiles()
	 * @param maxDepth Maximale Tiefe für die rekursive Iteration über {@link File#listFiles() Unterverzeichnisse}.<br>
	 *        Wenn diese {@code 0} ist, wird nur über die gegebenen Dateien und Verzeichnise iteriert. Andernfalls wird in den gegebenen Verzeichnissen rekursiv
	 *        mit maximaler Tiefe {@code maxDepth-1} rekursiv weiter iteriert.
	 * @param files Dateien und Verzeichnisse oder {@code null}.
	 * @return {@link Iterable} über {@link File}. */
	public static Iterable<File> listFiles(final int maxDepth, final File... files) {
		if (files == null) return Iterables.emptyIterable();
		return IO.listFiles(maxDepth, Arrays.asList(files));
	}

	/** Diese Methode gibt ein {@link Iterable} über die gegebenen Dateien und Verzeichnisse sowie ggf. derer in {@link File#listFiles() Unterverzeichnissen}
	 * zurück.
	 *
	 * @see #listFiles(int, File...)
	 * @param maxDepth Maximale Tiefe für die rekursive Iteration (siehe {@link #listFiles(int, File...)}).
	 * @param files Dateien und Verzeichnisse oder {@code null}.
	 * @return {@link Iterable} über {@link File}. */
	public static Iterable<File> listFiles(final int maxDepth, final Iterable<File> files) {
		if (files == null) return Iterables.emptyIterable();
		if (maxDepth == 0) return files;
		return Iterables.chainedIterable(Iterables.convertedIterable(new Converter<File, Iterable<File>>() {

			@Override
			public Iterable<File> convert(final File file) {
				final File[] list = file.listFiles();
				if (list == null) return Iterables.itemIterable(file);
				return Iterables.chainedIterable(Iterables.itemIterable(file), IO.listFiles(maxDepth - 1, list));
			}

		}, files));
	}

}
