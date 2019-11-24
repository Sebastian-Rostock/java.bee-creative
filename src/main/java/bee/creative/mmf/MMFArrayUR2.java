package bee.creative.mmf;

import java.nio.ByteBuffer;
import bee.creative.iam.IAMArray;

class MMFArrayUR2 extends MMFArraySR2 {

	protected MMFArrayUR2(final int length, final ByteBuffer buffer, final int offset) {
		super(length, buffer, offset);
	}

	@Override
	public byte mode() {
		return IAMArray.MODE_UINT16;
	}

	@Override
	public MMFArray asINT16() {
		return new MMFArraySR2(this.length, this.buffer, this.offset);
	}

	@Override
	public MMFArray asUINT16() {
		return this;
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArrayUN2(this.length, this.buffer, this.offset);
	}

	@Override
	protected int customGet(final int index) {
		return super.customGet(index) & 0xFFFF;
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArrayUR2(length, this.buffer, this.offset + (offset * 2));
	}

}
