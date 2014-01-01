package bee.creative.data;

import bee.creative.util.Field;

/**
 * Diese Schnittstelle definiert ein Objekt als Bestandteil seines Besitzers.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Part extends Owned, Typed, Labeled {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object owner();

	/**
	 * Diese Methode gibt das {@link Field} zurück, über welches dieser {@link Part} von seinem {@link #owner()} aus erreichbar ist.
	 * 
	 * @return {@link Field} des {@link Part}s in seinem {@link #owner()}.
	 */
	public Field<?, ?> field();

	/**
	 * {@inheritDoc}
	*/
	@Override
	public Type<?> type();

	/**
	 * {@inheritDoc}
	*/
	@Override
	public String label();

}
