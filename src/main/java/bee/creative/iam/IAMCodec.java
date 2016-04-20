package bee.creative.iam;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Map;
import bee.creative.data.DataTarget;
import bee.creative.fem.FEMBinary;
import bee.creative.iam.IAMLoader.IAMIndexLoader;
import bee.creative.ini.INIReader;
import bee.creative.ini.INIWriter;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Builders.HashMapBuilder;
import bee.creative.util.IO;

public final class IAMCodec {

	/** Diese Klasse implementiert die Aufzählung aller unterstützter Ein- und Ausgabedatenformate eines {@link IAMCodec}.
	 * 
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static enum DataType {

		/** Dieses Feld identifiziert das optimierte Binärdatenformat, das über einen {@link IAMIndexLoader} gelesen werden kann. */
		BIN {

			@Override
			public IAMIndex decode(final IAMCodec codec) throws IOException, IllegalArgumentException {
				try {
					return new IAMCodec_BIN().decode(codec);
				} catch (IOException | IllegalArgumentException cause) {
					throw cause;
				} catch (final Exception cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public void encode(final IAMCodec codec, final IAMIndex index) throws IOException, IllegalArgumentException {
				try {
					new IAMCodec_BIN().encode(codec, index);
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

		// TODO

		};

		{}

		/** Diese Methode liest die {@link IAMCodec#getSourceData() Eingabedaten} des gegebenen {@link IAMCodec} ein und gibt den daraus erstellten {@link IAMIndex}
		 * zurück.
		 * 
		 * @param codec {@link IAMCodec}.
		 * @return {@link IAMIndex}.
		 * @throws IOException Wenn die Eingabedaten nicht gelesen werden können.
		 * @throws IllegalArgumentException Wenn die Eingabedaten ungültig sind. */
		public IAMIndex decode(final IAMCodec codec) throws IOException, IllegalArgumentException {
			throw new IOException();
		}

		/** Diese Methode schreibt den gegebenen {@link IAMIndex} in die {@link IAMCodec#getTargetData() Ausgabedaten} des gegebenen {@link IAMCodec}.
		 * 
		 * @param codec
		 * @param index
		 * @throws IOException Wenn die Ausgabedaten nicht geschrieben werden können.
		 * @throws IllegalArgumentException Wenn die Ausgabedaten ungültig sind. */
		public void encode(final IAMCodec codec, final IAMIndex index) throws IOException, IllegalArgumentException {
			throw new IOException();
		}

	}

	public static enum FindMode {

		AUTO {

			@Override
			public boolean mode(final int entryCount) {
				return entryCount > 12 ? IAMMapping.MODE_HASHED : IAMMapping.MODE_SORTED;
			}

		},

		HASHED {

			@Override
			public boolean mode(final int entryCount) {
				return IAMMapping.MODE_HASHED;
			}

		},

		SORTED {

			@Override
			public boolean mode(final int entryCount) {
				return IAMMapping.MODE_SORTED;
			}

		};

		public abstract boolean mode(int entryCount);

	}

	public static enum ArrayFormat {

		ARRAY {

		},

		BINARY {

		},

		UTF8 {

		},

		UTF16 {

		},

		UTF32 {

		},

		df;

		{}

		public int[] parse(final String string) throws NullPointerException, IllegalArgumentException {

			return null;
		}

		public String format(final int[] array) throws NullPointerException, IllegalArgumentException {
			return null;
		}

	}

	{}

	static final Map<String, FindMode> _parseFindModeMap_ = new HashMapBuilder<String, FindMode>() //
		.useEntry(null, FindMode.AUTO).useEntry("", FindMode.AUTO) //
		.useEntry("A", FindMode.AUTO).useEntry("AUTO", FindMode.AUTO) //
		.useEntry("S", FindMode.SORTED).useEntry("SORTED", FindMode.SORTED) //
		.useEntry("H", FindMode.HASHED).useEntry("HASHED", FindMode.HASHED) //
		.build();

	@SuppressWarnings ("javadoc")
	static final Map<String, ByteOrder> _parseByteOrderMap_ = new HashMapBuilder<String, ByteOrder>() //
		.useEntry(null, ByteOrder.nativeOrder()).useEntry("", ByteOrder.nativeOrder()) //
		.useEntry("A", ByteOrder.nativeOrder()).useEntry("AUTO", ByteOrder.nativeOrder()) //
		.useEntry("B", ByteOrder.BIG_ENDIAN).useEntry("BIGENDIAN", ByteOrder.BIG_ENDIAN) //
		.useEntry("L", ByteOrder.LITTLE_ENDIAN).useEntry("LITTLEENDIAN", ByteOrder.LITTLE_ENDIAN) //
		.build();

	static final Map<String, ArrayFormat> _parseArrayFormatMap_ = new HashMapBuilder<String, ArrayFormat>() //
		.useEntry(null, ArrayFormat.ARRAY).useEntry("", ArrayFormat.ARRAY) //
		.useEntry("A", ArrayFormat.ARRAY).useEntry("ARRAY", ArrayFormat.ARRAY) //
		// TODO
		.build();

	static final Map<FindMode, String> _formatFindModeMap_ = new HashMapBuilder<FindMode, String>() //
		.useEntry(FindMode.AUTO, "AUTO") //
		// TODO
		.build();

	@SuppressWarnings ("javadoc")
	static final Map<ByteOrder, String> _formatByteOrderMap_ = new HashMapBuilder<ByteOrder, String>() //
		.useEntry(null, "AUTO") //
		.useEntry(ByteOrder.BIG_ENDIAN, "BIGENDIAN") //
		.useEntry(ByteOrder.LITTLE_ENDIAN, "LITTLEENDIAN") //
		.build();

	static final Map<ArrayFormat, String> _formatArrayFormatMap_ = new HashMapBuilder<ArrayFormat, String>() //
		.useEntry(ArrayFormat.ARRAY, "AUTO") //
		// TODO
		.build();

	{}

	static final <GResult> GResult _get_(final Map<?, GResult> map, final Object key) throws IllegalArgumentException {
		final GResult result = map.get(key);
		if (result != null) return result;
		throw new IllegalArgumentException();
	}

	public static final FindMode parseFindMode(final String value) throws IllegalArgumentException {
		return IAMCodec._get_(IAMCodec._parseFindModeMap_, value);
	}

	public static final ByteOrder parseByteOrder(final String value) throws IllegalArgumentException {
		return IAMCodec._get_(IAMCodec._parseByteOrderMap_, value);
	}

	public static final ArrayFormat parseArrayFormat(final String value) {
		return IAMCodec._get_(IAMCodec._parseArrayFormatMap_, value);
	}

	public static final String formatFindMode(final FindMode value) throws IllegalArgumentException {
		return IAMCodec._get_(IAMCodec._formatFindModeMap_, value);
	}

	public static final String formatByteOrder(final ByteOrder value) throws IllegalArgumentException {
		return IAMCodec._get_(IAMCodec._formatByteOrderMap_, value);
	}

	public static final String formatArrayFormat(final ArrayFormat value) throws IllegalArgumentException {
		return IAMCodec._get_(IAMCodec._formatArrayFormatMap_, value);
	}

	final static int[] _parseBytes_(final byte[] bytes) {
		final int length = bytes.length;
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = bytes[i];
		}
		return result;
	}

	final static int[] _parseChars_(final char[] chars) {
		final int length = chars.length;
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = (short)chars[i];
		}
		return result;
	}

	@SuppressWarnings ("javadoc")
	final static int[] _parseArray_(final String string) throws IllegalArgumentException {
		if (string.length() == 0) return new int[0];
		final String[] source = string.split(" ", -1);
		final int length = source.length;
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = IAMCodec._parseInteger_(source[i]);
		}
		return result;
	}

	@SuppressWarnings ("javadoc")
	final static int[] _parseBinary_(final String string) throws IllegalArgumentException {
		final char[] source = string.toCharArray();
		final int length = source.length;
		if ((length & 1) != 0) throw new IllegalArgumentException();
		final int[] result = new int[length >> 1];
		for (int i = 0; i < length;) {
			final int hi = FEMBinary.toDigit(source[i++]);
			final int lo = FEMBinary.toDigit(source[i++]);
			result[i] = (byte)((hi << 4) | (lo << 0));
		}
		return result;
	}

	@SuppressWarnings ("javadoc")
	final static int _parseInteger_(final String value) throws IllegalArgumentException {
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	@SuppressWarnings ("javadoc")
	static final String _formatArray_(final int[] value) {
		final int length = value.length;
		if (length == 0) return "";
		final StringBuilder result = new StringBuilder().append(value[0]);
		for (int index = 1; index < length; index++) {
			result.append(' ').append(value[index]);
		}
		return result.toString();
	}

	@SuppressWarnings ("javadoc")
	final static String _formatBinary_(final int[] binary) {
		final int length = binary.length + 1;
		final char[] result = new char[length];
		for (int i = 0; i < length;) {
			final int value = binary[i >> 1];
			result[i++] = FEMBinary.toChar((value >> 4) & 0xF);
			result[i++] = FEMBinary.toChar((value >> 0) & 0xF);
		}
		return new String(result);
	}

	{}

	/** Dieses Feld speichert die Bytereihenfolge. */
	ByteOrder _byteOrder_ = ByteOrder.nativeOrder();

	/** Dieses Feld speichert die Eingabedaten. */
	Object _sourceData_;

	/** Dieses Feld speichert das Eingabeformat. */
	DataType _sourceFormat_;

	/** Dieses Feld speichert die Ausgabedaten. */
	Object _targetData_;

	/** Dieses Feld speichert das Ausgabeformat. */
	DataType _targetFormat_;

	{}

	/** Diese Methode gibt die Bytereihenfolge zurück.
	 * 
	 * @see #useByteOrder(ByteOrder)
	 * @return Bytereihenfolge. */
	public final synchronized ByteOrder getByteOrder() {
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
	 * <dt> {@link DataType#BIN}</dt>
	 * <dd> {@link MMFArray#from(Object)}</dd>
	 * <dt> {@link DataType#INI}</dt>
	 * <dd> {@link INIReader#from(Object)}</dd>
	 * <dt> {@link DataType#XML}</dt>
	 * <dd> {@link IO#inputReaderFrom(Object)}</dd>
	 * </dl>
	 * 
	 * @return Eingabeformat. */
	public final synchronized DataType getSourceFormat() {
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
	 * <dt> {@link DataType#BIN}</dt>
	 * <dd> {@link IO#outputDataFrom(Object)}</dd>
	 * <dt> {@link DataType#INI}</dt>
	 * <dd> {@link INIWriter#from(Object)}</dd>
	 * <dt> {@link DataType#XML}</dt>
	 * <dd> {@link IO#outputWriterFrom(Object)}</dd>
	 * </dl>
	 * 
	 * @return Ausgabeformat. */
	public final DataType getTargetFormat() {
		return this._targetFormat_;
	}

	/** Diese Methode setzt die Bytereihenfolge und gibt {@code this} zurück.<br>
	 * Wenn diese {@code null} ist, wird {@link ByteOrder#nativeOrder()} verwendet.
	 * 
	 * @param order Bytereihenfolge.
	 * @return {@code this}. */
	public final synchronized IAMCodec useByteOrder(final ByteOrder order) {
		this._byteOrder_ = order == null ? ByteOrder.nativeOrder() : order;
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
	public final synchronized IAMCodec useSourceFormat(final DataType format) {
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
	public final synchronized IAMCodec useTargetFormat(final DataType format) {
		this._targetFormat_ = format;
		return this;
	}

	public final synchronized void run() throws IOException, IllegalStateException, IllegalArgumentException {
		this.encodeTarget(this.decodeSource());
	}

	public final synchronized IAMIndex decodeSource() throws IOException, IllegalStateException, IllegalArgumentException {
		final DataType format = this._sourceFormat_;
		if (format == null) throw new IllegalStateException();
		return format.decode(this);
	}

	public final synchronized void encodeTarget(final IAMIndex index) throws IOException, IllegalStateException, IllegalArgumentException {
		final DataType format = this._targetFormat_;
		if (format == null) throw new IllegalStateException();
		format.encode(this, index);
	}

}
