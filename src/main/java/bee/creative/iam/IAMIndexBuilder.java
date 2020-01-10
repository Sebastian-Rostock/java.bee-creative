package bee.creative.iam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.lang.Objects;
import bee.creative.util.Unique;

/** Diese Klasse implementiert einen modifizierbaren {@link IAMIndex}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class IAMIndexBuilder extends IAMIndex {

	/** Diese Klasse implementiert den abstrakten Ausgabedatensatz eines {@link BasePool}. */
	static abstract class BaseData implements Emuable {

		/** Dieses Feld speichert die Position, unter der dieses Objekt in {@link BasePool#datas} verwaltet wird. */
		public int index;

		@Override
		public String toString() {
			return Integer.toString(this.index);
		}

	}

	/** Diese Klasse implementiert ein abstraktes {@link Unique}, über welches Nutzdaten in der Reihenfolge ihrer Erfassung gesammelt werden können.
	 *
	 * @param <GData> Typ der Ausgabe. */
	static abstract class BasePool<GData> extends Unique<IAMArray, GData> implements Emuable {

		/** Dieses Feld speichert die gesammelten Ausgabedaten. */
		public final List<GData> datas = new ArrayList<>();

		/** Dieses Feld speichert Puffer zur Optimierung bzw. Aulagerung der Eingaben in {@link #customSource(IAMArray)}. */
		public final IAMBuffer buffer;

		public BasePool(final IAMBuffer builder) {
			this.buffer = Objects.notNull(builder);
		}

		/** Diese Methode nimmt einen neuen Ausgabedatensatz mit den gegebenen Eingabedaten in die Verwaltung auf und gibt diesen zurück.
		 *
		 * @param source Eingabedatensatz.
		 * @return Ausgabedatensatz.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public GData put(final IAMArray source) throws NullPointerException {
			final int index = this.datas.size();
			final GData data = this.customData(index, this.buffer.get(source));
			this.datas.add(index, data);
			return data;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.mapping) + EMU.from(this.datas) + EMU.fromAll(this.datas);
		}

		/** Diese Methode leert den Pool. */
		public void clear() {
			this.mapping.clear();
			this.datas.clear();
		}

		@Override
		protected final IAMArray customSource(final IAMArray source) {
			return this.buffer.get(source);
		}

		@Override
		protected final GData customTarget(final IAMArray source) {
			return this.put(source);
		}

		/** Diese Methode erzeugt einen neuen Ausgabedatensatz und gibt diesen zurück.
		 *
		 * @param index Position, unter welcher der Ausgabedatensatz in {@link #datas} verwaltet wird.
		 * @param source Eingabedatensatz.
		 * @return Ausgabedatensatz. */
		protected abstract GData customData(int index, IAMArray source);

	}

	static class ListingData extends IAMIndexBuilder.BaseData {

		public IAMListing listing;

		public ListingData(final int index, final IAMListing listing) {
			this.index = index;
			this.listing = listing;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.listing);
		}

	}

	static class ListingPool extends IAMIndexBuilder.BasePool<ListingData> {

		public ListingPool(final IAMBuffer buffer) {
			super(buffer);

		}

		@Override
		protected ListingData customData(final int index, final IAMArray source) {
			try {
				return new ListingData(index, IAMListing.from(source));
			} catch (final IOException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

	}

	static class MappingData extends IAMIndexBuilder.BaseData {

		public IAMMapping mapping;

		public MappingData(final int index, final IAMMapping mapping) {
			this.index = index;
			this.mapping = mapping;
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.mapping);
		}

	}

	static class MappingPool extends IAMIndexBuilder.BasePool<MappingData> {

		public MappingPool(final IAMBuffer buffer) {
			super(buffer);

		}

		@Override
		protected MappingData customData(final int index, final IAMArray source) {
			try {
				return new MappingData(index, IAMMapping.from(source));
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
		return this.mappings.get(IAMArray.from(mapping.toBytes())).index;
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
			this.mappings.datas.add(data.index, data);
			return data.index;
		} else {
			this.mappings.datas.get(index).mapping = Objects.notNull(mapping);
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
		return this.listings.get(IAMArray.from(listing.toBytes())).index;
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
			this.listings.datas.add(data.index, data);
			return data.index;
		} else {
			this.listings.datas.get(index).listing = Objects.notNull(listing);
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
		return this.listings.datas.get(index).listing;
	}

	@Override
	public int listingCount() {
		return this.listings.datas.size();
	}

	@Override
	public IAMMapping mapping(final int index) {
		if ((index < 0) || (index >= this.mappingCount())) return IAMMapping.EMPTY;
		return this.mappings.datas.get(index).mapping;
	}

	@Override
	public int mappingCount() {
		return this.mappings.datas.size();
	}

}