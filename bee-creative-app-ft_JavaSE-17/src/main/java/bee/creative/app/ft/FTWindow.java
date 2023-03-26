package bee.creative.app.ft;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import bee.creative.lang.Objects;
import bee.creative.lang.Runnable2;
import bee.creative.lang.Strings;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class FTWindow extends JFrame {

	private static final long serialVersionUID = -7101649509849692992L;

	public static void main(final String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new FTWindow().setVisible(true);
	}

	FTWindow() {
		this.setTitle("File-Tool");
		this.setMinimumSize(new Dimension(400, 200));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setSize(new Dimension(600, 400));
		final JTabbedPane pagePanel = new JTabbedPane(SwingConstants.TOP);
		{
			final JPanel inputPage = new JPanel();
			inputPage.setBackground(UIManager.getColor("TabbedPane.highlight"));
			inputPage.setLayout(new GridBagLayout());
			pagePanel.addTab("Pfadliste (Eingabepfade)", null, inputPage, null);
			{
				this.inputMenu = this.createInputMenu();
				this.inputArea = this.createInputArea();
			}
			{
				final JScrollPane inputPanel = new JScrollPane(this.inputArea);
				inputPanel.setColumnHeaderView(this.inputMenu);
				inputPage.add(inputPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
			}
			{
				final JLabel inputLabel = this.createInputLabel();
				inputPage.add(inputLabel, new GridBagConstraints(0, 1, 1, 1, 1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 3, 3, 3), 0, 0));
			}
		}
		{
			final JPanel tablePage = new JPanel();
			tablePage.setBackground(UIManager.getColor("TabbedPane.highlight"));
			tablePage.setLayout(new GridBagLayout());
			pagePanel.addTab("Pfadtabelle (Quell- und Zielpfade)", null, tablePage, null);
			{
				this.tableMenu = this.createTableMenu();
				this.tableArea = this.createTableArea();
			}
			{
				final JScrollPane targetPanel = new JScrollPane(this.tableArea);
				targetPanel.setColumnHeaderView(this.tableMenu);
				tablePage.add(targetPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
			}
			{
				final JLabel inputLabel = this.createTableLabel();
				tablePage.add(inputLabel, new GridBagConstraints(0, 1, 1, 1, 1, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 3, 3, 3), 0, 0));
			}
		}
		{
			this.taskInfo = new JLabel();
			this.taskStop = new JButton("abbrechen");
			this.taskStop.addActionListener(this::cancelProcess);
		}
		final Container contentPane = this.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		contentPane.add(pagePanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
		contentPane.add(this.taskInfo,
			new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 3, 3, 3), 0, 0));
		contentPane.add(this.taskStop,
			new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 3, 3), 0, 0));
		this.execUpdateEnabled();
		this.setLocationRelativeTo(null);
		new Timer(500, this::updateTask).start();
		this.settings.restore();
		this.settings.persist();
	}

	private JTextArea inputArea;

	private JMenuBar inputMenu;

	private String getInputText() {
		return this.inputArea.getText();
	}

	private void setInputText(final String inputText) {
		this.runLater(() -> this.inputArea.setText(inputText));
	}

	@SuppressWarnings ("serial")
	private JTextArea createInputArea() {
		final JTextArea res = new JTextArea();
		new DropTarget(res, new DropTargetAdapter() {

			@Override
			public void drop(final DropTargetDropEvent event) {
				FTWindow.this.setupImportInputs(event);
			}

		});
		res.getActionMap().put("paste-from-clipboard", new AbstractAction() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				FTWindow.this.setupImportInputs(event);
			}

		});
		return res;
	}

	private JLabel createInputLabel() {
		return new JLabel("" + //
			"<html>" + //
			"Dateien und Verzeichnisse können hier aus der Zwischenablage eingefügt bzw. aus dem Dateiexplorer fallengelassen werden." + //
			"</html>" //
		);
	}

	private JMenuBar createInputMenu() {
		return this.createMenuBar( //
			this.createMenu( //
				"Eingabepfade", //
				this.createMenuItem("" + //
					"<html>" + //
					"Fehlerpfade erhalten...<br>" + //
					"<i>&nbsp;&nbsp;Alle Eingabepfade zu existierenden Dateien bzw. Verzeichnissen werden verworfen.</i>" + //
					"</html>", //
					this::cleanupExistingInputs //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Fehlerpfade entfernen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Eingabepfade zu nicht existierenden Dateien bzw. Verzeichnissen werden verworfen.</i>" + //
					"</html>", //
					this::cleanupMissingInputs //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Dateien auflösen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Verzeichnispfade werden durch alle rekursiv darin enthaltenen Dateipfade ersetzt.</i>" + //
					"</html>", //
					this::resolveInputToFiles //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Verzeichnisse auflösen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Verzeichnispfade werden um alle rekursiv darin enthaltenen Verzeichnispfade ergänzt.</i>" + //
					"</html>", //
					this::resolveInputToFolders //
				), //
				this.createMenuBreak(), //
				this.createMenuItem("" + //
					"<html>" + //
					"Eingabepfade übertragen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Eingabepfade werden in die Pfadtabelle übertragen.</i>" + //
					"</html>", //
					this::transferInputs //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Eingabepfade exportieren<br>" + //
					"<i>&nbsp;&nbsp;Alle Eingabepfade werden in die Zwischenablage kopiert.</i>" + //
					"</html>", //
					this::exportInputs //
				) //
			), //
			this.createMenu( //
				"Dateien", //
				this.createMenuItem("" + //
					"<html>" + //
					"Dateien löschen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Dateien werden endgültig gelöscht.</i>" + //
					"</html>", //
					this::deleteInputFilesPermanently //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Dateien recyceln...<br>" + //
					"<i>&nbsp;&nbsp;Alle Dateien werden in den Papierkorb verschoben.</i>" + //
					"</html>", //
					this::deleteInputFilesTemporary //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Dateien erneuern...<br>" + //
					"<i>&nbsp;&nbsp;Alle alten Dateien werden kopiert und durch ihre Kopien ersetzt." + //
					"</html>", //
					this::refreshInputFiles //
				), //
				this.createMenuBreak(), //
				this.createMenuItem("" + //
					"<html>" + //
					"Dateiduplikate übertragen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Datenpfade zu inhaltlich gleichen Dateien werden in die Pfadtabelle übertragen." + //
					"</html>", //
					this::createTableWithClones //
				) //
			), //
			this.createMenu( //
				"Verzeichnisse", //
				this.createMenuItem("" + //
					"<html>" + //
					"Verzeichnisse löschen...<br>" + //
					"<i>&nbsp;&nbsp;Alle leeren Verzeichnisse werden endgültig gelöscht.</i>" + //
					"</html>", //
					this::deleteInputFoldersPermanently //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Verzeichnisse recyceln...<br>" + //
					"<i>&nbsp;&nbsp;Alle leeren Verzeichnisse werden in den Papierkorb verschoben.</i>" + //
					"</html>", //
					this::deleteInputFoldersTemporary //
				) //
			) //
		);
	}

	private JTextArea tableArea;

	private JMenuBar tableMenu;

	private String getTableText() {
		return this.tableArea.getText();
	}

	private void setTableText(final String tableText) {
		this.runLater(() -> this.tableArea.setText(tableText));
	}

	private JTextArea createTableArea() {
		return new JTextArea();
	}

	private JLabel createTableLabel() {
		return new JLabel("" + //
			"<html>" + //
			"Jede Zeile besteht mindestens aus einem Quell- und einen Zieldatenpfad. Alle Werte einer Zeile werden mit Tabulatoren getrennt." + //
			"</html>" //
		);
	}

	private JMenuBar createTableMenu() {
		return this.createMenuBar( //
			this.createMenu( //
				"Quellpfade", //
				this.createMenuItem("" + //
					"<html>" + //
					"Fehlerpfade erhalten...<br>" + //
					"<i>&nbsp;&nbsp;Alle Quellpfade zu existierenden Dateien bzw. Verzeichnissen werden verworfen.</i>" + //
					"</html>", //
					this::cleanupExistingSources //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Fehlerpfade entfernen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Quellpfade zu nicht existierenden Dateien bzw. Verzeichnissen werden verworfen.</i>" + //
					"</html>", //
					this::cleanupMissingSources //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Quellpfade ersetzen...<br>" + //
					"<i>&nbsp; Alle Quellpfade werden durch deren Zielpfaden ersetzt.<i>" + //
					"</html>", //
					this::replaceSourcesWithTargets //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Quellpfade tauschen...<br>" + //
					"<i>&nbsp; Alle Quell- und Zielpfade werden miteinander getauscht.<i>" + //
					"</html>", //
					this::exchangeSourcesWithTargets //
				), //
				this.createMenuBreak(), //
				this.createMenuItem("" + //
					"<html>" + //
					"Quellpfade übertragen...<br>" + //
					"<i>&nbsp; Alle Eingabepfade werden mit allen Quellpfaden ersetzt.<i>" + //
					"</html>", //
					this::transferSources //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Quellpfade exportieren<br>" + //
					"<i>&nbsp;&nbsp;Alle Quellpfade werden in die Zwischenablage kopiert.</i>" + //
					"</html>", //
					this::exportSources //
				)//
			), //

			this.createMenu( //
				"Zielpfade", //
				this.createMenuItem("" + //
					"<html>" + //
					"Fehlerpfade erhalten...<br>" + //
					"<i>&nbsp;&nbsp;Alle Zielpfade zu existierenden Dateien bzw. Verzeichnissen werden verworfen.</i>" + //
					"</html>", //
					this::cleanupExistingTargets //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Fehlerpfade entfernen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Zielpfade zu nicht existierenden Dateien bzw. Verzeichnissen werden verworfen.</i>" + //
					"</html>", //
					this::cleanupMissingTargets //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zielpfade ersetzen...<br>" + //
					"<i>&nbsp; Alle Zielpfade werden durch deren Quellpfaden ersetzt.<i>" + //
					"</html>", //
					this::replaceTargetsWithSources //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zielpfade tauschen...<br>" + //
					"<i>&nbsp; Alle Ziel- und Quellpfade werden miteinander getauscht.<i>" + //
					"</html>", //
					this::exchangeTargetsWithSources //
				), //
				this.createMenuBreak(), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zielpfade übertragen...<br>" + //
					"<i>&nbsp; Alle Eingabepfade werden mit allen Zielpfaden ersetzt.<i>" + //
					"</html>", //
					this::transferTargets //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zielpfade exportieren<br>" + //
					"<i>&nbsp;&nbsp;Alle Zielpfade werden in die Zwischenablage kopiert.</i>" + //
					"</html>", //
					this::exportTargets //
				), //
				this.createMenuBreak(), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zeitnamen ableiten... (Name)<br>" + //
					"<i>&nbsp;&nbsp;Alle Zieldateinamen werden aus dem Zeitpunkt im Quellnamen abgeleitet.</i>" + //
					"</html>", //
					this::createTargetsWithTimenameFromName //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zeitnamen ableiten... (Zeit)<br>" + //
					"<i>&nbsp;&nbsp;Alle Zieldateinamen werden aus dem Änderungszeitpunkt der Quelldatei abgeleitet.</i>" + //
					"</html>", //
					this::createTargetsWithTimenameFromTime //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zeitpfade ableiten... (Name)<br>" + //
					"<i>&nbsp;&nbsp;Alle Zieldateipfade werden aus dem Zeitpunkt im Quellnamen abgeleitet.</i>" + //
					"</html>", //
					this::createTargetsWithTimepathFromName //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zeitpfade ableiten... (Zeit)<br>" + //
					"<i>&nbsp;&nbsp;Alle Zieldateipfade werden aus ddem Änderungszeitpunkt der Quelldatei abgeleitet.</i>" + //
					"</html>", //
					this::createTargetsWithTimepathFromTime //
				) //
			), //
			this.createMenu(//
				"Dateien", //
				this.createMenuItem("" + //
					"<html>" + //
					"Quelldateien löschen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Quelldateien werden endgültig gelöscht.</i>" + //
					"</html>", //
					this::deleteSourceFilesPermanently //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Quelldateien recyceln...<br>" + //
					"<i>&nbsp;&nbsp;Alle Quelldateien werden in den Papierkorb verschoben.</i>" + //
					"</html>", //
					this::deleteSourceFilesTemporary //
				), //
				this.createMenuBreak(), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zieldateien löschen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Zieldateien werden endgültig gelöscht.</i>" + //
					"</html>", //
					this::deleteTargetFilesPermanently //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zieldateien recyceln...<br>" + //
					"<i>&nbsp;&nbsp;Alle Zieldateien werden in den Papierkorb verschoben.</i>" + //
					"</html>", //
					this::deleteTargetFilesTemporary //
				), //
				this.createMenuBreak(), //
				this.createMenuItem("" + //
					"<html>" + //
					"Dateien anzeigen...<br>" + //
					"<i>&nbsp;&nbsp;Alle Dateien werden zur Anzeige über Symlinks in ein temporäres Verteichnis eingefügt.</i>" + //
					"</html>", //
					this::showSourceAndTargetFiles //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Dateien kopieren...<br>" + //
					"<i>&nbsp;&nbsp;Alle in den Quellpfaden genannten Dateien werden in ihren Zielpfad kopiert.</i>" + //
					"</html>", //
					this::copySourceToTargetFiles //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Dateien verschieben...<br>" + //
					"<i>&nbsp;&nbsp;Alle in den Quellpfaden genannten Dateien werden in ihren Zielpfad verschoben.</i>" + //
					"</html>", //
					this::moveSourceToTargetFiles //
				) //
			), //
			this.createMenu(//
				"Verzeichnisse", //
				this.createMenuItem("" + //
					"<html>" + //
					"Quellverzeichnisse löschen...<br>" + //
					"<i>&nbsp;&nbsp;Alle leeren Quellverzeichnisse werden endgültig gelöscht.</i>" + //
					"</html>", //
					this::deleteSourceFoldersPermanently //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Quellverzeichnisse recyceln...<br>" + //
					"<i>&nbsp;&nbsp;Alle leeren Quellverzeichnisse werden in den Papierkorb verschoben.</i>" + //
					"</html>", //
					this::deleteSourceFoldersTemporary //
				), //
				this.createMenuBreak(), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zielverzeichnisse löschen...<br>" + //
					"<i>&nbsp;&nbsp;Alle leeren Zielverzeichnisse werden endgültig gelöscht.</i>" + //
					"</html>", //
					this::deleteTargetFoldersPermanently //
				), //
				this.createMenuItem("" + //
					"<html>" + //
					"Zielverzeichnisse recyceln...<br>" + //
					"<i>&nbsp;&nbsp;Alle leeren Zielverzeichnisse werden in den Papierkorb verschoben.</i>" + //
					"</html>", //
					this::deleteTargetFoldersTemporary //
				) //
			) //
		);
	}

	private JMenu createMenu(final String text, final Component... items) {
		final JMenu result = new JMenu(text);
		Iterators.fromArray(items).collectAll(result::add);
		return result;
	}

	private JMenuBar createMenuBar(final JMenu... menus) {
		final JMenuBar result = new JMenuBar();
		Iterators.fromArray(menus).collectAll(result::add);
		return result;
	}

	private JMenuItem createMenuItem(final String text, final Runnable onClick) {
		final JMenuItem result = new JMenuItem(text);
		result.addActionListener(event -> onClick.run());
		return result;
	}

	private JSeparator createMenuBreak() {
		return new JSeparator();
	}

	public final FTSettings settings = new FTSettings();

	private FTDialog createDialog() {
		final FTDialog res = new FTDialog();
		this.runLater(res::open);
		return res;
	}

	JLabel taskInfo;

	JButton taskStop;

	String taskTitle;

	Object taskEntry;

	int taskCount;

	FTDialog taskCancel;

	boolean isTaskRunning;

	boolean isTaskCanceled;

	/** Diese Methode führt die gegebene Berechnung {@code task} mit dem gegebenen Titel {@code title} in einem neuen {@link Thread} aus, sofern derzeit keine
	 * anderer derartige Berechnung {@link #isTaskRunning läuft}. Der Titel wird im Fehlerdialog sowie als {@link #taskTitle} verwendet. */
	private void runTask(final String title, final Runnable2 task) {
		final var thread = new Thread(() -> {
			synchronized (this) {
				if (this.isTaskRunning) return;
				this.taskTitle = title;
				this.taskEntry = null;
				this.taskCount = 0;
				this.isTaskRunning = true;
				this.isTaskCanceled = false;
				EventQueue.invokeLater(this::execUpdateEnabled);
			}
			try {
				task.run();
			} catch (final CancellationException ignore) {

			} catch (final Throwable error) {
				EventQueue.invokeLater(() -> this.createDialog() //
					.withTitle(title) //
					.withMessage("<html><b>Unerwarteter Fehler</b><br>%s</html>", error.toString().replaceAll("&", "&amp;").replaceAll("<", "&lt;")) //
					.withButton("Okay"));
			} finally {
				synchronized (this) {
					this.taskTitle = null;
					this.taskEntry = null;
					this.taskCount = 0;
					this.isTaskRunning = false;
					this.isTaskCanceled = false;
					EventQueue.invokeLater(this::execUpdateEnabled);
					if (this.taskCancel == null) return;
					this.taskCancel.dispose();
					this.taskCancel = null;
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/** Diese Methode führt die gegebene Berechnung {@code task} später aus. */
	private void runLater(final Runnable task) {
		EventQueue.invokeLater(task);
	}

	private void runDemo(final Runnable task) {
		final int stop = 300;
		final int step = 100;
		for (int i = 0; (i < stop) && !this.isTaskCanceled; i += step) {
			this.taskCount = stop - i;
			try {
				Thread.sleep(step);
			} catch (final InterruptedException e) {}
		}
		task.run();
	}

	private void execUpdateEnabled() {
		final var enabled = !this.isTaskRunning;
		this.inputArea.setEnabled(enabled);
		this.execUpdateEnabled(enabled, this.inputArea, this.inputMenu, this.tableArea, this.tableMenu);
		this.taskStop.setEnabled(!enabled && !this.isTaskCanceled);
	}

	private void execUpdateEnabled(final boolean value, final Component... targets) {
		for (final var target: targets) {
			target.setEnabled(value);
			if (target instanceof Container) {
				this.execUpdateEnabled(value, ((Container)target).getComponents());
			}
		}
	}

	private void execExportToClipboard(final List<File> fileList) {
		this.runLater(() -> {
			final var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new Transferable() {

				@Override
				public boolean isDataFlavorSupported(final DataFlavor flavor) {
					for (final var item: this.getTransferDataFlavors())
						if (item.equals(flavor)) return true;
					return false;
				}

				@Override
				public DataFlavor[] getTransferDataFlavors() {
					return new DataFlavor[]{DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor};
				}

				@Override
				public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
					if (DataFlavor.javaFileListFlavor.equals(flavor)) return fileList;
					if (DataFlavor.stringFlavor.equals(flavor)) return Strings.join("\n", Iterables.translate(fileList, File::getPath));
					throw new UnsupportedFlavorException(flavor);
				}

			}, null);
		});
	}

	void updateTask(final ActionEvent event) {
		if (this.isTaskRunning) {
			final String title = Objects.notNull(this.taskTitle, "?"), entry = String.valueOf(Objects.notNull(this.taskEntry, ""));
			this.taskInfo.setText("<html>" + title + " - " + this.taskCount + " - " + entry.replaceAll("\\\\", "\\\\<wbr>") + "</html>");
		} else {
			this.taskInfo.setText(" ");
		}
	}

	void setupImportInputs(final DropTargetDropEvent event) {
		if (!this.inputArea.isEnabled()) return;
		final Transferable transData = event.getTransferable();
		if (transData == null) return;
		event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		this.startImportInputs(transData);
	}

	void setupImportInputs(final ActionEvent event) {
		if (!this.inputArea.isEnabled()) return;
		final Transferable transData = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if ((transData == null) || this.startImportInputs(transData)) return;
		this.inputArea.paste();
	}

	boolean startImportInputs(final Transferable transData) {
		if (!transData.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return false;
		try {
			final var inputText = this.getInputText();
			@SuppressWarnings ("unchecked")
			final var fileList = (List<File>)transData.getTransferData(DataFlavor.javaFileListFlavor);
			this.importInputsRequest(inputText, fileList);
			return true;
		} catch (final Exception ignore) {}
		return false;
	}

	public void importInputsRequest(final String inputText, final List<File> fileList) {
		this.runDemo(() -> this.importInputsRespond(inputText));
	}

	public void importInputsRespond(final String inputText) {
		this.setInputText(inputText);
	}

	void cleanupExistingInputs() {
		this.createDialog()//
			.withTitle("Fehlerpfade erhalten") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Datenpfade zu existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?</b><br> " + //
				"Duplikate sowie relative Datenpfade werden ebenfalls verworfen. " + //
				"</html>" //
			) //
			.withButton("Ja", this::cleanupExistingInputsStart) //
			.withButton("Nein");
	}

	void cleanupExistingInputsStart() {
		final var inputText = this.getInputText();
		this.runTask("Fehlerpfaderhaltung", () -> this.cleanupExistingInputsRequest(inputText));
	}

	public void cleanupExistingInputsRequest(final String inputText) {
		this.runDemo(() -> this.cleanupExistingInputsRespond(inputText, 1234567, 7890));
	}

	public void cleanupExistingInputsRespond(final String keepText, final int validCount, final int errorCount) {
		this.setInputText(keepText);
		this.createDialog() //
			.withTitle("Fehlerpfade erhalten") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zeilen bleiben erhalten.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				validCount, errorCount//
			) //
			.withButton("Okay");
	}

	void cleanupExistingSources() {
		this.createDialog()//
			.withTitle("Fehlerquellpfade erhalten") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Quellpfade zu existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?</b><br> " + //
				"Relative Datenpfade werden ebenfalls verworfen. " + //
				"</html>" //
			) //
			.withButton("Ja", this::cleanupExistingSourcesStart) //
			.withButton("Nein");
	}

	void cleanupExistingSourcesStart() {
		final var inputText = this.getInputText();
		this.runTask("Fehlerquellpfaderhaltung", () -> this.cleanupExistingSourcesRequest(inputText));
	}

	public void cleanupExistingSourcesRequest(final String tableText) {
		this.runDemo(() -> this.cleanupExistingSourcesRespond(tableText, 1234567, 7890));
	}

	public void cleanupExistingSourcesRespond(final String tableText, final int validCount, final int errorCount) {
		this.setTableText(tableText);
		this.createDialog() //
			.withTitle("Fehlerquellpfade erhalten") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zeilen bleiben erhalten.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				validCount, errorCount//
			) //
			.withButton("Okay");
	}

	void cleanupExistingTargets() {
		this.createDialog()//
			.withTitle("Fehlerzielpfade erhalten") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Zielpfade zu existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?</b><br> " + //
				"Relative Zielpfade werden ebenfalls verworfen. " + //
				"</html>" //
			) //
			.withButton("Ja", this::cleanupExistingTargetsStart) //
			.withButton("Nein");
	}

	void cleanupExistingTargetsStart() {
		final var tableText = this.getTableText();
		this.runTask("Fehlerzielerhaltung", () -> this.cleanupExistingTargetsRequest(tableText));
	}

	public void cleanupExistingTargetsRequest(final String tableText) {
		this.runDemo(() -> this.cleanupExistingTargetsRespond(tableText, 1234567, 7890));
	}

	public void cleanupExistingTargetsRespond(final String tableText, final int validCount, final int errorCount) {
		this.setTableText(tableText);
		this.createDialog() //
			.withTitle("Fehlerzielpfade erhalten") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zeilen bleiben erhalten.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				validCount, errorCount//
			) //
			.withButton("Okay");
	}

	void cleanupMissingInputs() {
		this.createDialog()//
			.withTitle("Fehlerpfade entfernen") //
			.withMessage("<html>" + //
				"<b>Sollen alle Datenpfade zu nicht existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?</b><br> " + //
				"Duplikate sowie relative Datenpfade werden ebenfalls verworfen. " + //
				"</html>" //
			) //
			.withButton("Ja", this::cleanupMissingInputsStart) //
			.withButton("Nein");
	}

	void cleanupMissingInputsStart() {
		final var inputText = this.getInputText();
		this.runTask("Fehlerpfadentfernung", () -> this.cleanupMissingInputsRequest(inputText));
	}

	public void cleanupMissingInputsRequest(final String inputText) {
		this.runDemo(() -> this.cleanupMissingInputsRespond(inputText, 1234567, 7890));
	}

	public void cleanupMissingInputsRespond(final String inputText, final int validCount, final int errorCount) {
		this.setInputText(inputText);
		this.createDialog() //
			.withTitle("Fehlerpfade entfernt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zeilen bleiben erhalten.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				validCount, errorCount//
			) //
			.withButton("Okay");
	}

	void cleanupMissingSources() {
		this.createDialog()//
			.withTitle("Fehlerquellpfade entfernen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Quellpfade zu nicht existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?</b><br> " + //
				"Relative Datenpfade werden ebenfalls verworfen. " + //
				"</html>" //
			) //
			.withButton("Ja", this::cleanupMissingSourcesStart) //
			.withButton("Nein");
	}

	void cleanupMissingSourcesStart() {
		final var inputText = this.getInputText();
		this.runTask("Fehlerquellpfadentfernung", () -> this.cleanupExistingSourcesRequest(inputText));
	}

	public void cleanupMissingSourcesRequest(final String tableText) {
		this.runDemo(() -> this.cleanupMissingSourcesRespond(tableText, 1234567, 7890));
	}

	public void cleanupMissingSourcesRespond(final String tableText, final int validCount, final int errorCount) {
		this.setTableText(tableText);
		this.createDialog() //
			.withTitle("Fehlerquellpfade entfernt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zeilen bleiben erhalten.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				validCount, errorCount//
			) //
			.withButton("Okay");
	}

	void cleanupMissingTargets() {
		this.createDialog()//
			.withTitle("Fehlerzielpfade entfernen") //
			.withMessage("<html>" + //
				"<b>Sollen alle Zielpfade zu nicht existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?</b><br> " + //
				"Relative Zielpfade werden ebenfalls verworfen. " + //
				"</html>" //
			) //
			.withButton("Ja", this::cleanupMissingTargetsStart) //
			.withButton("Nein");
	}

	void cleanupMissingTargetsStart() {
		final var tableText = this.getTableText();
		this.runTask("Fehlerzielentfernung", () -> this.cleanupMissingTargetsRequest(tableText));
	}

	public void cleanupMissingTargetsRequest(final String tableText) {
		this.runDemo(() -> this.cleanupMissingTargetsRespond(tableText, 1234567, 7890));
	}

	public void cleanupMissingTargetsRespond(final String tableText, final int validCount, final int errorCount) {
		this.setTableText(tableText);
		this.createDialog() //
			.withTitle("Fehlerzielpfade entfernt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zeilen bleiben erhalten.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				validCount, errorCount//
			) //
			.withButton("Okay");
	}

	void deleteInputFilesTemporary() {
		this.createDialog()//
			.withTitle("Eingabedateien recyceln") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Dateien wirklich in den Papierkorb verschoben werden?</b><br> " + //
				"Die Zeilen recycelter Dateien werden aus der Pfadliste entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteInputFilesTemporaryStart) //
			.withButton("Nein");
	}

	void deleteInputFilesTemporaryStart() {
		final var inputText = this.getInputText();
		this.runTask("Eingabedateirecyclung", () -> this.deleteInputFilesTemporaryRequest(inputText));
	}

	public void deleteInputFilesTemporaryRequest(final String inputText) {
		this.runDemo(() -> this.deleteInputFilesTemporaryRespond(inputText, 1234567, 7890));
	}

	public void deleteInputFilesTemporaryRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setInputText(keepText);
		this.createDialog() //
			.withTitle("Eingabedateien recycelt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Dateien wurden recycelt.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteInputFilesPermanently() {
		this.createDialog()//
			.withTitle("Eingabedateien löschen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Dateien wirklich endgültig gelöscht werden?</b><br> " + //
				"Die Zeilen gelöschter Dateien werden aus der Pfadliste entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteInputFilesPermanentlyStart) //
			.withButton("Nein");
	}

	void deleteInputFilesPermanentlyStart() {
		final var inputText = this.getInputText();
		this.runTask("Eingabedateilöschung", () -> this.deleteInputFilesPermanentlyRequest(inputText));
	}

	public void deleteInputFilesPermanentlyRequest(final String inputText) {
		this.runDemo(() -> this.deleteInputFilesPermanentlyRespond(inputText, 1234567, 7890));
	}

	public void deleteInputFilesPermanentlyRespond(final String inputText, final int keepCount, final int dropCount) {
		this.setInputText(inputText);
		this.createDialog() //
			.withTitle("Eingabedateien gelöscht") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Dateien wurden gelöscht.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteInputFoldersTemporary() {
		this.createDialog()//
			.withTitle("Eingabeverzeichnisse recyceln") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle leeren Verzeichnisse wirklich in den Papierkorb verschoben werden?</b><br> " + //
				"Die Zeilen recycelter Verzeichnisse werden aus der Pfadliste entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteInputFoldersTemporaryStart) //
			.withButton("Nein");
	}

	void deleteInputFoldersTemporaryStart() {
		final var inputText = this.getInputText();
		this.runTask("Eingabeverzeichnisrecyclung", () -> this.deleteInputFoldersTemporaryRequest(inputText));
	}

	public void deleteInputFoldersTemporaryRequest(final String inputText) {
		this.runDemo(() -> this.deleteInputFoldersTemporaryRespond(inputText, 1234567, 7890));
	}

	public void deleteInputFoldersTemporaryRespond(final String inputText, final int keepCount, final int dropCount) {
		this.setInputText(inputText);
		this.createDialog() //
			.withTitle("Eingabeverzeichnisse recycelt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Verzeichnisse wurden recycelt.<br>" + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteInputFoldersPermanently() {
		this.createDialog()//
			.withTitle("Eingabeverzeichnisse löschen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle leeren Verzeichnisse wirklich endgültig gelöscht werden?</b><br> " + //
				"Die Zeilen gelöschter Verzeichnisse werden aus der Pfadliste entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteInputFoldersPermanentlyStart) //
			.withButton("Nein");
	}

	void deleteInputFoldersPermanentlyStart() {
		final String inputText = this.getInputText();
		this.runTask("Eingabeverzeichnislöschung", () -> this.deleteInputFoldersPermanentlyRequest(inputText));
	}

	public void deleteInputFoldersPermanentlyRequest(final String inputText) {
		this.runDemo(() -> this.deleteInputFoldersPermanentlyRespond(inputText, 1234567, 7890));
	}

	public void deleteInputFoldersPermanentlyRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setInputText(keepText);
		this.createDialog() //
			.withTitle("Eingabeverzeichnisse gelöscht") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Verzeichnisse wurden gelöscht.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteSourceFilesTemporary() {
		this.createDialog()//
			.withTitle("Quelldateien recyceln") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Dateien wirklich in den Papierkorb verschoben werden?</b><br> " + //
				"Die Zeilen recycelter Dateien werden aus der Pfadtabelle entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteSourceFilesTemporaryStart) //
			.withButton("Nein");
	}

	void deleteSourceFilesTemporaryStart() {
		final String tableText = this.getTableText();
		this.runTask("Quelldateirecyclung", () -> this.deleteSourceFilesTemporaryRequest(tableText));
	}

	public void deleteSourceFilesTemporaryRequest(final String tableText) {
		this.runDemo(() -> this.deleteSourceFilesTemporaryRespond(tableText, 1234567, 7890));
	}

	public void deleteSourceFilesTemporaryRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Quelldateien recycelt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Dateien wurden recycelt.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteSourceFilesPermanently() {
		this.createDialog()//
			.withTitle("Quelldateien löschen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Dateien wirklich endgültig gelöscht werden?</b><br> " + //
				"Die Zeilen gelöschter Dateien werden aus der Pfadtabelle entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteSourceFilesPermanentlyStart) //
			.withButton("Nein");
	}

	void deleteSourceFilesPermanentlyStart() {
		final String tableText = this.getTableText();
		this.runTask("Quelldateilöschung", () -> this.deleteSourceFilesPermanentlyRequest(tableText));
	}

	public void deleteSourceFilesPermanentlyRequest(final String tableText) {
		this.runDemo(() -> this.deleteSourceFilesPermanentlyRespond(tableText, 1234567, 7890));
	}

	public void deleteSourceFilesPermanentlyRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Quelldateien gelöscht") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Dateien wurden gelöscht.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteSourceFoldersTemporary() {
		this.createDialog()//
			.withTitle("Quellverzeichnisse recyceln") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle leeren Verzeichnisse wirklich in den Papierkorb verschoben werden?</b><br> " + //
				"Die Zeilen recycelter Verzeichnisse werden aus der Pfadtabelle entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteSourceFoldersTemporaryStart) //
			.withButton("Nein");
	}

	void deleteSourceFoldersTemporaryStart() {
		final String tableText = this.getTableText();
		this.runTask("Quellverzeichnisrecyclung", () -> this.deleteSourceFoldersTemporaryRequest(tableText));
	}

	public void deleteSourceFoldersTemporaryRequest(final String tableText) {
		this.runDemo(() -> this.deleteSourceFoldersTemporaryRespond(tableText, 1234567, 7890));
	}

	public void deleteSourceFoldersTemporaryRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Quellverzeichnisse recycelt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Verzeichnisse wurden recycelt.<br>" + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteSourceFoldersPermanently() {
		this.createDialog()//
			.withTitle("Quellverzeichnisse löschen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle leeren Verzeichnisse wirklich endgültig gelöscht werden?</b><br> " + //
				"Die Zeilen gelöschter Verzeichnisse werden aus der Pfadtabelle entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteSourceFoldersPermanentlyStart) //
			.withButton("Nein");
	}

	void deleteSourceFoldersPermanentlyStart() {
		final String tableText = this.getTableText();
		this.runTask("Quellverzeichnislöschung", () -> this.deleteSourceFoldersPermanentlyRequest(tableText));
	}

	public void deleteSourceFoldersPermanentlyRequest(final String tableText) {
		this.runDemo(() -> this.deleteSourceFoldersPermanentlyRespond(tableText, 1234567, 7890));
	}

	public void deleteSourceFoldersPermanentlyRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Quellverzeichnisse gelöscht") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Verzeichnisse wurden gelöscht.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteTargetFilesTemporary() {
		this.createDialog()//
			.withTitle("Zieldateien recyceln") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Dateien wirklich in den Papierkorb verschoben werden?</b><br> " + //
				"Die Zeilen recycelter Dateien werden aus der Pfadtabelle entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteTargetFilesTemporaryStart) //
			.withButton("Nein");
	}

	void deleteTargetFilesTemporaryStart() {
		final String tableText = this.getTableText();
		this.runTask("Zieldateirecyclung", () -> this.deleteSourceFilesTemporaryRequest(tableText));
	}

	public void deleteTargetFilesTemporaryRequest(final String tableText) {
		this.runDemo(() -> this.deleteTargetFilesTemporaryRespond(tableText, 1234567, 7890));
	}

	public void deleteTargetFilesTemporaryRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Zieldateien recycelt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Dateien wurden recycelt.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteTargetFilesPermanently() {
		this.createDialog()//
			.withTitle("Zieldateien löschen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Dateien wirklich endgültig gelöscht werden?</b><br> " + //
				"Die Zeilen gelöschter Dateien werden aus der Pfadtabelle entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteTargetFilesPermanentlyStart) //
			.withButton("Nein");
	}

	void deleteTargetFilesPermanentlyStart() {
		final String tableText = this.getTableText();
		this.runTask("Zieldateilöschung", () -> this.deleteTargetFilesPermanentlyRequest(tableText));
	}

	public void deleteTargetFilesPermanentlyRequest(final String tableText) {
		this.runDemo(() -> this.deleteTargetFilesPermanentlyRespond(tableText, 1234567, 7890));
	}

	public void deleteTargetFilesPermanentlyRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Zieldateien gelöscht") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Dateien wurden gelöscht.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteTargetFoldersTemporary() {
		this.createDialog()//
			.withTitle("Zielverzeichnisse recyceln") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle leeren Verzeichnisse wirklich in den Papierkorb verschoben werden?</b><br> " + //
				"Die Zeilen recycelter Verzeichnisse werden aus der Pfadtabelle entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteTargetFoldersTemporaryStart) //
			.withButton("Nein");
	}

	void deleteTargetFoldersTemporaryStart() {
		final String tableText = this.getTableText();
		this.runTask("Zielverzeichnisrecyclung", () -> this.deleteTargetFoldersTemporaryRequest(tableText));
	}

	public void deleteTargetFoldersTemporaryRequest(final String tableText) {
		this.runDemo(() -> this.deleteTargetFoldersTemporaryRespond(tableText, 1234567, 7890));
	}

	public void deleteTargetFoldersTemporaryRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Zielverzeichnisse recycelt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Verzeichnisse wurden recycelt.<br>" + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void deleteTargetFoldersPermanently() {
		this.createDialog()//
			.withTitle("Zielverzeichnisse löschen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle leeren Verzeichnisse wirklich endgültig gelöscht werden?</b><br> " + //
				"Die Zeilen gelöschter Verzeichnisse werden aus der Pfadtabelle entfert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::deleteTargetFoldersPermanentlyStart) //
			.withButton("Nein");
	}

	void deleteTargetFoldersPermanentlyStart() {
		final String tableText = this.getTableText();
		this.runTask("Zielverzeichnislöschung", () -> this.deleteTargetFoldersPermanentlyRequest(tableText));
	}

	public void deleteTargetFoldersPermanentlyRequest(final String tableText) {
		this.runDemo(() -> this.deleteTargetFoldersPermanentlyRespond(tableText, 1234567, 7890));
	}

	public void deleteTargetFoldersPermanentlyRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Zielverzeichnisse gelöscht") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Verzeichnisse wurden gelöscht.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void exportInputs() {
		this.exportInputsStart();
	}

	void exportInputsStart() {
		final var inputText = this.getInputText();
		this.runTask("Eingabepfadexport", () -> this.exportInputsRequest(inputText));
	}

	public void exportInputsRequest(final String inputText) {
		this.runDemo(() -> this.exportInputsRespond(Collections.emptyList()));
	}

	public void exportInputsRespond(final List<File> fileList) {
		this.execExportToClipboard(fileList);
	}

	void exportSources() {
		this.exportSourcesStart();
	}

	void exportSourcesStart() {
		final var tableText = this.getTableText();
		this.runTask("Quellpfadexport", () -> this.exportSourcesRequest(tableText));
	}

	public void exportSourcesRequest(final String inputText) {
		this.runDemo(() -> this.exportSourcesRespond(Collections.emptyList()));
	}

	public void exportSourcesRespond(final List<File> fileList) {
		this.execExportToClipboard(fileList);
	}

	void exportTargets() {
		this.exportTargetsStart();
	}

	void exportTargetsStart() {
		final var tableText = this.getTableText();
		this.runTask("Zielpfadexport", () -> this.exportTargetsRequest(tableText));
	}

	public void exportTargetsRequest(final String tableText) {
		this.runDemo(() -> this.exportTargetsRespond(Collections.emptyList()));
	}

	public void exportTargetsRespond(final List<File> fileList) {
		this.execExportToClipboard(fileList);
	}

	void exchangeSourcesWithTargets() {
		this.createDialog()//
			.withTitle("Quellpfade tauschen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Quellpfade mit deren Zielpfaden getauscht werden?</b><br> " + //
				"</html>" //
			) //
			.withButton("Ja", this::exchangeSourcesWithTargetsStart) //
			.withButton("Nein");
	}

	void exchangeSourcesWithTargetsStart() {
		final var tableText = this.getTableText();
		this.runTask("Quellpfadtauschung", () -> this.exchangeSourcesWithTargetsRequest(tableText));
	}

	public void exchangeSourcesWithTargetsRequest(final String tableText) {
		this.runDemo(() -> this.exchangeSourcesWithTargetsRespond(tableText, 1234567, 7890));
	}

	public void exchangeSourcesWithTargetsRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Quellpfade getauscht") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Quellpfade wurden getauscht.<br>" + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				keepCount - failCount, failCount //
			) //
			.withButton("Okay");
	}

	void exchangeTargetsWithSources() {
		this.createDialog()//
			.withTitle("Zielpfade tauschen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Zielpfade mit deren Quellpfaden getauscht werden?</b> " + //
				"</html>" //
			) //
			.withButton("Ja", this::exchangeTargetsWithSourcesStart) //
			.withButton("Nein");
	}

	void exchangeTargetsWithSourcesStart() {
		final var tableText = this.getTableText();
		this.runTask("Zielpfadtauschung", () -> this.exchangeTargetsWithSourcesRequest(tableText));
	}

	public void exchangeTargetsWithSourcesRequest(final String tableText) {
		this.runDemo(() -> this.exchangeTargetsWithSourcesRespond(tableText, 1234567, 7890));
	}

	public void exchangeTargetsWithSourcesRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Quellpfade getauscht") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zielpfade wurden getauscht.<br>" + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				keepCount - failCount, failCount //
			) //
			.withButton("Okay");
	}

	void replaceSourcesWithTargets() {
		this.createDialog()//
			.withTitle("Quellpfade ersetzen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Quellpfade durch deren Zielpfade ersetzt werden?</b><br> " + //
				"</html>" //
			) //
			.withButton("Ja", this::replaceSourcesWithTargetsStart) //
			.withButton("Nein");
	}

	void replaceSourcesWithTargetsStart() {
		final var tableText = this.getTableText();
		this.runTask("Quellpfadersetzung", () -> this.replaceSourcesWithTargetsRequest(tableText));
	}

	public void replaceSourcesWithTargetsRequest(final String tableText) {
		this.runDemo(() -> this.replaceSourcesWithTargetsRespond(tableText, 1234567, 7890));
	}

	public void replaceSourcesWithTargetsRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Quellpfade ersetzt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Quellpfade wurden ersetzt.<br>" + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				keepCount - failCount, failCount //
			) //
			.withButton("Okay");
	}

	void replaceTargetsWithSources() {
		this.createDialog()//
			.withTitle("Zielpfade ersetzen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Zielpfade mit deren Quellpfaden ersetzt werden?</b> " + //
				"</html>" //
			) //
			.withButton("Ja", this::replaceTargetsWithSourcesStart) //
			.withButton("Nein");
	}

	void replaceTargetsWithSourcesStart() {
		final var tableText = this.getTableText();
		this.runTask("Zielpfadersetzung", () -> this.replaceTargetsWithSourcesRequest(tableText));
	}

	public void replaceTargetsWithSourcesRequest(final String tableText) {
		this.runDemo(() -> this.replaceTargetsWithSourcesRespond(tableText, 1234567, 7890));
	}

	public void replaceTargetsWithSourcesRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Zielpfade ersetzt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zielpfade wurden ersetzt.<br>" + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				keepCount - failCount, failCount //
			) //
			.withButton("Okay");
	}

	void transferInputs() {
		this.createDialog()//
			.withTitle("Eingabepfade übertragen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Datenpfade wirklich als Quell- und Zielpfade in die Pfadtabelle übernommen werden?</b><br> " + //
				"Duplikate sowie relative Datenpfade werden ignoriert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::transferInputsStart) //
			.withButton("Nein");
	}

	void transferInputsStart() {
		final var inputText = this.getInputText();
		this.runTask("Eingabepfaddübertragung", () -> this.transferInputsRequest(inputText));
	}

	public void transferInputsRequest(final String inputText) {
		this.runDemo(() -> this.transferInputsRespond("", 1234567, 7890));
	}

	public void transferInputsRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Eingabepfade übertragen") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Eingabepfade wurden übernommen.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				keepCount, failCount //
			) //
			.withButton("Okay");
	}

	void transferSources() {
		this.createDialog()//
			.withTitle("Quellpfade übertragen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Eingabepfade in der Pfadliste mit allen Quellpfaden ersetzt werden?</b> " + //
				"</html>" //
			) //
			.withButton("Ja", this::transferSourcesStart) //
			.withButton("Nein");
	}

	void transferSourcesStart() {
		final var tableText = this.getTableText();
		this.runTask("Quellpfaddübertragung", () -> this.transferSourcesRequest(tableText));
	}

	public void transferSourcesRequest(final String tableText) {
		this.runDemo(() -> this.transferSourcesRespond("", 1234567, 7890));
	}

	public void transferSourcesRespond(final String keepText, final int keepCount, final int failCount) {
		this.setInputText(keepText);
		this.createDialog() //
			.withTitle("Quellpfade übertragen") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Quellpfade wurden übernommen.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				keepCount, failCount //
			) //
			.withButton("Okay");
	}

	void transferTargets() {
		this.createDialog()//
			.withTitle("Zielpfade übertragen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Zielpfade wirklich als Eingabepfade in die Pfadliste übernommen werden?</b><br> " + //
				"Duplikate sowie relative Zielpfade werden ignoriert. " + //
				"</html>" //
			) //
			.withButton("Ja", this::transferTargetsStart) //
			.withButton("Nein");
	}

	void transferTargetsStart() {
		final var tableText = this.getTableText();
		this.runTask("Zielpfaddübertragung", () -> this.transferTargetsRequest(tableText));
	}

	public void transferTargetsRequest(final String tableText) {
		this.runDemo(() -> this.transferTargetsRespond("", 1234567, 7890));
	}

	public void transferTargetsRespond(final String keepText, final int keepCount, final int failCount) {
		this.setInputText(keepText);
		this.createDialog() //
			.withTitle("Zielpfade übertragen") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zielpfade wurden übernommen.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				keepCount, failCount //
			) //
			.withButton("Okay");
	}

	void resolveInputToFiles() {
		this.createDialog()//
			.withTitle("Dateien auflösen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Verzeichnispfade wirklich durch die Pfade aller darin enthaltenen Dateien ersetzt werden?</b><br> " + //
				"Die Dateiauflösung wird in allen Unterverzeichnissen fortgesetzt. " + //
				"Duplikate sowie relative Datenpfade werden verworfen. " + //
				"Dateipfade bleiben erhalten. " + //
				"</html>" //
			) //
			.withButton("Ja", this::resolveInputToFilesStart) //
			.withButton("Nein");
	}

	void resolveInputToFilesStart() {
		final var inputText = this.getInputText();
		this.runTask("Dateiauflösung", () -> this.resolveInputToFilesRequest(inputText));
	}

	public void resolveInputToFilesRequest(final String inputText) {
		this.runDemo(() -> this.resolveInputToFilesRespond(inputText, 1234567, 7890));
	}

	public void resolveInputToFilesRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setInputText(keepText);
		this.createDialog() //
			.withTitle("Dateien aufgelöst") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Dateipfade wurden ermittelt.<br> " + //
				"<b>%,d</b> Datenpfade wurden verworfen." + //
				"</html>", //
				keepCount, dropCount //
			) //
			.withButton("Okay");
	}

	void resolveInputToFolders() {
		this.createDialog()//
			.withTitle("Verzeichnisse auflösen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Verzeichnispfade wirklich um die Pfade aller darin enthaltenen Verzeichnisse ergänzt werden?</b><br> " + //
				"Die Verzeichnisauflösung wird in allen Unterverzeichnissen fortgesetzt. " + //
				"Duplikate sowie relative Datenpfade werden verworfen. " + //
				"Dateipfade werden ebenfalls verworfen. " + //
				"</html>" //
			) //
			.withButton("Ja", this::resolveInputToFoldersStart) //
			.withButton("Nein");
	}

	void resolveInputToFoldersStart() {
		final var inputText = this.getInputText();
		this.runTask("Verzeichnisauflösung", () -> this.resolveInputToFoldersRequest(inputText));
	}

	public void resolveInputToFoldersRequest(final String inputText) {
		this.runDemo(() -> this.resolveInputToFoldersRespond(inputText, 1234567, 7890));
	}

	public void resolveInputToFoldersRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setInputText(keepText);
		this.createDialog() //
			.withTitle("Verzeichnisse aufgelöst") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Verzeichnispfade wurden ermittelt.<br> " + //
				"<b>%,d</b> Datenpfade wurden verworfen." + //
				"</html>", //
				keepCount, dropCount //
			) //
			.withButton("Okay");
	}

	void refreshInputFiles() {
		this.createDialog()//
			.withTitle("Dateien erneuern") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle alten Dateien wirklich erneuert werden?</b><br> " + //
				"Beim Erneuern werden alle Dateien, die vor mehr als der unten angegebenen Anzahl an Tagen erstellt wurden, kopiert und durch ihre Kopien ersetzt. " + //
				"Die dazu temporär erzeugten Kopien tragen die Dateiendung <u>.tempcopy</u>. " + //
				"Die Zeilen erneuerter Dateien werden aus der Pfadliste entfert. " + //
				"</html>" //
			) //
			.withOption("Dateialter in Tagen", this.settings.copyFilesTimeFilter) //
			.withButton("Ja", this::refreshInputFilesStart) //
			.withButton("Nein");
	}

	void refreshInputFilesStart() {
		final var inputText = this.getInputText();
		final var copyTime = this.settings.copyFilesTimeFilter.val;
		this.runTask("Dateierneuerung", () -> this.refreshInputFilesRequest(inputText, copyTime));
	}

	public void refreshInputFilesRequest(final String inputText, final long copyTime) {
		this.runDemo(() -> this.refreshInputFilesRespond(inputText, 1234567, 7890));
	}

	public void refreshInputFilesRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setInputText(keepText);
		this.createDialog() //
			.withTitle("Dateien erneuert") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Dateien wurden erneuert.<br> " + //
				"<b>%,d</b> Zeilen wurden nicht verarbeitet." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void createTableWithClones() {
		this.createDialog() //
			.withTitle("Duplikate übernehmen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Dateien auf Duplikate hin untersucht werden?</b><br> " + //
				"Beim Suchen von Duplikaten werden alle Dateien zunächst bezüglich ihrer <u>Dateigröße</u> partitioniert. " + //
				"Die Dateien innerhalb einer Dateigrößenpartition werden dann bezüglich ihres <u>SHA-256-Streuwerts</u> partitioniert. " + //
				"Dieser Streuwert wird aus höchstens der unten eingestellten Anzanl an Bytes jeweils ab dem Dateibeginn und dem Dateiende berechnet. " + //
				"Schließlich werden die Dateien innerhalb einer Streuwertpartition nach ihrem <u>Dateiinhalt</u> partitioniert. " + //
				"Als Dateiinhalt wird höchstens die unten eingestellte Anzanl an Bytes jeweils ab dem Dateibeginn und dem Dateiende betrachtet.<br>" + //
				"Der Pfad der ersten Datei einer Dateiinhaltspartition wird als Quellpfad verwendet. " + //
				"Die Pfade der anderen Dateien der Partitionen werden diesem als Zielpfade zugeordnet. " + //
				"Jedem Zielpfad wird zudem der Streuwert sowie die Dateigröße informativ angefügte. " + //
				"Quellpfade ohne Zielpfade werden verworfen. " + //
				"Duplikate sowie Relative Dateipfade werden verworfen." + //
				"</html>" //
			) //
			.withOption("Puffergröße für Streuwert", this.settings.findClonesHashSize) //
			.withOption("Puffergröße für Dateivergleich", this.settings.findClonesTestSize) //
			.withButton("Ja", this::createTableWithClonesStart) //
			.withButton("Nein");
	}

	void createTableWithClonesStart() {
		final var inputText = this.getInputText();
		final var hashSize = this.settings.findClonesHashSize.val;
		final var testSize = this.settings.findClonesTestSize.val;
		this.settings.persist();
		this.runTask("Duplikateübernahme", () -> this.createTableWithClonesRequest(inputText, hashSize, testSize));
	}

	public void createTableWithClonesRequest(final String inputText, final long hashSize, final long testSize) throws Exception {
		this.runDemo(() -> this.createTableWithClonesRespond("", 1234567, 7890));
	}

	public void createTableWithClonesRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Duplikate übernommen") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Duplikate wurden gefunden.<br> " + //
				"<b>%,d</b> Datenpfade konnten nicht verarbeitet werden." + //
				"</html>", //
				keepCount, failCount //
			) //
			.withButton("Okay");
	}

	void createTargetsWithTimenameFromName() {
		this.createDialog() //
			.withTitle("Zeitnamen ableiten") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen die Zielnamen wirklich aus den Zeitpunkten in den Quellnamen abgeleitet werden?</b><br> " + //
				"Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft " + //
				"verschobenen Zeitpunkten, die im Quellnamen mit beliebigen Trennzeichen angegeben sind. " + //
				"Die Zielpfade haben das Format <tt>{EP}\\JJJJ-MM-TT hh.mm.ss{NE}</tt>, wobei <tt>{EP}</tt> für den " + //
				"Elternverzeichnispfad und <tt>{NE}</tt> für die kleingeschriebene Namenserweiterung der Quelldatei stehen." + //
				"</html>" //
			) //
			.withOption("Zeitkorrektur in Sekunden", this.settings.moveFilesTimeOffset) //
			.withButton("Ja", this::createTargetsWithTimenameFromNameStart) //
			.withButton("Nein");
	}

	void createTargetsWithTimenameFromNameStart() {
		final var tableText = this.getTableText();
		final var moveTime = this.settings.moveFilesTimeOffset.val;
		this.settings.persist();
		this.runTask("Zeitnamensableitung", () -> this.createTargetsWithTimenameFromNameRequest(tableText, moveTime));
	}

	public void createTargetsWithTimenameFromNameRequest(final String tableText, final long moveTime) {
		this.runDemo(() -> this.createTargetsWithTimenameFromNameRespond(tableText, 1234567, 7890));
	}

	public void createTargetsWithTimenameFromNameRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Zeitnamen abgeleitet") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zielpfade wurden angepasst.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				keepCount - failCount, failCount //
			) //
			.withButton("Okay");
	}

	void createTargetsWithTimepathFromName() {
		this.createDialog() //
			.withTitle("Zeitpfade ableiten") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen die Zielpfade wirklich aus den Zeitpunkten in den Quellnamen abgeleitet werden?</b><br> " + //
				"Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft " + //
				"verschobenen Zeitpunkten, die im Quellnamen mit beliebigen Trennzeichen angegeben sind.<br> " + //
				"Die Zielpfade haben das Format <tt>{GP}\\JJJJ-MM_{EN}\\JJJJ-MM-TT hh.mm.ss{NE}</tt>, wobei <tt>{GP}</tt> für den " + //
				"Großelternverzeichnispfad, <tt>{EN}</tt> für den Elternverzeichnisnamen und <tt>{NE}</tt> für die kleingeschriebene " + //
				"Namenserweiterung der Quelldatei stehen." + //
				"</html>" //
			) //
			.withOption("Zeitkorrektur in Sekunden", this.settings.moveFilesTimeOffset) //
			.withButton("Ja", this::createTargetsWithTimepathFromNameStart) //
			.withButton("Nein");
	}

	void createTargetsWithTimepathFromNameStart() {
		final var tableText = this.getTableText();
		final var moveTime = this.settings.moveFilesTimeOffset.val;
		this.settings.persist();
		this.runTask("Zeitpfadableitung", () -> this.createTargetsWithTimepathFromNameRequest(tableText, moveTime));
	}

	public void createTargetsWithTimepathFromNameRequest(final String tableText, final long moveTime) {
		this.runDemo(() -> this.createTargetsWithTimepathFromNameRespond(tableText, 1234567, 7890));
	}

	public void createTargetsWithTimepathFromNameRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Zeitpfade abgeleitet") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zielpfade wurden angepasst.<br> " + //
				"<b>%s</b> Zeilen konnten nicht angepasst werden." + //
				"</html>", //
				keepCount - failCount, failCount //
			) //
			.withButton("Okay");
	}

	void createTargetsWithTimenameFromTime() {
		this.createDialog() //
			.withTitle("Zeitnamen aus Änderungszeitpunkten ableiten") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen die Zielnamen wirklich aus den Änderungszeitpunkten der Quelldateien abgeleitet werden?</b><br> " + //
				"Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft " + //
				"verschobenen Änderungszeitpunkten der Quelldateien.<br> " + //
				"Die Zielpfade haben das Format <tt>{EP}\\JJJJ-MM-TT hh.mm.ss{NE}</tt>, wobei <tt>{EP}</tt> für den " + //
				"Elternverzeichnispfad und <tt>{NE}</tt> für die kleingeschriebene Namenserweiterung der Quelldatei stehen." + //
				"</html>" //
			) //
			.withOption("Zeitkorrektur in Sekunden", this.settings.moveFilesTimeOffset) //
			.withButton("Ja", this::createTargetsWithTimenameFromTimeStart) //
			.withButton("Nein");
	}

	void createTargetsWithTimenameFromTimeStart() {
		final var tableText = this.getTableText();
		final var moveTime = this.settings.moveFilesTimeOffset.val;
		this.settings.persist();
		this.runTask("Zeitnamensableitung", () -> this.createTargetsWithTimenameFromTimeRequest(tableText, moveTime));
	}

	public void createTargetsWithTimenameFromTimeRequest(final String tableText, final long moveTime) {
		this.runDemo(() -> this.createTargetsWithTimenameFromTimeRespond(tableText, 1234567, 7890));
	}

	public void createTargetsWithTimenameFromTimeRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Zeitnamen abgeleitet") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zielpfade wurden angepasst.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				keepCount - failCount, failCount //
			) //
			.withButton("Okay");
	}

	void createTargetsWithTimepathFromTime() {
		this.createDialog() //
			.withTitle("Zeitpfade aus Änderungszeitpunkten ableiten") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen die Zielpfade wirklich aus den Änderungszeitpunkten der Quelldateien abgeleitet werden?</b><br> " + //
				"Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft " + //
				"verschobenen Änderungszeitpunkten der Quelldateien.<br> " + //
				"Die Zielpfade haben das Format <tt>{GP}\\JJJJ-MM_{EN}\\JJJJ-MM-TT hh.mm.ss{NE}</tt>, wobei <tt>{GP}</tt> für den " + //
				"Großelternverzeichnispfad, <tt>{EN}</tt> für den Elternverzeichnisnamen und <tt>{NE}</tt> für die kleingeschriebene " + //
				"Namenserweiterung der Quelldatei stehen." + //
				"</html>" //
			) //
			.withOption("Zeitkorrektur in Sekunden", this.settings.moveFilesTimeOffset) //
			.withButton("Ja", this::createTargetsWithTimepathFromTimeStart) //
			.withButton("Nein");
	}

	void createTargetsWithTimepathFromTimeStart() {
		final var tableText = this.getTableText();
		final var moveTime = this.settings.moveFilesTimeOffset.val;
		this.settings.persist();
		this.runTask("Zeitpfadableitung", () -> this.createTargetsWithTimepathFromTimeRequest(tableText, moveTime));
	}

	public void createTargetsWithTimepathFromTimeRequest(final String tableText, final long moveTime) {
		this.runDemo(() -> this.createTargetsWithTimepathFromTimeRespond(tableText, 1234567, 7890));
	}

	public void createTargetsWithTimepathFromTimeRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Zeitpfade abgeleitet") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Zielpfade wurden angepasst.<br> " + //
				"<b>%s</b> Zeilen konnten nicht angepasst werden." + //
				"</html>", //
				keepCount - failCount, failCount //
			) //
			.withButton("Okay");
	}

	void copySourceToTargetFiles() {
		this.createDialog() //
			.withTitle("Dateien kopieren") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Dateien wirklich nicht ersetzend kopiert werden?</b><br> " + //
				"Die Zeilen erfolgreich kopierter Dateien werden aus der Pfadtabelle entfernt. " + //
				"</html>" //
			) //
			.withButton("Ja", this::copySourceToTargetFilesStart) //
			.withButton("Nein");
	}

	void copySourceToTargetFilesStart() {
		final var tableText = this.getTableText();
		this.runTask("Dateikopieren", () -> this.copySourceToTargetFilesRequest(tableText));
	}

	public void copySourceToTargetFilesRequest(final String tableText) throws Exception {
		this.runDemo(() -> this.copySourceToTargetFilesRespond(tableText, 1234567, 7890));
	}

	public void copySourceToTargetFilesRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Dateien kopiert") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Dateien wurden kopiert.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void moveSourceToTargetFiles() {
		this.createDialog() //
			.withTitle("Dateien verschieben") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen alle Dateien wirklich nicht ersetzend verschoben werden?</b><br> " + //
				"Die Zeilen erfolgreich verschobener Dateien werden aus der Pfadtabelle entfernt. " + //
				"</html>" //
			) //
			.withButton("Ja", this::moveSourceToTargetFilesStart) //
			.withButton("Nein");
	}

	void moveSourceToTargetFilesStart() {
		final var tableText = this.getTableText();
		this.runTask("Dateiverschieben", () -> this.moveSourceToTargetFilesRequest(tableText));
	}

	public void moveSourceToTargetFilesRequest(final String tableText) throws Exception {
		this.runDemo(() -> this.moveSourceToTargetFilesRespond(tableText, 1234567, 7890));
	}

	public void moveSourceToTargetFilesRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withTitle("Dateien verschoben") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Dateien wurden verschoben.<br> " + //
				"<b>%,d</b> Zeilen konnten nicht verarbeitet werden." + //
				"</html>", //
				dropCount, keepCount //
			) //
			.withButton("Okay");
	}

	void showSourceAndTargetFiles() {
		this.createDialog() //
			.withTitle("Dateipaare anzeigen") //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen die Quell- und Zieldateien wirklich angezeigt werden?</b><br> " + //
				"Die Dateien werden dabei als Symlinks in ein temporäres Verzeichnis eingefügt. " + //
				"Die Quelldateien werden als <u>-ORIGINAL-</u> gekennzeichnet, die Zieldateien als <u>-DUPLIKAT-</u>. " + //
				"Das temporäre Verzeichnis wird abschließend angezeigt.<br>" + //
				"Das Erzeugen von Symlinks benötigt Administrator-Rechte!" + //
				"</html>" //
			) //
			.withButton("Ja", this::showSourceAndTargetFilesStart) //
			.withButton("Nein");
	}

	void showSourceAndTargetFilesStart() {
		final var tableText = this.tableArea.getText();
		this.runTask("Dateianzeigen", () -> this.showSourceAndTargetFilesRequest(tableText));
	}

	public void showSourceAndTargetFilesRequest(final String tableText) throws Exception {
		this.runDemo(() -> this.showSourceAndTargetFilesRespond("", 0));
	}

	public void showSourceAndTargetFilesRespond(final String linkPath, final int linkCount) {
		this.createDialog() //
			.withTitle("Dateipaare angezeigt") //
			.withMessage("" + //
				"<html>" + //
				"<b>%,d</b> Symlinks wurden in das folgende Verzeichnis eingefügt:<br>" + //
				"<b>%s</b>" + //
				"</html>", //
				linkCount, linkPath //
			) //
			.withButton("Okay");
	}

	void cancelProcess(final ActionEvent event) {
		this.taskCancel = this.createDialog() //
			.withTitle(Objects.notNull(this.taskTitle, "Abbrechen")) //
			.withMessage("" + //
				"<html>" + //
				"<b>Sollen der Vorgang wirklich abgebrochen werden?</b> " + //
				"</html>" //
			) //
			.withButton("Ja", this::cancelProcessStart) //
			.withButton("Nein");
	}

	synchronized void cancelProcessStart() {
		if (this.isTaskCanceled || !this.isTaskRunning) return;
		this.isTaskCanceled = true;
		this.runLater(this::execUpdateEnabled);
	}

}
