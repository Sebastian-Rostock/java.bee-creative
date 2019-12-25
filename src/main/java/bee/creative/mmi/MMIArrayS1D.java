package bee.creative.mmi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;

final class MMIArrayS1D extends MMIArrayS {

	MMIArrayS1D(final ByteBuffer buffer, final int length, final int address) {
		super(buffer, length, address);
		this.check2Impl();
	}

	@Override
	public byte mode() {
		return IAMArray.MODE_INT16;
	}

	@Override
	public ByteOrder order() {
		return this.orderDImpl();
	}

	@Override
	public MMIArray asRO() {
		return this.asINT16R16Impl();
	}

	@Override
	public MMIArray asINT8() {
		return this.asINT8D16Impl();
	}

	@Override
	public MMIArray asINT16() {
		return this;
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
		return this.asUINT16D16Impl();
	}

	@Override
	protected int customGet(final int index) {
		return this.customGetINT16DImpl(index);
	}

	@Override
	protected MMIArray customSection(final int offset, final int length) {
		return new MMIArrayS1D(this.buffer, length, this.address + (offset * 2));
	}

}