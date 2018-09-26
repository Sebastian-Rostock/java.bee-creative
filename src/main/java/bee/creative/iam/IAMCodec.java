package bee.creative.iam;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Map;
import bee.creative.fem.FEMBinary;
import bee.creative.fem.FEMString;
import bee.creative.ini.INIReader;
import bee.creative.ini.INIWriter;
import bee.creative.io.IO;
import bee.creative.util.Builders;

/** Diese Klasse implementiert den Konfigurator, mit welchem ein {@link IAMIndex} aus bzw. in unterschiedliche Datenformate gelesen bzw. geschieben werden kann.
 * Dabei können {@link #getSourceData() Eingabedaten} unterschiedlicher {@link #getSourceFormat() Eingabeformate} direkt in {@link #getTargetData()
 * Ausgabedaten} unterschiedlicher {@link #getTargetFormat() Ausgabeformate} überführt werden.
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class IAMCodec {

	/** Diese Klasse implementiert die Aufzählung der unterstützten Ein- und Ausgabedatenformate eines {@link IAMCodec}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static enum IAMDataType {

		/** Dieses Feld identifiziert das binäre optimierte Datenformat, das über {@link IAMIndex#toBytes(ByteOrder)} erzeugt bzw. einen
		 * {@link IAMIndex#from(Object)} gelesen werden kann. */
		IAM {

			@Override
			public IAMIndex decode(final IAMCodec codec) throws IOException, IllegalArgumentException {
				try {
					return IAMIndex.from(codec.getSourceData());
				} catch (IOException | IllegalArgumentException cause) {
					throw cause;
				} catch (final Exception cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public void encode(final IAMCodec codec, final IAMIndex index) throws IOException, IllegalArgumentException {
				try {
					try (OutputStream stream = IO.outputStreamFrom(codec.getTargetData())) {
						stream.write(index.toBytes(codec.getByteOrder().toOrder()));
					}
				} catch (IOException | IllegalArgumentException cause) {
					throw cause;
				} catch (final Exception cause) {
					throw new IllegalArgumentException(cause);
				}
			}

		},

		/** Dieses Feld identifiziert das textbasierte ini-Datenaustauschformat. */
		INI {

			@Override
			public IAMIndex decode(final IAMCodec codec) throws IOException, IllegalArgumentException {
				try {
					return new IAMCodec_INI().decode(codec);
				} catch (IOException | IllegalArgumentException cause) {
					throw cause;
				} catch (final Exception cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public void encode(final IAMCodec codec, final IAMIndex index) throws IOException, IllegalArgumentException {
				try {
					new IAMCodec_INI().encode(codec, index);
				} catch (IOException | IllegalArgumentException cause) {
					throw cause;
				} catch (final Exception cause) {
					throw new IllegalArgumentException(cause);
				}
			}

		},

		/** Dieses Feld identifiziert das textbasierte xml-Datenaustauschformat. */
		XML {

			@Override
			public IAMIndex decode(final IAMCodec codec) throws IOException, IllegalArgumentException {
				try {
					return new IAMCodec_XML().decode(codec);
				} catch (IOException | IllegalArgumentException cause) {
					throw cause;
				} catch (final Exception cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public void encode(final IAMCodec codec, final IAMIndex index) throws IOException, IllegalArgumentException {
				try {
					new IAMCodec_XML().encode(codec, index);
				} catch (IOException | IllegalArgumentException cause) {
					throw cause;
				} catch (final Exception cause) {
					throw new IllegalArgumentException(cause);
				}
			}

		};

		/** Diese Methode liest die {@link IAMCodec#getSourceData() Eingabedaten} des gegebenen {@link IAMCodec} ein und gibt den daraus erstellten {@link IAMIndex}
		 * zurück.
		 *
		 * @param codec {@link IAMCodec}.
		 * @return {@link IAMIndex}.
		 * @throws IOException Wenn die Eingabedaten nicht gelesen werden können.
		 * @throws IllegalArgumentException Wenn die Eingabedaten ungültig sind. */
		public abstract IAMIndex decode(final IAMCodec codec) throws IOException, IllegalArgumentException;

		/** Diese Methode schreibt den gegebenen {@link IAMIndex} in die {@link IAMCodec#getTargetData() Ausgabedaten} des gegebenen {@link IAMCodec}.
		 *
		 * @param codec {@link IAMCodec}.
		 * @param index {@link IAMIndex}.
		 * @throws IOException Wenn die Ausgabedaten nicht geschrieben werden können.
		 * @throws IllegalArgumentException Wenn die Ausgabedaten ungültig sind. */
		public abstract void encode(final IAMCodec codec, final IAMIndex index) throws IOException, IllegalArgumentException;

	}

	/** Diese Klasse implementiert die Aufzählung der unterstützten {@link IAMMapping#mode() Suchmodus} eines {@link IAMMapping}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static enum IAMFindMode {

		/** Dieses Feld identifiziert den automatisch gewählten {@link IAMMapping#mode() Suchmodus}. Dieser ist bei mehr als {@code 8} Einträgen
		 * {@link IAMMapping#MODE_HASHED} und sonst {@link IAMMapping#MODE_SORTED}. */
		AUTO {

			@Override
			public boolean toMode(final int entryCount) {
				return entryCount > 8 ? IAMMapping.MODE_HASHED : IAMMapping.MODE_SORTED;
			}

		},

		/** Dieses Feld identifiziert den {@link IAMMapping#mode() Suchmodus} {@link IAMMapping#MODE_HASHED}. */
		HASHED {

			@Override
			public boolean toMode(final int entryCount) {
				return IAMMapping.MODE_HASHED;
			}

		},

		/** Dieses Feld identifiziert den {@link IAMMapping#mode() Suchmodus} {@link IAMMapping#MODE_SORTED}. */
		SORTED {

			@Override
			public boolean toMode(final int entryCount) {
				return IAMMapping.MODE_SORTED;
			}

		};

		@SuppressWarnings ("javadoc")
		static final Map<?, IAMFindMode> values = Builders.MapBuilder.<Object, IAMFindMode>forHashMap() //
			.put(null, AUTO).put("", AUTO).put("A", AUTO).put("AUTO", AUTO) //
			.put(IAMMapping.MODE_HASHED, HASHED).put("H", HASHED).put("HASHED", HASHED) //
			.put(IAMMapping.MODE_SORTED, SORTED).put("S", SORTED).put("SORTED", SORTED) //
			.get();

		/** Diese Methode gibt den {@link IAMFindMode} zum gegebenen Objekt zurück.<br>
		 * Hierbei werden folgende Eingaben unterstützt:
		 * <dl>
		 * <dt>{@code null}, {@code ""}, {@code "A"}, {@code "AUTO"}</dt>
		 * <dd>{@link #AUTO}</dd>
		 * <dt>{@link IAMMapping#MODE_HASHED}, {@code "H"}, {@code "HASHED"}</dt>
		 * <dd>{@link #HASHED}</dd>
		 * <dt>{@link IAMMapping#MODE_SORTED}, {@code "S"}, {@code "SORTED"}</dt>
		 * <dd>{@link #SORTED}</dd>
		 * </dl>
		 *
		 * @param object {@link Object} oder {@code null}.
		 * @return {@link IAMFindMode}.
		 * @throws IllegalArgumentException Wenn {@code object} ungültig ist. */
		public static IAMFindMode from(final Object object) throws IllegalArgumentException {
			if (object instanceof IAMFindMode) return (IAMFindMode)object;
			final IAMFindMode result = IAMFindMode.values.get(object);
			if (result == null) throw new IllegalArgumentException("illegal find-mode: " + object);
			return result;
		}

		/** Diese Methode gibt den {@link IAMMapping#mode() Suchmodus} dieses {@link IAMFindMode} zurück.
		 *
		 * @param entryCount Anzahl der Einträge eines {@link IAMMapping}.
		 * @return {@link IAMMapping#MODE_HASHED} oder {@link IAMMapping#MODE_SORTED}. */
		public abstract boolean toMode(int entryCount);

	}

	/** Diese Klasse implementiert die Aufzählung der unterstützten Bytereigenfolgen eines {@link IAMCodec}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static enum IAMByteOrder {

		/** Dieses Feld identifiziert die native Bytereigenfolge. */
		AUTO {

			@Override
			public ByteOrder toOrder() {
				return ByteOrder.nativeOrder();
			}

			@Override
			public String toString() {
				return "AUTO";
			}

		},

		/** Dieses Feld identifiziert die <em>big-endian</em> Bytereigenfolge. */
		BIGENDIAN {

			@Override
			public ByteOrder toOrder() {
				return ByteOrder.BIG_ENDIAN;
			}

		},

		/** Dieses Feld identifiziert die <em>little-endian</em> Bytereigenfolge. */
		LITTLEENDIAN {

			@Override
			public ByteOrder toOrder() {
				return ByteOrder.LITTLE_ENDIAN;
			}

		};

		@SuppressWarnings ("javadoc")
		static final Map<?, IAMByteOrder> values = Builders.MapBuilder.<Object, IAMByteOrder>forHashMap() //
			.put(null, AUTO).put("", AUTO).put("A", AUTO).put("AUTO", AUTO) //
			.put("B", BIGENDIAN).put("BIGENDIAN", BIGENDIAN).put(BIGENDIAN.toOrder(), BIGENDIAN) //
			.put("L", LITTLEENDIAN).put("LITTLEENDIAN", LITTLEENDIAN).put(LITTLEENDIAN.toOrder(), LITTLEENDIAN) //
			.get();

		/** Diese Methode gibt die {@link IAMByteOrder} zum gegebenen Objekt zurück.<br>
		 * Hierbei werden folgende Eingaben unterstützt:
		 * <dl>
		 * <dt>{@code null}, {@code ""}, {@code "A"}, {@code "AUTO"}</dt>
		 * <dd>{@link #AUTO}</dd>
		 * <dt>{@link ByteOrder#BIG_ENDIAN}, {@code "B"}, {@code "BIGENDIAN"}</dt>
		 * <dd>{@link #BIGENDIAN}</dd>
		 * <dt>{@link ByteOrder#LITTLE_ENDIAN}, {@code "L"}, {@code "LITTLEENDIAN"}</dt>
		 * <dd>{@link #LITTLEENDIAN}</dd>
		 * </dl>
		 *
		 * @param object {@link Object} oder {@code null}.
		 * @return {@link IAMByteOrder}.
		 * @throws IllegalArgumentException Wenn {@code object} ungültig ist. */
		public static IAMByteOrder from(final Object object) throws IllegalArgumentException {
			if (object instanceof IAMByteOrder) return (IAMByteOrder)object;
			final IAMByteOrder result = IAMByteOrder.values.get(object);
			if (result == null) throw new IllegalArgumentException("illegal byte-order: " + object);
			return result;
		}

		/** Diese Methode gibt die Bytereihenfolge dieser {@link IAMByteOrder} zurück.
		 *
		 * @return Bytereihenfolge. */
		public abstract ByteOrder toOrder();

	}

	/** Diese Klasse implementiert die Aufzählung der unterstützten Zahlenfolgenformaten eines {@link IAMCodec}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static enum IAMArrayFormat {

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen als Dezimalzahlen mit optionalem Vorzeichen
		 * leerzeichensepariert angegeben werden, z.B. {@code ""}, {@code "12"}, {@code "12 -34 5 -6"}. */
		ARRAY {

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				if (string.length() == 0) return new int[0];
				final String[] source = string.trim().split("\\s+", -1);
				final int length = source.length;
				final int[] result = new int[length];
				for (int i = 0; i < length; i++) {
					try {
						result[i] = Integer.parseInt(source[i]);
					} catch (final NumberFormatException cause) {
						throw new IllegalArgumentException(cause);
					}
				}
				return result;
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				final int length = array.length;
				if (length == 0) return "";
				final StringBuilder result = new StringBuilder().append(array[0]);
				for (int index = 1; index < length; index++) {
					result.append(' ').append(array[index]);
				}
				return result.toString();
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen als zweistellige hexadezimale Zahlen in großen Buchstaben und
		 * ohne Trennzeichen angegeben werden, z.B. {@code ""}, {@code "12"}, {@code "12ABF0"}. */
		BINARY {

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				final char[] source = string.toCharArray();
				final int length = source.length;
				if ((length & 1) != 0) throw new IllegalArgumentException();
				final int[] result = new int[length >> 1];
				for (int i = 0; i < length;) {
					final int x = i >> 1;
					final int hi = FEMBinary.toDigit(source[i++]);
					final int lo = FEMBinary.toDigit(source[i++]);
					result[x] = (byte)((hi << 4) | (lo << 0));
				}
				return result;
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				final int length = array.length << 1;
				final char[] result = new char[length];
				for (int i = 0; i < length;) {
					final int value = array[i >> 1];
					result[i++] = FEMBinary.toChar((value >> 4) & 0xF);
					result[i++] = FEMBinary.toChar((value >> 0) & 0xF);
				}
				return new String(result);
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die Token des <em>8-Bit UCS Transformation Format</em>
		 * stehen und ein Codepoint mit bis zu vier Token kodiert sein kann. */
		STRING_UTF_8 {

			final Charset charset = Charset.forName("UTF-8");

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				return IAMCodec.parseBytes(string.getBytes(this.charset));
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(IAMCodec.formatBytes(array), this.charset);
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die Token des <em>16-Bit UCS Transformation Format</em>
		 * stehen und ein Codepoint mit bis zu zwei Token kodiert sein kann. */
		STRING_UTF_16 {

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				return IAMCodec.parseChars(string.toCharArray());
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(IAMCodec.formatChars(array));
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die Codepoints des <em>32-Bit UCS Transformation
		 * Format</em> stehen. */
		STRING_UTF_32 {

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				final int length = string.codePointCount(0, string.length());
				final int[] result = new int[length];
				for (int i = 0, j = 0; i < length; i++) {
					result[i] = string.codePointAt(j);
					j = string.offsetByCodePoints(j, 1);
				}
				return result;
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(array, 0, array.length);
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die Codepoints des 8-Bit Textformat <em>CP-1252</em>
		 * stehen. */
		STRING_CP_1252 {

			final Charset charset = Charset.forName("CP1252");

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				return IAMCodec.parseBytes(string.getBytes(this.charset));
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(IAMCodec.formatBytes(array), this.charset);
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die Codepoints des 8-Bit Textformat <em>ISO-8859-1</em>
		 * stehen. */
		STRING_ISO_8859_1 {

			final Charset charset = Charset.forName("ISO-8859-1");

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				return IAMCodec.parseBytes(string.getBytes(this.charset));
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(IAMCodec.formatBytes(array), this.charset);
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die Codepoints des 8-Bit Textformat <em>ISO-8859-15</em>
		 * stehen. */
		STRING_ISO_8859_15 {

			final Charset charset = Charset.forName("ISO-8859-15");

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				return IAMCodec.parseBytes(string.getBytes(this.charset));
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(IAMCodec.formatBytes(array), this.charset);
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die strukturierte {@link FEMString#toArray(int, boolean)
		 * 32-Bit-Einzelwertkodierung} eines {@link FEMString} stehen. */
		STRING_FEM {

			@Override
			public int[] parse(final String source) throws NullPointerException, IllegalArgumentException {
				return FEMString.from(source).toArray(4, true).toArray();
			}

			@Override
			public String format(final int[] source) throws NullPointerException, IllegalArgumentException {
				return FEMString.from(IAMArray.from(source), false).toString();
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die strukturierte {@link FEMString#toArray(int, boolean)
		 * 8-Bit-Einzelwertkodierung} eines {@link FEMString} stehen. */
		STRING_FEM_INT8 {

			@Override
			public int[] parse(final String source) throws NullPointerException, IllegalArgumentException {
				return FEMString.from(source).toArray(4, true).toArray();
			}

			@Override
			public String format(final int[] source) throws NullPointerException, IllegalArgumentException {
				return FEMString.from(IAMArray.from(IAMArray.toBytes(IAMArray.from(source))), false).toString();
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die strukturierte {@link FEMString#toArray(int, boolean)
		 * 16-Bit-Einzelwertkodierung} eines {@link FEMString} stehen. */
		STRING_FEM_INT16 {

			@Override
			public int[] parse(final String source) throws NullPointerException, IllegalArgumentException {
				return FEMString.from(source).toArray(2, false).toArray();
			}

			@Override
			public String format(final int[] source) throws NullPointerException, IllegalArgumentException {
				return FEMString.from(IAMArray.from(IAMArray.toChars(IAMArray.from(source))), false).toString();
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die strukturierte {@link FEMString#toArray(int, boolean)
		 * 8-Bit-Mehrwertkodierung} eines {@link FEMString} stehen. */
		STRING_FEM_UTF8 {

			@Override
			public int[] parse(final String source) throws NullPointerException, IllegalArgumentException {
				return FEMString.from(source).toArray(1, true).toArray();
			}

			@Override
			public String format(final int[] source) throws NullPointerException, IllegalArgumentException {
				return FEMString.from(IAMArray.from(IAMArray.toBytes(IAMArray.from(source))), true).toString();
			}

		},

		/** Dieses Feld identifiziert das Format zur Angabe einer Zahlenfolge, bei welcher die Zahlen für die strukturierte {@link FEMString#toArray(int, boolean)
		 * 16-Bit-Mehrwertkodierung} eines {@link FEMString} stehen. */
		STRING_FEM_UTF16 {

			@Override
			public int[] parse(final String source) throws NullPointerException, IllegalArgumentException {
				return FEMString.from(source).toArray(2, true).toArray();
			}

			@Override
			public String format(final int[] source) throws NullPointerException, IllegalArgumentException {
				return FEMString.from(IAMArray.from(IAMArray.toChars(IAMArray.from(source))), true).toString();
			}

		};

		@SuppressWarnings ("javadoc")
		static final Map<IAMArrayFormat, String> strings = Builders.MapBuilder.<IAMArrayFormat, String>forHashMap() //
			.put(ARRAY, "ARRAY") //
			.put(BINARY, "BINARY") //
			.put(STRING_UTF_8, "UTF-8").put(STRING_UTF_16, "UTF-16").put(STRING_UTF_32, "UTF-32") //
			.put(STRING_CP_1252, "CP-1252") //
			.put(STRING_ISO_8859_1, "ISO-8859-1").put(STRING_ISO_8859_15, "ISO-8859-15") //
			.put(STRING_FEM, "FEM-32") //
			.put(STRING_FEM_INT8, "FEM-UTF8").put(STRING_FEM_UTF16, "FEM-UTF16") //
			.put(STRING_FEM_UTF8, "FEM-UTF8").put(STRING_FEM_UTF16, "FEM-UTF16") //
			.get();

		@SuppressWarnings ("javadoc")
		static final Map<?, IAMArrayFormat> values = Builders.MapBuilder.<Object, IAMArrayFormat>forHashMap() //
			.putAllInverse(IAMArrayFormat.strings).put(null, ARRAY).put("", ARRAY).put("A", ARRAY).put("B", BINARY) //
			.get();

		/** Diese Methode gibt das {@link IAMArrayFormat} zum gegebenen Objekt zurück.<br>
		 * Hierbei werden folgende Eingaben unterstützt:
		 * <dl>
		 * <dt>{@code null}, {@code ""}, {@code "A"}, {@code "ARRAY"}</dt>
		 * <dd>{@link #ARRAY}</dd>
		 * <dt>{@code "B"}, {@code "BINARY"}</dt>
		 * <dd>{@link #BINARY}</dd>
		 * <dt>{@code "UTF-8"}</dt>
		 * <dd>{@link #STRING_UTF_8}</dd>
		 * <dt>{@code "UTF-16"}</dt>
		 * <dd>{@link #STRING_UTF_16}</dd>
		 * <dt>{@code "UTF-32"}</dt>
		 * <dd>{@link #STRING_UTF_32}</dd>
		 * <dt>{@code "CP-1252"}</dt>
		 * <dd>{@link #STRING_CP_1252}</dd>
		 * <dt>{@code "ISO-8859-1"}</dt>
		 * <dd>{@link #STRING_ISO_8859_1}</dd>
		 * <dt>{@code "ISO-8859-15"}</dt>
		 * <dd>{@link #STRING_ISO_8859_15}</dd>
		 * <dt>{@code "FEM-UTF8"}</dt>
		 * <dd>{@link #STRING_FEM_UTF8}</dd>
		 * <dt>{@code "FEM-UTF16"}</dt>
		 * <dd>{@link #STRING_FEM_UTF16}</dd>
		 * <dt>{@code "FEM-32"}</dt>
		 * <dd>{@link #STRING_FEM}</dd>
		 * </dl>
		 *
		 * @param object {@link Object} oder {@code null}.
		 * @return {@link IAMByteOrder}.
		 * @throws IllegalArgumentException Wenn {@code object} ungültig ist. */
		public static IAMArrayFormat from(final Object object) throws IllegalArgumentException {
			if (object instanceof IAMArrayFormat) return (IAMArrayFormat)object;
			final IAMArrayFormat result = IAMArrayFormat.values.get(object);
			if (result == null) throw new IllegalArgumentException("illegal array-format: " + object);
			return result;
		}

		/** Diese Methode parst die gegebene Zeichenkette in eine Zahlenfolge und gibt diese zurück.
		 *
		 * @param source Zeichenkette.
		 * @return Zahlenfolge.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
		public abstract int[] parse(final String source) throws NullPointerException, IllegalArgumentException;

		/** Diese Methode formatiert die gegebene Zahlenfolge in eine Zeichenkette und gibt diese zurück.
		 *
		 * @param source Zahlenfolge.
		 * @return Zeichenkette.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Zahlenfolge ungültig ist. */
		public abstract String format(final int[] source) throws NullPointerException, IllegalArgumentException;

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return IAMArrayFormat.strings.get(this);
		}

	}

	@SuppressWarnings ("javadoc")
	static int checkRange(final int value, final int length) throws IllegalArgumentException {
		if ((value >= 0) && (value < length)) return value;
		throw new IllegalArgumentException("illegal integer: " + value);
	}

	@SuppressWarnings ("javadoc")
	static int[] parseBytes(final byte[] source) {
		final int length = source.length;
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = source[i];
		}
		return result;
	}

	@SuppressWarnings ("javadoc")
	static byte[] formatBytes(final int[] source) {
		final int length = source.length;
		final byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = (byte)source[i];
		}
		return result;
	}

	@SuppressWarnings ("javadoc")
	static int[] parseChars(final char[] source) {
		final int length = source.length;
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = (short)source[i];
		}
		return result;
	}

	@SuppressWarnings ("javadoc")
	static char[] formatChars(final int[] source) {
		final int length = source.length;
		final char[] result = new char[length];
		for (int i = 0; i < length; i++) {
			result[i] = (char)source[i];
		}
		return result;
	}

	/** Dieses Feld speichert die Bytereihenfolge. */
	IAMByteOrder byteOrder = IAMByteOrder.AUTO;

	/** Dieses Feld speichert die Eingabedaten. */
	Object sourceData;

	/** Dieses Feld speichert das Eingabeformat. */
	IAMDataType sourceFormat;

	/** Dieses Feld speichert die Ausgabedaten. */
	Object targetData;

	/** Dieses Feld speichert das Ausgabeformat. */
	IAMDataType targetFormat;

	/** Dieser Konstruktor initialisiert einen neuen {@link IAMCodec} mit Bytereihenfolge {@link IAMByteOrder#AUTO}) */
	public IAMCodec() {
	}

	/** Diese Methode gibt die Bytereihenfolge zurück.
	 *
	 * @see #useByteOrder(IAMByteOrder)
	 * @return Bytereihenfolge. */
	public final synchronized IAMByteOrder getByteOrder() {
		return this.byteOrder;
	}

	/** Diese Methode gibt die Eingabedaten zurück.
	 *
	 * @see #useSourceData(Object)
	 * @see #getSourceFormat()
	 * @return Eingabedaten. */
	public final synchronized Object getSourceData() {
		return this.sourceData;
	}

	/** Diese Methode gibt das Format der Eingabedaten zurück.
	 * <dl>
	 * <dt>{@link IAMDataType#IAM}</dt>
	 * <dd>{@link IAMIndex#from(Object)}</dd>
	 * <dt>{@link IAMDataType#INI}</dt>
	 * <dd>{@link INIReader#from(Object)}</dd>
	 * <dt>{@link IAMDataType#XML}</dt>
	 * <dd>{@link IO#inputReaderFrom(Object)}</dd>
	 * </dl>
	 *
	 * @return Eingabeformat. */
	public final synchronized IAMDataType getSourceFormat() {
		return this.sourceFormat;
	}

	/** Diese Methode gibt die Ausgabedaten zurück.
	 *
	 * @see #useTargetData(Object)
	 * @see #getTargetFormat()
	 * @return Ausgabedaten. */
	public final synchronized Object getTargetData() {
		return this.targetData;
	}

	/** Diese Methode gibt das Format der Ausgabedaten zurück.
	 * <dl>
	 * <dt>{@link IAMDataType#IAM}</dt>
	 * <dd>{@link IO#outputDataFrom(Object)}</dd>
	 * <dt>{@link IAMDataType#INI}</dt>
	 * <dd>{@link INIWriter#from(Object)}</dd>
	 * <dt>{@link IAMDataType#XML}</dt>
	 * <dd>{@link IO#outputWriterFrom(Object)}</dd>
	 * </dl>
	 *
	 * @return Ausgabeformat. */
	public final synchronized IAMDataType getTargetFormat() {
		return this.targetFormat;
	}

	/** Diese Methode setzt die Bytereihenfolge und gibt {@code this} zurück.
	 *
	 * @param order Bytereihenfolge.
	 * @return {@code this}. */
	public final synchronized IAMCodec useByteOrder(final IAMByteOrder order) {
		this.byteOrder = order;
		return this;
	}

	/** Diese Methode setzt die Eingabedaten und gibt {@code this} zurück.
	 *
	 * @see #getSourceData()
	 * @see #getSourceFormat()
	 * @param data Eingabedaten.
	 * @return {@code this}. */
	public final synchronized IAMCodec useSourceData(final Object data) {
		this.sourceData = data;
		return this;
	}

	/** Diese Methode setzt das Eingabeformat und gibt {@code this} zurück.
	 *
	 * @see #getSourceData()
	 * @see #getSourceFormat()
	 * @param format Eingabeformat.
	 * @return {@code this}. */
	public final synchronized IAMCodec useSourceFormat(final IAMDataType format) {
		this.sourceFormat = format;
		return this;
	}

	/** Diese Methode setzt die Ausgabedaten und gibt {@code this} zurück.
	 *
	 * @see #getTargetData()
	 * @see #getTargetFormat()
	 * @param data Ausgabedaten.
	 * @return {@code this}. */
	public final synchronized IAMCodec useTargetData(final Object data) {
		this.targetData = data;
		return this;
	}

	/** Diese Methode setzt das Ausgabeformat und gibt {@code this} zurück.
	 *
	 * @see #getTargetData()
	 * @see #getTargetFormat()
	 * @param format Ausgabeformat.
	 * @return {@code this}. */
	public final synchronized IAMCodec useTargetFormat(final IAMDataType format) {
		this.targetFormat = format;
		return this;
	}

	/** Diese Methode liest die {@link #getSourceData() Eingabedaten}, erstellt daraus einen {@link IAMIndex} und schreibt diesen in die {@link #getTargetData()
	 * Ausgabedaten}.
	 *
	 * @see #decodeSource()
	 * @see #encodeTarget(IAMIndex)
	 * @throws IOException Wenn die Eingabedaten nicht gelesen bzw. die Ausgabedaten nicht geschrieben werden können.
	 * @throws IllegalStateException Wenn kein Eingabeformat, kein Ausgabeformat oder keine Bytereihenfolge eingestellt ist.
	 * @throws IllegalArgumentException Wenn die Eingabedaten bzw. die Ausgabedaten ungültig sind. */
	public final synchronized void run() throws IOException, IllegalStateException, IllegalArgumentException {
		this.encodeTarget(this.decodeSource());
	}

	/** Diese Methode liest die {@link #getSourceData() Eingabedaten} und gibt den daraus erstellten {@link IAMIndex} zurück.
	 *
	 * @see IAMDataType#decode(IAMCodec)
	 * @return {@link IAMIndex}.
	 * @throws IOException Wenn die Eingabedaten nicht gelesen werden können.
	 * @throws IllegalStateException Wenn kein Eingabeformat eingestellt ist.
	 * @throws IllegalArgumentException Wenn die Eingabedaten ungültig sind. */
	public final synchronized IAMIndex decodeSource() throws IOException, IllegalStateException, IllegalArgumentException {
		final IAMDataType format = this.sourceFormat;
		if (format == null) throw new IllegalStateException();
		return format.decode(this);
	}

	/** Diese Methode schreibt den gegebenen {@link IAMIndex} in die {@link #getTargetData() Ausgabedaten}.
	 *
	 * @see IAMDataType#encode(IAMCodec, IAMIndex)
	 * @param index {@link IAMIndex}.
	 * @throws IOException Wenn die Ausgabedaten nicht geschrieben werden können.
	 * @throws IllegalStateException Wenn kein Ausgabeformat oder keine Bytereihenfolge eingestellt ist.
	 * @throws IllegalArgumentException Wenn die Ausgabedaten ungültig sind. */
	public final synchronized void encodeTarget(final IAMIndex index) throws IOException, IllegalStateException, IllegalArgumentException {
		final IAMDataType format = this.targetFormat;
		if (format == null) throw new IllegalStateException();
		if (this.byteOrder == null) throw new IllegalStateException();
		format.encode(this, index);
	}

}
