package bee.creative.data;

import bee.creative.util.Field;

/**
 * Diese Schnittstelle definiert den Datentyp eines {@link Item}s, welcher zur {@link #is(Type) Typprüfung} verwendet werden kann und die {@link Field}s des
 * {@link Item}s kennt.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des {@link Item}.
 */
public interface Type<GItem extends Item<?>> {

	/**
	 * Diese Methode gibt den Identifikator dieses {@link Type}s zurück, dessen Zahlenwert über eine statische Konstante definiert werden sollte, um
	 * Fallunterscheidungen mit einem {@code switch}-Statement zu ermöglichen.
	 * 
	 * @return Identifikator dieses {@link Type}{@code s}.
	 */
	public int id();

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn ein {@code cast} des {@link Item} ({@code GItem}) in den gegebenen {@link Type} (Vorfahrentyp)
	 * zulässig ist.
	 * 
	 * @see Class#isAssignableFrom(Class)
	 * @param type {@link Type}.
	 * @return {@code true}, wenn ein {@code cast} in den gegebenen {@link Type} (Vorfahrentyp) zulässig ist.
	 * @throws NullPointerException Wenn der gegebene {@link Type} {@code null} ist.
	 */
	public boolean is(Type<?> type) throws NullPointerException;

	/**
	 * Diese Methode gibt die {@link Field} des {@link Item}s zurück.
	 * 
	 * @return {@link Field} des {@link Item}s.
	 */
	public Iterable<? extends Field<? super GItem, ?>> fields();

}
