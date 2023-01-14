package bee.creative.iam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.lang.Objects;
import bee.creative.util.HashMap2;

/** Diese Klasse implementiert einen modifizierbaren {@link IAMIndex}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMIndexBuilder extends IAMIndex {

	/** Diese Klasse implementiert den abstrakten Ausgabedatensatz eines {@link BasePool}. */
	static abstract class BaseItem implements Emuable {

		/** Dieses Feld speichert die Position, unter der dieses Objekt in {@link BasePool#items} verwaltet wird. */
		public final int index;

		public BaseItem(final int index) {
			this.index = index;
		}

		@Override
		public String toString() {
			return Integer.toString(this.index);
		}

	}

	/** Diese Klasse implementiert eine abstrakte {@link HashMap2}, über welche Nutzdaten in der Reihenfolge ihrer Erfassung gesammelt werden können.
	 *
	 * @param <GItem> Typ der Ausgabe. */
	static abstract class BasePool<GItem> extends HashMap2<IAMArray, GItem> {

		private static final long serialVersionUID = -8857584884331103416L;

		/** Dieses Feld speichert die gesammelten Ausgabedaten. */
		public final List<GItem> items = new ArrayList<>();

		/** Dieses Feld speichert Puffer zur Optimierung bzw. Auslagerung der Eingaben in {@link #customInstallKey(IAMArray)}. */
		public final IAMBuffer buffer;

		public BasePool(final IAMBuffer builder) {
			this.buffer = Objects.notNull(builder);
		}

		public GItem getItem(final IAMArray source) throws RuntimeException {
			return this.install(source);
		}

		/** Diese Methode nimmt einen neuen Ausgabedatensatz mit den gegebenen Eingabedaten in die Verwaltung auf und gibt diesen zurück.
		 *
		 * @param source Eingabedatensatz.
		 * @return Ausgabedatensatz.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public GItem putItem(final IAMArray source) throws NullPointerException {
			final int index = this.items.size();
			final GItem data = this.customInstallItem(index, this.buffer.get(source));
			this.items.add(index, data);
			return data;
		}

		@Override
		public void clear() {
			super.clear();
			this.items.clear();
		}

		@Override
		public long emu() {
			return super.emu() + EMU.from(this.items) + EMU.fromAll(this.items);
		}

		@Override
		protected IAMArray customInstallKey(final IAMArray key) {
			return this.buffer.get(key);
		}

		@Override
		protected GItem customInstallValue(final IAMArray key) {
			return this.putItem(key);
		}

		/** Diese Methode erzeugt einen neuen Ausgabedatensatz und gibt diesen zurück.
		 *
		 * @param index Position, unter welcher der Ausgabedatensatz in {@link #items} verwaltet wird.
		 * @param source Eingabedatensatz.
		 * @return Ausgabedatensatz. */
		protected abstract GItem customInstallItem(int index, IAMArray source);

	}

	static class ListingItem extends BaseItem {

		public IAMListing listing;

		public ListingItem(final int index, final IAMListing listing) {
			super(index);
			this.listing = listing;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.listing);
		}

	}

	static class ListingPool extends BasePool<ListingItem> {

		private static final long serialVersionUID = -8019050767276703804L;

		public ListingPool(final IAMBuffer buffer) {
			super(buffer);

		}

		@Override
		protected ListingItem customInstallItem(final int index, final IAMArray key) {
			try {
				return new ListingItem(index, IAMListing.from(key));
			} catch (final IOException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

	}

	static class MappingItem extends BaseItem {

		public IAMMapping mapping;

		public MappingItem(final int index, final IAMMapping mapping) {
			super(index);
			this.mapping = mapping;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.mapping);
		}

	}

	static class MappingPool extends BasePool<MappingItem> {

		private static final long serialVersionUID = 5897263117998581431L;

		public MappingPool(final IAMBuffer buffer) {
			super(buffer);

		}

		@Override
		protected MappingItem customInstallItem(final int index, final IAMArray source) {
			try {
				return new MappingItem(index, IAMMapping.from(source));
			} catch (final IOException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

	}

	/** Dieses Feld speichert die {@link IAMListing}. */
	protected final ListingPool listings;

	/** Dieses Feld speichert die {@link IAMMapping}. */
	protected final MappingPool mappings;

	/** Dieser Konstruktor initialisiert den Zahlenfolgenpuffer mit {@link IAMBuffer#EMPTY}. */
	public IAMIndexBuilder() {
		this(IAMBuffer.EMPTY);
	}

	/** Dieser Konstruktor initialisiert den Zahlenfolgenpuffer mit dem gegebenen. */
	public IAMIndexBuilder(final IAMBuffer buffer) {
		this.listings = new ListingPool(buffer);
		this.mappings = new MappingPool(buffer);
	}

	/** Diese Methode fügt eine Abbildung mit den gegebenen Daten hinzu und gibt die Position zurück, unter welcher diese verwaltet wird. Wenn bereits ein
	 * Abbildung mit den gleichen Daten über diese Methode hinzugefügt wurde, wird deren Position geliefert.
	 * <p>
	 * <u>Achtung:</u> Die gelieferte Position ist garantiert der gleiche, die im {@link IAMListingLoader} verwerden wird. Änderungen an den gegebenen Daten
	 * werden nicht auf die verwaltete Abbildung übertragen.
	 *
	 * @see #put(int, IAMMapping)
	 * @param mapping Daten der Abbildung.
	 * @return Position der Abbildung.
	 * @throws NullPointerException Wenn {@code mapping} {@code null} ist. */
	public int put(final IAMMapping mapping) throws NullPointerException {
		return this.mappings.getItem(IAMArray.from(mapping.toBytes())).index;
	}

	/** Diese Methode setzt die Daten der Abbildung an der gegebenen Position und gibt diese Position zurück. Wenn die Position negativ ist, werden eine neue
	 * Abbildung mit diesen Daten hinzugefügt und die Position geliefert, unter welcher diese verwaltet wird.
	 * <p>
	 * <u>Achtung:</u> Die gelieferte Position ist garantiert der gleiche, die im {@link IAMListingLoader} verwerden wird. Änderungen an den gegebenen Daten
	 * werden auf die verwaltete Abbildung übertragen.
	 *
	 * @see #put(IAMMapping)
	 * @param index Position der anzupassenden Abbildung oder {@code -1}.
	 * @param mapping Daten der Abbildung.
	 * @return Position der Abbildung.
	 * @throws NullPointerException Wenn {@code mapping} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
	public int put(final int index, final IAMMapping mapping) throws NullPointerException, IndexOutOfBoundsException {
		if (index < 0) {
			final MappingItem data = new MappingItem(this.mappingCount(), Objects.notNull(mapping));
			this.mappings.items.add(data.index, data);
			return data.index;
		} else {
			this.mappings.items.get(index).mapping = Objects.notNull(mapping);
			return index;
		}
	}

	/** Diese Methode fügt eine Auflistung mit den gegebenen Daten hinzu und gibt die Position zurück, unter welcher diese verwaltet wird. Wenn bereits ein
	 * Auflistung mit den gleichen Daten über diese Methode hinzugefügt wurde, wird deren Position geliefert.
	 * <p>
	 * <u>Achtung:</u> Die gelieferte Position ist garantiert der gleiche, die im {@link IAMListingLoader} verwerden wird. Änderungen an den gegebenen Daten
	 * werden nicht auf die verwaltete Auflistung übertragen.
	 *
	 * @see #put(int, IAMListing)
	 * @param listing Daten der Auflistung.
	 * @return Position der Auflistung.
	 * @throws NullPointerException Wenn {@code listing} {@code null} ist. */
	public int put(final IAMListing listing) throws NullPointerException {
		return this.listings.getItem(IAMArray.from(listing.toBytes())).index;
	}

	/** Diese Methode setzt die Daten der Auflistung an der gegebenen Position und gibt diese Position zurück. Wenn die Position negativ ist, werden eine neue
	 * Auflistung mit diesen Daten hinzugefügt und die Position geliefert, unter welcher diese verwaltet wird.
	 * <p>
	 * <u>Achtung:</u> Die gelieferte Position ist garantiert der gleiche, die im {@link IAMListingLoader} verwerden wird. Änderungen an den gegebenen Daten
	 * werden auf die verwaltete Auflistung übertragen.
	 *
	 * @see #put(IAMListing)
	 * @param index Position der anzupassenden Auflistung oder {@code -1}.
	 * @param listing Daten der Auflistung.
	 * @return Position der Auflistung.
	 * @throws NullPointerException Wenn {@code listing} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
	public int put(final int index, final IAMListing listing) throws NullPointerException, IndexOutOfBoundsException {
		if (index < 0) {
			final ListingItem data = new ListingItem(this.listingCount(), Objects.notNull(listing));
			this.listings.items.add(data.index, data);
			return data.index;
		} else {
			this.listings.items.get(index).listing = Objects.notNull(listing);
			return index;
		}
	}

	/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
	public void clear() {
		this.mappings.clear();
		this.listings.clear();
	}

	@Override
	public IAMListing listing(final int index) {
		if ((index < 0) || (index >= this.listingCount())) return IAMListing.EMPTY;
		return this.listings.items.get(index).listing;
	}

	@Override
	public int listingCount() {
		return this.listings.items.size();
	}

	@Override
	public IAMMapping mapping(final int index) {
		if ((index < 0) || (index >= this.mappingCount())) return IAMMapping.EMPTY;
		return this.mappings.items.get(index).mapping;
	}

	@Override
	public int mappingCount() {
		return this.mappings.items.size();
	}

}