package bee.creative.array;

import java.util.Arrays;
import java.util.List;

/**
 * Diese Schnittstelle definiert ein modifizierbares Array mit {@link List}-Sicht auf seine Werte.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GArray> Typ des Arrays (z.B. {@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]} oder {@code boolean[]}).
 * @param <GValue> Typ der Werte (z.B. {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} oder {@link Boolean}).
 */
public interface Array<GArray, GValue> {

	/**
	 * Diese Methode gibt die Anzahl der Werte zurück.
	 * 
	 * @return Anzahl der Werte.
	 */
	public int size();

	/**
	 * Diese Methode entfernt alle Werte ({@code this.remove(0, this.size())}).
	 * 
	 * @see Array#remove(int, int)
	 */
	public void clear();

	/**
	 * Diese Methode nur dann {@code true} zurück, wenn die Anzahl der Werte {@code 0} ist.
	 * 
	 * @see Array#size()
	 * @return {@code true}, wenn die Anzahl der Werte {@code 0} ist.
	 */
	public boolean isEmpty();

	/**
	 * Diese Methode füllt das gegebene {@link Array} mit den Werten ab der gegebenen Position.
	 * 
	 * @param index Position.
	 * @param values {@link Array}.
	 * @throws NullPointerException Wenn das gegebene {@link Array} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}).
	 */
	public void get(int index, Array<GArray, GValue> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode füllt die gegebene {@link ArraySection} mit den Werten ab der gegebenen Position.
	 * 
	 * @param index Position.
	 * @param values {@link ArraySection}.
	 * @throws NullPointerException Wenn die gegebene {@link ArraySection} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}).
	 */
	public void get(int index, ArraySection<GArray> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode kopiert die Werte des gegebenen {@link Array}s an die gegebene Position.
	 * 
	 * @param index Position.
	 * @param values {@link Array}.
	 * @throws NullPointerException Wenn das gegebene {@link Array} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}).
	 */
	public void set(int index, Array<GArray, GValue> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode kopiert die Werte der gegebenen {@link ArraySection} an die gegebene Position.
	 * 
	 * @param index Position.
	 * @param values {@link ArraySection}.
	 * @throws NullPointerException Wenn die gegebene {@link ArraySection} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}).
	 */
	public void set(int index, ArraySection<GArray> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode fügt die Werte des gegebenen {@link Array}s am Ende ein.
	 * 
	 * @see Array#add(int, Array)
	 * @param values {@link Array}.
	 * @throws NullPointerException Wenn das gegebene {@link Array} {@code null} ist.
	 */
	public void add(Array<GArray, GValue> values) throws NullPointerException;

	/**
	 * Diese Methode fügt die Werte der gegebenen {@link ArraySection} am Ende ein.
	 * 
	 * @see Array#add(int, ArraySection)
	 * @param values {@link ArraySection}.
	 * @throws NullPointerException Wenn die gegebene {@link ArraySection} {@code null} ist.
	 */
	public void add(ArraySection<GArray> values) throws NullPointerException;

	/**
	 * Diese Methode fügt die Werte des gegebenen {@link Array}s an der gegebenen Position ein.
	 * 
	 * @see Array#insert(int, int)
	 * @see Array#set(int, Array)
	 * @param index Position.
	 * @param values {@link Array}.
	 * @throws NullPointerException Wenn da gegebene {@link Array} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}).
	 */
	public void add(int index, Array<GArray, GValue> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode fügt die Werte der gegebenen {@link ArraySection} an der gegebenen Position ein.
	 * 
	 * @see Array#insert(int, int)
	 * @see Array#set(int, ArraySection)
	 * @param index Position.
	 * @param values {@link ArraySection}.
	 * @throws NullPointerException Wenn die gegebene {@link ArraySection} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size()}).
	 */
	public void add(int index, ArraySection<GArray> values) throws NullPointerException, IndexOutOfBoundsException;

	/**
	 * Diese Methode fügt die gegebene Anzahl an Werten ab dem gegebenen Index in das Array ein.
	 * 
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
	 * @throws IllegalArgumentException Wenn die gegebene Anzahl ungültig ist.
	 */
	public void insert(final int index, final int count) throws IndexOutOfBoundsException, IllegalArgumentException;

	/**
	 * Diese Methode entfernt die gegebene Anzahl an Werten ab dem gegebenen Index aus dem Array.
	 * 
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
	 * @throws IllegalArgumentException Wenn die gegebene Anzahl ungültig ist.
	 */
	public void remove(final int index, final int count) throws IndexOutOfBoundsException, IllegalArgumentException;

	/**
	 * Diese Methode gibt eine {@link List} als modifizierbare Sicht auf die Werte zurück.
	 * 
	 * @return modifizierbare {@link List}-Sicht.
	 */
	public List<GValue> values();

	/**
	 * Diese Methode gibt den Abschnitt des intern verwalteten Arrays zurück, in dem sich die Werte dieses {@link Arrays} befinden. 
	 * 
	 * @return mit Werten belegter Abschnitt des intern verwalteten Arrays.
	 */
	public ArraySection<GArray> section();

	/**
	 * Diese Methode gibt ein neues Array mit allen Werten dieses {@link Array}s zurück.
	 * 
	 * @see List#toArray()
	 * @return neues Array.
	 */
	public GArray toArray();

	/**
	 * Diese Methode gibt ein {@link Array} als modifizierbare Sicht auf einen Teil diese {@link Array}s zurück.
	 * 
	 * @see List#subList(int, int)
	 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
	 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
	 * @return modifizierbare Teil-{@link Array}-Sicht.
	 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > size()} oder {@code startIndex > finalIndex}).
	 */
	public Array<GArray, GValue> subArray(int startIndex, int finalIndex) throws IndexOutOfBoundsException;

}