package bee.creative.fem;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.RandomAccess;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMArray.CompactArray3;
import bee.creative.fem.FEMString.CompactStringINT16;
import bee.creative.fem.FEMString.CompactStringINT8;
import bee.creative.io.MappedBuffer;
import bee.creative.lang.Array2;
import bee.creative.lang.Bytes;
import bee.creative.lang.Integers;
import bee.creative.lang.Objects;
import bee.creative.util.AbstractSet2;
import bee.creative.util.Entries;
import bee.creative.util.HashMapLO;
import bee.creative.util.Iterator2;
import bee.creative.util.Iterators;
import bee.creative.util.Property;

/** Diese Klasse implementiert einen Puffer zur Auslagerung von {@link FEMFunction Funktionen} in einen {@link MappedBuffer Dateipuffer}. Die darüber
 * angebundene Datei besitz dafür eine entsprechende Datenstruktur, deren Kopfdaten beim Öffnen erzeugt bzw. geprüft werden.
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMBuffer implements Property<FEMFunction>, Emuable {

	/** Diese Klasse implementiert eine Wertliste, deren Elemente als Referenzen gegeben sind und in {@link #customGet(int)} über einen gegebenen
	 * {@link FEMBuffer} in Werte {@link FEMBuffer#get(long) übersetzt} werden. */
	public static class MappedArray extends FEMArray {

		final FEMBuffer buffer;

		/** this.addr = value: long[length], tableAddr: long */
		/** tableAddr = count: int, range: int[count-1], index: int[length] */
		final long addr;

		MappedArray(final FEMBuffer buffer, final long addr) throws IllegalArgumentException {
			super(buffer.buffer.getInt(addr));
			this.buffer = buffer;
			this.addr = addr + 8;
		}

		@Override
		protected FEMValue customGet(final int index) {
			return this.buffer.getAt(this.addr + (index * 8L), FEMValue.class);
		}

		@Override
		protected int customFind(final FEMValue that, final int offset, int length, final boolean foreward) {
			final long tableAddr = this.buffer.buffer.getLong(this.addr + (this.length * 8L));
			if (tableAddr == 0) return super.customFind(that, offset, length, foreward);
			final int count = this.buffer.buffer.getInt(tableAddr), hash = that.hashCode() & (count - 2);
			final long rangeAddr = tableAddr + (hash * 4L);
			int rangeL = this.buffer.buffer.getInt(rangeAddr), rangeR = this.buffer.buffer.getInt(rangeAddr + 4) - 1;
			length += offset;
			if (foreward) {
				for (; rangeL <= rangeR; rangeL++) {
					final int result = this.buffer.buffer.getInt(tableAddr + (rangeL * 4L));
					if (length <= result) return -1;
					if ((offset <= result) && that.equals(this.customGet(result))) return result;
				}
			} else {
				for (; rangeL <= rangeR; rangeR--) {
					final int result = this.buffer.buffer.getInt(tableAddr + (rangeR * 4L));
					if (result < offset) return -1;
					if ((result < length) && that.equals(this.customGet(result))) return result;
				}
			}
			return -1;
		}

		@Override
		protected boolean customEquals(final FEMArray that) throws NullPointerException {
			if (this == that) return true;
			if (that instanceof MappedArray) {
				final MappedArray that2 = (MappedArray)that;
				if ((this.addr == that2.addr) && (this.buffer.buffer == that2.buffer.buffer)) return true;
			}
			return super.customEquals(that);
		}

		@Override
		public FEMArray compact(final boolean index) {
			return !index || this.isIndexed() ? this : super.compact(true);
		}

		@Override
		public boolean isIndexed() {
			return this.buffer.buffer.getLong(this.addr + (this.length * 8L)) != 0;
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

		@Override
		public int hashCode() {
			return this.buffer.buffer.getInt(this.addr - 4);
		}

	}

	/** Diese Klasse implementiert eine Bytefolge als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedBinary extends FEMBinary {

		final MappedBuffer buffer;

		final long addr;

		MappedBinary(final MappedBuffer buffer, final long addr) throws NullPointerException, IllegalArgumentException {
			super(buffer.getInt(addr));
			this.buffer = buffer;
			this.addr = addr + 8;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.buffer.get(this.addr + index);
		}

		@Override
		protected boolean customEquals(final FEMBinary that) throws NullPointerException {
			if (this == that) return true;
			if (that instanceof MappedBinary) {
				final MappedBinary that2 = (MappedBinary)that;
				if ((this.addr == that2.addr) && (this.buffer == that2.buffer)) return true;
			}
			return super.customEquals(that);
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

		@Override
		public int hashCode() {
			return this.buffer.getInt(this.addr - 4);
		}

	}

	/** Diese Klasse implementiert eine Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedString extends FEMString {

		final MappedBuffer buffer;

		final long addr;

		MappedString(final MappedBuffer buffer, final long addr) throws NullPointerException, IllegalArgumentException {
			super(buffer.getInt(addr));
			this.buffer = buffer;
			this.addr = addr + 8;
		}

		@Override
		protected boolean customEquals(final FEMString that) throws NullPointerException {
			if (this == that) return true;
			if (that instanceof MappedString) {
				final MappedString that2 = (MappedString)that;
				if ((this.addr == that2.addr) && (this.buffer == that2.buffer)) return true;
			}
			return super.customEquals(that);
		}

		@Override
		public FEMString compact() {
			return this;
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

		@Override
		public int hashCode() {
			return this.buffer.getInt(this.addr - 4);
		}

	}

	/** Diese Klasse implementiert eine {@code byte}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedStringINT8 extends MappedString {

		MappedStringINT8(final MappedBuffer buffer, final long addr) throws NullPointerException, IllegalArgumentException {
			super(buffer, addr);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.buffer.get(this.addr + index) & 0xFF;
		}

	}

	/** Diese Klasse implementiert eine {@code short}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedStringINT16 extends MappedString {

		MappedStringINT16(final MappedBuffer buffer, final long addr) throws NullPointerException, IllegalArgumentException {
			super(buffer, addr);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.buffer.getShort(this.addr + (index * 2L)) & 0xFFFF;
		}

	}

	/** Diese Klasse implementiert eine {@code int}-Zeichenkette als Sicht auf eine Speicherbereich eines {@link MappedBuffer}. */
	public static class MappedStringINT32 extends MappedString {

		MappedStringINT32(final MappedBuffer buffer, final long addr) throws NullPointerException, IllegalArgumentException {
			super(buffer, addr);
		}

		@Override
		protected int customGet(final int index) throws IndexOutOfBoundsException {
			return this.buffer.getInt(this.addr + (index * 4L));
		}

	}

	/** Diese Klasse implementiert die {@link FEMBuffer#reusables() Liste der Wiederverwendbaren Funktionen}. */
	public class Reusables extends AbstractSet2<FEMFunction> implements Array2<FEMFunction>, RandomAccess {

		@Override
		public FEMFunction get(final int index) {
			return FEMBuffer.this.reusablesGet(index);
		}

		@Override
		public int size() {
			return FEMBuffer.this.reusablesSize();
		}

		@Override
		public boolean contains(final Object item) {
			return FEMBuffer.this.reusablesContains(item);
		}

		@Override
		public Iterator2<FEMFunction> iterator() {
			return Iterators.fromArray(this, 0, this.size());
		}

		@Override
		public String toString() {
			return Objects.printIterable(true, this);
		}

	}

	/** Dieses Feld speichert die Typkennung für {@link FEMVoid}. */
	protected static final byte TYPE_VOID_DATA = 0;

	/** Dieses Feld speichert die Typkennung für {@link FEMArray#EMPTY}. */
	protected static final byte TYPE_ARRAY_DATA = 1;

	/** Dieses Feld speichert die Typkennung für {@link MappedArray}. */
	protected static final byte TYPE_ARRAY_ADDR = 2;

	/** Dieses Feld speichert die Typkennung für {@link FEMString.UniformString}. */
	protected static final byte TYPE_STRING_DATA = 3;

	/** Dieses Feld speichert die Typkennung für {@link MappedStringINT8}. */
	protected static final byte TYPE_STRING_ADDR1 = 4;

	/** Dieses Feld speichert die Typkennung für {@link MappedStringINT16}. */
	protected static final byte TYPE_STRING_ADDR2 = 5;

	/** Dieses Feld speichert die Typkennung für {@link MappedStringINT32}. */
	protected static final byte TYPE_STRING_ADDR3 = 6;

	/** Dieses Feld speichert die Typkennung für {@link FEMBinary.UniformBinary}. */
	protected static final byte TYPE_BINARY_DATA = 7;

	/** Dieses Feld speichert die Typkennung für {@link MappedBinary}. */
	protected static final byte TYPE_BINARY_ADDR = 8;

	/** Dieses Feld speichert die Typkennung für {@link FEMObject} mit kleinem {@link FEMObject#value()}. */
	protected static final byte TYPE_OBJECT_DATA1 = 9;

	/** Dieses Feld speichert die Typkennung für {@link FEMObject} mit ausgerichtetem {@link FEMObject#value()}. */
	protected static final byte TYPE_OBJECT_DATA2 = 10;

	/** Dieses Feld speichert die Typkennung für {@link FEMObject} mit beliebigem {@link FEMObject#value()}. */
	protected static final byte TYPE_OBJECT_ADDR = 11;

	/** Dieses Feld speichert die Typkennung für {@link FEMInteger} mit positivem kleinen {@link FEMInteger#value()}. */
	protected static final byte TYPE_INTEGER_DATA1 = 12;

	/** Dieses Feld speichert die Typkennung für {@link FEMInteger} mit negativem kleinen {@link FEMInteger#value()}. */
	protected static final byte TYPE_INTEGER_DATA2 = 13;

	/** Dieses Feld speichert die Typkennung für {@link FEMInteger} mit beliebigen {@link FEMInteger#value()}. */
	protected static final byte TYPE_INTEGER_ADDR = 14;

	/** Dieses Feld speichert die Typkennung für {@link FEMDecimal}. */
	protected static final byte TYPE_DECIMAL_ADDR = 15;

	/** Dieses Feld speichert die Typkennung für {@link FEMBoolean}. */
	protected static final byte TYPE_BOOLEAN_DATA = 16;

	/** Dieses Feld speichert die Typkennung für {@link FEMHandler}. */
	protected static final byte TYPE_HANDLER_ADDR = 17;

	/** Dieses Feld speichert die Typkennung für {@link FEMDatetime} mit kleinem {@link FEMDatetime#value()}. */
	protected static final byte TYPE_DATETIME_DATA1 = 18;

	/** Dieses Feld speichert die Typkennung für {@link FEMDatetime} mit ausgerichtetem {@link FEMDatetime#value()}. */
	protected static final byte TYPE_DATETIME_DATA2 = 19;

	/** Dieses Feld speichert die Typkennung für {@link FEMDatetime} mit beliebigen {@link FEMDatetime#value()}. */
	protected static final byte TYPE_DATETIME_ADDR = 20;

	/** Dieses Feld speichert die Typkennung für {@link FEMDuration} mit kleinem {@link FEMDuration#value()}. */
	protected static final byte TYPE_DURATION_DATA1 = 21;

	/** Dieses Feld speichert die Typkennung für {@link FEMDuration} mit ausgerichtetem {@link FEMDuration#value()}. */
	protected static final byte TYPE_DURATION_DATA2 = 22;

	/** Dieses Feld speichert die Typkennung für {@link FEMDuration} mit beliebigen {@link FEMDuration#value()}. */
	protected static final byte TYPE_DURATION_ADDR = 23;

	/** Dieses Feld speichert die Typkennung für {@link FEMProxy}. */
	protected static final byte TYPE_PROXY_ADDR = 24;

	/** Dieses Feld speichert die Typkennung für {@link FEMParam}. */
	protected static final byte TYPE_PARAM_DATA = 25;

	/** Dieses Feld speichert die Typkennung für {@link FEMClosure}. */
	protected static final byte TYPE_CLOSURE_ADDR = 26;

	/** Dieses Feld speichert die Typkennung für {@link FEMComposite}. */
	protected static final byte TYPE_COMPOSITE_ADDR = 27;

	/** Dieses Feld speichert die Adresse des nächsten von {@link #putData(long)} gelieferten Speicherbereichs. Diese Adresse ist stets ein Vielfaches von 8. */
	private long nextAddr;

	/** Dieses Feld speichert die Adresse der streuwertbasierten Tabelle der {@link #putRef(int, long) wiederverwendbaren} Funktionen. Diese Tabelle besteht aus
	 * den folgenden Speicherbereichen:
	 * <dl>
	 * <dt>{@code head: int[reusableLimit]}</dt>
	 * <dd>Spalte der Kopfpositionen der einfach verketten Listen aus Positionen von {@code -1} bis {@link #reusableCount}{@code -1}.</dd>
	 * <dt>{@code next: int[reusableLimit]}</dt>
	 * <dd>Spalte der Folgepositionen der infach verketten Listen aus Positionen von {@code -1} bis {@link #reusableCount}{@code -1}.</dd>
	 * <dt>{@code hash: int[reusableLimit]}</dt>
	 * <dd>Spalte der Streuwerte</dd>
	 * <dt>{@code item: long[reusableLimit]}</dt>
	 * <dd>Spalte der Referenzen auf die wiederverwendbaren Funktionen.</dd>
	 * </dl>
	 */
	private long reusableTable;

	/** Dieses Feld speichert die Kapazität der {@link #reusableTable} und ist stets eine positive Potenz von {@code 2}. Bei Zahlenüberlauf wird die
	 * {@link #reusableTable} geleert. */
	private int reusableLimit;

	/** Dieses Feld speichert die Anzahl der aktuell in der {@link #reusableTable} enthaltenen Einträge. */
	private int reusableCount;

	/** Dieses Feld speichert eine {@link List} als Sicht auf die wiederverwendbaren Funktionen. */
	private final Reusables reusables = new Reusables();

	/** Dieses Feld bildet von einer Adresse auf einen Platzhalter ab und dient in {@link #getProxyByAddr(long)} der Behandlung der Rekursion. */
	protected final HashMapLO<FEMProxy> proxyGetMap = new HashMapLO<>();

	/** Dieses Feld speichert die in {@link #getProxyByAddr(long)} nicht rekursiv aufzulösenden Adressen. */
	protected final LinkedList<Long> proxyGetList = new LinkedList<>();

	/** Dieses Feld speichert die in {@link #putProxyAsRef(FEMProxy)} nicht rekursiv einzufügenden Adresse-Zielfunktion-Paare. */
	protected final LinkedList<Entry<Long, FEMFunction>> proxyPutList = new LinkedList<>();

	/** Dieses Feld speichert den Dateipuffer. Dieser besteht auf folgenden Speicherbereichen:
	 * <dl>
	 * <dt>{@code fileType: long}</dt>
	 * <dd>Dateitypkennung {@code FEMFILE2}</dd>
	 * <dt>{@code rootRef: long}</dt>
	 * <dd>Referenz für {@link #get()} und {@link #set(FEMFunction)}.</dd>
	 * <dt>{@code nextAddr: long}</dt>
	 * <dd>Adresse des nächsten von {@link #putData(long)} gelieferten Speicherbereichs.</dd>
	 * <dt>{@code reusableCount: int}</dt>
	 * <dd>{@link #reusableCount Anzahl} der erfassten wiederverwendbaren Funktionen.</dd>
	 * <dt>{@code reusableLimit: int}</dt>
	 * <dd>{@link #reusableLimit Kapazität} der Tabelle der wiederverwendbaren Funktionen.</dd>
	 * <dt>{@code reusableTable: long}</dt>
	 * <dd>{@link #reusableTable Adresse} der Tabelle der wiederverwendbaren Funktionen.</dd>
	 * </dl>
	 */
	protected final MappedBuffer buffer;

	/** Dieser Konstruktor initialisiert den Dateipuffer.
	 *
	 * @param buffer Dateipuffer.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public FEMBuffer(final MappedBuffer buffer) throws IOException {
		final long fileType = 0x33454c49464d4546L, fileSize = buffer.file().length();
		this.buffer = buffer;
		if (fileSize == 0) {
			if (buffer.isReadonly()) throw new IOException();
			buffer.resize(120L);
			this.putFileType(fileType);
			this.clearBuffer();
		} else {
			if (fileSize < 120L) throw new IOException();
			buffer.resize(120L);
			if (this.getFileType() != fileType) throw new IOException();
			this.setNextAddr();
			this.setReusableCount();
			this.setReusableLimit();
			this.setReusableTable();
			buffer.resize(this.size());
		}
	}

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei in nativer Bytereihenfolge nur mit Lesezugriff.
	 *
	 * @see FEMBuffer#FEMBuffer(File, boolean)
	 * @param file Datei.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public FEMBuffer(final File file) throws IOException {
		this(file, true);
	}

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei in nativer Bytereihenfolge.
	 *
	 * @see FEMBuffer#FEMBuffer(File, boolean, ByteOrder)
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public FEMBuffer(final File file, final boolean readonly) throws IOException {
		this(file, readonly, Bytes.NATIVE_ORDER);
	}

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei.
	 *
	 * @see MappedBuffer#MappedBuffer(File, boolean)
	 * @see MappedBuffer#order(ByteOrder)
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @param order Bytereihenfolge.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public FEMBuffer(final File file, final boolean readonly, final ByteOrder order) throws IOException {
		this(new MappedBuffer(file, 0, readonly).order(order).growScale(1));
	}

	private long getFileType() {
		return this.buffer.getLong(0);
	}

	private void putFileType(final long fileType) {
		this.buffer.putLong(0, fileType);
	}

	private long getRootRef() {
		return this.buffer.getLong(8);
	}

	private void putRootRef(final long dataRoot) {
		this.buffer.putLong(8, dataRoot);
	}

	private void setNextAddr() {
		this.nextAddr = this.buffer.getLong(16);
	}

	private void putNextAddr(final long nextAddr) {
		this.buffer.putLong(16, nextAddr);
		this.nextAddr = nextAddr;
	}

	/** Diese Methode sucht die gegebene Funktion mit dem gegebenen Streuwert in der {@link #reusableTable} und gibt deren Referenz oder {@code 0} zurück. */
	private long getReusableRef(final FEMFunction reusable, final int reusableHash) {
		final int reusableLimit = this.reusableLimit, hashMask = reusableLimit - 1, reusableIndex = reusableHash & hashMask;
		final long reusableTable = this.reusableTable;
		final long reusableTable_headValueAddr = reusableTable + (reusableIndex * 4L);
		int reusedIndex = this.buffer.getInt(reusableTable_headValueAddr);
		while (reusedIndex >= 0) {
			final long reusableTable_hashValueAddr = reusableTable + (reusableLimit * 8L) + (reusedIndex * 4L);
			final int valHash = this.buffer.getInt(reusableTable_hashValueAddr);
			if (reusableHash == valHash) {
				final long reusableTable_itemValueAddr = reusableTable + (reusableLimit * 12L) + (reusedIndex * 8L);
				final long reusedRef = this.buffer.getLong(reusableTable_itemValueAddr);
				final FEMFunction reused = this.get(reusedRef);
				if (Objects.equals(reused, reusable)) return reusedRef;
			}
			final long reusableTable_nextValueAddr = reusableTable + (reusableLimit * 4L) + (reusedIndex * 4L);
			reusedIndex = this.buffer.getInt(reusableTable_nextValueAddr);
		}
		return 0;
	}

	/** Diese Methode fügt die Funktion mit der gegebenen Referenz in die {@link #reusableTable} ein. Wenn eine dazu {@link Objects#equals(Object, Object)
	 * äquivalente} Funktion bereits enthalten ist, wird die Referenz nicht in die Tabelle aufgenommen. Dies führt dann zu einem Speicherleck, da es mindestens
	 * zwei äquivalente Funktionen mit unterschiedlichen Adressen gibt. */
	private void putReusableRef(final long reusableRef) {
		final FEMFunction reusable = this.get(reusableRef);
		final int reusableHash = reusable.hashCode();
		final long reusedRef = this.getReusableRef(reusable, reusableHash);
		if (reusedRef != 0) return;
		this.growReusableTable();
		final int reusableLimit = this.reusableLimit, reusableIndex = this.reusableCount, hashMask = reusableLimit - 1, headIndex = reusableHash & hashMask;
		final long reusableTable = this.reusableTable;
		final long reusableTable_headValueAddr = reusableTable + (headIndex * 4L);
		final long reusableTable_nextValueAddr = reusableTable + (reusableLimit * 4L) + (reusableIndex * 4L);
		final long reusableTable_hashValueAddr = reusableTable + (reusableLimit * 8L) + (reusableIndex * 4L);
		final long reusableTable_itemValueAddr = reusableTable + (reusableLimit * 12L) + (reusableIndex * 8L);
		this.buffer.putInt(reusableTable_nextValueAddr, this.buffer.getInt(reusableTable_headValueAddr));
		this.buffer.putInt(reusableTable_headValueAddr, reusableIndex);
		this.buffer.putInt(reusableTable_hashValueAddr, reusableHash);
		this.buffer.putLong(reusableTable_itemValueAddr, reusableRef);
		this.putReusableCount(reusableIndex + 1);
	}

	private void setReusableCount() {
		this.reusableCount = this.buffer.getInt(24);
	}

	private void putReusableCount(final int reusableCount) {
		this.buffer.putInt(24, reusableCount);
		this.reusableCount = reusableCount;
	}

	private int setReusableLimit() {
		return this.reusableLimit = this.buffer.getInt(28);
	}

	private void putReusableLimit(final int reusableLimit) {
		this.buffer.putInt(28, reusableLimit);
		this.reusableLimit = reusableLimit;
	}

	private long setReusableTable() {
		return this.reusableTable = this.buffer.getLong(32);
	}

	private void putReusableTable(final long reusableTable) {
		this.buffer.putLong(32, reusableTable);
		this.reusableTable = reusableTable;
	}

	/** Diese Methode vergrößert die {@link #reusableTable}, wenn {@link #reusableCount} nicht kleiner als {@link #reusableLimit} ist. */
	private void growReusableTable() {
		final int reusableLimit = this.reusableLimit, reusableCount = this.reusableCount;
		if (reusableCount >= reusableLimit) {
			final int reusableLimit2 = reusableLimit * 2, hashMask = reusableLimit2 - 1;
			if (reusableLimit2 >= 0) {
				final long reusableTable = this.reusableTable, reusableTable2, reusableTableSize = reusableLimit * 20L;
				final long reusableTable3 = reusableTable - reusableTableSize;
				if (this.nextAddr > reusableTable3) {
					final long reusableTableSize2 = reusableTableSize * 2;
					this.buffer.grow(reusableTable + reusableTableSize + reusableTableSize2);
					reusableTable2 = (this.buffer.size() & -8L) - reusableTableSize2;
				} else {
					reusableTable2 = reusableTable3;
				}
				final long reusableTable_hashArrayAddr = reusableTable + (reusableLimit * 8L);
				final long reusableTable_hashArrayAddr2 = reusableTable2 + (reusableLimit2 * 8L);
				this.buffer.copy(reusableTable_hashArrayAddr2, reusableTable_hashArrayAddr, reusableCount * 4L);
				final long reusableTable_itemArrayAddr = reusableTable + (reusableLimit * 12L);
				final long reusableTable_itemArrayAddr2 = reusableTable2 + (reusableLimit2 * 12L);
				this.buffer.copy(reusableTable_itemArrayAddr2, reusableTable_itemArrayAddr, reusableCount * 8L);
				final int[] headValue = new int[Math.min(262144, reusableLimit2)];
				Arrays.fill(headValue, -1);
				for (int reusedIndex2 = 0; reusedIndex2 < reusableLimit2; reusedIndex2 += headValue.length) {
					final long reusableTable_headValueAddr2 = reusableTable2 + (reusedIndex2 * 4L);
					this.buffer.putInt(reusableTable_headValueAddr2, headValue);
				}
				for (int reusedIndex = 0; reusedIndex < reusableCount; reusedIndex++) {
					final long reusableTable_hashItemAddr2 = reusableTable_hashArrayAddr2 + (reusedIndex * 4L);
					final int reusableHash = this.buffer.getInt(reusableTable_hashItemAddr2), reusableIndex = reusableHash & hashMask;
					final long reusableTable_headValueAddr2 = reusableTable2 + (reusableIndex * 4L);
					final long reusableTable_nextValueAddr2 = reusableTable2 + (reusableLimit2 * 4L) + (reusedIndex * 4L);
					this.buffer.putInt(reusableTable_nextValueAddr2, this.buffer.getInt(reusableTable_headValueAddr2));
					this.buffer.putInt(reusableTable_headValueAddr2, reusedIndex);
				}
				this.putReusableTable(reusableTable2);
				this.putReusableLimit(reusableLimit2);
			} else {
				this.clearReusables();
			}
		}
	}

	private void clearBuffer() {
		this.putRootRef(0);
		this.putNextAddr(40L);
		this.clearReusables();
	}

	private void clearProxies() {
		this.proxyGetMap.clear();
		this.proxyGetList.clear();
		this.proxyPutList.clear();
		this.proxyGetMap.compact();
	}

	private void clearReusables() {
		final long reusableTable = this.nextAddr;
		this.putReusableCount(0);
		this.putReusableLimit(2);
		this.putReusableTable(reusableTable);
		this.buffer.putInt(reusableTable + 0L, -1);
		this.buffer.putInt(reusableTable + 4L, -1);
		this.buffer.resize(this.size());
	}

	FEMFunction reusablesGet(final int index) {
		synchronized (this.buffer) {
			if ((index < 0) || (index >= this.reusableCount)) throw new IndexOutOfBoundsException();
			final long reusableTable_itemValueAddr = this.reusableTable + (this.reusableLimit * 12L) + (index * 8L);
			return this.get(this.buffer.getLong(reusableTable_itemValueAddr));
		}
	}

	int reusablesSize() {
		synchronized (this.buffer) {
			return this.reusableCount;
		}
	}

	boolean reusablesContains(final Object item) {
		synchronized (this.buffer) {
			return (item instanceof FEMFunction) && (this.getReusableRef((FEMFunction)item, item.hashCode()) != 0);
		}
	}

	@Override
	public FEMFunction get() {
		return this.get(this.getRootRef());
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz zurück. Für die Referenz {@code 0} wird {@code null} geliefert.
	 *
	 * @param ref Referenz oder {@code 0}.
	 * @return Funktion oder {@code null}.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public FEMFunction get(final long ref) throws IllegalArgumentException {
		return ref != 0 ? this.customGet(this.getHead(ref), this.getBody(ref)) : null;
	}

	/** Diese Methode gibt die Funktion zur gegebenen Referenz als Instanz der gegebenen Klasse zurück und ist eine Abkürzung für {@link Class#cast(Object)
	 * clazz.cast(this.get(ref))}.
	 *
	 * @param ref Referenz.
	 * @param clazz Klasse der gelieferten Funktion.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public <GResult> GResult get(final long ref, final Class<GResult> clazz) throws IllegalArgumentException {
		try {
			return clazz.cast(this.get(ref));
		} catch (final IllegalArgumentException cause) {
			throw cause;
		} catch (final Exception cause) {
			throw new IllegalArgumentException(cause);
		}
	}

	/** Diese Methode gibt die Funktion zu der Referenz zurück, die an der gegebenen Adresse steht, und ist eine Abkürzung für {@link #get(long)
	 * this.get(this.buffer().getLong(addr))}. */
	public FEMFunction getAt(final long addr) throws IllegalArgumentException {
		return this.get(this.buffer.getLong(addr));
	}

	/** Diese Methode gibt die Funktion zu der Referenz zurück, die an der gegebenen Adresse steht, und ist eine Abkürzung für {@link #get(long, Class)
	 * this.get(this.buffer().getLong(addr), clazz)}. */
	public <GResult> GResult getAt(final long addr, final Class<GResult> clazz) throws IllegalArgumentException {
		return this.get(this.buffer.getLong(addr), clazz);
	}

	/** Diese Methode gibt die Funktionen zur gegebene Anzahl an Referenzen im gegebenen Speicherbereich zurück.
	 *
	 * @see #getAt(long)
	 * @param addr Adresse des Speicherbereichs.
	 * @param count Anzahl der Referenzen.
	 * @return Funktionen. */
	public FEMFunction[] getAllAt(final long addr, final int count) throws IllegalArgumentException {
		final FEMFunction[] result = new FEMFunction[count];
		for (int i = 0; i < count; i++) {
			result[i] = this.getAt(addr + (i * 8));
		}
		return result;
	}

	/** Diese Methode sucht die gegebene Funktion in den {@link #reusables() wiederverwendbaren Funktionen} und gibt deren Referenz oder {@code 0} zurück.
	 *
	 * @param src Funktion.
	 * @return Referenz oder {@code 0}. */
	protected long getRef(final FEMFunction src) {
		return this.getReusableRef(src, src.hashCode());
	}

	/** Diese Methode gibt die Referenz mit den gegebenen Eigenschaften zurück.
	 *
	 * @param head Kopfdaten als Typkennung (6 Bit).
	 * @param body Rumpfdaten als Adresse oder Nutzdaten (58 Bit).
	 * @return Referenz. */
	protected long getRef(final int head, final long body) {
		return (body << 6) | head;
	}

	/** Diese Methode gibt die Kopfdaten der gegebenen Referenz zurück. */
	protected int getHead(final long ref) {
		return (int)(ref & 63);
	}

	/** Diese Methode gibt die Rumpfdaten der gegebenen Referenz zurück. */
	protected long getBody(final long ref) {
		return ref >>> 6;
	}

	@Override
	public void set(final FEMFunction value) {
		this.putRootRef(this.put(value));
	}

	/** Diese Methode fügt die gegebene Funktion in den Puffer ein und gibt die Referenz darauf zurück. Für die Funktion {@code null} wird {@code 0} geliefert.
	 *
	 * @param src Funktion oder {@code null}.
	 * @return Referenz auf die Funktion oder {@code 0}.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn die Funktion nicht angefügt werden kann. */
	public long put(final FEMFunction src) throws IllegalStateException, IllegalArgumentException {
		if (src == null) return 0;
		synchronized (this.buffer) {
			return this.customPut(src);
		}
	}

	/** Diese Methode fügt den gegebenen Platzhalter sowie die gegebene Zielfunktion in den Puffer ein und übernimmt die Zielfunktion für den hinterlegten
	 * Platzhalter.
	 *
	 * @param proxy Platzhalter.
	 * @param target Zielfunktion oder {@code null}.
	 * @throws NullPointerException Wenn {@code proxy} {@code null} ist.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist oder der hinterlegte Platzhalter unzulässig kodiert ist.
	 * @throws IllegalArgumentException Wenn die Funktionen nicht angefügt werden können. */
	public void put(final FEMProxy proxy, final FEMFunction target) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		synchronized (this.buffer) {
			final long ref = this.put(proxy);
			if (this.getHead(ref) != FEMBuffer.TYPE_PROXY_ADDR) throw new IllegalStateException();
			final long addr = this.getBody(ref);
			this.proxyPutList.addLast(Entries.from(Long.valueOf(addr), target));
			this.runProxyPutList();
		}
	}

	/** Diese Methode {@link #put(FEMFunction) überführt} die gegebenen Funktionen in deren Referenzen und gibt die Liste dieser Referenzen zurück.
	 *
	 * @param src Funktionen.
	 * @return Referenzen auf die Funktionen.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn der Puffer nur zum Lesen angebunden ist.
	 * @throws IllegalArgumentException Wenn mindestens eine der Funktionen nicht angefügt werden kann. */
	public long[] putAll(final FEMFunction... src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.length;
		final long[] result = new long[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.put(src[i]);
		}
		return result;
	}

	/** Diese Methode gibt die {@link #getRef(int, long) Referenz mit den gegebenen Eigenschaften} zurück und speichert diese zur {@link #reusables()
	 * Wiederverwendung}.
	 *
	 * @param head Kopfdaten, bspw. Typkennung (6 Bit).
	 * @param body Rumpfdaten, bspw. Adresse (58 Bit).
	 * @return Referenz. */
	protected long putRef(final int head, final long body) {
		final long ref = this.getRef(head, body);
		this.putReusableRef(ref);
		return ref;
	}

	/** Diese Methode reserviert einen neuen Speicherbereich mit der gegebenen Größe und gibt seine Adresse zurück. Diese Adresse ist stets ein Vielfaches von
	 * {@code 8}.
	 *
	 * @param size Größe des Speicherbereichs in Byte.
	 * @return Adresse des Speicherbereichs.
	 * @throws IllegalStateException Wenn der Puffer nicht zum schreiben angebunden ist.
	 * @throws IllegalArgumentException Wenn {@code size} ungültig ist. */
	protected long putData(final long size) throws IllegalStateException, IllegalArgumentException {
		synchronized (this.buffer) {
			final long nextAddr = this.nextAddr, nextAddr2 = (nextAddr + size + 7) & -8L, reusableTable = this.reusableTable;
			if (nextAddr2 < nextAddr) throw new IllegalArgumentException();
			if (nextAddr2 > reusableTable) {
				final long reusableTableSize = this.reusableLimit * 20L;
				this.buffer.grow(nextAddr2 + reusableTableSize + reusableTableSize);
				final long reusableTable2 = (this.buffer.size() - reusableTableSize) & -8L;
				this.buffer.copy(reusableTable2, reusableTable, reusableTableSize);
				this.putReusableTable(reusableTable2);
			}
			this.putNextAddr(nextAddr2);
			return nextAddr;
		}
	}

	/** Diese Methode gibt die Funktion zu den gegebenen Kopf- und Rumpfdaten zurück.
	 *
	 * @param head Kopfdaten als Typkennung.
	 * @param body Rumpfdaten als Adresse oder Nutzdaten.
	 * @return Funktion.
	 * @throws IllegalArgumentException Wenn die gegebenen Daten ungültig sind. */
	protected FEMFunction customGet(final int head, final long body) {
		switch (head) {
			case TYPE_VOID_DATA:
				return FEMVoid.INSTANCE;
			case TYPE_ARRAY_DATA:
				return FEMArray.EMPTY;
			case TYPE_ARRAY_ADDR:
				return this.getArrayByAddr(body);
			case TYPE_STRING_DATA:
				return this.getStringByData(body);
			case TYPE_STRING_ADDR1:
				return this.getStringByAddr1(body);
			case TYPE_STRING_ADDR2:
				return this.getStringByAddr2(body);
			case TYPE_STRING_ADDR3:
				return this.getStringByAddr3(body);
			case TYPE_BINARY_DATA:
				return this.getBinaryByData(body);
			case TYPE_BINARY_ADDR:
				return this.getBinaryByAddr(body);
			case TYPE_OBJECT_DATA1:
				return this.getObjectByData1(body);
			case TYPE_OBJECT_DATA2:
				return this.getObjectByData2(body);
			case TYPE_OBJECT_ADDR:
				return this.getObjectByAddr(body);
			case TYPE_INTEGER_DATA1:
				return this.getIntegerByData1(body);
			case TYPE_INTEGER_DATA2:
				return this.getIntegerByData2(body);
			case TYPE_INTEGER_ADDR:
				return this.getIntegerByAddr(body);
			case TYPE_DECIMAL_ADDR:
				return this.getDecimalByAddr(body);
			case TYPE_BOOLEAN_DATA:
				return this.getBooleanByData(body);
			case TYPE_HANDLER_ADDR:
				return this.getHandlerByAddr(body);
			case TYPE_DATETIME_DATA1:
				return this.getDatetimeByData1(body);
			case TYPE_DATETIME_DATA2:
				return this.getDatetimeByData2(body);
			case TYPE_DATETIME_ADDR:
				return this.getDatetimeByAddr(body);
			case TYPE_DURATION_DATA1:
				return this.getDurationByData1(body);
			case TYPE_DURATION_DATA2:
				return this.getDurationByData2(body);
			case TYPE_DURATION_ADDR:
				return this.getDurationByAddr(body);
			case TYPE_PROXY_ADDR:
				return this.getProxyByAddr(body);
			case TYPE_PARAM_DATA:
				return this.getParamByData(body);
			case TYPE_CLOSURE_ADDR:
				return this.getClosureByAddr(body);
			case TYPE_COMPOSITE_ADDR:
				return this.getCompositeByAddr(body);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Referenz zur gegebenen Funktion zurück. Dazu erfolgt auf dem Typ der Funktion eine Fallunterscheidung.
	 *
	 * @param src Funktion.
	 * @return Referenz.
	 * @throws NullPointerException Wenn {@code src} {@code null} ist.
	 * @throws IllegalStateException Wenn {@link #putData(long)} diese auslöst.
	 * @throws IllegalArgumentException Wenn die Funktion nicht ausgelagert werden kann. */
	protected long customPut(final FEMFunction src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		if (src instanceof FEMVoid) return this.putVoidAsRef();
		if (src instanceof FEMBoolean) return this.putBooleanAsRef((FEMBoolean)src);
		if (src instanceof FEMFuture) return this.putFutureAsRef((FEMFuture)src);
		if (src instanceof FEMArray) return this.putArrayAsRef((FEMArray)src);
		if (src instanceof FEMString) return this.putStringAsRef((FEMString)src);
		if (src instanceof FEMBinary) return this.putBinaryAsRef((FEMBinary)src);
		if (src instanceof FEMObject) return this.putObjectAsRef((FEMObject)src);
		if (src instanceof FEMInteger) return this.putIntegerAsRef((FEMInteger)src);
		if (src instanceof FEMDecimal) return this.putDecimalAsRef((FEMDecimal)src);
		if (src instanceof FEMHandler) return this.putHandlerAsRef((FEMHandler)src);
		if (src instanceof FEMDatetime) return this.putDatetimeAsRef((FEMDatetime)src);
		if (src instanceof FEMDuration) return this.putDurationAsRef((FEMDuration)src);
		if (src instanceof FEMProxy) return this.putProxyAsRef((FEMProxy)src);
		if (src instanceof FEMParam) return this.putParamAsRef((FEMParam)src);
		if (src instanceof FEMClosure) return this.putClosureAsRef((FEMClosure)src);
		if (src instanceof FEMComposite) return this.putCompositeAsRef((FEMComposite)src);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Referenz auf {@link FEMVoid#INSTANCE} zurück. */
	protected long putVoidAsRef() {
		return this.getRef(FEMBuffer.TYPE_VOID_DATA, 1);
	}

	/** Diese Methode fügt den gegebenen Ergebniswert in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putFutureAsRef(final FEMFuture src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		return this.put(src.result());
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene Wertliste zurück. Die Struktur des Speicherbereichs ist
	 * {@code (length: int, hash: int, valueRef: long[length], tableAddr: long)}. Sie besteht aus {@link FEMArray#length() Länge}, {@link FEMArray#hashCode()
	 * Streuwert}, den {@link #putAll(FEMFunction...) Referenzen} der Elemente sowie der Adresse einer {@link CompactArray3#table Streuwerttabelle} zur
	 * beschleunigten Einzelwertsuche. Letztere kann {@code 0} sein. */
	protected FEMArray getArrayByAddr(final long addr) throws IllegalArgumentException {
		return new MappedArray(this, addr);
	}

	/** Diese Methode fügt die gegebene Wertliste in den Puffer ein und gibt die Adresse darauf zurück. Eine über {@link FEMArray#compact(boolean)} indizierte
	 * Wertliste wird mit der Indizierung kodiert. */
	protected long putArrayAsRef(final FEMArray src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final int length = src.length();
		if (length == 0) return this.getRef(FEMBuffer.TYPE_ARRAY_DATA, 0);
		final FEMArray src2 = src.compact();
		final long ref = this.getRef(src2), addr;
		if (ref != 0) {
			if (!src.isIndexed()) return ref;
			addr = this.getBody(ref);
			if (this.buffer.getLong(addr + 8 + (length * 8L)) != 0) return ref;
		} else {
			addr = this.putData((length * 8L) + 16);
			this.buffer.putInt(addr + 0, length);
			this.buffer.putInt(addr + 4, src2.hashCode());
			this.buffer.putLong(addr + 8 + (length * 8L), 0);
			this.buffer.putLong(addr + 8, this.putAll(src2.value()));
			if (!src.isIndexed()) return this.putRef(FEMBuffer.TYPE_ARRAY_ADDR, addr);
		}
		final CompactArray3 src3 = (src2 instanceof CompactArray3) ? (CompactArray3)src2 : new CompactArray3(src2.value());
		final long addr2 = this.putData(src3.table.length * 4L);
		this.buffer.putInt(addr2, src3.table);
		this.buffer.putLong(addr + 8 + (length * 8L), addr2);
		return this.putRef(FEMBuffer.TYPE_ARRAY_ADDR, addr);
	}

	/** Diese Methode gibt die Zeichenkette mit den gegebenen Daten ({@code length: 32, value: 32}) zurück. */
	protected FEMString getStringByData(final long data) {
		return FEMString.from(Integers.toIntL(data), Integers.toIntH(data));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code byte}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (length: int, hash: int, item: byte[length])}. */
	protected FEMString getStringByAddr1(final long addr) throws IllegalArgumentException {
		return new MappedStringINT8(this.buffer, addr);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code short}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (length: int, hash: int, item: short[length])}. */
	protected FEMString getStringByAddr2(final long addr) throws IllegalArgumentException {
		return new MappedStringINT16(this.buffer, addr);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich enthaltene {@code int}-Zeichenkette zurück. Die Struktur des Speicherbereichs ist
	 * {@code (length: int, hash: int, item: int[length])}. */
	protected FEMString getStringByAddr3(final long addr) throws IllegalArgumentException {
		return new MappedStringINT32(this.buffer, addr);
	}

	/** Diese Methode fügt die gegebene Zeichenkette in den Puffer ein und gibt die Adresse darauf zurück. Dazu werden die {@link FEMString#compact()
	 * kompaktierten} Formen der Zeichenkette analysiert und entsprechend als {@code byte}, {@code short} oder {@code int}-Codepoints gespeichert. */
	protected long putStringAsRef(final FEMString src) throws NullPointerException, IllegalStateException {
		final int length = src.length();
		if (length == 0) return this.getRef(FEMBuffer.TYPE_STRING_DATA, 0);
		final FEMString src2 = src.compact();
		if (src2.isUniform()) return this.getRef(FEMBuffer.TYPE_STRING_DATA, Integers.toLong(src2.get(0), length));
		final long ref = this.getRef(src2);
		if (ref != 0) return ref;
		if ((src2 instanceof CompactStringINT8) || (src2 instanceof MappedStringINT8)) {
			final long addr = this.putData(length + 8L);
			this.buffer.putInt(addr + 0, length);
			this.buffer.putInt(addr + 4, src2.hashCode());
			this.buffer.put(addr + 8, src2.toBytes());
			return this.putRef(FEMBuffer.TYPE_STRING_ADDR1, addr);
		} else if ((src2 instanceof CompactStringINT16) || (src2 instanceof MappedStringINT16)) {
			final long addr = this.putData((length * 2L) + 8L);
			this.buffer.putInt(addr + 0, length);
			this.buffer.putInt(addr + 4, src2.hashCode());
			this.buffer.putShort(addr + 8, src2.toShorts());
			return this.putRef(FEMBuffer.TYPE_STRING_ADDR2, addr);
		} else {
			final long addr = this.putData((length * 4L) + 8L);
			this.buffer.putInt(addr + 0, length);
			this.buffer.putInt(addr + 4, src2.hashCode());
			this.buffer.putInt(addr + 8, src2.toInts());
			return this.putRef(FEMBuffer.TYPE_STRING_ADDR3, addr);
		}
	}

	/** Diese Methode gibt die Bytefolge mit den gegebenen Daten ({@code item: 8}) zurück. */
	protected FEMBinary getBinaryByData(final long data) {
		return FEMBinary.from(Integers.toIntL(data), (byte)Integers.toIntH(data));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code length: int, hash: int, item: byte[length]}) enthaltene Bytefolge zurück. */
	protected FEMBinary getBinaryByAddr(final long addr) throws IllegalArgumentException {
		return new MappedBinary(this.buffer, addr);
	}

	/** Diese Methode fügt die gegebene Bytefolge in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putBinaryAsRef(final FEMBinary src) throws NullPointerException, IllegalStateException {
		final int length = src.length();
		if (length == 0) return this.getRef(FEMBuffer.TYPE_BINARY_DATA, 0);
		final FEMBinary src2 = src.compact();
		if (src2.isUniform()) return this.getRef(FEMBuffer.TYPE_BINARY_DATA, Integers.toLong(src2.get(0) & 0xFF, length));
		final long ref = this.getRef(src2);
		if (ref != 0) return ref;
		final long addr = this.putData(length + 8L);
		this.buffer.putInt(addr + 0, length);
		this.buffer.putInt(addr + 4, src2.hashCode());
		this.buffer.put(addr + 8, src2.value());
		return this.putRef(FEMBuffer.TYPE_BINARY_ADDR, addr);
	}

	/** Diese Methode gibt die Dezimalzahl mit den gegebenen Daten ({@code +value: 58}) zurück. */
	protected FEMInteger getIntegerByData1(final long data) throws IllegalArgumentException {
		return new FEMInteger(+data);
	}

	/** Diese Methode gibt die Dezimalzahl mit den gegebenen Daten ({@code -value: 58}) zurück. */
	protected FEMInteger getIntegerByData2(final long data) throws IllegalArgumentException {
		return new FEMInteger(-data);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code value: long}) enthaltene Dezimalzahl zurück. */
	protected FEMInteger getIntegerByAddr(final long addr) throws IllegalArgumentException {
		return new FEMInteger(this.buffer.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Dezimalzahl in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putIntegerAsRef(final FEMInteger src) throws NullPointerException, IllegalStateException {
		final long data1 = src.value();
		if (this.getBody(this.getRef(0, data1)) == data1) return this.getRef(FEMBuffer.TYPE_INTEGER_DATA1, data1);
		final long data2 = -data1;
		if (this.getBody(this.getRef(0, data2)) == data2) return this.getRef(FEMBuffer.TYPE_INTEGER_DATA2, data2);
		final long ref = this.getRef(src);
		if (ref != 0) return ref;
		final long addr = this.putData(8);
		this.buffer.putLong(addr, data1);
		return this.putRef(FEMBuffer.TYPE_INTEGER_ADDR, addr);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich ({@code value: double}) enthaltenen Dezimalbruch zurück. */
	protected FEMDecimal getDecimalByAddr(final long addr) throws IllegalArgumentException {
		return new FEMDecimal(Double.longBitsToDouble(this.buffer.getLong(addr)));
	}

	/** Diese Methode fügt den gegebenen Dezimalbruch in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putDecimalAsRef(final FEMDecimal src) throws NullPointerException, IllegalStateException {
		final long ref = this.getRef(src);
		if (ref != 0) return ref;
		final long addr = this.putData(8);
		this.buffer.putLong(addr, Double.doubleToRawLongBits(src.value()));
		return this.putRef(FEMBuffer.TYPE_DECIMAL_ADDR, addr);
	}

	/** Diese Methode gibt die Referenz auf den gegebenen Wahrheitswert zurück. */
	protected long putBooleanAsRef(final FEMBoolean src) {
		return this.getRef(FEMBuffer.TYPE_BOOLEAN_DATA, src.value() ? 1 : 0);
	}

	/** Diese Methode gibt den Wahrheitswert mit den gegebenen Daten zurück. */
	protected FEMBoolean getBooleanByData(final long data) throws IllegalArgumentException {
		return FEMBoolean.from(data != 0);
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code value: long}) enthaltene Zeitspanne zurück. */
	protected FEMDuration getDurationByAddr(final long addr) throws IllegalArgumentException {
		return new FEMDuration(this.buffer.getLong(addr));
	}

	/** Diese Methode gibt die Zeitspanne mit den gegebenen Daten zurück. */
	protected FEMDuration getDurationByData1(final long data) throws IllegalArgumentException {
		return new FEMDuration(data);
	}

	/** Diese Methode gibt die Zeitspanne mit den gegebenen Daten zurück. */
	protected FEMDuration getDurationByData2(final long data) throws IllegalArgumentException {
		return new FEMDuration(this.getRef(0, data));
	}

	/** Diese Methode fügt den gegebenen Zeitspanne in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putDurationAsRef(final FEMDuration src) throws NullPointerException, IllegalStateException {
		final long data1 = src.value();
		if (this.getBody(this.getRef(0, data1)) == data1) return this.getRef(FEMBuffer.TYPE_DURATION_DATA1, data1);
		final long data2 = this.getBody(data1);
		if (this.getRef(0, data2) == data1) return this.getRef(FEMBuffer.TYPE_DURATION_DATA2, data2);
		final long ref = this.getRef(src);
		if (ref != 0) return ref;
		final long addr = this.putData(8);
		this.buffer.putLong(addr, data1);
		return this.putRef(FEMBuffer.TYPE_DURATION_ADDR, addr);
	}

	/** Diese Methode gibt die Zeitangabe mit den gegebenen Daten zurück. */
	protected FEMDatetime getDatetimeByData1(final long data) throws IllegalArgumentException {
		return new FEMDatetime(data);
	}

	/** Diese Methode gibt die Zeitangabe mit den gegebenen Daten zurück. */
	protected FEMDatetime getDatetimeByData2(final long data) throws IllegalArgumentException {
		return new FEMDatetime(this.getRef(0, data));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code value: long}) enthaltene Zeitangabe zurück. */
	protected FEMDatetime getDatetimeByAddr(final long addr) throws IllegalArgumentException {
		return new FEMDatetime(this.buffer.getLong(addr));
	}

	/** Diese Methode fügt die gegebene Zeitangabe in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putDatetimeAsRef(final FEMDatetime src) throws NullPointerException, IllegalStateException {
		final long data1 = src.value();
		if (this.getBody(this.getRef(0, data1)) == data1) return this.getRef(FEMBuffer.TYPE_DATETIME_DATA1, data1);
		final long data2 = this.getBody(data1);
		if (this.getRef(0, data2) == data1) return this.getRef(FEMBuffer.TYPE_DATETIME_DATA2, data2);
		final long ref = this.getRef(src);
		if (ref != 0) return ref;
		final long addr = this.putData(8);
		this.buffer.putLong(addr, data1);
		return this.putRef(FEMBuffer.TYPE_DATETIME_ADDR, addr);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich ({@code functionRef: long}) enthaltenen Funktionszeiger zurück. */
	protected FEMHandler getHandlerByAddr(final long addr) throws IllegalArgumentException {
		return new FEMHandler(this.getAt(addr));
	}

	/** Diese Methode fügt den gegebenen Funktionszeiger in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putHandlerAsRef(final FEMHandler src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final long ref = this.getRef(src);
		if (ref != 0) return ref;
		final long addr = this.putData(8);
		this.buffer.putLong(addr, this.put(src.value));
		return this.putRef(FEMBuffer.TYPE_HANDLER_ADDR, addr);
	}

	/** Diese Methode gibt die Objektreferenz mit den gegebenen Daten zurück. */
	protected FEMObject getObjectByData1(final long data) throws IllegalArgumentException {
		return new FEMObject(data);
	}

	/** Diese Methode gibt die Objektreferenz mit den gegebenen Daten zurück. */
	protected FEMObject getObjectByData2(final long data) throws IllegalArgumentException {
		return new FEMObject(this.getRef(0, data));
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code value: long}) enthaltene Objektreferenz zurück. */
	protected FEMObject getObjectByAddr(final long addr) throws IllegalArgumentException {
		return new FEMObject(this.buffer.getLong(addr));
	}

	/** Diese Methode fügt den gegebenen Objektreferenz in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putObjectAsRef(final FEMObject src) throws NullPointerException, IllegalStateException {
		final long data1 = src.value();
		if (this.getBody(this.getRef(0, data1)) == data1) return this.getRef(FEMBuffer.TYPE_OBJECT_DATA1, data1);
		final long data2 = this.getBody(data1);
		if (this.getRef(0, data2) == data1) return this.getRef(FEMBuffer.TYPE_OBJECT_DATA2, data2);
		final long ref = this.getRef(src);
		if (ref != 0) return ref;
		final long addr = this.putData(8);
		this.buffer.putLong(addr, data1);
		return this.putRef(FEMBuffer.TYPE_OBJECT_ADDR, addr);
	}

	/** Diese Methode gibt den im gegebenen Speicherbereich ({@code idRef: long, nameRef: long, targetRef: long}) enthaltenen Platzhalter zurück. */
	protected FEMProxy getProxyByAddr(final long addr) throws IllegalArgumentException {
		synchronized (this.buffer) {
			final Long addr2 = Long.valueOf(addr);
			final FEMProxy proxy = this.proxyGetMap.get(addr2);
			if (proxy != null) return proxy;
			final FEMValue id = this.getAt(addr + 0, FEMValue.class);
			final FEMString name = this.getAt(addr + 8, FEMString.class);
			final FEMProxy proxy2 = new FEMProxy(id, name, null);
			this.proxyGetMap.put(addr2, proxy2);
			this.proxyGetList.addLast(addr2);
			this.runProxyGetList();
			return proxy2;
		}
	}

	/** Diese Methode fügt den gegebenen Platzhalter in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putProxyAsRef(final FEMProxy src) throws NullPointerException, IllegalArgumentException {
		final long ref = this.getRef(src);
		if (ref != 0) return ref;
		final long idRef = this.put(src.id());
		final FEMValue id2 = this.get(idRef, FEMValue.class);
		final long nameRef = this.put(src.name());
		final FEMString name2 = this.get(nameRef, FEMString.class);
		final FEMProxy proxy2 = FEMProxy.from(id2, name2);
		final long addr = this.putData(24);
		final Long addr2 = Long.valueOf(addr);
		this.buffer.putLong(addr + 0, idRef);
		this.buffer.putLong(addr + 8, nameRef);
		this.buffer.putLong(addr + 16, 0);
		this.proxyGetMap.put(addr2, proxy2);
		this.proxyPutList.addLast(Entries.from(addr2, src.get()));
		this.runProxyPutList();
		return this.putRef(FEMBuffer.TYPE_PROXY_ADDR, addr);
	}

	/** Diese Methode verarbeitet die in {@link #proxyGetList} erfassten Adressen, sofern dort weniger als 2 erfasst wurden. */
	protected void runProxyGetList() {
		if (this.proxyGetList.size() > 1) return;
		while (!this.proxyGetList.isEmpty()) {
			this.runProxyGetItem(this.proxyGetList.getFirst());
			this.proxyGetList.removeFirst();
		}
	}

	/** Diese Methode setzt die Zielfunktion des Platzhalters mit der gegebenen Adresse. */
	protected void runProxyGetItem(final Long addr) {
		final FEMProxy proxy = this.proxyGetMap.get(addr);
		final FEMFunction target = this.getAt(addr.longValue() + 16);
		this.setProxyTarget(proxy, target);
	}

	/** Diese Methode verarbeitet die in {@link #proxyPutList} und {@link #put(FEMProxy, FEMFunction)} erfassten Paare aus Adresse und Zielfunktion, sofern dort
	 * weniger als 2 erfasst wurden. */
	protected void runProxyPutList() {
		if (this.proxyPutList.size() > 1) return;
		while (!this.proxyPutList.isEmpty()) {
			final Entry<Long, FEMFunction> next = this.proxyPutList.getFirst();
			this.runProxyPutItem(next.getKey(), next.getValue());
			this.proxyPutList.removeFirst();
		}
	}

	/** Diese Methode trägt die Zielfunktion des Platzhalters mit der gegebenen Adresse in den Puffer ein. */
	protected void runProxyPutItem(final Long addr, final FEMFunction target) {
		this.buffer.putLong(addr.longValue() + 16, this.put(target));
		this.runProxyGetItem(addr);
	}

	/** Diese Methode setzt die gegebene Funktion als {@link FEMProxy#set(FEMFunction) Zielfunktion} des gegebenen {@link FEMProxy Funktionsplatzhalters}. */
	protected void setProxyTarget(final FEMProxy res, final FEMFunction fun) {
		res.set(fun);
	}

	/** Diese Methode gibt die Parameterfunktion zum gegebenen Index zurück. */
	protected FEMParam getParamByData(final long data) throws IllegalArgumentException {
		return FEMParam.from((int)data);
	}

	/** Diese Methode fügt die gegebene Parameterfunktion in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putParamAsRef(final FEMParam src) throws NullPointerException, IllegalStateException {
		return this.getRef(FEMBuffer.TYPE_PARAM_DATA, src.index());
	}

	/** Diese Methode gibt die im gegebenen Speicherbereich ({@code targetRef: long}) enthaltene Funktionsbindung zurück. */
	protected FEMClosure getClosureByAddr(final long addr) throws IllegalArgumentException {
		return new FEMClosure(this.getAt(addr));
	}

	/** Diese Methode fügt die gegebene Funktionsbindung in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putClosureAsRef(final FEMClosure src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final long ref = this.getRef(src);
		if (ref != 0) return ref;
		final long addr = this.putData(8);
		this.buffer.putLong(addr, this.put(src.target));
		return this.putRef(FEMBuffer.TYPE_CLOSURE_ADDR, addr);
	}

	/** Diese Methode gibt dden im gegebenen Speicherbereich ({@code count: int, concat: int, targetRef: long, paramRef: long[count]}) enthaltenen Funktionsaufruf
	 * zurück. */
	protected FEMFunction getCompositeByAddr(final long addr) throws IllegalArgumentException {
		return FEMComposite.from(this.buffer.getInt(addr + 4) != 0, this.getAt(addr + 8), this.getAllAt(addr + 16, this.buffer.getInt(addr)));
	}

	/** Diese Methode fügt den gegebenen Funktionsaufruf in den Puffer ein und gibt die Referenz darauf zurück. */
	protected long putCompositeAsRef(final FEMComposite src) throws NullPointerException, IllegalStateException, IllegalArgumentException {
		final long ref = this.getRef(src);
		if (ref != 0) return ref;
		final int count = src.params.length;
		final long addr = this.putData((count * 8L) + 16L);
		this.buffer.putInt(addr + 0, count);
		this.buffer.putInt(addr + 4, src.isConcat() ? 1 : 0);
		this.buffer.putLong(addr + 8, this.put(src.target));
		this.buffer.putLong(addr + 16, this.putAll(src.params));
		return this.putRef(FEMBuffer.TYPE_COMPOSITE_ADDR, addr);
	}

	/** Diese Methode leert den {@link MappedBuffer Dateipuffer} und entfernt damit alle bisher ausgelagerten Funktionen. */
	public void reset() throws IllegalStateException {
		if (this.buffer.isReadonly()) throw new IllegalStateException();
		synchronized (this.buffer) {
			this.clearProxies();
			this.clearBuffer();
		}
	}

	/** Diese Methode leert den Puffer zur Behandlung der Rekursion bei {@link FEMProxy Platzhaltern}. */
	public void cleanup() {
		synchronized (this.buffer) {
			this.clearProxies();
		}
	}

	/** Diese Methode leert den Puffer zur Erkennung {@link #reusables() wiederverwendbarer Funktionen} und minimiert damit die {@link #size()}. */
	public void minimize() throws IllegalStateException {
		if (this.buffer.isReadonly()) throw new IllegalStateException();
		synchronized (this.buffer) {
			this.clearReusables();
		}
	}

	@Override
	public long emu() {
		synchronized (this.buffer) {
			return EMU.fromObject(this) + EMU.fromAll(this.buffer, this.proxyGetMap, this.proxyGetList, this.proxyPutList, this.reusables)
				+ EMU.fromAll(this.proxyGetMap.values());
		}
	}

	/** Diese Methode liefert die Größe des im {@link #buffer()} benutzten Speicherbereichs. */
	public long size() {
		synchronized (this.buffer) {
			return this.reusableTable + (this.reusableLimit * 20L);
		}
	}

	/** Diese Methode gibt den {@link MappedBuffer Dateipuffer} zurück, in welchen die Funktionen ausgelagert sind.
	 *
	 * @return Dateipuffer. */
	public MappedBuffer buffer() {
		return this.buffer;
	}

	/** Diese Methode gibt die liste der Wiederverwendeten Funktionen zurück. */
	public Reusables reusables() {
		return this.reusables;
	}

	@Override
	public String toString() {
		synchronized (this.buffer) {
			return Objects.toInvokeString(this, this.buffer.file().toString(), "file=" + Integers.printSize(this.buffer.file().length()) + " used="
				+ Integers.printSize(this.size()) + " heap=" + Integers.printSize(this.emu()) + " reusables=" + this.reusableCount);
		}
	}

}
