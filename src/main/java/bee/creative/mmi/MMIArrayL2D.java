package bee.creative.mmi;

import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;
import bee.creative.io.MappedBuffer;

final class MMIArrayL2D extends MMIArrayL {

	MMIArrayL2D(final MappedBuffer buffer, final int length, final long address) throws IllegalArgumentException {
		super(buffer, length, address);
		this.check4Impl();
	}

	@Override
	public byte mode() {
		return IAMArray.MODE_INT32;
	}

	@Override
	public ByteOrder order() {
		return this.orderDImpl();
	}

	@Override
	public MMIArray asRO() {
		return this.asINT32R32Impl();
	}

	@Override
	public MMIArray asINT8() {
		return this.asINT8D32Impl();
	}

	@Override
	public MMIArray asINT16() {
		return this.asINT16D32Impl();
	}

	@Override
	public MMIArray asINT32() {
		return this;
	}

	@Override
	public MMIArray asUINT8() {
		return this.asUINT8D32Impl();
	}

	@Override
	public MMIArray asUINT16() {
		return this.asUINT16D32Impl();
	}

	@Override
	protected int customGet(final int index) {
		return this.customGetINT32DImpl(index);
	}

	@Override
	protected void customGet(final int index, final int[] array, final int offset, final int length) {
		this.buffer.getInt(this.address + index, array, offset, length);
	}

	@Override
	protected MMIArray customSection(final int offset, final int length) {
		return new MMIArrayL2D(this.buffer, length, this.address + (offset * 4));
	}

}