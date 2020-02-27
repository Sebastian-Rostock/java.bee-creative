package bee.creative.io;

import java.io.File;
import java.io.IOException;

/** Diese Klasse ergänzt einen {@link MappedBuffer} um Methoden zur {@link #insertRegion(long) Reservierung} und {@link #deleteRegion(long) Freigabe} von
 * Speicherbereichen.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class MappedBuffer3 extends MappedBuffer {

	public static void main(final String[] args) throws Exception {
		final File file = new File("E:/DELETE-ME.mb3");
		//file.delete();
		final MappedBuffer3 b = new MappedBuffer3(file, false);
		  long a1 = b.insertRegion(16);
		b.pr();
		final long a2 = b.insertRegion(32);
		b.pr();
		long a3 = b.insertRegion(64);
		b.pr();

		a1 = b.updateRegion(a1, 40);
		b.pr();

		b.deleteRegion(a2);
		b.pr();
		b.deleteRegion(a1);
		b.pr();
		b.deleteRegion(a3);
		b.pr();
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

	private final long alignSize(final long size) {
		return (size + 15) & -16;
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
		final long prev = this.getNodePrev(node);
		final long next = this.getNodeNext(node);
		this.setNodeNext(prev, next);
		this.setNodePrev(next, prev);
	}

	/** Diese Methode ersetzt den gegebenen alten Knoten durch den gegebenen neuen.
	 *
	 * @param oldNode alter Knoten.
	 * @param newNode neuer Knoten */
	private final void replaceNode(final long oldNode, final long newNode) {
		final long prev = this.getNodePrev(oldNode);
		final long next = this.getNodeNext(oldNode);
		this.setNodePrev(newNode, prev);
		this.setNodeNext(newNode, next);
		this.setNodeNext(prev, newNode);
		this.setNodePrev(next, newNode);
	}

	/** Diese Methode gibt die Adresse des Wurzelspeicherbereichs zurück.<br>
	 * Dieser Speicherbereich soll als Inhaltsverzeichnis der in den übrigen Speicherbereichen abgelegten Datenstrukturen verwendet werden.
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

	/** Diese Methode gibt die Größe des gegebenen Speicherbereichs zurück.
	 *
	 * @param address Adresse, an welcher der Speicherbereich beginnt.
	 * @return Größe des Speicherbereichs.
	 * @throws IllegalArgumentException Wenn die gegebene Adresse ungültig ist. */
	public long getRegionSize(final long address) throws IllegalArgumentException {
		synchronized (this) {
			return this.getRegionSizeImpl(address);
		}
	}

	private final long getRegionSizeImpl(final long node) throws IllegalArgumentException {
		final long size = this.getNodeSize(node);
		if (size < 0) throw new IllegalArgumentException(); // Fehler bei einem freigegebenen Speicherbereich
		if ((size & 15) != 0) throw new IllegalArgumentException(); // Fehler bei einer nicht auf 16 ausgerichteten Größe
		if (this.getLong(node + size) != size) throw new IllegalArgumentException(); // Fehler bei abweichender sekundärer Größenangabe
		return size;
	}

	public long insertRegion(final long size) throws IOException, IllegalArgumentException {
		final long newSize = this.alignSize(size);
		if (newSize < 0) throw new IllegalArgumentException();
		if (newSize == 0) return 0;
		synchronized (this) {
			return this.insertRegionImpl(newSize);
		}
	}

	private final long insertRegionImpl(long nodeSize) throws IOException {
		// über die doppelt verkettete Liste der freien Knoten iterieren
		for (long node = this.getNodeNext(16); node != 16; node = this.getNodeNext(node)) {
			// die negativ gespeicherte Größe des leerer Knoten um die benötigte verringern
			final long left = -this.getNodeSize(node) - nodeSize;
			if (left >= 0) { // verfügbarer Speicher reicht
				if (left < 32) { // zu wenig speicher zum Aufspalten
					nodeSize += left;
					this.deleteNode(node);
				} else {
					final long free = node + nodeSize + 16;
					this.replaceNode(node, free);
					this.setNodeFreeSize(free, left - 16);
				}
				this.setNodeUsedSize(node, nodeSize);
				return node;
			}
		}
		final long node = this.getLong(32); // neuen speicher am Dateiende finden
		final long free = node + nodeSize + 16;
		this.grow(free);
		this.putLong(32, free);
		this.setNodeUsedSize(node, nodeSize);
		this.putLong(free - 8, 0);
		return node;
	}

	/** Diese Methode gibt den gegebenen Speicherbereich zur Wiederverwendung frei.
	 *
	 * @param address Adresse, an welcher der Speicherbereich beginnt.
	 * @throws IllegalArgumentException Wenn die gegebene Adresse ungültig ist. */
	public void deleteRegion(final long address) throws IllegalArgumentException {
		synchronized (this) {
			this.deleteRegionImpl(address, this.getRegionSizeImpl(address));
		}
	}

	private final void deleteRegionImpl(final long node, final long nodeSize) {
		final long prevSize = this.getLong(node - 16);
		final long nextSize = this.getLong(node + nodeSize + 8);
		if (prevSize < 0) {
			final long prev = node - -prevSize - 16;
			if (nextSize == 0) { // davor LEER, danach ENDE
				this.deleteNode(prev);
				this.putLong(prev - 8, 0);
				this.putLong(32, prev);
			} else if (nextSize < 0) { // davor LEER, danach LEER
				this.deleteNode(node + nodeSize + 16);
				this.setNodeFreeSize(prev, -prevSize + nodeSize + -nextSize + 32);
			} else { // davor LEER, danach VOLL
				this.setNodeFreeSize(prev, -prevSize + nodeSize + 16);
			}
		} else {
			if (nextSize == 0) { // davor VOLL, danach ENDE
				this.putLong(node - 8, 0);
				this.putLong(32, node);
			} else if (nextSize < 0) { // davor VOLL, danach LEER
				this.replaceNode(node + nodeSize + 16, node);
				this.setNodeFreeSize(node, nodeSize + -nextSize + 16);
			} else { // davor VOLL, danach VOLL
				this.setNodeFreeSize(node, nodeSize);
				this.insertNode(node, 16);
			}
		}
	}

	public long updateRegion(final long address, final long size) throws IOException, IllegalArgumentException {
		final long newSize = this.alignSize(size);
		if (newSize < 0) throw new IllegalArgumentException();
		synchronized (this) {
			if (address == 0) return newSize == 0 ? 0 : this.insertRegionImpl(newSize);
			final long oldSize = this.getRegionSizeImpl(address);
			if (oldSize == newSize) return address;
			if (newSize != 0) return this.updateRegionImpl(address, oldSize, newSize);
			this.deleteRegionImpl(address, oldSize);
			return 0;
		}
	}

	private long updateRegionImpl(final long nodeg, final long oldSize, final long newSize) throws IOException {
		// TODO mit beim verkleinern aufspalten, immer defragmentieren

		final long node = this.insertRegionImpl(newSize);
		this.put(node, this, nodeg, Math.min(oldSize, newSize));
		this.deleteRegionImpl(nodeg, oldSize);
		return node;
	}

	private void pr() {
		System.out.print(" ###");
		final long free = this.getLong(32);
		long node = 48;
		while (node < free) {
			final long size = this.getNodeSize(node);
			if (size < 0) {
				System.out.printf("-F%s", -size);
				node += -size + 16;
			} else {
				System.out.printf("-U%s", size);
				node += size + 16;
			}
		}
		System.out.print(" ###");
		node = this.getNodeNext(16);
		while (node != 16) {
			final long size = -this.getNodeSize(node);
			final long next = this.getNodeNext(node);
			System.out.printf("-N%s", size);
			node = next;
		}
		System.out.println();
	}

}