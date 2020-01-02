package bee.creative.mmi;

import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;
import bee.creative.io.MappedBuffer;

final class MMIArrayL1R extends MMIArrayL {

	MMIArrayL1R(final MappedBuffer buffer, final int length, final long address) throws IllegalArgumentException {
		super(buffer, length, address);
		this.check2Impl();
	}

	@Override
	public int mode() {
		return IAMArray.MODE_INT16;
	}

	@Override
	public ByteOrder order() {
		return this.orderRImpl();
	}

	@Override
	public MMIArray asRO() {
		return this.asINT16D16Impl();
	}

	@Override
	public MMIArray asINT8() {
		return this.asINT8R16Impl();
	}

	@Override
	public MMIArray asINT16() {
		return this;
	}

	@Override
	public MMIArray asINT32() {
		return this.asINT32R16Impl();
	}

	@Override
	public MMIArray asUINT8() {
		return this.asUINT8R16Impl();
	}

	@Override
	public MMIArray asUINT16() {
		return this.asUINT16R16Impl();
	}

	@Override
	protected int customGet(final int index) {
		return this.customGetINT16RImpl(index);
	}

	@Override
	protected MMIArray customSection(final int offset, final int length) {
		return new MMIArrayL1R(this.buffer, length, this.address + (offset * 2));
	}

}