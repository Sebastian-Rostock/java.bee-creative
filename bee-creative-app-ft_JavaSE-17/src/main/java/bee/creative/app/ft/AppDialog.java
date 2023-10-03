package bee.creative.app.ft;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import bee.creative.lang.Objects;
import bee.creative.util.Consumer;
import bee.creative.util.Entries;
import bee.creative.util.Getter;

class AppDialog {

	public static void wait(Shell shell) {
		var display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public AppDialog(Shell parent) {
		this.parent = parent;
	}

	public AppDialog useSetup(Consumer<AppDialog> setup) {
		setup.set(this);
		return this;
	}

	public AppDialog useFocus(boolean focus) {
		this.focus = focus;
		return this;
	}

	public AppDialog useTitle(String title) {
		this.title = Objects.notNull(title, "");
		return this;
	}

	public AppDialog useTitle(String title, Object... args) {
		return this.useTitle(String.format(title, args));
	}

	public AppDialog useTarget(Control target) {
		this.target = target;
		return this;
	}

	public AppDialog useMessage(String message) {
		this.message = Objects.notNull(message);
		return this;
	}

	public AppDialog useMessage(String message, Object... args) {
		return this.useMessage(String.format(message, args));
	}

	public AppDialog useButton(String string) {
		return this.useButton(string, null);
	}

	/** Diese Methode fügt eine neue Schaltfläche mit der gegebenen Beschriftung an und gibt {@code this} zurück. Bei Betätigung der Schaltfläche wird der Dialog
	 * {@link Shell#dispose() verworfen} und die gegebene Berechnung für eine spätere Auswertung {@link Display#asyncExec(Runnable) registriert}.
	 *
	 * @param text Beschriftung des {@link Button}.
	 * @param onClick Berechnung oder {@code null};
	 * @return {@code this}. */
	public AppDialog useButton(String text, Runnable onClick) {
		this.buttons.add(Entries.from(Objects.notNull(text), onClick));
		return this;
	}

	public AppDialog useOption(String label, Getter<Composite, Control> option) {
		this.options.add(Entries.from(Objects.notNull(label), Objects.notNull(option)));
		return this;
	}

	public AppDialog open() {
		this.close();
		this.createShell();
		this.createTitle();
		this.createMessage();
		this.createOptions();
		this.createButtons();
		this.resizeShell();
		this.locateShell();
		this.shell.setVisible(true);
		if (this.focus) {
			this.shell.setFocus();
		}
		AppDialog.wait(this.shell);
		return this.reset();
	}

	public AppDialog openOnClick(Button target, Consumer<AppDialog> setupOnClick) {
		target.addListener(SWT.Selection, event -> this.reset().useSetup(setupOnClick).useFocus(true).useTarget(null).open());
		return this;
	}

	public AppDialog openOnHover(Control target, Consumer<AppDialog> setupOnHover) {
		target.addListener(SWT.MouseExit, event -> {
			if (this.isActive()) return;
			this.reset();
		});
		target.addListener(SWT.MouseEnter, event -> {
			if (this.isActive()) return;
			this.reset().useSetup(setupOnHover).useFocus(false).useTarget(target).open();
		});
		return this;
	}

	public AppDialog close() {
		if (this.shell == null) return this;
		this.shell.dispose();
		this.shell = null;
		return this;
	}

	public AppDialog reset() {
		this.focus = false;
		this.title = "";
		this.active = false;
		this.target = null;
		this.message = "";
		this.buttons.clear();
		this.options.clear();
		return this.close();
	}

	public boolean isActive() {
		return this.active;
	}

	private final Shell parent;

	private String title = "";

	private String message = "";

	private boolean focus;

	private Control target;

	private final List<Entry<String, Runnable>> buttons = new LinkedList<>();

	private final List<Entry<String, Getter<Composite, Control>>> options = new LinkedList<>();

	private Shell shell;

	/** Dieses Feld speichert nur dann {@code true}, wenn die {@link #shell} aktiviert wurde. */
	private boolean active;

	private void resizeShell() {
		var size = this.shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		this.shell.setSize(this.shell.computeSize(Math.min(size.x, 500), SWT.DEFAULT, true));
		this.shell.layout(true);
	}

	private void locateShell() {
		if (this.target != null) {
			var p = this.target.getBounds();
			var x = this.target.toDisplay(p.width - 5, p.height - 5);
			this.shell.setLocation(x);
		} else {
			var tgtRect = this.parent.getClientArea();
			var sz = this.shell.getSize();
			this.shell.setLocation(this.parent.toDisplay((tgtRect.width - sz.x) / 2, (tgtRect.height - sz.y) / 2));
		}
	}

	private void createShell() {
		var lay = new GridLayout();
		lay.marginWidth = 4;
		lay.marginHeight = 4;
		lay.verticalSpacing = 0;
		lay.horizontalSpacing = 0;
		this.shell = new Shell(this.parent, SWT.NONE);
		this.shell.setLayout(lay);
		this.shell.addListener(SWT.Close, event -> this.reset());
		this.shell.addListener(SWT.Activate, event -> this.active = true);
		this.shell.addListener(SWT.Deactivate, event -> this.reset());
	}

	private void createTitle() {
		if (this.title.isEmpty()) return;
		var res = new Label(this.shell, SWT.WRAP);
		var font = res.getFont().getFontData()[0];
		res.setText(this.title);
		res.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		res.setFont(new Font(this.shell.getDisplay(), new FontData(font.getName(), font.getHeight(), SWT.BOLD)));
	}

	private void createButtons() {
		if (this.buttons.isEmpty()) return;
		var parent = new Composite(this.shell, SWT.NONE);
		parent.setLayout(new GridLayout(this.buttons.size(), true));
		parent.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
		this.buttons.forEach(entry -> {
			var res = new Button(parent, SWT.PUSH);
			res.setText(entry.getKey());
			res.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
			res.addListener(SWT.Selection, event -> {
				this.reset();
				event.display.syncExec(entry.getValue());
			});
			this.shell.setDefaultButton(Objects.notNull(this.shell.getDefaultButton(), res));
		});
	}

	private void createOptions() {
		if (this.options.isEmpty()) return;
		var parent = new Composite(this.shell, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
		this.options.forEach(entry -> {
			new Label(parent, SWT.NONE).setText(entry.getKey());
			var lay = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			lay.widthHint = 250;
			entry.getValue().get(parent).setLayoutData(lay);
		});
	}

	private void createMessage() {
		if (this.message.isEmpty()) return;
		var res = new Label(this.shell, SWT.WRAP);
		res.setText(this.message);
		res.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
	}

}
