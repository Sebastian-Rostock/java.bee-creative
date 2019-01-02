package bee.creative.mmf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import bee.creative.iam.IAMArray;
import bee.creative.io.IO;

/** Diese Klasse implementiert ein {@link IAMArray}, dessen Zahlen durch einen {@link ByteBuffer} gegeben sind. Zur Interpretation dieses Speicherbereiches
 * können entsprechende Sichten über {@link #toINT8()}, {@link #toUINT8()}, {@link #toINT16()}, {@link #toUINT16()} bzw. {@link #toINT32()} erzeugte werden. Die
 * Bytereihenfolge kann ebenfalls eingestellt werden.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class MMFArray extends IAMArray {

	/** Dieses Feld speichert das leere {@link MMFArray}. */
	public static final MMFArray EMPTY = new MMFArraySN1(0, ByteBuffer.wrap(new byte[0]), 0);

	/** Dieses Feld speichert die native Bytereihenfolge. */
	protected static final ByteOrder NATIVE_ORDER = ByteOrder.nativeOrder();

	/** Dieses Feld speichert die zur nativen umgekehrte Bytereihenfolge. */
	protected static final ByteOrder REVERSE_ORDER = MMFArray.NATIVE_ORDER == ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;

	/** Diese Methode erzeugt aus dem gegebenen Objekt ein {@link MMFArray} und gibt dieses zurück. Wenn das Objekt ein {@link MMFArray} ist, wird dieses
	 * geliefert. Wenn es ein {@link ByteBuffer} ist, wird dazu eine {@link #toINT8() INT8}-Sicht erzeugt. Andernfalls wird das Objekt in einen {@link ByteBuffer}
	 * {@link IO#inputBufferFrom(Object) überführt}.
	 *
	 * @see IO#inputBufferFrom(Object)
	 * @param object Objekt.
	 * @return {@link MMFArray}.
	 * @throws IOException Wenn das {@link MMFArray} nicht erzeugt werden kann. */
	public static MMFArray from(final Object object) throws IOException {
		if (object instanceof MMFArray) return (MMFArray)object;
		final ByteBuffer buffer = IO.inputBufferFrom(object);
		final int length = Math.min(buffer.limit(), 1073741823);
		return buffer.order() == MMFArray.NATIVE_ORDER ? new MMFArraySN1(length, buffer, 0) : new MMFArraySR1(length, buffer, 0);
	}

	@SuppressWarnings ("javadoc")
	protected MMFArray(final int length) {
		super(length);
	}

	/** Diese Methode gibt die Größe jeder Zahl dieser Zahlenfolge zurück. Diese Größe ist {@code 1} für {@code INT8}- sowie {@code UINT8}-Zahlen, {@code 2} für
	 * {@code INT16}- sowie {@code UINT16}-Zahlen und {@code 4} für {@code INT32}-Zahlen.
	 *
	 * @return Größe jeder Zahl dieser Zahlenfolge (1, 2 oder 4). */
	@Override
	public abstract int mode();

	/** Diese Methode gibt die Bytereihenfolge zur Interpretation des internen {@link ByteBuffer} zurück.
	 *
	 * @see #withOrder(ByteOrder)
	 * @return Bytereihenfolge. */
	public abstract ByteOrder order();

	/** Diese Methode ist eine Abkürzung für {@link #withOrder(ByteOrder) this.withOrder(ByteOrder.nativeOrder())}.
	 *
	 * @see ByteOrder#nativeOrder() */
	public MMFArray toNE() {
		return this.withOrder(MMFArray.NATIVE_ORDER);
	}

	/** Diese Methode ist eine Abkürzung für {@link #withOrder(ByteOrder) this.withOrder(ByteOrder.BIG_ENDIAN)}.
	 *
	 * @see ByteOrder#BIG_ENDIAN */
	public MMFArray toBE() {
		return this.withOrder(ByteOrder.BIG_ENDIAN);
	}

	/** Diese Methode ist eine Abkürzung für {@link #withOrder(ByteOrder) this.withOrder(ByteOrder.LITTLE_ENDIAN)}.
	 *
	 * @see ByteOrder#LITTLE_ENDIAN */
	public MMFArray toLE() {
		return this.withOrder(ByteOrder.BIG_ENDIAN);
	}

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT8}-Zahlen ({@code byte}) interpretiert zurück.
	 *
	 * @return {@code byte}-{@link MMFArray}. */
	public abstract MMFArray toINT8();

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT16}-Zahlen ({@code short}) interpretiert zurück.
	 *
	 * @return {@code short}-{@link MMFArray}. */
	public abstract MMFArray toINT16();

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code INT32}-Zahlen ({@code int}) interpretiert zurück.
	 *
	 * @return {@code int}-{@link MMFArray}. */
	public abstract MMFArray toINT32();

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code UINT8}-Zahlen ({@code unsigned byte}) interpretiert zurück.
	 *
	 * @return {@code unsigned byte}-{@link MMFArray}. */
	public abstract MMFArray toUINT8();

	/** Diese Methode gibt den Speicherbereich dieser Zahlenfolge als Folge von {@code UINT16}-Zahlen ({@code unsigned short}) interpretiert zurück.
	 *
	 * @see ByteBuffer#getShort()
	 * @return {@code unsigned short}-{@link MMFArray}. */
	public abstract MMFArray toUINT16();

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
		if ((offset < 0) || (length <= 0) || ((offset + length) > this.length)) return this.customSection(0, 0);
		return this.customSection(offset, length);
	}

	/** {@inheritDoc} */
	@Override
	protected abstract MMFArray customSection(final int offset, final int length);

}