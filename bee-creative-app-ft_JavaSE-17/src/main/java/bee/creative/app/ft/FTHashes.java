package bee.creative.app.ft;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import bee.creative.csv.CSVReader;
import bee.creative.csv.CSVWriter;
import bee.creative.fem.FEMBinary;
import bee.creative.util.HashMap;

/** Diese Klasse implementiert einen persistierbaren Puffer für Streuwerte von Dateien.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class FTHashes extends FTStorable {

	/** Dieses Feld speichert den Dateinamen für {@link #persist()} und {@link #restore()}. */
	public static final String FILENAME = "hashes.csv.gz";

	public FTHashes() throws NoSuchAlgorithmException {
		this.digest = MessageDigest.getInstance("SHA-256");
	}

	/** Diese Methode liefert den Streuwert der gegebenen Datei. Dazu wird höchstens die gegebene Byteanzahl jeweils am Beginn und Ende der Datei herangezogen. */
	public String get(final String filePath, final long hashSize) {
		final var file = new File(filePath);
		final var fileSize = file.length();
		final var fileTime = file.lastModified();
		final var hashSize2 = Math.min(fileSize, hashSize * 2);
		var entry = this.cache.get(filePath);
		if ((entry != null) && (this.getFileSize(entry).longValue() == fileSize) && (this.getFileTime(entry).longValue() == fileTime)) {
			final var count = this.getHashCount(entry);
			for (var index = 0; index < count; index++) {
				if (this.getHashSize(entry, index).longValue() == hashSize2) return this.getHashCode(entry, index);
			}
			final var hashCode = this.getImpl(filePath, fileSize, hashSize);
			entry = this.setHashCount(entry, count + 1);
			this.setHashSize(entry, count, hashSize2);
			this.setHashCode(entry, count, hashCode);
			this.cache.put(filePath, entry);
			return hashCode;
		}
		final var hashCode = this.getImpl(filePath, fileSize, hashSize);
		entry = this.setHashCount(FTHashes.EMPTY, 1);
		this.setFileSize(entry, fileSize);
		this.setFileTime(entry, fileTime);
		this.setHashSize(entry, 0, hashSize2);
		this.setHashCode(entry, 0, hashCode);
		this.cache.put(filePath, entry);
		return hashCode;
	}

	@Override
	public void persist() {
		this.persist(FTHashes.FILENAME);
	}

	@Override
	public void persist(final CSVWriter writer) throws NullPointerException, IOException {
		writer.writeEntry((Object[])FTHashes.FILEHEAD);
		for (final var src: this.cache.entrySet()) {
			final Object[] entry = src.getValue();
			final var hashCount = this.getHashCount(entry);
			final var values = new Object[(hashCount * 2) + 3];
			values[0] = src.getKey();
			values[1] = this.getFileSize(entry);
			values[2] = this.getFileTime(entry);
			for (var i = 0; i < hashCount; i++) {
				values[(i * 2) + 3] = this.getHashSize(entry, i);
				values[(i * 2) + 4] = this.getHashCode(entry, i);
			}
			writer.writeEntry(values);
		}
	}

	@Override
	public void restore() {
		this.restore(FTHashes.FILENAME);
	}

	@Override
	public void restore(final CSVReader reader) throws NullPointerException, IllegalArgumentException, IOException {
		if (!Arrays.equals(FTHashes.FILEHEAD, reader.readEntry())) return;
		for (var src = reader.readEntry(); src != null; src = reader.readEntry()) {
			final var hashCount = (src.length - 3) / 2;
			final var entry = this.setHashCount(FTHashes.EMPTY, hashCount);
			this.setFileSize(entry, Long.valueOf(src[1]));
			this.setFileTime(entry, Long.valueOf(src[2]));
			for (var i = 0; i < hashCount; i++) {
				this.setHashSize(entry, i, Long.valueOf(src[(i * 2) + 3]));
				this.setHashCode(entry, i, src[(i * 2) + 4]);
			}
			this.cache.put(src[0], entry);
		}
	}

	private static final Object[] EMPTY = new Object[0];

	private static final String[] FILEHEAD = {"filePath", "fileSize", "fileTime", "hashSize#", "hashCode#"};

	/** Dieses Feld bildet von einem absoluten Dateipfad auf eine Liste der Form ({@link Long}, {@link String})+ besteht. */
	private final HashMap<String, Object[]> cache = new HashMap<>(1000);

	private final MessageDigest digest;

	private String getImpl(final String filePath, final long fileSize, final long hashSize) {
		try (var channel = FTData.openChannel(filePath)) {
			this.digest.reset();
			if (fileSize > (2 * hashSize)) {
				FTData.digestChannel(this.digest, channel, hashSize);
				channel.position(fileSize - hashSize);
				FTData.digestChannel(this.digest, channel, hashSize);
			} else {
				FTData.digestChannel(this.digest, channel, fileSize);
			}
			return FEMBinary.from(this.digest.digest()).toString(false);
		} catch (final Exception error) {
			error.printStackTrace();
			return null;
		}
	}

	private Long getFileSize(final Object[] entry) {
		return (Long)entry[0];
	}

	private void setFileSize(final Object[] entry, final Long value) {
		entry[0] = value;
	}

	private Long getFileTime(final Object[] entry) {
		return (Long)entry[1];
	}

	private void setFileTime(final Object[] entry, final Long value) {
		entry[1] = value;
	}

	private Long getHashSize(final Object[] entry, final int index) {
		return (Long)entry[(index * 2) + 2];
	}

	private void setHashSize(final Object[] entry, final int index, final Long value) {
		entry[(index * 2) + 2] = value;
	}

	private String getHashCode(final Object[] entry, final int index) {
		return (String)entry[(index * 2) + 3];
	}

	private void setHashCode(final Object[] entry, final int index, final String value) {
		entry[(index * 2) + 3] = value;
	}

	private int getHashCount(final Object[] entry) {
		return (entry.length - 2) / 2;
	}

	private Object[] setHashCount(final Object[] entry, final int value) {
		return Arrays.copyOf(entry, 2 + (value * 2));
	}

}
