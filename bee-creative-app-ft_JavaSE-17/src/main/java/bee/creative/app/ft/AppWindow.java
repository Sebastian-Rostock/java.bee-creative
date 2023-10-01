package bee.creative.app.ft;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import bee.creative.lang.Objects;
import bee.creative.util.Comparators;
import bee.creative.util.Consumer;
import bee.creative.util.Filter;
import bee.creative.util.HashMap2;
import bee.creative.util.HashSet2;
import bee.creative.util.Iterables;
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
		this.editLimit = System.currentTimeMillis() + Integer.MIN_VALUE;
		this.createMenu();
		this.enableUndo_DONE();
		this.enableRedo_DONE();
		this.DONE_runInfoUpdate();
	}

	private final Shell shell;

	private MenuItem redoItem;

	private MenuItem undoItem;

	private MenuItem stopMenuItem;

	private final Text text;

	private final Label info;

	private Shell createShell() {
		final var res = new Shell(this.display, SWT.SHELL_TRIM);
		res.setText("File-Tool");
		res.setSize(600, 400);
		res.setLayout(new GridLayout(1, false));
		res.setMenuBar(new Menu(res, SWT.BAR));
		this.setImage(res::setImage, AppIcon.iconApp);
		return res;
	}

	private Text createText() {
		final var res = new Text(this.shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		res.addListener(SWT.Verify, event -> this.runEdit_DONE());
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

	private Label createInfo() {
		final var res = new Label(this.shell, SWT.NONE);
		res.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		return res;
	}

	private void createMenu() {
		final var res = this.shell.getMenuBar();

		this.undoItem = this.createMenuItem(res, AppIcon.menuUndo, null, this::runUndo_DONE);
		this.undoItem.setAccelerator(SWT.CTRL | 'Z');

		this.redoItem = this.createMenuItem(res, AppIcon.menuRedo, null, this::runRedo_DONE);
		this.redoItem.setAccelerator(SWT.CTRL | 'Y');

		this.createMenuItem(res, AppIcon.menuSwap, null, this::runSwap_DONE);

		this.createMenu(res, AppIcon.menuFilter, null, menu -> {
			this.createMenuItem(menu, AppIcon.itemFilterBySourceFile, "Dateien filtern...", this::runFilterBySourceFile_DONE);
			this.createMenuItem(menu, AppIcon.filterFolder, "Verzeichnisse filtern...", this::askFilterBySourceFolder_DONE);
			this.createMenuLine(menu);
			this.createMenuItem(menu, AppIcon.filterChange, "Nach Änderung filtern..", this::DONE_askFilterEntriesBySourceTime);
			this.createMenuItem(menu, AppIcon.filterCreate, "Nach Erzeugung filtern...", this::DONE_askFilterEntriesBySourceMade);
			this.createMenuItem(menu, AppIcon.filterLength, "Nach Dateigröße filtern...", this::DONE_askFilterEntriesBySourceSize);
			this.createMenuItem(menu, AppIcon.filterLabel, "Nach Datenpfad filtern..", this::DONE_askFilterEntriesBySourcePath);
		});
		this.createMenu(res, AppIcon.menuSort, null, menu -> {
			this.createMenuItem(menu, AppIcon.sortReverse, "Rückwärts ordnen", this::DONE_runSortEntriesReverse);
			this.createMenuLine(menu);
			this.createMenuItem(menu, AppIcon.sortChange, "Nach Änderung sortieren", this::DONE_runSortEntriesBySourceTime);
			this.createMenuItem(menu, AppIcon.sortCreate, "Nach Erzeugung sortieren", this::DONE_runSortEntriesBySourceMade);
			this.createMenuItem(menu, AppIcon.sortLength, "Nach Dateigröße sortieren", this::DONE_runSortEntriesBySourceSize);
			this.createMenuItem(menu, AppIcon.sortLabel, "Nach Eingabepfad sortieren", this::DONE_runSortEntriesBySourcePath);
		});
		this.createMenu(res, AppIcon.menuHash, null, menu -> {
			this.createMenuItem(menu, AppIcon.hashAnalyze, "Duplikate erkennen", this::askAnalyzeHashes);
			this.createMenuLine(menu);
			this.createMenuItem(menu, AppIcon.hashSetup, "Streuwertepuffer erzeugen", this::DONE_askSetupHashes);
			this.createMenuItem(menu, AppIcon.hashUpdate, "Streuwertepuffer befüllen", this::DONE_askUpdateHashes);
		});
		this.createMenu(res, AppIcon.menuMemory, null, menu -> {
			this.createMenuItem(menu, AppIcon.saveVariable, "...in Variable speichern", this::runSaveEntriesToVar);
			this.createMenuItem(menu, AppIcon.loadVariable, "...aus Variable einfügen", this::runLoadEntriesFromVar);
			this.createMenuLine(menu);
			this.createMenuItem(menu, AppIcon.saveClipboard, "...in Zwischenablage speichern", this::runSaveEntriesToClip);
			this.createMenuItem(menu, AppIcon.loadClipboard, "...aus Zwischenablage einfügen", this::runLoadEntriesFromClip);
		});
		this.createMenu(res, AppIcon.menuName, null, menu -> {
			this.createMenuItem(menu, AppIcon.setupTimeName, "Zeitnamen ableiten", null);
			this.createMenuItem(menu, AppIcon.updateTimeName, "Zeitnamen aktualisieren", null);
			this.createMenuItem(menu, AppIcon.updateTimePath, "Zeitpfade aktualisieren", null);
		});
		this.createMenu(res, AppIcon.file, null, menu -> {
			this.createMenuItem(menu, AppIcon.deleteFile, "Dateien löschen", this::runDeleteSourceFilesPermanently_DONE);
			this.createMenuItem(menu, AppIcon.recycleFile, "Dateien recyclen", this::runDeleteSourceFilesTemporary_DONE);
			this.createMenuLine(menu);
			this.createMenuItem(menu, AppIcon.refreshFile, "Dateien erneuern", null);
			this.createMenuLine(menu);
			this.createMenuItem(menu, AppIcon.showFile, "Dateien anzeigen", this::DONE_askShowSourcesAndTargets);
			this.createMenuItem(menu, AppIcon.copyFile, "Dateien kopieren", this::askCopySourcesToTargets_DONE);
			this.createMenuItem(menu, AppIcon.moveFile, "Dateien umbenennen", this::askMoveSourcesToTargets_DONE);
		});
		this.createMenu(res, AppIcon.folder, null, menu -> {
			this.createMenuItem(menu, AppIcon.deleteFolder, "Verzeichnisse löschen", this::runDeleteSourceFoldersPermanently_DONE);
			this.createMenuItem(menu, AppIcon.recycleFolder, "Verzeichnisse recyclen", this::runDeleteSourceFoldersTemporary_DONE);
			this.createMenuLine(menu);
			this.createMenuItem(menu, AppIcon.resolveFile, "Dateien auflösen...", this::runResolveSourceFiles_DONE);
			this.createMenuItem(menu, AppIcon.resolveFolder, "Verteichnisse auflösen...", this::runResolveSourceFolders_DONE);
		});
		this.stopMenuItem = this.createMenuItem(res, AppIcon.stop, null, this::runStop_DONE);
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

	private ToolItem createMenuItem(final ToolBar parent, String image, final String label, final Runnable onClick) {
		final var res = new ToolItem(parent, SWT.PUSH);
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

	private void updateMenuItem(final ToolItem item, String image, final String label) {
		if (image != null) {
			this.setImage(item::setImage, image);
		}
		if (label != null) {
			item.setText(label);
		}
	}

	private void setImage(Consumer<Image> taret, String image) {
		try {
			taret.set(new Image(this.display, AppWindow.class.getResourceAsStream(image)));
		} catch (Exception e) {
			System.out.println(image + " " + e);
		}
	}

	private final AppQueue queue;

	private AppQueue createQueue() {
		return new AppQueue() {

			@Override
			public void onError(AppProcess proc, Throwable error) {
				AppWindow.this.runDialog_DONE() //
					.useTitle(proc != null ? proc.title : "") //
					.useMessage("Unerwarteter Fehler\n%s", error);
			}

			@Override
			public void onSelect(AppProcess proc) {
				AppWindow.this.display.syncExec(() -> {
					AppWindow.this.enableText_DONE();
					AppWindow.this.enableStop_DONE();
				});
			}

		};
	}

	public AppDialog runDialog_DONE() {
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

	public List<AppEntry> getEntries_DONE() {
		return AppEntry.parseAll(this.getInput());
	}

	public void setEntries_DONE(Iterable<AppEntry> value) {
		this.setInput(AppEntry.printAll(value));
	}

	/** Dieses Feld speichert den Zeitpunkt, ab welchem Änderungen über {@link #runEdit_DONE()} in den {@link #undoQueue} übernommen werden dürfen. */
	private long editLimit = System.currentTimeMillis();

	private final LinkedList<AppState> redoQueue = new LinkedList<>();

	private final LinkedList<AppState> undoQueue = new LinkedList<>();

	private void enableUndo_DONE() {
		this.undoItem.setEnabled(!this.undoQueue.isEmpty());
	}

	private void enableRedo_DONE() {
		this.redoItem.setEnabled(!this.redoQueue.isEmpty());
	}

	private void enableStop_DONE() {
		this.stopMenuItem.setEnabled(this.queue.isRunning());
	}

	private void enableText_DONE() {
		this.text.setEnabled(!this.queue.isRunning());
	}

	public void runEdit_DONE() {
		this.display.syncExec(() -> {
			final var time = System.currentTimeMillis();
			if ((time - this.editLimit) >= 0) {
				this.undoQueue.addFirst(new AppState(this.text));
				this.enableUndo_DONE();
				this.redoQueue.clear();
				this.enableRedo_DONE();
			}
			this.editLimit = time + 500;
		});
	}

	public void runUndo_DONE() {
		this.runTask("Undo", proc -> this.display.syncExec(() -> {
			if (this.undoQueue.isEmpty()) return;
			final var state = this.undoQueue.removeFirst();
			this.enableUndo_DONE();
			this.redoQueue.addFirst(new AppState(this.text));
			this.enableRedo_DONE();
			this.editLimit = System.currentTimeMillis() + Integer.MAX_VALUE;
			state.apply(this.text);
		}));
	}

	public void runRedo_DONE() {
		this.runTask("Redo", proc -> this.display.syncExec(() -> {
			if (this.redoQueue.isEmpty()) return;
			final var state = this.redoQueue.removeFirst();
			this.enableRedo_DONE();
			this.undoQueue.addFirst(new AppState(this.text));
			this.enableUndo_DONE();
			this.editLimit = System.currentTimeMillis() + Integer.MAX_VALUE;
			state.apply(this.text);
		}));
	}

	public void runSwap_DONE() {
		this.runTask("Tauschen", proc -> {
			final var entries = AppEntry.list();
			this.runItems(proc, this.getEntries_DONE(), entry -> entries.add(new AppEntry(entry.target.text, entry.source.text)), entries::add);
			this.setEntries_DONE(entries);
		});
	}

	public void runStop_DONE() {
		this.queue.cancel();
	}

	public void DONE_runPushEntries(final List<String> sourceList) {
		this.runTask("Datenpfade anfügen", proc -> {
			var input = this.getInput();
			final var entries = AppEntry.list();
			if (!input.isEmpty()) {
				entries.add(new AppEntry(input, ""));
			}
			this.runItems(proc, sourceList, source -> entries.add(new AppEntry(source, "")), null);
			this.setEntries_DONE(entries);
		});
	}

	private void DONE_runFilterEntriesBySource(String title, Producer<Filter<AppItem>> filterBuilder) {
		this.runTask(title, proc -> {
			var resultList = AppEntry.list();
			var sourceFilter = filterBuilder.get();
			this.runItems(proc, this.getEntries_DONE(), entry -> {
				if (sourceFilter.accept(entry.source)) {
					resultList.add(entry);
				}
			}, resultList::add);
			this.setEntries_DONE(resultList);
		});
	}

	public void runFilterBySourceFile_DONE() {
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, die existierende Dateien angeben?") //
			.useMessage("Relative Eingabepfade werden entfernt.") //
			.useButton("Eingabepfade von Dateien behalten", () -> this.runFilterBySourceFileImpl_DONE(true)) //
			.useButton("Eingabepfade von Dateien entfernen", () -> this.runFilterBySourceFileImpl_DONE(false)) //
		;
	}

	private void runFilterBySourceFileImpl_DONE(boolean isKeep) {
		this.DONE_runFilterEntriesBySource(isKeep ? "Eingabepfade behalten (Datei)" : "Eingabepfade entfernen (Datei)",
			() -> source -> (source.fileOrNull() != null) && (source.fileOrNull().isFile() == isKeep));
	}

	public void askFilterBySourceFolder_DONE() {
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, die existierende Verzeichnisse angeben?") //
			.useMessage("Relative Eingabepfade werden entfernt.") //
			.useButton("Eingabepfade von Verzeichnissen behalten", () -> this.DONE_runFilterEntriesBySourceFolder(true)) //
			.useButton("Eingabepfade von Verzeichnissen entfernen", () -> this.DONE_runFilterEntriesBySourceFolder(false)) //
		;
	}

	private void DONE_runFilterEntriesBySourceFolder(boolean isKeep) {
		this.DONE_runFilterEntriesBySource(isKeep ? "Eingabepfade behalten (Verzeichnis)" : "Eingabepfade entfernen (Verzeichnis)",
			() -> source -> (source.fileOrNull() != null) && (source.fileOrNull().isDirectory() == isKeep));
	}

	public void DONE_askFilterEntriesBySourcePath() { //
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, für die der unten genannte reguläre Ausdruck einen Treffer findet?") //
			.useOption("Regulärer Ausdruck", this.settings.filterPath) //
			.useButton("Eingabepfade behalten", () -> this.DONE_runFilterEntriesBySourcePath(true)) //
			.useButton("Eingabepfade entfernen", () -> this.DONE_runFilterEntriesBySourcePath(false)) //
		;
	}

	private void DONE_runFilterEntriesBySourcePath(boolean isKeep) {
		final var value = this.settings.filterPath.putValue().getValue();
		this.DONE_runFilterEntriesBySource(isKeep ? "Eingabepfade behalten (Muster)" : "Eingabepfade entfernen (Muster)", () -> {
			var pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
			return source -> pattern.matcher(source.text).find() == isKeep;
		});
	}

	public void DONE_askFilterEntriesBySourceSize() {
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, die Dateie angeben, deren Größe kleiner als die unten genannte ist?") //
			.useMessage("Relative Eingabepfade und die zu Verzeichnissen werden entfernt.") //
			.useOption("Dateigröße", this.settings.filterSize) //
			.useButton("Pfade kleinere Dateien behalten", () -> this.DONE_runFilterEntriesBySourceSize(true)) //
			.useButton("Pfade kleinere Dateien entfernen", () -> this.DONE_runFilterEntriesBySourceSize(false)) //
		;
	}

	private void DONE_runFilterEntriesBySourceSize(boolean isKeep) {
		final var filterSize = this.settings.filterSize.getValue();
		this.DONE_runFilterEntriesBySource(isKeep ? "Eingabepfade behalten (Größe)" : "Eingabepfade entfernen (Größe)",
			() -> source -> (source.sizeOrNull() != null) && ((source.sizeOrNull().longValue() <= filterSize) == isKeep));
	}

	public void DONE_askFilterEntriesBySourceMade() { //
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, die vor dem unten genannten Zeitpunkt erzeugte Dateien angeben?") //
			.useMessage("Relative Eingabepfade und die zu Verzeichnissen werden entfernt.") //
			.useOption("Erzeugungszeitpunkt", this.settings.filterMade) //
			.useButton("Pfade alterer Dateien behalten", () -> this.DONE_runFilterEntriesBySourceMade(true)) //
			.useButton("Pfade alterer Dateien entfernen", () -> this.DONE_runFilterEntriesBySourceMade(false)) //
		;
	}

	private void DONE_runFilterEntriesBySourceMade(boolean isKeep) {
		final var filterMade = this.settings.filterMade.getValue();
		this.DONE_runFilterEntriesBySource(isKeep ? "Eingabepfade behalten (Erzeugung)" : "Eingabepfade entfernen (Erzeugung)",
			() -> source -> (source.madeOrNull() != null) && ((source.madeOrNull().longValue() < filterMade) == isKeep));
	}

	public void DONE_askFilterEntriesBySourceTime() { //
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, die vor dem unten genannten Zeitpunkt geänderte Dateien angeben?") //
			.useMessage("Relative Eingabepfade und die zu Verzeichnissen werden entfernt.") //
			.useOption("Änderungszeitpunkt", this.settings.filterTime) //
			.useButton("Pfade alterer Dateien behalten", () -> this.DONE_runFilterEntriesBySourceTime(true)) //
			.useButton("Pfade alterer Dateien entfernen", () -> this.DONE_runFilterEntriesBySourceTime(false)) //
		;
	}

	private void DONE_runFilterEntriesBySourceTime(boolean isKeep) {
		final var filterTime = this.settings.filterMade.getValue();
		this.DONE_runFilterEntriesBySource(isKeep ? "Eingabepfade behalten (Änderung)" : "Eingabepfade entfernen (Änderung)",
			() -> source -> (source.timeOrNull() != null) && ((source.timeOrNull().longValue() < filterTime) == isKeep));
	}

	public void DONE_runSortEntriesReverse() {
		this.runTask("Sortierung umkehren", proc -> {
			var resultList = this.getEntries_DONE();
			Collections.reverse(resultList);
			this.setEntries_DONE(resultList);
		});
	}

	private void DONE_runSortEntriesBySource(String title, Comparator<AppItem> order) {
		this.runTask(title, proc -> {
			var resultList = this.getEntries_DONE();
			resultList.sort(Comparators.optionalize(order).translate(entry -> entry.source));
			this.setEntries_DONE(resultList);
		});
	}

	public void DONE_runSortEntriesBySourcePath() {
		this.DONE_runSortEntriesBySource("Sortierung nach Datenpfad", Comparators.AlphanumericalComparator.INSTANCE.translate(item -> item.text));
	}

	public void DONE_runSortEntriesBySourceTime() {
		this.DONE_runSortEntriesBySource("Sortierung nach Änderung", Comparators.LongComparator.INSTANCE.translate(AppItem::timeOrNull));
	}

	public void DONE_runSortEntriesBySourceSize() {
		this.DONE_runSortEntriesBySource("Sortierung nach Größe", Comparators.LongComparator.INSTANCE.translate(AppItem::sizeOrNull));
	}

	public void DONE_runSortEntriesBySourceMade() {
		this.DONE_runSortEntriesBySource("Sortierung nach Erzeugung", Comparators.LongComparator.INSTANCE.translate(AppItem::madeOrNull));
	}

	void askAnalyzeHashes() {
		this.runDialog_DONE() //
			.useTitle("Sollen alle Eingabedateien auf Duplikate hin untersucht werden?") //
			.useMessage("""
				Beim Suchen von Duplikaten werden alle Eingabedateien zunächst bezüglich ihrer Dateigröße partitioniert. \
				Die Dateien innerhalb einer Dateigrößenpartition werden dann bezüglich ihres SHA-256-Streuwerts partitioniert. \
				Dieser Streuwert wird aus höchstens der unten eingestellten Anzanl an Bytes jeweils ab dem Dateibeginn und dem Dateiende berechnet. \
				Schließlich werden die Dateien innerhalb einer Streuwertpartition nach ihrem Dateiinhalt partitioniert. \
				Als Dateiinhalt wird höchstens die unten eingestellte Anzanl an Bytes jeweils ab dem Dateibeginn und dem Dateiende betrachtet.
				Der Pfad der ersten Datei einer Dateiinhaltspartition wird als Eingabepfad verwendet. \
				Die Pfade der anderen Dateien der Partitionen werden diesem als Ausgabepfade zugeordnet. \
				Eingabepfade ohne Ausgabepfad beschreiben einzigartige Dateien.
				Die Streuwerte werden in der Pufferdatei '%s' zwischengespeichert. \
				Dabei wird die im Elternpfad näheste bzw. die im Verzeichnis dieser Anwendung liegende Pufferdatei verwendet.
				Relative Eingabepfade und die zu Verzeichnissen werden ignoriert.""", FTHashes.FILENAME) //
			.useOption("Puffergröße für Streuwert", this.settings.findClonesHashSize) //
			.useOption("Puffergröße für Dateivergleich", this.settings.findClonesTestSize) //
			.useButton("Duplikate behalten", () -> this.runReplaceEntriesWithSourceClones(true)) //
			.useButton("Einzigartige behalten", () -> this.runReplaceEntriesWithSourceClones(false)) //
		;
	}

	void runReplaceEntriesWithSourceClones(boolean isKeep) {
		this.runTask(isKeep ? "Duplikate behalten" : "Duplikate entfernen", proc -> {

			var hashSize = this.settings.findClonesHashSize.getValue();
			var testSize = this.settings.findClonesTestSize.getValue();

			var caches = new FTCaches();
			caches.restore();

			var pathSet = new HashSet2<>(1000);
			var itemList = new LinkedList<AppItem>();
			var entryList = this.getEntries_DONE();

			// Dateien Duplikatfrei erfassen
			this.runItems(proc, entryList, line -> {
				var file = line.source.fileOrNull();
				if ((file == null) || !file.isFile() || !pathSet.add(file.getPath())) return;
				itemList.add(0, line.source);
				proc.steps++;
			}, null);

			var sizeListMap = new HashMap2<Object, AppItem>();
			while (!itemList.isEmpty() && !proc.isCanceled) { // rückwärts
				var sizeItem = itemList.remove(0);
				proc.object = sizeItem;
				proc.steps--;
				sizeItem.prev = sizeListMap.put(sizeItem.sizeOrNull(), sizeItem);
				if (sizeItem.prev != null) {
					proc.steps++;
				}
			}
			sizeListMap.remove(null);
			itemList.clear();

			var hashListMap = new HashMap2<Object, AppItem>(1000);
			var itemDataListMap = new HashMap2<Object, AppItem>(1000);
			var originalList = new LinkedList<AppItem>();

			var equalSizeIter = sizeListMap.values().iterator();
			while (equalSizeIter.hasNext() && !proc.isCanceled) {
				var equalSizeList = equalSizeIter.next();
				if (equalSizeList.prev != null) {
					proc.steps++;
					hashListMap.clear();
					var testSize2 = Math.min(equalSizeList.size.longValue(), testSize);
					for (AppItem equalSizeItem = equalSizeList, next; equalSizeItem != null; equalSizeItem = next) { // vorwärts
						proc.object = equalSizeItem;
						proc.steps--;
						next = equalSizeItem.prev;

						equalSizeItem.hash = Objects.notNull(caches.get(equalSizeItem.text, hashSize), equalSizeItem);

						equalSizeItem.prev = hashListMap.put(equalSizeItem.hash, equalSizeItem);
						if (equalSizeItem.prev != null) {
							proc.steps++;
						}
					}

					var iterator = hashListMap.values().iterator();
					while (iterator.hasNext() && !proc.isCanceled) {
						var hashList = iterator.next();

						if (hashList.prev != null) {
							proc.steps++;

							itemDataListMap.clear();
							for (AppItem item3 = hashList, prev; (item3 != null) && !proc.isCanceled; item3 = prev) { // rückwärts

								prev = item3.prev;

								item3.data = new FTData(item3.text, testSize2);

								item3.prev = itemDataListMap.put(item3.data, item3);
								if (item3.prev != null) {
									proc.steps++;
								}
							}
							for (var original: itemDataListMap.values()) {
								if (original.prev != null) {
									originalList.add(original);
								}
							}

						}
					}

				}
			}

			caches.persist();

			var result = AppEntry.list();
			var originalMap = new HashMap2<String, AppItem>(1000);

			this.runItems(proc, originalList, original -> originalMap.put(original.file.getPath(), original), null);
			this.runItems(proc, entryList, entry -> {
				var file = entry.source.fileOrNull();
				if (file == null) return;
				var path = file.getPath();
				if (isKeep) {
					var original = originalMap.remove(path);
					if (original == null) return;
					for (var duplikat = original.prev; duplikat != null; duplikat = duplikat.prev) { // vorwärts
						result.add(new AppEntry(original, duplikat));
					}
				} else {
					if (!pathSet.remove(path)) return;
					result.add(new AppEntry(path));
				}
			}, null);

			this.setEntries_DONE(result);
		});
	}

	public void DONE_askSetupHashes() {
		this.runDialog_DONE() //
			.useTitle("Sollen Streuwertepuffer in den Eingabeordnern angelegt werden?") //
			.useMessage("""
				Die Pufferdateien tragen den Namen '%s'.
				Relative Eingabepfade und die zu Dateien werden ignoriert.""", FTHashes.FILENAME) //
			.useButton("Streuwertepuffer erzeugen", () -> this.DONE_runCreateHashes()) //
		;
	}

	public void DONE_runCreateHashes() {
		this.runTask("Streuwertepuffer anlegen", proc -> {
			this.runItems(proc, this.getEntries_DONE(), entry -> {
				try {
					if (entry.source.fileOrNull() != null) {
						FTHashes.fileFrom(entry.source.text).createNewFile();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}, null);
		});
	}

	public void DONE_askUpdateHashes() {
		this.runDialog_DONE() //
			.useTitle("Sollen Streuwertepuffer mit den Streuwerten der Eingabedateien befüllt werden?") //
			.useMessage("""
				Die SHA-256-Streuwerte werden aus höchstens der unten genannten Anzanl an Bytes jeweils ab dem Dateibeginn und dem Dateiende berechnet \
				und in der Pufferdatei '%s' zwischengespeichert. Dabei wird die im Elternpfad näheste bzw. die im Verzeichnis dieser Anwendung liegende \
				Pufferdatei verwendet.
				Relative Eingabepfade und die zu Verzeichnissen werden ignoriert.""", FTHashes.FILENAME) //
			.useOption("Puffergröße für Streuwert", this.settings.findClonesHashSize) //
			.useButton("Streuwertepuffer befüllen", () -> this.DONE_runUpdateHashes()) //
		;
	}

	public void DONE_runUpdateHashes() {
		this.runTask("Streuwertepuffer befüllen", proc -> {
			var caches = new FTCaches();
			caches.restore();
			var hashSize = this.settings.findClonesHashSize.getValue();
			this.runItems(proc, this.getEntries_DONE(), entry -> {
				var file = entry.source.fileOrNull();
				if ((file != null) && file.isFile()) {
					caches.get(file.getPath(), hashSize);
				}
			}, null);
			caches.persist();
		});
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
			var clp = new Clipboard(this.display);
			var fileList = (String[])clp.getContents(FileTransfer.getInstance());
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

	private void runResolveSources(boolean isFile) {
		this.runTask(isFile ? "Dateien auflösen" : "Verzeichnisse auflösen", proc -> {
			var pathSet = new HashSet2<>(100);
			var result = AppEntry.list();
			var fileStack = new LinkedList<File>();
			this.runItems(proc, this.getEntries_DONE(), line -> {
				var file = line.source.fileOrNull();
				if (file != null) {
					proc.steps++;
					fileStack.addFirst(file);
				}
			}, result::add);
			while (!fileStack.isEmpty() && !proc.isCanceled) {
				var file = fileStack.removeFirst();
				var path = file.getPath();
				proc.object = path;
				if (pathSet.add(path)) {
					if (file.isDirectory()) {
						var files = file.listFiles();
						if ((files != null) && (files.length != 0)) {
							fileStack.addAll(0, Arrays.asList(files));
							proc.steps += files.length;
						}
						if (!isFile) {
							result.add(new AppEntry(path));
						}
					} else if (isFile) {
						if (file.isFile()) {
							result.add(new AppEntry(path));
						}
					}
				}
				proc.steps--;
			}
			result.addAll(0, Iterables.translate(fileStack, file -> new AppEntry(file.getPath())).toList());
			this.setEntries_DONE(result);
		});
	}

	public void runResolveSourceFiles_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle Verzeichnispfade wirklich durch die Pfade aller darin enthaltenen Dateien ersetzt werden?") //
			.useMessage("""
				Die Dateiauflösung wird in allen Unterverzeichnissen fortgesetzt. \
				Duplikate sowie relative Eingabepfade werden ignoriert, die zu Dateien bleiben erhalten.""") //
			.useButton("Dateipfade ermitteln", () -> this.runResolveSources(true)) //
		;
	}

	public void runResolveSourceFolders_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle Verzeichnispfade wirklich um die Pfade aller darin enthaltenen Verzeichnisse ergänzt werden?") //
			.useMessage("""
				Die Verzeichnisauflösung wird in allen Unterverzeichnissen fortgesetzt. \
				Duplikate sowie relative Eingabepfade und die zu Dateien werden ignoriert.""") //
			.useButton("Verzeichnispfade ermitteln", () -> this.runResolveSources(false)) //
		;
	}

	void refreshInputFiles() {
		this.createDialog()//
			.withText("Dateien erneuern") //
			.withTitle("Sollen alle alten Dateien wirklich erneuert werden?") //
			.withMessage("""
				Beim Erneuern werden alle Dateien, die vor mehr als der unten angegebenen Anzahl\s\
				an Tagen erstellt wurden, kopiert und durch ihre Kopien ersetzt.\s\
				Die dazu temporär erzeugten Kopien tragen die Dateiendung .tempcopy.\s\
				Die Zeilen erneuerter Dateien werden aus der Pfadliste entfert.\s""") //
			.withOption("Dateialter in Tagen", this.settings.copyFilesTimeFilter) //
			.withButton("Ja", this::refreshInputFilesStart) //
			.withButton("Nein") //
			.open();
	}

	private void runDeleteSourcesImpl(boolean isFile, boolean isTemp) {
		this.runTask(isFile ? (isTemp ? "Dateien recyclen" : "Dateien löschen") : (isTemp ? "Verzeichnisse recyclen" : "Verzeichnisse löschen"), proc -> {
			var pathSet = new HashSet2<String>(1000);
			this.runItems(proc, this.getEntries_DONE(), entry -> {
				if (entry.source.fileOrNull() == null) return;
				pathSet.add(entry.source.file.getPath());
			}, null);
			var desktop = Desktop.getDesktop();
			var pathList = new ArrayList<>(pathSet);
			pathList.sort(Comparators.IntComparator.INSTANCE.translate(String::length).reverse());
			pathSet.clear();
			this.runItems(proc, pathList, path -> {
				try {
					var file = new File(path);
					if (!isFile) {
						if (!file.isDirectory()) return;
						var files = file.list();
						if ((files != null) && (files.length != 0)) return;
					} else {
						if (!file.isFile()) return;
					}
					if (isTemp) {
						if (!desktop.moveToTrash(file)) return;
					} else {
						if (!file.delete()) return;
					}
					pathSet.add(path);
				} catch (Exception ignore) {}
			}, null);
			var keepList = AppEntry.list();
			this.runItems(proc, this.getEntries_DONE(), line -> {
				var file = line.source.fileOrNull();
				if ((file == null) || pathSet.contains(file.getPath())) return;
				keepList.add(line);
			}, keepList::add);
			this.setEntries_DONE(keepList);
		});
	}

	public void runDeleteSourceFilesTemporary_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle Eingabedateien wirklich in den Papierkorb verschoben werden?") //
			.useMessage("Die Zeilen recycelter Dateien werden aus der Pfadtabelle entfert.") //
			.useButton("Dateien recyclen", () -> this.runDeleteSourcesImpl(true, true)) //
		;
	}

	public void runDeleteSourceFilesPermanently_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle Eingabedateien wirklich endgültig gelöscht werden?") //
			.useMessage("Die Zeilen gelöschter Dateien werden aus der Pfadtabelle entfert.") //
			.useButton("Dateien löschen", () -> this.runDeleteSourcesImpl(true, false)) //
		;
	}

	public void runDeleteSourceFoldersTemporary_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle leeren Verzeichnisse wirklich in den Papierkorb verschoben werden?") //
			.useMessage("Die Zeilen recycelter Verzeichnisse werden aus der Pfadtabelle entfert.") //
			.useButton("Verzeichnisse recyclen", () -> this.runDeleteSourcesImpl(false, true)) //
		;
	}

	public void runDeleteSourceFoldersPermanently_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle leeren Verzeichnisse wirklich endgültig gelöscht werden?") //
			.useMessage("Die Zeilen gelöschter Verzeichnisse werden aus der Pfadtabelle entfert.") //
			.useButton("Verzeichnisse löschen", () -> this.runDeleteSourcesImpl(false, false)) //
		;
	}

	private void runProcSourcesToTargetsImpl_DONE(boolean isMove) {
		this.runTask(isMove ? "Dateiverschieben" : "Dateikopieren", proc -> {
			var resultList = AppEntry.list();
			this.runItems(proc, this.getEntries_DONE(), entry -> {
				var source = entry.source;
				var target = entry.target;
				if ((source.fileOrNull() != null) && source.fileOrNull().isFile() && (target.fileOrNull() != null) && !target.fileOrNull().isFile()) {
					try {
						target.fileOrNull().getParentFile().mkdirs();
						if (isMove) {
							Files.move(source.pathOrNull(), target.pathOrNull());
						} else {
							Files.copy(source.pathOrNull(), target.pathOrNull(), StandardCopyOption.COPY_ATTRIBUTES);
						}
						return;
					} catch (Exception ignored) {}
				}
				resultList.add(entry);
			}, resultList::add);
			this.setEntries_DONE(resultList);
		});
	}

	public void askCopySourcesToTargets_DONE() {
		this.runDialog_DONE() //
			.useTitle("Sollen alle Dateien wirklich nicht ersetzend kopiert werden?") //
			.useMessage("""
				Jede Zeile des Textfeldes besteht dazu aus einem Quell- und einen Zieldateipfad. \
				Trennzeichen der Pfade ist ein Tabulator. \
				Die Zeilen erfolgreich kopierter Dateien werden entfernt.""") //
			.useButton("Ja", () -> this.runProcSourcesToTargetsImpl_DONE(false)) //
			.useButton("Nein") //
		;
	}

	public void askMoveSourcesToTargets_DONE() {
		this.runDialog_DONE() //
			.useTitle("Sollen alle Dateien wirklich nicht ersetzend verschoben werden?") //
			.useMessage("""
				Jede Zeile des Textfeldes besteht dazu aus einem Quell- und einen Zieldateipfad. \
				Trennzeichen der Pfade ist ein Tabulator. \
				Die Zeilen erfolgreich verschobener Dateien werden entfernt.""") //
			.useButton("Ja", () -> this.runProcSourcesToTargetsImpl_DONE(true)) //
			.useButton("Nein") //
		;
	}

	public void DONE_askShowSourcesAndTargets() {
		this.runDialog_DONE() //
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
			var parentFile = Files.createTempDirectory("file-tool-app-").toFile();
			var linkCount = new int[2];
			var inputList = this.getEntries_DONE();
			this.runItems(proc, inputList, entry -> {
				var source = entry.source;
				var target = entry.target;
				if ((source.fileOrNull() != null) && source.fileOrNull().isFile() && (target.fileOrNull() != null) && target.fileOrNull().isFile()) {
					linkCount[0]++;
					var sourceLink = new File(parentFile, linkCount[0] + "-ORIGINAL-" + source.fileOrNull().getName());
					var targetLink = new File(parentFile, linkCount[0] + "-DUPLIKAT-" + target.fileOrNull().getName());
					try {
						Files.createSymbolicLink(sourceLink.toPath(), source.pathOrNull());
						linkCount[1]++;
					} catch (Exception ignored) {}
					try {
						Files.createSymbolicLink(targetLink.toPath(), target.pathOrNull());
						linkCount[1]++;
					} catch (Exception ignored) {}
				}
			}, null);
			Desktop.getDesktop().open(parentFile);
		});
	}

	FTSettings settings = new FTSettings();

	/** Diese Methode führt die gegebene Berechnung {@code task} mit dem gegebenen Titel {@code title} in einem {@link Thread} aus. */
	public void runTask(String title, AppTask task) {
		this.queue.push(title, task);
	}

	private <GItem> void runItems(AppProcess proc, Collection<GItem> items, Consumer<GItem> regular, Consumer<GItem> canceled) {
		var iter = items.iterator();
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
	private void runLater(Runnable task) {
		this.display.asyncExec(task);
	}

	private void runDemo(AppProcess task) throws Exception {
		int stop = 300;
		int step = 100;
		for (int i = 0; (i < stop) && !task.isCanceled; i += step) {
			task.steps = stop - i;
			try {
				Thread.sleep(step);
			} catch (InterruptedException e) {}
		}
	}

	public void DONE_runInfoUpdate() {
		this.display.timerExec(500, this::DONE_runInfoUpdate);
		var proc = this.queue.current();
		this.info.setText(proc != null ? Objects.notNull(proc.title, "?") + " - " + proc.steps + " - " + Objects.notNull(proc.object, "") : " ");
	}

}
