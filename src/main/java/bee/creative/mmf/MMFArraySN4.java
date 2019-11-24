package bee.creative.mmf;

import java.nio.ByteBuffer;
import bee.creative.iam.IAMArray;

class MMFArraySN4 extends MMFArraySN1 {

	protected MMFArraySN4(final int length, final ByteBuffer buffer, final int offset) {
		super(length, buffer, offset);
	}

	@Override
	public byte mode() {
		return IAMArray.MODE_INT32;
	}

	@Override
	public MMFArray asINT8() {
		return new MMFArraySN1(this.length * 4, this.buffer, this.offset);
	}

	@Override
	public MMFArray asINT16() {
		return new MMFArraySN2(this.length * 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray asINT32() {
		return this;
	}

	@Override
	public MMFArray asUINT8() {
		return new MMFArrayUN1(this.length * 4, this.buffer, this.offset);
	}

	@Override
	public MMFArray asUINT16() {
		return new MMFArrayUN2(this.length * 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArraySR4(this.length, this.buffer, this.offset);
	}

	@Override
	protected int customGet(final int index) {
		return this.buffer.getInt(this.offset + (index * 4));
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArraySN4(length, this.buffer, this.offset + (offset * 4));
	}

}
