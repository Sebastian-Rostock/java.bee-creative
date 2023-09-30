package bee.creative.app.ft;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
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
import bee.creative.util.Producer;
import bee.creative.util.Properties;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class FTWindow implements Runnable {

	public static void center(final Shell shell) {
		final var srcRect = shell.getBounds();
		final var tgtRect = shell.getDisplay().getPrimaryMonitor().getBounds();
		shell.setLocation(tgtRect.x + ((tgtRect.width - srcRect.width) / 2), tgtRect.y + ((tgtRect.height - srcRect.height) / 2));
	}

	public static void openAndWait(final Shell shell) {
		shell.open();
		AppUtil.wait(shell);
	}

	public final FTSettings settings = new FTSettings();

	public FTWindow() {
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
		this.updateTask();
		this.settings.restore();
		this.settings.persist();
	}

	@Override
	public void run() {
		this.shell.layout();
		FTWindow.center(this.shell);
		FTWindow.openAndWait(this.shell);
		System.exit(0);
	}

	private final Shell shell;

	private final Display display;

	private final Text inputArea;

	private final Menu inputMenu;

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
		this.createMenuItem(mn1, "In Zwischanablage kopieren", this::exportInputs);
		this.createMenuItem(mn1, "Aus Zwischanablage einfügen", this::importInputs);
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
		final var dnd = new DropTarget(res, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		dnd.setTransfer(FileTransfer.getInstance(), TextTransfer.getInstance());
		dnd.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(final DropTargetEvent event) {
				FTWindow.this.importInputsStart(event);
			}

		});
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
		this.createMenuItem(mn1, "In Zwischanablage kopieren", this::exportSources);
		final var mn2 = this.createMenu(res, "Zielpfade");
		this.createMenuItem(mn2, "Fehlerpfade erhalten...", this::cleanupExistingTargets);
		this.createMenuItem(mn2, "Fehlerpfade entfernen...", this::cleanupMissingTargets);
		this.createMenuItem(mn2, "Zielpfade ersetzen...", this::replaceTargetsWithSources);
		this.createMenuItem(mn2, "Zielpfade tauschen...", this::exchangeTargetsWithSources);
		this.createMenuBreak(mn2);
		this.createMenuItem(mn2, "Zielpfade übertragen...", this::transferTargets);
		this.createMenuItem(mn2, "In Zwischanablage kopieren", this::exportTargets);
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

	private <T> T getSync(final Producer<T> getter) {
		final var res = Properties.<T>fromValue(null);
		this.display.syncExec(() -> res.set(getter.get()));
		return res.get();
	}

	private <T> void setSync(final Consumer<? super T> setter, final T value) {
		this.display.asyncExec(() -> setter.set(value));
	}

	private FTDialog createDialog() {
		return new FTDialog(this.shell);
	}

	Label taskInfo;

	Button taskStop;

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
				this.runLater(this::execUpdateEnabled);
			}
			try {
				task.run();
			} catch (final CancellationException ignore) {

			} catch (final Exception error) {
				this.createDialog() //
					.withText(title) //
					.withMessage("Unerwarteter Fehler\n%s", error.toString().replaceAll("&", "&amp;").replaceAll("<", "&lt;")) //
					.withButton("Okay") //
					.open();
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

	private void execExportToClipboard(final List<String> fileList) {
		if (fileList.isEmpty()) return;
		this.setSync(val -> {
			final var clipboard = new Clipboard(this.display);
			clipboard.setContents(new Object[]{val, Strings.join("\n", (Object[])val)}, new Transfer[]{FileTransfer.getInstance(), TextTransfer.getInstance()});
			clipboard.dispose();
		}, fileList.toArray(new String[0]));
	}

	void updateTask() {
		this.display.timerExec(500, this::updateTask);
		if (this.isTaskRunning) {
			final String title = Objects.notNull(this.taskTitle, "?"), entry = String.valueOf(Objects.notNull(this.taskEntry, ""));
			this.taskInfo.setText(title + " - " + this.taskCount + " - " + entry);
		} else {
			this.taskInfo.setText(" ");
		}
	}

	void cleanupExistingInputs() {
		this.createDialog()//
			.withText("Fehlerpfade erhalten") //
			.withTitle("Sollen alle Datenpfade zu existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?") //
			.withMessage("Duplikate sowie relative Datenpfade werden ebenfalls verworfen.") //
			.withButton("Ja", this::cleanupExistingInputsStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Fehlerpfade erhalten") //
			.withTitle("%,d Zeilen bleiben erhalten.", validCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", errorCount) //
			.withButton("Okay") //
			.open();
	}

	void cleanupExistingSources() {
		this.createDialog()//
			.withText("Fehlerquellpfade erhalten") //
			.withTitle("Sollen alle Quellpfade zu existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?") //
			.withMessage("Relative Datenpfade werden ebenfalls verworfen.") //
			.withButton("Ja", this::cleanupExistingSourcesStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Fehlerquellpfade erhalten") //
			.withTitle("%,d Zeilen bleiben erhalten.", validCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", errorCount) //
			.withButton("Okay") //
			.open();
	}

	void cleanupExistingTargets() {
		this.createDialog()//
			.withText("Fehlerzielpfade erhalten") //
			.withTitle("Sollen alle Zielpfade zu existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?") //
			.withMessage("Relative Zielpfade werden ebenfalls verworfen.") //
			.withButton("Ja", this::cleanupExistingTargetsStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Fehlerzielpfade erhalten") //
			.withTitle("%,d Zeilen bleiben erhalten.", validCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", errorCount) //
			.withButton("Okay") //
			.open();
	}

	void cleanupMissingInputs() {
		this.createDialog()//
			.withText("Fehlerpfade entfernen") //
			.withTitle("Sollen alle Datenpfade zu nicht existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?") //
			.withMessage("Duplikate sowie relative Datenpfade werden ebenfalls verworfen.") //
			.withButton("Ja", this::cleanupMissingInputsStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Fehlerpfade entfernt") //
			.withTitle("%,d Zeilen bleiben erhalten.", validCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", errorCount) //
			.withButton("Okay") //
			.open();
	}

	void cleanupMissingSources() {
		this.createDialog()//
			.withText("Fehlerquellpfade entfernen") //
			.withMessage("Sollen alle Quellpfade zu nicht existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?\n " + //
				"Relative Datenpfade werden ebenfalls verworfen.") //
			.withButton("Ja", this::cleanupMissingSourcesStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Fehlerquellpfade entfernt") //
			.withTitle("%,d Zeilen bleiben erhalten.", validCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", errorCount) //
			.withButton("Okay") //
			.open();
	}

	void cleanupMissingTargets() {
		this.createDialog()//
			.withText("Fehlerzielpfade entfernen") //
			.withTitle("Sollen alle Zielpfade zu nicht existierenden Dateien bzw. Verzeichnissen wirklich verworfen werden?") //
			.withMessage("Relative Zielpfade werden ebenfalls verworfen.") //
			.withButton("Ja", this::cleanupMissingTargetsStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Fehlerzielpfade entfernt") //
			.withTitle("%,d Zeilen bleiben erhalten.", validCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", errorCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteInputFilesTemporary() {
		this.createDialog()//
			.withText("Eingabedateien recyceln") //
			.withTitle("Sollen alle Dateien wirklich in den Papierkorb verschoben werden?") //
			.withMessage("Die Zeilen recycelter Dateien werden aus der Pfadliste entfert.") //
			.withButton("Ja", this::deleteInputFilesTemporaryStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Eingabedateien recycelt") //
			.withTitle("%,d Dateien wurden recycelt.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteInputFilesPermanently() {
		this.createDialog()//
			.withText("Eingabedateien löschen") //
			.withTitle("Sollen alle Dateien wirklich endgültig gelöscht werden?") //
			.withMessage("Die Zeilen gelöschter Dateien werden aus der Pfadliste entfert.") //
			.withButton("Ja", this::deleteInputFilesPermanentlyStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Eingabedateien gelöscht") //
			.withTitle("%,d Dateien wurden gelöscht.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteInputFoldersTemporary() {
		this.createDialog()//
			.withText("Eingabeverzeichnisse recyceln") //
			.withTitle("Sollen alle leeren Verzeichnisse wirklich in den Papierkorb verschoben werden?") //
			.withMessage("Die Zeilen recycelter Verzeichnisse werden aus der Pfadliste entfert.") //
			.withButton("Ja", this::deleteInputFoldersTemporaryStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Eingabeverzeichnisse recycelt") //
			.withTitle("%,d Verzeichnisse wurden recycelt.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteInputFoldersPermanently() {
		this.createDialog()//
			.withText("Eingabeverzeichnisse löschen") //
			.withTitle("Sollen alle leeren Verzeichnisse wirklich endgültig gelöscht werden?") //
			.withMessage("Die Zeilen gelöschter Verzeichnisse werden aus der Pfadliste entfert.") //
			.withButton("Ja", this::deleteInputFoldersPermanentlyStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Eingabeverzeichnisse gelöscht") //
			.withTitle("%,d Verzeichnisse wurden gelöscht.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteSourceFilesTemporary() {
		this.createDialog()//
			.withText("Quelldateien recyceln") //
			.withTitle("Sollen alle Dateien wirklich in den Papierkorb verschoben werden?") //
			.withMessage("Die Zeilen recycelter Dateien werden aus der Pfadtabelle entfert.") //
			.withButton("Ja", this::deleteSourceFilesTemporaryStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Quelldateien recycelt") //
			.withTitle("%,d Dateien wurden recycelt.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteSourceFilesPermanently() {
		this.createDialog()//
			.withText("Quelldateien löschen") //
			.withTitle("Sollen alle Dateien wirklich endgültig gelöscht werden?") //
			.withMessage("Die Zeilen gelöschter Dateien werden aus der Pfadtabelle entfert.") //
			.withButton("Ja", this::deleteSourceFilesPermanentlyStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Quelldateien gelöscht") //
			.withTitle("%,d Dateien wurden gelöscht.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteSourceFoldersTemporary() {
		this.createDialog()//
			.withText("Quellverzeichnisse recyceln") //
			.withTitle("Sollen alle leeren Verzeichnisse wirklich in den Papierkorb verschoben werden?") //
			.withMessage("Die Zeilen recycelter Verzeichnisse werden aus der Pfadtabelle entfert.") //
			.withButton("Ja", this::deleteSourceFoldersTemporaryStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Quellverzeichnisse recycelt") //
			.withTitle("%,d Verzeichnisse wurden recycelt.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteSourceFoldersPermanently() {
		this.createDialog()//
			.withText("Quellverzeichnisse löschen") //
			.withTitle("Sollen alle leeren Verzeichnisse wirklich endgültig gelöscht werden?") //
			.withMessage("Die Zeilen gelöschter Verzeichnisse werden aus der Pfadtabelle entfert.") //
			.withButton("Ja", this::deleteSourceFoldersPermanentlyStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Quellverzeichnisse gelöscht") //
			.withTitle("%,d Verzeichnisse wurden gelöscht.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteTargetFilesTemporary() {
		this.createDialog()//
			.withText("Zieldateien recyceln") //
			.withTitle("Sollen alle Dateien wirklich in den Papierkorb verschoben werden?") //
			.withMessage("Die Zeilen recycelter Dateien werden aus der Pfadtabelle entfert.") //
			.withButton("Ja", this::deleteTargetFilesTemporaryStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Zieldateien recycelt") //
			.withTitle("%,d Dateien wurden recycelt.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteTargetFilesPermanently() {
		this.createDialog()//
			.withText("Zieldateien löschen") //
			.withTitle("Sollen alle Dateien wirklich endgültig gelöscht werden?") //
			.withMessage("Die Zeilen gelöschter Dateien werden aus der Pfadtabelle entfert.") //
			.withButton("Ja", this::deleteTargetFilesPermanentlyStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Zieldateien gelöscht") //
			.withTitle("%,d Dateien wurden gelöscht.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteTargetFoldersTemporary() {
		this.createDialog()//
			.withText("Zielverzeichnisse recyceln") //
			.withTitle("Sollen alle leeren Verzeichnisse wirklich in den Papierkorb verschoben werden?") //
			.withMessage("Die Zeilen recycelter Verzeichnisse werden aus der Pfadtabelle entfert.") //
			.withButton("Ja", this::deleteTargetFoldersTemporaryStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Zielverzeichnisse recycelt") //
			.withTitle("%,d Verzeichnisse wurden recycelt.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void deleteTargetFoldersPermanently() {
		this.createDialog()//
			.withText("Zielverzeichnisse löschen") //
			.withTitle("Sollen alle leeren Verzeichnisse wirklich endgültig gelöscht werden?") //
			.withMessage("Die Zeilen gelöschter Verzeichnisse werden aus der Pfadtabelle entfert.") //
			.withButton("Ja", this::deleteTargetFoldersPermanentlyStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Zielverzeichnisse gelöscht") //
			.withTitle("%,d Verzeichnisse wurden gelöscht.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void importInputs() {
		this.importInputsStart();
	}

	void importInputsStart() {
		if (!this.inputArea.isEnabled()) return;
		final var clp = new Clipboard(this.display);
		final var fileList = (String[])clp.getContents(FileTransfer.getInstance());
		clp.dispose();
		if (fileList == null) return;
		this.importInputsRequest(this.getInputText(), Arrays.asList(fileList));
	}

	void importInputsStart(final DropTargetEvent event) {
		if (!this.inputArea.isEnabled()) return;
		event.detail = DND.DROP_COPY;
		try {
			if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
				final var inputText = this.getInputText();
				final var fileList = (String[])FileTransfer.getInstance().nativeToJava(event.currentDataType);
				this.importInputsRequest(inputText, Arrays.asList(fileList));
			} else if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
				final var inputText = this.getInputText();
				final var file = (String)TextTransfer.getInstance().nativeToJava(event.currentDataType);
				this.importInputsRequest(inputText, Arrays.asList(file));
			}
		} catch (final Exception ignore) {}
	}

	public void importInputsRequest(final String inputText, final List<String> fileList) {
		this.runDemo(() -> this.importInputsRespond(inputText));
	}

	public void importInputsRespond(final String inputText) {
		this.setInputText(inputText);
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

	public void exportInputsRespond(final List<String> fileList) {
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

	public void exportSourcesRespond(final List<String> fileList) {
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

	public void exportTargetsRespond(final List<String> fileList) {
		this.execExportToClipboard(fileList);
	}

	void exchangeSourcesWithTargets() {
		this.createDialog()//
			.withText("Quellpfade tauschen") //
			.withTitle("Sollen alle Quellpfade mit deren Zielpfaden getauscht werden?") //
			.withButton("Ja", this::exchangeSourcesWithTargetsStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Quellpfade getauscht") //
			.withTitle("%,d Quellpfade wurden getauscht.", keepCount - failCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void exchangeTargetsWithSources() {
		this.createDialog()//
			.withText("Zielpfade tauschen") //
			.withTitle("Sollen alle Zielpfade mit deren Quellpfaden getauscht werden?") //
			.withButton("Ja", this::exchangeTargetsWithSourcesStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Quellpfade getauscht") //
			.withTitle("%,d Zielpfade wurden getauscht.", keepCount - failCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void replaceSourcesWithTargets() {
		this.createDialog()//
			.withText("Quellpfade ersetzen") //
			.withTitle("Sollen alle Quellpfade durch deren Zielpfade ersetzt werden?") //
			.withButton("Ja", this::replaceSourcesWithTargetsStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Quellpfade ersetzt") //
			.withTitle("%,d Quellpfade wurden ersetzt.", keepCount - failCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void replaceTargetsWithSources() {
		this.createDialog()//
			.withText("Zielpfade ersetzen") //
			.withTitle("Sollen alle Zielpfade mit deren Quellpfaden ersetzt werden?") //
			.withButton("Ja", this::replaceTargetsWithSourcesStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Zielpfade ersetzt") //
			.withTitle("%,d Zielpfade wurden ersetzt.", keepCount - failCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void transferInputs() {
		this.createDialog()//
			.withText("Eingabepfade übertragen") //
			.withTitle("Sollen alle Datenpfade wirklich als Quell- und Zielpfade in die Pfadtabelle übernommen werden?") //
			.withMessage("Duplikate sowie relative Datenpfade werden ignoriert.") //
			.withButton("Ja", this::transferInputsStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Eingabepfade übertragen") //
			.withTitle("%,d Eingabepfade wurden übernommen.", keepCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void transferSources() {
		this.createDialog()//
			.withText("Quellpfade übertragen") //
			.withTitle("Sollen alle Eingabepfade in der Pfadliste mit allen Quellpfaden ersetzt werden?") //
			.withButton("Ja", this::transferSourcesStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Quellpfade übertragen") //
			.withTitle("%,d Quellpfade wurden übernommen.", keepCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void transferTargets() {
		this.createDialog()//
			.withText("Zielpfade übertragen") //
			.withTitle("Sollen alle Zielpfade wirklich als Eingabepfade in die Pfadliste übernommen werden?") //
			.withMessage("Duplikate sowie relative Zielpfade werden ignoriert.") //
			.withButton("Ja", this::transferTargetsStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Zielpfade übertragen") //
			.withTitle("%,d Zielpfade wurden übernommen.", keepCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void resolveInputToFiles() {
		this.createDialog()//
			.withText("Dateien auflösen") //
			.withTitle("Sollen alle Verzeichnispfade wirklich durch die Pfade aller darin enthaltenen Dateien ersetzt werden?")
			.withMessage("Die Dateiauflösung wird in allen Unterverzeichnissen fortgesetzt. " + //
				"Duplikate sowie relative Datenpfade werden verworfen. " + //
				"Dateipfade bleiben erhalten. ") //
			.withButton("Ja", this::resolveInputToFilesStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Dateien aufgelöst") //
			.withTitle("%,d Dateipfade wurden ermittelt.", keepCount) //
			.withMessage("%,d Datenpfade wurden verworfen.", dropCount) //
			.withButton("Okay") //
			.open();
	}

	void resolveInputToFolders() {
		this.createDialog()//
			.withText("Verzeichnisse auflösen") //
			.withTitle("Sollen alle Verzeichnispfade wirklich um die Pfade aller darin enthaltenen Verzeichnisse ergänzt werden?")
			.withMessage("Die Verzeichnisauflösung wird in allen Unterverzeichnissen fortgesetzt. " + //
				"Duplikate sowie relative Datenpfade werden verworfen. " + //
				"Dateipfade werden ebenfalls verworfen. ") //
			.withButton("Ja", this::resolveInputToFoldersStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Verzeichnisse aufgelöst") //
			.withMessage("%,d Verzeichnispfade wurden ermittelt.", keepCount) //
			.withMessage("%,d Datenpfade wurden verworfen.", dropCount) //
			.withButton("Okay") //
			.open();
	}

	void refreshInputFiles() {
		this.createDialog()//
			.withText("Dateien erneuern") //
			.withTitle("Sollen alle alten Dateien wirklich erneuert werden?") //
			.withMessage("Beim Erneuern werden alle Dateien, die vor mehr als der unten angegebenen Anzahl " + //
				"an Tagen erstellt wurden, kopiert und durch ihre Kopien ersetzt. " + //
				"Die dazu temporär erzeugten Kopien tragen die Dateiendung .tempcopy. " + //
				"Die Zeilen erneuerter Dateien werden aus der Pfadliste entfert. ") //
			.withOption("Dateialter in Tagen", this.settings.copyFilesTimeFilter) //
			.withButton("Ja", this::refreshInputFilesStart) //
			.withButton("Nein") //
			.open();
	}

	void refreshInputFilesStart() {
		final var inputText = this.getInputText();
		final var copyTime = this.settings.copyFilesTimeFilter.getValue();
		this.runTask("Dateierneuerung", () -> this.refreshInputFilesRequest(inputText, copyTime));
	}

	public void refreshInputFilesRequest(final String inputText, final long copyTime) {
		this.runDemo(() -> this.refreshInputFilesRespond(inputText, 1234567, 7890));
	}

	public void refreshInputFilesRespond(final String keepText, final int keepCount, final int dropCount) {
		this.setInputText(keepText);
		this.createDialog() //
			.withText("Dateien erneuert") //
			.withTitle("%,d Dateien wurden erneuert.", dropCount) //
			.withMessage("%,d Zeilen wurden nicht verarbeitet.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void createTableWithClones() {
		this.createDialog() //
			.withText("Duplikate übernehmen") //
			.withTitle("Sollen alle Dateien auf Duplikate hin untersucht werden?") //
			.withMessage("Beim Suchen von Duplikaten werden alle Dateien zunächst bezüglich ihrer Dateigröße partitioniert. " + //
				"Die Dateien innerhalb einer Dateigrößenpartition werden dann bezüglich ihres SHA-256-Streuwerts partitioniert. " + //
				"Dieser Streuwert wird aus höchstens der unten eingestellten Anzanl an Bytes jeweils ab dem Dateibeginn und dem Dateiende berechnet. " + //
				"Schließlich werden die Dateien innerhalb einer Streuwertpartition nach ihrem Dateiinhalt partitioniert. " + //
				"Als Dateiinhalt wird höchstens die unten eingestellte Anzanl an Bytes jeweils ab dem Dateibeginn und dem Dateiende betrachtet.\n" + //
				"Der Pfad der ersten Datei einer Dateiinhaltspartition wird als Quellpfad verwendet. " + //
				"Die Pfade der anderen Dateien der Partitionen werden diesem als Zielpfade zugeordnet. " + //
				"Jedem Zielpfad wird zudem der Streuwert sowie die Dateigröße informativ angefügte. " + //
				"Quellpfade ohne Zielpfade werden verworfen. " + //
				"Duplikate sowie Relative Dateipfade werden verworfen.\n" + //
				"Die Streuwerte werden in der Pufferdatei " + FTHashes.FILENAME + " zwischengespeichert. " + //
				"Dabei wird die im Elternpfad näheste bzw. die im Anwendungsverzeichnis liegende Pufferdatei verwendet.") //
			.withOption("Puffergröße für Streuwert", this.settings.findClonesHashSize) //
			.withOption("Puffergröße für Dateivergleich", this.settings.findClonesTestSize) //
			.withButton("Ja", this::createTableWithClonesStart) //
			.withButton("Nein") //
			.open();
	}

	void createTableWithClonesStart() {
		final var inputText = this.getInputText();
		final var hashSize = this.settings.findClonesHashSize.getValue();
		final var testSize = this.settings.findClonesTestSize.getValue();
		this.settings.persist();
		this.runTask("Duplikateübernahme", () -> this.createTableWithClonesRequest(inputText, hashSize, testSize));
	}

	public void createTableWithClonesRequest(final String inputText, final long hashSize, final long testSize) throws Exception {
		this.runDemo(() -> this.createTableWithClonesRespond("", 1234567, 7890));
	}

	public void createTableWithClonesRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withText("Duplikate übernommen") //
			.withTitle("%,d Duplikate wurden gefunden.", keepCount) //
			.withMessage("%,d Datenpfade konnten nicht verarbeitet werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void createTargetsWithTimenameFromName() {
		this.createDialog() //
			.withText("Zeitnamen ableiten") //
			.withTitle("Sollen die Zielnamen wirklich aus den Zeitpunkten in den Quellnamen abgeleitet werden?") //
			.withMessage("Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft " + //
				"verschobenen Zeitpunkten, die im Quellnamen mit beliebigen Trennzeichen angegeben sind. " + //
				"Die Zielpfade haben das Format {EP}\\JJJJ-MM-TT hh.mm.ss{NE}, wobei {EP} für den " + //
				"Elternverzeichnispfad und {NE} für die kleingeschriebene Namenserweiterung der Quelldatei stehen.") //
			.withOption("Zeitkorrektur in Sekunden", this.settings.moveFilesTimeOffset) //
			.withButton("Ja", this::createTargetsWithTimenameFromNameStart) //
			.withButton("Nein") //
			.open();
	}

	void createTargetsWithTimenameFromNameStart() {
		final var tableText = this.getTableText();
		final var moveTime = this.settings.moveFilesTimeOffset.getValue();
		this.settings.persist();
		this.runTask("Zeitnamensableitung", () -> this.createTargetsWithTimenameFromNameRequest(tableText, moveTime));
	}

	public void createTargetsWithTimenameFromNameRequest(final String tableText, final long moveTime) {
		this.runDemo(() -> this.createTargetsWithTimenameFromNameRespond(tableText, 1234567, 7890));
	}

	public void createTargetsWithTimenameFromNameRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withText("Zeitnamen abgeleitet") //
			.withTitle("%,d Zielpfade wurden angepasst.", keepCount - failCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void createTargetsWithTimepathFromName() {
		this.createDialog() //
			.withText("Zeitpfade ableiten") //
			.withTitle("Sollen die Zielpfade wirklich aus den Zeitpunkten in den Quellnamen abgeleitet werden?") //
			.withMessage("Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft " + //
				"verschobenen Zeitpunkten, die im Quellnamen mit beliebigen Trennzeichen angegeben sind.\n " + //
				"Die Zielpfade haben das Format {GP}\\JJJJ-MM_{EN}\\JJJJ-MM-TT hh.mm.ss{NE}, wobei {GP} für den " + //
				"Großelternverzeichnispfad, {EN} für den Elternverzeichnisnamen und {NE} für die kleingeschriebene " + //
				"Namenserweiterung der Quelldatei stehen.") //
			.withOption("Zeitkorrektur in Sekunden", this.settings.moveFilesTimeOffset) //
			.withButton("Ja", this::createTargetsWithTimepathFromNameStart) //
			.withButton("Nein") //
			.open();
	}

	void createTargetsWithTimepathFromNameStart() {
		final var tableText = this.getTableText();
		final var moveTime = this.settings.moveFilesTimeOffset.getValue();
		this.settings.persist();
		this.runTask("Zeitpfadableitung", () -> this.createTargetsWithTimepathFromNameRequest(tableText, moveTime));
	}

	public void createTargetsWithTimepathFromNameRequest(final String tableText, final long moveTime) {
		this.runDemo(() -> this.createTargetsWithTimepathFromNameRespond(tableText, 1234567, 7890));
	}

	public void createTargetsWithTimepathFromNameRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withText("Zeitpfade abgeleitet") //
			.withTitle("%,d Zielpfade wurden angepasst.", keepCount - failCount) //
			.withMessage("%s Zeilen konnten nicht angepasst werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void createTargetsWithTimenameFromTime() {
		this.createDialog() //
			.withText("Zeitnamen aus Änderungszeitpunkten ableiten") //
			.withTitle("Sollen die Zielnamen wirklich aus den Änderungszeitpunkten der Quelldateien abgeleitet werden?") //
			.withMessage("Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft " + //
				"verschobenen Änderungszeitpunkten der Quelldateien.\n " + //
				"Die Zielpfade haben das Format {EP}\\JJJJ-MM-TT hh.mm.ss{NE}, wobei {EP} für den " + //
				"Elternverzeichnispfad und {NE} für die kleingeschriebene Namenserweiterung der Quelldatei stehen.") //
			.withOption("Zeitkorrektur in Sekunden", this.settings.moveFilesTimeOffset) //
			.withButton("Ja", this::createTargetsWithTimenameFromTimeStart) //
			.withButton("Nein") //
			.open();
	}

	void createTargetsWithTimenameFromTimeStart() {
		final var tableText = this.getTableText();
		final var moveTime = this.settings.moveFilesTimeOffset.getValue();
		this.settings.persist();
		this.runTask("Zeitnamensableitung", () -> this.createTargetsWithTimenameFromTimeRequest(tableText, moveTime));
	}

	public void createTargetsWithTimenameFromTimeRequest(final String tableText, final long moveTime) {
		this.runDemo(() -> this.createTargetsWithTimenameFromTimeRespond(tableText, 1234567, 7890));
	}

	public void createTargetsWithTimenameFromTimeRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withText("Zeitnamen abgeleitet") //
			.withTitle("%,d Zielpfade wurden angepasst.", keepCount - failCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void createTargetsWithTimepathFromTime() {
		this.createDialog() //
			.withText("Zeitpfade aus Änderungszeitpunkten ableiten") //
			.withTitle("Sollen die Zielpfade wirklich aus den Änderungszeitpunkten der Quelldateien abgeleitet werden?") //
			.withMessage("Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft " + //
				"verschobenen Änderungszeitpunkten der Quelldateien.\n " + //
				"Die Zielpfade haben das Format {GP}\\JJJJ-MM_{EN}\\JJJJ-MM-TT hh.mm.ss{NE}, wobei {GP} für den " + //
				"Großelternverzeichnispfad, {EN} für den Elternverzeichnisnamen und {NE} für die kleingeschriebene " + //
				"Namenserweiterung der Quelldatei stehen.") //
			.withOption("Zeitkorrektur in Sekunden", this.settings.moveFilesTimeOffset) //
			.withButton("Ja", this::createTargetsWithTimepathFromTimeStart) //
			.withButton("Nein") //
			.open();
	}

	void createTargetsWithTimepathFromTimeStart() {
		final var tableText = this.getTableText();
		final var moveTime = this.settings.moveFilesTimeOffset.getValue();
		this.settings.persist();
		this.runTask("Zeitpfadableitung", () -> this.createTargetsWithTimepathFromTimeRequest(tableText, moveTime));
	}

	public void createTargetsWithTimepathFromTimeRequest(final String tableText, final long moveTime) {
		this.runDemo(() -> this.createTargetsWithTimepathFromTimeRespond(tableText, 1234567, 7890));
	}

	public void createTargetsWithTimepathFromTimeRespond(final String keepText, final int keepCount, final int failCount) {
		this.setTableText(keepText);
		this.createDialog() //
			.withText("Zeitpfade abgeleitet") //
			.withTitle("%,d Zielpfade wurden angepasst.", keepCount - failCount) //
			.withMessage("%s Zeilen konnten nicht angepasst werden.", failCount) //
			.withButton("Okay") //
			.open();
	}

	void copySourceToTargetFiles() {
		this.createDialog() //
			.withText("Dateien kopieren") //
			.withTitle("Sollen alle Dateien wirklich nicht ersetzend kopiert werden?") //
			.withMessage("Die Zeilen erfolgreich kopierter Dateien werden aus der Pfadtabelle entfernt.") //
			.withButton("Ja", this::copySourceToTargetFilesStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Dateien kopiert") //
			.withTitle("%,d Dateien wurden kopiert.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void moveSourceToTargetFiles() {
		this.createDialog() //
			.withText("Dateien verschieben") //
			.withTitle("Sollen alle Dateien wirklich nicht ersetzend verschoben werden?") //
			.withMessage("Die Zeilen erfolgreich verschobener Dateien werden aus der Pfadtabelle entfernt.") //
			.withButton("Ja", this::moveSourceToTargetFilesStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Dateien verschoben") //
			.withTitle("%,d Dateien wurden verschoben.", dropCount) //
			.withMessage("%,d Zeilen konnten nicht verarbeitet werden.", keepCount) //
			.withButton("Okay") //
			.open();
	}

	void showSourceAndTargetFiles() {
		this.createDialog() //
			.withText("Dateipaare anzeigen") //
			.withTitle("Sollen die Quell- und Zieldateien wirklich angezeigt werden?") //
			.withMessage("Die Dateien werden dabei als Symlinks in ein temporäres Verzeichnis eingefügt. " + //
				"Die Quelldateien werden als -ORIGINAL- gekennzeichnet, die Zieldateien als -DUPLIKAT-. " + //
				"Das temporäre Verzeichnis wird abschließend angezeigt.\n" + //
				"Das Erzeugen von Symlinks benötigt Administrator-Rechte!") //
			.withButton("Ja", this::showSourceAndTargetFilesStart) //
			.withButton("Nein") //
			.open();
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
			.withText("Dateipaare angezeigt") //
			.withTitle("%,d Symlinks wurden in das Verzeichnis eingefügt:", linkCount) //
			.withMessage(linkPath) //
			.withButton("Okay") //
			.open();
	}

	void cancelProcess(final SelectionEvent event) {
		(this.taskCancel = this.createDialog()) //
			.withText(Objects.notNull(this.taskTitle, "Abbrechen")) //
			.withTitle("Sollen der Vorgang wirklich abgebrochen werden?") //
			.withButton("Ja", this::cancelProcessStart) //
			.withButton("Nein") //
			.open();
	}

	synchronized void cancelProcessStart() {
		if (this.isTaskCanceled || !this.isTaskRunning) return;
		this.isTaskCanceled = true;
		this.execUpdateEnabled();
	}

}
