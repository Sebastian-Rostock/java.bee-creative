package bee.creative.app.ft;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.JLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import bee.creative.util.Entries;
import bee.creative.util.Getter;

public class FTDialog_SWT {

	public static void main(final String[] args) throws Exception {
		final var display = Display.getDefault();
		final var shell = new Shell(display);
		new FTDialog_SWT() //
			.withTitle("Verzeichnisse aufbereiten") //
			.withMessage("<html><b>Soll das wirklich passieren mid den folgenden Optionen?</b>"
				+ "<br> Soll das <u>wirklich</u> passieren mid den folgenden Optionen? Soll das wirklich passieren mid den folgenden Optionen? </html>") //
			.withButton("Bloß nicht!") //
			.withButton("Ja doch!", () -> System.out.println("JA")) //
			.withOption("abc dsfsdf asdf dsa ", new FTOptionInt(0, 0, 500, 2)) //
			.withOption("def", new FTOptionInt(0, 0, 500, 2)) //
			// .withOption("ghi", new FTTextOption("mal sehen")) //
			.open(shell);
	}

	private Shell shell;

	public void open(final Shell parent) {
		this.shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		this.shell.setText(this.title);
		this.shell.setLayout(new GridLayout());
		this.shell.setMinimumSize(500, 0);
		this.shell.addShellListener(ShellListener.shellClosedAdapter(event -> this.shell.dispose()));
		{
			final var messagePane = new Composite(this.shell, SWT.EMBEDDED);
			messagePane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			final var background = this.shell.getBackground().getRGB();
			final var messageFrame = SWT_AWT.new_Frame(messagePane);
			messageFrame.setBackground(new Color(background.red, background.green, background.blue));
			final var font = this.shell.getFont().getFontData()[0];
			final var foreground = this.shell.getForeground().getRGB();
			final var messageLabel = new JLabel(this.message);
			messageLabel.setFont(new Font(font.getName(), Font.PLAIN, (font.getHeight() * 5) / 4));
			messageLabel.setForeground(new Color(foreground.red, foreground.green, foreground.blue));
			messageFrame.add(messageLabel);
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
					event.display.asyncExec(entry.getValue());
					this.shell.dispose();
				}));
			});
		}
		this.shell.pack();
		this.shell.layout();
		FTWindow_SWT.openAndWait(this.shell);
	}

	public void dispose() {
		if (this.shell != null) {
			this.shell.dispose();
		}
	}

	public FTDialog_SWT withTitle(final String title) {
		this.title = title;
		return this;
	}

	public FTDialog_SWT withMessage(final String message, final Object... args) {
		this.message = String.format(message, args);
		return this;
	}

	public FTDialog_SWT withButton(final String string) {
		return this.withButton(string, null);
	}

	/** Diese Methode fügt eine neue Schaltfläche mit der gegebenen Beschriftung an und gibt {@code this} zurück. Bei Betätigung der Schaltfläche wird der Dialog
	 * {@link Shell#dispose() verworfen} und die gegebene Berechnung für eine spätere Auswertung {@link Display#asyncExec(Runnable) registriert}.
	 *
	 * @param text Beschriftung des {@link Button}.
	 * @param onClick Berechnung oder {@code null};
	 * @return {@code this}. */
	public FTDialog_SWT withButton(final String text, final Runnable onClick) {
		this.buttons.add(Entries.from(text, onClick));
		return this;
	}

	public FTDialog_SWT withOption(final String label, final Getter<Composite, Control> option) {
		this.options.add(Entries.from(label, option));
		return this;
	}

	private String title;

	private String message;

	private final List<Entry<String, Runnable>> buttons = new LinkedList<>();

	private final List<Entry<String, Getter<Composite, Control>>> options = new LinkedList<>();

}
