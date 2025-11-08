package bee.creative.util;

import static bee.creative.util.Entries.entryFrom;
import static bee.creative.util.Properties.propertyFrom;
import java.util.Map.Entry;

/** Diese Schnittstelle ergänzt einen {@link Entry} insb. um eine Anbindung an Methoden von {@link Entries}.
 *
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <K> Typ des Schlüssels.
 * @param <V> Typ des Werts. */
public interface Entry3<K, V> extends Entry<K, V> {

	/** Diese Methode liefert das {@link Property3} zu {@link #getKey()} und {@link #setKey(Object)}. */
	default Property3<K> key() {
		return propertyFrom(this::getKey, this::setKey);
	}

	/** Diese Methode liefert das {@link Property3} zu {@link #getValue()} und {@link #setValue(Object)}. */
	default Property3<V> value() {
		return propertyFrom(this::getValue, this::setValue);
	}

	/** Diese Methode setzt den Schlüssel. Wenn dies nicht möglich ist, wird eine {@link UnsupportedOperationException} ausgelöst. */
	default K setKey(K key) {
		var result = this.getKey();
		this.useKey(key);
		return result;
	}

	/** Diese Methode setzt den Wert. Wenn dies nicht möglich ist, wird eine {@link UnsupportedOperationException} ausgelöst. */
	@Override
	default V setValue(V value) {
		var result = this.getValue();
		this.useValue(value);
		return result;
	}

	/** Diese Methode setzt den {@link #getKey() Schlüssel} und gibt {@code this} zurück. */
	default Entry3<K, V> useKey(K key) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/** Diese Methode setzt den {@link #getValue() Wert} und gibt {@code this} zurück. */
	default Entry3<K, V> useValue(V value) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/** Diese Methode liefert ein umkehrendes {@link Entry3}, welches Schlüssel und Wert dieses {@link Entry3} miteinander tauscht. Sie ist eine Abkürzung für
	 * {@link Entries#entryFrom(Property, Property) entryFrom(this.value(), this.key())}. */
	default Entry3<V, K> reverse() {
		return entryFrom(this.value(), this.key());
	}

	/** Diese Methode liefert ein übersetzendes {@link Entry3}, welches Schlüssel und Wert dieses {@link Entry3} mit den gegebenen {@link Translator} übersetzt.
	 * Sie ist eine Abkürzung für {@link Entries#entryFrom(Property, Property) entryFrom(this.key().translate(keyTrans), this.value().translate(valueTrans))}. */
	default <K2, V2> Entry3<K2, V2> translate(Translator<K, K2> keyTrans, Translator<V, V2> valueTrans) {
		return entryFrom(this.key().translate(keyTrans), this.value().translate(valueTrans));
	}

}
