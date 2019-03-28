package bee.creative._deprecated_;

import java.util.Set;
import bee.creative.bind.Field;

/** Diese Schnittstelle definiert einen Adapter zur Modifikation eines {@link Set}, welches über ein {@link Field} einer gegebenen Eingabe gelesen bzw.
 * geschrieben wird. Die Modifikation erfolgt an einer Kopie des {@link Set}, welche nach ihrer Modifikation über {@link #set(Object, Object)} zugewiesen
 * wird.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GEntry> Typ der Elemente. */
public interface SetField<GItem, GEntry> extends ItemsField<GItem, GEntry>, Field<GItem, Set<GEntry>> {
}