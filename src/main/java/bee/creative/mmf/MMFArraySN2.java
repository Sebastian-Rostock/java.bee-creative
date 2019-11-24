package bee.creative.mmf;

import java.nio.ByteBuffer;
import bee.creative.iam.IAMArray;

class MMFArraySN2 extends MMFArraySN1 {

	protected MMFArraySN2(final int length, final ByteBuffer buffer, final int offset) {
		super(length, buffer, offset);
	}

	@Override
	public byte mode() {
		return IAMArray.MODE_INT16;
	}

	@Override
	public MMFArray asINT8() {
		return new MMFArraySN1(this.length * 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray asINT16() {
		return this;
	}

	@Override
	public MMFArray asINT32() {
		return new MMFArraySN4(this.length / 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray asUINT8() {
		return new MMFArrayUN1(this.length * 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray asUINT16() {
		return new MMFArrayUN2(this.length, this.buffer, this.offset);
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArraySR2(this.length, this.buffer, this.offset);
	}

	@Override
	protected int customGet(final int index) {
		return this.buffer.getShort(this.offset + (index * 2));
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArraySN2(length, this.buffer, this.offset + (offset * 2));
	}

}
