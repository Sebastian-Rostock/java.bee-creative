package bee.creative.mmf;

import java.nio.ByteBuffer;

class MMFArrayUR2 extends MMFArraySR2 {

	protected MMFArrayUR2(final int length, final ByteBuffer buffer, final int offset) {
		super(length, buffer, offset);
	}

	@Override
	public MMFArray toINT16() {
		return new MMFArraySR2(this.length, this.buffer, this.offset);
	}

	@Override
	public MMFArray toUINT16() {
		return this;
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArrayUN2(this.length, this.buffer, this.offset);
	}

	@Override
	protected int customGet(final int index) {
		return super.customGet(index) & 65535;
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArrayUR2(length, this.buffer, this.offset + (offset * 2));
	}

}
