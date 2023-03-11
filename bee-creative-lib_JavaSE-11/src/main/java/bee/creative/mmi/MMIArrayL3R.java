package bee.creative.mmi;

import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;
import bee.creative.io.MappedBuffer;

final class MMIArrayL3R extends MMIArrayL {

	MMIArrayL3R(final MappedBuffer buffer, final int length, final long address) throws IllegalArgumentException {
		super(buffer, length, address);
		this.check1Impl();
	}

	@Override
	public int mode() {
		return IAMArray.MODE_UINT8;
	}

	@Override
	public ByteOrder order() {
		return this.orderRImpl();
	}

	@Override
	public MMIArray asRO() {
		return this.asUINT8D8Impl();
	}

	@Override
	public MMIArray asINT8() {
		return this.asINT8R8Impl();
	}

	@Override
	public MMIArray asINT16() {
		return this.asINT16R8Impl();
	}

	@Override
	public MMIArray asINT32() {
		return this.asINT32R8Impl();
	}

	@Override
	public MMIArray asUINT8() {
		return this;
	}

	@Override
	public MMIArray asUINT16() {
		return this.asUINT16R8Impl();
	}

	@Override
	protected int customGet(final int index) {
		return this.customGetUINT8Impl(index);
	}

	@Override
	protected MMIArray customSection(final int offset, final int length) {
		return new MMIArrayL3R(this.buffer, length, this.address + offset);
	}

}