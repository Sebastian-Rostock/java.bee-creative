package bee.creative.array;

import java.util.Arrays;

/**
 * Diese Klasse implementiert ein {@link ShortArray} als {@link CompactArray}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class CompactShortArray extends CompactArray<short[], Short> implements ShortArray {

	/**
	 * Diese Klasse implementiert ein {@link ShortArray} als modifizierbare Sicht auf einen Teil eines
	 * {@link CompactShortArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactShortSubArray extends CompactSubArray<CompactShortArray, short[], Short> implements
		ShortArray {

		/**
		 * Dieser Konstrukteur initialisiert Besitzer und Indices.
		 * 
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder
		 *         {@code finalIndex > owner.size()} oder {@code startIndex > finalIndex}).
		 */
		public CompactShortSubArray(final CompactShortArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ShortArraySection section() {
			return new CompactShortSubArraySection(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public short get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void get(final int index, final short[] values) {
			this.get(index, ShortArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final short value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final short[] values) {
			this.set(index, ShortArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final short value) {
			this.add(this.size(), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final short[] values) {
			this.add(this.size(), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final short value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final short[] values) {
			this.add(this.size(), ShortArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ShortArray subArray(final int fromIndex, final int toIndex) {
			return (ShortArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link ShortArraySection} eines {@link CompactShortArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactShortArraySection extends ShortArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactShortArray owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactShortArraySection(final CompactShortArray owner) throws NullPointerException {
			if(owner == null) throw new NullPointerException();
			this.owner = owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.owner.size;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public short[] array() {
			return this.owner.array;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int startIndex() {
			return this.owner.from;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int finalIndex() {
			final CompactShortArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link ShortArraySection} eines {@link CompactShortSubArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactShortSubArraySection extends ShortArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactShortSubArray owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactShortSubArraySection(final CompactShortSubArray owner) throws NullPointerException {
			if(owner == null) throw new NullPointerException();
			this.owner = owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.owner.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public short[] array() {
			return this.owner.owner.array;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int startIndex() {
			return this.owner.startIndex;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int finalIndex() {
			return this.owner.finalIndex;
		}

	}

	/**
	 * Dieses Feld speichert das leere {@code short}-Array.
	 */
	protected static final short[] VOID = new short[0];

	/**
	 * Dieses Feld speichert das {@code short}-Array.
	 */
	protected short[] array;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected short[] getArray() {
		return this.array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setArray(final short[] array) {
		this.array = array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected short[] newArray(final int length) {
		if(length == 0) return CompactShortArray.VOID;
		return new short[length];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void clearArray(final short[] array, final int fromIndex, final int toIndex) {
		Arrays.fill(array, fromIndex, toIndex, (short)0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Short getValue(final int index) {
		return Short.valueOf(this.get(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(final int index, final Short value) {
		this.set(index, value.shortValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getLength(final short[] array) {
		return array.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int index, final short[] values) {
		this.get(index, ShortArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final short value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final short[] values) {
		this.set(index, ShortArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final short value) {
		this.add(this.size, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final short[] values) {
		this.add(this.size, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final short value) {
		this.exclusiveIndex(index);
		this.insert(index, 1);
		this.set(this.from + index, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final short[] values) {
		this.add(this.size, ShortArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShortArraySection section() {
		return new CompactShortArraySection(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShortArray subArray(final int startIndex, final int finalIndex) {
		return new CompactShortSubArray(this, startIndex, finalIndex);
	}

}
