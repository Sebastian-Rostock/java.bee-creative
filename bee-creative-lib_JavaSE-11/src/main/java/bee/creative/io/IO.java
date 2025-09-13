package bee.creative.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.io.StringWriter;
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
import bee.creative.lang.Bytes;
import bee.creative.util.Getter;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert Methoden zur Erzeugung von Ein- und Ausgabe.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IO {

	/** Diese Methode kopiert den Inhalt des gegebenen {@link InputStream} in den gegebenen {@link OutputStream}.
	 *
	 * @param input Quellobjekt.
	 * @param output Zielobjekt.
	 * @return Anzahl der kopierten Bytes.
	 * @throws IOException - */
	public static long copyBytes(final InputStream input, final OutputStream output) throws IOException {
		final byte[] buffer = new byte[256 * 1024];
		long result = 0;
		while (true) {
			final int count = input.read(buffer);
			if (count < 0) return result;
			output.write(buffer, 0, count);
			result += count;
		}
	}

	/** Diese Methode kopiert den Inhalt des gegebenen {@link Reader} in den gegebenen {@link Writer}.
	 *
	 * @param input Quellobjekt.
	 * @param output Zielobjekt.
	 * @return Anzahl der kopierten Zeichen.
	 * @throws IOException - */
	public static long copyChars(final Reader input, final Writer output) throws IOException {
		final char[] buffer = new char[128 * 1024];
		long result = 0;
		while (true) {
			final int count = input.read(buffer);
			if (count < 0) return result;
			output.write(buffer, 0, count);
			result += count;
		}
	}

	/** Diese Methode liest den Inhalt des {@link #inputStreamFrom(Object) zum gegebenen Quellobjekt ermittelten} {@link InputStream} und gibt ihn als Bytefolge
	 * zurück.
	 *
	 * @param input Quellobjekt.
	 * @return Bytefolge.
	 * @throws IOException - */
	public static byte[] readBytes(final Object input) throws IOException {
		try (InputStream stream = IO.inputStreamFrom(input); ByteArrayOutputStream result = new ByteArrayOutputStream()) {
			IO.copyBytes(stream, result);
			return result.toByteArray();
		}
	}

	/** Diese Methode liest den Inhalt des {@link #inputReaderFrom(Object) zum gegebenen Quellobjekt ermittelten} {@link Reader} und gibt ihn als Zeichenkette
	 * zurück.
	 *
	 * @param input Quellobjekt.
	 * @return Zeichenkette.
	 * @throws IOException - */
	public static String readChars(final Object input) throws IOException {
		try (Reader reader = IO.inputReaderFrom(input); StringWriter result = new StringWriter()) {
			IO.copyChars(reader, result);
			return result.toString();
		}
	}

	/** Diese Methode schreibt die gegebene Bytefolge in den {@link #outputStreamFrom(Object) zum gegebenen Zielobjekt ermittelten} {@link OutputStream}.
	 *
	 * @param output Zielobjekt.
	 * @param value Bytefolge.
	 * @throws IOException - */
	public static void writeBytes(final Object output, final byte[] value) throws IOException {
		try (OutputStream stream = IO.outputStreamFrom(output)) {
			stream.write(value);
		}
	}

	/** Diese Methode schreibt die gegebene Zeichenkette in den {@link #outputWriterFrom(Object) zum gegebenen Zielobjekt ermittelten} {@link Writer}.
	 *
	 * @param output Zielobjekt.
	 * @param value Zeichenkette.
	 * @throws IOException - */
	public static void writeChars(final Object output, final String value) throws IOException {
		try (Writer writer = IO.outputWriterFrom(output)) {
			writer.write(value);
		}
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt eine {@link DataSource} und gibt diese zurück. Hierbei werden folgende Datentypen für {@code object}
	 * unterstützt:
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
		if (object instanceof File) return new FileDataSource((File)object);
		if (object instanceof RandomAccessFile) return new FileDataSource((RandomAccessFile)object);
		if (object instanceof ByteArraySection) return new ArrayDataSource((ByteArraySection)object);
		if (object instanceof ByteArray) return new ArrayDataSource((ByteArray)object);
		if (object instanceof byte[]) return new ArrayDataSource((byte[])object);
		if (object instanceof DataInput) return new InputDataSource((DataInput)object);
		if (object instanceof InputStream) return new StreamDataSource((InputStream)object);
		if (object instanceof ByteBuffer) return new BufferDataSource((ByteBuffer)object);
		throw new IOException();
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link ByteBuffer} zum Lesen und gibt diesen zurück. Hierbei werden folgende Datentypen für
	 * {@code object} unterstützt:
	 * <dl>
	 * <dt>{@link ByteBuffer}</dt>
	 * <dd>Der gegebene {@link ByteBuffer} wird geliefert.</dd>
	 * <dt>{@link File}, {@link FileChannel}, {@link RandomAccessFile}</dt>
	 * <dd>Es wird ein zur gegebenen Datei {@link FileChannel#map(MapMode, long, long) erzeugter} {@link ByteBuffer} in nativer Bytereihenfolge geliefert.</dd>
	 * <dt>{@code byte[]}, {@link ByteArray}, {@link ByteArraySection}</dt>
	 * <dd>Es wird ein zum gegebenen Array {@link ByteBuffer#wrap(byte[], int, int) erzeugter} {@link ByteBuffer} in nativer Bytereihenfolge geliefert.</dd>
	 * </dl>
	 *
	 * @param object Objekt.
	 * @return {@link ByteBuffer}.
	 * @throws IOException Wenn der {@link ByteBuffer} nicht erzeugt werden kann. */
	public static ByteBuffer inputBufferFrom(final Object object) throws IOException {
		if (object instanceof ByteBuffer) return (ByteBuffer)object;
		if (object instanceof File) return IO.inputBufferFrom((File)object);
		if (object instanceof FileChannel) return IO.inputBufferFrom((FileChannel)object);
		if (object instanceof RandomAccessFile) return IO.inputBufferFrom((RandomAccessFile)object);
		if (object instanceof ByteArraySection) return IO.inputBufferFrom((ByteArraySection)object);
		if (object instanceof ByteArray) return IO.inputBufferFrom((ByteArray)object);
		if (object instanceof byte[]) return IO.inputBufferFrom((byte[])object);
		throw new IOException();
	}

	static ByteBuffer inputBufferFrom(final byte[] object) {
		return ByteBuffer.wrap(object).order(Bytes.NATIVE_ORDER);
	}

	static ByteBuffer inputBufferFrom(final ByteArray object) {
		return IO.inputBufferFrom(object.section());
	}

	static ByteBuffer inputBufferFrom(final ByteArraySection object) {
		return ByteBuffer.wrap(object.array(), object.offset(), object.length()).order(Bytes.NATIVE_ORDER);
	}

	static ByteBuffer inputBufferFrom(final File object) throws IOException {
		try (final RandomAccessFile file = new RandomAccessFile(object, "r")) {
			return IO.inputBufferFrom(file);
		}
	}

	static ByteBuffer inputBufferFrom(final FileChannel object) throws IOException {
		return object.map(MapMode.READ_ONLY, 0, object.size()).order(Bytes.NATIVE_ORDER);
	}

	static ByteBuffer inputBufferFrom(final RandomAccessFile object) throws IOException {
		return IO.inputBufferFrom(object.getChannel());
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link InputStream} und gibt diesen zurück. Hierbei werden folgende Datentypen für {@code object}
	 * unterstützt:
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
		if (object instanceof File) return new FileInputStream((File)object);
		if (object instanceof ByteArraySection) return IO.inputStreamFrom((ByteArraySection)object);
		if (object instanceof ByteArray) return IO.inputStreamFrom((ByteArray)object);
		if (object instanceof byte[]) return new ByteArrayInputStream((byte[])object);
		throw new IOException();
	}

	static InputStream inputStreamFrom(final ByteArray object) {
		return IO.inputStreamFrom(object.section());
	}

	static InputStream inputStreamFrom(final ByteArraySection object) {
		return new ByteArrayInputStream(object.array(), object.offset(), object.length());
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link Reader} und gibt diesen zurück. Hierbei werden folgende Datentypen für {@code object}
	 * unterstützt:
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
		if (object instanceof File) return new FileReader((File)object);
		if (object instanceof InputStream) return new InputStreamReader((InputStream)object);
		if (object instanceof CharacterArraySection) return IO.inputReaderFrom((CharacterArraySection)object);
		if (object instanceof CharacterArray) return IO.inputReaderFrom((CharacterArray)object);
		if (object instanceof char[]) return new CharArrayReader((char[])object);
		if (object instanceof String) return new StringReader((String)object);
		throw new IOException();
	}

	static Reader inputReaderFrom(final CharacterArray object) throws IOException {
		return IO.inputReaderFrom(object.section());
	}

	static Reader inputReaderFrom(final CharacterArraySection object) throws IOException {
		return new CharArrayReader(object.array(), object.offset(), object.length());
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt ein {@link DataTarget} und gibt dieses zurück. Hierbei werden folgende Datentypen für {@code object}
	 * unterstützt:
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
		if (object instanceof File) return new FileDataTarget((File)object);
		if (object instanceof RandomAccessFile) return new FileDataTarget((RandomAccessFile)object);
		if (object instanceof CompactByteArray) return new ArrayDataTarget((CompactByteArray)object);
		if (object instanceof ByteArray) return IO.outputDataFrom((ByteArray)object);
		if (object instanceof DataOutput) return new OutputDataTarget((DataOutput)object);
		if (object instanceof OutputStream) return new StreamDataTarget((OutputStream)object);
		if (object instanceof ByteBuffer) return new BufferDataTarget((ByteBuffer)object);
		throw new IOException();
	}

	static DataTarget outputDataFrom(final ByteArray object) {
		return new BaseDataTarget() {

			@Override
			public Object data() {
				return object;
			}

			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				object.addAll(ByteArraySection.from(b, off, len));
			}

		};
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link ByteBuffer} zum Schreiben und gibt diesen zurück. Hierbei werden folgende Datentypen für
	 * {@code object} unterstützt:
	 * <dl>
	 * <dt>{@link ByteBuffer}</dt>
	 * <dd>Der gegebene {@link ByteBuffer} wird geliefert.</dd>
	 * <dt>{@link File}, {@link FileChannel}, {@link RandomAccessFile}</dt>
	 * <dd>Es wird ein zur gegebenen Datei {@link FileChannel#map(MapMode, long, long) erzeugter} {@link ByteBuffer} in nativer Bytereihenfolge geliefert.</dd>
	 * <dt>{@code byte[]}, {@link ByteArray}, {@link ByteArraySection}</dt>
	 * <dd>Es wird ein zum gegebenen Array {@link ByteBuffer#wrap(byte[], int, int) erzeugter} {@link ByteBuffer} in nativer Bytereihenfolge geliefert.</dd>
	 * </dl>
	 *
	 * @param object Objekt.
	 * @return {@link ByteBuffer}.
	 * @throws IOException Wenn der {@link ByteBuffer} nicht erzeugt werden kann. */
	public static ByteBuffer outputBufferFrom(final Object object) throws IOException {
		if (object instanceof ByteBuffer) return (ByteBuffer)object;
		if (object instanceof File) return IO.outputBufferFrom((File)object);
		if (object instanceof FileChannel) return IO.outputBufferFrom((FileChannel)object);
		if (object instanceof RandomAccessFile) return IO.outputBufferFrom((RandomAccessFile)object);
		if (object instanceof ByteArraySection) return IO.outputBufferFrom((ByteArraySection)object);
		if (object instanceof ByteArray) return IO.outputBufferFrom((ByteArray)object);
		if (object instanceof byte[]) return IO.outputBufferFrom((byte[])object);
		throw new IOException();
	}

	static ByteBuffer outputBufferFrom(final File object) throws IOException {
		try (final RandomAccessFile file = new RandomAccessFile(object, "rw")) {
			return IO.outputBufferFrom(file);
		}
	}

	static ByteBuffer outputBufferFrom(final FileChannel object) throws IOException {
		return object.map(MapMode.READ_WRITE, 0, object.size()).order(Bytes.NATIVE_ORDER);
	}

	static ByteBuffer outputBufferFrom(final RandomAccessFile object) throws IOException {
		return IO.outputBufferFrom(object.getChannel());
	}

	static ByteBuffer outputBufferFrom(final byte[] object) {
		return ByteBuffer.wrap(object).order(Bytes.NATIVE_ORDER);
	}

	static ByteBuffer outputBufferFrom(final ByteArray object) {
		return IO.outputBufferFrom(object.section());
	}

	static ByteBuffer outputBufferFrom(final ByteArraySection object) {
		return ByteBuffer.wrap(object.array(), object.offset(), object.length()).order(Bytes.NATIVE_ORDER);
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link OutputStream} und gibt diesen zurück. Hierbei werden folgende Datentypen für {@code object}
	 * unterstützt:
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
		if (object instanceof File) return new FileOutputStream((File)object);
		if (object instanceof ByteArray) return IO.outputStreamFrom((ByteArray)object);
		throw new IOException();
	}

	static OutputStream outputStreamFrom(final ByteArray object) throws IOException {
		return new OutputStream() {

			@Override
			public void write(final int b) throws IOException {
				object.add((byte)b);
			}

			@Override
			public void write(final byte[] b, final int off, final int len) throws IOException {
				object.addAll(ByteArraySection.from(b, off, len));
			}

		};
	}

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link Writer} und gibt diesen zurück. Hierbei werden folgende Datentypen für {@code object}
	 * unterstützt:
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
		if (object instanceof File) return new FileWriter((File)object);
		if (object instanceof OutputStream) return new OutputStreamWriter((OutputStream)object);
		if (object instanceof CharacterArray) return IO.outputWriterFrom((CharacterArray)object);
		if (object instanceof StringBuilder) return IO.outputWriterFrom((StringBuilder)object);
		throw new IOException();
	}

	static Writer outputWriterFrom(final CharacterArray object) {
		return new Writer() {

			@Override
			public void write(final char[] cbuf, final int off, final int len) throws IOException {
				object.addAll(CharacterArraySection.from(cbuf, off, len));
			}

			@Override
			public void flush() throws IOException {
			}

			@Override
			public void close() throws IOException {
			}

		};
	}

	static Writer outputWriterFrom(final StringBuilder object) {
		return new Writer() {

			@Override
			public void write(final char[] cbuf, final int off, final int len) throws IOException {
				object.append(cbuf, off, len);
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
	 * @param maxDepth Maximale Tiefe für die rekursive Iteration über {@link File#listFiles() Unterverzeichnisse}. Wenn diese {@code 0} ist, wird nur über die
	 *        gegebenen Dateien und Verzeichnise iteriert. Andernfalls wird in den gegebenen Verzeichnissen rekursiv mit maximaler Tiefe {@code maxDepth-1}
	 *        rekursiv weiter iteriert.
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
		return Iterables.concatAll(Iterables.translate(files, new Getter<File, Iterable<File>>() {

			@Override
			public Iterable<File> get(final File file) {
				final File[] list = file.listFiles();
				if (list == null) return Iterables.fromItem(file);
				return Iterables.concat(Iterables.fromItem(file), IO.listFiles(maxDepth - 1, list));
			}

		}));
	}

}
