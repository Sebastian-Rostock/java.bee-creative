package bee.creative.mmi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;

final class MMIArrayS0D extends MMIArrayS {

	MMIArrayS0D(final ByteBuffer buffer, final int length, final int address) {
		super(buffer, length, address);
		this.check1Impl();
	}

	@Override
	public byte mode() {
		return IAMArray.MODE_INT8;
	}

	@Override
	public ByteOrder order() {
		return this.orderDImpl();
	}

	@Override
	public MMIArray asRO() {
		return this.asINT8R8Impl();
	}

	@Override
	public MMIArray asINT8() {
		return this;
	}

	@Override
	public MMIArray asINT16() {
		return this.asINT16D8Impl();
	}

	@Override
	public MMIArray asINT32() {
		return this.asINT32D8Impl();
	}

	@Override
	public MMIArray asUINT8() {
		return this.asUINT8D8Impl();
	}

	@Override
	public MMIArray asUINT16() {
		return this.asUINT16D8Impl();
	}

	@Override
	protected int customGet(final int index) {
		return this.customGetINT8Impl(index);
	}

	@Override
	protected MMIArray customSection(final int offset, final int length) {
		return new MMIArrayS0D(this.buffer, length, this.address + offset);
	}

}