package bee.creative.iam;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import bee.creative.bind.Getter;
import bee.creative.emu.EMU;
import bee.creative.io.MappedBuffer;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.mmi.MMIArray;
import bee.creative.mmi.MMIArrayL;

public abstract class IAMArrayBuilder extends BaseObject implements Getter<IAMArray, IAMArray> {

	static class FileBuilder extends IAMArrayBuilder {

		final MappedBuffer buffer;

		long address;

		FileBuilder(final File file, final long size) throws IOException {
			this.buffer = new MappedBuffer(file, size);
		}

		@Override
		public synchronized MMIArray get(final IAMArray item) {
			if (item instanceof MMIArray) {
				final MMIArrayL array = (MMIArrayL)item;
				if (array.buffer == this.buffer) return array;
			}
			try {
				final int mode = item.mode(), length = item.length();
				final long result = this.address;
				this.buffer.grow(this.address = result + (MMIArray.size(mode) * (long)length));
				this.buffer.putArray(result, item);
				return this.buffer.getArray(result, length, mode);
			} catch (final IOException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public File file() {
			return this.buffer.file();
		}

	}

	static class HeapBuilder extends IAMArrayBuilder {

		@Override
		public IAMArray get(final IAMArray item) {
			return item.compact();
		}

		@Override
		public File file() {
			return null;
		}

	}

	public static final IAMArrayBuilder EMPTY = new HeapBuilder();

	/** Diese Methode ist eine Abkürzung für {@link #from(File, long) IAMBuffer.from(file, 1 << 20)}. */
	public static FileBuilder from(final File file) throws IOException {
		return IAMArrayBuilder.from(file, 1 << 20);
	}

	/** Diese Methode gibt das zurück. Wenn der Dateiname {@code null} ist, wird eine {@link File#createTempFile(String, String) temporäre} Datei angelegt.
	 *
	 * @param file Dateiname oder {@code null}.
	 * @param size initiale Dateigröße.
	 * @return Zahlenfolgenpuffer. */
	public static FileBuilder from(final File file, final long size) throws IOException {
		return new FileBuilder(file != null ? file : File.createTempFile("ArrayBuilder", ".iam"), size);
	}

	public static void main(final String[] args) throws Exception {
		final FileBuilder f = IAMArrayBuilder.from(null);
		final IAMListingBuilder lb = new IAMListingBuilder();
		final Random r = new Random();
		final int cc = 1 << 20;
		for (int i = 0; i < cc; i++) {
			final byte[] b = new byte[20 + r.nextInt(60)];
			r.nextBytes(b);
			final IAMArray a = IAMArray.from(b);
			final IAMArray a2 = f.get(a);
			lb.put(a2);
		}
		System.out.println(cc - lb.itemCount());

		final IAMArray x = f.get(IAMArray.from(123, 456, 789));
		System.out.println(x);
		System.out.println(f.file());
		System.out.println("IAMFileBuffer " + EMU.from(f));
		System.out.println("IAMListingBuilder " + (EMU.from(lb) / 1024 / 1024f) + " MB");

	}

	/** Diese Methode gibt die Datei des intern genutzten Puffers oder {@code null} zurück. */
	public abstract File file();

}