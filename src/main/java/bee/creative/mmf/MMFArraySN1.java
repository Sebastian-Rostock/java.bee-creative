package bee.creative.mmf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class MMFArraySN1 extends MMFArray {

	/** Dieses Feld speichert den Speicherbereich. */
	protected ByteBuffer buffer;

	/** Dieses Feld speichert die Startposition. */
	protected int offset;

	protected MMFArraySN1(final int length, final ByteBuffer buffer, final int offset) {
		super(length);
		this.buffer = buffer;
		this.offset = offset;
	}

	@Override
	public int mode() {
		return 1;
	}

	@Override
	public ByteOrder order() {
		return MMFArray.NATIVE_ORDER;
	}

	@Override
	public MMFArray toINT8() {
		return this;
	}

	@Override
	public MMFArray toINT16() {
		return new MMFArraySN2(this.length / 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray toINT32() {
		return new MMFArraySN4(this.length / 4, this.buffer, this.offset);
	}

	@Override
	public MMFArray toUINT8() {
		return new MMFArrayUN1(this.length, this.buffer, this.offset);
	}

	@Override
	public MMFArray toUINT16() {
		return new MMFArrayUN2(this.length / 2, this.buffer, this.offset);
	}

	@Override
	public MMFArray withReverseOrder() {
		return new MMFArraySR1(this.length, this.buffer, this.offset);
	}

	@Override
	protected int customGet(final int index) {
		return this.buffer.get(this.offset + index);
	}

	@Override
	protected MMFArray customSection(final int offset, final int length) {
		return new MMFArraySN1(length, this.buffer, this.offset + offset);
	}

}