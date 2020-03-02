package bee.creative.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import bee.creative.bind.Consumer;
import bee.creative.lang.Strings;

/** Diese Klasse ergänzt einen {@link MappedBuffer} um Methoden zur {@link #insertRegion(long) Reservierung} und {@link #deleteRegion(long) Freigabe} von
 * Speicherbereichen.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class MappedBuffer3 extends MappedBuffer {

	public static void main(final String[] args) throws Exception {

		final File file = new File("E:/DELETE-ME.mb3");
		file.delete();
		final MappedBuffer3 b = new MappedBuffer3(file, false);
		long a1 = b.insertRegion(16);
		final long a2 = b.insertRegion(32);
		b.deleteRegion(a1);
		b.pr();

		b.putLong(a2, 1234567891011121314L);
		b.putLong(a2+8, 999999999999999L);

		long a4 = b.updateRegion(a2, 8);
		b.pr();

		System.out.println(b.getLong(a4));
		System.out.println(b.getLong(a4+8));

	}

	private static boolean isAlingned(final long value) {
		return (value & 15) == 0;
	}

	private static long asAlingned(final long value) {
		return (value + 15) & -16;
	}

	/** Dieser Konstruktor initialisiert den Puffer zum Zugriff auf die gegebene Datei. Wenn die Datei zum Schreiben angebunden wird und leer ist, werden ihre
	 * Kopfdaten initialisiert. Andernfals werden ihre Kopfdaten geprüft.
	 *
	 * @param file Datei.
	 * @param readonly {@code true}, wenn die Datei nur mit Lesezugriff angebunden werden soll.
	 * @throws IOException Wenn die Anbindung nicht möglich ist.
	 * @throws IllegalArgumentException Wenn die Kopfdaten ungültig sind. */
	public MappedBuffer3(final File file, final boolean readonly) throws IOException, IllegalArgumentException {
		super(file, readonly);
		final long MAGIC = 0x474F4F44464F4F44L;
		final long size = this.size();
		if (!readonly && (size == 0)) {
			this.grow(48);
			this.putLong(0, new long[]{MAGIC, 0, 16, 16, 48, 0});
		} else {
			if (size < 48) throw new IllegalArgumentException();
			if (this.getLong(0) != MAGIC) throw new IllegalArgumentException();
		}
	}

	/** Diese Methode gibt die Größe des gegebenen Speicherbereichs zurück. */
	private final long getNodeSize(final long node) {
		return this.getLong(node - 8);
	}

	private final long getNodePrev(final long node) {
		return this.getLong(node + 0);
	}

	private final long getNodeNext(final long node) {
		return this.getLong(node + 8);
	}

	private final void setNodePrev(final long node, final long prev) {
		this.putLong(node + 0, prev);
	}

	private final void setNodeNext(final long node, final long next) {
		this.putLong(node + 8, next);
	}

	/** Diese Methode setzt die Größe des gegebenen unbenutzten Speicherbereichs. */
	private final void setNodeFreeSize(final long node, final long size) {
		this.putLong(node - 8, -size);
		this.putLong(node + size, -size);
	}

	/** Diese Methode setzt die Größe des gegebenen benutzten Speicherbereichs. */
	private final void setNodeUsedSize(final long node, final long size) {
		this.putLong(node - 8, size);
		this.putLong(node + size, size);
	}

	/** Diese Methode fügt den gegebenen neuen Knoten vor dem gegebenen Nachfolger ein.
	 *
	 * @param node neuer Knoten.
	 * @param next Nachfolger. */
	private final void insertNode(final long node, final long next) {
		final long prev = this.getNodePrev(next);
		this.setNodePrev(next, node);
		this.setNodeNext(prev, node);
		this.setNodePrev(node, prev);
		this.setNodeNext(node, next);
	}

	/** Diese Methode entfernt den gegebenen Knoten aus der doppelt verketteten Liste. Sein Vorgänger zeigt danach auf seinen Nachfolger und umgekehrt.
	 *
	 * @param node Knoten. */
	private final void deleteNode(final long node) {
		final long prev = this.getNodePrev(node), next = this.getNodeNext(node);
		this.setNodeNext(prev, next);
		this.setNodePrev(next, prev);
	}

	/** Diese Methode ersetzt den gegebenen alten Knoten durch den gegebenen neuen.
	 *
	 * @param oldNode alter Knoten.
	 * @param newNode neuer Knoten */
	private final void replaceNode(final long oldNode, final long newNode) {
		final long prev = this.getNodePrev(oldNode), next = this.getNodeNext(oldNode);
		this.setNodePrev(newNode, prev);
		this.setNodeNext(newNode, next);
		this.setNodeNext(prev, newNode);
		this.setNodePrev(next, newNode);
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
		if (address < 48) throw new IllegalArgumentException();
		this.putLong(8, address);
	}

	/** Diese Methode gibt die Größe des gegebenen Speicherbereichs zurück. Diese Größe ist stets ein Vielfaches von 16.
	 *
	 * @param address Adresse, an welcher der Speicherbereich beginnt.
	 * @return Größe des Speicherbereichs.
	 * @throws IllegalArgumentException Wenn die gegebene Adresse ungültig ist. */
	public long regionSize(final long address) throws IllegalArgumentException {
		synchronized (this) {
			return this.regionSizeImpl(address);
		}
	}

	private final long regionSizeImpl(final long node) throws IllegalArgumentException {
		if ((node < 48) || !MappedBuffer3.isAlingned(node)) throw new IllegalArgumentException();
		final long size = this.getNodeSize(node);
		if ((size < 0) || !MappedBuffer3.isAlingned(size)) throw new IllegalArgumentException();
		if (this.getLong(node + size) != size) throw new IllegalArgumentException();
		return size;
	}

	/** Diese Methode reserviert einen neuen Speicherbereich mit der gegebenen Größe und gibt die Adresse auf dessen Beginn zurück. Diese Adresse ist stets ein
	 * Vielfaches von 16. Der reservierte Speicherbereich kann dabei bis zu 31 Byte größer sein, als die gegebene Größe.
	 *
	 * @param size Mindestgröße des Speicherbereichs.
	 * @return Adresse, an welcher der Speicherbereich beginnt.
	 * @throws IOException Wenn die Datei nicht ausreichend vergrößert werden kann.
	 * @throws IllegalArgumentException Wenn die gegebene Größe ungültig ist. */
	public long insertRegion(final long size) throws IOException, IllegalArgumentException {
		if (size < 0) throw new IllegalArgumentException();
		if (size == 0) return 0;
		final long newSize = MappedBuffer3.asAlingned(size);
		if (newSize < 0) throw new IllegalArgumentException();
		synchronized (this) {
			return this.insertRegionImpl(newSize);
		}
	}

	private final long insertRegionImpl(long newSize) throws IOException {
		// wiederverwendbaren Speicherbereich suchen
		for (long node = this.getNodeNext(16); node != 16; node = this.getNodeNext(node)) {
			final long left = -this.getNodeSize(node) - newSize;
			if (left >= 0) { // verfügbarer Speicher reicht
				if (left < 32) { // zu wenig speicher zum Aufspalten
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
		// neuen Speicherbereich erzeugen
		return this.createRegionImpl(newSize);
	}

	private final long createRegionImpl(final long newSize) throws IOException {
		final long node = this.getLong(32), free = node + newSize + 16;
		this.grow(free);
		this.putLong(32, free);
		this.putLong(free - 8, 0);
		this.setNodeUsedSize(node, newSize);
		return node;
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

	private final void deleteRegionImpl(final long node, final long oldSize) {
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

	/** Diese Methode ändert die Größe des an der gegebenen Adresse beginnenden Speicherbereichs und gibt seine neue Adresse zurück. Wenn die Adresse {@code 0}
	 * ist, wird ein neuer Speicherbereich {@link #insertRegion(long) reserviert}. Wenn die Größe {@code 0} ist, wird der Speicherbereich zur Wiederverwendung
	 * {@link #deleteRegion(long) freigegeben}. Andernfals wird die Größe des Speicherbereichs angepasst, was zu seiner Verschiebung führen kann, wobei hier sien
	 * Inhalt an die neue Position kopiert wird.
	 *
	 * @param address Adresse, an welcher der Speicherbereich beginnt oder {@code 0}.
	 * @param size Größe des Speicherbereichs oder {@code 0}.
	 * @return neue Adresse.
	 * @throws IOException Wenn die Datei nicht ausreichend vergrößert werden kann.
	 * @throws IllegalArgumentException Wenn {@code address} bzw. {@code size} ungültig ist. */
	public long updateRegion(final long address, final long size) throws IOException, IllegalArgumentException {
		if (address == 0) return this.insertRegion(size);
		if (size < 0) throw new IllegalArgumentException();
		synchronized (this) {
			final long oldSize = this.regionSizeImpl(address);
			if (size == 0) {
				this.deleteRegionImpl(address, oldSize);
				return 0;
			} else {
				final long newSize = MappedBuffer3.asAlingned(size);
				if (newSize < 0) throw new IllegalArgumentException();
				return this.updateRegionImpl(address, oldSize, newSize);
			}
		}
	}

	private final long updateRegionImpl(final long oldNode, final long oldSize, final long newSize) throws IOException {
		final long putSize;
		if (oldSize <= newSize) {
			putSize = oldNode - 16;
		} else {
			final long free = this.getLong(32) + newSize + 16;
			this.grow(free); // Exception vorbeugen
			putSize = newSize - 16;
		}
		final long prev = this.getNodePrev(oldNode), next = this.getNodeNext(oldNode);
		this.deleteRegionImpl(oldNode, oldSize);
		final long newNode = this.insertRegionImpl(newSize);
		this.setNodePrev(newNode, prev);
		this.setNodeNext(newNode, next);
		if (putSize == 0) return newNode;
		this.put(newNode + 16, this, oldNode + 16, putSize);
		return newNode;
	}

	/** Diese Methode gibt die Liste der Größen der wiederverwendbaren Speicherbereiche zurück. Diese werden bei der {@link #insertRegion(long) Reservierung}
	 * eines neuen Speicherbereiches in der hier angegebenen Reihenfolge herangezogen, wobei der erste Speicherbereich mit ausreichender Größe wiederverwendet
	 * wird.
	 *
	 * @param limit maximale Anzahl der gelieferten Größen.
	 * @return Liste der Größen. */
	public synchronized ArrayList<Long> reuseSizes(int limit) {
		final ArrayList<Long> result = new ArrayList<>(16);
		long node = this.getNodeNext(16);
		while ((node != 16) && (limit > 0)) {
			final long size = -this.getNodeSize(node), next = this.getNodeNext(node);
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
	public synchronized ArrayList<Long> regionSizes(int limit) {
		final ArrayList<Long> result = new ArrayList<>(16);
		final long free = this.getLong(32);
		long node = 48;
		while (node < free && (limit > 0)) {
			final long size = this.getNodeSize(node), next = node + Math.abs(size) + 16;
			result.add(size);
			node = next;
			limit--;
		}
		return result;
	}

	private void pr() {
		System.out.append("REGION ").append(Strings.join(" ", regionSizes(50))).append(" FREE ").append(Strings.join(" ", reuseSizes(50))).println();
	}

}