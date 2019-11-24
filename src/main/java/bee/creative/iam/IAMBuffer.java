package bee.creative.iam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import bee.creative.bind.Getter;
import bee.creative.emu.EMU;
import bee.creative.iam.IAMArray.ByteArray;
import bee.creative.iam.IAMBuilder.IAMListingBuilder;
import bee.creative.io.MappedBuffer;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;

public abstract class IAMBuffer extends BaseObject implements Getter<IAMArray, IAMArray> {

	public static class IAMFileBuffer extends IAMBuffer {

		private final MappedBuffer buffer;

		private long address;

		public IAMFileBuffer(final File file) throws IOException {
			this.buffer = new MappedBuffer(file, 1 << 20);
		}

		@Override
		public synchronized IAMArray get(final IAMArray item) {
			final long pos = this.address;
			final int mod = item.mode(), len = item.length();
			if ((mod == IAMArray.MODE_INT8) || (mod == IAMArray.MODE_UINT8)) {
				this.seek(pos + len);
				this.buffer.put(pos, item.toBytes());
			} else if ((mod == IAMArray.MODE_INT16) || (mod == IAMArray.MODE_UINT16)) {
				this.seek(pos + (len * 2));
				this.buffer.putShort(pos, item.toShorts());
			} else {
				this.seek(pos + (len * 4));
				this.buffer.putInt(pos, item.toInts());
			}
			return this.buffer.getArray(pos, len, mod);
		}

		void seek(final long pos) {
			this.address = pos;
			if (this.address <= this.buffer.size()) return;
			try {
				this.buffer.resize(pos + (pos / 2));
			} catch (final IOException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

	}

	static class HeapBuffer extends IAMBuffer {

		@Override
		public IAMArray get(final IAMArray item) {
			return item.compact();
		}

	}

	public static IAMFileBuffer fromFile() throws IOException {
		return IAMBuffer.fromFile(File.createTempFile("iambuffer", ".iam"));
	}

	public static IAMFileBuffer fromFile(final File file) throws IOException {
		return new IAMFileBuffer(file);
	}

	public static IAMFileBuffer fromFile(final String file) throws IOException {
		return IAMBuffer.fromFile(new File(file));
	}

	public static void main(String[] args) throws Exception {
		IAMFileBuffer f = fromFile();
		IAMListingBuilder lb = new IAMListingBuilder();
		Random r = new Random();
		int cc = 1 << 20;
		for (int i = 0; i < cc; i++) {
			byte[] b = new byte[20+r.nextInt(60)];
			r.nextBytes(b);
			IAMArray a = IAMArray.from(b);
			IAMArray a2 = f.get(a);
			lb.put(a);
		}
		System.out.println(cc - lb.itemCount());

		IAMArray x = f.get(IAMArray.from(123, 456, 789));
		System.out.println(x);
		System.out.println(f.buffer.file());
		System.out.println("IAMFileBuffer " + EMU.from(f));
		System.out.println("IAMListingBuilder " + (EMU.from(lb)/1024/1024f) + " MB");

	}

}
