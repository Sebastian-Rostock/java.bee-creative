package bee.creative.app.ft;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.UIManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import bee.creative.io.IO;
import bee.creative.util.Consumer;
import bee.creative.util.Filter;
import bee.creative.util.Producer;

public class AppWindow {

	public static void main(String args[]) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		Display display = Display.getDefault();
		AppWindow shell = new AppWindow(display);
		FTWindow.center(shell.shell);
		FTWindow.openAndWait(shell.shell);
	}

	private Text textArea;

	private AppDialog menu;

	public final Shell shell;

	public final Display display;

	private MenuItem undoMenuItem;

	private MenuItem redoMenuItem;

	public AppWindow(Display display) {
		this.shell = new Shell(display, SWT.SHELL_TRIM);
		this.shell.setLayout(new GridLayout(1, false));

		this.display = display;

		this.createMenuBar(this.shell, men -> {
			this.undoMenuItem = this.createMenuItem(men, "icon-undo.png", "", this::runUndoEntries);
			this.undoMenuItem.setAccelerator(SWT.CTRL | 'Z');

			this.redoMenuItem = this.createMenuItem(men, "icon-redo.png", "", this::runRedoEntries);
			this.redoMenuItem.setAccelerator(SWT.CTRL | 'Y');

			this.createMenu(men, null, "Swap...", swap -> {

			});

			this.createMenu(men, null, "Filtern...", save -> {
				this.createMenuItem(save, "icon-file.png", "...nach Datei", this::askFilterSourcesByFile);
				this.createMenuItem(save, "icon-folder.png", "...nach Ordner", this::askFilterSourcesByFolder);
				this.createMenuItem(save, "icon-size.png", "...nach Größe", this::askFilterSourcesByLength);
				this.createMenuItem(save, "icon-time.png", "...nach Änderung", this::askFilterSourcesByModification);
				this.createMenuItem(save, "icon-made.png", "...nach Erzeugung", this::askFilterSourcesByCreation);
				this.createMenuItem(save, "icon-name.png", "...nach Datenpfad", this::askFilterSourcesByPattern);
			});
			this.createMenu(men, null, "Sortieren...", save -> {
				this.createMenuItem(save, null, "...nach Größe", this::runSortEntriesBySourceSize);
				this.createMenuItem(save, null, "...nach Änderung", this::runSortEntriesBySourceTime);
				this.createMenuItem(save, null, "...nach Erzeugung", this::runSortEntriesBySourceMade);
				this.createMenuItem(save, null, "...nach Datenpfad", this::runSortEntriesBySourcePath);
			});
			this.createMenu(men, null, "Hash", hash -> {
				this.createMenuItem(hash, null, "Duplikate erkennen", null);
				this.createMenuBreak(hash);
				this.createMenuItem(hash, null, "Cache anlegen", null);
				this.createMenuItem(hash, null, "Cache aktualisieren", null);
			});
			this.createMenu(men, null, "Speicher", save -> {
				this.createMenuItem(save, null, "...in Variable speichern", this::runSaveEntriesToVar);
				this.createMenuItem(save, null, "...aus Variable einfügen", this::runLoadEntriesFromVar);
				this.createMenuBreak(save);
				this.createMenuItem(save, null, "...in Zwischenablage speichern", this::runSaveEntriesToClip);
				this.createMenuItem(save, null, "...aus Zwischenablage einfügen", this::runLoadEntriesFromClip);

			});
			this.createMenu(men, null, "Dateien", save -> {
				this.createMenuItem(save, null, "Dateien löschen", null);
				this.createMenuItem(save, null, "Dateien recyclen", null);
				this.createMenuItem(save, null, "Dateien erneuern", null);
				this.createMenuBreak(save);
				this.createMenuItem(save, null, "Dateien anzeigen", null);
				this.createMenuItem(save, null, "Dateien kopieren", null);
				this.createMenuItem(save, null, "Dateien umbenennen", null);
			});
			this.createMenu(men, null, "Ordner", save -> {
				this.createMenuItem(save, null, "Ordner löschen", null);
				this.createMenuItem(save, null, "Ordner recyclen", null);
			});
			this.taskStop = this.createMenuItem(men, null, "Abbrechen", this::runCancelProcesses);
		});

		this.menu = new AppDialog(this.shell);

		this.textArea = new Text(this.shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		this.textArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.textArea.addVerifyListener(l -> {
			this.runEditEntries();
		});

		final var dnd = new DropTarget(this.textArea, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		dnd.setTransfer(FileTransfer.getInstance(), TextTransfer.getInstance());
		dnd.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(final DropTargetEvent event) {
				AppWindow.this.runDropEntries(event);
			}

		});

		this.taskInfo = new Label(this.shell, SWT.NONE);
		this.taskInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.taskInfo.setText("New Label");

		this.shell.setText("SWT Application");
		this.shell.setSize(600, 400);

		this.thread = new AppQueue() {

			@Override
			public void onError(AppProcess proc, Throwable error) {
				AppWindow.this.openDialog() //
					.useTitle(proc.title) //
					.useMessage("Unerwarteter Fehler\n%s", error.toString().replaceAll("&", "&amp;").replaceAll("<", "&lt;"));
			}

			@Override
			public void onSelect(AppProcess proc) {
				// TODO erfassen und update
			}

		};

		this.editWait = System.currentTimeMillis() + Integer.MIN_VALUE;

		this.runUndoEnable();
		this.runRedoEnable();

	}

	private Image getIcon(String string) {
		try {
			return new Image(this.display, AppWindow.class.getResourceAsStream(string));
		} catch (Exception e) {
			System.out.println(string + " " + e);
			return null;
			// TODO: handle exception
		}
	}

	public String getInput() {
		return this.display.syncCall(this.textArea::getText);
	}

	public void setInput(String value) {
		this.display.syncExec(() -> this.textArea.setText(value));
	}

	public List<AppEntry> getInputEntries() {
		return AppEntry.parseAll(this.getInput());
	}

	public void setInputEntries(Iterable<AppEntry> value) {
		this.setInput(AppEntry.printAll(value));
	}

	public AppDialog openDialog() {
		var res = new AppDialog(this.shell);
		this.shell.getDisplay().asyncExec(res::open);
		return res.useFocus(true);
	}

	public void runShowInfo() {
	}

	static class AppState {

		private Point selection;

		private byte[] data;

		public AppState(Text widget) {
			try (var bd = new ByteArrayOutputStream(); var cd = new OutputStreamWriter(new GZIPOutputStream(bd), StandardCharsets.UTF_8)) {
				IO.writeChars(cd, widget.getText());
				this.data = bd.toByteArray();
			} catch (IOException ignore) {}
			this.selection = widget.getSelection();
		}

		public void apply(Text widget) {
			try (var bd = new GZIPInputStream(new ByteArrayInputStream(this.data)); var cd = new InputStreamReader(bd, StandardCharsets.UTF_8)) {
				widget.setText(IO.readChars(cd));
			} catch (IOException ignore) {}
			widget.setSelection(this.selection);
		}

	}

	private final LinkedList<AppState> undoQueue = new LinkedList<>();

	private final LinkedList<AppState> redoQueue = new LinkedList<>();

	/** Dieses Feld speichert den Zeitpunkt, ab welchem Änderungen über {@link #runEditEntries()} in den {@link #undoQueue} übernommen werden dürfen. */
	private long editWait = System.currentTimeMillis();

	public void runEditEntries() {
		this.display.syncExec(() -> {
			final var time = System.currentTimeMillis();
			if ((time - this.editWait) >= 0) {
				this.undoQueue.addFirst(new AppState(this.textArea));
				this.redoQueue.clear();
				this.runUndoEnable();
				this.runRedoEnable();
			}
			this.editWait = time + 500;
		});
	}

	private void runUndoEnable() {
		this.undoMenuItem.setEnabled(!this.undoQueue.isEmpty());
	}

	public void runUndoEntries() {
		this.runTask("Undo", proc -> this.display.syncExec(() -> {
			if (this.undoQueue.isEmpty()) return;
			final var state = this.undoQueue.removeFirst();
			this.runUndoEnable();
			this.redoQueue.addFirst(new AppState(this.textArea));
			this.runRedoEnable();
			this.editWait = System.currentTimeMillis() + Integer.MAX_VALUE;
			state.apply(this.textArea);
		}));
	}

	private void runRedoEnable() {
		this.redoMenuItem.setEnabled(!this.redoQueue.isEmpty());
	}

	public void runRedoEntries() {
		this.runTask("Redo", proc -> this.display.syncExec(() -> {
			if (this.redoQueue.isEmpty()) return;
			final var state = this.redoQueue.removeFirst();
			this.runRedoEnable();
			this.undoQueue.addFirst(new AppState(this.textArea));
			this.runUndoEnable();
			this.editWait = System.currentTimeMillis() + Integer.MAX_VALUE;
			state.apply(this.textArea);
		}));
	}

	void runDropEntries(final DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
		try {
			if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
				final var fileList = (String[])FileTransfer.getInstance().nativeToJava(event.currentDataType);
				this.runPushEntries(Arrays.asList(fileList));
			} else if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
				final var file = (String)TextTransfer.getInstance().nativeToJava(event.currentDataType);
				this.runPushEntries(Arrays.asList(file));
			}
		} catch (final Exception ignore) {}
	}

	public void runPushEntries(final List<String> fileList) {
		var input = this.getInput();
		this.runTask("Eingabepfade anfügen", proc -> {
			final var result = AppEntry.list();
			if (!input.isEmpty()) {
				result.add(new AppEntry(input, ""));
			}
			this.runItems(proc, fileList, file -> result.add(new AppEntry(file, "")), null);
			this.setInputEntries(result);
		});
	}

	public void runFilterSources(String title, Producer<Filter<AppItem>> filterBuilder) {
		this.runTask(title, proc -> {
			var filter = filterBuilder.get();
			var inputList = this.getInputEntries();
			var resultList = AppEntry.list();
			this.runItems(proc, inputList, entry -> {
				if (filter.accept(entry.source)) {
					resultList.add(entry);
				}
			}, resultList::add);
			this.setInputEntries(resultList);
		});
	}

	public void askFilterSourcesByFile() {
		this.openDialog() //
			.useTitle("Eingabepfad nach Datei filtern") //
			.useMessage("Möchten Sie Eingabepfade zu existierenden Dateien behalten oder entfernen?\nRelative Eingabepfade werden entfernt.") //
			.useButton("Dateien behalten", () -> this.runFilterSourcesByFile(true)) //
			.useButton("Dateien entfernen", () -> this.runFilterSourcesByFile(false)) //
		;
	}

	public void runFilterSourcesByFile(boolean isKeep) {
		this.runFilterSources(isKeep ? "Dateipfade behalten" : "Dateipfade entfernen",
			() -> source -> (source.fileOrNull() != null) && (source.fileOrNull().isFile() == isKeep));
	}

	public void askFilterSourcesByFolder() {
		this.openDialog() //
			.useTitle("Eingabepfad nach Ordner filtern") //
			.useMessage("Möchten Sie Eingabepfade zu existierenden Ordnern behalten oder entfernen?\nRelative Eingabepfade werden entfernt.") //
			.useButton("Ordnern behalten", () -> this.runFilterSourcesByFolder(true)) //
			.useButton("Ordnern entfernen", () -> this.runFilterSourcesByFolder(false)) //
		;
	}

	public void runFilterSourcesByFolder(boolean isKeep) {
		this.runFilterSources(isKeep ? "Ordnerpfade behalten" : "Ordnerpfade entfernen",
			() -> source -> (source.fileOrNull() != null) && (source.fileOrNull().isDirectory() == isKeep));
	}

	public void askFilterSourcesByPattern() { //
		this.openDialog() //
			.useTitle("Eingabepfad nach Muster filtern") //
			.useMessage("Ein Eingabepfad gilt als Treffer, wenn den unten angegebenen regulären Ausdruck darin einen Treffer findet.") //
			.useOption("Regulärer Ausdruck", this.settings.filterPattern) //
			.useButton("Treffer behalten", () -> this.runFilterSourcesByPattern(true)) //
			.useButton("Treffer entfernen", () -> this.runFilterSourcesByPattern(false)) //
		;
	}

	public void runFilterSourcesByPattern(boolean isKeep) {
		final var value = this.settings.filterPattern.putValue().getValue();
		this.runFilterSources(isKeep ? "Muster behalten" : "Muster entfernen", () -> {
			var pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
			return source -> pattern.matcher(source.text()).find() == isKeep;
		});
	}

	public void askFilterSourcesByLength() {
		this.openDialog() //
			.useTitle("Eingabepfad nach Dateigröße filtern") //
			.useMessage("Ein Eingabepfad gilt als Treffer, wenn er eine Datei mit einer Dateigröße innerhalb der unten angegebenen Grenzen besitzt."
				+ " Ein Eingabepfad wird verworfen, wenn er ein Verzeichnis angibt.") //
			.useOption("Dateigröße", this.settings.filterLengthMin) //
			.useButton("Kleinere behalten", () -> this.runFilterSourcesByLength(true)) //
			.useButton("Größere behalten", () -> this.runFilterSourcesByLength(false)) //
		;
	}

	public void runFilterSourcesByLength(boolean isKeep) {
		final var minLength = this.settings.filterLengthMin.getValue();
		final var maxLength = this.settings.filterLengthMax.getValue();
		this.runFilterSources(isKeep ? "Dateigröße behalten" : "Dateigröße entfernen", () -> source -> (source.sizeOrNull() != null)
			&& (((minLength <= source.sizeOrNull().longValue()) && (source.sizeOrNull().longValue() <= maxLength)) == isKeep));
	}

	public void askFilterSourcesByCreation() { //
		this.openDialog() //
			.useTitle("Eingabepfad nach Erzeugungszeitpunkt filtern") //
			.useMessage("Ein Eingabepfad gilt als Treffer, wenn er eine Datei mit einem Erzeugungszeitpunkt innerhalb der unten angegebenen Grenzen besitzt."
				+ " Ein Eingabepfad wird verworfen, wenn er ein Verzeichnis angibt.") //
			.useOption("Erzeugungszeitpunkt", this.settings.filterCreationMin) //
			.useButton("Ältere behalten", null) //
			.useButton("Jüngere behalten", null) //
		;
		// try {
		// BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
		// FileTime fileTime = attr.creationTime();
		// } catch (IOException ex) {
		// // handle exception
		// }
	}

	public void askFilterSourcesByModification() { //
		this.openDialog() //
			.useTitle("Eingabepfad nach Änderungszeitpunkt filtern") //
			.useMessage("Ein Eingabepfad gilt als Treffer, wenn er eine Datei mit einem Änderungszeitpunkt innerhalb der unten angegebenen Grenzen besitzt."
				+ " Ein Eingabepfad wird verworfen, wenn er ein Verzeichnis angibt.") //
			.useOption("Änderungszeitpunkt", this.settings.filterModificationMin) //
			.useButton("Ältere behalten", null) //
			.useButton("Jüngere behalten", null) //
		;
		// File f;
		// f.lastModified();
	}

	public void runSortEntries(String title, Comparator<AppEntry> order) {
		var input = this.getInput();
		this.runTask(title, proc -> {
			var resultList = this.getInputEntries();
			resultList.sort(order);
			this.setInputEntries(resultList);
		});
	}

	public void runSortEntriesReverse() {
	}

	public void runSortEntriesBySourcePath() {
	}

	public void runSortEntriesBySourceTime() {
	}

	public void runSortEntriesBySourceSize() {
	}

	public void runSortEntriesBySourceMade() {
	}

	public void runSaveEntriesToClip() { // file
	}

	// tabelle als text in ram puffer
	public void runSaveEntriesToVar() {
	}

	public void runLoadEntriesFromVar() {
	}

	public void runLoadEntriesFromClip() {
		this.runTask("Paste", proc -> this.display.syncExec(() -> {
			final var clp = new Clipboard(this.display);
			final var fileList = (String[])clp.getContents(FileTransfer.getInstance());
			clp.dispose();
			if (fileList == null) return;
			this.runPushEntries(Arrays.asList(fileList));
		}));

	}

	public void runSwapEntriesWithVar() {
	}

	public void runSwapSourcesWithTargets() {
	}

	public void runDropSources() {
	}

	public void runMakeTargetTimenameBySourceName() {
	}

	public void runMakeTargetTimenameBySourceTime() {
	}

	public void runMakeTargetTimepathBySourceName() {
	}

	public void runMakeTargetTimepathBySourceTime() {
	}

	public void runMakeEntriesByCloneSource() {
	}

	public void runMakeEntriesByUniqueSource() {
	}

	// neuen hash puffer tabelle in source pfaden anlegen
	public void runCreateHashes() {
	}

	public void runUpdateHashes() {
	}

	public void runReplaceByUnion() { // entries = entries union var
	}

	public void runReplaceByException() { // entries = entries except var
	}

	public void runReplaceByIntersection() { // entries = entries intersect var
	}

	public void runReplaceByPattern() { // target = regex(source)
	}

	public void runResolveSourceToFiles() {
	}

	public void runResolveSourcesToFolders() {
	}

	public void runDeleteSourceFilesTemporary() {
	}

	public void runDeleteSourceFilesPermanently() {
	}

	public void runDeleteSourceFoldersTemporary() {
	}

	public void runDeleteSourceFoldersPermanently() {
	}

	public void runCopySourcesToTargets() throws Exception {
	}

	public void runMoveSourcesToTargets() throws Exception {
	}

	public void runShowSourcesAndTargets() throws Exception {
	}

	FTSettings settings = new FTSettings();

	private MenuItem createMenu(final Menu parent, String icon, final String text, Consumer<Menu> setup) {
		final var res = new MenuItem(parent, SWT.CASCADE);
		if (icon != null) {
			res.setImage(this.getIcon(icon));
		}
		if (text != null) {
			res.setText(text);
		}
		res.setMenu(new Menu(res));
		setup.set(res.getMenu());
		return res;
	}

	// private MenuItem createMenu(final MenuItem parent, final String text, Consumer<MenuItem> setup) {
	// return this.createMenu(parent.getMenu(), text, setup);
	// }

	private Menu createMenuBar(final Decorations parent, Consumer<Menu> setup) {
		final var res = new Menu(parent, SWT.BAR);
		parent.setMenuBar(res);
		setup.set(res);
		return res;
	}

	private MenuItem createMenuItem(final Menu parent, String icon, final String text, final Runnable onClick) {
		final var res = new MenuItem(parent, SWT.NONE);
		if (icon != null) {
			res.setImage(this.getIcon(icon));
		}
		if (text != null) {
			res.setText(text);
		}
		if (onClick != null) {
			res.addListener(SWT.Selection, event -> onClick.run());
		}
		return res;
	}

	private MenuItem createMenuBreak(final Menu parent) {
		return new MenuItem(parent, SWT.SEPARATOR);
	}

	Label taskInfo;

	MenuItem taskStop;

	AppQueue thread;

	/** Diese Methode führt die gegebene Berechnung {@code task} mit dem gegebenen Titel {@code title} in einem {@link Thread} aus. */
	public void runTask(final String title, final AppTask task) {
		this.thread.push(title, task);
	}

	private <GItem> void runItems(AppProcess proc, final Collection<GItem> items, final Consumer<GItem> regular, final Consumer<GItem> canceled) {
		final var iter = items.iterator();
		proc.steps += items.size();
		if (regular != null) {
			while (iter.hasNext() && !proc.isCanceled) {
				regular.set(iter.next());
				proc.steps--;
			}
		}
		if (canceled != null) {
			while (iter.hasNext()) {
				canceled.set(iter.next());
				proc.steps--;
			}
		}
	}

	/** Diese Methode führt die gegebene Berechnung {@code task} später aus. */
	private void runLater(final Runnable task) {
		this.display.asyncExec(task);
	}

	private void runDemo(final AppProcess task) throws Exception {
		final int stop = 300;
		final int step = 100;
		for (int i = 0; (i < stop) && !task.isCanceled; i += step) {
			task.steps = stop - i;
			try {
				Thread.sleep(step);
			} catch (final InterruptedException e) {}
		}
	}

	private void execUpdateEnabled() {
		final var enabled = !this.thread.isRunning();
		// TODO
		this.taskStop.setEnabled(!enabled && !this.thread.isRunning());
	}

	public void runCancelProcesses() {
		this.thread.cancel();
	}

}
