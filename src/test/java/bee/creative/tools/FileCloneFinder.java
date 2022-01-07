package bee.creative.tools;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import bee.creative.fem.FEMBinary;
import bee.creative.lang.Objects;
import bee.creative.lang.Strings;
import bee.creative.util.HashMap;
import bee.creative.util.HashSet;

/** Diese Applikation ermittelt aus allen gewählten Dateien die jenigen, die in den ersten X Byte gleich sind und gruppiert diese. <pre> einstellbar sind -
 * größe des abzugleichenden dateikopfes -
 *
 * @author [cc-by] 2022 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FileCloneFinder extends FileCloneFinderWindow {

	static class Item {

		static final Pattern LINE_BREAK = Pattern.compile("[\r\n]+");

		static final Pattern PAIR_BREAK = Pattern.compile(">|\t");

		String targetPath;

		String sourcePath;

		Long sourceSize;

		Object sourceHash;

		Object sourceData;

		Item previousItem;

		Item(final String sourcePath) {
			this.sourcePath = sourcePath;
		}

		void computeSize() {
			try {
				this.sourceSize = new Long(new File(this.sourcePath).length());
			} catch (final Exception error) {
				error.printStackTrace();
				this.sourceSize = null;
			}
		}

		void computeHash(final long hashSize, final ByteBuffer hashBuffer, final MessageDigest hashBuilder) {
			try (FileChannel channel = Data.openChannel(this.sourcePath)) {
				hashBuilder.reset();
				final int bufSize = hashBuffer.capacity();
				for (long remSize = hashSize; remSize > 0; remSize -= bufSize) {
					final int remLimit = (int)Math.min(remSize, bufSize);
					hashBuffer.limit(remLimit).position(0);
					final boolean last = Data.readChannel(channel, hashBuffer);
					hashBuffer.limit(hashBuffer.position()).position(0);
					hashBuilder.update(hashBuffer);
					if (last) {
						break;
					}
				}
				this.sourceHash = FEMBinary.from(hashBuilder.digest()).toString(false);
			} catch (final Exception error) {
				error.printStackTrace();
				this.sourceHash = this;
			}
		}

		void computeData(final long dataSize) {
			try {
				final Data fileData = new Data();
				fileData.dataPath = this.sourcePath;
				fileData.dataSize = dataSize;
				this.sourceData = fileData;
			} catch (final Exception error) {
				error.printStackTrace();
				this.sourceData = this;
			}
		}

		void computePath(final String pathLabel, final int pathIndex) {
			File file = new File(this.sourcePath);
			final List<File> parts = new LinkedList<>();
			while (file != null) {
				parts.add(0, file);
				file = file.getParentFile();
			}
			final int size = parts.size();
			int i = pathIndex < 0 ? Math.max(0, size + pathIndex + -1) : Math.min(pathIndex, size - 2);
			file = new File(parts.get(i), pathLabel);
			for (i++; i < size; i++) {
				file = new File(file, parts.get(i).getName());
			}
			this.targetPath = file.getPath();
		}

		@Override
		public String toString() {
			return this.sourcePath;
		}

	}

	static class Data {

		static final int BUFFER_SIZE = 1024 * 1024 * 10;

		static final ByteBuffer BUFFER_THIS = ByteBuffer.allocateDirect(Data.BUFFER_SIZE);

		static final ByteBuffer BUFFER_THAT = ByteBuffer.allocateDirect(Data.BUFFER_SIZE);

		/** Diese Methode füllt den gegebenen Puffer target mit den Daten aus dem Datenkanal und liefert nutr dann true, wenn dabei das Ende des Datenkanals
		 * erreicht wurde. */
		static boolean readChannel(final FileChannel source, final ByteBuffer target) throws IOException {
			while (target.remaining() != 0) {
				if (source.read(target) < 0) return true;
			}
			return false;
		}

		static FileChannel openChannel(final String filepath) throws IOException {
			return FileChannel.open(new File(filepath).toPath(), StandardOpenOption.READ);
		}

		String dataPath;

		long dataSize;

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Data)) return false;
			final Data that = (Data)object;
			if (Objects.equals(this.dataPath, that.dataPath)) return true;
			if (this.dataSize != that.dataSize) return true;
			try (FileChannel thisChannel = Data.openChannel(this.dataPath)) {
				try (FileChannel thatChannel = Data.openChannel(that.dataPath)) {
					final int bufSize = Data.BUFFER_SIZE;
					final ByteBuffer thisBuffer = Data.BUFFER_THIS;
					final ByteBuffer thatBuffer = Data.BUFFER_THAT;
					for (long remSize = this.dataSize; remSize > 0; remSize -= bufSize) {
						final int remLimit = (int)Math.min(remSize, bufSize);
						thisBuffer.limit(remLimit).position(0);
						thatBuffer.limit(remLimit).position(0);
						final boolean thisLast = Data.readChannel(thisChannel, thisBuffer);
						final boolean thatLast = Data.readChannel(thatChannel, thatBuffer);
						if (thisLast != thatLast) return false;
						thisBuffer.limit(thisBuffer.position()).position(0);
						thatBuffer.limit(thatBuffer.position()).position(0);
						if (!thisBuffer.equals(thatBuffer)) return false;
					}
					return true;
				}
			} catch (final Exception error) {
				error.printStackTrace();
				return false;
			}
		}

	}

	@XmlRootElement (name = "file-clone-finder-options")
	static class Store {

		static final File OPTION_FILE = new File("options").getAbsoluteFile();

		@XmlAttribute
		long hashSize;

		@XmlAttribute
		long testSize;

		@XmlAttribute
		String pathLabel;

		@XmlAttribute
		int pathIndex;

	}

	public static void main(final String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new FileCloneFinder();
	}

	final Timer processTimer;

	int maxHashSize = 1024 * 1024 * 2; // 2 MB

	int maxDataSize = 1024 * 1024 * 100; // 100 MB

	FileCloneFinder() throws Exception {

		new DropTarget(this.sourceList, new DropTargetAdapter() {

			@Override
			public void drop(final DropTargetDropEvent event) {
				FileCloneFinder.this.performPaste(event);
			}

		});
		final Action action = this.sourceList.getActionMap().get("paste-from-clipboard");
		this.sourceList.getActionMap().put("paste-from-clipboard", new AbstractAction() {

			private static final long serialVersionUID = 7198411113139330345L;

			@Override
			public void actionPerformed(final ActionEvent event) {
				FileCloneFinder.this.performPaste(event, action);
			}

		});

		this.scanButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				FileCloneFinder.this.performScan(event);
			}

		});
		this.testButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				FileCloneFinder.this.performTest(event);
			}

		});
		this.moveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				FileCloneFinder.this.performMove(event);
			}

		});
		this.processTimer = new Timer(500, new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				FileCloneFinder.this.performProcess(event);
			}

		});
		this.processTimer.start();

		this.frame.setSize(800, 400);
		this.frame.setLocationRelativeTo(null);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setVisible(true);
	}

	@Override
	void initialize() {
		super.initialize();
		this.performRestore();
	}

	Object processEntry;

	int processCount;

	boolean processActive;

	synchronized boolean getProcessActive() {
		return this.processActive;
	}

	/** Diese Methode setzt {@link #processActive} und liefert dessen vorherigen Wert. */
	synchronized boolean setProcessActive(final boolean value) {
		final FileCloneFinder that = this;
		if (that.processActive == value) return value;
		that.processEntry = null;
		that.processCount = 0;
		that.processActive = value;
		final boolean enabled = !value;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (final JComponent item: new JComponent[]{ //
					that.sourceList, that.targetList, that.hashSize, that.testSize, that.pathLabel, //
					that.pathIndex, that.scanButton, that.testButton, that.moveButton}) {
					item.setEnabled(enabled);
				}
			}

		});
		return enabled;
	}

	void performAsync(final Runnable task) {
		final Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	boolean performPaste(final Transferable trans) throws UnsupportedFlavorException, IOException {
		if (trans == null) return false;
		if (!trans.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return false;
		if (!this.sourceList.isEnabled()) return true;
		@SuppressWarnings ("unchecked")
		final List<File> files = (List<File>)trans.getTransferData(DataFlavor.javaFileListFlavor);
		final List<String> sourceList = new ArrayList<>();
		final String source = this.sourceList.getText();
		if (!source.isEmpty()) {
			sourceList.add(source);
		}
		for (final File file: files) {
			sourceList.add(file.getPath());
		}
		this.sourceList.setText(Strings.join("\n", sourceList));
		return true;
	}

	void performPaste(final DropTargetDropEvent event) {
		try {
			event.acceptDrop(DnDConstants.ACTION_COPY);
			FileCloneFinder.this.performPaste(event.getTransferable());
		} catch (final Exception error) {
			error.printStackTrace();
		}
	}

	void performPaste(final ActionEvent event, final Action action) {
		try {
			if (FileCloneFinder.this.performPaste(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null))) return;
			action.actionPerformed(event);
		} catch (final Exception error) {
			error.printStackTrace();
		}
	}

	void performScan(final ActionEvent event) {
		final FileCloneFinder that = this;
		final String stringListValue = that.sourceList.getText();

		this.performAsync(new Runnable() {

			@Override
			public void run() {
				if (that.setProcessActive(true)) return;
				try {

					final List<String> stringList = Strings.split(Item.LINE_BREAK, stringListValue);
					that.processCount = stringList.size();

					final List<File> sourceStack = new LinkedList<>();
					for (final String string: stringList) {
						that.processEntry = string;
						final File file = new File(string.trim());
						if (file.isAbsolute()) {
							sourceStack.add(file.getAbsoluteFile());
						} else {
							that.processCount--;
						}
					}

					final Set<String> resultSet = new HashSet<>();
					final List<String> resultList = new ArrayList<>();
					while (!sourceStack.isEmpty()) {
						final File source = sourceStack.remove(0);
						that.processEntry = source;
						if (source.isDirectory()) {
							final File[] fileArray = source.listFiles();
							if (fileArray != null) {
								sourceStack.addAll(0, Arrays.asList(fileArray));
								that.processCount += fileArray.length;
							}
						} else if (source.isFile()) {
							final String result = source.getAbsolutePath();
							if (resultSet.add(result)) {
								resultList.add(result);
							}
						}
						that.processCount--;
					}

					final String sourceListValue2 = Strings.join("\n", resultList);

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							that.sourceList.setText(sourceListValue2);
						}

					});

				} finally {
					that.setProcessActive(false);
				}

			}

		});
	}

	void performTest(final ActionEvent event) {
		final FileCloneFinder that = FileCloneFinder.this;
		final String stringListValue = that.sourceList.getText();
		final long hashSizeValue = (Long)that.hashSize.getValue();
		final long testSizeValue = (Long)that.testSize.getValue();
		final String pathLabelValue = that.pathLabel.getText();
		final int pathIndexValue = (Integer)that.pathIndex.getValue();

		this.performAsync(new Runnable() {

			@Override
			public void run() {
				if (that.setProcessActive(true)) return;
				try {
					final List<String> stringList = Strings.split(Item.LINE_BREAK, stringListValue);
					that.processCount = stringList.size() + 1;

					final Set<String> pathSet = new HashSet<>(that.processCount);
					final List<Item> itemList = new LinkedList<>();
					for (final String string: stringList) {
						that.processEntry = string;
						that.processCount--;
						final File file = new File(string).getAbsoluteFile();
						if (file.isFile()) {
							final String path = file.getPath();
							if (pathSet.add(path)) {
								itemList.add(0, new Item(path));
								that.processCount++;
							}
						}
					}
					pathSet.clear();

					final HashMap<Object, Item> sizeListMap = new HashMap<>();
					for (final Item item: itemList) { // rückwärts
						that.processEntry = item;
						that.processCount--;

						item.computeSize();

						item.previousItem = sizeListMap.put(item.sourceSize, item);
						if (item.previousItem != null) {
							that.processCount++;
						}
					}
					sizeListMap.remove(null);
					itemList.clear();

					final HashMap<Object, Item> hashListMap = new HashMap<>();
					final HashMap<Object, Item> itemDataListMap = new HashMap<>();
					final LinkedList<Item> targetList = new LinkedList<>();
					final ByteBuffer hashBuffer = Data.BUFFER_THIS;
					final MessageDigest hashBuilder = MessageDigest.getInstance("SHA-256");

					for (final Item sizeList: sizeListMap.values()) {
						if (sizeList.previousItem != null) {
							that.processCount++;
							hashListMap.clear();
							final long testSize = Math.min(sizeList.sourceSize.longValue(), testSizeValue);
							for (Item item = sizeList, next; item != null; item = next) { // vorwärts
								that.processEntry = item;
								that.processCount--;
								next = item.previousItem;

								item.computeHash(hashSizeValue, hashBuffer, hashBuilder);

								item.previousItem = hashListMap.put(item.sourceHash, item);
								if (item.previousItem != null) {
									that.processCount++;
								}
							}

							for (final Item hashList: hashListMap.values()) {
								if (hashList.previousItem != null) {
									that.processCount++;

									itemDataListMap.clear();
									for (Item item = hashList, prev; item != null; item = prev) { // rückwärts
										prev = item.previousItem;

										item.computeData(testSize);

										item.previousItem = itemDataListMap.put(item.sourceData, item);
										if (item.previousItem != null) {
											that.processCount++;
										}
									}
									for (final Item dataList: itemDataListMap.values()) {
										if (dataList.previousItem != null) {
											targetList.add(dataList);
										}
									}

								}
							}

						}
					}

					final StringBuilder targetBuilder = new StringBuilder();
					for (final Item dataList: targetList) {
						that.processEntry = dataList;
						that.processCount--;
						targetBuilder.append(dataList.sourcePath).append("\n").append("  # ").append(dataList.sourceHash).append(" @ ").append(dataList.sourceSize)
							.append("\n");
						for (Item item = dataList.previousItem; item != null; item = item.previousItem) { // vorwärts
							item.computePath(pathLabelValue, pathIndexValue);
							targetBuilder.append("  ").append(item.sourcePath).append(" > ").append(item.targetPath).append("\n");
						}
					}

					final String targetTextValue = targetBuilder.toString();
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							that.targetList.setText(targetTextValue);
							FileCloneFinder.this.performPersist();
						}

					});

				} catch (final Exception error) {
					error.printStackTrace();
				} finally {
					that.setProcessActive(false);
				}
			}

		});
	}

	void performMove(final ActionEvent event) {
		final FileCloneFinder that = FileCloneFinder.this;
		final String sourceListValue = that.targetList.getText();
		this.performAsync(new Runnable() {

			@Override
			public void run() {
				if (that.setProcessActive(true)) return;
				try {
					final List<String> stringList = Strings.split(Item.LINE_BREAK, sourceListValue);
					that.processCount = stringList.size();

					final Map<String, String> targetMap = new HashMap<>(that.processCount);
					for (final String string: stringList) {
						that.processEntry = string;
						that.processCount--;
						final List<String> entry = Strings.split(Item.PAIR_BREAK, string.trim());
						if (entry.size() == 2) {
							final String sourcePath = entry.get(0).trim();
							final String targetPath = entry.get(1).trim();
							if (!sourcePath.isEmpty() && !targetPath.isEmpty()) {
								targetMap.put(sourcePath, targetPath);
								that.processCount++;
							}
						}
					}
					for (final Entry<String, String> entry: targetMap.entrySet()) {
						final File sourcePath = new File(entry.getKey());
						final File targetPath = new File(entry.getValue());
						FileCloneFinder.this.processEntry = sourcePath;
						that.processCount--;
						if (sourcePath.isFile() && sourcePath.isAbsolute() && targetPath.isAbsolute()) {
							final File parent = targetPath.getParentFile();
							if (parent != null) {
								parent.mkdirs();
							}
							Files.move(sourcePath.toPath(), targetPath.toPath());
						}
					}

				} catch (final Exception error) {
					error.printStackTrace();
				} finally {
					that.setProcessActive(false);
				}
			}

		});

	}

	void performPersist() {
		try {
			final Store store = new Store();
			store.hashSize = (Long)this.hashSize.getValue();
			store.testSize = (Long)this.testSize.getValue();
			store.pathLabel = this.pathLabel.getText();
			store.pathIndex = (Integer)this.pathIndex.getValue();
			JAXB.marshal(store, Store.OPTION_FILE);
		} catch (final Exception error) {
			error.printStackTrace();
		}
	}

	void performRestore() {
		try {
			final Store store = JAXB.unmarshal(Store.OPTION_FILE, Store.class);
			this.hashSize.setValue(store.hashSize);
			this.testSize.setValue(store.testSize);
			this.pathLabel.setText(store.pathLabel);
			this.pathIndex.setValue(store.pathIndex);
		} catch (final Exception error) {
			error.printStackTrace();
		}
	}

	synchronized void performProcess(final ActionEvent event) {
		if (this.processActive) {
			this.progressInfo.setText(this.processCount + " - " + this.processEntry);
		} else {
			this.progressInfo.setText(" ");
		}
	}

}
