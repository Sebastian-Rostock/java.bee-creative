package bee.creative.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import bee.creative.array.CompactLongArray;
import bee.creative.lang.Bytes;

/** Diese Klasse ergänzt einen {@link MappedBuffer} um Methoden zur {@link #insertRegion(long) Reservierung} und {@link #deleteRegion(long) Freigabe} von
 * Speicherbereichen. Die darüber angebundene Datei besitz dafür eine entsprechende Datenstruktur, deren Kopfdaten beim Öffnen erzeugt bzw. geprüft werden. Nur
 * wenn die Datei zum Schreiben angebunden wird und leer ist, werden ihre Kopfdaten initialisiert.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class MappedBuffer2 extends MappedBuffer {

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei in nativer Bytereihenfolge.
	 *
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist.
	 * @throws IllegalArgumentException Wenn die Kopfdaten ungültig sind. */
	public MappedBuffer2(final File file, final boolean readonly) throws IOException, IllegalArgumentException {
		this(file, readonly, Bytes.NATIVE_ORDER);
	}

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei.
	 *
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @param order Bytereihenfolge.
	 * @throws IOException Wenn die Anbindung nicht möglich ist.
	 * @throws IllegalArgumentException Wenn die Kopfdaten ungültig sind. */
	public MappedBuffer2(final File file, final boolean readonly, final ByteOrder order) throws IOException, IllegalArgumentException {
		super(file, readonly);
		this.order(order);
		final long MAGIC = 0x474F4F44464F4F44L;
		final long size = this.size();
		if (!readonly && (size == 0)) {
			this.grow(48);
			this.putLong(0, new long[]{MAGIC, 0, 16, 16, 48, 0});
		} else {
			if ((size < 48) || (this.getLong(0) != MAGIC)) throw new IllegalArgumentException();
		}
	}

	/** Diese Methode gibt die Adresse des Wurzelspeicherbereichs zurück, welcher als Inhaltsverzeichnis der in den übrigen Speicherbereichen abgelegten
	 * Datenstrukturen verwendet werden sollte.
	 *
	 * @return Adresse, an welcher der Wurzelspeicherbereich beginnt, oder {@code 0}. */
	public long getRoot() {
		return this.getLong(8);
	}

	/** Diese Methode setzt die {@link #getRoot() Adresse des Wurzelspeicherbereichs}.
	 *
	 * @param address Adresse oder {@code 0}.
	 * @throws IllegalArgumentException Wenn die Adresse negativ ist. */
	public void putRoot(final long address) throws IllegalArgumentException {
		if (address < 0) throw new IllegalArgumentException();
		this.putLong(8, address);
	}

	/** Diese Methode gibt die Größe des gegebenen Speicherbereichs zurück. Diese Größe ist stets ein Vielfaches von 16 Byte.
	 *
	 * @param address Adresse, an welcher der Speicherbereich beginnt.
	 * @return Größe des Speicherbereichs in Byte.
	 * @throws IllegalArgumentException Wenn die gegebene Adresse ungültig ist. */
	public long regionSize(final long address) throws IllegalArgumentException {
		synchronized (this) {
			return this.regionSizeImpl(address);
		}
	}

	/** Diese Methode {@link #insertRegion(long) reserviert} einen neuen Speicherbereich mit der gegebenen Größe, kopiert die Daten des an der gegebenen Adresse
	 * beginnenden Speicherbereichs dort hin und gibt die Adresse auf den Beginn des neuen Speicherbereichs zurück.
	 *
	 * @param address Adresse, an welcher der alte Speicherbereich beginnt.
	 * @param size Größe des neuen Speicherbereichs.
	 * @return Adresse, an welcher der neue Speicherbereich beginnt.
	 * @throws IllegalStateException Wenn die Datei nicht ausreichend vergrößert werden kann.
	 * @throws IllegalArgumentException Wenn {@code address} bzw. {@code size} ungültig ist. */
	public long cloneRegion(final long address, final long size) throws IllegalStateException, IllegalArgumentException {
		final long result, oldSize, newSize = MappedBuffer2.asAlignedSize(size);
		synchronized (this) {
			oldSize = this.regionSizeImpl(address);
			result = this.insertRegionImpl(newSize);
		}
		this.copy(result, address, Math.min(oldSize, newSize));
		return result;
	}

	/** Diese Methode reserviert einen neuen Speicherbereich mit der gegebenen Größe und gibt die Adresse auf dessen Beginn zurück. Diese Adresse ist stets ein
	 * Vielfaches von 16 Byte. Der reservierte Speicherbereich kann dazu bis zu 31 Byte größer sein, als die gegebene Größe.
	 *
	 * @param size Mindestgröße des Speicherbereichs in Byte.
	 * @return Adresse, an welcher der Speicherbereich beginnt.
	 * @throws IllegalStateException Wenn die Datei nicht ausreichend vergrößert werden kann.
	 * @throws IllegalArgumentException Wenn die gegebene Größe ungültig ist. */
	public long insertRegion(final long size) throws IllegalStateException, IllegalArgumentException {
		final long newSize = MappedBuffer2.asAlignedSize(size);
		synchronized (this) {
			return this.insertRegionImpl(newSize);
		}
	}

	/** Diese Methode gibt nur dann den an der gegebenen Adresse beginnenden Speicherbereich zur Wiederverwendung frei, wenn diese Adresse gültig und nicht
	 * {@code 0} ist.
	 *
	 * @param address Adresse, an welcher der Speicherbereich beginnt oder {@code 0}.
	 * @throws IllegalArgumentException Wenn {@code address} ungültig ist. */
	public void deleteRegion(final long address) throws IllegalArgumentException {
		if (address == 0) return;
		synchronized (this) {
			this.deleteRegionImpl(address, this.regionSizeImpl(address));
		}
	}

	/** Diese Methode ändert die Größe des an der gegebenen Adresse beginnenden Speicherbereichs und gibt seine neue Adresse zurück. Wenn die Adresse {@code 0}
	 * ist, wird ein neuer Speicherbereich {@link #insertRegion(long) reserviert}. Wenn die Größe {@code 0} ist, wird der Speicherbereich zur Wiederverwendung
	 * {@link #deleteRegion(long) freigegeben}. Andernfals wird die Größe des Speicherbereichs angepasst, was zu seiner Verschiebung führen kann, wobei hier sien
	 * Inhalt an die neue Position kopiert wird.
	 *
	 * @param address Adresse, an welcher der Speicherbereich beginnt oder {@code 0}.
	 * @param size Größe des Speicherbereichs in Byte oder {@code 0}.
	 * @return neue Adresse.
	 * @throws IllegalStateException Wenn die Datei nicht ausreichend vergrößert werden kann.
	 * @throws IllegalArgumentException Wenn {@code address} bzw. {@code size} ungültig ist. */
	public long updateRegion(final long address, final long size) throws IllegalStateException, IllegalArgumentException {
		final long reuse1, reuse2, result, oldSize, newSize = MappedBuffer2.asAlignedSize(size);
		final boolean reusing;
		synchronized (this) {
			if (address == 0) return this.insertRegionImpl(newSize);
			oldSize = this.regionSizeImpl(address);
			if (newSize == 0) {
				this.deleteRegionImpl(address, oldSize);
				return 0;
			}
			reuse1 = this.reuseRegionImpl(newSize);
			if (reuse1 == 0) {
				result = this.createRegionImpl(newSize);
				reusing = false;
			} else {
				result = reuse1;
				reusing = true;
			}
		}
		this.copy(result, address, Math.min(oldSize, newSize));
		synchronized (this) {
			this.deleteRegionImpl(address, oldSize);
			if (reusing) return result;
			reuse2 = this.reuseRegionImpl(newSize);
		}
		if (reuse2 == 0) return result;
		this.copy(reuse2, result, Math.min(oldSize, newSize));
		synchronized (this) {
			this.deleteRegionImpl(result, newSize);
		}
		return reuse2;
	}

	/** Diese Methode gibt die Liste der Größen der wiederverwendbaren Speicherbereiche zurück. Diese werden bei der {@link #insertRegion(long) Reservierung}
	 * eines neuen Speicherbereiches in der hier angegebenen Reihenfolge herangezogen, wobei der erste Speicherbereich mit ausreichender Größe wiederverwendet
	 * wird.
	 *
	 * @param limit maximale Anzahl der gelieferten Größen.
	 * @return Liste der Größen. */
	public synchronized CompactLongArray reuseSizes(int limit) {
		var node = this.getNodeNext(16);
		var result = new CompactLongArray(16, 0f);
		while ((node != 16) && (limit > 0)) {
			var size = -this.getNodeSize(node);
			var next = this.getNodeNext(node);
			result.add(size);
			node = next;
			limit--;
		}
		return result;
	}

	/** Diese Methode gibt die Liste der Größen aller Speicherbereiche zurück, wobei die Größe wiederverwendbarer Speicherbereiche negativ angegeben ist.
	 *
	 * @param limit maximale Anzahl der gelieferten Größen.
	 * @return Liste der Größen. */
	public synchronized CompactLongArray regionSizes(int limit) {
		var free = this.getLong(32);
		var result = new CompactLongArray(16, 0f);
		var node = 48L;
		while ((node < free) && (limit > 0)) {
			var size = this.getNodeSize(node);
			var next = node + Math.abs(size) + 16;
			result.add(size);
			node = next;
			limit--;
		}
		return result;
	}

	private static long asAlignedSize(final long size) throws IllegalArgumentException {
		final long result = (size + 15) & -16;
		if (result > 0) return result;
		if (size == 0) return 0;
		throw new IllegalArgumentException();
	}

	private static boolean isAlingnedValue(final long value) {
		return (value & 15) == 0;
	}

	/** Diese Methode gibt die Größe des gegebenen Speicherbereichs zurück. */
	private long getNodeSize(final long node) {
		return this.getLong(node - 8);
	}

	private long getNodePrev(final long node) {
		return this.getLong(node + 0);
	}

	private long getNodeNext(final long node) {
		return this.getLong(node + 8);
	}

	private void setNodePrev(final long node, final long prev) {
		this.putLong(node + 0, prev);
	}

	private void setNodeNext(final long node, final long next) {
		this.putLong(node + 8, next);
	}

	/** Diese Methode setzt die Größe des gegebenen unbenutzten Speicherbereichs. */
	private void setNodeFreeSize(final long node, final long size) {
		this.putLong(node - 8, -size);
		this.putLong(node + size, -size);
	}

	/** Diese Methode setzt die Größe des gegebenen benutzten Speicherbereichs. */
	private void setNodeUsedSize(final long node, final long size) {
		this.putLong(node - 8, size);
		this.putLong(node + size, size);
	}

	/** Diese Methode fügt den gegebenen neuen Knoten vor dem gegebenen Nachfolger ein.
	 *
	 * @param node neuer Knoten.
	 * @param next Nachfolger. */
	private void insertNode(final long node, final long next) {
		final long prev = this.getNodePrev(next);
		this.setNodePrev(next, node);
		this.setNodeNext(prev, node);
		this.setNodePrev(node, prev);
		this.setNodeNext(node, next);
	}

	/** Diese Methode entfernt den gegebenen Knoten aus der doppelt verketteten Liste. Sein Vorgänger zeigt danach auf seinen Nachfolger und umgekehrt.
	 *
	 * @param node Knoten. */
	private void deleteNode(final long node) {
		final long prev = this.getNodePrev(node), next = this.getNodeNext(node);
		this.setNodeNext(prev, next);
		this.setNodePrev(next, prev);
	}

	/** Diese Methode ersetzt den gegebenen alten Knoten durch den gegebenen neuen.
	 *
	 * @param oldNode alter Knoten.
	 * @param newNode neuer Knoten */
	private void replaceNode(final long oldNode, final long newNode) {
		final long prev = this.getNodePrev(oldNode), next = this.getNodeNext(oldNode);
		this.setNodePrev(newNode, prev);
		this.setNodeNext(newNode, next);
		this.setNodeNext(prev, newNode);
		this.setNodePrev(next, newNode);
	}

	private long regionSizeImpl(final long node) throws IllegalArgumentException {
		if ((node < 48) || !MappedBuffer2.isAlingnedValue(node)) throw new IllegalArgumentException();
		final long size = this.getNodeSize(node);
		if ((size < 0) || !MappedBuffer2.isAlingnedValue(size) || (this.getLong(node + size) != size)) throw new IllegalArgumentException();
		return size;
	}

	private long reuseRegionImpl(long newSize) {
		for (long node = this.getNodeNext(16); node != 16; node = this.getNodeNext(node)) {
			final long left = -this.getNodeSize(node) - newSize;
			if (left >= 0) {
				if (left < 32) {
					newSize += left;
					this.deleteNode(node);
				} else {
					final long free = node + newSize + 16;
					this.replaceNode(node, free);
					this.setNodeFreeSize(free, left - 16);
				}
				this.setNodeUsedSize(node, newSize);
				return node;
			}
		}
		return 0;
	}

	private long insertRegionImpl(final long newSize) throws IllegalStateException {
		final long result = this.reuseRegionImpl(newSize);
		return result != 0 ? result : this.createRegionImpl(newSize);
	}

	private long createRegionImpl(final long newSize) throws IllegalStateException {
		final long node = this.getLong(32), free = node + newSize + 16;
		this.grow(free);
		this.putLong(32, free);
		this.putLong(free - 8, 0);
		this.setNodeUsedSize(node, newSize);
		return node;
	}

	private void deleteRegionImpl(final long node, final long oldSize) {
		final long prevSize = this.getLong(node - 16), nextSize = this.getLong(node + oldSize + 8);
		if (prevSize < 0) {
			final long prev = node - -prevSize - 16;
			if (nextSize == 0) { // davor LEER, danach ENDE
				this.deleteNode(prev);
				this.putLong(prev - 8, 0);
				this.putLong(32, prev);
			} else if (nextSize < 0) { // davor LEER, danach LEER
				this.deleteNode(node + oldSize + 16);
				this.setNodeFreeSize(prev, -prevSize + oldSize + -nextSize + 32);
			} else { // davor LEER, danach VOLL
				this.setNodeFreeSize(prev, -prevSize + oldSize + 16);
			}
		} else {
			if (nextSize == 0) { // davor VOLL, danach ENDE
				this.putLong(node - 8, 0);
				this.putLong(32, node);
			} else if (nextSize < 0) { // davor VOLL, danach LEER
				this.replaceNode(node + oldSize + 16, node);
				this.setNodeFreeSize(node, oldSize + -nextSize + 16);
			} else { // davor VOLL, danach VOLL
				this.setNodeFreeSize(node, oldSize);
				this.insertNode(node, 16);
			}
		}
	}

}