package bee.creative.data;

import bee.creative.util.Assigner;
import bee.creative.util.Assignment;
import bee.creative.util.Converter;
import bee.creative.util.Field;
import bee.creative.util.Filters;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert ein abstraktes {@link Item}, dass seinen {@link BasePool} kennt und einen Teil seiner Schnittstelle an diesen Delegiert. <br>
 * Die Methoden {@link #append()}, {@link #remove()} und {@link #update()} delegieren an {@link BasePool#append(BaseItem)}, {@link BasePool#remove(BaseItem)}
 * bzw. {@link BasePool#update(BaseItem)}. <br>
 * Der {@link #hashCode() Streuwert} basiert auf dem {@link #key() Schlüssel}, die {@link #equals(Object) Äquivalenz} basiert auf der von {@link #key()
 * Schlüssel} und {@link #pool() Pool}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class BaseItem implements Item {

	/**
	 * Dieses Feld speichert den {@link Converter} für {@link #assigners(Assignment)}, welcher seine Eingabe ({@link Field}) als {@link Assigner} zurück gibt,
	 * sofern diese ein solcher ist ({@code instanceof}). Andernfalls wird {@code null} geliefert.
	 */
	protected static final Converter<Field<?, ?>, Assigner<? super Item, ? super Item>> FIELD_ASSIGNER_CONVERTER =
		new Converter<Field<?, ?>, Assigner<? super Item, ? super Item>>() {

			@SuppressWarnings ("unchecked")
			@Override
			public Assigner<? super Item, ? super Item> convert(final Field<?, ?> input) {
				return input instanceof Assigner<?, ?> ? (Assigner<? super Item, ? super Item>)input : null;
			}

		};

	{}

	/**
	 * Diese Methode gibt die {@link Assigner} zurück, die in {@link #assign(Assignment)} zur Übertragung der Informatioenen des gegebenen {@link Item}s auf
	 * dieses {@link Item} verwendet werden.
	 * <p>
	 * Die Implementation in {@link BaseItem} verwndet hierfür die am {@link #type()} dieses bzw. des gegebenen {@link Item}s definierten {@link Field}s, die die
	 * {@link Assigner}-Schnittstelle implementieren. Die genutzten {@link Field}s ergeben sich aus:
	 * {@code this.type().is(value.type()) ? value.type().fields() : value.type().is(this.type()) ? this.type().fields() : Iterables.voidIterable())}.
	 * 
	 * @see #FIELD_ASSIGNER_CONVERTER
	 * @param assignment {@link Item} als Quellobjekt des in {@link #assign(Assignment)} gegebenen {@link Assignment}s.
	 * @return {@link Assigner}s.
	 */
	protected Iterable<? extends Assigner<? super Item, ? super Item>> assigners(final Assignment<? extends Item> assignment) {
		final Type<?> thisType = this.type(), thatType = assignment.value().type();
		return Iterables.filteredIterable(Filters.nullFilter(), Iterables.convertedIterable(BaseItem.FIELD_ASSIGNER_CONVERTER, //
			thisType.is(thatType) ? thatType.fields() : thatType.is(thisType) ? thisType.fields() : Iterables.<Field<?, ?>>emptyIterable()));
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract BasePool<? extends Item> pool();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<?> type() {
		return this.pool().type();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object owner() {
		return this.pool().owner();
	}

	/**
	 * {@inheritDoc} Hierbei werden die {@link Assigner} verwendet, die über die Methode {@link #assigners(Assignment)} aus dem {@link Item} des gegebenen
	 * {@link Assignment}s ({@link Assignment#value() Quellobjekt}) ermittelt werden.
	 * 
	 * @see #assigners(Assignment)
	 * @see Assignment#assign(Object, Object, Assigner)
	 */
	@Override
	public void assign(final Assignment<? extends Item> assignment) throws NullPointerException, IllegalArgumentException {
		if (assignment == null) throw new NullPointerException("assignment = null");
		final Item value = assignment.value();
		if (value == null) throw new IllegalArgumentException("value = null");
		for (final Assigner<? super Item, ? super Item> assigner: this.assigners(assignment)) {
			assigner.assign(this, assignment);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see BasePool#delete(BaseItem)
	 * @see BasePool#doDelete(Item)
	 */
	@Override
	public void delete() {
		this.pool().delete(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see BasePool#append(BaseItem)
	 * @see BasePool#doAppend(Item)
	 */
	@Override
	public void append() {
		this.pool().append(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see BasePool#remove(BaseItem)
	 * @see BasePool#doRemove(Item)
	 */
	@Override
	public void remove() throws IllegalStateException {
		this.pool().remove(this);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see BasePool#update(BaseItem)
	 * @see BasePool#doUpdate(Item)
	 */
	@Override
	public void update() throws IllegalStateException {
		this.pool().update(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final long value = this.key();
		return (int)(value ^ (value >>> 32));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Item)) return false;
		final Item data = (Item)object;
		return (this.key() == data.key()) && Objects.equals(this.pool(), data.pool());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this);
	}

}