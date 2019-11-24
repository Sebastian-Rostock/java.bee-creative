package bee.creative.mmf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class MMFArraySR1 extends MMFArraySN1 {

	protected MMFArraySR1(final int length, final ByteBuffer buffer, final int offset) {
		super(length, buffer, offset);
	}

	@Override
	public ByteOrder order() {
		return BufferArray.REVERSE_ORDER;
	}

	@Override
	public MMFArray asINT16() {
		return new MMFArraySR2(this.length / 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray asINT32() {
		return new MMFArraySR4(this.length / 4, this.buffer, this.offset);
	}

	@Override
	public MMFArray asUINT8() {
		return new MMFArrayUR1(this.length, this.buffer, this.offset);
	}

	@Override
	public MMFArray asUINT16() {
		return new MMFArrayUR2(this.length / 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArraySN1(this.length, this.buffer, this.offset);
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArraySR1(length, this.buffer, this.offset + offset);
	}

}
