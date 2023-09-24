package bee.creative.app.ft;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import bee.creative.io.IO;
import bee.creative.lang.Objects;
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

	public final Display display;

	public AppWindow(Display display) {
		this.display = display;
		this.shell = this.createShell();
		this.text = this.createText();
		this.info = this.createInfo();
		this.queue = this.createQueue();
		this.limit = System.currentTimeMillis() + Integer.MIN_VALUE;
		this.createMenu();
		this.DONE_runUndoEnable();
		this.DONE_runRedoEnable();
		this.DONE_runInfoUpdate();
	}

	private final Shell shell;

	private Shell createShell() {
		final var res = new Shell(this.display, SWT.SHELL_TRIM);
		res.setText("File-Tool");
		res.setSize(600, 400);
		res.setLayout(new GridLayout(1, false));
		res.setMenuBar(new Menu(res, SWT.BAR));
		return res;
	}

	private final Text text;

	private Text createText() {
		final var res = new Text(this.shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		res.addListener(SWT.Verify, event -> this.DONE_runEditEntries());
		res.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		final var dnd = new DropTarget(res, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		dnd.setTransfer(FileTransfer.getInstance(), TextTransfer.getInstance());
		dnd.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(final DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
				try {
					if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
						final var fileList = (String[])FileTransfer.getInstance().nativeToJava(event.currentDataType);
						AppWindow.this.DONE_runPushEntries(Arrays.asList(fileList));
					} else if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
						final var file = (String)TextTransfer.getInstance().nativeToJava(event.currentDataType);
						AppWindow.this.DONE_runPushEntries(Arrays.asList(file));
					}
				} catch (final Exception ignore) {}
			}

		});
		return res;
	}

	private final Label info;

	private Label createInfo() {
		final var res = new Label(this.shell, SWT.NONE);
		res.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		return res;
	}

	MenuItem stop;

	private MenuItem redoMenuItem;

	private MenuItem undoMenuItem;

	private void createMenu() {
		final var res = this.shell.getMenuBar();

		this.undoMenuItem = this.createMenuItem(res, "icon-undo.png", null, this::DONE_runUndoEntries);
		this.undoMenuItem.setAccelerator(SWT.CTRL | 'Z');

		this.redoMenuItem = this.createMenuItem(res, "icon-redo.png", null, this::DONE_runRedoEntries);
		this.redoMenuItem.setAccelerator(SWT.CTRL | 'Y');

		this.createMenuItem(res, "icon-swap.png", "Swap", this::DONE_runSwapSourcesWithTargets);

		this.createMenu(res, "icon-filter.png", "Filtern...", menu -> {
			this.createMenuItem(menu, "icon-file.png", "...nach Datei", this::askFilterSourcesByFile);
			this.createMenuItem(menu, "icon-folder.png", "...nach Ordner", this::askFilterSourcesByFolder);
			this.createMenuItem(menu, "icon-size.png", "...nach Größe", this::askFilterSourcesByLength);
			this.createMenuItem(menu, "icon-time.png", "...nach Änderung", this::askFilterSourcesByModification);
			this.createMenuItem(menu, "icon-made.png", "...nach Erzeugung", this::askFilterSourcesByCreation);
			this.createMenuItem(menu, "icon-name.png", "...nach Datenpfad", this::askFilterSourcesByPattern);
		});
		this.createMenu(res, "icon-sort.png", "Sortieren...", menu -> {
			this.createMenuItem(menu, null, "...rückwärts", this::runSortEntriesReverse);
			this.createMenuItem(menu, "icon-size.png", "...nach Größe", this::runSortEntriesBySourceSize);
			this.createMenuItem(menu, "icon-time.png", "...nach Änderung", this::runSortEntriesBySourceTime);
			this.createMenuItem(menu, "icon-made.png", "...nach Erzeugung", this::runSortEntriesBySourceMade);
			this.createMenuItem(menu, "icon-name.png", "...nach Datenpfad", this::runSortEntriesBySourcePath);
		});
		this.createMenu(res, null, "Hash", menu -> {
			this.createMenuItem(menu, null, "Duplikate erkennen", null);
			this.createMenuLine(menu);
			this.createMenuItem(menu, null, "Cache anlegen", null);
			this.createMenuItem(menu, null, "Cache aktualisieren", null);
		});
		this.createMenu(res, null, "Speicher", menu -> {
			this.createMenuItem(menu, null, "...in Variable speichern", this::runSaveEntriesToVar);
			this.createMenuItem(menu, null, "...aus Variable einfügen", this::runLoadEntriesFromVar);
			this.createMenuLine(menu);
			this.createMenuItem(menu, null, "...in Zwischenablage speichern", this::runSaveEntriesToClip);
			this.createMenuItem(menu, null, "...aus Zwischenablage einfügen", this::runLoadEntriesFromClip);

		});
		this.createMenu(res, null, "Dateien", menu -> {
			this.createMenuItem(menu, "icon-file-delete.png", "Dateien löschen", this::askDeleteSourceFilesPermanently);
			this.createMenuItem(menu, "icon-recycle.png", "Dateien recyclen", this::askDeleteSourceFilesTemporary);
			this.createMenuItem(menu, "icon-file-update.png", "Dateien erneuern", null);
			this.createMenuLine(menu);
			this.createMenuItem(menu, "icon-show.png", "Dateien anzeigen", this::DONE_askShowSourcesAndTargets);
			this.createMenuItem(menu, "icon-copy.png", "Dateien kopieren", this::DONE_askCopySourcesToTargets);
			this.createMenuItem(menu, "icon-rename.png", "Dateien umbenennen", this::DONE_askMoveSourcesToTargets);
			this.createMenuLine(menu);
			this.createMenuItem(menu, null, "Zeitnamen ableiten", null);
			this.createMenuItem(menu, null, "Zeitnamen ableiten", null);
			this.createMenuItem(menu, null, "Zeitpfade ableiten", null);
			this.createMenuItem(menu, null, "Zeitpfade ableiten", null);
		});
		this.createMenu(res, "icon-folder.png", "Verzeichnisse", menu -> {
			this.createMenuItem(menu, "icon-folder-delete2.png", "Ordner löschen", this::askDeleteSourceFoldersPermanently);
			this.createMenuItem(menu, "icon-folder-recycle.png", "Ordner recyclen", this::askDeleteSourceFoldersTemporary);
		});
		this.stop = this.createMenuItem(res, null, "Abbrechen", this::DONE_runStopQueue);
	}

	private MenuItem createMenu(final Menu parent, String image, final String label, Consumer<Menu> setup) {
		final var res = new MenuItem(parent, SWT.CASCADE);
		this.updateMenuItem(res, image, label);
		res.setMenu(new Menu(res));
		setup.set(res.getMenu());
		return res;
	}

	private MenuItem createMenuItem(final Menu parent, String image, final String label, final Runnable onClick) {
		final var res = new MenuItem(parent, SWT.NONE);
		this.updateMenuItem(res, image, label);
		if (onClick != null) {
			res.addListener(SWT.Selection, event -> onClick.run());
		}
		return res;
	}

	private MenuItem createMenuLine(final Menu parent) {
		return new MenuItem(parent, SWT.SEPARATOR);
	}

	private void updateMenuItem(final MenuItem item, String image, final String label) {
		if (image != null) {
			try {
				item.setImage(new Image(this.display, AppWindow.class.getResourceAsStream(image)));
			} catch (Exception e) {
				System.out.println(image + " " + e);
				// TODO: handle exception
			}
		}
		if (label != null) {
			item.setText(label);
		}
	}

	AppQueue queue;

	private final LinkedList<AppState> redoQueue = new LinkedList<>();

	private final LinkedList<AppState> undoQueue = new LinkedList<>();

	private AppQueue createQueue() {
		return new AppQueue() {

			@Override
			public void onError(AppProcess proc, Throwable error) {
				AppWindow.this.DONE_ask() //
					.useTitle(proc.title) //
					.useMessage("Unerwarteter Fehler\n%s", error.toString().replaceAll("&", "&amp;").replaceAll("<", "&lt;"));
			}

			@Override
			public void onSelect(AppProcess proc) {
				AppWindow.this.display.syncExec(() -> AppWindow.this.text.setEnabled(proc == null));
			}

		};
	}

	public AppDialog DONE_ask() {
		var res = new AppDialog(this.shell);
		this.shell.getDisplay().asyncExec(res::open);
		return res.useFocus(true);
	}

	public String getInput() {
		return this.display.syncCall(this.text::getText);
	}

	public void setInput(String value) {
		this.display.syncExec(() -> this.text.setText(value));
	}

	public List<AppEntry> getInputEntries() {
		return AppEntry.parseAll(this.getInput());
	}

	public void DONE_setEntries(Iterable<AppEntry> value) {
		this.setInput(AppEntry.printAll(value));
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

	/** Dieses Feld speichert den Zeitpunkt, ab welchem Änderungen über {@link #DONE_runEditEntries()} in den {@link #undoQueue} übernommen werden dürfen. */
	private long limit = System.currentTimeMillis();

	public void DONE_runEditEntries() {
		this.display.syncExec(() -> {
			final var time = System.currentTimeMillis();
			if ((time - this.limit) >= 0) {
				this.undoQueue.addFirst(new AppState(this.text));
				this.DONE_runUndoEnable();
				this.redoQueue.clear();
				this.DONE_runRedoEnable();
			}
			this.limit = time + 500;
		});
	}

	private void DONE_runUndoEnable() {
		this.undoMenuItem.setEnabled(!this.undoQueue.isEmpty());
	}

	public void DONE_runUndoEntries() {
		this.runTask("Undo", proc -> this.display.syncExec(() -> {
			if (this.undoQueue.isEmpty()) return;
			final var state = this.undoQueue.removeFirst();
			this.DONE_runUndoEnable();
			this.redoQueue.addFirst(new AppState(this.text));
			this.DONE_runRedoEnable();
			this.limit = System.currentTimeMillis() + Integer.MAX_VALUE;
			state.apply(this.text);
		}));
	}

	private void DONE_runRedoEnable() {
		this.redoMenuItem.setEnabled(!this.redoQueue.isEmpty());
	}

	public void DONE_runRedoEntries() {
		this.runTask("Redo", proc -> this.display.syncExec(() -> {
			if (this.redoQueue.isEmpty()) return;
			final var state = this.redoQueue.removeFirst();
			this.DONE_runRedoEnable();
			this.undoQueue.addFirst(new AppState(this.text));
			this.DONE_runUndoEnable();
			this.limit = System.currentTimeMillis() + Integer.MAX_VALUE;
			state.apply(this.text);
		}));
	}

	public void DONE_runSwapSourcesWithTargets() {
		this.runTask("Tauschen", proc -> {
			final var entries = AppEntry.list();
			this.runItems(proc, this.getInputEntries(), entry -> entries.add(new AppEntry(entry.target.text, entry.source.text)), entries::add);
			this.DONE_setEntries(entries);
		});
	}

	public void DONE_runPushEntries(final List<String> sourceList) {
		this.runTask("Datenpfade anfügen", proc -> {
			var input = this.getInput();
			final var entries = AppEntry.list();
			if (!input.isEmpty()) {
				entries.add(new AppEntry(input, ""));
			}
			this.runItems(proc, sourceList, source -> entries.add(new AppEntry(source, "")), null);
			this.DONE_setEntries(entries);
		});
	}

	void DONE_runFilterSources(String title, Producer<Filter<AppItem>> filterBuilder) {
		this.runTask(title, proc -> {
			var resultList = AppEntry.list();
			var sourceFilter = filterBuilder.get();
			this.runItems(proc, this.getInputEntries(), entry -> {
				if (sourceFilter.accept(entry.source)) {
					resultList.add(entry);
				}
			}, resultList::add);
			this.DONE_setEntries(resultList);
		});
	}

	public void askFilterSourcesByFile() {
		this.DONE_ask() //
			.useTitle("Eingabepfad nach Datei filtern") //
			.useMessage("Möchten Sie Eingabepfade zu existierenden Dateien behalten oder entfernen?\nRelative Eingabepfade werden entfernt.") //
			.useButton("Dateien behalten", () -> this.runFilterSourcesByFile(true)) //
			.useButton("Dateien entfernen", () -> this.runFilterSourcesByFile(false)) //
		;
	}

	public void runFilterSourcesByFile(boolean isKeep) {
		this.DONE_runFilterSources(isKeep ? "Dateipfade behalten" : "Dateipfade entfernen",
			() -> source -> (source.fileOrNull() != null) && (source.fileOrNull().isFile() == isKeep));
	}

	public void askFilterSourcesByFolder() {
		this.DONE_ask() //
			.useTitle("Eingabepfad nach Ordner filtern") //
			.useMessage("Möchten Sie Eingabepfade zu existierenden Ordnern behalten oder entfernen?\nRelative Eingabepfade werden entfernt.") //
			.useButton("Ordnern behalten", () -> this.runFilterSourcesByFolder(true)) //
			.useButton("Ordnern entfernen", () -> this.runFilterSourcesByFolder(false)) //
		;
	}

	public void runFilterSourcesByFolder(boolean isKeep) {
		this.DONE_runFilterSources(isKeep ? "Ordnerpfade behalten" : "Ordnerpfade entfernen",
			() -> source -> (source.fileOrNull() != null) && (source.fileOrNull().isDirectory() == isKeep));
	}

	public void askFilterSourcesByPattern() { //
		this.DONE_ask() //
			.useTitle("Eingabepfad nach Muster filtern") //
			.useMessage("Ein Eingabepfad gilt als Treffer, wenn den unten angegebenen regulären Ausdruck darin einen Treffer findet.") //
			.useOption("Regulärer Ausdruck", this.settings.filterPattern) //
			.useButton("Treffer behalten", () -> this.runFilterSourcesByPattern(true)) //
			.useButton("Treffer entfernen", () -> this.runFilterSourcesByPattern(false)) //
		;
	}

	public void runFilterSourcesByPattern(boolean isKeep) {
		final var value = this.settings.filterPattern.putValue().getValue();
		this.DONE_runFilterSources(isKeep ? "Muster behalten" : "Muster entfernen", () -> {
			var pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
			return source -> pattern.matcher(source.text).find() == isKeep;
		});
	}

	public void askFilterSourcesByLength() {
		this.DONE_ask() //
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
		this.DONE_runFilterSources(isKeep ? "Dateigröße behalten" : "Dateigröße entfernen", () -> source -> (source.sizeOrNull() != null)
			&& (((minLength <= source.sizeOrNull().longValue()) && (source.sizeOrNull().longValue() <= maxLength)) == isKeep));
	}

	public void askFilterSourcesByCreation() { //
		this.DONE_ask() //
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
		this.DONE_ask() //
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
		this.runTask(title, proc -> {
			var resultList = this.getInputEntries();
			resultList.sort(order);
			this.DONE_setEntries(resultList);
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
		this.display.syncExec(() -> {
			final var clp = new Clipboard(this.display);
			final var fileList = (String[])clp.getContents(FileTransfer.getInstance());
			clp.dispose();
			if (fileList == null) return;
			this.DONE_runPushEntries(Arrays.asList(fileList));
		});
	}

	public void runSwapEntriesWithVar() {
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

	void askDeleteSourceFilesTemporary() {
		this.DONE_ask()//
			.useTitle("Sollen alle Dateien wirklich in den Papierkorb verschoben werden?") //
			.useMessage("Die Zeilen recycelter Dateien werden aus der Pfadtabelle entfert.") //
			.useButton("Ja", this::runDeleteSourceFilesTemporary) //
			.useButton("Nein") //
		;
	}

	public void runDeleteSourceFilesTemporary() {
	}

	void askDeleteSourceFilesPermanently() {
		this.DONE_ask()//
			.useTitle("Sollen alle Dateien wirklich endgültig gelöscht werden?") //
			.useMessage("Die Zeilen gelöschter Dateien werden aus der Pfadtabelle entfert.") //
			.useButton("Ja", this::runDeleteSourceFilesPermanently) //
			.useButton("Nein") //
		;
	}

	public void runDeleteSourceFilesPermanently() {
	}

	void askDeleteSourceFoldersTemporary() {
		this.DONE_ask()//
			.useTitle("Sollen alle leeren Verzeichnisse wirklich in den Papierkorb verschoben werden?") //
			.useMessage("Die Zeilen recycelter Verzeichnisse werden aus der Pfadtabelle entfert.") //
			.useButton("Ja", this::runDeleteSourceFoldersTemporary) //
			.useButton("Nein") //
		;
	}

	public void runDeleteSourceFoldersTemporary() {
	}

	void askDeleteSourceFoldersPermanently() {
		this.DONE_ask()//
			.useTitle("Sollen alle leeren Verzeichnisse wirklich endgültig gelöscht werden?") //
			.useMessage("Die Zeilen gelöschter Verzeichnisse werden aus der Pfadtabelle entfert.") //
			.useButton("Ja", this::runDeleteSourceFoldersPermanently) //
			.useButton("Nein") //
		;
	}

	public void runDeleteSourceFoldersPermanently() {
	}

	private void DONE_runProcSourcesToTargets(String title, final boolean isMove) {
		this.runTask(title, proc -> {
			var resultList = AppEntry.list();
			this.runItems(proc, this.getInputEntries(), entry -> {
				final var source = entry.source;
				final var target = entry.target;
				if ((source.fileOrNull() != null) && source.fileOrNull().isFile() && (target.fileOrNull() != null) && !target.fileOrNull().isFile()) {
					try {
						target.fileOrNull().getParentFile().mkdirs();
						if (isMove) {
							Files.move(source.pathOrNull(), target.pathOrNull());
						} else {
							Files.copy(source.pathOrNull(), target.pathOrNull(), StandardCopyOption.COPY_ATTRIBUTES);
						}
						return;
					} catch (final Exception ignored) {}
				}
				resultList.add(entry);
			}, resultList::add);
			this.DONE_setEntries(resultList);
		});
	}

	public void DONE_askCopySourcesToTargets() {
		this.DONE_ask() //
			.useTitle("Sollen alle Dateien wirklich nicht ersetzend kopiert werden?") //
			.useMessage("""
				Jede Zeile des Textfeldes besteht dazu aus einem Quell- und einen Zieldateipfad.\s\
				Trennzeichen der Pfade ist ein Tabulator.
				 \
				Die Zeilen erfolgreich kopierter Dateien werden entfernt.""") //
			.useButton("Ja", this::DONE_runCopySourcesToTargets) //
			.useButton("Nein") //
		;
	}

	public void DONE_runCopySourcesToTargets() {
		this.DONE_runProcSourcesToTargets("Dateikopieren", false);
	}

	public void DONE_askMoveSourcesToTargets() {
		this.DONE_ask() //
			.useTitle("Sollen alle Dateien wirklich nicht ersetzend verschoben werden?") //
			.useMessage("""
				Jede Zeile des Textfeldes besteht dazu aus einem Quell- und einen Zieldateipfad.\s\
				Trennzeichen der Pfade ist ein Tabulator.
				 \
				Die Zeilen erfolgreich verschobener Dateien werden entfernt.""") //
			.useButton("Ja", this::DONE_runMoveSourcesToTargets) //
			.useButton("Nein") //
		;
	}

	public void DONE_runMoveSourcesToTargets() {
		this.DONE_runProcSourcesToTargets("Dateiverschieben", true);
	}

	public void DONE_askShowSourcesAndTargets() {
		this.DONE_ask() //
			.useTitle("Sollen die Quell- und Zieldateien wirklich angezeigt werden?") //
			.useMessage("""
				Jede Zeile des Textfeldes besteht dazu aus einem Quell- und einen Zieldateipfad.\s\
				Trennzeichen der Pfade ist ein Tabulator.
				 \
				Die Quell- und Zieldateien werden als Symlinks in ein temporäres Verzeichnis eingefügt.\s\
				Die Quelldateien werden als -ORIGINAL- gekennzeichnet, die Zieldateien als -DUPLIKAT-.\s\
				Das temporäre Verzeichnis wird abschließend angezeigt.
				Das Erzeugen von Symlinks benötigt Administrator-Rechte!""") //
			.useButton("Ja", this::DONE_runShowSourcesAndTargets) //
			.useButton("Nein") //
		;
	}

	public void DONE_runShowSourcesAndTargets() {
		this.runTask("Dateien anzeigen", proc -> {
			final var parentFile = Files.createTempDirectory("file-tool-app-").toFile();
			var linkCount = new int[2];
			var inputList = this.getInputEntries();
			this.runItems(proc, inputList, entry -> {
				final var source = entry.source;
				final var target = entry.target;
				if ((source.fileOrNull() != null) && source.fileOrNull().isFile() && (target.fileOrNull() != null) && target.fileOrNull().isFile()) {
					linkCount[0]++;
					final var sourceLink = new File(parentFile, linkCount[0] + "-ORIGINAL-" + source.fileOrNull().getName());
					final var targetLink = new File(parentFile, linkCount[0] + "-DUPLIKAT-" + target.fileOrNull().getName());
					try {
						Files.createSymbolicLink(sourceLink.toPath(), source.pathOrNull());
						linkCount[1]++;
					} catch (final Exception ignored) {}
					try {
						Files.createSymbolicLink(targetLink.toPath(), target.pathOrNull());
						linkCount[1]++;
					} catch (final Exception ignored) {}
				}
			}, null);
			Desktop.getDesktop().open(parentFile);
		});
	}

	FTSettings settings = new FTSettings();

	/** Diese Methode führt die gegebene Berechnung {@code task} mit dem gegebenen Titel {@code title} in einem {@link Thread} aus. */
	public void runTask(final String title, final AppTask task) {
		this.queue.push(title, task);
	}

	private <GItem> void runItems(AppProcess proc, final Collection<GItem> items, final Consumer<GItem> regular, final Consumer<GItem> canceled) {
		final var iter = items.iterator();
		proc.steps += items.size();
		if (regular != null) {
			while (iter.hasNext() && !proc.isCanceled) {
				var item = iter.next();
				proc.object = item;
				regular.set(item);
				proc.steps--;

			}
		}
		if (canceled != null) {
			while (iter.hasNext()) {
				var item = iter.next();
				proc.object = item;
				canceled.set(item);
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

	public void DONE_runStopQueue() {
		this.queue.cancel();
	}

	public void DONE_runInfoUpdate() {
		this.display.timerExec(500, this::DONE_runInfoUpdate);
		var proc = this.queue.current();
		this.info.setText(proc != null ? Objects.notNull(proc.title, "?") + " - " + proc.steps + " - " + Objects.notNull(proc.object, "") : " ");
	}

}
