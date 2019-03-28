package bee.creative._deprecated_;

import java.util.Map;
import bee.creative.bind.Field;

/** Diese Schnittstelle definiert einen Adapter zur Modifikation einer {@link Map}, welche über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
 * geschrieben wird. Die Modifikation erfolgt an einer Kopie der {@link Map}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
 * wird.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public interface MapField<GItem, GKey, GValue> extends EntriesField<GItem, GKey, GValue>, Field<GItem, Map<GKey, GValue>> {
}