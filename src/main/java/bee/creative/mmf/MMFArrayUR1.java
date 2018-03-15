package bee.creative.mmf;

import java.nio.ByteBuffer;

@SuppressWarnings ("javadoc")
class MMFArrayUR1 extends MMFArraySR1 {

	protected MMFArrayUR1(final int length, final ByteBuffer buffer, final int offset) {
		super(length, buffer, offset);
	}

	{}

	@Override
	public MMFArray toINT8() {
		return new MMFArraySR1(this.length, this.buffer, this.offset);
	}

	@Override
	public MMFArray toUINT8() {
		return this;
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArrayUN1(this.length, this.buffer, this.offset);
	}

	@Override
	protected int customGet(final int index) {
		return super.customGet(index) & 255;
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArrayUR1(length, this.buffer, this.offset + offset);
	}

}
