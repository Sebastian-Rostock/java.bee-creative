package bee.creative.bex;

import java.nio.charset.Charset;
import bee.creative.iam.IAM.IAMBaseArray;
import bee.creative.iam.IAM.IAMEmptyArray;
import bee.creative.iam.IAMArray;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Objects;

public class BEX {

	public static abstract class BEXBaseFile implements BEXFile {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.root());
		}

	}

	public static abstract class BEXBaseNode implements BEXNode {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.key(), this.type(), this.index(), this.uri(), this.name(), this.value(), this.children().length(), this
				.attributes().length());
		}

	}

	public static abstract class BEXBaseList implements BEXList {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this);
		}

	}

	{}

	public static final Charset CHARSET = Charset.forName("UTF8");

	{}

	public static byte[] toBytes(final String value) {
		return value.getBytes(BEX.CHARSET);
	}

	public static byte[] toBytes(final IAMArray value) {
		final int length = value.length();
		final byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = (byte)value.get(i);
		}
		return result;
	}

	public static byte[] toBytes(final MMFArray value) {
		return value.toBytes();
	}

	public static IAMArray toArray(final byte[] value) {
		if (value.length == 0) return IAMEmptyArray.INSTANCE;
		return new IAMBaseArray() {

			@Override
			public int length() {
				return value.length;
			}

			@Override
			public int get(final int index) {
				if ((index < 0) || (index > value.length)) return 0;
				return value[index];
			}

		};
	}

	public static IAMArray toArray(final String value) {
		return BEX.toArray(BEX.toBytes(value));
	}

	public static String toString(final byte[] value) {
		return new String(value, BEX.CHARSET);
	}

	public static String toString(final IAMArray value) {
		return BEX.toString(BEX.toBytes(value));
	}

	public static String toString(final MMFArray value) {
		return BEX.toString(BEX.toBytes(value));
	}

}
