package bee.creative.io;

import java.io.File;
import java.io.IOException;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.iam.IAMArray;
import bee.creative.lang.Integers;
import bee.creative.lang.Objects;
import bee.creative.mmi.MMIArray;
import bee.creative.mmi.MMIArrayL;
import bee.creative.util.HashMapOI;

/** Diese Klasse ergänzt einen {@link MappedBuffer} um Methoden zur Auslagerung typisierter Speicherbereiche in eine Datei mit der nachfolgend spezifizierten
 * Dateistruktur.
 * <p>
 * Die Dateistruktur besteht aus folgender Sequenz und kann ca. 33 GB groß werden:<br>
 * {@code MAGIC:4, COUNT:4, ROOT:4, ZERO:4, (TYPE:4, SIZE:4, DATA:SIZE, ZERO:0..15)*}
 * <p>
 * Die 16 Byte Kopfdaten bestehen aus den Komponenten: {@code MAGIC} = Dateikennung, {@code COUNT} = Anzahl belegter 16-Byte-Blöcke, {@code ROOT} = Referenz auf
 * den {@link #getRoot() Wurzelbereich} und {@code ZERO} = Füllwert zur Ausrichtung auf 16 Byte. Jeder typisierte Speicherbereich belegt stets ein Vielfaches
 * von 16 Byte und bestehen aus den Komponenten: {@code TYPE} = Typkennung, {@code SIZE} = Größe des Speicherbereichs, {@code DATA} Daten des Speicherbereichs
 * und {@code ZERO} = Füllwert.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class MappedBuffer2 extends MappedBuffer implements Emuable {

	/** Diese Klasse implementiert eine auf 16 Byte ausgerichteten Speicherbereich innerhalb eines {@link MappedBuffer2} mit {@link #hashCode()} und
	 * {@link #equals(Object)} zur Erkennung von Duplikaten. */
	private static final class Range {

		/** Dieses Feld speichert die Größe des Speicherbereichs div 16. */
		final int size;

		/** Dieses Feld speichert die Adresse des Speicherbereichs div 16. */
		final int addr;

		/** Dieses Feld speichert den Streuwert des Speicherbereichs. */
		final int hash;

		/** Dieses Feld speichert den Puffer, in dem der Speicherbereich liegt. */
		final MappedBuffer2 store;

		Range(final MappedBuffer2 store, final int addr, final int size) {
			this.store = store;
			this.addr = addr;
			this.size = size;
			int hash = Objects.hashInit();
			long index = addr * 16L;
			for (int count = size * 2; 0 < count; index += 8, count--) {
				final long value = store.getLong(index);
				hash = Objects.hashPush(hash, Integers.toIntH(value));
				hash = Objects.hashPush(hash, Integers.toIntL(value));
			}
			this.hash = hash;
		}

		@Override
		public int hashCode() {
			return this.hash;
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof MappedBuffer2.Range)) return false;
			final MappedBuffer2.Range that = (MappedBuffer2.Range)object;
			if (this.hash != that.hash) return false;
			final MappedBuffer2 store = this.store;
			if (store != that.store) return false;
			final int thisSize = this.size;
			if (thisSize != that.size) return false;
			final long thisAddr = this.addr * 16L, thatAddr = that.addr * 16L;
			if (thisAddr == thatAddr) return true;
			for (int count = thisSize * 2; 0 < count; count--) {
				if (store.getLong(thisAddr) != store.getLong(thatAddr)) return false;
			}
			return true;
		}

	}

	/** Dieses Feld speichert die Referenz auf den Wurzelspeicherbereich oder {@code 0}. */
	private int blockRoot;

	/** Dieses Feld speichert nach den erfolgreichen Aufrufen von {@link #openRegion(int, int)} bzw. {@link #closeRegion()} {@code true} bzw. {@code false}. */
	private boolean blockSetup;

	/** Dieses Feld speichert die Anzahl der 16 Byte-Blöcke und damit die Referenz des nächsten {@link #closeRegion() angefügten} Speicherbereichs. Diese ist
	 * mindestens {@code 2}. */
	private int blockCount;

	/** Dieses Feld speichert {@code true}, wenn Speicherbereiche bei {@link #closeRegion()} wiederverwendet werden sollen. . */
	private boolean reuseEnabled;

	/** Dieses Feld bildet von einer Zahlenfolge auf deren Referenz ab und wird zusammen mit {@link #reuseEnabled} in {@link #closeRegion()} eingesetzt. */
	private final HashMapOI<Object> reuseMapping;

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei. Wenn die Datei zum Schreiben angebunden wird und leer ist, werden ihre
	 * Kopfdaten initialisiert. Andernfals werden ihre Kopfdaten ausgelesen und geprüft.
	 *
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist. */
	public MappedBuffer2(final File file, final boolean readonly) throws IOException {
		super(file, readonly);
		if (!readonly) {
			this.reuseEnabled = true;
			this.reuseMapping = new HashMapOI<>();
			if (this.size() == 0) {
				this.grow(16);
				this.putInt(0, 0xABAD1DEA);
				this.putInt(4, 1);
				this.putLong(8, 0);
			}
		} else {
			this.reuseMapping = null;
		}
		if (this.size() < 16) throw new IOException();
		if (this.getInt(0) != 0xABAD1DEA) throw new IOException();
		this.blockCount = this.getInt(4);
		if (this.blockCount == 0) throw new IOException();
		this.blockRoot = this.getInt(8);
		if ((this.blockRoot < 0) || (this.blockRoot > this.blockCount)) throw new IOException();
	}

	/** Diese Methode gibt die Referenz auf den Wurzelspeicherbereich zurück. Dieser Speicherbereich sollte für das Inhaltsverzeichnis der in den übrigen
	 * Speicherbereichen abgelegten Datenstrukturen verwendet werden.
	 *
	 * @return Referenz auf den Wurzelspeicherbereich oder {@code 0}. */
	public final int getRoot() {
		return this.blockRoot;
	}

	/** Diese Methode setzt die {@link #getRoot() Referenz auf den Wurzelspeicherbereich}.
	 *
	 * @param value Referenz oder {@code 0}.
	 * @throws IllegalStateException Wenn die Datei nur zum {@link #isReadonly() Lesen} geöffnet wurde.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public final void setRoot(final int value) throws IllegalStateException, IllegalArgumentException {
		if (this.isReadonly()) throw new IllegalStateException();
		if ((value < 0) || (value > this.blockCount)) throw new IllegalArgumentException();
		this.putInt(8, this.blockRoot = value);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn bei den über {@link #closeRegion()} abgeschlossenen Speicherbereichen Duplikate elliminiert werden,
	 * d.h. bereits abgeschlossene Speicherbereiche wiederverwendet werden sollen. Zur Erkennung der Duplikate werden je Speicherbereich ca. 40 Byte
	 * Verwaltungsdaten benötigt.
	 *
	 * @return Aktivierung der Wiederverwendung. */
	public final boolean getReusing() {
		return this.reuseEnabled;
	}

	/** Diese Methode setzt die {@link #getReusing() Aktivierung der Wiederverwendung} von Speicherbereichen.
	 *
	 * @param value Aktivierung der Wiederverwendung. */
	public final void setReusing(final boolean value) {
		this.reuseEnabled = value;
	}

	/** Diese Methode gibt die Adresse der Kopfdaten des Speicherbereichs mit der gegebenen Referenz zurück. Die ersten vier Byte der Kopfdaten geben die
	 * Typkennung an, die nächsten vier Byte nennen die Größe des danach folgenden Speicherbereichs.
	 *
	 * @param ref Referenz.
	 * @return Adresse der Kopfdaten. */
	public final long getRegionAddr(final int ref) {
		return ref * 16L;
	}

	/** Diese Methode gibt den Speicherbereich zur gegebenen Referenz als {@link MMIArray Zahlenfolge} zurück.
	 *
	 * @see #openRegion(int, int)
	 * @see #closeRegion()
	 * @param ref Referenz.
	 * @return Zahlenfolge.
	 * @throws IllegalArgumentException Wenn die Referenz ungültig ist. */
	public final MMIArrayL getRegionArray(final int ref) throws IllegalArgumentException {
		final long addr = ref * 16L;
		return this.getArray(addr + 8, this.getInt(addr + 4), IAMArray.MODE_INT8);
	}

	/** Diese Methode reserviert einen Speicherbereich mit den gegebenen Merkmalen am Ende des Puffers und gibt die Adresse dieses Speicherbereichs zurück.
	 * Mehrfache Aufrufe dieser Methode aktualisieren die Merkmale des Speicherbereichs und überschreiben seine letzten 8 Byte mit Null. <b>Abschließend angefügt
	 * wird der Speicherbereich erst durch {@link #closeRegion()}.</b>
	 *
	 * @param dataType Typkennung des Speicherbereichs.
	 * @param dataSize Größe des Speicherbereichs (0..0x3FFFFFFF).
	 * @return Adresse des Speicherbereichs.
	 * @throws IllegalStateException Wenn die Datei nur zum {@link #isReadonly() Lesen} geöffnet wurde.
	 * @throws IllegalArgumentException Wenn die Größe ungültig ist. */
	public final long openRegion(final int dataType, final int dataSize) throws IllegalStateException, IllegalArgumentException {
		if (this.isReadonly()) throw new IllegalStateException();
		if ((dataSize & 0xC0000000) != 0) throw new IllegalArgumentException();
		try {
			final int dataRef = this.blockCount;
			if (dataRef < 0) throw new IllegalStateException();
			final long addr = dataRef * 16L, size = (addr + dataSize + 23) & -15L;
			this.grow(size);
			this.putLong(size - 8, 0);
			this.putInt(addr + 0, dataType);
			this.putInt(addr + 4, dataSize);
			this.blockSetup = true;
			return addr + 8;
		} catch (final Exception cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode schließt die Barbeitung des über {@link #openRegion(int, int)} reservierten Steicherbereichs ab, fügt diesen an das Dateiende an, hebt die
	 * Reservierung auf und gibt die Referenz auf den Steicherbereich zurück. Die Referenz entspricht der Adresse des Steicherbereichs geteilt durch 16. Wenn
	 * {@link #reuseEnabled}
	 *
	 * @return Referenz.
	 * @throws IllegalStateException Wenn aktuell kein Steicherbereich reserviert ist. */
	public final int closeRegion() throws IllegalStateException {
		if (!this.blockSetup) throw new IllegalStateException();
		final int result = this.blockCount, length = (this.getInt((result * 16L) + 4) + 23) / 16;
		if (this.reuseEnabled) {
			final Object key = new Range(this, result, length);
			final Integer value = this.reuseMapping.get(key);
			if (value != null) return value.intValue();
			this.reuseMapping.put(key, new Integer(result));
		}
		this.putInt(4, this.blockCount = result + length);
		this.blockSetup = false;
		return result;
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + this.reuseMapping.emu();
	}

}