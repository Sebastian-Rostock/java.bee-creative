package bee.creative.util;

import static bee.creative.lang.Objects.hashInit;
import static bee.creative.lang.Objects.hashPush;

/** Diese Klasse implementiert die Zeichenkette als Eingabe eines {@link Parser}.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Source {

	/** Dieses Feld speichert die leere Zeichenkette. */
	public static Source EMPTY = sourceFrom(new char[0]);

	/** Diese Methode liefert eine Zeichenkette mit der gegebenen Zeichen. */
	public static Source sourceFrom(char[] chars) throws NullPointerException {
		return sourceFrom(chars, 0, chars.length);
	}

	/** Diese Methode liefert eine Zeichenkette mit dem Abschnitt der gegebenen Zeichenkette. */
	public static Source sourceFrom(char[] chars, int offset, int length) throws NullPointerException, IllegalArgumentException {
		if (((offset | length) < 0) || (length > (chars.length - offset))) throw new IllegalArgumentException();
		if (length == 0) return EMPTY;
		return new Source(chars, offset, length);
	}

	/** Diese Methode liefert eine Zeichenkette mit der gegebenen Zeichen. */
	public static Source sourceFrom(String string) throws NullPointerException {
		return sourceFrom(string, 0, string.length());
	}

	/** Diese Methode liefert eine Zeichenkette mit dem Abschnitt der gegebenen Zeichenkette. */
	public static Source sourceFrom(String string, int offset, int length) throws NullPointerException, IllegalArgumentException {
		if (((offset | length) < 0) || (length > (string.length() - offset))) throw new IllegalArgumentException();
		if (length == 0) return EMPTY;
		var chars = new char[length];
		string.getChars(offset, offset + length, chars, 0);
		return new Source(chars, 0, length);
	}

	/** Diese Methode liefert die Länge der Zeichenkette. */
	public int length() {
		return this.length;
	}

	/** Diese Methode liefert einen Abschnitt dieser Zeichenkette. */
	public Source section(int offset, int length) throws IllegalArgumentException {
		if (((offset | length) < 0) || (length > (this.length - offset))) throw new IllegalArgumentException();
		return new Source(this.chars, this.offset + offset, length);
	}

	public int minIndexOf(char value, int offset) {
		if ((offset < 0) || (offset >= this.length)) return -1;
		for (var index = offset; index < this.length; index++) {
			if (this.chars[this.offset + index] == value) return index;
		}

		return this.toString().indexOf(value, offset);
	}

	public int maxIndexOf(char value, int offset) {
		if ((offset < 0) || (offset >= this.length)) return -1;
		for (var index = offset; 0 <= index; index--) {
			if (this.chars[this.offset + index] == value) return index;
		}
		return -1;
	}

	@Override
	public int hashCode() {
		var prev = hashInit();
		for (var index = 0; index < this.length; index++) {
			prev = hashPush(prev, this.chars[this.offset + index]);
		}
		return prev;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof Source)) return false;
		var that = (Source)object;
		if (this.length != that.length) return false;
		for (var index = 0; index < this.length; index++) {
			if (this.chars[this.offset + index] != that.chars[that.offset + index]) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return new String(this.chars, this.offset, this.length);
	}

	final char[] chars;

	final int offset;

	final int length;

	private Source(char[] chars, int offset, int length) {
		this.chars = chars;
		this.offset = offset;
		this.length = length;
	}

}