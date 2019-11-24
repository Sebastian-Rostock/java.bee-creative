package bee.creative.mmf;

import java.nio.ByteBuffer;
import bee.creative.iam.IAMArray;

class MMFArrayUN1 extends MMFArraySN1 {

	protected MMFArrayUN1(final int length, final ByteBuffer buffer, final int offset) {
		super(length, buffer, offset);
	}

	@Override
	public byte mode() {
		return IAMArray.MODE_UINT8;
	}

	@Override
	public MMFArray asINT8() {
		return new MMFArraySN1(this.length, this.buffer, this.offset);
	}

	@Override
	public MMFArray asUINT8() {
		return this;
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArrayUR1(this.length, this.buffer, this.offset);
	}

	@Override
	protected int customGet(final int index) {
		return super.customGet(index) & 0xFF;
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArrayUN1(length, this.buffer, this.offset + offset);
	}

}
