package bee.creative.app.ft;

import javax.swing.UIManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import bee.creative.util.Consumer;

public class AppWindow extends Shell {

	static class AppButton extends Button {

		String info = "99";

		public AppButton(Composite parent, int style) {
			super(parent, style);
			this.addListener(SWT.Paint, v -> {
				var r = 4;
				var s = this.getSize();
				final var gc = v.gc;
				this.setInfoFont(gc);
				var e = gc.textExtent(this.info);
				gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION));
				gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
				gc.fillRoundRectangle(s.x - e.x - r, s.y - e.y - r, e.x + r, e.y + r, r, r);
				gc.drawText(this.info, s.x - e.x - (r / 2), s.y - e.y - (r / 2), true);
				gc.setFont(this.getFont());
			});
		}

		@Override
		protected void checkSubclass() {
		}

		private void setInfoFont(GC gc) {
			var fd = gc.getFont().getFontData()[0];
			fd.height *= .8;
			gc.setFont(new Font(gc.getDevice(), fd));
		}

	}

	public static void main(String args[]) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		Display display = Display.getDefault();
		AppWindow shell = new AppWindow(display);
		FTWindow.center(shell);
		FTWindow.openAndWait(shell);
	}

	private Text textArea;

	private AppDialog menu;

	private Composite menuArea;

	Button putMenuItem(String text, String icon, Consumer<AppDialog> onHover, Runnable onClick) {
		var tool = this.putMenuItem(text, icon, onHover, (Consumer<AppDialog>)null);
		if (onClick != null) {
			tool.addSelectionListener(SelectionListener.widgetSelectedAdapter(event -> onClick.run()));
		}
		return tool;
	}

	Button putMenuItem(String text, String icon, Consumer<AppDialog> onHover, Consumer<AppDialog> onClick) {
		var tool = new Button(this.menuArea, SWT.FLAT);
		if (text != null) {
			tool.setText(text);
		}
		if (icon != null) {
			tool.setImage(new Image(tool.getDisplay(), AppWindow.class.getResourceAsStream(icon)));
		}
		if (onHover != null) {
			this.menu.openOnHover(tool, onHover);
		}
		if (onClick != null) {
			this.menu.openOnClick(tool, onClick);
		}
		return tool;
	}

	/** Create contents of the shell. */
	protected void createContents() {
		this.setText("SWT Application");
		this.setSize(400, 200);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	AppWindow(Display display) {
		super(display, SWT.SHELL_TRIM);

		this.createMenuBar(this, men -> {
			this.createMenuItem(men, "Info", this::runShowInfo);
			this.createMenuItem(men, "Undo", this::runUndoEntries);
			this.createMenuItem(men, "Redo", this::runRedoEntries);
			this.createMenu(men, "Speichern...", save -> {
				this.createMenuItem(save, "Tabelle auf der Variable", this::runSaveEntriesToVar);
				this.createMenuBreak(save);
				this.createMenuItem(save, "Quelldateien in die Zwischenablage", this::runSaveSourcesToClip);

			});
			this.createMenu(men, "Filtern...", save -> {
				this.createMenuItem(save, "...nach Größe", this::askFilterSourcesByLength);
				this.createMenuItem(save, "...nach Muster", this::askFilterSourcesByPattern);
				this.createMenuItem(save, "...nach Existenz", this::askFilterSourcesByCreation);
				this.createMenuItem(save, "...nach Änderung", this::askFilterSourcesByModification);
				this.createMenuItem(save, "...nach Erzeugung", this::askFilterSourcesByCreation);
			});
			this.createMenu(men, "Drop...", drop -> {
				this.createMenuItem(drop, "Sources", this::runDropSources);
				this.createMenu(drop, "Entries", drop1 -> {
					this.createMenu(drop1, "BySource", drop2 -> {
						this.createMenuItem(drop2, "Aging", this::runDropEntriesBySourceAging);
						this.createMenuItem(drop2, "Existing", this::runDropEntriesBySourceExisting);
						this.createMenuItem(drop2, "Matching", this::runDropEntriesBySourceMatching);
						this.createMenuItem(drop2, "Changing", this::runDropEntriesBySourceChanging);
					});
					this.createMenu(drop1, "ByTarget", drop2 -> {});
				});

			});
			this.createMenu(men, "Swap...", swap -> {

			});
		});

		this.setLayout(new GridLayout(1, false));

		this.menu = new AppDialog(this);

		this.menuArea = new Composite(this, SWT.NONE);
		this.menuArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		RowLayout rl_menuArea = new RowLayout(SWT.HORIZONTAL);
		rl_menuArea.marginRight = 0;
		rl_menuArea.marginTop = 0;
		rl_menuArea.marginLeft = 0;
		rl_menuArea.marginBottom = 0;
		rl_menuArea.spacing = 1;
		this.menuArea.setLayout(rl_menuArea);

		this.textArea = new Text(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		this.textArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite infoArea = new Composite(this, SWT.NONE);
		GridLayout gl_infoArea = new GridLayout(2, false);
		gl_infoArea.marginWidth = 0;
		gl_infoArea.marginHeight = 0;
		infoArea.setLayout(gl_infoArea);
		infoArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label infoText = new Label(infoArea, SWT.NONE);
		infoText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		infoText.setText("New Label");

		Button stopIcon = new Button(infoArea, SWT.FLAT);
		stopIcon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		stopIcon.setText("CL");

		this.putMenuItem("U", null, menu -> menu //
			.useTitle("Rückgängig") //
			.useMessage("Die Pfadtabelle wird mit der vorherigen ersetzt."), //
			this::runUndoEntries);
		this.putMenuItem("R", null, menu -> menu //
			.useTitle("Wiederholen") //
			.useMessage("Die Pfadtabelle wird mit der nachfolgenden ersetzt."), //
			this::runRedoEntries);
		this.putMenuItem("DS", null, menu -> menu //
			.useTitle("Quellpfade verwerfen") //
			.useMessage("Die Spalte der Quellpfade wird entfernt.\nDie Zielpfade werden zu Quellpfaden."), //
			this::runDropSources);

		this.putMenuItem("DSE", null, menu -> menu //
			.useTitle("Zeilen nach Quelldateiexistenz verwerfen") //
			.useMessage("Die Spalte der Zielpfade wird entfernt."), //
			this::runDropEntriesBySourceExisting);
		this.putMenuItem("DSA", null, menu -> menu //
			.useTitle("Zeilen nach Quelldateierzeugung verwerfen") //
			.useMessage("Zeilen mit Quelldateien in bestimmtem Erzeugungszeitraum werden entfernt."), //
			this::runDropEntriesBySourceAging);
		this.putMenuItem("DSC", null, menu -> menu //
			.useTitle("Zeilen nach Quelldateiänderung verwerfen") //
			.useMessage("Die Spalte der Zielpfade wird entfernt."), //
			this::runDropEntriesBySourceChanging);
		this.putMenuItem("DSM", null, menu -> menu //
			.useTitle("Zeilen nach Quellpfadmuster verwerfen") //
			.useMessage("Zeilen mit Quelldateien mit bestimmten Dateipfaden werden entfernt."), //
			this::runDropEntriesBySourceMatching);

		this.putMenuItem("MS", null, menu -> menu.useTitle("Wiederholen").useMessage("Ersetzt die Pfadtabelle mit der nachfolgenden."), this::runSaveEntriesToVar);

		this.putMenuItem("ML", null, menu -> menu.useTitle("Wiederholen").useMessage("Ersetzt die Pfadtabelle mit der nachfolgenden."),
			this::runLoadEntriesFromVar);

		this.putMenuItem("malsehen", null, menu -> {
			menu.useButton("ja").useButton("nein");
		}, () -> {});

		this.createContents();
	}

	public AppDialog openDialog() {
		var res = new AppDialog(this.getShell());
		this.getDisplay().asyncExec(res::open);
		return res.useFocus(true);
	}

	public void runShowInfo() {
	}

	public void runUndoEntries() {
	}

	public void runRedoEntries() {
	}

	public void runSaveSourcesToClip() { // file
	}

	// tabelle als text in ram puffer
	public void runSaveEntriesToVar() {
	}

	public void runLoadEntriesFromVar() {
	}

	public void runSwapEntriesWithVar() {
	}

	public void runSwapSourcesWithTargets() {
	}

	public void runSortEntriesReverse() {
	}

	public void runSortEntriesBySourcePath() {

	}

	public void runSortEntriesBySourceTime() {
	}

	public void runSortEntriesBySourceSize() {
	}

	public void runDropSources() {
	}

	public void askFilterSourcesByLength() {
		this.openDialog() //
			.useTitle("Eingabepfad nach Dateigröße filtern") //
			.useMessage("Ein Eingabepfad gilt als Treffer, wenn er eine Datei mit einer Dateigröße innerhalb der unten angegebenen Grenzen besitzt."
				+ " Ein Eingabepfad wird verworfen, wenn er ein Verzeichnis angibt.") //
			.useOption("Minimale Dateigröße", this.settings.filterLengthMin) //
			.useOption("Maximale Dateigröße", this.settings.filterLengthMax) //
			.useButton("Treffer erhalten", null) //
			.useButton("Treffer verwerfen", null) //
		;
		// min max dateigröße in MB schritten spinner
		// File f;
		// f.length();
	}

	public void askFilterSourcesByCreation() { //
		this.openDialog() //
			.useTitle("Eingabepfad nach Erzeugungszeitpunkt filtern") //
			.useMessage("Ein Eingabepfad gilt als Treffer, wenn er eine Datei mit einem Erzeugungszeitpunkt innerhalb der unten angegebenen Grenzen besitzt."
				+ " Ein Eingabepfad wird verworfen, wenn er ein Verzeichnis angibt.") //
			.useOption("Frühester Erzeugungszeitpunkt", this.settings.filterCreationMin) //
			.useOption("Spätester Erzeugungszeitpunkt", this.settings.filterCreationMax) //
			.useButton("Treffer erhalten", null) //
			.useButton("Treffer verwerfen", null) //
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
			.useOption("Frühester Änderungszeitpunkt", this.settings.filterModificationMin) //
			.useOption("Spätester Änderungszeitpunkt", this.settings.filterModificationMax) //
			.useButton("Treffer erhalten", null) //
			.useButton("Treffer verwerfen", null) //
		;
		// File f;
		// f.lastModified();
	}

	public void askFilterSourcesByPattern() { //
		this.openDialog() //
			.useTitle("Eingabepfad nach Muster filtern") //
			.useMessage("Ein Eingabepfad gilt als Treffer, wenn den unten angegebenen regulären Ausdruck darin einen Treffer findet.") //
			.useOption("Regulärer Ausdruck", this.settings.filterPattern) //
			.useButton("Treffer erhalten", null) //
			.useButton("Treffer verwerfen", null) //
		;
	}

	public void runDropEntriesBySourceAging() { //

	}

	public void runDropEntriesBySourceExisting() {
	}

	public void runDropEntriesBySourceMatching() { // regex
	}

	public void runDropEntriesBySourceChanging() { //

	}

	public void runKeepEntriesBySourceAging() {
	}

	public void runKeepEntriesBySourceExisting() {
	}

	public void runKeepEntriesBySourceMatching() {
	}

	public void runKeepEntriesBySourceChanging() {
	}

	public void runKeepEntriesBySourceMeasuring() {
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

	private MenuItem createMenu(final Menu parent, final String text, Consumer<MenuItem> setup) {
		final var res = new MenuItem(parent, SWT.CASCADE);
		res.setText(text);
		res.setMenu(new Menu(res));
		setup.set(res);
		return res;
	}

	private MenuItem createMenu(final MenuItem parent, final String text, Consumer<MenuItem> setup) {
		return this.createMenu(parent.getMenu(), text, setup);
	}

	private Menu createMenuBar(final Decorations parent, Consumer<Menu> setup) {
		final var res = new Menu(parent, SWT.BAR);
		parent.setMenuBar(res);
		setup.set(res);
		return res;
	}

	private MenuItem createMenuItem(final Menu parent, final String text, final Runnable onClick) {
		final var res = new MenuItem(parent, SWT.NONE);
		res.setText(text);
		if (onClick != null) {
			res.addListener(SWT.Selection, event -> onClick.run());
		}
		return res;
	}

	private MenuItem createMenuItem(final MenuItem parent, final String text, final Runnable onClick) {
		return this.createMenuItem(parent.getMenu(), text, onClick);
	}

	private MenuItem createMenuBreak(final MenuItem parent) {
		return new MenuItem(parent.getMenu(), SWT.SEPARATOR);
	}

}
