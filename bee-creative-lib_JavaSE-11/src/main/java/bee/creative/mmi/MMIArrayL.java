package bee.creative.mmi;

import java.nio.ByteOrder;
import bee.creative.io.MappedBuffer;
import bee.creative.lang.Bytes;
import bee.creative.lang.Integers;

/** Diese Klasse implementiert ein {@link MMIArray} als Sicht auf einen {@link MappedBuffer}. */
public abstract class MMIArrayL extends MMIArray {

	/** Dieses Feld speichert den Quellpuffer. */
	public final MappedBuffer buffer;

	/** Dieses Feld speichert den Beginn des Speicherbereichs. */
	public final long address;

	MMIArrayL(final MappedBuffer buffer, final int length, final long address) throws IllegalArgumentException {
		super(length);
		this.buffer = buffer;
		this.address = address;
	}

	final void checkImpl(final int itemSize) throws IllegalArgumentException {
		final long remain = (this.buffer.size() - this.address) / itemSize;
		if ((remain < 0) || (remain < this.length)) throw new IllegalArgumentException();
	}

	final void check1Impl() throws IllegalArgumentException {
		this.checkImpl(1);
	}

	final void check2Impl() throws IllegalArgumentException {
		this.checkImpl(2);
	}

	final void check4Impl() throws IllegalArgumentException {
		this.checkImpl(4);
	}

	final ByteOrder orderRImpl() {
		return Bytes.reverseOrder(this.orderDImpl());
	}

	final ByteOrder orderDImpl() {
		return this.buffer.order();
	}

	final MMIArray asINT8D8Impl() {
		return new MMIArrayL0D(this.buffer, this.length, this.address);
	}

	final MMIArray asINT8D16Impl() {
		return new MMIArrayL0D(this.buffer, this.length * 2, this.address);
	}

	final MMIArray asINT8D32Impl() {
		return new MMIArrayL0D(this.buffer, this.length * 4, this.address);
	}

	final MMIArray asINT8R8Impl() {
		return new MMIArrayL0R(this.buffer, this.length, this.address);
	}

	final MMIArray asINT8R16Impl() {
		return new MMIArrayL0R(this.buffer, this.length * 2, this.address);
	}

	final MMIArray asINT8R32Impl() {
		return new MMIArrayL0R(this.buffer, this.length * 4, this.address);
	}

	final MMIArray asINT16D8Impl() {
		return new MMIArrayL1D(this.buffer, this.length / 2, this.address);
	}

	final MMIArray asINT16D16Impl() {
		return new MMIArrayL1D(this.buffer, this.length, this.address);
	}

	final MMIArray asINT16D32Impl() {
		return new MMIArrayL1D(this.buffer, this.length * 2, this.address);
	}

	final MMIArray asINT16R8Impl() {
		return new MMIArrayL1R(this.buffer, this.length / 2, this.address);
	}

	final MMIArray asINT16R16Impl() {
		return new MMIArrayL1R(this.buffer, this.length, this.address);
	}

	final MMIArray asINT16R32Impl() {
		return new MMIArrayL1R(this.buffer, this.length * 2, this.address);
	}

	final MMIArray asINT32D8Impl() {
		return new MMIArrayL2D(this.buffer, this.length / 4, this.address);
	}

	final MMIArray asINT32D16Impl() {
		return new MMIArrayL2D(this.buffer, this.length / 2, this.address);
	}

	final MMIArray asINT32D32Impl() {
		return new MMIArrayL2D(this.buffer, this.length, this.address);
	}

	final MMIArray asINT32R8Impl() {
		return new MMIArrayL2R(this.buffer, this.length / 4, this.address);
	}

	final MMIArray asINT32R16Impl() {
		return new MMIArrayL2R(this.buffer, this.length / 2, this.address);
	}

	final MMIArray asINT32R32Impl() {
		return new MMIArrayL2R(this.buffer, this.length, this.address);
	}

	final MMIArray asUINT8D8Impl() {
		return new MMIArrayL3D(this.buffer, this.length, this.address);
	}

	final MMIArray asUINT8D16Impl() {
		return new MMIArrayL3D(this.buffer, this.length * 2, this.address);
	}

	final MMIArray asUINT8D32Impl() {
		return new MMIArrayL3D(this.buffer, this.length * 4, this.address);
	}

	final MMIArray asUINT8R8Impl() {
		return new MMIArrayL3R(this.buffer, this.length, this.address);
	}

	final MMIArray asUINT8R16Impl() {
		return new MMIArrayL3R(this.buffer, this.length * 2, this.address);
	}

	final MMIArray asUINT8R32Impl() {
		return new MMIArrayL3R(this.buffer, this.length * 4, this.address);
	}

	final MMIArray asUINT16D8Impl() {
		return new MMIArrayL4D(this.buffer, this.length / 2, this.address);
	}

	final MMIArray asUINT16D16Impl() {
		return new MMIArrayL4D(this.buffer, this.length, this.address);
	}

	final MMIArray asUINT16D32Impl() {
		return new MMIArrayL4D(this.buffer, this.length * 2, this.address);
	}

	final MMIArray asUINT16R8Impl() {
		return new MMIArrayL4R(this.buffer, this.length / 2, this.address);
	}

	final MMIArray asUINT16R16Impl() {
		return new MMIArrayL4R(this.buffer, this.length, this.address);
	}

	final MMIArray asUINT16R32Impl() {
		return new MMIArrayL4R(this.buffer, this.length * 2, this.address);
	}

	final int customGetINT8Impl(final int index) {
		return this.buffer.get(this.address + index);
	}

	final int customGetINT16DImpl(final int index) {
		return this.buffer.getShort(this.address + (index * 2));
	}

	final int customGetINT16RImpl(final int index) {
		return (short)this.customGetUINT16RImpl(index);
	}

	final int customGetINT32DImpl(final int index) {
		return this.buffer.getInt(this.address + (index * 4));
	}

	final int customGetINT32RImpl(final int index) {
		return Integer.reverseBytes(this.customGetINT32DImpl(index));
	}

	final int customGetUINT8Impl(final int index) {
		return Integers.toByteL(this.customGetINT8Impl(index));
	}

	final int customGetUINT16DImpl(final int index) {
		return Integers.toShortL(this.customGetINT16DImpl(index));
	}

	final int customGetUINT16RImpl(final int index) {
		final int value = this.customGetINT16DImpl(index);
		return Integers.toShort(Integers.toByteL(value), Integers.toByteH(value));
	}

}