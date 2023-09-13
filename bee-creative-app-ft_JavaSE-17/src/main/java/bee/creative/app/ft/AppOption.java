package bee.creative.app.ft;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import bee.creative.util.Getter;
import bee.creative.util.Property;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface AppOption extends Getter<Composite, Control>, Property<String> {

	/** Diese Methode liefert die Textdarstellung zum Speichern des Werts. */
	@Override
	String get();

	/** Diese Methode übernimmt die gegebene Textdarstellung beim Laden des Werts. */
	@Override
	void set(String value);

	/** Diese Methode erzeugt das Steuerelement zur Änderung des Werts. */
	@Override
	Control get(Composite parent);

}
