package bee.creative.util;

/**
 * Diese Schnittstelle definiert ein Objekt, das von einem {@link Assignable} zur Übertragung von Informationen eines Quellobjekt verwendet wird.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ des Quellobjekt, dessen Informationen auf ein {@link Assignable} übertragen werden können.
 */
public interface Assigner<GSource> {

	/**
	 * Diese Methode gibt das Quellobjekt zurück, dessen Informationen auf ein {@link Assignable Zielobjekt} übertragen werden sollen.
	 * 
	 * @see Assignable#assign(Assigner)
	 * @return Quellobjekt.
	 */
	public GSource source();

	/**
	 * Diese Methode gibt das dem gegebenen Quellobjekt zugeordnete Zielobjekt zurück, sofern das Quellobjekt nicht {@code null} ist und ihm zuvor über
	 * {@link #set(Object, Object)} ein Zielobjekt zugeordnet wurde. Andernfalls wird das gegebene Quellobjekt zurück gegeben.
	 * 
	 * @param <GValue> Typ der Quell- und Zielobjekte.
	 * @param source Quellobjekt oder {@code null}.
	 * @return Zielobjekt oder Quellobjekt.
	 */
	public <GValue> GValue get(GValue source);

	/**
	 * Diese Methode ordnet dem gegebenen Quellobjekt das gegebene Zielobjekt zu. Wenn das Zielobjekt {@code null} ist, wird die Zuordnung aufgehoben. Die
	 * Zuordnung gilt rekursiv auch für den {@link Assigner}, der diesen via {@link #assign(Object, Assignable)} erzeugt hat.
	 * 
	 * @param <GValue> Typ der Quell- und Zielobjekte.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt oder {@code null}.
	 * @throws NullPointerException Wenn das gegebene Quellobjekt {@code null} ist.
	 */
	public <GValue> void set(GValue source, GValue target) throws NullPointerException;

	/**
	 * Diese Methode überträgt die Informationen des gegebenen Quellobjekts auf das gegebene Zielobjekt. Dazu wird über die Methode {@link #set(Object, Object)}
	 * dem Quellobjekt das Zielobjekt zugeordnet. Danach wird die Methode {@link Assignable#assign(Assigner)} des Zielobjekts mit einem {@link Assigner}
	 * aufgerufen, der das gegebene Quellobjekt und die in diesem {@link Assigner} gemachten Zuordnugnen verwendet.
	 * 
	 * @param <GSource2> Typ des Quellobjekts.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Quellobjekt vom Zielobjekt nicht unterstützt wird.
	 */
	public <GSource2> void assign(GSource2 source, Assignable<? super GSource2> target) throws NullPointerException, IllegalArgumentException;

}
