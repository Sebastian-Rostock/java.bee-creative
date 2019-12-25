package bee.creative.mmi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;

final class MMIArrayS4D extends MMIArrayS {

	MMIArrayS4D(final ByteBuffer buffer, final int length, final int address) {
		super(buffer, length, address);
		this.check2Impl();
	}

	@Override
	public byte mode() {
		return IAMArray.MODE_UINT16;
	}

	@Override
	public ByteOrder order() {
		return this.orderDImpl();
	}

	@Override
	public MMIArray asRO() {
		return this.asUINT16R16Impl();
	}

	@Override
	public MMIArray asINT8() {
		return this.asINT8D16Impl();
	}

	@Override
	public MMIArray asINT16() {
		return this.asINT16D16Impl();
	}

	@Override
	public MMIArray asINT32() {
		return this.asINT32D16Impl();
	}

	@Override
	public MMIArray asUINT8() {
		return this.asUINT8D16Impl();
	}

	@Override
	public MMIArray asUINT16() {
		return this;
	}

	@Override
	protected int customGet(final int index) {
		return this.customGetUINT16DImpl(index);
	}

	@Override
	protected MMIArray customSection(final int offset, final int length) {
		return new MMIArrayS4D(this.buffer, length, this.address + (offset * 2));
	}

}