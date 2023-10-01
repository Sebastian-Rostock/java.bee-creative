package bee.creative.app.ft;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import bee.creative.lang.Objects;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class AppData {

	public final String filePath;

	public final long testSize;

	public AppData(final String filePath, final long testSize) {
		this.filePath = filePath;
		this.testSize = testSize;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof AppData)) return false;
		final var that = (AppData)object;
		if (Objects.equals(this.filePath, that.filePath) || (this.testSize != that.testSize)) return true;
		try (var thisChannel = AppData.openChannel(this.filePath)) {
			try (var thatChannel = AppData.openChannel(that.filePath)) {
				final var testSize = this.testSize;
				final var fileSize = thisChannel.size();
				if (fileSize <= (2 * testSize)) return AppData.equalsChannel(thisChannel, thatChannel, fileSize);
				if (!AppData.equalsChannel(thisChannel, thatChannel, testSize)) return false;
				return AppData.equalsChannel(thisChannel.position(fileSize - testSize), thatChannel.position(fileSize - testSize), testSize);
			}
		} catch (final Exception error) {
			error.printStackTrace();
			return false;
		}
	}

	@Override
	public String toString() {
		return this.filePath;
	}

	static FileChannel openChannel(final String filepath) throws IOException {
		return FileChannel.open(new File(filepath).toPath(), StandardOpenOption.READ);
	}

	/** Diese Methode überträgt die gegebene Anzahl an Byte vom gegebenen {@link FileChannel} in das gegebene {@link MessageDigest}. */
	static void digestChannel(final MessageDigest target, final FileChannel source, final long length) throws IOException {
		final var bufSize = AppData.BUFFER_SIZE;
		final var hashBuffer = AppData.BUFFER_HASH;
		for (var remSize = length; remSize > 0; remSize -= bufSize) {
			final var done = AppData.readChannel(source, hashBuffer.limit((int)Math.min(remSize, bufSize)).position(0));
			target.update(hashBuffer.limit(hashBuffer.position()).position(0));
			if (done) return;
		}
	}

	/** Diese Methode liefert nur dann {@code true}, wenn die gegebenen {@link FileChannel} ab ihrer jeweiligen Position die gleichen Daten enthalten, wobei hier
	 * höchstens die gegebene Anzahl an Byte herangezogen wird. */
	static boolean equalsChannel(final FileChannel thisChannel, final FileChannel thatChannel, final long length) throws IOException {
		final var bufSize = AppData.BUFFER_SIZE;
		final var bufThis = AppData.BUFFER_THIS;
		final var bufThat = AppData.BUFFER_THAT;
		for (long remSize = length; remSize > 0; remSize -= bufSize) {
			final int remLimit = (int)Math.min(remSize, bufSize);
			final var thisLast = AppData.readChannel(thisChannel, bufThis.limit(remLimit).position(0));
			final var thatLast = AppData.readChannel(thatChannel, bufThat.limit(remLimit).position(0));
			if ((thisLast != thatLast) || !bufThis.limit(bufThis.position()).position(0).equals(bufThat.limit(bufThat.position()).position(0))) return false;
		}
		return true;
	}

	/** Diese Methode füllt den gegebenen {@link ByteBuffer} mit den Daten aus dem gegebenen {@link FileChannel} und liefert nur dann {@code true}, wenn dabei das
	 * Ende des {@link FileChannel} erreicht wurde. */
	private static boolean readChannel(final FileChannel source, final ByteBuffer target) throws IOException {
		while (target.remaining() != 0) {
			if (source.read(target) < 0) return true;
		}
		return source.size() == source.position();
	}

	private static final int BUFFER_SIZE = 1024 * 1024 * 10;

	private static final ByteBuffer BUFFER_THIS = ByteBuffer.allocateDirect(AppData.BUFFER_SIZE);

	private static final ByteBuffer BUFFER_THAT = ByteBuffer.allocateDirect(AppData.BUFFER_SIZE);

	private static final ByteBuffer BUFFER_HASH = ByteBuffer.allocateDirect(AppData.BUFFER_SIZE);

}