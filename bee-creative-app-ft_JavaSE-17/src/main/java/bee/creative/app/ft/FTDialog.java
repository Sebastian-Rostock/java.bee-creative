package bee.creative.app.ft;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import bee.creative.lang.Objects;
import bee.creative.util.Producer;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FTDialog extends JDialog {

	public static void main(final String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new FTDialog() //
			.withTitle("Verzeichnisse aufbereiten") //
			.withMessage(
				"<html><b>Soll das wirklich passieren mid den folgenden Optionen?</b><br> Soll das wirklich passieren mid den folgenden Optionen? Soll das wirklich passieren mid den folgenden Optionen? </html>") //
			.withButton("Bloß nicht!") //
			.withButton("Ja doch!", () -> System.out.println("JA")) //
			.withOption("abc", new FTLongOption(0, 0, 500, 2)) //
			.withOption("def", new FTLongOption(0, 0, 500, 2)) //
			.withOption("ghi", new FTTextOption("mal sehen")) //
			.open();
	}

	public FTDialog() {
		this.setResizable(false);
		this.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new GridBagLayout());

		final GridBagConstraints contentPanelCon = new GridBagConstraints();
		contentPanelCon.fill = GridBagConstraints.HORIZONTAL;
		contentPanelCon.gridy = 0;
		contentPanelCon.gridx = 0;
		contentPanelCon.anchor = GridBagConstraints.NORTH;
		contentPanelCon.weightx = 1.0;
		this.contentPanel = new JPanel(new GridBagLayout());
		this.getContentPane().add(this.contentPanel, contentPanelCon);

		final GridBagConstraints messageLabelCon = new GridBagConstraints();
		messageLabelCon.fill = GridBagConstraints.BOTH;
		messageLabelCon.gridx = 0;
		messageLabelCon.gridy = 0;
		messageLabelCon.anchor = GridBagConstraints.NORTHWEST;
		messageLabelCon.weighty = 0.0;
		messageLabelCon.weightx = 1.0;
		messageLabelCon.insets = new Insets(16, 16, 8, 16);
		this.messageLabel = new JLabel();
		this.contentPanel.add(this.messageLabel, messageLabelCon);

		final GridBagConstraints centerPanelCon = new GridBagConstraints();
		centerPanelCon.weighty = 1.0;
		centerPanelCon.insets = new Insets(5, 5, 5, 5);
		centerPanelCon.gridx = 0;
		centerPanelCon.gridy = 1;
		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		this.contentPanel.add(centerPanel, centerPanelCon);

		final GridBagLayout optionPanelLay = new GridBagLayout();
		optionPanelLay.columnWidths = new int[]{0, 128};
		final GridBagConstraints optionPanelCon = new GridBagConstraints();
		optionPanelCon.fill = GridBagConstraints.VERTICAL;
		optionPanelCon.gridx = 1;
		optionPanelCon.gridy = 0;
		this.optionPanel = new JPanel();
		this.optionPanel.setLayout(optionPanelLay);
		centerPanel.add(this.optionPanel, optionPanelCon);

		final GridBagConstraints buttonPanelCon = new GridBagConstraints();
		buttonPanelCon.weighty = 0.0;
		buttonPanelCon.insets = new Insets(5, 5, 5, 5);
		buttonPanelCon.gridx = 0;
		buttonPanelCon.gridy = 2;
		this.buttonPanel = new JPanel();
		this.buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.contentPanel.add(this.buttonPanel, buttonPanelCon);

		this.setPreferredSize(new Dimension(500, 600));
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent event) {
				final FTDialog thiz = FTDialog.this;
				thiz.setSize(thiz.getWidth(), (thiz.contentPanel.getHeight() + thiz.getHeight()) - thiz.getContentPane().getHeight());
				thiz.setLocationRelativeTo(null);
			}

		});

		this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE_KEY");
		this.getRootPane().getActionMap().put("ESCAPE_KEY", new AbstractAction() {

			private static final long serialVersionUID = -645686117710964112L;

			@Override
			public void actionPerformed(final ActionEvent ae) {
				FTDialog.this.dispose();
			}

		});
	}

	public void open() {
		this.pack();
		this.setVisible(true);
	}

	public FTDialog withTitle(final String title) {
		this.setTitle(title);
		return this;
	}

	public FTDialog withMessage(final String message, final Object... args) {
		this.messageLabel.setText(String.format(message, args));
		return this;
	}

	public FTDialog withButton(final String string) {
		return this.withButton(string, null);
	}

	/** Diese Methode fügt eine neue Schaltfläche mit der gegebenen Beschriftung an und gibt {@code this} zurück. Bei Betätigung der Schaltfläche wird der Dialog
	 * {@link #dispose() verworfen} und die gegebene Berechnung für eine spätere Auswertung {@link EventQueue#invokeLater(Runnable) registriert}. Letzteres
	 * entfällt, wenn die Berechnung {@code null} ist.
	 *
	 * @param text Beschriftung des {@link JButton}.
	 * @param onClick Berechnung oder {@code null};
	 * @return {@code this}. */
	public FTDialog withButton(final String text, final Runnable onClick) {
		final JButton button = new JButton(text);
		this.buttonPanel.add(button);
		button.addActionListener(event -> {
			this.dispose();
			EventQueue.invokeLater(Objects.notNull(onClick, FTDialog.noClick));
		});
		return this;
	}

	public FTDialog withOption(final String label, final Producer<? extends Component> option) {
		return this.withOption(new JLabel(label), option.get());
	}

	public FTDialog withOption(final Component label, final Component control) {
		final int index = this.optionPanel.getComponentCount() / 2;
		final Insets insets = new Insets(3, 3, 3, 3);
		this.optionPanel.add(label, new GridBagConstraints(0, index, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		this.optionPanel.add(control, new GridBagConstraints(1, index, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		return this;
	}

	final JLabel messageLabel;

	final JPanel optionPanel;

	final JPanel buttonPanel;

	final JPanel contentPanel;

	private static Runnable noClick = () -> {};

	private static final long serialVersionUID = -765132423276005705L;

}
