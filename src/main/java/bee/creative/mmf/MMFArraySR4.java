package bee.creative.mmf;

import java.nio.ByteBuffer;

class MMFArraySR4 extends MMFArraySR1 {

	protected MMFArraySR4(final int length, final ByteBuffer buffer, final int offset) {
		super(length, buffer, offset);
	}

	@Override
	public int mode() {
		return 4;
	}

	@Override
	public MMFArray toINT8() {
		return new MMFArraySR1(this.length * 4, this.buffer, this.offset);
	}

	@Override
	public MMFArray toINT16() {
		return new MMFArraySR2(this.length * 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray toINT32() {
		return this;
	}

	@Override
	public MMFArray toUINT8() {
		return new MMFArrayUR1(this.length * 4, this.buffer, this.offset);
	}

	@Override
	public MMFArray toUINT16() {
		return new MMFArrayUR2(this.length * 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArraySN4(this.length, this.buffer, this.offset);
	}

	@Override
	protected int customGet(final int index) {
		return Integer.reverseBytes(this.buffer.getInt(this.offset + (index * 4)));
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArraySR4(length, this.buffer, this.offset + (offset * 4));
	}

}
