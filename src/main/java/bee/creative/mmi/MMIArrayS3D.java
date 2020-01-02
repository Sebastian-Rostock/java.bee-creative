package bee.creative.mmi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;

final class MMIArrayS3D extends MMIArrayS {

	MMIArrayS3D(final ByteBuffer buffer, final int length, final int address) {
		super(buffer, length, address);
		this.check1Impl();
	}

	@Override
	public int mode() {
		return IAMArray.MODE_UINT8;
	}

	@Override
	public ByteOrder order() {
		return this.orderDImpl();
	}

	@Override
	public MMIArray asRO() {
		return this.asUINT8R8Impl();
	}

	@Override
	public MMIArray asINT8() {
		return this.asINT8D8Impl();
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
		return this;
	}

	@Override
	public MMIArray asUINT16() {
		return this.asUINT16D8Impl();
	}

	@Override
	protected int customGet(final int index) {
		return this.customGetUINT8Impl(index);
	}

	@Override
	protected MMIArray customSection(final int offset, final int length) {
		return new MMIArrayS3D(this.buffer, length, this.address + offset);
	}

}