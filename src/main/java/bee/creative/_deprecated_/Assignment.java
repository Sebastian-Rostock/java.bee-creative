package bee.creative._deprecated_;

/** Diese Schnittstelle definiert Methoden zur Bereitstellung und Zuweisung von Informationen eines Quellobjekts auf ein Zielobket, welche von einem
 * {@link Assigner} bzw. {@link Assignable} genutzt werden können.<br>
 * Im Kontext eines {@link Assignable} kann einem Quellobjekt ein Zielobjekt zugeordnet werden, wodurch das Übertragen von Informationen in komplexern
 * Objektgraphen erleichtert werden kann.
 *
 * @see Assigner
 * @see Assignable
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GSource> Typ der Informationen, die auf ein Zielobjekt übertragen werden können. */
public interface Assignment<GSource> {

	/** Diese Methode gibt das Quellobjekt zurück, dessen Informationen auf ein Zielobjekt übertragen werden sollen.
	 *
	 * @see Assigner#assign(Object, Assignment)
	 * @see Assignable#assign(Assignment)
	 * @return Quellobjekt. */
	public GSource value();

	/** Diese Methode gibt das dem gegebenen Quellobjekt zugeordnete Zielobjekt zurück, sofern das Quellobjekt nicht {@code null} ist und ihm zuvor über
	 * {@link #set(Object, Object)} ein Zielobjekt zugeordnet wurde. Andernfalls wird das gegebene Quellobjekt zurück gegeben.
	 *
	 * @param <GObject> Typ der Quell- und Zielobjekte.
	 * @param source Quellobjekt oder {@code null}.
	 * @return Zielobjekt oder Quellobjekt. */
	public <GObject> GObject get(GObject source);

	/** Diese Methode ordnet dem gegebenen Quellobjekt das gegebene Zielobjekt zu. Wenn das Zielobjekt {@code null} ist, wird die Zuordnung aufgehoben.
	 *
	 * @param <GObject> Typ der Quell- und Zielobjekte.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt oder {@code null}.
	 * @throws NullPointerException Wenn das gegebene Quellobjekt {@code null} ist. */
	public <GObject> void set(GObject source, GObject target) throws NullPointerException;

	/** Diese Methode überträgt die Informationen des gegebenen Quellobjekts auf das gegebene Zielobjekt.<br>
	 * Die Implementation entspricht <pre>
	 * this.assign(source, target, true);</pre> und damit <pre>
	 * this.set(source, target);
	 * target.assign(this.assignment(source));</pre>
	 *
	 * @see #assign(Object, Assignable, boolean)
	 * @param <GObject> Typ der Quell- und Zielobjekte.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Quellobjekt ungültig ist. */
	public <GObject> void assign(GObject source, Assignable<? super GObject> target) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überträgt die Informationen des gegebenen Quellobjekts auf das gegebene Zielobjekt.<br>
	 * Hierbei wird dem Quellobjekt zuerst über {@link #set(Object, Object)} das Zielobjekt zugeordnet. Anschließend erfolgt die Zuweisung über die Methode
	 * {@link Assignable#assign(Assignment)} des gegebenen Zielobjekt entweder sofort ({@code commit = true}) oder später ({@code commit = false}) im Rahmen der
	 * Methode {@link #commit()}.
	 *
	 * @param <GObject> Typ der Quell- und Zielobjekte.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt.
	 * @param commit {@code true}, wenn der {@link Assigner} sofort angewandt werden soll; {@code false}, wenn dies erst durch die Methode {@link #commit()}
	 *        erfolgen soll.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Quellobjekt ungültig ist. */
	public <GObject> void assign(GObject source, Assignable<? super GObject> target, boolean commit) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überträgt die Informationen des gegebenen Quellobjekts auf das gegebene Zielobjekt.<br>
	 * Die Implementation entspricht <pre>
	 * this.assign(source, target, assigner, true);</pre> und damit <pre>
	 * this.set(source, target);
	 * assigner.assign(target, this.assignment(source));</pre>
	 *
	 * @see #assign(Object, Object, Assigner, boolean)
	 * @param <GObject> Typ der Quell- und Zielobjekte.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt.
	 * @param assigner {@link Assigner}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Quellobjekt ungültig ist. */
	public <GObject> void assign(GObject source, GObject target, Assigner<? super GObject, ? super GObject> assigner)
		throws NullPointerException, IllegalArgumentException;

	/** Diese Methode überträgt die Informationen des gegebenen Quellobjekts auf das gegebene Zielobjekt.<br>
	 * Hierbei wird dem Quellobjekt zuerst über {@link #set(Object, Object)} das Zielobjekt zugeordnet. Anschließend erfolgt die Zuweisung über die Methode
	 * {@link Assigner#assign(Object, Assignment)} des gegebenen {@link Assigner} entweder sofort ({@code commit = true}) oder später ({@code commit = false}) im
	 * Rahmen der Methode {@link #commit()}.
	 *
	 * @param <GObject> Typ des Quellobjekts sowie des Zielobjekts.
	 * @param source Quellobjekt.
	 * @param target Zielobjekt.
	 * @param assigner {@link Assigner}.
	 * @param commit {@code true}, wenn der {@link Assigner} sofort angewandt werden soll; {@code false}, wenn dies erst durch die Methode {@link #commit()}
	 *        erfolgen soll.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 * @throws IllegalArgumentException Wenn das Quellobjekt ungültig ist. */
	public <GObject> void assign(GObject source, GObject target, Assigner<? super GObject, ? super GObject> assigner, boolean commit)
		throws NullPointerException, IllegalArgumentException;

	/** Diese Methode führt alle Informationsübertragungen in der Reihenfolge aus, in der sie über die Methoden {@link #assign(Object, Assignable, boolean)} bzw.
	 * {@link #assign(Object, Object, Assigner, boolean)} registriert wurden.
	 *
	 * @throws IllegalArgumentException Wenn das Quellobjekt ungültig ist */
	public void commit() throws IllegalArgumentException;

	/** Diese Methode erzeugt eine {@link Assignment}-Sicht mit den gegebenen Quellobjekt. Der über {@link #set(Object, Object)},
	 * {@link #assign(Object, Assignable, boolean)} und {@link #assign(Object, Object, Assigner, boolean)} erzeugte Zustand dieses {@link Assignment} gilt auch im
	 * erzeugten {@link Assignment} und kann ebenfalls über dieses modifiziert werden.
	 *
	 * @param <GObject> Typ der Quell- und Zielobjekte.
	 * @param source Quellobjekt.
	 * @return neues {@link Assignment}. */
	public <GObject> Assignment<GObject> assignment(GObject source);

}
