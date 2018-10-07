package bee.creative.iam;

import java.util.ArrayList;
import java.util.List;
import bee.creative.iam.IAMLoader.IAMListingLoader;
import bee.creative.util.Comparators;
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
	static final class ItemData {

		/** Dieses Feld speichert den Index, unter dem dieses Objekt in {@link UniquePool#datas} verwaltet wird. */
		public int index;

		/** Dieses Feld speichert die Zahlenfolge des Elements. */
		public int[] item;

	}

	/** Diese Klasse implementiert ein Element einer {@link UniqueEntryPool} eines {@link IAMMappingBuilder}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	static final class EntryData {

		/** Dieses Feld speichert den Index, unter dem dieses Objekt in {@link UniquePool#datas} verwaltet wird. */
		public int index;

		/** Dieses Feld speichert die Zahlenfolge des Schlüssels. */
		public int[] key;

		/** Dieses Feld speichert die Zahlenfolge des Werts. */
		public int[] value;

	}

	/** Diese Klasse implementiert ein abstraktes {@link Unique} mit Zahlenlisten als Eingabe.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Nutzdaten (Ausgabe). */
	static abstract class UniquePool<GData> extends Unique<int[], GData> {

		/** Dieses Feld speichert die gesammelten Nutzdaten. */
		public final List<GData> datas = new ArrayList<>();

		/** Diese Methode nimmt einen neuen Nutzdatensatz mit der gegebenen Zahlenliste in die Verwaltung auf und gibt den Index zurück, unter dem diese in
		 * {@link #datas} verwaltet werden.
		 *
		 * @param array Zahlenliste.
		 * @return Nutzdatensatz.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public final GData put(final int[] array) throws NullPointerException {
			final GData data = this.customBuild(this.datas.size(), array.clone());
			this.datas.add(data);
			return data;
		}

		/** Diese Methode leert diesen Pool. */
		public final void clear() {
			this.mapping.clear();
			this.datas.clear();
		}

		/** Diese Methode erzeugt einen neuen Nutzdatensatz und gibt diesen zurück.
		 *
		 * @param index Index, unter dem der Nutzdatensatz in {@link #datas} verwaltet wird.
		 * @param array Zahlenliste.
		 * @return Nutzdatensatz. */
		protected abstract GData customBuild(int index, int[] array);

		/** {@inheritDoc} */
		@Override
		public final int hash(final Object array) throws NullPointerException {
			return IAMBuilder.hash((int[])array);
		}

		/** {@inheritDoc} */
		@Override
		public final boolean equals(final Object array1, final Object array2) throws NullPointerException {
			return IAMBuilder.equals((int[])array1, (int[])array2);
		}

		/** {@inheritDoc} */
		@Override
		public final int compare(final int[] array1, final int[] array2) throws NullPointerException {
			return IAMBuilder.compare(array1, array2);
		}

		/** {@inheritDoc} */
		@Override
		protected final GData customBuild(final int[] array) {
			return this.put(array);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniqueItemPool extends UniquePool<ItemData> {

		@Override
		protected final ItemData customBuild(final int index, final int[] array) {
			final ItemData data = new ItemData();
			data.index = index;
			data.item = array;
			return data;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniqueEntryPool extends UniquePool<EntryData> {

		@Override
		protected final EntryData customBuild(final int index, final int[] array) {
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
	public static final class IAMIndexBuilder extends IAMIndex {

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
		public final int putMapping(final IAMMapping mapping) throws NullPointerException {
			final int result = this.mappings.size();
			this.mappings.add(result, Objects.notNull(mapping));
			return result;
		}

		/** Diese Methode fügt das gegebene {@link IAMListing} hinzu und gibt den Index zurück, unter dem dieses verwaltet wird.
		 *
		 * @param listing {@link IAMListingBuilder}.
		 * @return Index des {@link IAMListing}.
		 * @throws NullPointerException Wenn {@code listing} {@code null} ist. */
		public final int putListing(final IAMListing listing) throws NullPointerException {
			final int result = this.listings.size();
			this.listings.add(result, Objects.notNull(listing));
			return result;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public final void clear() {
			this.mappings.clear();
			this.listings.clear();
		}

		/** {@inheritDoc} */
		@Override
		public final IAMMapping mapping(final int index) {
			if ((index < 0) || (index >= this.mappings.size())) return IAMMapping.EMPTY;
			return this.mappings.get(index);
		}

		/** {@inheritDoc} */
		@Override
		public final int mappingCount() {
			return this.mappings.size();
		}

		/** {@inheritDoc} */
		@Override
		public final IAMListing listing(final int index) {
			if ((index < 0) || (index >= this.listings.size())) return IAMListing.EMPTY;
			return this.listings.get(index);
		}

		/** {@inheritDoc} */
		@Override
		public final int listingCount() {
			return this.listings.size();
		}

	}

	/** Diese Klasse implementiert ein modifizierbares {@link IAMListing}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMListingBuilder extends IAMListing {

		/** Dieses Feld speichert die bisher gesammelten Elemente. */
		final UniqueItemPool items = new UniqueItemPool();

		/** Dieser Konstruktor initialisiert einen leeren {@link IAMListingBuilder}. */
		public IAMListingBuilder() {
		}

		/** Diese Methode gibt das modifizierbare {@code int}-Array des {@code itemIndex}-te Elements zurück. Dieses Array sollte nur dann verändert werden, wenn es
		 * über {@link #put(int[], boolean)} ohne Wiederverwendung hunzugefügt wurde.
		 *
		 * @see #put(int[], boolean)
		 * @see #itemCount()
		 * @param itemIndex Index des Elements.
		 * @return {@code int}-Array des {@code itemIndex}-ten Elements.
		 * @throws IndexOutOfBoundsException Wenn eine der Eingaben ungültig ist. */
		public final int[] get(final int itemIndex) throws IndexOutOfBoundsException {
			return this.items.datas.get(itemIndex).item;
		}

		/** Diese Methode fügt das gegebene Element hinzu und gibt den Index zurück, unter dem das Element verwaltet wird. Wenn bereits ein Element mit dem gleichen
		 * Daten existiert, wird dessen Index zurück gegeben.
		 * <p>
		 * <u>Achtung:</u> Der Index ist garantiert der gleiche, der im {@link IAMListingLoader} verwerden wird.
		 *
		 * @see #put(int[], boolean)
		 * @param value Element.
		 * @return Index, unter dem die Zahlenliste in der optimierten Datenstruktur registriert ist.
		 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
		public final int put(final int[] value) throws NullPointerException {
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
		public final int put(final int[] value, final boolean reuse) throws NullPointerException {
			if (reuse) return this.items.get(value).index;
			return this.items.put(value).index;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public final void clear() {
			this.items.clear();
		}

		/** {@inheritDoc} */
		@Override
		public final IAMArray item(final int itemIndex) {
			final List<ItemData> datas = this.items.datas;
			if ((itemIndex < 0) || (itemIndex >= datas.size())) return IAMArray.EMPTY;
			return IAMArray.from(datas.get(itemIndex).item);
		}

		/** {@inheritDoc} */
		@Override
		public final int itemCount() {
			return this.items.datas.size();
		}

	}

	/** Diese Klasse implementiert ein modifizierbares {@link IAMMapping}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class IAMMappingBuilder extends IAMMapping {

		/** Dieses Feld speichert den Modus. */
		boolean mode = IAMMapping.MODE_HASHED;

		/** Dieses Feld speichert die Einträge. */
		final UniqueEntryPool entries = new UniqueEntryPool();

		/** Dieser Konstruktor initialisiert einen leeren {@link IAMMappingBuilder}. */
		public IAMMappingBuilder() {
		}

		/** Diese Methode gibt das modifizierbare {@code int}-Array des Werts des {@code entryIndex}-ten Eintrags zurück.
		 *
		 * @see #entryCount()
		 * @param entryIndex Index des Eintrags.
		 * @return {@code int}-Array des Werts des {@code entryIndex}-ten Eintrags.
		 * @throws IndexOutOfBoundsException Wenn {@code entryIndex} ungültig ist. */
		public final int[] get(final int entryIndex) throws IndexOutOfBoundsException {
			return this.entries.datas.get(entryIndex).value;
		}

		/** Diese Methode fügt einen Eintrag mit dem gegebenen Schlüssel sowie dem gegebenen Wert hinzu. Wenn bereits ein Eintrag mit diesem Schlüssel existiert,
		 * wird dessen Wert ersetzt.
		 *
		 * @param key Schlüssel.
		 * @param value Wert.
		 * @throws NullPointerException Wenn {@code key} bzw. {@code value} {@code null} ist. */
		public final void put(final int[] key, final int[] value) throws NullPointerException {
			Objects.notNull(key);
			Objects.notNull(value);
			this.entries.get(key).value = value;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean mode() {
			return this.mode;
		}

		/** Diese Methode setzt den Modus.
		 *
		 * @see #MODE_HASHED
		 * @see #MODE_SORTED
		 * @param mode Modus. */
		public final void mode(final boolean mode) {
			this.mode = mode;
		}

		/** Diese Methode entfernt alle bisher zusammengestellten Daten. */
		public final void clear() {
			this.entries.clear();
		}

		/** {@inheritDoc} */
		@Override
		public final IAMArray key(final int entryIndex) {
			final List<EntryData> datas = this.entries.datas;
			if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMArray.EMPTY;
			return IAMArray.from(datas.get(entryIndex).key);
		}

		/** {@inheritDoc} */
		@Override
		public final IAMArray value(final int entryIndex) {
			final List<EntryData> datas = this.entries.datas;
			if ((entryIndex < 0) || (entryIndex >= datas.size())) return IAMArray.EMPTY;
			return IAMArray.from(datas.get(entryIndex).value);
		}

		/** {@inheritDoc} */
		@Override
		public final int entryCount() {
			return this.entries.datas.size();
		}

		/** {@inheritDoc} */
		@Override
		public final int find(final IAMArray key) throws NullPointerException {
			final EntryData result = this.entries.mapping().get(key.toArray());
			return result == null ? -1 : result.index;
		}

	}

	/** Diese Methode gibt den Streuwert der gegebenen Zahlenfolge zurück.
	 *
	 * @see IAMArray#hash()
	 * @param array Zahlenfolge.
	 * @return Streuwert.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static int hash(final int[] array) throws NullPointerException {
		int hash = 0x811C9DC5;
		for (int i = 0, size = array.length; i < size; i++) {
			hash = (hash * 0x01000193) ^ array[i];
		}
		return hash;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Zahlenfolgen gleich sind.
	 *
	 * @see IAMArray#equals(IAMArray)
	 * @param array1 erste Zahlenfolge.
	 * @param array2 zweite Zahlenfolge.
	 * @return {@code true}, wenn die Zahlenfolgen gleich sind.
	 * @throws NullPointerException Wenn {@code array1} bzw. {@code array2} {@code null} ist. */
	public static boolean equals(final int[] array1, final int[] array2) throws NullPointerException {
		final int length1 = array1.length, length2 = array2.length;
		if (length1 != length2) return false;
		for (int i = 0; i < length1; i++)
			if (array1[i] != array2[i]) return false;
		return true;
	}

	/** Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn die Ordnung der ersten Zahlenfolge lexikografisch kleiner, gleich bzw.
	 * größer als die der zweiten Zahlenfolge ist.
	 *
	 * @see IAMArray#compare(IAMArray)
	 * @param array1 erste Zahlenfolge.
	 * @param array2 zweite Zahlenfolge.
	 * @return Vergleichswert der Ordnungen.
	 * @throws NullPointerException Wenn {@code array1} bzw. {@code array2} {@code null} ist. */
	public static int compare(final int[] array1, final int[] array2) throws NullPointerException {
		final int length1 = array1.length, length2 = array2.length;
		for (int i = 0, length = length1 < length2 ? length1 : length2, result; i < length; i++)
			if ((result = Comparators.compare(array1[i], array2[i])) != 0) return result;
		return length1 - length2;
	}

}
