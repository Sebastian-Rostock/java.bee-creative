package bee.creative.iam;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import bee.creative.fem.FEMBinary;
import bee.creative.iam.IAMLoader.IAMIndexLoader;
import bee.creative.ini.INIReader;
import bee.creative.ini.INIWriter;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Builders.HashMapBuilder;
import bee.creative.util.IO;

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
					final MMFArray array = MMFArray.from(codec.getSourceData());
					final ByteOrder order = IAMIndexLoader.HEADER.orderOf(array);
					codec.useByteOrder(IAMByteOrder.from(order));
					return new IAMIndexLoader(array.withOrder(order));
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

		{}

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

		/** Dieses Feld identifiziert den automatisch gewählten {@link IAMMapping#mode() Suchmodus}. Dieser ist bei mehr al {@code 8} Einträgen
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
		static final Map<?, IAMFindMode> _values_ = new HashMapBuilder<Object, IAMFindMode>() //
			.useEntry(null, AUTO).useEntry("", AUTO).useEntry("A", AUTO).useEntry("AUTO", AUTO) //
			.useEntry(IAMMapping.MODE_HASHED, SORTED).useEntry("H", HASHED).useEntry("HASHED", HASHED) //
			.useEntry(IAMMapping.MODE_SORTED, SORTED).useEntry("S", SORTED).useEntry("SORTED", SORTED) //
			.build();

		{}

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
			final IAMFindMode result = IAMFindMode._values_.get(object);
			if (result == null) throw new IllegalArgumentException("illegal find-mode: " + object);
			return result;
		}

		{}

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
		static final Map<?, IAMByteOrder> _values_ = new HashMapBuilder<Object, IAMByteOrder>() //
			.useEntry(null, AUTO).useEntry("", AUTO).useEntry("A", AUTO).useEntry("AUTO", AUTO) //
			.useEntry("B", BIGENDIAN).useEntry("BIGENDIAN", BIGENDIAN).useEntry(BIGENDIAN.toOrder(), BIGENDIAN) //
			.useEntry("L", LITTLEENDIAN).useEntry("LITTLEENDIAN", LITTLEENDIAN).useEntry(LITTLEENDIAN.toOrder(), LITTLEENDIAN) //
			.build();

		{}

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
			final IAMByteOrder result = IAMByteOrder._values_.get(object);
			if (result == null) throw new IllegalArgumentException("illegal byte-order: " + object);
			return result;
		}

		{}

		/** Diese Methode gibt die Bytereihenfolge dieser {@link IAMByteOrder} zurück.
		 * 
		 * @return Bytereihenfolge. */
		public abstract ByteOrder toOrder();

	}

	public static enum IAMArrayFormat {

		ARRAY {

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				if (string.length() == 0) return new int[0];
				final String[] source = string.split(" ", -1);
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
				final int length = array.length + 1;
				final char[] result = new char[length];
				for (int i = 0; i < length;) {
					final int value = array[i >> 1];
					result[i++] = FEMBinary.toChar((value >> 4) & 0xF);
					result[i++] = FEMBinary.toChar((value >> 0) & 0xF);
				}
				return new String(result);
			}

		},

		STRING_UTF_8 {

			final Charset charset = Charset.forName("UTF-8");

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				return IAMCodec._parseBytes_(string.getBytes(this.charset));
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(IAMCodec._formatBytes_(array), this.charset);
			}

		},

		STRING_UTF_16 {

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				return IAMCodec._parseChars_(string.toCharArray());
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(IAMCodec._formatChars_(array));
			}

		},

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

		STRING_CP_1252 {

			final Charset charset = Charset.forName("CP1252");

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				return IAMCodec._parseBytes_(string.getBytes(this.charset));
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(IAMCodec._formatBytes_(array), this.charset);
			}

		},

		STRING_ISO_8859_1 {

			final Charset charset = Charset.forName("ISO-8859-1");

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				return IAMCodec._parseBytes_(string.getBytes(this.charset));
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(IAMCodec._formatBytes_(array), this.charset);
			}

		},

		STRING_ISO_8859_15 {

			final Charset charset = Charset.forName("ISO-8859-15");

			@Override
			public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {
				return IAMCodec._parseBytes_(string.getBytes(this.charset));
			}

			@Override
			public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
				return new String(IAMCodec._formatBytes_(array), this.charset);
			}

		};

		@SuppressWarnings ("javadoc")
		static final Map<?, IAMArrayFormat> _values_ = new HashMapBuilder<Object, IAMArrayFormat>() //
			.useEntry(null, ARRAY).useEntry("", ARRAY).useEntry("A", ARRAY).useEntry("ARRAY", ARRAY) //
			.useEntry("B", BINARY).useEntry("BINARY", BINARY) //
			.useEntry("UTF-8", STRING_UTF_8).useEntry("UTF-16", STRING_UTF_16).useEntry("UTF-32", STRING_UTF_32) //
			.useEntry("CP-1252", STRING_CP_1252) //
			.useEntry("ISO-8859-1", STRING_ISO_8859_1).useEntry("ISO-8859-15", STRING_ISO_8859_15)//
			.build();

		@SuppressWarnings ("javadoc")
		static final Map<?, String> _strings_ = new HashMapBuilder<Object, String>() //
			.useEntry(ARRAY, "ARRAY") //
			.useEntry(BINARY, "BINARY") //
			.useEntry(STRING_UTF_8, "UTF-8").useEntry(STRING_UTF_16, "UTF-16").useEntry(STRING_UTF_32, "UTF-32") //
			.useEntry(STRING_CP_1252, "CP-1252") //
			.useEntry(STRING_ISO_8859_1, "ISO-8859-1").useEntry(STRING_ISO_8859_15, "ISO-8859-15") //
			.build();

		{}

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
		 * </dl>
		 * 
		 * @param object {@link Object} oder {@code null}.
		 * @return {@link IAMByteOrder}.
		 * @throws IllegalArgumentException Wenn {@code object} ungültig ist. */
		public static IAMArrayFormat from(final Object object) throws IllegalArgumentException {
			final IAMArrayFormat result = IAMArrayFormat._values_.get(object);
			if (result == null) throw new IllegalArgumentException("illegal array-format: " + object);
			return result;
		}

		{}

		public abstract int[] parse(final String string) throws NullPointerException, IllegalArgumentException;

		public abstract String format(final int[] array) throws NullPointerException, IllegalArgumentException;

		{}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return IAMArrayFormat._strings_.get(this);
		}

	}

	{}

	static int _checkRange_(final int value, final int length) throws IllegalArgumentException {
		if ((value >= 0) && (value < length)) return value;
		throw new IllegalArgumentException("illegal integer: " + value);
	}

	static int[] _parseBytes_(final byte[] source) {
		final int length = source.length;
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = source[i];
		}
		return result;
	}

	static byte[] _formatBytes_(final int[] source) {
		final int length = source.length;
		final byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = (byte)source[i];
		}
		return result;
	}

	static int[] _parseChars_(final char[] source) {
		final int length = source.length;
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = (short)source[i];
		}
		return result;
	}

	static char[] _formatChars_(final int[] source) {
		final int length = source.length;
		final char[] result = new char[length];
		for (int i = 0; i < length; i++) {
			result[i] = (char)source[i];
		}
		return result;
	}

	{}

	/** Dieses Feld speichert die Bytereihenfolge. */
	IAMByteOrder _byteOrder_ = IAMByteOrder.AUTO;

	/** Dieses Feld speichert die Eingabedaten. */
	Object _sourceData_;

	/** Dieses Feld speichert das Eingabeformat. */
	IAMDataType _sourceFormat_;

	/** Dieses Feld speichert die Ausgabedaten. */
	Object _targetData_;

	/** Dieses Feld speichert das Ausgabeformat. */
	IAMDataType _targetFormat_;

	/** Dieser Konstruktor initialisiert einen neuen {@link IAMCodec} mit:
	 * <p>
	 * {@code useByteOrder(IAMByteOrder.AUTO)}<br> */
	public IAMCodec() {
	}

	{}

	/** Diese Methode gibt die Bytereihenfolge zurück.
	 * 
	 * @see #useByteOrder(IAMByteOrder)
	 * @return Bytereihenfolge. */
	public final synchronized IAMByteOrder getByteOrder() {
		return this._byteOrder_;
	}

	/** Diese Methode gibt die Eingabedaten zurück.
	 * 
	 * @see #useSourceData(Object)
	 * @see #getSourceFormat()
	 * @return Eingabedaten. */
	public final synchronized Object getSourceData() {
		return this._sourceData_;
	}

	/** Diese Methode gibt das Format der Eingabedaten zurück.
	 * <dl>
	 * <dt> {@link IAMDataType#IAM}</dt>
	 * <dd> {@link MMFArray#from(Object)}</dd>
	 * <dt> {@link IAMDataType#INI}</dt>
	 * <dd> {@link INIReader#from(Object)}</dd>
	 * <dt> {@link IAMDataType#XML}</dt>
	 * <dd> {@link IO#inputReaderFrom(Object)}</dd>
	 * </dl>
	 * 
	 * @return Eingabeformat. */
	public final synchronized IAMDataType getSourceFormat() {
		return this._sourceFormat_;
	}

	/** Diese Methode gibt die Ausgabedaten zurück.
	 * 
	 * @see #useTargetData(Object)
	 * @see #getTargetFormat()
	 * @return Ausgabedaten. */
	public final synchronized Object getTargetData() {
		return this._targetData_;
	}

	/** Diese Methode gibt das Format der Ausgabedaten zurück.
	 * <dl>
	 * <dt> {@link IAMDataType#IAM}</dt>
	 * <dd> {@link IO#outputDataFrom(Object)}</dd>
	 * <dt> {@link IAMDataType#INI}</dt>
	 * <dd> {@link INIWriter#from(Object)}</dd>
	 * <dt> {@link IAMDataType#XML}</dt>
	 * <dd> {@link IO#outputWriterFrom(Object)}</dd>
	 * </dl>
	 * 
	 * @return Ausgabeformat. */
	public final synchronized IAMDataType getTargetFormat() {
		return this._targetFormat_;
	}

	/** Diese Methode setzt die Bytereihenfolge und gibt {@code this} zurück.
	 * 
	 * @param order Bytereihenfolge.
	 * @return {@code this}. */
	public final synchronized IAMCodec useByteOrder(final IAMByteOrder order) {
		this._byteOrder_ = order;
		return this;
	}

	/** Diese Methode setzt die Eingabedaten und gibt {@code this} zurück.
	 * 
	 * @see #getSourceData()
	 * @see #getSourceFormat()
	 * @param data Eingabedaten.
	 * @return {@code this}. */
	public final synchronized IAMCodec useSourceData(final Object data) {
		this._sourceData_ = data;
		return this;
	}

	/** Diese Methode setzt das Eingabeformat und gibt {@code this} zurück.
	 * 
	 * @see #getSourceData()
	 * @see #getSourceFormat()
	 * @param format Eingabeformat.
	 * @return {@code this}. */
	public final synchronized IAMCodec useSourceFormat(final IAMDataType format) {
		this._sourceFormat_ = format;
		return this;
	}

	/** Diese Methode setzt die Ausgabedaten und gibt {@code this} zurück.
	 * 
	 * @see #getTargetData()
	 * @see #getTargetFormat()
	 * @param data Ausgabedaten.
	 * @return {@code this}. */
	public final synchronized IAMCodec useTargetData(final Object data) {
		this._targetData_ = data;
		return this;
	}

	/** Diese Methode setzt das Ausgabeformat und gibt {@code this} zurück.
	 * 
	 * @see #getTargetData()
	 * @see #getTargetFormat()
	 * @param format Ausgabeformat.
	 * @return {@code this}. */
	public final synchronized IAMCodec useTargetFormat(final IAMDataType format) {
		this._targetFormat_ = format;
		return this;
	}

	public final synchronized void run() throws IOException, IllegalStateException, IllegalArgumentException {
		this.encodeTarget(this.decodeSource());
	}

	public final synchronized IAMIndex decodeSource() throws IOException, IllegalStateException, IllegalArgumentException {
		final IAMDataType format = this._sourceFormat_;
		if (format == null) throw new IllegalStateException();
		return format.decode(this);
	}

	public final synchronized void encodeTarget(final IAMIndex index) throws IOException, IllegalStateException, IllegalArgumentException {
		final IAMDataType format = this._targetFormat_;
		if (format == null) throw new IllegalStateException();
		if (this.getByteOrder() == null) throw new IllegalStateException();
		format.encode(this, index);
	}

}