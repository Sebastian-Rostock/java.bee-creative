package bee.creative.iam;

import java.util.ArrayList;
import java.util.List;
import bee.creative.iam.IAMLoader.IAMListingLoader;
import bee.creative.util.Objects;
import bee.creative.util.Unique;

/** Diese Klasse implementiert Klassen und Methoden zur erzeugung der {code Integer Array Model} Datenstrukturen.
 *
 * @see IAMIndexBuilder
 * @see IAMListingBuilder
 * @see IAMMappingBuilder
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMBuilder {

	/** Diese Klasse implementiert ein Element einer {@link UniqueItemPool} eines {@link IAMListingBuilder}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static class ItemData {

		/** Dieses Feld speichert den Index, unter dem dieses Objekt in {@link UniquePool#datas} verwaltet wird. */
		public int index;

		/** Dieses Feld speichert die Zahlenfolge des Elements. */
		public IAMArray item;

	}

	/** Diese Klasse implementiert ein Element einer {@link UniqueEntryPool} eines {@link IAMMappingBuilder}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static class EntryData {

		/** Dieses Feld speichert den Index, unter dem dieses Objekt in {@link UniquePool#datas} verwaltet wird. */
		public int index;

		/** Dieses Feld speichert die Zahlenfolge des Schlüssels. */
		public IAMArray key;

		/** Dieses Feld speichert die Zahlenfolge des Werts. */
		public IAMArray value;

	}

	/** Diese Klasse implementiert ein abstraktes {@link Unique} mit Zahlenlisten als Eingabe.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten (Ausgabe). */
	static abstract class UniquePool<GData> extends Unique<IAMArray, GData> {

		/** Dieses Feld speichert die gesammelten Nutzdaten. */
		public final List<GData> datas = new ArrayList<>();

		/** Diese Methode nimmt einen neuen Nutzdatensatz mit der gegebenen Zahlenliste in die Verwaltung auf und gibt den Index zurück, unter dem diese in
		 * {@link #datas} verwaltet werden.
		 *
		 * @param array Zahlenliste.
		 * @return Nutzdatensatz.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public GData put(final IAMArray array) throws NullPointerException {
			final GData data = this.customBuild(this.datas.size(), array);
			this.datas.add(data);
			return data;
		}

		/** Diese Methode leert diesen Pool. */
		public void clear() {
			this.mapping.clear();
			this.datas.clear();
		}

		/** Diese Methode erzeugt einen neuen Nutzdatensatz und gibt diesen zurück.
		 *
		 * @param index Index, unter dem der Nutzdatensatz in {@link #datas} verwaltet wird.
		 * @param array Zahlenliste.
		 * @return Nutzdatensatz. */
		protected abstract GData customBuild(int index, IAMArray array);

		/** {@inheritDoc} */
		@Override
		protected GData customBuild(final IAMArray array) {
			return this.put(array);
		}

	}

	@SuppressWarnings ("javadoc")
	static class UniqueItemPool extends UniquePool<ItemData> {

		@Override
		protected ItemData customBuild(final int index, final IAMArray array) {
			final ItemData data = new ItemData();
			data.index = index;
			data.item = array;
			return data;
		}

	}

	@SuppressWarnings ("javadoc")
	static class UniqueEntryPool extends UniquePool<EntryData> {

		@Override
		protected EntryData customBuild(final int index, final IAMArray array) {
			final EntryData data = new EntryData();
			data.index = index;
			data.key = array;
			data.value = array;
			return data;
		}

	}

	/** Diese Klasse implementiert einen modifizierbaren {@link IAMIndex}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class IAMIndexBuilder extends IAMIndex {

		/** Dieses Feld speichert die {@link IAMMapping}. */
		final List<IAMMapping> mappings = new ArrayList<>();

		/** Dieses Feld speichert die {@link IAMListing}. */
		final List<IAMListing> listings = new ArrayList<>();

		/** Dieser Konstruktor initialisiert einen leeren {@link IAMIndexBuilder}. */
		public IAMIndexBuilder() {
		}

		/** Diese Methode fügt das gegebene {@link IAMMapping} hinzu und gibt den Index zurück, unter dem dieses verwaltet wird.
		 *
		 * @param mapping {@link IAMMapping}.
		 * @return Index des {@link IAMMapping}.
		 * @throws NullPointerException Wenn {@code mapping} {@code null} ist. */
		public int putMapping(final IAMMapping mapping) throws NullPointerException {
			final int result = this.mappings.size();
			this.mappings.add(result, Objects.notNull(mapping));
			return result;
		}

		/** Diese Methode fügt das gegebene {@link IAMListing} hinzu und gibt den Index zurück, unter dem dieses verwaltet wird.
		 *
		 * @param listing {@link IAMListingBuilder}.
		 * @return Index des {@link IAMListing}.
		 * @throws NullPointerException Wenn {@code listing} {@code null} ist. */
		public int putListing(final IAMListing listing) throws NullPointerException {
			final int result = this.listings.size();
			this.listings.add(result, Objects.notNull(listing));
			return result;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public void clear() {
			this.mappings.clear();
			this.listings.clear();
		}

		/** {@inheritDoc} */
		@Override
		public IAMMapping mapping(final int index) {
			if ((index < 0) || (index >= this.mappings.size())) return IAMMapping.EMPTY;
			return this.mappings.get(index);
		}

		/** {@inheritDoc} */
		@Override
		public int mappingCount() {
			return this.mappings.size();
		}

		/** {@inheritDoc} */
		@Override
		public IAMListing listing(final int index) {
			if ((index < 0) || (index >= this.listings.size())) return IAMListing.EMPTY;
			return this.listings.get(index);
		}

		/** {@inheritDoc} */
		@Override
		public int listingCount() {
			return this.listings.size();
		}

	}

	/** Diese Klasse implementiert ein modifizierbares {@link IAMListing}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class IAMListingBuilder extends IAMListing {

		/** Dieses Feld speichert die bisher gesammelten Elemente. */
		final UniqueItemPool items = new UniqueItemPool();

		/** Dieser Konstruktor initialisiert einen leeren {@link IAMListingBuilder}. */
		public IAMListingBuilder() {
		}

		/** Diese Methode fügt das gegebene Element hinzu und gibt den Index zurück, unter dem das Element verwaltet wird. Wenn bereits ein Element mit dem gleichen
		 * Daten existiert, wird dessen Index zurück gegeben.
		 * <p>
		 * <u>Achtung:</u> Der Index ist garantiert der gleiche, der im {@link IAMListingLoader} verwerden wird.
		 *
		 * @see #put(IAMArray, boolean)
		 * @param value Element.
		 * @return Index, unter dem die Zahlenliste in der optimierten Datenstruktur registriert ist.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public int put(final IAMArray value) throws NullPointerException {
			return this.items.put(value).index;
		}

		/** Diese Methode fügt das gegebene Element hinzu und gibt den Index zurück, unter dem das Element verwaltet wird. Wenn die Wiederverwendung aktiviert ist
		 * und bereits ein Element mit dem gleichen Daten existiert, wird dessen Index zurück gegeben.
		 * <p>
		 * <u>Achtung:</u> Der Index ist garantiert der gleiche, der im {@link IAMListingLoader} verwerden wird.
		 *
		 * @param value Element.
		 * @param reuse {@code true}, wenn die Wiederverwendung aktiviert ist.
		 * @return Index, unter dem die Zahlenliste in der optimierten Datenstruktur registriert ist.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public int put(final IAMArray value, final boolean reuse) throws NullPointerException {
			if (reuse) return this.items.get(value).index;
			return this.items.put(value).index;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public void clear() {
			this.items.clear();
		}

		/** {@inheritDoc} */
		@Override
		public IAMArray item(final int itemIndex) {
			final List<ItemData> datas = this.items.datas;
			if ((itemIndex < 0) || (itemIndex >= datas.size())) return IAMArray.EMPTY;
			return datas.get(itemIndex).item;
		}

		/** {@inheritDoc} */
		@Override
		public int itemCount() {
			return this.items.datas.size();
		}

	}

	/** Diese Klasse implementiert ein modifizierbares {@link IAMMapping}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class IAMMappingBuilder extends IAMMapping {

		/** Dieses Feld speichert den Modus. */
		boolean mode = IAMMapping.MODE_HASHED;

		/** Dieses Feld speichert die Einträge. */
		final UniqueEntryPool entries = new UniqueEntryPool();

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
			this.entries.get(Objects.notNull(key)).value = Objects.notNull(value);
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
			final List<EntryData> datas = this.entries.datas;
			if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMArray.EMPTY;
			return datas.get(entryIndex).key;
		}

		/** {@inheritDoc} */
		@Override
		public IAMArray value(final int entryIndex) {
			final List<EntryData> datas = this.entries.datas;
			if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMArray.EMPTY;
			return datas.get(entryIndex).value;
		}

		/** {@inheritDoc} */
		@Override
		public int entryCount() {
			return this.entries.datas.size();
		}

		/** {@inheritDoc} */
		@Override
		public int find(final IAMArray key) throws NullPointerException {
			final EntryData result = this.entries.mapping().get(key.toInts());
			return result == null ? -1 : result.index;
		}

	}

}
