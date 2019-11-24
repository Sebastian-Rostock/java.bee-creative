package bee.creative.mmf;

import java.nio.ByteBuffer;
import bee.creative.iam.IAMArray;

class MMFArraySR2 extends MMFArraySR1 {

	protected MMFArraySR2(final int length, final ByteBuffer buffer, final int offset) {
		super(length, buffer, offset);
	}

	@Override
	public byte mode() {
		return IAMArray.MODE_INT16;
	}

	@Override
	public MMFArray asINT8() {
		return new MMFArraySR1(this.length * 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray asINT16() {
		return this;
	}

	@Override
	public MMFArray asINT32() {
		return new MMFArraySR4(this.length / 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray asUINT8() {
		return new MMFArrayUR1(this.length * 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray asUINT16() {
		return new MMFArrayUR2(this.length, this.buffer, this.offset);
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArraySN2(this.length, this.buffer, this.offset);
	}

	@Override
	protected int customGet(final int index) {
		return Short.reverseBytes(this.buffer.getShort(this.offset + (index * 2)));
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArraySR2(length, this.buffer, this.offset + (offset * 2));
	}

}
