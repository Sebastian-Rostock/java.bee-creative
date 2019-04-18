package bee.creative.mmf;

import java.nio.ByteBuffer;

class MMFArrayUN1 extends MMFArraySN1 {

	protected MMFArrayUN1(final int length, final ByteBuffer buffer, final int offset) {
		super(length, buffer, offset);
	}

	@Override
	public MMFArray toINT8() {
		return new MMFArraySN1(this.length, this.buffer, this.offset);
	}

	@Override
	public MMFArray toUINT8() {
		return this;
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArrayUR1(this.length, this.buffer, this.offset);
	}

	@Override
	protected int customGet(final int index) {
		return super.customGet(index) & 255;
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArrayUN1(length, this.buffer, this.offset + offset);
	}

}
