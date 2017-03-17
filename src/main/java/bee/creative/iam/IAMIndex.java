package bee.creative.iam;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractList;
import java.util.List;
import bee.creative.iam.IAMLoader.IAMIndexLoader;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine abstrakte Zusammenstellung beliebig vieler Listen ({@link IAMListing}) und Abbildungen ({@link IAMMapping}).
 * <p>
 * Die Methoden {@link #mappings()} und {@link #listings()} delegieren an {@link #mapping(int)} und {@link #mappingCount()} bzw. {@link #listing(int)} und
 * {@link #listingCount()}.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMIndex {

	@SuppressWarnings ("javadoc")
	static final class EmptyIndex extends IAMIndex {

		@Override
		public IAMMapping mapping(final int index) {
			return IAMMapping.EMPTY;
		}

		@Override
		public int mappingCount() {
			return 0;
		}

		@Override
		public IAMListing listing(final int index) {
			return IAMListing.EMPTY;
		}

		@Override
		public int listingCount() {
			return 0;
		}

	}

	/** Diese Klasse implementiert ein Objekt zur Ermittlung der Längen gegebener Zahlenlisten.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class SizeStats {

		/** Diese Methode füht die Startpositionen der gegebenen Zahlenfolgen an den gegebenen {@link ByteBuffer} an.<br>
		 * Die Zahlenfolgen repräsentieren die von {@link IAMListing#toBytes(ByteOrder)} bzw. {@link IAMMapping#toBytes(ByteOrder)} kodierten Datenstrukturen.
		 *
		 * @param buffer {@link ByteBuffer}.
		 * @param source Zahlenfolgen. */
		public static void putSize(final ByteBuffer buffer, final byte[][] source) {
			int offset = 0;
			buffer.putInt(0);
			for (final byte[] data: source) {
				offset += data.length >> 2;
				buffer.putInt(offset);
			}
		}

		/** Diese Methode gibt den Datentyp für die gegebene Größe zurück.<br>
		 * Die Datentypen {@code 1}, {@code 2} und {@code 3} stehen für {@code UINT8}, {@code UINT16} und {@code UINT32}.
		 *
		 * @param value Größe.
		 * @return Datentyp ({@code 1..3}). */
		public static int computeSizeType(final int value) {
			if (value <= 255) return 1;
			if (value <= 65535) return 2;
			return 3;
		}

		{}

		/** Dieses Feld speichert den Größentyp.<br>
		 * Der Wert {@code 0} legt fest, dass alle Zahlenfolgen die gleiche Länge {@link #dataLength} besitzen.<br>
		 * Die Werte {@code 1}, {@code 2} und {@code 2} legen fest, dass die kummulierten Längen der Zahlenlisten als {@code UINT8}, {@code UINT16} bzw.
		 * {@code UINT32} in {@link #dataOffset} gespeichert sind.
		 *
		 * @see SizeStats#computeSizeType(int) */
		public final int type;

		/** Dieses Feld speichert die Größe des Speicherbereichs für {@link #dataOffset}. */
		public final int bytes;

		/** Dieses Feld speichert die homogene Länge der Zahlenlisten. */
		public final int dataLength;

		/** Dieses Feld speichert die heterogenen Längen der Zahlenlisten. */
		public final int[] dataOffset;

		/** Dieser Konstruktor analysiert die gegebene Zahlenliste und initialisiert die Felder.
		 *
		 * @param arrays Zahlenliste. */
		public SizeStats(final List<int[]> arrays) {
			final int count = arrays.size();
			this.dataOffset = new int[count + 1];
			int minLength = 1073741823, maxLength = 0;
			for (int i = 0, offset = 0; i < count;) {
				final int length = arrays.get(i++).length;
				offset += length;
				this.dataOffset[i] = offset;
				if (length > maxLength) {
					maxLength = length;
				}
				if (length < minLength) {
					minLength = length;
				}
			}
			if (minLength >= maxLength) {
				this.type = 0;
				this.dataLength = maxLength;
				this.bytes = 4;
			} else {
				this.type = SizeStats.computeSizeType(this.dataOffset[count]);
				this.dataLength = 0;
				final int dataOffsetBytes = this.dataOffset.length * IAMLoader.byteCount(this.type);
				this.bytes = (dataOffsetBytes + 3) & -4;
			}

		}

		{}

		/** Diese Methode schreibt die {@link #dataLength} bzw. das {@link #dataOffset} gemäß {@link #type} in den gegebenen Puffer.
		 *
		 * @param buffer Puffer. */
		public final void putSize(final ByteBuffer buffer) {
			if (this.type == 0) {
				buffer.putInt(this.dataLength);
			} else {
				DataStats.putData(buffer, this.type, this.dataOffset);
			}
		}

	}

	/** Diese Klasse implementiert ein Objekt zur Ermittlung der Kodierung gegebener Zahlenlisten.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class DataStats {

		/** Diese Methode speichert die gegebene Zahlenfolge an den gegebenen {@link ByteBuffer} an.<br>
		 * Der geschriebene Speicherbereich wird mit Nullwerten ergänzt, um eine restlos durch vier teilbare Größe zu erreichen.
		 *
		 * @see ByteBuffer#put(byte)
		 * @see ByteBuffer#putInt(int)
		 * @see ByteBuffer#putShort(short)
		 * @param buffer {@link ByteBuffer}.
		 * @param type Datentyp ({@code 1=INT8/UINT8}, {@code 2=INT16/UINT16}, {@code 3=INT32}).
		 * @param values Zahlenfolge. */
		public static void putData(final ByteBuffer buffer, final int type, final int[] values) {
			switch (type) {
				case 1:
					for (int i = 0, length = values.length; i < length; i++) {
						buffer.put((byte)values[i]);
					}
					switch (values.length & 3) {
						case 1:
							buffer.put((byte)0);
						case 2:
							buffer.put((byte)0);
						case 3:
							buffer.put((byte)0);
					}
				break;
				case 2:
					for (int i = 0, length = values.length; i < length; i++) {
						buffer.putShort((short)values[i]);
					}
					switch (values.length & 1) {
						case 1:
							buffer.putShort((short)0);
					}
				break;
				case 3:
					for (int i = 0, length = values.length; i < length; i++) {
						buffer.putInt(values[i]);
					}
				break;
			}
		}

		/** Diese Methode gibt den Datentyp für den gegebenen Wert zurück.<br>
		 * Die Datentypen {@code 1}, {@code 2} und {@code 3} stehen für {@code INT8}, {@code INT16} und {@code INT32}.
		 *
		 * @param value Wert.
		 * @return Datengrößentyps ({@code 1..3}). */
		public static int computeDataType(final int value) {
			if ((-128 <= value) && (value <= 127)) return 1;
			if ((-32768 <= value) && (value <= 32767)) return 2;
			return 3;
		}

		{}

		/** Dieses Feld speichert den Datentyp.<br>
		 * Die Werte {@code 1}, {@code 2} und {@code 2} legen fest, dass die Werte der Zahlenlisten als {@code INT8}, {@code INT16} bzw. {@code INT32} in
		 * {@link #dataValue} gespeichert sind.
		 *
		 * @see DataStats#computeDataType(int) */
		public final int type;

		/** Dieses Feld speichert die Größe des Speicherbereichs für {@link #dataValue}. */
		public final int bytes;

		/** Dieses Feld speichert die Längen gegebener Zahlenlisten. */
		public final SizeStats dataSize;

		/** Dieses Feld speichert die Werte der Zahlenlisten. */
		public final int[] dataValue;

		/** Dieser Konstruktor analysiert die gegebene Zahlenliste und initialisiert die Felder.
		 *
		 * @param arrays Zahlenliste. */
		public DataStats(final List<int[]> arrays) {
			this.dataSize = new SizeStats(arrays);
			final int count = arrays.size();
			this.dataValue = new int[this.dataSize.dataOffset[count]];
			int minValue = 0, maxValue = 0;
			for (int i = 0, offset = 0; i < count; i++) {
				final int[] item = arrays.get(i);
				for (int i2 = 0, length = item.length; i2 < length; i2++) {
					final int value = item[i2];
					if (value > maxValue) {
						maxValue = value;
					}
					if (value < minValue) {
						minValue = value;
					}
					this.dataValue[offset++] = value;
				}
			}
			this.type = Math.max(DataStats.computeDataType(minValue), DataStats.computeDataType(maxValue));
			final int dataValueBytes = this.dataValue.length * IAMLoader.byteCount(this.type);
			this.bytes = (dataValueBytes + 3) & -4;
		}

		{}

		/** Diese Methode schreibt die {@link #dataValue} gemäß {@link #type} in den gegebenen Puffer.
		 *
		 * @param buffer Puffer. */
		public final void putData(final ByteBuffer buffer) {
			DataStats.putData(buffer, this.type, this.dataValue);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class Listingss extends AbstractList<IAMListing> {

		final IAMIndex owner;

		Listingss(final IAMIndex owner) {
			this.owner = owner;
		}

		{}

		@Override
		public IAMListing get(final int index) {
			if ((index < 0) || (index >= this.owner.listingCount())) throw new IndexOutOfBoundsException();
			return this.owner.listing(index);
		}

		@Override
		public int size() {
			return this.owner.listingCount();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class Mappings extends AbstractList<IAMMapping> {

		final IAMIndex owner;

		Mappings(final IAMIndex owner) {
			this.owner = owner;
		}

		{}

		@Override
		public IAMMapping get(final int index) {
			if ((index < 0) || (index >= this.owner.mappingCount())) throw new IndexOutOfBoundsException();
			return this.owner.mapping(index);
		}

		@Override
		public int size() {
			return this.owner.mappingCount();
		}

	}

	{}

	/** Dieses Feld speichert den leeren {@link IAMIndex}. */
	public static final IAMIndex EMPTY = new EmptyIndex();

	{}

	/** Diese Methode ist eine Abkürzung für {@code new IAMIndexLoader(MMFArray.from(object))}.
	 *
	 * @see MMFArray#from(Object)
	 * @see IAMIndexLoader#IAMIndexLoader(MMFArray)
	 * @param object Objekt.
	 * @return {@link IAMIndexLoader}.
	 * @throws IOException Wenn {@link MMFArray#from(Object)} eine entsprechende Ausnahme auslöst.
	 * @throws IAMException Wenn {@link IAMIndexLoader#IAMIndexLoader(MMFArray)} eine entsprechende Ausnahme auslöst. */
	public static IAMIndexLoader from(final Object object) throws IOException, IAMException {
		return new IAMIndexLoader(MMFArray.from(object));
	}

	{}

	/** Diese Methode gibt die {@code index}-te Abbildung zurück. Bei einem ungültigen {@code index} wird eine leere Abbildung geliefert.
	 *
	 * @see #mappingCount()
	 * @param index Index.
	 * @return {@code index}-te Abbildung. */
	public abstract IAMMapping mapping(final int index);

	/** Diese Methode gibt die Anzahl der Abbildungen zurück ({@code 0..1073741823}).
	 *
	 * @see #mapping(int)
	 * @return Anzahl der Abbildungen. */
	public abstract int mappingCount();

	/** Diese Methode gibt eine {@link List}-Sicht auf die Abbildungen zurück.
	 *
	 * @see #mapping(int)
	 * @see #mappingCount()
	 * @return Abbildungen. */
	public final List<IAMMapping> mappings() {
		return new Mappings(this);
	}

	/** Diese Methode gibt die {@code index}-te Liste zurück. Bei einem ungültigen {@code index} wird eine leere Liste geliefert.
	 *
	 * @see #listingCount()
	 * @param index Index.
	 * @return {@code index}-te Liste. */
	public abstract IAMListing listing(final int index);

	/** Diese Methode gibt die Anzahl der Listen zurück.
	 *
	 * @see #listing(int)
	 * @return Anzahl der Listen. */
	public abstract int listingCount();

	/** Diese Methode gibt {@link List}-Sicht auf die Listen zurück.
	 *
	 * @see #listing(int)
	 * @see #listingCount()
	 * @return Listen. */
	public final List<IAMListing> listings() {
		return new Listingss(this);
	}

	/** Diese Methode ist eine Ankürzung für {@code this.toBytes(ByteOrder.nativeOrder())}.
	 *
	 * @return Binärdatenformat {@code IAM_INDEX}. */
	public final byte[] toBytes() {
		return this.toBytes(ByteOrder.nativeOrder());
	}

	/** Diese Methode kodiert diesen {@link IAMIndex} in das binäre optimierte Datenformat {@code IAM_INDEX} und gibt dieses als Bytefolge zurück.
	 *
	 * @param order Bytereihenfolge.
	 * @return {@code IAM_INDEX}. */
	public final byte[] toBytes(final ByteOrder order) {

		int length = 20;

		final int mappingCount = this.mappingCount();
		final byte[][] mappingBytes = new byte[mappingCount][];
		for (int i = 0; i < mappingCount; i++) {
			length += (mappingBytes[i] = this.mapping(i).toBytes(order)).length;
		}
		length += mappingCount << 2;

		final int listingCount = this.listingCount();
		final byte[][] listingBytes = new byte[listingCount][];
		for (int i = 0; i < listingCount; i++) {
			length += (listingBytes[i] = this.listing(i).toBytes(order)).length;
		}
		length += listingCount << 2;

		final byte[] result = new byte[length];
		final ByteBuffer buffer = ByteBuffer.wrap(result).order(order);

		buffer.putInt(0xF00DBA5E);
		buffer.putInt(mappingCount);
		buffer.putInt(listingCount);
		SizeStats.putSize(buffer, mappingBytes);
		SizeStats.putSize(buffer, listingBytes);
		for (final byte[] data: mappingBytes) {
			buffer.put(data);
		}
		for (final byte[] data: listingBytes) {
			buffer.put(data);
		}

		return result;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString("IAMIndex", this.mappings(), this.listings());
	}

}
