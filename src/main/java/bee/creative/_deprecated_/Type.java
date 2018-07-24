package bee.creative._deprecated_;

import bee.creative.util.Field;

/** Diese Schnittstelle definiert den Identifikator eines Datentyps, welcher eine {@link #label() Beschriftung} besitzt und zur {@link #is(Type) Typprüfung}
 * verwendet werden kann. Die {@link #fields()} ermöglichen den generischen Zugriff auf die Attributen eines Datensatzes dieses Datentyps.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes. */
public interface Type<GItem> extends Labeled {

	/** Diese Methode gibt nur dann {@code true} zurück, wenn ein {@code cast} des {@link Item} ({@code GItem}) in den gegebenen {@link Type} (Vorfahrentyp)
	 * zulässig ist.
	 *
	 * @see Class#isAssignableFrom(Class)
	 * @param type {@link Type} oder {@code null}.
	 * @return {@code true}, wenn der gegebene {@link Type} nicht {@code null} und ein {@code cast} in den gegebenen {@link Type} (Vorfahrentyp) zulässig sind. */
	public boolean is(Type<?> type);

	/** Diese Methode gibt die {@link Field} eines Datensatzes dieses Datentyps zurück.
	 *
	 * @return {@link Field}. */
	public Iterable<? extends Field<? super GItem, ?>> fields();

}
