package bee.creative.app.ft;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.swing.Timer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import bee.creative.lang.Objects;
import bee.creative.lang.Runnable2;
import bee.creative.lang.Strings;
import bee.creative.util.Consumer;
import bee.creative.util.Iterables;
import bee.creative.util.Producer;
import bee.creative.util.Properties;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class FTWindow_SWT implements Runnable {

	public static void openAndWait(final Shell shell) {
		shell.open();
		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public FTWindow_SWT() {
		this.display = Display.getDefault();
		this.shell = new Shell(this.display, SWT.SHELL_TRIM);
		this.shell.setText("File-Tool");
		this.shell.setMinimumSize(400, 200);
		this.shell.setSize(600, 400);
		this.shell.setLayout(new GridLayout(2, false));
		this.shell.addShellListener(ShellListener.shellClosedAdapter(event -> this.shell.dispose()));

		final var pagePanel = new TabFolder(this.shell, SWT.TOP);
		pagePanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		{
			final var inputPage = new TabItem(pagePanel, SWT.NONE);
			final var inputPanel = new Decorations(pagePanel, SWT.NO_TRIM);
			inputPage.setText("Pfadliste (Eingabepfade)");
			inputPage.setControl(inputPanel);
			inputPanel.setLayout(new GridLayout());
			this.inputMenu = this.createInputMenu(inputPanel);
			this.inputArea = this.createInputArea(inputPanel);
			this.inputArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			final var inputLabel = this.createInputLabel(inputPanel);
			inputLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		}
		{
			final var tablePage = new TabItem(pagePanel, SWT.NONE);
			final var tablePanel = new Decorations(pagePanel, SWT.NO_TRIM);
			tablePage.setText("Pfadtabelle (Quell- und Zielpfade)");
			tablePage.setControl(tablePanel);
			tablePanel.setLayout(new GridLayout());
			this.tableMenu = this.createTableMenu(tablePanel);
			this.tableArea = this.createTableArea(tablePanel);
			this.tableArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			final var tableLabel = this.createTableLabel(tablePanel);
			tableLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		}
		{
			this.taskInfo = new Label(this.shell, SWT.NONE);
			this.taskInfo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		}
		{
			this.taskStop = new Button(this.shell, SWT.NONE);
			this.taskStop.setText("abbrechen");
			this.taskStop.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::cancelProcess));
		}
		this.execUpdateEnabled();
		new Timer(500, this::updateTask).start();
		this.settings.restore();
		this.settings.persist();
	}

	@Override
	public void run() {
		this.shell.layout();
		final var bounds = this.display.getPrimaryMonitor().getBounds();
		final var rect = this.shell.getBounds();
		this.shell.setLocation(bounds.x + ((bounds.width - rect.width) / 2), bounds.y + ((bounds.height - rect.height) / 2));
		final Shell shell2 = this.shell;
		FTWindow_SWT.openAndWait(shell2);
		System.exit(0);
	}

	private final Shell shell;

	private final Display display;

	private final Text inputArea;

	private final Menu inputMenu;

	private <T> T getSync(final Producer<T> getter) {
		final var res = Properties.<T>fromValue(null);
		this.display.syncExec(() -> res.set(getter.get()));
		return res.get();
	}

	private <T> void setSync(final Consumer<? super T> setter, final T value) {
		this.display.asyncExec(() -> setter.set(value));
	}

	private String getInputText() {
		return this.getSync(this.inputArea::getText);
	}

	private void setInputText(final String inputText) {
		this.setSync(this.inputArea::setText, inputText);
	}

	private Menu createInputMenu(final Decorations parent) {
		final var res = this.createMenuBar(parent);
		final var mn1 = this.createMenu(res, "Eingabepfade");
		this.createMenuItem(mn1, "Fehlerpfade erhalten...", this::cleanupExistingInputs);
		this.createMenuItem(mn1, "Fehlerpfade entfernen...", this::cleanupMissingInputs);
		this.createMenuItem(mn1, "Dateien auflösen...", this::resolveInputToFiles);
		this.createMenuItem(mn1, "Verzeichnisse auflösen...", this::resolveInputToFolders);
		this.createMenuBreak(mn1);
		this.createMenuItem(mn1, "Eingabepfade übertragen...", this::transferInputs);
		this.createMenuItem(mn1, "Eingabepfade kopieren", this::exportInputs);
		final var mn2 = this.createMenu(res, "Dateien");
		this.createMenuItem(mn2, "Dateien löschen...", this::deleteInputFilesPermanently);
		this.createMenuItem(mn2, "Dateien recyceln...", this::deleteInputFilesTemporary);
		this.createMenuItem(mn2, "Dateien erneuern...", this::refreshInputFiles);
		this.createMenuBreak(mn2);
		this.createMenuItem(mn2, "Dateiduplikate übertragen...", this::createTableWithClones);
		final var m23 = this.createMenu(res, "Verzeichnisse");
		this.createMenuItem(m23, "Verzeichnisse löschen...", this::deleteInputFoldersPermanently);
		this.createMenuItem(m23, "Verzeichnisse recyceln...", this::deleteInputFoldersTemporary);
		return res;
	}

	private Text createInputArea(final Composite parent) {
		final var res = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		// res.setOnDrop(this::setupImportInputs);
		// res.setOnPaste(this::setupImportInputs);
		return res;
	}

	private Label createInputLabel(final Composite parent) {
		final var res = new Label(parent, SWT.WRAP);
		res.setText("Dateien und Verzeichnisse können hier aus der Zwischenablage eingefügt bzw. aus dem Dateiexplorer fallengelassen werden.");
		return res;
	}

	private Text tableArea;

	private Menu tableMenu;

	private String getTableText() {
		return this.getSync(this.tableArea::getText);
	}

	private void setTableText(final String tableText) {
		this.setSync(this.tableArea::setText, tableText);
	}

	private Menu createTableMenu(final Decorations parent) {
		final var res = this.createMenuBar(parent); //
		final var mn1 = this.createMenu(res, "Quellpfade");
		this.createMenuItem(mn1, "Fehlerpfade erhalten...", this::cleanupExistingSources);
		this.createMenuItem(mn1, "Fehlerpfade entfernen...", this::cleanupMissingSources);
		this.createMenuItem(mn1, "Quellpfade ersetzen...", this::replaceSourcesWithTargets);
		this.createMenuItem(mn1, "Quellpfade tauschen...", this::exchangeSourcesWithTargets);
		this.createMenuBreak(mn1);
		this.createMenuItem(mn1, "Quellpfade übertragen...", this::transferSources);
		this.createMenuItem(mn1, "Quellpfade kopieren", this::exportSources);
		final var mn2 = this.createMenu(res, "Zielpfade");
		this.createMenuItem(mn2, "Fehlerpfade erhalten...", this::cleanupExistingTargets);
		this.createMenuItem(mn2, "Fehlerpfade entfernen...", this::cleanupMissingTargets);
		this.createMenuItem(mn2, "Zielpfade ersetzen...", this::replaceTargetsWithSources);
		this.createMenuItem(mn2, "Zielpfade tauschen...", this::exchangeTargetsWithSources);
		this.createMenuBreak(mn2);
		this.createMenuItem(mn2, "Zielpfade übertragen...", this::transferTargets);
		this.createMenuItem(mn2, "Zielpfade kopieren", this::exportTargets);
		this.createMenuBreak(mn2);
		this.createMenuItem(mn2, "Zeitnamen ableiten... (Name)", this::createTargetsWithTimenameFromName);
		this.createMenuItem(mn2, "Zeitnamen ableiten... (Zeit)", this::createTargetsWithTimenameFromTime);
		this.createMenuItem(mn2, "Zeitpfade ableiten... (Name)", this::createTargetsWithTimepathFromName);
		this.createMenuItem(mn2, "Zeitpfade ableiten... (Zeit)", this::createTargetsWithTimepathFromTime);
		final var mn3 = this.createMenu(res, "Dateien");
		this.createMenuItem(mn3, "Quelldateien löschen...", this::deleteSourceFilesPermanently);
		this.createMenuItem(mn3, "Quelldateien recyceln...", this::deleteSourceFilesTemporary);
		this.createMenuBreak(mn3);
		this.createMenuItem(mn3, "Zieldateien löschen...", this::deleteTargetFilesPermanently);
		this.createMenuItem(mn3, "Zieldateien recyceln...", this::deleteTargetFilesTemporary);
		this.createMenuBreak(mn3);
		this.createMenuItem(mn3, "Dateien anzeigen...", this::showSourceAndTargetFiles);
		this.createMenuItem(mn3, "Dateien kopieren...", this::copySourceToTargetFiles);
		this.createMenuItem(mn3, "Dateien verschieben...", this::moveSourceToTargetFiles);
		final var mn4 = this.createMenu(res, "Verzeichnisse");
		this.createMenuItem(mn4, "Quellverzeichnisse löschen...", this::deleteSourceFoldersPermanently);
		this.createMenuItem(mn4, "Quellverzeichnisse recyceln...", this::deleteSourceFoldersTemporary);
		this.createMenuBreak(mn4);
		this.createMenuItem(mn4, "Zielverzeichnisse löschen...", this::deleteTargetFoldersPermanently);
		this.createMenuItem(mn4, "Zielverzeichnisse recyceln...", this::deleteTargetFoldersTemporary);
		return res;
	}

	private Text createTableArea(final Composite parent) {
		return new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
	}

	private Label createTableLabel(final Composite parent) {
		final var res = new Label(parent, SWT.WRAP);
		res.setText("Jede Zeile besteht mindestens aus einem Quell- und einen Zieldatenpfad. Alle Werte einer Zeile werden mit Tabulatoren getrennt.");
		return res;
	}

	private MenuItem createMenu(final Menu parent, final String text) {
		final var res = new MenuItem(parent, SWT.CASCADE);
		res.setText(text);
		res.setMenu(new Menu(res));
		return res;
	}

	private Menu createMenuBar(final Decorations parent) {
		final var res = new Menu(parent, SWT.BAR);
		parent.setMenuBar(res);
		return res;
	}

	private MenuItem createMenuItem(final MenuItem parent, final String text, final Runnable onClick) {
		final var res = new MenuItem(parent.getMenu(), SWT.NONE);
		res.setText(text);
		res.addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> onClick.run()));
		return res;
	}

	private MenuItem createMenuBreak(final MenuItem parent) {
		return new MenuItem(parent.getMenu(), SWT.SEPARATOR);
	}

	public final FTSettings settings = new FTSettings();

	private FTDialog_SWT createDialog() {
		final var res = new FTDialog_SWT();
		this.runLater(() -> res.open(this.shell));
		return res;
	}

	Label taskInfo;

	Button taskStop;

	String taskTitle;

	Object taskEntry;

	int taskCount;

	FTDialog_SWT taskCancel;

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
				this.runLater(this::execUpdateEnabled);
			}
			try {
				task.run();
			} catch (final CancellationException ignore) {

			} catch (final Throwable error) {
				this.runLater(() -> this.createDialog() //
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
					this.runLater(this::execUpdateEnabled);
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
		this.display.asyncExec(task);
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

	private void execUpdateEnabled(final boolean value, final Object... targets) {
		for (final var target: targets) {
			if (target instanceof Control) {
				((Control)target).setEnabled(value);
			} else if (target instanceof Menu) {
				final Menu that = (Menu)target;
				that.setEnabled(value);
				this.execUpdateEnabled(value, (Object[])that.getItems());
			} else if (target instanceof MenuItem) {
				final MenuItem that = (MenuItem)target;
				that.setEnabled(value);
				this.execUpdateEnabled(value, that.getMenu());
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
			this.setSync(this.taskInfo::setText, title + " - " + this.taskCount + " - " + entry);
		} else {
			this.setSync(this.taskInfo::setText, " ");
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
				"Duplikate sowie Relative Dateipfade werden verworfen.<br>" + //
				"Die Streuwerte werden in der Pufferdatei <u>" + FTHashes.FILENAME + "</u> zwischengespeichert. " + //
				"Dabei wird die im Elternpfad näheste bzw. die im Anwendungsverzeichnis liegende Pufferdatei verwendet." + //
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

	void cancelProcess(final SelectionEvent event) {
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
