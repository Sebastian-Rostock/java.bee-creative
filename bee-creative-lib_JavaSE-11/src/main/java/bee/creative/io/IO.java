package bee.creative.io;

import static bee.creative.util.Iterables.emptyIterable;
import static bee.creative.util.Iterables.iterableFromArray;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import bee.creative.array.ByteArray;
import bee.creative.array.ByteArraySection;
import bee.creative.array.CharacterArray;
import bee.creative.array.CharacterArraySection;
import bee.creative.array.CompactByteArray;
import bee.creative.array.CompactCharacterArray;
import bee.creative.lang.Bytes;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert Methoden zur Erzeugung von Ein- und Ausgabe.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IO {

	/** Diese Methode kopiert den Inhalt des gegebenen {@link InputStream} in den gegebenen {@link OutputStream} und liefert die Anzahl der kopierten Bytes. */
	public static long copyBytes(InputStream byteReader, OutputStream byteWriter) throws IOException {
		var buffer = new byte[256 * 1024];
		var result = 0L;
		while (true) {
			var count = byteReader.read(buffer);
			if (count < 0) return result;
			byteWriter.write(buffer, 0, count);
			result += count;
		}
	}

	/** Diese Methode kopiert den Inhalt des gegebenen {@link Reader} in den gegebenen {@link Writer} und liefert die Anzahl der kopierten Zeichen. */
	public static long copyChars(Reader charReader, Writer charWriter) throws IOException {
		var buffer = new char[128 * 1024];
		var result = 0L;
		while (true) {
			var count = charReader.read(buffer);
			if (count < 0) return result;
			charWriter.write(buffer, 0, count);
			result += count;
		}
	}

	/** Diese Methode liest den Inhalt des zu {@code source} {@link #byteReaderFrom(Object) erzeugten} {@link InputStream} und liefert diesen als Bytefolge. */
	public static byte[] readBytes(Object source) throws IOException {
		try (var byteReader = byteReaderFrom(source); var byteWriter = new ByteArrayOutputStream()) {
			copyBytes(byteReader, byteWriter);
			return byteWriter.toByteArray();
		}
	}

	/** Diese Methode liest den Inhalt des zu {@code source} {@link #charReaderFrom(Object) erzeugten} {@link Reader} und liefert diesen als Zeichenkette. */
	public static String readChars(Object source) throws IOException {
		try (var charReader = charReaderFrom(source); var charWriter = new StringWriter()) {
			copyChars(charReader, charWriter);
			return charWriter.toString();
		}
	}

	/** Diese Methode schreibt die gegebene Bytefolge in den zu {@code target} {@link #byteWriterFrom(Object) erzeugten} {@link OutputStream}. **/
	public static void writeBytes(Object target, byte[] byteValue) throws IOException {
		try (var byteWriter = byteWriterFrom(target)) {
			byteWriter.write(byteValue);
		}
	}

	/** Diese Methode schreibt die gegebene Zeichenkette in den zu {@code target} {@link #charWriterFrom(Object) erzeugten} {@link Writer}. **/
	public static void writeChars(Object target, String charValue) throws IOException {
		try (var charWriter = charWriterFrom(target)) {
			charWriter.write(charValue);
		}
	}

	/** Diese Methode liefert den zu {@code source} erzeugten {@link ByteReader}:
	 * <dl>
	 * <dt>{@link ByteReader}</dt>
	 * <dd>=> {@code source}</dd>
	 * <dt>{@link InputStream}</dt>
	 * <dd>=> {@link ByteReader}</dd>
	 * <dt>{@link File}</dt>
	 * <dd>=> {@link FileInputStream} => {@link ByteReader}</dd>
	 * <dt>{@link ByteArraySection}</dt>
	 * <dd>=> {@link ByteArrayInputStream} => {@link ByteReader}</dd>
	 * <dt>{@link ByteArray}</dt>
	 * <dd>=> {@link ByteArraySection} => {@link ByteArrayInputStream} => {@link ByteReader}</dd>
	 * <dt>{@code byte[]}</dt>
	 * <dd>=> {@link ByteArrayInputStream} => {@link ByteReader}</dd>
	 * </dl>
	 */
	public static ByteReader byteReaderFrom(Object source) throws IOException {
		if (source instanceof ByteReader) return (ByteReader)source;
		if (source instanceof InputStream) return byteReaderFrom((InputStream)source);
		if (source instanceof File) return byteReaderFrom((File)source);
		if (source instanceof ByteArraySection) return byteReaderFrom((ByteArraySection)source);
		if (source instanceof ByteArray) return byteReaderFrom((ByteArray)source);
		if (source instanceof byte[]) return byteReaderFrom((byte[])source);
		throw new IOException();
	}

	/** Diese Methode liefert den zu {@code target} erzeugten {@link ByteWriter}:
	 * <dl>
	 * <dt>{@link ByteWriter}</dt>
	 * <dd>=> {@code target}</dd>
	 * <dt>{@link OutputStream}</dt>
	 * <dd>=> {@link ByteWriter}</dd>
	 * <dt>{@link File}</dt>
	 * <dd>=> {@link FileOutputStream} => {@link ByteWriter}</dd>
	 * <dt>{@link ByteArraySection}</dt>
	 * <dd>=> {@link CompactByteArray} => {@link OutputStream} => {@link ByteWriter}</dd>
	 * <dt>{@link ByteArray}</dt>
	 * <dd>=> {@link OutputStream} => {@link ByteWriter}</dd>
	 * <dt>{@code byte[]}</dt>
	 * <dd>=> {@link ByteArraySection} => {@link CompactByteArray} => {@link OutputStream} => {@link ByteWriter}</dd>
	 * </dl>
	 */
	public static ByteWriter byteWriterFrom(Object target) throws IOException {
		if (target instanceof ByteWriter) return (ByteWriter)target;
		if (target instanceof OutputStream) return byteWriterFrom((OutputStream)target);
		if (target instanceof File) return byteWriterFrom((File)target);
		if (target instanceof ByteArraySection) return byteWriterFrom((ByteArraySection)target);
		if (target instanceof ByteArray) return byteWriterFrom((ByteArray)target);
		if (target instanceof byte[]) return byteWriterFrom((byte[])target);
		throw new IOException();
	}

	/** Diese Methode liefert den zu {@code source} erzeugten {@link CharReader}:
	 * <dl>
	 * <dt>{@link CharReader}</dt>
	 * <dd>=> source</dd>
	 * <dt>{@link Reader}</dt>
	 * <dd>=> {@link CharReader}</dd>
	 * <dt>{@link InputStream}</dt>
	 * <dd>=> {@link InputStreamReader} => {@link CharReader}</dd>
	 * <dt>{@link File}</dt>
	 * <dd>=> {@link FileInputStream} => {@link InputStreamReader} => {@link CharReader}</dd>
	 * <dt>{@link CharacterArraySection}</dt>
	 * <dd>=> {@link CharArrayReader} => {@link CharReader}</dd>
	 * <dt>{@link CharacterArray}</dt>
	 * <dd>=> {@link CharArrayReader} => {@link CharArrayReader} => {@link CharReader}</dd>
	 * <dt>{@code char[]}</dt>
	 * <dd>=> {@link CharArrayReader} => {@link CharArrayReader} => {@link CharReader}</dd>
	 * <dt>{@link String}</dt>
	 * <dd>=> {@link StringReader} => {@link CharReader}</dd>
	 * </dl>
	 */
	public static CharReader charReaderFrom(Object source) throws IOException {
		if (source instanceof CharReader) return (CharReader)source;
		if (source instanceof Reader) return charReaderFrom((Reader)source);
		if (source instanceof InputStream) return charReaderFrom((InputStream)source);
		if (source instanceof File) return charReaderFrom((File)source);
		if (source instanceof CharacterArraySection) return charReaderFrom((CharacterArraySection)source);
		if (source instanceof CharacterArray) return charReaderFrom((CharacterArray)source);
		if (source instanceof char[]) return charReaderFrom((char[])source);
		if (source instanceof String) return charReaderFrom((String)source);
		throw new IOException();
	}

	/** Diese Methode liefert den zu {@code target} erzeugten {@link CharWriter}:
	 * <dl>
	 * <dt>{@link CharWriter}</dt>
	 * <dd>=> {@code target}</dd>
	 * <dt>{@link Writer}</dt>
	 * <dd>=> {@link CharWriter}</dd>
	 * <dt>{@link OutputStream}</dt>
	 * <dd>=> {@link OutputStreamWriter} => {@link CharWriter}</dd>
	 * <dt>{@link File}</dt>
	 * <dd>=> {@link FileOutputStream} => {@link OutputStreamWriter} => {@link CharWriter}</dd>
	 * <dt>{@link CharacterArraySection}</dt>
	 * <dd>=> {@link CompactCharacterArray} => {@link Writer} => {@link CharWriter}</dd>
	 * <dt>{@link CharacterArray}</dt>
	 * <dd>=> {@link Writer} => {@link CharWriter}</dd>
	 * <dt>{@link StringBuilder}</dt>
	 * <dd>=> {@link Writer} => {@link CharWriter}</dd>
	 * </dl>
	 */
	public static CharWriter charWriterFrom(Object target) throws IOException {
		if (target instanceof CharWriter) return (CharWriter)target;
		if (target instanceof Writer) return charWriterFrom((Writer)target);
		if (target instanceof OutputStream) return charWriterFrom((OutputStream)target);
		if (target instanceof File) return charWriterFrom((File)target);
		if (target instanceof CharacterArraySection) return charWriterFrom((CharacterArraySection)target);
		if (target instanceof CharacterArray) return charWriterFrom((CharacterArray)target);
		if (target instanceof char[]) return charWriterFrom((char[])target);
		if (target instanceof StringBuilder) return charWriterFrom((StringBuilder)target);
		throw new IOException();
	}

	/** Diese Methode liefert den zu {@code source} erzeugten {@link DataReader}:
	 * <dl>
	 * <dt>{@link DataReader}</dt>
	 * <dd>{@code source}</dd>
	 * <dt>{@link InputStream}</dt>
	 * <dd>=> {@link StreamDataReader}</dd>
	 * <dd>=> {@link StreamDataReader}</dd>
	 * <dt>{@link File}</dt>
	 * <dd>=> {@link RandomAccessFile} => {@link FileDataReader}</dd>
	 * <dt>{@link RandomAccessFile}</dt>
	 * <dd>=> {@link FileDataReader}</dd>
	 * <dt>{@link ByteArraySection}</dt>
	 * <dd>=> {@link CompactByteArray} => {@link ArrayDataReader}</dd>
	 * <dt>{@link ByteArray}</dt>
	 * <dd>=> {@link ArrayDataReader}</dd>
	 * <dt>{@code byte[]}</dt>
	 * <dd>=> {@link ByteArraySection} => {@link CompactByteArray} => {@link ArrayDataReader}</dd>
	 * <dt>{@link ByteBuffer}</dt>
	 * <dd>=> {@link BufferDataReader}</dd>
	 * </dl>
	 */
	public static DataReader dataReaderFrom(Object source) throws IOException {
		if (source instanceof DataReader) return (DataReader)source;
		if (source instanceof InputStream) return dataReaderFrom((InputStream)source);
		if (source instanceof File) return dataReaderFrom((File)source);
		if (source instanceof RandomAccessFile) return dataReaderFrom((RandomAccessFile)source);
		if (source instanceof ByteArraySection) return dataReaderFrom((ByteArraySection)source);
		if (source instanceof ByteArray) return dataReaderFrom((ByteArray)source);
		if (source instanceof byte[]) return dataReaderFrom((byte[])source);
		if (source instanceof ByteBuffer) return dataReaderFrom((ByteBuffer)source);
		throw new IOException();
	}

	/** Diese Methode liefert den zu {@code target} erzeugten {@link DataWriter}:
	 * <dl>
	 * <dt>{@link DataWriter}</dt>
	 * <dd>{@code target}</dd>
	 * <dt>{@link OutputStream}</dt>
	 * <dd>=> {@link StreamDataWriter}</dd>
	 * <dt>{@link File}</dt>
	 * <dd>=> {@link RandomAccessFile} => {@link FileDataWriter}</dd>
	 * <dt>{@link RandomAccessFile}</dt>
	 * <dd>=> {@link FileDataWriter}</dd>
	 * <dt>{@link ByteArraySection}</dt>
	 * <dd>=> {@link CompactByteArray} => {@link ArrayDataWriter}</dd>
	 * <dt>{@link ByteArray}</dt>
	 * <dd>=> {@link ArrayDataWriter}</dd>
	 * <dt>{@code byte[]}</dt>
	 * <dd>=> {@link ByteArraySection} => {@link CompactByteArray} => {@link ArrayDataWriter}</dd>
	 * <dt>{@link ByteBuffer}</dt>
	 * <dd>=> {@link BufferDataWriter}</dd>
	 * </dl>
	 */
	public static DataWriter dataWriterFrom(Object target) throws IOException {
		if (target instanceof DataWriter) return (DataWriter)target;
		if (target instanceof OutputStream) return dataWriterFrom((OutputStream)target);
		if (target instanceof File) return dataWriterFrom((File)target);
		if (target instanceof RandomAccessFile) return dataWriterFrom((RandomAccessFile)target);
		if (target instanceof ByteArraySection) return dataWriterFrom((ByteArraySection)target);
		if (target instanceof ByteArray) return dataWriterFrom((ByteArray)target);
		if (target instanceof byte[]) return dataWriterFrom((byte[])target);
		if (target instanceof ByteBuffer) return dataWriterFrom((ByteBuffer)target);
		throw new IOException();
	}

	private static ByteReader byteReaderFrom(InputStream source) throws IOException {
		return new ByteReader(source);
	}

	private static ByteReader byteReaderFrom(File source) throws IOException {
		return byteReaderFrom(new FileInputStream(source));
	}

	private static ByteReader byteReaderFrom(ByteArraySection source) throws IOException {
		return byteReaderFrom(new ByteArrayInputStream(source.array(), source.offset(), source.length()));
	}

	private static ByteReader byteReaderFrom(ByteArray source) throws IOException {
		return byteReaderFrom(source.section());
	}

	private static ByteReader byteReaderFrom(byte[] source) throws IOException {
		return byteReaderFrom(new ByteArrayInputStream(source));
	}

	private static ByteWriter byteWriterFrom(OutputStream target) throws IOException {
		return new ByteWriter(target);
	}

	private static ByteWriter byteWriterFrom(File target) throws IOException {
		return byteWriterFrom(new FileOutputStream(target));
	}

	private static ByteWriter byteWriterFrom(ByteArraySection target) throws IOException {
		return byteWriterFrom(new CompactByteArray(target));
	}

	private static ByteWriter byteWriterFrom(ByteArray target) throws IOException {
		return new ByteWriter(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				target.add((byte)b);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				target.addAll(ByteArraySection.from(b, off, len));
			}

		});
	}

	private static ByteWriter byteWriterFrom(byte[] target) throws IOException {
		return byteWriterFrom(ByteArraySection.from(target));
	}

	private static CharReader charReaderFrom(Reader source) throws IOException {
		return new CharReader(source);
	}

	private static CharReader charReaderFrom(InputStream source) throws IOException {
		return charReaderFrom(new InputStreamReader(source));
	}

	private static CharReader charReaderFrom(File source) throws IOException {
		return charReaderFrom(new FileInputStream(source));
	}

	private static CharReader charReaderFrom(CharacterArraySection source) throws IOException {
		return charReaderFrom(new CharArrayReader(source.array(), source.offset(), source.length()));
	}

	private static CharReader charReaderFrom(CharacterArray source) throws IOException {
		return charReaderFrom(source.section());
	}

	private static CharReader charReaderFrom(char[] source) throws IOException {
		return charReaderFrom(new CharArrayReader(source));
	}

	private static CharReader charReaderFrom(String source) throws IOException {
		return charReaderFrom(new StringReader(source));
	}

	private static CharWriter charWriterFrom(Writer target) {
		return new CharWriter(target);
	}

	private static CharWriter charWriterFrom(OutputStream target) {
		return charWriterFrom(new OutputStreamWriter(target));
	}

	private static CharWriter charWriterFrom(File target) throws IOException {
		return charWriterFrom(new FileOutputStream(target));
	}

	private static CharWriter charWriterFrom(CharacterArraySection target) {
		return charWriterFrom(new CompactCharacterArray(target));
	}

	private static CharWriter charWriterFrom(CharacterArray target) {
		return charWriterFrom(new Writer() {

			@Override
			public void write(char[] c, int off, int len) throws IOException {
				target.addAll(CharacterArraySection.from(c, off, len));
			}

			@Override
			public void flush() throws IOException {
			}

			@Override
			public void close() throws IOException {
			}

		});
	}

	private static CharWriter charWriterFrom(char[] target) {
		return charWriterFrom(CharacterArraySection.from(target));
	}

	private static CharWriter charWriterFrom(StringBuilder target) {
		return charWriterFrom(new Writer() {

			@Override
			public void write(char[] c, int off, int len) throws IOException {
				target.append(c, off, len);
			}

			@Override
			public void flush() throws IOException {
			}

			@Override
			public void close() throws IOException {
			}

		});
	}

	private static DataReader dataReaderFrom(InputStream source) throws IOException {
		return new StreamDataReader(source);
	}

	private static DataReader dataReaderFrom(File source) throws IOException {
		return dataReaderFrom(new RandomAccessFile(source, "r"));
	}

	private static DataReader dataReaderFrom(RandomAccessFile source) throws IOException {
		return new FileDataReader(source);
	}

	private static DataReader dataReaderFrom(ByteArraySection source) throws IOException {
		return dataReaderFrom(new CompactByteArray(source));
	}

	private static DataReader dataReaderFrom(ByteArray source) throws IOException {
		return new ArrayDataReader(source);
	}

	private static DataReader dataReaderFrom(byte[] source) throws IOException {
		return dataReaderFrom(ByteArraySection.from(source));
	}

	private static DataReader dataReaderFrom(ByteBuffer source) throws IOException {
		return new BufferDataReader(source);
	}

	private static DataWriter dataWriterFrom(OutputStream target) throws IOException {
		return new StreamDataWriter(target);
	}

	private static DataWriter dataWriterFrom(File target) throws IOException {
		return dataWriterFrom(new RandomAccessFile(target, "rw"));
	}

	private static DataWriter dataWriterFrom(RandomAccessFile target) throws IOException {
		return new FileDataWriter(target);
	}

	private static DataWriter dataWriterFrom(ByteArraySection target) throws IOException {
		return dataWriterFrom(new CompactByteArray(target));
	}

	private static DataWriter dataWriterFrom(ByteArray target) throws IOException {
		return new ArrayDataWriter(target);
	}

	private static DataWriter dataWriterFrom(byte[] target) throws IOException {
		return dataWriterFrom(ByteArraySection.from(target));
	}

	private static DataWriter dataWriterFrom(ByteBuffer target) throws IOException {
		return new BufferDataWriter(target);
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
		if (object instanceof File) return inputBufferFrom((File)object);
		if (object instanceof FileChannel) return inputBufferFrom((FileChannel)object);
		if (object instanceof RandomAccessFile) return inputBufferFrom((RandomAccessFile)object);
		if (object instanceof ByteArraySection) return inputBufferFrom((ByteArraySection)object);
		if (object instanceof ByteArray) return inputBufferFrom((ByteArray)object);
		if (object instanceof byte[]) return inputBufferFrom((byte[])object);
		throw new IOException();
	}

	static ByteBuffer inputBufferFrom(final byte[] object) {
		return ByteBuffer.wrap(object).order(Bytes.NATIVE_ORDER);
	}

	static ByteBuffer inputBufferFrom(final ByteArray object) {
		return inputBufferFrom(object.section());
	}

	static ByteBuffer inputBufferFrom(final ByteArraySection object) {
		return ByteBuffer.wrap(object.array(), object.offset(), object.length()).order(Bytes.NATIVE_ORDER);
	}

	static ByteBuffer inputBufferFrom(final File object) throws IOException {
		try (final var file = new RandomAccessFile(object, "r")) {
			return inputBufferFrom(file);
		}
	}

	static ByteBuffer inputBufferFrom(final FileChannel object) throws IOException {
		return object.map(MapMode.READ_ONLY, 0, object.size()).order(Bytes.NATIVE_ORDER);
	}

	static ByteBuffer inputBufferFrom(final RandomAccessFile object) throws IOException {
		return inputBufferFrom(object.getChannel());
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
		if (object instanceof File) return outputBufferFrom((File)object);
		if (object instanceof FileChannel) return outputBufferFrom((FileChannel)object);
		if (object instanceof RandomAccessFile) return outputBufferFrom((RandomAccessFile)object);
		if (object instanceof ByteArraySection) return outputBufferFrom((ByteArraySection)object);
		if (object instanceof ByteArray) return outputBufferFrom((ByteArray)object);
		if (object instanceof byte[]) return outputBufferFrom((byte[])object);
		throw new IOException();
	}

	static ByteBuffer outputBufferFrom(final File object) throws IOException {
		try (final var file = new RandomAccessFile(object, "rw")) {
			return outputBufferFrom(file);
		}
	}

	static ByteBuffer outputBufferFrom(final FileChannel object) throws IOException {
		return object.map(MapMode.READ_WRITE, 0, object.size()).order(Bytes.NATIVE_ORDER);
	}

	static ByteBuffer outputBufferFrom(final RandomAccessFile object) throws IOException {
		return outputBufferFrom(object.getChannel());
	}

	static ByteBuffer outputBufferFrom(final byte[] object) {
		return ByteBuffer.wrap(object).order(Bytes.NATIVE_ORDER);
	}

	static ByteBuffer outputBufferFrom(final ByteArray object) {
		return outputBufferFrom(object.section());
	}

	static ByteBuffer outputBufferFrom(final ByteArraySection object) {
		return ByteBuffer.wrap(object.array(), object.offset(), object.length()).order(Bytes.NATIVE_ORDER);
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
	public static Iterable<File> listFiles(int maxDepth, File... files) {
		return files == null ? emptyIterable() : listFiles(maxDepth, iterableFromArray(files));
	}

	/** Diese Methode gibt ein {@link Iterable} über die gegebenen Dateien und Verzeichnisse sowie ggf. derer in {@link File#listFiles() Unterverzeichnissen}
	 * zurück.
	 *
	 * @see #listFiles(int, File...)
	 * @param maxDepth Maximale Tiefe für die rekursive Iteration (siehe {@link #listFiles(int, File...)}).
	 * @param files Dateien und Verzeichnisse oder {@code null}.
	 * @return {@link Iterable} über {@link File}. */
	public static Iterable<File> listFiles(final int maxDepth, final Iterable<File> files) {
		if (files == null) return emptyIterable();
		if (maxDepth == 0) return files;
		return Iterables.concatIterable(Iterables.translatedIterable(files, file -> {
			var list = file.listFiles();
			if (list == null) return Iterables.iterableFromItem(file);
			return Iterables.concatIterable(Iterables.iterableFromItem(file), listFiles(maxDepth - 1, list));
		}));
	}

}
