package bee.creative.tools;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.UIManager;
import bee.creative.fem.FEMBinary;
import bee.creative.lang.Strings;

/** Diese Applikation ermittelt aus allen gewählten Dateien die jenigen, die in den ersten X Byte gleich sind und gruppiert diese. <pre> einstellbar sind -
 * größe des abzugleichenden dateikopfes -
 *
 * @author [cc-by] 2022 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FileCloneFinder {

	static class Item {

		String filePath;

		Object fileSize;

		Object fileHash;

		Object fileData;

		Item prevItem;

	}

	public static void main(final String[] args) throws Exception {
		new FileCloneFinder();
	}

	final Timer progressTimer;

	final HashMap<Object, Item> itemPathListMap = new HashMap<>();

	final HashMap<Object, Item> itemSizeListMap = new HashMap<>();

	final HashMap<Object, Item> itemHashListMap = new HashMap<>();

	final HashMap<Object, Item> itemDataListMap = new HashMap<>();

	final LinkedList<Item> sourceList = new LinkedList<>();

	final LinkedList<Item> targetList = new LinkedList<>();

	final ByteBuffer hashBuffer;

	final MessageDigest hashBuilder;

	final FileCloneFinderView view;

	int maxHashSize = 1024 * 1024 * 2; // 2 MB

	int maxDataSize = 1024 * 1024 * 100; // 100 MB

	private boolean active;

	private int progressCount;

	private int progressIndex;

	FileCloneFinder() throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		this.view = new FileCloneFinderView();

		this.view.sourceText.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(final KeyEvent event) {
				try {
					FileCloneFinder.this.addSourceText(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null));
				} catch (final Exception error) {
					error.printStackTrace();
				}
			}

		});

		new DropTarget(this.view.sourceText, new DropTargetAdapter() {

			@Override
			public void drop(final DropTargetDropEvent event) {
				try {
					event.acceptDrop(DnDConstants.ACTION_COPY);
					FileCloneFinder.this.addSourceText(event.getTransferable());
				} catch (final Exception error) {
					error.printStackTrace();
				}
			}

		});

		this.hashBuffer = ByteBuffer.allocate(this.maxHashSize);
		this.hashBuilder = MessageDigest.getInstance("SHA-256");

		this.view.frame.setSize(400, 400);

		this.progressTimer = new Timer(500, new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FileCloneFinder.this.handleUpdateStatus(e);
			}

		});
		this.progressTimer.start();

		this.view.testButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				putTarget(false);
			}
		});
		this.view.moveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				putTarget(true);
			}
		});

		this.view.frame.setVisible(true);

		// DEMO

		// this.putFile();
		// this.appendFileTask(new File("D:\\projects\\java\\bee-creative"));
		// this.appendFileTask(new File("F:\\2021-12-26_NS"));
		// this.appendFileTask(new File("F:\\2021-12-26_NS"));
	}

	synchronized String getSourceText() {
		return this.view.sourceText.getText();
	}

	synchronized void addSourceText(final Transferable trans) {
		try {
			if (trans == null) return;
			if (!trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return;
			if (!this.view.sourceText.isEnabled()) return;
			final List<?> files = (List<?>)trans.getTransferData(DataFlavor.javaFileListFlavor);
			final StringBuilder source = new StringBuilder(this.getSourceText());
			for (final Object file: files) {
				(source.length() != 0 ? source.append("\n") : source).append(((File)file).getAbsolutePath());
			}
			this.view.sourceText.setText(source.toString());
		} catch (final Exception error) {
			error.printStackTrace();
		}
	}

	void addTargetFile(final File path) {
		if (path.isDirectory()) {
			final File[] files = path.listFiles();
			if (files == null) return;
			for (final File file: files) {
				this.addTargetFile(file);
			}
		} else if (path.isFile()) {
			final Item item = new Item();
			item.filePath = path.getAbsolutePath();
			this.targetList.addLast(item);
		}
	}

	synchronized void setActive(final boolean value) {
		this.active = value;
		for (final JComponent item: new JComponent[]{this.view.sourceText, this.view.targetText, this.view.hashSizeText, this.view.testSizeText,
			this.view.testSizeText, this.view.testButton, this.view.moveButton}) {
			item.setEnabled(!this.active);
		}
	}

	void putTargetAsync(final boolean move) {
		Thread t = new Thread() {

			@Override
			public void run() {
				FileCloneFinder.this.putTarget(move);
			}

		};
		t.setDaemon(true);
		t.start();
	}

	public synchronized boolean isActive() {
		return this.active;
	}

	void putTarget(final boolean move) {
		if (this.isActive()) return;
		try {
			this.setActive(true);

			// Eingabetext parsen
			this.progressIndex = 0;
			final List<String> paths = Strings.split(Pattern.compile("[\r\n]+"), this.getSourceText());
			this.progressCount = paths.size();

			// Eingabedaten vervollständigen
			this.targetList.clear();
			for (final String path: paths) {
				this.addTargetFile(new File(path).getAbsoluteFile());
				this.progressIndex++;
			}
			this.progressCount += this.targetList.size();
			this.progressIndex++;

			// einzigartige Dateinamen behalten
			this.sourceList.clear();
			this.itemPathListMap.clear();
			for (final Item item: this.targetList) {
				item.prevItem = this.itemPathListMap.put(item.filePath, item);
				if (item.prevItem == null) {
					this.sourceList.addFirst(item);
					this.progressCount++;
				}
				this.progressIndex++;
			}
			this.targetList.clear();
			this.itemPathListMap.clear();

			// Eingabedateien nach Größe partitionieren
			this.itemSizeListMap.clear();
			for (final Item item: this.sourceList) { // rückwärts
				item.fileSize = this.getItemSize(item);
				item.prevItem = this.itemSizeListMap.put(item.fileSize, item);
				if (item.prevItem != null) {
					this.progressCount++;
				}
				this.progressIndex++;
			}
			this.sourceList.clear();

			// Jede Größenpartition nach Streuwert partitionieren
			for (final Item sizeList: this.itemSizeListMap.values()) {
				if (sizeList.prevItem != null) {

					this.itemHashListMap.clear();
					for (Item item = sizeList, next; item != null; item = next) { // vorwärts
						next = item.prevItem;
						item.fileHash = this.getItemHash(item);
						item.prevItem = this.itemHashListMap.put(item.fileHash, item);
						if (item.prevItem != null) {
							this.progressCount++;
						}
					}
					this.progressIndex++;

					for (final Item hashList: this.itemHashListMap.values()) {
						if (hashList.prevItem != null) {

							// TODO dateiinhaltsverleich
							// targetlist füllen mit den gruppen
							this.itemDataListMap.clear();
							for (Item item = hashList, prev; item != null; item = prev) { // rückwärts
								prev = item.prevItem;
								item.fileData = this.getItemData(item);
								item.prevItem = this.itemDataListMap.put(item.fileData, item);
								if (item.prevItem != null) {
									this.progressCount++;
								}
							}
							for (final Item dataList: this.itemDataListMap.values()) {
								if (dataList.prevItem != null) {
									this.targetList.add(dataList);
									this.progressCount++;
									this.progressIndex++;
								}
							}

							this.progressIndex++;

						}
					}

				}
			}

			final StringBuilder target = new StringBuilder();
			for (final Item dataList: this.targetList) {
				for (Item item = dataList; item != null; item = item.prevItem) { // vorwärts
					target.append(item.filePath).append("\n");
				}
				this.progressIndex++;
				target.append("---\n");
			}
			this.view.targetText.setText(target.toString());

			// TODO move

		} catch (final Exception error) {
			error.printStackTrace();
		} finally {
			this.setActive(false);
			this.sourceList.clear();
			this.itemPathListMap.clear();
			this.itemSizeListMap.clear();
			this.itemHashListMap.clear();
			this.itemDataListMap.clear();
		}
	}

	private Object getItemData(final Item item) {
		// TODO wrapper für abgleich von dateiinhalt
		return null;
	}

	Long getItemSize(final Item item) {
		return new Long(new File(item.filePath).length());
	}

	Object getItemHash(final Item item) {
		try (FileInputStream fis = new FileInputStream(item.filePath)) {
			this.hashBuffer.limit(this.hashBuffer.capacity()).position(0);
			fis.getChannel().read(this.hashBuffer);
			this.hashBuffer.limit(this.hashBuffer.position()).position(0);
			this.hashBuilder.reset();
			this.hashBuilder.update(this.hashBuffer.slice());
			return FEMBinary.from(this.hashBuilder.digest());
		} catch (final Exception error) {
			error.printStackTrace();
			return item;
		}
	}

	void handleUpdateStatus(final ActionEvent e) {
		this.view.progressInfo.setText(String.format("Tasks: %s / %s", this.progressIndex, this.progressCount));
	}

}
