package bee.creative.app.ft;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellListener;
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
import bee.creative.util.Entries;
import bee.creative.util.Getter;

class FTDialog {

	public static void main(final String[] args) throws Exception {
		final var display = Display.getDefault();
		final var shell = new Shell(display);
		new FTDialog(shell) //
			.withText("Verzeichnisse aufbereiten") //
			.withTitle("Soll Dies und Das passieren?") //
			.withMessage(
				"Soll das wirklich mit den folgenden Optionen passieren? Soll das wirklich mit den folgenden Optionen passieren? Soll das wirklich mit den folgenden Optionen passieren? ") //
			.withButton("Bloß nicht!") //
			.withButton("Ja doch!", () -> System.out.println("JA")) //
			.withOption("abc dsfsdf asdf dsa ", new FTOptionInt(0, 0, 500, 2)) //
			.withOption("def", new FTOptionInt(0, 0, 500, 2)) //
			.withOption("ghi", new FTOptionStr("mal sehen")) //
			.open();
	}

	public FTDialog(final Shell parent) {
		this.parent = parent;
	}

	public void open() {
		this.parent.getDisplay().asyncExec(() -> {
			this.dispose();
			{
				final var lay = new GridLayout();
				this.shell = new Shell(this.parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
				this.shell.setText(Objects.notNull(this.text, ""));
				lay.marginWidth = 10;
				lay.marginHeight = 10;
				lay.verticalSpacing = 0;
				lay.horizontalSpacing = 0;
				this.shell.setLayout(lay);
				this.shell.addShellListener(ShellListener.shellClosedAdapter(event -> this.shell.dispose()));
			}
			{
				final var titleLabel = new Label(this.shell, SWT.WRAP);
				final var font = titleLabel.getFont().getFontData()[0];
				titleLabel.setText(this.title);
				titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
				titleLabel.setFont(new Font(this.shell.getDisplay(), new FontData(font.getName(), font.getHeight(), SWT.BOLD)));
			}
			{
				final var messageLabel = new Label(this.shell, SWT.WRAP);
				messageLabel.setText(this.message);
				messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
				System.out.println(messageLabel);
			}
			{
				final var optionaPane = new Composite(this.shell, SWT.NONE);
				optionaPane.setLayout(new GridLayout(2, false));
				optionaPane.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
				this.options.forEach(entry -> {
					final var lbl = new Label(optionaPane, SWT.NONE);
					lbl.setText(entry.getKey());
					final var layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
					layoutData.widthHint = 128;
					entry.getValue().get(optionaPane).setLayoutData(layoutData);
				});
			}
			{
				final var buttonPane = new Composite(this.shell, SWT.NONE);
				buttonPane.setLayout(new GridLayout(this.buttons.size(), true));
				buttonPane.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
				this.buttons.forEach(entry -> {
					final var res = new Button(buttonPane, SWT.PUSH);
					res.setText(entry.getKey());
					res.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
					res.addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> {
						this.shell.dispose();
						event.display.syncExec(entry.getValue());
					}));
					this.shell.setDefaultButton(Objects.notNull(this.shell.getDefaultButton(), res));
				});
			}
			{
				this.shell.setSize(this.shell.computeSize(500, SWT.DEFAULT, true));
				this.shell.layout(true);
				FTWindow.center(this.shell);
				FTWindow.openAndWait(this.shell);
			}
		});
	}

	public void dispose() {
		if (this.shell != null) {
			this.shell.dispose();
			this.shell = null;
		}
	}

	public FTDialog withText(final String text) {
		this.text = Objects.notNull(text);
		return this;
	}

	public FTDialog withTitle(final String title) {
		this.title = Objects.notNull(title);
		return this;
	}

	public FTDialog withMessage(final String message) {
		this.message = Objects.notNull(message);
		return this;
	}

	public FTDialog withMessage(final String message, final Object... args) {
		return this.withMessage(String.format(message, args));
	}

	public FTDialog withButton(final String string) {
		return this.withButton(string, null);
	}

	/** Diese Methode fügt eine neue Schaltfläche mit der gegebenen Beschriftung an und gibt {@code this} zurück. Bei Betätigung der Schaltfläche wird der Dialog
	 * {@link Shell#dispose() verworfen} und die gegebene Berechnung für eine spätere Auswertung {@link Display#asyncExec(Runnable) registriert}.
	 *
	 * @param text Beschriftung des {@link Button}.
	 * @param onClick Berechnung oder {@code null};
	 * @return {@code this}. */
	public FTDialog withButton(final String text, final Runnable onClick) {
		this.buttons.add(Entries.from(Objects.notNull(text), onClick));
		return this;
	}

	public FTDialog withoutButtons() {
		this.buttons.clear();
		return this;
	}

	public FTDialog withOption(final String label, final Getter<Composite, Control> option) {
		this.options.add(Entries.from(Objects.notNull(label), Objects.notNull(option)));
		return this;
	}

	public FTDialog withoutOptions() {
		this.options.clear();
		return this;
	}

	private final Shell parent;

	private Shell shell;

	private String text = "";

	private String title = "";

	private String message = "";

	private final List<Entry<String, Runnable>> buttons = new LinkedList<>();

	private final List<Entry<String, Getter<Composite, Control>>> options = new LinkedList<>();

}
