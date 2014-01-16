package bee.creative.util;

/**
 * Diese Schnittstelle definiert Methoden zur Bereitstellung und Zuweisung von Informationen eines Quellobjekts auf ein Zielobket, welche von einem
 * {@link Assigner} bzw. {@link Assignable} genutzt werden können.<br>
 * Im Kontext eines {@link Assignable}s kann einem Quellobjekt ein Zielobjekt zugeordnet werden, wodurch das Übertragen von Informationen in komplexern
 * Objektgraphen erleichtert werden kann.
 * 
 * @see Assigner
 * @see Assignable
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der Informationen, die auf ein Zielobjekt übertragen werden können.
 */
public interface Assignment<GValue> {

	/**
	 * Diese Methode gibt das Quellobjekt zurück, dessen Informationen auf ein Zielobjekt übertragen werden sollen.
	 * 
	 * @see Assigner#assign(Object, Assignment)
	 * @see Assignable#assign(Assignment)
	 * @return Quellobjekt.
	 */
	public GValue value();

	/**
	 * Diese Methode gibt das dem gegebenen Quellobjekt zugeordnete Zielobjekt zurück, sofern das Quellobjekt nicht {@code null} ist und ihm zuvor über
	 * {@link #set(Object, Object)} ein Zielobjekt zugeordnet wurde. Andernfalls wird das gegebene Quellobjekt zurück gegeben.
	 * 
	 * @param <GObject> Typ der Quell- und Zielobjekte.
	 * @param source Quellobjekt oder {@code null}.
	 * @return Zielobjekt oder Quellobjekt.
	 */
	public <GObject> GObject get(GObject source);

	/**
	 * Diese Methode ordnet dem gegebenen Quellobjekt das gegebene Zielobjekt zu. Wenn das Zielobjekt {@code null} ist, wird die Zuordnung aufgehoben. Die
	 * Zuordnung gilt rekursiv auch für das {@link Assignment}, das diesen via {@link #assignment(Object)} erzeugt hat.
	 * 
	 * @param <GObject> Typ der Quell- und Zielobjekte.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt oder {@code null}.
	 * @throws NullPointerException Wenn das gegebene Quellobjekt {@code null} ist.
	 */
	public <GObject> void set(GObject source, GObject target) throws NullPointerException;

	/**
	 * Diese Methode überträgt die Informationen des gegebenen Quellobjekts auf das gegebene Zielobjekt.<br>
	 * Dazu wird über die Methode {@link #set(Object, Object)} dem Quellobjekt das Zielobjekt zugeordnet. Danach wird die Methode
	 * {@link Assignable#assign(Assignment)} des Zielobjekts mit einem {@link Assignment} aufgerufen, das via {@link #assignment(Object)} zu dem gegebenen
	 * Quellobjekt erzeugt wurde.
	 * <p>
	 * Die Implementation entspricht folgenden Befehlen:
	 * 
	 * <pre>
	 * this.set(source, target);
	 * target.assign(this.assignment(source));</pre>
	 * 
	 * @see #set(Object, Object)
	 * @see #value()
	 * @see #assignment(Object)
	 * @see Assignable#assign(Assignment)
	 * @param <GObject> Typ des Quellobjekts.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Quellobjekt vom Zielobjekt nicht unterstützt wird.
	 */
	public <GObject> void assign(GObject source, Assignable<? super GObject> target) throws NullPointerException, IllegalArgumentException;

	/**
	 * Diese Methode überträgt die Informationen des gegebenen Quellobjekts auf das gegebene Zielobjekt.<br>
	 * Dazu wird über die Methode {@link #set(Object, Object)} dem Quellobjekt das Zielobjekt zugeordnet. Danach wird die Methode
	 * {@link Assigner#assign(Object, Assignment)} mit dem Zielobjekts sowie einem {@link Assignment} aufgerufen, das via {@link #assignment(Object)} zu dem
	 * gegebenen Quellobjekt erzeugt wurde. <br>
	 * <p>
	 * Die Implementation entspricht folgenden Befehlen:
	 * 
	 * <pre>
	 * this.set(source, target);
	 * assigner.assign(target, this.assignment(source));</pre>
	 * 
	 * @param <GObject> Typ des Quellobjektssowie des Zielobjekts.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt.
	 * @param assigner {@link Assigner}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Quellobjekt vom Zielobjekt nicht unterstützt wird.
	 */
	public <GObject> void assign(GObject source, GObject target, Assigner<? super GObject, ? super GObject> assigner) throws NullPointerException,
		IllegalArgumentException;

	/**
	 * Diese Methode erzeugt ein {@link Assignment} mit den gegebenen Quellobjekt sowie den in diesem {@link Assignment} via {@link #set(Object, Object)}
	 * gemachten Zuordnugnen, und gibt es zurück.
	 * 
	 * @see #set(Object, Object)
	 * @see #value()
	 * @param <GObject> Typ der Informationen.
	 * @param source Quellobjekt.
	 * @return neues {@link Assignment}.
	 */
	public <GObject> Assignment<GObject> assignment(GObject source);

}
