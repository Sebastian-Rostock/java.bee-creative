package bee.creative._dev_.sts;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;

class STSFileStore {

	/** Dieses Feld speichert die Dateikennung einer Datei mit Kantenlisten. */
	private static final long LIST_MAGIC = 0x5354535F4C495354L;

	private static final int LIST_ITEM_MIN = 32;

	private static final int LIST_ITEM_MAX = 63 * 1024 * 1024;

	/** Dieses Feld speichert die Länge einer Kantenliste. */
	private static final int LIST_ITEM_SIZE = 8;

	private static final int LIST_FIELD_MAGIC = 0;

	private static final int LIST_FIELD_INDEX = 8;

	private static final int LIST_FIELD_COUNT = 12;

	private static final int LIST_FIELD_LIMIT = 16;

	private static final int LIST_FIELD_VALUE = 20;

	private static final int LIST_LOCAL_BITS = 26;

	private static final int LIST_FILE_LOCAL_MASK = (1 << STSFileStore.LIST_LOCAL_BITS) - 1;

	File filePath;

	static class BaseFile {

		int fileSize; // für längenprüfung

		MappedByteBuffer fileData;// für datenzugriff

		File fileName; // für resize relevant

		public void resize(final int size) {

		}

	}

	File listFile(final int listFileIndex) {
		return new File(this.filePath, "LIST" + listFileIndex);
	}

	ByteBuffer[] nodeFileBuffers;

	static MappedByteBuffer getBuffer(final File file, final int size) {
		try (final RandomAccessFile fileData = new RandomAccessFile(file, "rw")) {
			try (final FileChannel fileChannel = fileData.getChannel()) {
				final MappedByteBuffer result = fileChannel.map(MapMode.READ_WRITE, 0, size);
				result.order(ByteOrder.nativeOrder());
				return result;
			}
		} catch (final IOException cause) {
			throw new IllegalStateException(cause);
		}
	}

	static int liststoreGetRegionOffset(final int itemIndex) {
		return (itemIndex * STSFileStore.LIST_ITEM_SIZE * 4) + STSFileStore.LIST_FIELD_VALUE;
	}

	static int liststoreGetBufferIndex(final int listIndex) {
		// index des puffers, in dem der knoten verwaltet wird
		return listIndex >>> STSFileStore.LIST_LOCAL_BITS;
	}

	static int liststoreGetRegionIndex(final int listIndex) {
		// index des eintrags im puffer
		return listIndex & STSFileStore.LIST_FILE_LOCAL_MASK;
	}

	ByteBuffer[] liststoreBuffers = {};

	/** Dieses Feld speichert den {@link #liststoreGetRegionIndex(int) lokalen Index} der nächsten erzeugten Kantenliste. */
	int listStoreNewIndex = STSFileStore.LIST_ITEM_MAX;

	int listStoreNewLimit = STSFileStore.LIST_ITEM_MAX;

	/** Dieses Feld speichert die Differenz zwischen dem Index der {@link #liststoreNewList() nächsten erzeugten Kantenliste} und ihrem {@link #listStoreNewIndex
	 * lokalem Index}. */
	int listStoreNewOffset;

	/** Dieses Feld speichert den Puffer, in den {@link #liststoreNewList() neue Listen} eingefügt werden. */
	ByteBuffer listStoreNewBuffer;

	/** Diese Methode gibt den Index einer neuen Knotenliste zurück. Dabei wird die {@link #listStoreNewBuffer aktuelle Datei} bei Bedarf vergrößert bzw. wird
	 * eine {@link #liststoreBuffers weitere Datei} erzeugt.
	 *
	 * @return Index einer neuen Kantenliste. */
	protected final int liststoreNewList() { // erzeugt neue leere liste
		final int newIndex = this.listStoreNewIndex, newLimit = this.listStoreNewLimit;
		if (newIndex < newLimit) {
			final ByteBuffer newBuffer = this.listStoreNewBuffer;
			newBuffer.putInt(STSFileStore.LIST_FIELD_COUNT, this.listStoreNewIndex = newIndex + 1);
			return newIndex + this.listStoreNewOffset;
		} else if (newLimit < STSFileStore.LIST_ITEM_MAX) {
			final int itemCount = Math.min(newLimit * 2, STSFileStore.LIST_ITEM_MAX);
			final int fileIndex = this.liststoreBuffers.length - 1;
			final ByteBuffer newBuffer = STSFileStore.getBuffer(this.listFile(fileIndex), STSFileStore.liststoreGetRegionOffset(itemCount));
			newBuffer.putInt(STSFileStore.LIST_FIELD_COUNT, this.listStoreNewIndex = newIndex + 1);
			newBuffer.putInt(STSFileStore.LIST_FIELD_LIMIT, this.listStoreNewLimit = itemCount);
			this.listStoreNewBuffer = this.liststoreBuffers[fileIndex] = newBuffer;
			return newIndex + this.listStoreNewOffset;
		} else {
			final int itemCount = STSFileStore.LIST_ITEM_MIN;
			final int fileIndex = this.liststoreBuffers.length;
			final ByteBuffer newBuffer = STSFileStore.getBuffer(this.listFile(fileIndex), STSFileStore.liststoreGetRegionOffset(itemCount));
			this.liststoreBuffers = Arrays.copyOf(this.liststoreBuffers, fileIndex + 1);
			newBuffer.putLong(STSFileStore.LIST_FIELD_MAGIC, STSFileStore.LIST_MAGIC);
			newBuffer.putInt(STSFileStore.LIST_FIELD_INDEX, fileIndex);
			newBuffer.putInt(STSFileStore.LIST_FIELD_COUNT, this.listStoreNewIndex = 1);
			newBuffer.putInt(STSFileStore.LIST_FIELD_LIMIT, this.listStoreNewLimit = itemCount);
			this.listStoreNewBuffer = this.liststoreBuffers[fileIndex] = newBuffer;
			return this.listStoreNewOffset = fileIndex << STSFileStore.LIST_LOCAL_BITS;
		}
	}

	protected final int liststoreSetupList() {
		final int listIndex = this.liststoreNewList();
		final int offset = STSFileStore.liststoreGetRegionOffset(STSFileStore.liststoreGetRegionIndex(listIndex));
		final ByteBuffer buffer = this.listStoreNewBuffer;
		buffer.putInt(offset, 0); // Anzahl der Einträge in der Liste
		buffer.putInt(offset + 4, -listIndex); // negativer Index der letzten Region der Liste
		return listIndex;
	}

	protected void liststoreExtendList(final int listIndex, final int value) {
		final int bufferIndex = STSFileStore.liststoreGetBufferIndex(listIndex);
		final int regionIndex = STSFileStore.liststoreGetRegionIndex(listIndex);
		final ByteBuffer headBuffer = this.liststoreBuffers[bufferIndex];
		final int headOffset = STSFileStore.liststoreGetRegionOffset(regionIndex);
		final int listSize = headBuffer.getInt(headOffset);
		if (listSize < 6) {
			// auffüllen

			headBuffer.putInt(headOffset, listSize + 1);
			headBuffer.putInt(headOffset + (listSize * 4) + 8, value);

		} else {
			final int used = (listSize - 6) & 7; // in letzter liste benutzte einträge, 0 bei voller liste
			if (used == 0) {
				// neue liste reservieren und zwei elemente eintragen

			} else {
				// auffüllen
			}
		}

	}

	void putNodeImpl() {
		// neuen knoten einfügen in node file und je rolle eine liste reservieren

	}

	void putNode(final int nodeIndex, final int edgeIndex, final int roleOffset) {
		final ByteBuffer nodeBuffer = this.nodeFileBuffers[STSFileStore.liststoreGetBufferIndex(nodeIndex)];
		final int fieldOffset = (16 * STSFileStore.liststoreGetRegionIndex(nodeIndex)) + roleOffset + 20;
		final int fieldValue = nodeBuffer.getInt(fieldOffset);
		if (fieldValue < 0) { // listenverweis

		} else if (fieldValue == 0) { // initial

		}
	}

	// neue liste einfügen, da einzelelement überschritten
	public int putList(final int item1, final int item2) {
		return 0; // index der erzeugten liste (fileindex und pageindex)
	}

}
