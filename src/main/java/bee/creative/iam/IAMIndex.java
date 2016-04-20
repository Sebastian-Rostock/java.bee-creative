package bee.creative.iam;

import java.util.AbstractList;
import java.util.List;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine abstrakte Zusammenstellung beliebig vieler Listen ({@link IAMListing}) und Abbildungen ({@link IAMMapping}).
 * <p>
 * Die Methoden {@link #mappings()} und {@link #listings()} delegieren an {@link #mapping(int)} und {@link #mappingCount()} bzw. {@link #listing(int)} und
 * {@link #listingCount()}.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMIndex {

	@SuppressWarnings ("javadoc")
	static final class ListingsList extends AbstractList<IAMListing> {

		final IAMIndex _owner_;

		ListingsList(final IAMIndex owner) {
			this._owner_ = owner;
		}

		{}

		@Override
		public IAMListing get(final int index) {
			if ((index < 0) || (index >= this._owner_.listingCount())) throw new IndexOutOfBoundsException();
			return this._owner_.listing(index);
		}

		@Override
		public int size() {
			return this._owner_.listingCount();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class MappingList extends AbstractList<IAMMapping> {

		final IAMIndex _owner_;

		MappingList(final IAMIndex owner) {
			this._owner_ = owner;
		}

		{}

		@Override
		public IAMMapping get(final int index) {
			if ((index < 0) || (index >= this._owner_.mappingCount())) throw new IndexOutOfBoundsException();
			return this._owner_.mapping(index);
		}

		@Override
		public int size() {
			return this._owner_.mappingCount();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyIndex extends IAMIndex {

		@Override
		public IAMMapping mapping(final int index) {
			return IAMMapping.EMPTY;
		}

		@Override
		public int mappingCount() {
			return 0;
		}

		@Override
		public IAMListing listing(final int index) {
			return IAMListing.EMPTY;
		}

		@Override
		public int listingCount() {
			return 0;
		}

	}

	{}

	/** Dieses Feld speichert den leeren {@link IAMIndex}. */
	public static final IAMIndex EMPTY = new EmptyIndex();

	{}

	/** Diese Methode gibt die {@code index}-te Abbildung zurück. Bei einem ungültigen {@code index} wird eine leere Abbildung geliefert.
	 * 
	 * @see #mappingCount()
	 * @param index Index.
	 * @return {@code index}-te Abbildung. */
	public abstract IAMMapping mapping(final int index);

	/** Diese Methode gibt die Anzahl der Abbildungen zurück ({@code 0..1073741823}).
	 * 
	 * @see #mapping(int)
	 * @return Anzahl der Abbildungen. */
	public abstract int mappingCount();

	/** Diese Methode gibt eine {@link List}-Sicht auf die Abbildungen zurück.
	 * 
	 * @see #mapping(int)
	 * @see #mappingCount()
	 * @return Abbildungen. */
	public final List<IAMMapping> mappings() {
		return new MappingList(this);
	}

	/** Diese Methode gibt die {@code index}-te Liste zurück. Bei einem ungültigen {@code index} wird eine leere Liste geliefert.
	 * 
	 * @see #listingCount()
	 * @param index Index.
	 * @return {@code index}-te Liste. */
	public abstract IAMListing listing(final int index);

	/** Diese Methode gibt die Anzahl der Listen zurück.
	 * 
	 * @see #listing(int)
	 * @return Anzahl der Listen. */
	public abstract int listingCount();

	/** Diese Methode gibt {@link List}-Sicht auf die Listen zurück.
	 * 
	 * @see #listing(int)
	 * @see #listingCount()
	 * @return Listen. */
	public final List<IAMListing> listings() {
		return new ListingsList(this);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return Objects.toInvokeString("IAMIndex", this.mappings(), this.listings());
	}

}
