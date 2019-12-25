package bee.creative.mmi;

import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;
import bee.creative.io.MappedBuffer;

final class MMIArrayL4D extends MMIArrayL {

	MMIArrayL4D(final MappedBuffer buffer, final int length, final long address) throws IllegalArgumentException {
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
	protected void customGet(final int index, final short[] array, final int offset, final int length) {
		this.buffer.getShort(this.address + index, array, offset, length);
	}

	@Override
	protected MMIArray customSection(final int offset, final int length) {
		return new MMIArrayL4D(this.buffer, length, this.address + (offset * 2));
	}

}