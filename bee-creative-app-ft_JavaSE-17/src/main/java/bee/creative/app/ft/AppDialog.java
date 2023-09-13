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

// A parent zentriert als nachrcht ohne button

// B an button ecke öffnenn/schließen mit mouse over

// C schließen bei deactivate

class AppDialog {

	public static void main(final String[] args) throws Exception {
		final var display = Display.getDefault();
		final var shell = new Shell(display);
		shell.setSize(200, 200);
		shell.setLayout(new GridLayout());

		var appDialog = new AppDialog(shell);

		var f = new Button(shell, SWT.FLAT);
		appDialog.bind(f, a -> {

		}, a -> {

		});

		f.setText("mal sehen");

		var b = new Button(shell, SWT.FLAT);
 
		b.setText("Test 2");
		appDialog.bind(b, z -> {
			z//
				.useTitle("Dies und Das machen") //
				.useMessage("Dabei wird dies und das gemacht.\n Dies wird vorher gemacht. Das wird nachher gemacht");

		}, z -> {
			z//
				.useTitle("Soll Dies und Das passieren?") //
				.useMessage(
					"Soll das wirklich mit den folgenden Optionen passieren? Soll das wirklich mit den folgenden Optionen passieren? Soll das wirklich mit den folgenden Optionen passieren? ") //
				.useButton("Bloß nicht!") //
				.useButton("Ja doch!", () -> System.out.println("JA")) //
				.useOption("abc dsfsdf asdf dsa ", new AppOptionLong().useMinimum(0).useMaximum(1<<20).useIncrease(1<<10)) //
				.useOption("def", new AppOptionTime()) //
				.useOption("def", new AppOptionText().useValue("lala")) //
 ;
		});

		FTWindow.openAndWait(shell);

	}

	void bind(Button target, Consumer<AppDialog> hover, Consumer<AppDialog> click) {
		this.openOnHover(target, hover);
		this.openOnClick(target, click);
	}

	public AppDialog(final Shell parent) {
		this.parent = parent;
	}

	void resizeShell() {
		var a = this.shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		this.shell.setSize(this.shell.computeSize(Math.min(a.x, 500), SWT.DEFAULT, true));
		this.shell.layout(true);
	}

	private void locateShell() {
		if (this.target != null) {

			var p = this.target.getBounds();
			var x = this.target.toDisplay(p.width - 5, p.height - 5);

			this.shell.setLocation(x);
		} else {
			final var tgtRect = this.parent.getSize();
			var sz = this.shell.getSize();
			this.shell.setLocation(this.parent.toDisplay((tgtRect.x - sz.x) / 2, (tgtRect.y - sz.y) / 2));

		}
	}

	private void createShell() {
		final var lay = new GridLayout();
		lay.marginWidth = 4;
		lay.marginHeight = 4;
		lay.verticalSpacing = 0;
		lay.horizontalSpacing = 0;
		this.shell = new Shell(this.parent, SWT.NONE);
		this.shell.setLayout(lay);
		this.shell.addListener(SWT.Close, event -> this.clear());
		this.shell.addListener(SWT.Activate, event -> this.active = true);
		this.shell.addListener(SWT.Deactivate, event -> this.clear());
	}

	void createTitle() {
		if (this.title.isEmpty()) return;
		final var titleLabel = new Label(this.shell, SWT.WRAP);
		final var font = titleLabel.getFont().getFontData()[0];
		titleLabel.setText(this.title);
		titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		titleLabel.setFont(new Font(this.shell.getDisplay(), new FontData(font.getName(), font.getHeight(), SWT.BOLD)));
	}

	void createButtons() {
		if (this.buttons.isEmpty()) return;
		final var buttonPane = new Composite(this.shell, SWT.NONE);
		buttonPane.setLayout(new GridLayout(this.buttons.size(), true));
		buttonPane.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
		this.buttons.forEach(entry -> {
			final var res = new Button(buttonPane, SWT.PUSH);
			res.setText(entry.getKey());
			res.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
			res.addListener(SWT.Selection, event -> {
				this.clear();
				event.display.syncExec(entry.getValue());
			});
			this.shell.setDefaultButton(Objects.notNull(this.shell.getDefaultButton(), res));
		});
	}

	void createOptions() {
		if (this.options.isEmpty()) return;
		final var optionPane = new Composite(this.shell, SWT.NONE);
		optionPane.setLayout(new GridLayout(2, false));
		optionPane.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
		this.options.forEach(entry -> {
			final var lbl = new Label(optionPane, SWT.NONE);
			lbl.setText(entry.getKey());
			final var layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			layoutData.widthHint = 250;
			entry.getValue().get(optionPane).setLayoutData(layoutData);
		});
	}

	void createMessage() {
		if (this.message.isEmpty()) return;
		final var messageLabel = new Label(this.shell, SWT.WRAP);
		messageLabel.setText(this.message);
		messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
	}

	public AppDialog useFocus(boolean focus) {
		this.focus = focus;
		return this;
	}

	public AppDialog useTarget(Control target) {
		this.target = target;
		return this;
	}

	public AppDialog useSetup(Consumer<AppDialog> setup) {
		setup.set(this);
		return this;
	}

	public AppDialog useTitle(final String title) {
		this.title = Objects.notNull(title, "");
		return this;
	}

	public AppDialog useTitle(final String title, final Object... args) {
		return this.useTitle(String.format(title, args));
	}

	public AppDialog useMessage(final String message) {
		this.message = Objects.notNull(message);
		return this;
	}

	public AppDialog useMessage(final String message, final Object... args) {
		return this.useMessage(String.format(message, args));
	}

	public AppDialog useButton(final String string) {
		return this.useButton(string, null);
	}

	/** Diese Methode fügt eine neue Schaltfläche mit der gegebenen Beschriftung an und gibt {@code this} zurück. Bei Betätigung der Schaltfläche wird der Dialog
	 * {@link Shell#dispose() verworfen} und die gegebene Berechnung für eine spätere Auswertung {@link Display#asyncExec(Runnable) registriert}.
	 *
	 * @param text Beschriftung des {@link Button}.
	 * @param onClick Berechnung oder {@code null};
	 * @return {@code this}. */
	public AppDialog useButton(final String text, final Runnable onClick) {
		this.buttons.add(Entries.from(Objects.notNull(text), onClick));
		return this;
	}

	public AppDialog useOption(final String label, final Getter<Composite, Control> option) {
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
		FTWindow.wait(this.shell);
		return this.clear();
	}

	public AppDialog openOnClick(Button target, Consumer<AppDialog> onClick) {
		target.addListener(SWT.Selection, event -> this.clear().useSetup(onClick).useFocus(true).useTarget(null).open());
		return this;
	}

	public AppDialog openOnHover(Control target, Consumer<AppDialog> onHover) {
		target.addListener(SWT.MouseExit, event -> {
			if (this.isActive()) return;
			this.clear();
		});
		target.addListener(SWT.MouseEnter, event -> {
			if (this.isActive()) return;
			this.clear().useSetup(onHover).useFocus(false).useTarget(target).open();
		});
		return this;
	}

	public AppDialog close() {
		if (this.shell == null) return this;
		this.shell.dispose();
		this.shell = null;
		return this;
	}

	public AppDialog clear() {
		this.title = "";
		this.message = "";
		this.buttons.clear();
		this.options.clear();
		this.focus = false;
		this.active = false;
		this.target = null;
		return this.close();
	}

	public boolean isActive() {
		return this.active;
	}

	private final Shell parent;

	private Shell shell;

	private String title = "";

	private String message = "";

	private boolean focus = false;

	private Control target;

	private final List<Entry<String, Runnable>> buttons = new LinkedList<>();

	private final List<Entry<String, Getter<Composite, Control>>> options = new LinkedList<>();

	/** Dieses Feld speichert nur dann {@code true}, wenn die {@link #shell} aktiviert wurde. */
	boolean active = false;

}
