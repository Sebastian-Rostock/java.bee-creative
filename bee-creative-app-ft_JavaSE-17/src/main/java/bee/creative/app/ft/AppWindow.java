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
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import bee.creative.fem.FEMDatetime;
import bee.creative.io.IO;
import bee.creative.lang.Bytes;
import bee.creative.lang.Objects;
import bee.creative.lang.Strings;
import bee.creative.util.Comparators;
import bee.creative.util.Consumer;
import bee.creative.util.Filter;
import bee.creative.util.Getter;
import bee.creative.util.HashMap2;
import bee.creative.util.HashSet2;
import bee.creative.util.Iterables;
import bee.creative.util.Producer;

public class AppWindow {

	public static void main(String args[]) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new AppWindow();
	}

	public AppWindow() {
		{
			this.settings = new AppSettings();
			this.settings.restore();
		}
		{
			this.display = Display.getDefault();
		}
		{
			this.procQueue = new AppQueue() {

				// TODO setter
				@Override
				public void onError(AppProcess proc, Throwable error) {
					AppWindow.this.runDialog_DONE() //
						.useTitle(proc != null ? proc.title : "") //
						.useMessage("Unerwarteter Fehler\n%s", error);
				}

				// TODO consumer
				@Override
				public void onSelect(AppProcess proc) {
					AppWindow.this.display.syncExec(() -> {
						AppWindow.this.enableText_DONE();
						AppWindow.this.enableStop_DONE();
					});
				}

			};
		}
		{
			this.shell = new Shell(this.display, SWT.SHELL_TRIM);
			this.shell.setText("File-Tool");
			this.shell.setSize(600, 400);
			this.shell.setLayout(new GridLayout(1, false));
			this.shell.setMenuBar(new Menu(this.shell, SWT.BAR));
			var mbar = this.shell.getMenuBar();

			this.undo = this.createMenuItem(mbar, "arrow-undo.png", null, this::runUndo_DONE);
			this.undo.setAccelerator(SWT.CTRL | 'Z');

			this.redo = this.createMenuItem(mbar, "arrow-redo.png", null, this::runRedo_DONE);
			this.redo.setAccelerator(SWT.CTRL | 'Y');

			this.createMenuItem(mbar, "arrow-swap.png", null, this::runSwap_DONE);

			this.createMenu(mbar, "filter.png", null, menu -> {
				this.createMenuItem(menu, "file.png", "Dateien filtern...", this::runFilterBySourceFile_DONE);
				this.createMenuItem(menu, "folder.png", "Verzeichnisse filtern...", this::runFilterBySourceFolder_DONE);
				this.createMenuLine(menu);
				this.createMenuItem(menu, "update.png", "Nach Änderung filtern..", this::runFilterBySourceChange_DONE);
				this.createMenuItem(menu, "date.png", "Nach Erzeugung filtern...", this::runFilterBySourceCreate_DONE);
				this.createMenuItem(menu, "size.png", "Nach Dateigröße filtern...", this::runFilterBySourceSize_DONE);
				this.createMenuItem(menu, "name.png", "Nach Datenpfad filtern..", this::runFilterBySourcePath_DONE);
			});
			this.createMenu(mbar, "sort-asc.png", null, menu -> {
				this.createMenuItem(menu, "sort-des.png", "Rückwärts ordnen", this::runSortReverse_DONE);
				this.createMenuLine(menu);
				this.createMenuItem(menu, "update.png", "Nach Änderung sortieren", this::runSortByChange_DONE);
				this.createMenuItem(menu, "date.png", "Nach Erzeugung sortieren", this::runSortByCreate_DONE);
				this.createMenuItem(menu, "size.png", "Nach Dateigröße sortieren", this::runSortBySize_DONE);
				this.createMenuItem(menu, "name.png", "Nach Eingabepfad sortieren", this::runSortByPath_DONE);
			});
			this.createMenu(mbar, "hash.png", null, menu -> {
				this.createMenuItem(menu, "clone.png", "Duplikate erkennen", this::runAnalyzeContent_DONE);
				this.createMenuLine(menu);
				this.createMenuItem(menu, "hash-check.png", "Streuwertepuffer erzeugen", this::runSetupCaches_DONE);
				this.createMenuItem(menu, "hash-update.png", "Streuwertepuffer befüllen", this::runUpdateCaches_DONE);
			});
			this.createMenu(mbar, "mem.png", null, menu -> {
				// this.createMenuItem(menu, AppIcon.saveVariable, "...in Variable speichern", this::runSaveEntriesToVar);
				// this.createMenuItem(menu, AppIcon.loadVariable, "...aus Variable einfügen", this::runLoadEntriesFromVar);
				// this.createMenuLine(menu);
				this.createMenuItem(menu, "copy.png", "Datenpfade in Zwischenablage kopieren", this::runSaveSourcesToClipboard_DONE);
				this.createMenuItem(menu, "paste.png", "Datenpfade aus Zwischenablage annfügen", this::runLoadSourcesFromClipboard_DONE);
			});
			this.createMenu(mbar, "name.png", null, menu -> {
				this.createMenuItem(menu, "name-okay.png", "Zeitnamen ableiten", this::runSetupTimename);
				this.createMenuItem(menu, "name-update.png", "Zeitnamen aktualisieren", this::runUpdateTimename);
				this.createMenuItem(menu, "name-edit.png", "Zeitpfade aktualisieren", this::runUpdateTimepath);
			});
			this.createMenu(mbar, "file.png", null, menu -> {
				this.createMenuItem(menu, "delete-perm.png", "Dateien löschen", this::runDeleteFilesPermanently_DONE);
				this.createMenuItem(menu, "delete-temp.png", "Dateien recyclen", this::runDeleteFilesTemporary_DONE);
				this.createMenuLine(menu);
				this.createMenuItem(menu, "update.png", "Dateien erneuern", this::runRefreshFiles_DONE);
				this.createMenuLine(menu);
				this.createMenuItem(menu, "show.png", "Dateien anzeigen", this::runShowFiles_DONE);
				this.createMenuItem(menu, "copy.png", "Dateien kopieren", this::runCopyFiles_DONE);
				this.createMenuItem(menu, "move.png", "Dateien umbenennen", this::runMoveFiles_DONE);
			});
			this.createMenu(mbar, "folder.png", null, menu -> {
				this.createMenuItem(menu, "delete-perm.png", "Verzeichnisse löschen", this::runDeleteFoldersPermanently_DONE);
				this.createMenuItem(menu, "delete-temp.png", "Verzeichnisse recyclen", this::runDeleteFoldersTemporary_DONE);
				this.createMenuLine(menu);
				this.createMenuItem(menu, "file-search.png", "Dateien auflösen...", this::runResolveFiles_DONE);
				this.createMenuItem(menu, "folder-search.png", "Verteichnisse auflösen...", this::runResolveFolders_DONE);
			});
			this.stop = this.createMenuItem(mbar, "stop.png", null, this::runStop_DONE);
			this.setIcon(this.shell::setImage, "file-gear.png");
		}
		{
			this.text = new Text(this.shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
			this.text.addListener(SWT.Verify, event -> this.runEdit_DONE());
			this.text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			var dnd = new DropTarget(this.text, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
			dnd.setTransfer(FileTransfer.getInstance(), TextTransfer.getInstance());
			dnd.addDropListener(new DropTargetAdapter() {

				@Override
				public void drop(DropTargetEvent event) {
					event.detail = DND.DROP_COPY;
					try {
						if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
							var fileList = (String[])FileTransfer.getInstance().nativeToJava(event.currentDataType);
							AppWindow.this.runPush_DONE(Arrays.asList(fileList));
						} else if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
							var file = (String)TextTransfer.getInstance().nativeToJava(event.currentDataType);
							AppWindow.this.runPush_DONE(Arrays.asList(file));
						}
					} catch (Exception ignore) {}
				}

			});
		}
		{
			this.info = new Label(this.shell, SWT.NONE);
			this.info.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		}
		{
			this.edit = System.currentTimeMillis() + Integer.MIN_VALUE;
		}
		this.enableUndo_DONE();
		this.enableRedo_DONE();
		this.runInfo_DONOE();
		{
			var srcRect = this.shell.getBounds();
			var tgtRect = this.shell.getDisplay().getPrimaryMonitor().getBounds();
			this.shell.setLocation(tgtRect.x + ((tgtRect.width - srcRect.width) / 2), tgtRect.y + ((tgtRect.height - srcRect.height) / 2));
		}
		this.shell.open();
		AppDialog.wait(this.shell);
		this.settings.persist();
	}

	private final AppSettings settings;

	private final Display display;

	private final Shell shell;

	private final MenuItem stop;

	private final MenuItem redo;

	private final MenuItem undo;

	private final Text text;

	private final Label info;

	/** Dieses Feld speichert den Zeitpunkt, ab welchem Änderungen über {@link #runEdit_DONE()} in den {@link #undoQueue} übernommen werden dürfen. */
	private long edit;

	private final AppQueue procQueue;

	private final LinkedList<AppState> redoQueue = new LinkedList<>();

	private final LinkedList<AppState> undoQueue = new LinkedList<>();

	private void setText(Consumer<String> taret, String text) {
		try {
			if (text == null) return;
			taret.set(text);
		} catch (Exception ignore) {}
	}

	private void setIcon(Consumer<Image> taret, String icon) {
		try {
			if (icon == null) return;
			taret.set(new Image(this.display, AppWindow.class.getResourceAsStream(icon)));
		} catch (Exception ignore) {}
	}

	private MenuItem createMenu(Menu parent, String image, String label, Consumer<Menu> setup) {
		var res = new MenuItem(parent, SWT.CASCADE);
		this.setIcon(res::setImage, image);
		this.setText(res::setText, label);
		res.setMenu(new Menu(res));
		setup.set(res.getMenu());
		return res;
	}

	private MenuItem createMenuItem(Menu parent, String image, String label, Runnable onClick) {
		var res = new MenuItem(parent, SWT.NONE);
		this.setIcon(res::setImage, image);
		this.setText(res::setText, label);
		if (onClick != null) {
			res.addListener(SWT.Selection, event -> onClick.run());
		}
		return res;
	}

	private MenuItem createMenuLine(Menu parent) {
		return new MenuItem(parent, SWT.SEPARATOR);
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

	private void enableUndo_DONE() {
		this.undo.setEnabled(!this.undoQueue.isEmpty());
	}

	private void enableRedo_DONE() {
		this.redo.setEnabled(!this.redoQueue.isEmpty());
	}

	private void enableStop_DONE() {
		this.stop.setEnabled(this.procQueue.isRunning());
	}

	private void enableText_DONE() {
		this.text.setEnabled(!this.procQueue.isRunning());
	}

	/** Diese Methode führt die gegebene Berechnung {@code task} mit dem gegebenen Titel {@code title} in einem {@link Thread} aus. */
	private void runTask(String title, AppTask task) {
		this.procQueue.push(title, task);
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

	public AppDialog runDialog_DONE() {
		var res = new AppDialog(this.shell);
		this.shell.getDisplay().asyncExec(res::open);
		return res.useFocus(true);
	}

	public void runEdit_DONE() {
		this.display.syncExec(() -> {
			var time = System.currentTimeMillis();
			if ((time - this.edit) >= 0) {
				this.undoQueue.addFirst(new AppState(this.text));
				this.enableUndo_DONE();
				this.redoQueue.clear();
				this.enableRedo_DONE();
			}
			this.edit = time + 500;
		});
	}

	public void runUndo_DONE() {
		this.runTask("Undo", proc -> this.display.syncExec(() -> {
			if (this.undoQueue.isEmpty()) return;
			var state = this.undoQueue.removeFirst();
			this.enableUndo_DONE();
			this.redoQueue.addFirst(new AppState(this.text));
			this.enableRedo_DONE();
			this.edit = System.currentTimeMillis() + Integer.MAX_VALUE;
			state.apply(this.text);
		}));
	}

	public void runRedo_DONE() {
		this.runTask("Redo", proc -> this.display.syncExec(() -> {
			if (this.redoQueue.isEmpty()) return;
			var state = this.redoQueue.removeFirst();
			this.enableRedo_DONE();
			this.undoQueue.addFirst(new AppState(this.text));
			this.enableUndo_DONE();
			this.edit = System.currentTimeMillis() + Integer.MAX_VALUE;
			state.apply(this.text);
		}));
	}

	public void runSwap_DONE() {
		this.runTask("Tauschen", proc -> {
			var entries = AppEntry.list();
			this.runItems(proc, this.getEntries_DONE(), entry -> entries.add(new AppEntry(entry.target.text, entry.source.text)), entries::add);
			this.setEntries_DONE(entries);
		});
	}

	public void runStop_DONE() {
		this.procQueue.cancel();
	}

	public void runPush_DONE(List<String> sourceList) {
		this.runTask("Datenpfade anfügen", proc -> {
			this.runPushImpl(proc, sourceList);
		});
	}

	private void runPushImpl(AppProcess proc, List<String> sourceList) {
		if (sourceList.isEmpty()) return;
		var input = this.getInput();
		var entries = AppEntry.list();
		if (!input.isEmpty()) {
			entries.add(new AppEntry(input, ""));
		}
		this.runItems(proc, sourceList, source -> entries.add(new AppEntry(source, "")), null);
		this.setEntries_DONE(entries);
	}

	private void runFilterBySourceImpl_DONE(String title, Producer<Filter<AppItem>> filterBuilder) {
		this.runTask(title, proc -> {
			var result = AppEntry.list();
			var filter = filterBuilder.get();
			this.runItems(proc, this.getEntries_DONE(), entry -> {
				if (filter.accept(entry.source)) {
					result.add(entry);
				}
			}, result::add);
			this.setEntries_DONE(result);
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
		this.runFilterBySourceImpl_DONE(isKeep ? "Eingabepfade behalten (Datei)" : "Eingabepfade entfernen (Datei)",
			() -> source -> (source.fileOrNull() != null) && (source.file.isFile() == isKeep));
	}

	public void runFilterBySourceFolder_DONE() {
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, die existierende Verzeichnisse angeben?") //
			.useMessage("Relative Eingabepfade werden entfernt.") //
			.useButton("Eingabepfade von Verzeichnissen behalten", () -> this.runFilterBySourceFolderImpl_DONE(true)) //
			.useButton("Eingabepfade von Verzeichnissen entfernen", () -> this.runFilterBySourceFolderImpl_DONE(false)) //
		;
	}

	private void runFilterBySourceFolderImpl_DONE(boolean isKeep) {
		this.runFilterBySourceImpl_DONE(isKeep ? "Eingabepfade behalten (Verzeichnis)" : "Eingabepfade entfernen (Verzeichnis)",
			() -> source -> (source.fileOrNull() != null) && (source.file.isDirectory() == isKeep));
	}

	public void runFilterBySourcePath_DONE() { //
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, für die der unten genannte reguläre Ausdruck einen Treffer findet?") //
			.useOption("Regulärer Ausdruck", this.settings.filterPath) //
			.useButton("Eingabepfade behalten", () -> this.runFilterBySourcePathImpl_DONE(true)) //
			.useButton("Eingabepfade entfernen", () -> this.runFilterBySourcePathImpl_DONE(false)) //
		;
	}

	private void runFilterBySourcePathImpl_DONE(boolean isKeep) {
		var filter = this.settings.filterPath.putValue().getValue();
		this.runFilterBySourceImpl_DONE(isKeep ? "Eingabepfade behalten (Muster)" : "Eingabepfade entfernen (Muster)", () -> {
			var filterBuilder = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
			return source -> filterBuilder.matcher(source.text).find() == isKeep;
		});
	}

	public void runFilterBySourceSize_DONE() {
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, die Dateie angeben, deren Größe kleiner als die unten genannte ist?") //
			.useMessage("Relative Eingabepfade und die zu Verzeichnissen werden entfernt.") //
			.useOption("Dateigröße", this.settings.filterSize) //
			.useButton("Pfade kleinere Dateien behalten", () -> this.runFilterBySourceSizeImpl_DONE(true)) //
			.useButton("Pfade kleinere Dateien entfernen", () -> this.runFilterBySourceSizeImpl_DONE(false)) //
		;
	}

	private void runFilterBySourceSizeImpl_DONE(boolean isKeep) {
		var filter = this.settings.filterSize.getValue();
		this.runFilterBySourceImpl_DONE(isKeep ? "Eingabepfade behalten (Größe)" : "Eingabepfade entfernen (Größe)",
			() -> source -> (source.sizeOrNull() != null) && ((source.size.longValue() < filter) == isKeep));
	}

	public void runFilterBySourceCreate_DONE() { //
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, die vor dem unten genannten Zeitpunkt erzeugte Dateien angeben?") //
			.useMessage("Relative Eingabepfade und die zu Verzeichnissen werden entfernt.") //
			.useOption("Erzeugungszeitpunkt", this.settings.filterCreate) //
			.useButton("Pfade alterer Dateien behalten", () -> this.runFilterBySourceCreateImpl_DONE(true)) //
			.useButton("Pfade alterer Dateien entfernen", () -> this.runFilterBySourceCreateImpl_DONE(false)) //
		;
	}

	private void runFilterBySourceCreateImpl_DONE(boolean isKeep) {
		var filter = this.settings.filterCreate.getValue();
		this.runFilterBySourceImpl_DONE(isKeep ? "Eingabepfade behalten (Erzeugung)" : "Eingabepfade entfernen (Erzeugung)",
			() -> source -> (source.madeOrNull() != null) && ((source.made.longValue() < filter) == isKeep));
	}

	public void runFilterBySourceChange_DONE() { //
		this.runDialog_DONE() //
			.useTitle("Möchten Sie Eingabepfade behalten bzw. entfernen, die vor dem unten genannten Zeitpunkt geänderte Dateien angeben?") //
			.useMessage("Relative Eingabepfade und die zu Verzeichnissen werden entfernt.") //
			.useOption("Änderungszeitpunkt", this.settings.filterChange) //
			.useButton("Pfade alterer Dateien behalten", () -> this.runFilterBySourceChangeImpl_DONE(true)) //
			.useButton("Pfade alterer Dateien entfernen", () -> this.runFilterBySourceChangeImpl_DONE(false)) //
		;
	}

	private void runFilterBySourceChangeImpl_DONE(boolean isKeep) {
		var filter = this.settings.filterCreate.getValue();
		this.runFilterBySourceImpl_DONE(isKeep ? "Eingabepfade behalten (Änderung)" : "Eingabepfade entfernen (Änderung)",
			() -> source -> (source.timeOrNull() != null) && ((source.time.longValue() < filter) == isKeep));
	}

	public void runSortBySize_DONE() {
		this.runSortByImpl_DONE("Sortierung nach Größe", Comparators.LongComparator.INSTANCE.translate(AppItem::sizeOrNull));
	}

	public void runSortByPath_DONE() {
		this.runSortByImpl_DONE("Sortierung nach Datenpfad", Comparators.AlphanumericalComparator.INSTANCE.translate(item -> item.text));
	}

	public void runSortByCreate_DONE() {
		this.runSortByImpl_DONE("Sortierung nach Erzeugung", Comparators.LongComparator.INSTANCE.translate(AppItem::madeOrNull));
	}

	public void runSortByChange_DONE() {
		this.runSortByImpl_DONE("Sortierung nach Änderung", Comparators.LongComparator.INSTANCE.translate(AppItem::timeOrNull));
	}

	private void runSortByImpl_DONE(String title, Comparator<AppItem> order) {
		this.runTask(title, proc -> {
			var result = this.getEntries_DONE();
			result.sort(Comparators.optionalize(order).translate(entry -> entry.source));
			this.setEntries_DONE(result);
		});
	}

	public void runSortReverse_DONE() {
		this.runTask("Sortierung umkehren", proc -> {
			var result = this.getEntries_DONE();
			Collections.reverse(result);
			this.setEntries_DONE(result);
		});
	}

	public void runAnalyzeContent_DONE() {
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
				Relative Eingabepfade und die zu Verzeichnissen werden ignoriert.""", AppHashes.FILENAME) //
			.useOption("Puffergröße für Streuwert", this.settings.contentHashSize) //
			.useOption("Puffergröße für Dateivergleich", this.settings.contentTestSize) //
			.useButton("Duplikate Dateien finden", () -> this.runAnalyzeContentImpl_DONE(true)) //
			.useButton("Einzigartige Dateien finden", () -> this.runAnalyzeContentImpl_DONE(false)) //
		;
	}

	private void runAnalyzeContentImpl_DONE(boolean isKeep) {
		var hashSize = this.settings.contentHashSize.getValue();
		var testSize = this.settings.contentTestSize.getValue();
		this.runTask(isKeep ? "Duplikate behalten" : "Duplikate entfernen", proc -> {

			var caches = new AppCaches();
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

			// Dateien je Dateigröße zusammenfassen
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

			// Dateien je Inhalt zusammenfassen
			pathSet.clear();
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

					}

					var equalHashIter = hashListMap.values().iterator();
					while (equalHashIter.hasNext() && !proc.isCanceled) {
						var equalHashList = equalHashIter.next();

						if (equalHashList.prev != null) {

							itemDataListMap.clear();
							for (AppItem item3 = equalHashList, prev; (item3 != null) && !proc.isCanceled; item3 = prev) { // rückwärts

								prev = item3.prev;

								item3.data = new AppData(item3.text, testSize2);

								item3.prev = itemDataListMap.put(item3.data, item3);

							}
							for (var equalDataList: itemDataListMap.values()) {
								if (equalDataList.prev != null) {
									originalList.add(equalDataList);
								} else {
									pathSet.add(equalDataList);
								}
							}

						} else {
							pathSet.add(equalHashList);
						}
					}

				} else {
					pathSet.add(equalSizeList);
				}
			}

			caches.persist();

			var result = AppEntry.list();
			var originalMap = new HashMap2<String, AppItem>(1000);

			originalList.forEach(original -> originalMap.put(original.file.getPath(), original));
			entryList.forEach(entry -> {
				if (isKeep) {
					var file = entry.source.fileOrNull();
					if (file == null) return;
					var path = file.getPath();
					var original = originalMap.remove(path);
					if (original == null) return;
					for (var duplikat = original.prev; duplikat != null; duplikat = duplikat.prev) { // vorwärts
						result.add(new AppEntry(original, duplikat));
					}
				} else {
					if (!pathSet.remove(entry.source)) return;
					result.add(entry);
				}
			});

			this.setEntries_DONE(result);
		});
	}

	public void runSetupCaches_DONE() {
		this.runDialog_DONE() //
			.useTitle("Sollen Streuwertepuffer in den Eingabeordnern angelegt werden?") //
			.useMessage("""
				Die Pufferdateien tragen den Namen '%s'.
				Relative Eingabepfade und die zu Dateien werden ignoriert.""", AppHashes.FILENAME) //
			.useButton("Streuwertepuffer erzeugen", () -> this.runSetupCachesImpl_DONE()) //
		;
	}

	private void runSetupCachesImpl_DONE() {
		this.runTask("Streuwertepuffer anlegen", proc -> {
			this.runItems(proc, this.getEntries_DONE(), entry -> {
				try {
					if (entry.source.fileOrNull() != null) {
						AppHashes.fileFrom(entry.source.text).createNewFile();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}, null);
		});
	}

	public void runUpdateCaches_DONE() {
		this.runDialog_DONE() //
			.useTitle("Sollen Streuwertepuffer mit den Streuwerten der Eingabedateien befüllt werden?") //
			.useMessage("""
				Die SHA-256-Streuwerte werden aus höchstens der unten genannten Anzanl an Bytes jeweils ab dem Dateibeginn und dem Dateiende berechnet \
				und in der Pufferdatei '%s' zwischengespeichert. Dabei wird die im Elternpfad näheste bzw. die im Verzeichnis dieser Anwendung liegende \
				Pufferdatei verwendet.
				Relative Eingabepfade und die zu Verzeichnissen werden ignoriert.""", AppHashes.FILENAME) //
			.useOption("Puffergröße für Streuwert", this.settings.contentHashSize) //
			.useButton("Streuwertepuffer befüllen", () -> this.runUpdateCachesImpl_DONE()) //
		;
	}

	private void runUpdateCachesImpl_DONE() {
		var hashSize = this.settings.contentHashSize.getValue();
		this.runTask("Streuwertepuffer befüllen", proc -> {
			var caches = new AppCaches();
			caches.restore();
			this.runItems(proc, this.getEntries_DONE(), entry -> {
				var file = entry.source.fileOrNull();
				if ((file != null) && file.isFile()) {
					caches.get(file.getPath(), hashSize);
				}
			}, null);
			caches.persist();
		});
	}

	public void runSaveSourcesToClipboard_DONE() {
		this.runTask("Kopieren", prov -> {
			var pathList = new ArrayList<String>(1000);
			this.runItems(prov, this.getEntries_DONE(), entry -> {
				if (entry.source.fileOrNull() == null) return;
				pathList.add(entry.source.file.getPath());
			}, null);
			var fileData = pathList.toArray(new String[0]);
			var textData = Strings.join(System.lineSeparator(), pathList);
			this.display.syncExec(() -> {
				var clp = new Clipboard(this.display);
				clp.setContents(new Object[]{fileData, textData}, new Transfer[]{FileTransfer.getInstance(), TextTransfer.getInstance()});
				clp.dispose();
			});
		});
	}

	public void runLoadSourcesFromClipboard_DONE() {
		this.runTask("Einfügen", proc -> {
			this.runPushImpl(proc, this.display.syncCall(() -> {
				var clp = new Clipboard(this.display);
				var res = (String[])clp.getContents(FileTransfer.getInstance());
				clp.dispose();
				return res != null ? Arrays.asList(res) : Collections.emptyList();
			}));
		});
	}

	public void runReplaceByPattern() { // TODO target = regex(source)
	}

	public void runResolveFiles_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle Verzeichnispfade wirklich durch die Pfade aller darin enthaltenen Dateien ersetzt werden?") //
			.useMessage("""
				Die Dateiauflösung wird in allen Unterverzeichnissen fortgesetzt. \
				Duplikate sowie relative Eingabepfade werden ignoriert, die zu Dateien bleiben erhalten.""") //
			.useButton("Dateipfade ermitteln", () -> this.runResolveImpl_DONE(true)) //
		;
	}

	public void runResolveFolders_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle Verzeichnispfade wirklich um die Pfade aller darin enthaltenen Verzeichnisse ergänzt werden?") //
			.useMessage("""
				Die Verzeichnisauflösung wird in allen Unterverzeichnissen fortgesetzt. \
				Duplikate sowie relative Eingabepfade und die zu Dateien werden ignoriert.""") //
			.useButton("Verzeichnispfade ermitteln", () -> this.runResolveImpl_DONE(false)) //
		;
	}

	private void runResolveImpl_DONE(boolean isFile) {
		this.runTask(isFile ? "Dateien auflösen" : "Verzeichnisse auflösen", proc -> {
			var result = AppEntry.list();
			var pathSet = new HashSet2<>(100);
			var fileStack = new LinkedList<File>();
			this.runItems(proc, this.getEntries_DONE(), line -> {
				var file = line.source.fileOrNull();
				if (file != null) {
					proc.steps++;
					fileStack.addLast(file);
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

	public void runSetupTimename() {
		this.runDialog_DONE() //
			.useTitle("Sollen die Zielnamen wirklich aus den Änderungszeitpunkten der Quelldateien abgeleitet werden?") //
			.useMessage("""
				Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft \
				verschobenen Änderungszeitpunkten der Quelldateien.
				Die Zielpfade haben das Format {EP}\\JJJJ-MM-TT hh.mm.ss{NE}, wobei {EP} für den \
				Elternverzeichnispfad und {NE} für die kleingeschriebene Namenserweiterung der Quelldatei stehen.""") //
			.useOption("Zeitkorrektur in Sekunden", this.settings.timenameOffset) //
			.useButton("Datei-Zeitnamen ableiten", () -> this.runComputeTimenameImpl("Datei-Zeitnamen ableiten", false, this::getDatetimeFromFile)) //
			.useButton("MP4-Zeitnamen ableiten", () -> this.runComputeTimenameImpl("MP4-Zeitnamen ableiten", false, this::getDatetimeFromMP4)) //
		;
	}

	public void runUpdateTimename() {
		this.runDialog_DONE() //
			.useTitle("Sollen die Zielnamen wirklich aus den Zeitpunkten in den Quellnamen abgeleitet werden?") //
			.useMessage("""
				Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft \
				verschobenen Zeitpunkten, die im Quellnamen mit beliebigen Trennzeichen angegeben sind.
				Die Zielpfade haben das Format {EP}\\JJJJ-MM-TT hh.mm.ss{NE}, wobei {EP} für den \
				Elternverzeichnispfad und {NE} für die kleingeschriebene Namenserweiterung der Quelldatei stehen.""") //
			.useOption("Zeitkorrektur in Sekunden", this.settings.timenameOffset) //
			.useButton("Zeitnamen aktualisieren", () -> this.runComputeTimenameImpl("Zeitnamen aktualisieren", false, this::getDatetimeFromName)) //
		;
	}

	public void runUpdateTimepath() {
		this.runDialog_DONE() //
			.useTitle("Sollen die Zielpfade wirklich aus den Zeitpunkten in den Quellnamen abgeleitet werden?") //
			.useMessage("""
				Die verwendeten Zeitpunkte entsprechen den um die unten angegebene Anzahl an Sekunden in die Zukunft \
				verschobenen Zeitpunkten, die im Quellnamen mit beliebigen Trennzeichen angegeben sind.
				Die Zielpfade haben das Format {GP}\\JJJJ-MM_{EN}\\JJJJ-MM-TT hh.mm.ss{NE}, wobei {GP} für den \
				Großelternverzeichnispfad, {EN} für den Elternverzeichnisnamen und {NE} für die kleingeschriebene \
				Namenserweiterung der Quelldatei stehen.""") //
			.useOption("Zeitkorrektur in Sekunden", this.settings.timenameOffset) //
			.useButton("Zeitpfade aktualisieren", () -> this.runComputeTimenameImpl("Zeitpfade aktualisieren", true, this::getDatetimeFromName)) //
		;
	}

	private void runComputeTimenameImpl(String title, boolean isPath, Getter<File, FEMDatetime> getDatetime) {
		// isName = true, wenn Zeitpunkt aus Dateinamen abgeleitet werden soll
		// isName = false, wenn Anderungszeitpunkt verwendet werden soll
		long moveTime = this.settings.timenameOffset.getValue();
		this.runTask(title, proc -> {
			var result = AppEntry.list();
			var pathSet = new HashSet2<String>(1000);
			this.runItems(proc, this.getEntries_DONE(), line -> {
				var sourceFile = line.source.fileOrNull();
				if (sourceFile == null) return;
				var parentFile = sourceFile.getParentFile();
				if (parentFile == null) return;
				var parentName = isPath ? parentFile.getName() : null;
				var grandparentFile = isPath ? parentFile.getParentFile() : parentFile;
				if (grandparentFile == null) return;
				var sourceName = sourceFile.getName();
				var index = sourceName.lastIndexOf('.');
				if (index < 0) return;
				var sourceType = sourceName.substring(index).toLowerCase();
				var datetime = getDatetime.get(sourceFile);
				if (datetime == null) return;
				datetime = datetime.move(0, moveTime * 1000);
				while (true) {
					var targetName = String.format("%04d-%02d-%02d %02d.%02d.%02d%s", //
						datetime.yearValue(), datetime.monthValue(), datetime.dateValue(), //
						datetime.hourValue(), datetime.minuteValue(), datetime.secondValue(), sourceType);
					var targetFile = isPath //
						? new File(new File(grandparentFile, String.format("%04d-%02d_%s", //
							datetime.yearValue(), datetime.monthValue(), parentName)), targetName) //
						: new File(parentFile, targetName);
					var targetPath = targetFile.getPath();
					if (pathSet.add(targetPath)) {
						result.add(new AppEntry(sourceFile.getPath(), targetPath));
						return;
					}
					datetime = datetime.move(0, 1000);
				}
			}, null);
			this.setEntries_DONE(result);
		});
	}

	FEMDatetime getDatetimeFromFile(File sourceFile) {
		if (!sourceFile.isFile()) return null;
		return FEMDatetime.from(sourceFile.lastModified());
	}

	static final Pattern namePattern = Pattern.compile("([0-9]{4})[^0-9]*([0-9]{2})[^0-9]*([0-9]{2})[^0-9]*([0-9]{2})[^0-9]*([0-9]{2})[^0-9]*([0-9]{2})[^0-9]*");

	FEMDatetime getDatetimeFromName(File sourceFile) {
		var nameMatcher = namePattern.matcher(sourceFile.getName());
		if (!nameMatcher.find()) return null;
		var yearValue = nameMatcher.group(1);
		var monthValue = nameMatcher.group(2);
		var dateValue = nameMatcher.group(3);
		var hourValue = nameMatcher.group(4);
		var minuteValue = nameMatcher.group(5);
		var secondValue = nameMatcher.group(6);
		try {
			return FEMDatetime.from(yearValue + "-" + monthValue + "-" + dateValue + "T" + hourValue + ":" + minuteValue + ":" + secondValue);
		} catch (RuntimeException ignore) {
			try {
				return FEMDatetime.fromDate(Integer.parseInt(yearValue), 1, 1).withTime(0)
					.move(0, 0, Integer.parseInt(dateValue) - 1, Integer.parseInt(hourValue), Integer.parseInt(minuteValue), Integer.parseInt(secondValue), 0)
					.move(Integer.parseInt(monthValue) - 1, 0);
			} catch (RuntimeException e) {
				return null;
			}
		}
	}

	static final FEMDatetime MP4_BASE_DATETIME = FEMDatetime.from("1904-01-01T00:00:00Z");

	FEMDatetime getDatetimeFromMP4(File sourceFile) {
		if (!sourceFile.isFile()) return null;
		try (var sourceStream = IO.inputStreamFrom(sourceFile)) {
			var buf = new byte[4];
			{ // ftype
				sourceStream.readNBytes(buf, 0, 4);
				var boxSize = Bytes.getInt4BE(buf, 0);
				sourceStream.readNBytes(buf, 0, 4);
				var boxName = Bytes.getInt4BE(buf, 0);
				if (boxName != 1718909296) return null;
				sourceStream.skip(boxSize);
			}
			while (true) {
				sourceStream.readNBytes(buf, 0, 4);
				var boxSize = Bytes.getInt4BE(buf, 0);
				sourceStream.readNBytes(buf, 0, 4);
				var boxName = Bytes.getInt4BE(buf, 0);
				if (boxName == 1836476516) { // mvhd
					sourceStream.readNBytes(buf, 0, 4);
					var mvhdVersion = Bytes.getInt4BE(buf, 0);
					if (mvhdVersion != 0) return null;
					sourceStream.readNBytes(buf, 0, 4);
					var mvhdCreated = Bytes.getInt4BE(buf, 0) & 0xFFFFFFFFL;
					var move = MP4_BASE_DATETIME.move(0, 0, 0, 0, 0, mvhdCreated, 0);
					//System.out.println(move);
					return move;
				}
				sourceStream.skip(boxSize);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public void runRefreshFiles_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle alten Dateien wirklich erneuert werden?") //
			.useMessage("""
				Beim Erneuern werden alle Dateien, die vor mehr als der unten angegebenen Anzahl \
				an Tagen erstellt wurden, kopiert und durch ihre Kopien ersetzt. \
				Die dazu temporär erzeugten Kopien tragen die Dateiendung '.tempcopy'. \
				Die Zeilen erneuerter Dateien werden aus der Pfadliste entfert.""") //
			.useOption("Dateialter in Tagen", this.settings.refreshOffset) //
			.useButton("Dateien erneuern", () -> this.runRefreshFilesImpl_DONE()) //
		;
	}

	private void runRefreshFilesImpl_DONE() {
		this.runTask("Dateien erneuern", proc -> {
			var filterTime = System.currentTimeMillis() - (this.settings.refreshOffset.getValue() * 24 * 60 * 60 * 1000);
			var entryList = AppEntry.list();
			this.runItems(proc, this.getEntries_DONE(), entry -> {
				try {
					var sourceFile = entry.source.fileOrNull();
					if ((sourceFile != null) && sourceFile.isFile() && (entry.source.madeOrNull().longValue() < filterTime)) {
						var sourcePath = sourceFile.toPath();
						var targetFile = new File(sourceFile.getParentFile(), sourceFile.getName() + ".tempcopy");
						var targetPath = targetFile.toPath();
						Files.copy(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES);
						Files.move(targetPath, sourcePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
						return;
					}
				} catch (Exception ignored) {}
				entryList.add(entry);
			}, entryList::add);
			this.setEntries_DONE(entryList);
		});
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

	public void runDeleteFilesTemporary_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle Eingabedateien wirklich in den Papierkorb verschoben werden?") //
			.useMessage("Die Zeilen recycelter Dateien werden aus der Pfadtabelle entfert.") //
			.useButton("Dateien recyclen", () -> this.runDeleteSourcesImpl(true, true)) //
		;
	}

	public void runDeleteFilesPermanently_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle Eingabedateien wirklich endgültig gelöscht werden?") //
			.useMessage("Die Zeilen gelöschter Dateien werden aus der Pfadtabelle entfert.") //
			.useButton("Dateien löschen", () -> this.runDeleteSourcesImpl(true, false)) //
		;
	}

	public void runDeleteFoldersTemporary_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle leeren Verzeichnisse wirklich in den Papierkorb verschoben werden?") //
			.useMessage("Die Zeilen recycelter Verzeichnisse werden aus der Pfadtabelle entfert.") //
			.useButton("Verzeichnisse recyclen", () -> this.runDeleteSourcesImpl(false, true)) //
		;
	}

	public void runDeleteFoldersPermanently_DONE() {
		this.runDialog_DONE()//
			.useTitle("Sollen alle leeren Verzeichnisse wirklich endgültig gelöscht werden?") //
			.useMessage("Die Zeilen gelöschter Verzeichnisse werden aus der Pfadtabelle entfert.") //
			.useButton("Verzeichnisse löschen", () -> this.runDeleteSourcesImpl(false, false)) //
		;
	}

	private void runProcSourcesToTargetsImpl_DONE(boolean isMove) {
		this.runTask(isMove ? "Dateiverschieben" : "Dateikopieren", proc -> {
			var result = AppEntry.list();
			this.runItems(proc, this.getEntries_DONE(), entry -> {
				var source = entry.source;
				var target = entry.target;
				if ((source.fileOrNull() != null) && source.file.isFile() && (target.fileOrNull() != null) && !target.file.isFile()) {
					try {
						target.file.getParentFile().mkdirs();
						if (isMove) {
							Files.move(source.pathOrNull(), target.pathOrNull());
						} else {
							Files.copy(source.pathOrNull(), target.pathOrNull(), StandardCopyOption.COPY_ATTRIBUTES);
						}
						result.add(new AppEntry(target.file.getPath()));
						return;
					} catch (Exception ignored) {}
				}
			}, result::add);
			this.setEntries_DONE(result);
		});
	}

	public void runCopyFiles_DONE() {
		this.runDialog_DONE() //
			.useTitle("Sollen alle Dateien wirklich nicht ersetzend kopiert werden?") //
			.useMessage("""
				Jede Zeile des Textfeldes besteht dazu aus einem Quell- und einen Zieldateipfad. \
				Trennzeichen der Pfade ist ein Tabulator. \
				Die erfolgreich kopierten Dateien werden erfasst.""") //
			.useButton("Dateien kopieren", () -> this.runProcSourcesToTargetsImpl_DONE(false)) //
		;
	}

	public void runMoveFiles_DONE() {
		this.runDialog_DONE() //
			.useTitle("Sollen alle Dateien wirklich nicht ersetzend verschoben werden?") //
			.useMessage("""
				Jede Zeile des Textfeldes besteht dazu aus einem Quell- und einen Zieldateipfad. \
				Trennzeichen der Pfade ist ein Tabulator. \
				Die erfolgreich verschobenen Dateien werden erfasst.""") //
			.useButton("Dateien verschieben", () -> this.runProcSourcesToTargetsImpl_DONE(true)) //
		;
	}

	public void runShowFiles_DONE() {
		this.runDialog_DONE() //
			.useTitle("Sollen die Quell- und Zieldateien wirklich angezeigt werden?") //
			.useMessage("""
				Jede Zeile des Textfeldes besteht dazu aus einem Quell- und einen Zieldateipfad. \
				Trennzeichen der Pfade ist ein Tabulator. \
				Die Quell- und Zieldateien werden als Symlinks in ein temporäres Verzeichnis eingefügt. \
				Die Quelldateien werden als -ORIGINAL- gekennzeichnet, die Zieldateien als -DUPLIKAT-. \
				Das temporäre Verzeichnis wird abschließend angezeigt.
				Das Erzeugen von Symlinks benötigt Administrator-Rechte!""") //
			.useButton("Dateien anzeigen", this::runShowSourcesAndTargetsImpl_DONE) //
		;
	}

	private void runShowSourcesAndTargetsImpl_DONE() {
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

	public void runInfo_DONOE() {
		this.display.timerExec(500, this::runInfo_DONOE);
		var proc = this.procQueue.current();
		this.info.setText(proc != null ? Objects.notNull(proc.title, "?") + " - " + proc.steps + " - " + Objects.notNull(proc.object, "") : " ");
	}

}
