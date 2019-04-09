package bee.creative.iam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import bee.creative.iam.IAMLoader.IAMListingLoader;
import bee.creative.lang.Objects;
import bee.creative.util.Unique;

/** Diese Klasse implementiert Klassen und Methoden zur erzeugung der {code Integer Array Model} Datenstrukturen.
 *
 * @see IAMIndexBuilder
 * @see IAMListingBuilder
 * @see IAMMappingBuilder
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMBuilder {

	/** Diese Klasse implementiert den abstrakten Ausgabedatensatz eines {@link BasePool}. */
	static class BaseData {

		/** Dieses Feld speichert die Position, unter der dieses Objekt in {@link BasePool#targets} verwaltet wird. */
		public int index;

		@Override
		public String toString() {
			return Integer.toString(this.index);
		}

	}

	/** Diese Klasse implementiert ein abstraktes {@link Unique}, über welches Nutzdaten in der Reihenfolge ihrer Erfassung gesammelt werden können.
	 *
	 * @param <GSource> Typ der Eingabe.
	 * @param <GTarget> Typ der Ausgabe. */
	static abstract class BasePool<GSource, GTarget> extends Unique<GSource, GTarget> {

		/** Dieses Feld speichert die gesammelten Ausgabedaten. */
		public final List<GTarget> targets = new ArrayList<>();

		/** Diese Methode nimmt einen neuen Ausgabedatensatz mit den gegebenen Eingabedaten in die Verwaltung auf und gibt diesen zurück.
		 *
		 * @param source Eingabedatensatz.
		 * @return Ausgabedatensatz.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public GTarget put(final GSource source) throws NullPointerException {
			final int index = this.targets.size();
			final GTarget target = this.customBuild(index, Objects.notNull(source));
			this.targets.add(index, target);
			return target;
		}

		/** Diese Methode leert den Pool. */
		public void clear() {
			this.mapping.clear();
			this.targets.clear();
		}

		/** {@inheritDoc} */
		@Override
		protected GTarget customBuild(final GSource source) {
			return this.put(source);
		}

		/** Diese Methode erzeugt einen neuen Ausgabedatensatz und gibt diesen zurück.
		 *
		 * @param index Position, unter welcher der Ausgabedatensatz in {@link #targets} verwaltet wird.
		 * @param source Eingabedatensatz.
		 * @return Ausgabedatensatz. */
		protected abstract GTarget customBuild(int index, GSource source);

	}

	@SuppressWarnings ("javadoc")
	static abstract class BasePoolA<GTarget> extends BasePool<IAMArray, GTarget> {

	}

	@SuppressWarnings ("javadoc")
	static abstract class BasePoolB<GTarget> extends BasePool<byte[], GTarget> {

		@Override
		public int hash(final Object source) {
			return Objects.deepHash(source);
		}

		@Override
		public boolean equals(final Object source1, final Object source2) {
			return Objects.deepEquals(source1, source2);
		}

	}

	@SuppressWarnings ("javadoc")
	static class ItemData extends BaseData {

		public IAMArray data;

		public ItemData(final int index, final IAMArray data) {
			this.index = index;
			this.data = data;
		}

	}

	@SuppressWarnings ("javadoc")
	static class ItemPool extends BasePoolA<ItemData> {

		@Override
		protected ItemData customBuild(final int index, final IAMArray source) {
			return new ItemData(index, source);
		}

	}

	@SuppressWarnings ("javadoc")
	static class EntryData extends BaseData {

		public IAMArray key;

		public IAMArray value;

		public EntryData(final int index, final IAMArray key) {
			this.index = index;
			this.key = key;
			this.value = key;
		}

	}

	@SuppressWarnings ("javadoc")
	static class EntryPool extends BasePoolA<EntryData> {

		@Override
		protected EntryData customBuild(final int index, final IAMArray source) {
			return new EntryData(index, source);
		}

	}

	@SuppressWarnings ("javadoc")
	static class ListingData extends BaseData {

		public IAMListing listing;

		public ListingData(final int index, final IAMListing listing) {
			this.index = index;
			this.listing = listing;
		}

	}

	@SuppressWarnings ("javadoc")
	static class ListingPool extends BasePoolB<ListingData> {

		@Override
		protected ListingData customBuild(final int index, final byte[] source) {
			try {
				return new ListingData(index, IAMListing.from(source));
			} catch (final IOException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

	}

	@SuppressWarnings ("javadoc")
	static class MappingData extends BaseData {

		public IAMMapping mapping;

		public MappingData(final int index, final IAMMapping mapping) {
			this.index = index;
			this.mapping = mapping;
		}

	}

	@SuppressWarnings ("javadoc")
	static class MappingPool extends BasePoolB<MappingData> {

		@Override
		protected MappingData customBuild(final int index, final byte[] source) {
			try {
				return new MappingData(index, IAMMapping.from(source));
			} catch (final IOException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

	}

	/** Diese Klasse implementiert einen modifizierbaren {@link IAMIndex}. */
	public static class IAMIndexBuilder extends IAMIndex {

		/** Dieses Feld speichert die {@link IAMListing}. */
		protected final ListingPool listings = new ListingPool();

		/** Dieses Feld speichert die {@link IAMMapping}. */
		protected final MappingPool mappings = new MappingPool();

		/** Dieser Konstruktor initialisiert einen leeren {@link IAMIndexBuilder}. */
		public IAMIndexBuilder() {
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
			return this.mappings.get(mapping.toBytes()).index;
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
				final MappingData data = new MappingData(this.mappingCount(), Objects.notNull(mapping));
				this.mappings.targets.add(data.index, data);
				return data.index;
			} else {
				this.mappings.targets.get(index).mapping = Objects.notNull(mapping);
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
			return this.listings.get(listing.toBytes()).index;
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
				final ListingData data = new ListingData(this.listingCount(), Objects.notNull(listing));
				this.listings.targets.add(data.index, data);
				return data.index;
			} else {
				this.listings.targets.get(index).listing = Objects.notNull(listing);
				return index;
			}
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public void clear() {
			this.mappings.clear();
			this.listings.clear();
		}

		/** {@inheritDoc} */
		@Override
		public IAMListing listing(final int index) {
			if ((index < 0) || (index >= this.listingCount())) return IAMListing.EMPTY;
			return this.listings.targets.get(index).listing;
		}

		/** {@inheritDoc} */
		@Override
		public int listingCount() {
			return this.listings.targets.size();
		}

		/** {@inheritDoc} */
		@Override
		public IAMMapping mapping(final int index) {
			if ((index < 0) || (index >= this.mappingCount())) return IAMMapping.EMPTY;
			return this.mappings.targets.get(index).mapping;
		}

		/** {@inheritDoc} */
		@Override
		public int mappingCount() {
			return this.mappings.targets.size();
		}

	}

	/** Diese Klasse implementiert ein modifizierbares {@link IAMListing}. */
	public static class IAMListingBuilder extends IAMListing {

		/** Dieses Feld speichert die bisher gesammelten Elemente. */
		protected final ItemPool items = new ItemPool();

		/** Dieser Konstruktor initialisiert einen leeren {@link IAMListingBuilder}. */
		public IAMListingBuilder() {
		}

		/** Diese Methode fügt das Element mit den gegebenen Daten hinzu und gibt die Position zurück, unter welcher dieses verwaltet wird. Wenn bereits ein Element
		 * mit den gleichen Daten über diese Methode hinzugefügt wurde, wird dessen Position geliefert.
		 * <p>
		 * <u>Achtung:</u> Die gelieferte Position ist garantiert der gleiche, die im {@link IAMListingLoader} verwerden wird.
		 *
		 * @see #put(int, IAMArray)
		 * @param data Daten des Elements.
		 * @return Position des Elements.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
		public int put(final IAMArray data) throws NullPointerException {
			return this.items.get(data).index;
		}

		/** Diese Methode setzt die Daten des Elements an der gegebenen Position und gibt diese Position zurück. Wenn die Position negativ ist, werden ein neues
		 * Element mit diesen Daten hinzugefügt und die Position geliefert, unter welcher dieses verwaltet wird.
		 * <p>
		 * <u>Achtung:</u> Die gelieferte Position ist garantiert der gleiche, die im {@link IAMListingLoader} verwerden wird.
		 *
		 * @see #put(IAMArray)
		 * @param index Position des anzupassenden Elements oder {@code -1}.
		 * @param data Daten des Elements.
		 * @return Position des Elements.
		 * @throws NullPointerException Wenn {@code data} {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
		public int put(final int index, final IAMArray data) throws NullPointerException, IndexOutOfBoundsException {
			if (index < 0) return this.items.put(data).index;
			this.items.targets.get(index).data = Objects.notNull(data);
			return index;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public void clear() {
			this.items.clear();
		}

		/** {@inheritDoc} */
		@Override
		public IAMArray item(final int itemIndex) {
			final List<ItemData> datas = this.items.targets;
			if ((itemIndex < 0) || (itemIndex >= datas.size())) return IAMArray.EMPTY;
			return datas.get(itemIndex).data;
		}

		/** {@inheritDoc} */
		@Override
		public int itemCount() {
			return this.items.targets.size();
		}

	}

	/** Diese Klasse implementiert ein modifizierbares {@link IAMMapping}. */
	public static class IAMMappingBuilder extends IAMMapping {

		/** Dieses Feld speichert den Modus. */
		protected boolean mode = IAMMapping.MODE_HASHED;

		/** Dieses Feld speichert die Einträge. */
		protected final EntryPool entries = new EntryPool();

		/** Dieser Konstruktor initialisiert einen leeren {@link IAMMappingBuilder}. */
		public IAMMappingBuilder() {
		}

		/** Diese Methode fügt einen Eintrag mit dem gegebenen Schlüssel sowie dem gegebenen Wert hinzu. Wenn bereits ein Eintrag mit diesem Schlüssel existiert,
		 * wird dessen Wert ersetzt.
		 *
		 * @param key Schlüssel.
		 * @param value Wert.
		 * @throws NullPointerException Wenn {@code key} bzw. {@code value} {@code null} ist. */
		public void put(final IAMArray key, final IAMArray value) throws NullPointerException {
			Objects.notNull(value);
			this.entries.get(key).value = value;
		}

		/** {@inheritDoc} */
		@Override
		public boolean mode() {
			return this.mode;
		}

		/** Diese Methode setzt den Modus.
		 *
		 * @see #MODE_HASHED
		 * @see #MODE_SORTED
		 * @param mode Modus. */
		public void mode(final boolean mode) {
			this.mode = mode;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public void clear() {
			this.entries.clear();
		}

		/** {@inheritDoc} */
		@Override
		public IAMArray key(final int entryIndex) {
			final List<EntryData> datas = this.entries.targets;
			if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMArray.EMPTY;
			return datas.get(entryIndex).key;
		}

		/** {@inheritDoc} */
		@Override
		public IAMArray value(final int entryIndex) {
			final List<EntryData> datas = this.entries.targets;
			if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMArray.EMPTY;
			return datas.get(entryIndex).value;
		}

		/** {@inheritDoc} */
		@Override
		public int entryCount() {
			return this.entries.targets.size();
		}

		/** {@inheritDoc} */
		@Override
		public int find(final IAMArray key) throws NullPointerException {
			final EntryData result = this.entries.mapping().get(Objects.notNull(key));
			return result != null ? result.index : -1;
		}

	}

}
