package bee.creative.mmf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;
import bee.creative.iam.IAMArray.BufferArray;
import bee.creative.io.IO;

/** Diese Klasse implementiert ein {@link IAMArray}, dessen Zahlen durch einen {@link ByteBuffer} gegeben sind. Zur Interpretation dieses Speicherbereiches
 * können entsprechende Sichten über {@link #asINT8()}, {@link #asUINT8()}, {@link #asINT16()}, {@link #asUINT16()} bzw. {@link #asINT32()} erzeugte werden. Die
 * Bytereihenfolge kann ebenfalls eingestellt werden.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class MMFArray extends BufferArray {

	/** Dieses Feld speichert das leere {@link MMFArray}. */
	public static final MMFArray EMPTY = new MMFArraySN1(0, ByteBuffer.wrap(new byte[0]), 0);

	/** Diese Methode erzeugt aus dem gegebenen Objekt ein {@link MMFArray} und gibt dieses zurück. Wenn das Objekt ein {@link MMFArray} ist, wird dieses
	 * geliefert. Andernfalls wird das Objekt in einen {@link ByteBuffer} {@link IO#inputBufferFrom(Object) überführt} und dazu ein {@link MMFArray}
	 * {@link #from(ByteBuffer) erzeugt}.
	 *
	 * @see #from(ByteBuffer)
	 * @see IO#inputBufferFrom(Object)
	 * @param object Objekt.
	 * @return {@link MMFArray}.
	 * @throws IOException Wenn das {@link MMFArray} nicht erzeugt werden kann. */
	public static MMFArray from(final Object object) throws IOException {
		if (object instanceof MMFArray) return (MMFArray)object;
		return MMFArray.from(IO.inputBufferFrom(object));
	}

	/** Diese Methode erzeugt auf dem gegebenen Speicherbereich eine {@link #asINT8() INT8}-{@link MMFArray}-Sicht und gibt dieses zurück.
	 *
	 * @param buffer Speicherbereich.
	 * @return {@link MMFArray}. */
	public static MMFArray from(final ByteBuffer buffer) {
		final int length = Math.min(buffer.limit(), 1073741823);
		return buffer.order() == BufferArray.NATIVE_ORDER ? new MMFArraySN1(length, buffer, 0) : new MMFArraySR1(length, buffer, 0);
	}

	@SuppressWarnings ("javadoc")
	protected MMFArray(final int length) {
		super(length);
	}

	/** Diese Methode gibt die Bytereihenfolge zur Interpretation des internen {@link ByteBuffer} zurück.
	 *
	 * @see #withOrder(ByteOrder)
	 * @return Bytereihenfolge. */
	public abstract ByteOrder order();

	/** Diese Methode ist eine Abkürzung für {@link #withOrder(ByteOrder) this.withOrder(ByteOrder.nativeOrder())}.
	 *
	 * @see ByteOrder#nativeOrder() */
	public MMFArray asNE() {
		return this.withOrder(BufferArray.NATIVE_ORDER);
	}

	/** Diese Methode ist eine Abkürzung für {@link #withOrder(ByteOrder) this.withOrder(ByteOrder.BIG_ENDIAN)}.
	 *
	 * @see ByteOrder#BIG_ENDIAN */
	public MMFArray asBE() {
		return this.withOrder(ByteOrder.BIG_ENDIAN);
	}

	/** Diese Methode ist eine Abkürzung für {@link #withOrder(ByteOrder) this.withOrder(ByteOrder.LITTLE_ENDIAN)}.
	 *
	 * @see ByteOrder#LITTLE_ENDIAN */
	public MMFArray asLE() {
		return this.withOrder(ByteOrder.BIG_ENDIAN);
	}

	/** {@inheritDoc}
	 *
	 * @return {@code byte}-{@link MMFArray}. */
	@Override
	public abstract MMFArray asINT8();

	/** {@inheritDoc}
	 *
	 * @return {@code short}-{@link MMFArray}. */
	@Override
	public abstract MMFArray asINT16();

	/** {@inheritDoc}
	 *
	 * @return {@code int}-{@link MMFArray}. */
	@Override
	public abstract MMFArray asINT32();

	/** {@inheritDoc}
	 *
	 * @return {@code unsigned byte}-{@link MMFArray}. */
	@Override
	public abstract MMFArray asUINT8();

	/** {@inheritDoc}
	 *
	 * @see ByteBuffer#getShort()
	 * @return {@code unsigned short}-{@link MMFArray}. */
	@Override
	public abstract MMFArray asUINT16();

	/** Diese Methode gibt diese Zahlenfolge mit der gegebenen Bytereihenfolge zurück.
	 *
	 * @see #order()
	 * @param order Bytereihenfolge.
	 * @return {@link MMFArray}. */
	public MMFArray withOrder(final ByteOrder order) {
		return this.order() == order ? this : this.withReverseOrder();
	}

	/** Diese Methode gibt diese Zahlenfolge mit umgekehrter Bytereihenfolge zurück.
	 *
	 * @see #order()
	 * @see #withOrder(ByteOrder)
	 * @return {@link MMFArray}. */
	public abstract MMFArray withReverseOrder();

	/** {@inheritDoc} */
	@Override
	public MMFArray section(final int offset) {
		return this.section(offset, this.length - offset);
	}

	/** {@inheritDoc} */
	@Override
	public MMFArray section(final int offset, final int length) {
		return (offset < 0) || (length <= 0) || ((offset + length) > this.length) ? this.customSection(0, 0) : this.customSection(offset, length);
	}

	/** {@inheritDoc} */
	@Override
	protected abstract MMFArray customSection(final int offset, final int length);

}