package bee.creative.app.ft;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.UIManager;
import bee.creative.fem.FEMDatetime;
import bee.creative.lang.Objects;
import bee.creative.lang.Strings;
import bee.creative.util.Consumer;
import bee.creative.util.HashMap2;
import bee.creative.util.HashSet2;
import bee.creative.util.Iterables;

/** @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FTMain extends FTWindow_SWT {

	public static void main(final String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new FTMain();
	}

	public FTMain() {
		this.run();
	}

	private HashSet2<String> createPathSet() {
		return new HashSet2<>();
	}

	static final Pattern lineText = Pattern.compile("[^\r\n]+");

	static final Pattern itemText = Pattern.compile("[^\t]+");

	private List<List<String>> createLineList() {
		return new ArrayList<>();
	}

	private List<List<String>> createLineList(final String text) {
		final var linelist = this.createLineList();
		this.runItems(Strings.match(FTMain.lineText, text), //
			line -> linelist.add(Strings.match(FTMain.itemText, line)), //
			line -> linelist.add(Collections.singletonList(line)));
		return linelist;
	}

	private String createLineText(final Iterable<? extends Iterable<?>> lines) {
		final var res = new StringBuilder(1024 * 1024 * 16);
		Strings.join(res, "\n", lines, (res2, line) -> Strings.join(res2, "\t", line));
		return res.toString();
	}

	private <GItem> void runItems(final Collection<GItem> items, final Consumer<GItem> regular, final Consumer<GItem> canceled) {
		this.taskCount += items.size();
		final var iter = items.iterator();
		if (regular != null) {
			while (iter.hasNext() && !this.isTaskCanceled) {
				regular.set(iter.next());
				this.taskCount--;
			}
		}
		if (canceled != null) {
			while (iter.hasNext()) {
				canceled.set(iter.next());
				this.taskCount--;
			}
		}
	}

	@Override
	public void importInputsRequest(final String inputText, final List<File> fileList) {
		final var tableList = this.createLineList();
		if (!inputText.isEmpty()) {
			tableList.add(Collections.singletonList(inputText));
		}
		this.runItems(fileList, file -> tableList.add(Collections.singletonList(file.getPath())), null);
		this.importInputsRespond(this.createLineText(tableList));
	}

	private int cleanupImpl(final String tableText, final List<List<String>> keepList, final int itemCol, final boolean isExisting) { // DONE
		final var lineList = this.createLineList(tableText);
		this.runItems(lineList, line -> {
			if (line.size() <= itemCol) return;
			final var file = new File(line.get(itemCol));
			if (!file.isAbsolute() || (file.exists() == isExisting)) return;
			this.taskEntry = file.getPath();
			keepList.add(line);
		}, keepList::add);
		final int dropCount = lineList.size() - keepList.size();
		return dropCount;
	}

	@Override
	public void cleanupExistingInputsRequest(final String inputText) {
		final var keepList = this.createLineList();
		final var dropCount = this.cleanupImpl(inputText, keepList, 0, true);
		this.cleanupExistingInputsRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void cleanupExistingSourcesRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.cleanupImpl(tableText, keepList, 0, true);
		this.cleanupExistingSourcesRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void cleanupExistingTargetsRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.cleanupImpl(tableText, keepList, 1, true);
		this.cleanupExistingTargetsRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void cleanupMissingInputsRequest(final String inputText) {
		final var keepList = this.createLineList();
		final var dropCount = this.cleanupImpl(inputText, keepList, 0, false);
		this.cleanupMissingInputsRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void cleanupMissingSourcesRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.cleanupImpl(tableText, keepList, 0, false);
		this.cleanupMissingSourcesRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void cleanupMissingTargetsRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.cleanupImpl(tableText, keepList, 1, false);
		this.cleanupMissingTargetsRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	private int deleteImpl(final String tableText, final List<List<String>> keepList, final int itemCol, final boolean isFolder, final boolean isRecycle) { // DONE
		final var pathSet = this.createPathSet();
		final var lineList = this.createLineList(tableText);
		this.runItems(lineList, line -> {
			if (line.size() <= itemCol) return;
			final var file = new File(line.get(itemCol));
			if (!file.isAbsolute()) return;
			final var path = file.getPath();
			this.taskEntry = path;
			pathSet.add(file.getPath());
		}, null);
		this.taskEntry = null;
		final var desktop = Desktop.getDesktop();
		final var pathList = new ArrayList<>(pathSet);
		pathSet.clear();
		pathList.sort((a, b) -> a.length() - b.length());
		this.runItems(pathList, path -> {
			try {
				final var file = new File(path);
				if (isFolder) {
					if (!file.isDirectory()) return;
					final var files = file.list();
					if ((files != null) && (files.length != 0)) return;
				} else {
					if (!file.isFile()) return;
				}
				if (isRecycle) {
					if (!desktop.moveToTrash(file)) return;
				} else {
					if (!file.delete()) return;
				}
				this.taskEntry = path;
				pathSet.add(path); // gelöschte dateien
			} catch (final Exception ignore) {}
		}, null);
		this.taskEntry = null;
		this.runItems(lineList, line -> {
			if (line.size() > itemCol) {
				final String path = line.get(itemCol);
				this.taskEntry = path;
				if (pathSet.contains(path)) return;
			}
			keepList.add(line);
		}, keepList::add);
		final int dropCount = pathSet.size();
		return dropCount;
	}

	@Override
	public void deleteInputFilesTemporaryRequest(final String inputText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(inputText, keepList, 0, false, true);
		this.deleteInputFilesTemporaryRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteInputFilesPermanentlyRequest(final String inputText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(inputText, keepList, 0, false, false);
		this.deleteInputFilesPermanentlyRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteInputFoldersTemporaryRequest(final String inputText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(inputText, keepList, 0, true, true);
		this.deleteInputFoldersTemporaryRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteInputFoldersPermanentlyRequest(final String inputText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(inputText, keepList, 0, true, false);
		this.deleteInputFoldersPermanentlyRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteSourceFilesTemporaryRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(tableText, keepList, 0, false, true);
		this.deleteSourceFilesTemporaryRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteSourceFilesPermanentlyRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(tableText, keepList, 0, false, false);
		this.deleteSourceFilesPermanentlyRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteSourceFoldersTemporaryRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(tableText, keepList, 0, true, true);
		this.deleteSourceFoldersTemporaryRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteSourceFoldersPermanentlyRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(tableText, keepList, 0, true, false);
		this.deleteSourceFoldersPermanentlyRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteTargetFilesTemporaryRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(tableText, keepList, 1, false, true);
		this.deleteTargetFilesTemporaryRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteTargetFilesPermanentlyRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(tableText, keepList, 1, false, false);
		this.deleteTargetFilesPermanentlyRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteTargetFoldersTemporaryRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(tableText, keepList, 1, true, true);
		this.deleteTargetFoldersTemporaryRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void deleteTargetFoldersPermanentlyRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.deleteImpl(tableText, keepList, 1, true, false);
		this.deleteTargetFoldersPermanentlyRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	private List<File> exportImpl(final String tableText, final int itemCol) { // DONE
		final var pathSet = this.createPathSet();
		final var fileList = new ArrayList<File>();
		this.runItems(this.createLineList(tableText), line -> {
			if (line.size() <= itemCol) return;
			final var file = new File(line.get(itemCol));
			if (!file.isAbsolute()) return;
			final var path = file.getPath();
			this.taskEntry = path;
			if (!pathSet.add(path)) return;
			fileList.add(file);
		}, null);
		return fileList;
	}

	@Override
	public void exportInputsRequest(final String inputText) {
		final var fileList = this.exportImpl(inputText, 0);
		this.exportInputsRespond(fileList);
	}

	@Override
	public void exportSourcesRequest(final String tableText) {
		final var fileList = this.exportImpl(tableText, 0);
		this.exportSourcesRespond(fileList);
	}

	@Override
	public void exportTargetsRequest(final String tableText) {
		final var fileList = this.exportImpl(tableText, 1);
		this.exportTargetsRespond(fileList);
	}

	private int exchangeImpl(final String sourceText, final List<List<String>> keepList) { // DONE
		final var lineList = this.createLineList(sourceText);
		final var failCount = new int[]{lineList.size()};
		this.runItems(lineList, line -> {
			keepList.add(line);
			if (line.size() < 2) return;
			failCount[0]--;
			this.taskEntry = line.set(1, line.set(0, line.get(1)));
		}, keepList::add);
		return failCount[0];
	}

	@Override
	public void exchangeSourcesWithTargetsRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var failCount = this.exchangeImpl(tableText, keepList);
		this.exchangeSourcesWithTargetsRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	@Override
	public void exchangeTargetsWithSourcesRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var failCount = this.exchangeImpl(tableText, keepList);
		this.exchangeTargetsWithSourcesRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	private int replaceImpl(final String tableText, final List<List<String>> keepList, final int sourceCol, final int targetCol) { // DONE
		final var lineList = this.createLineList(tableText);
		final var failCount = new int[]{lineList.size()};
		this.runItems(lineList, line -> {
			keepList.add(line);
			if (line.size() < 2) return;
			failCount[0]--;
			this.taskEntry = line.set(targetCol, line.get(sourceCol));
		}, keepList::add);
		return failCount[0];
	}

	@Override
	public void replaceSourcesWithTargetsRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var failCount = this.replaceImpl(tableText, keepList, 1, 0);
		this.replaceSourcesWithTargetsRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	@Override
	public void replaceTargetsWithSourcesRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var failCount = this.replaceImpl(tableText, keepList, 0, 1);
		this.replaceTargetsWithSourcesRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	private int transferImpl(final String tableText, final List<List<String>> keepList, final int itemCol, final int itemCount) { // DONE
		final var pathSet = this.createPathSet();
		final var lineList = this.createLineList(tableText);
		final var failCount = new int[]{lineList.size()};
		this.runItems(lineList, line -> {
			if (line.size() <= itemCol) return;
			final var file = new File(line.get(itemCol));
			if (!file.isAbsolute()) return;
			failCount[0]--;
			final var path = file.getPath();
			if (!pathSet.add(path)) return;
			this.taskEntry = path;
			keepList.add(Collections.nCopies(itemCount, path));
		}, null);
		return failCount[0];
	}

	@Override
	public void transferInputsRequest(final String inputText) {
		final var keepList = this.createLineList();
		final var failCount = this.transferImpl(inputText, keepList, 0, 2);
		this.transferInputsRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	@Override
	public void transferSourcesRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var failCount = this.transferImpl(tableText, keepList, 0, 1);
		this.transferSourcesRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	@Override
	public void transferTargetsRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var failCount = this.transferImpl(tableText, keepList, 1, 1);
		this.transferTargetsRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	private int resolveImpl(final String sourceText, final List<List<String>> keepList, final boolean isFolder) { // DONE
		final var pathSet = this.createPathSet();
		final var lineList = this.createLineList(sourceText);
		final var fileStack = new LinkedList<File>();
		this.runItems(lineList, line -> {
			if (line.size() < 1) return;
			final var file = new File(line.get(0));
			fileStack.add(file);
			if (!file.isAbsolute()) return;
			this.taskEntry = file;
			this.taskCount++;
		}, keepList::add);
		var dropCount = lineList.size() - fileStack.size();
		while (!fileStack.isEmpty() && !this.isTaskCanceled) {
			final var file = fileStack.remove(0);
			final var path = file.getPath();
			this.taskEntry = path;
			if (pathSet.add(path)) {
				if (file.isDirectory()) {
					final var files = file.listFiles();
					if ((files != null) && (files.length != 0)) {
						fileStack.addAll(0, Arrays.asList(files));
						this.taskCount += files.length;
					}
					if (isFolder) {
						keepList.add(Collections.singletonList(path));
					}
				} else if (!isFolder) {
					if (file.isFile()) {
						keepList.add(Collections.singletonList(path));
					}
				}
			} else {
				dropCount++;
			}
			this.taskCount--;
		}
		keepList.addAll(0, Iterables.translate(fileStack, file -> Collections.singletonList(file.getPath())).toList());
		return dropCount;
	}

	@Override
	public void resolveInputToFilesRequest(final String inputText) {
		final var keepList = this.createLineList();
		final int dropCount = this.resolveImpl(inputText, keepList, false);
		this.resolveInputToFilesRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void resolveInputToFoldersRequest(final String inputText) {
		final var targetList = this.createLineList();
		final var dropCount = this.resolveImpl(inputText, targetList, true);
		this.resolveInputToFoldersRespond(this.createLineText(targetList), targetList.size(), dropCount);
	}

	@Override
	public void refreshInputFilesRequest(final String inputText, final long copyTime) {
		final var keepList = this.createLineList();
		final var baseList = this.createLineList(inputText);
		final var filterTime = FileTime.fromMillis(System.currentTimeMillis() - (copyTime * 24 * 60 * 60 * 1000));
		this.runItems(baseList, line -> {
			if (line.size() > 0) {
				try {
					final var file = new File(line.get(0));
					if (file.isAbsolute()) {
						this.taskEntry = file;
						final var path = file.toPath();
						final var attr = Files.readAttributes(path, BasicFileAttributes.class);
						if (attr.isRegularFile() && (attr.creationTime().compareTo(filterTime) < 0)) {
							final var file2 = new File(file.getParentFile(), file.getName() + ".tempcopy");
							final Path path2 = file2.toPath();
							Files.copy(path, path2, StandardCopyOption.COPY_ATTRIBUTES);
							Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
							return;
						}
					}
				} catch (final Exception ignored) {}
			}
			keepList.add(line);
		}, keepList::add);
		this.refreshInputFilesRespond(this.createLineText(keepList), keepList.size(), baseList.size() - keepList.size());
	}

	FTCaches caches;

	@Override
	public void createTableWithClonesRequest(final String inputText, final long hashSize, final long testSize) throws Exception {

		this.caches = new FTCaches();
		this.caches.restore();

		final var pathSet = this.createPathSet();
		final var itemList = new LinkedList<FTItem>();
		final var lineList = this.createLineList(inputText);
		final var failCount = new int[]{lineList.size()};
		this.runItems(lineList, line -> {
			if (line.size() <= 0) return;
			final var file = new File(line.get(0));
			if (!file.isAbsolute() || !file.isFile()) return;
			failCount[0]--;
			final var path = file.getPath();
			this.taskEntry = path;
			if (!pathSet.add(path)) return;
			itemList.add(0, new FTItem(path));
			this.taskCount++;
		}, null);

		pathSet.clear();
		final var sizeListMap = new HashMap2<Object, FTItem>();
		while (!itemList.isEmpty() && !this.isTaskCanceled) { // rückwärts
			final var sizeItem = itemList.remove(0);
			this.taskEntry = sizeItem;
			this.taskCount--;
			try {
				sizeItem.sourceSize = new File(sizeItem.sourcePath).length();
			} catch (final Exception error) {
				sizeItem.sourceSize = null;
			}
			sizeItem.previousItem = sizeListMap.put(sizeItem.sourceSize, sizeItem);
			if (sizeItem.previousItem != null) {
				this.taskCount++;
			}
		}
		sizeListMap.remove(null);
		itemList.clear();

		final var hashListMap = new HashMap2<Object, FTItem>();
		final var itemDataListMap = new HashMap2<Object, FTItem>();
		final var originalList = new LinkedList<FTItem>();

		final var equalSizeIter = sizeListMap.values().iterator();
		while (equalSizeIter.hasNext() && !this.isTaskCanceled) {
			final var equalSizeList = equalSizeIter.next();
			if (equalSizeList.previousItem != null) {
				this.taskCount++;
				hashListMap.clear();
				final var testSize2 = Math.min(equalSizeList.sourceSize.longValue(), testSize);
				for (FTItem equalSizeItem = equalSizeList, next; equalSizeItem != null; equalSizeItem = next) { // vorwärts
					this.taskEntry = equalSizeItem;
					this.taskCount--;
					next = equalSizeItem.previousItem;

					equalSizeItem.sourceHash = Objects.notNull(this.caches.get(equalSizeItem.sourcePath, hashSize), equalSizeItem);

					equalSizeItem.previousItem = hashListMap.put(equalSizeItem.sourceHash, equalSizeItem);
					if (equalSizeItem.previousItem != null) {
						this.taskCount++;
					}
				}

				final var iterator = hashListMap.values().iterator();
				while (iterator.hasNext() && !this.isTaskCanceled) {
					final var hashList = iterator.next();

					if (hashList.previousItem != null) {
						this.taskCount++;

						itemDataListMap.clear();
						for (FTItem item3 = hashList, prev; (item3 != null) && !this.isTaskCanceled; item3 = prev) { // rückwärts

							prev = item3.previousItem;

							item3.sourceData = new FTData(item3.sourcePath, testSize2);

							item3.previousItem = itemDataListMap.put(item3.sourceData, item3);
							if (item3.previousItem != null) {
								this.taskCount++;
							}
						}
						for (final var original: itemDataListMap.values()) {
							if (original.previousItem != null) {
								originalList.add(original);
							}
						}

					}
				}

			}
		}

		final var targetList = this.createLineList();
		while (!originalList.isEmpty() && !this.isTaskCanceled) {
			final var original = originalList.remove(0);
			this.taskEntry = original;
			for (var duplikat = original.previousItem; (duplikat != null) && !this.isTaskCanceled; duplikat = duplikat.previousItem) { // vorwärts
				targetList.add(Arrays.asList(original.sourcePath, duplikat.sourcePath, original.sourceHash.toString(), original.sourceSize.toString()));
			}
			this.taskCount--;
		}

		this.caches.persist();
		this.caches = null;

		this.createTableWithClonesRespond(this.createLineText(targetList), targetList.size(), failCount[0]);
	}

	private final Pattern datetime = Pattern.compile("([0-9]{4})[^0-9]*([0-9]{2})[^0-9]*([0-9]{2})[^0-9]*([0-9]{2})[^0-9]*([0-9]{2})[^0-9]*([0-9]{2})[^0-9]*");

	private FEMDatetime getDatetime(final String fileName, final long moveTime) {
		final Matcher m = this.datetime.matcher(fileName);
		if (!m.find()) return null;
		return FEMDatetime.from(m.group(1) + "-" + m.group(2) + "-" + m.group(3) + "T" + m.group(4) + ":" + m.group(5) + ":" + m.group(6)).move(0, moveTime * 1000);
	}

	FEMDatetime getDatetime(final long fileTime, final long moveTime) {
		return FEMDatetime.from(fileTime + (moveTime * 1000));
	}

	private int createTargetsWithTimeImpl(final String tableText, final List<List<String>> keepList, final long moveTime, final boolean isPath,
		final boolean isName) {
		final var pathSet = this.createPathSet();
		final var lineList = this.createLineList(tableText);
		final var failCount = new int[]{lineList.size()};
		this.runItems(lineList, line -> {
			keepList.add(line);
			if (line.size() < 1) return;
			final var source = new File(line.get(0));
			if (!source.isAbsolute() || (!isName && !source.isFile())) return;
			final var parentFile = source.getParentFile();
			if (parentFile == null) return;
			final var parentName = isPath ? parentFile.getName() : null;
			final var grandparentFile = isPath ? parentFile.getParentFile() : parentFile;
			if (grandparentFile == null) return;
			final var fileName = source.getName();
			final var fileTime = source.lastModified();
			final var index = fileName.lastIndexOf('.');
			if (index < 0) return;
			this.taskEntry = source.getPath();
			final var type = fileName.substring(index).toLowerCase();
			var datetime = isName ? this.getDatetime(fileName, moveTime) : this.getDatetime(fileTime, moveTime);
			while (true) {
				final var targetName = String.format("%04d-%02d-%02d %02d.%02d.%02d%s", //
					datetime.yearValue(), datetime.monthValue(), datetime.dateValue(), //
					datetime.hourValue(), datetime.minuteValue(), datetime.secondValue(), type);
				final var targetFile = isPath //
					? new File(new File(grandparentFile, String.format("%04d-%02d_%s", //
						datetime.yearValue(), datetime.monthValue(), parentName)), targetName) //
					: new File(parentFile, targetName);
				final var targetPath = targetFile.getPath();
				if (pathSet.add(targetPath)) {
					if (line.size() > 1) {
						line.set(1, targetPath);
					} else {
						line.add(targetPath);
					}
					failCount[0]--;
					return;
				}
				datetime = datetime.move(0, 1000);
			}
		}, keepList::add);
		return failCount[0];
	}

	@Override
	public void createTargetsWithTimenameFromNameRequest(final String tableText, final long moveTime) {
		final var keepList = this.createLineList();
		final var failCount = this.createTargetsWithTimeImpl(tableText, keepList, moveTime, false, true);
		this.createTargetsWithTimenameFromNameRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	@Override
	public void createTargetsWithTimepathFromNameRequest(final String tableText, final long moveTime) {
		final var keepList = this.createLineList();
		final var failCount = this.createTargetsWithTimeImpl(tableText, keepList, moveTime, true, true);
		this.createTargetsWithTimepathFromNameRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	@Override
	public void createTargetsWithTimenameFromTimeRequest(final String tableText, final long moveTime) {
		final var keepList = this.createLineList();
		final var failCount = this.createTargetsWithTimeImpl(tableText, keepList, moveTime, false, false);
		this.createTargetsWithTimenameFromTimeRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	@Override
	public void createTargetsWithTimepathFromTimeRequest(final String tableText, final long moveTime) {
		final var keepList = this.createLineList();
		final var failCount = this.createTargetsWithTimeImpl(tableText, keepList, moveTime, true, false);
		this.createTargetsWithTimepathFromTimeRespond(this.createLineText(keepList), keepList.size(), failCount);
	}

	private int procSourceToTargetFilesImpl(final String tableText, final List<List<String>> keepList, final boolean isMove) {
		final var lineList = this.createLineList(tableText);
		this.runItems(lineList, line -> {
			if (line.size() > 1) {
				final var sourceFile = new File(line.get(0));
				final var targetFile = new File(line.get(1));
				try {
					if (sourceFile.isAbsolute() && targetFile.isAbsolute() && sourceFile.isFile() && !targetFile.isFile()) {
						this.taskEntry = sourceFile;
						targetFile.getParentFile().mkdirs();
						if (isMove) {
							Files.move(sourceFile.toPath(), targetFile.toPath());
						} else {
							Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
						}
						return;
					}
				} catch (final Exception ignored) {}
			}
			keepList.add(line);
		}, keepList::add);
		final var dropCount = lineList.size() - keepList.size();
		return dropCount;
	}

	@Override
	public void copySourceToTargetFilesRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.procSourceToTargetFilesImpl(tableText, keepList, false);
		this.copySourceToTargetFilesRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void moveSourceToTargetFilesRequest(final String tableText) {
		final var keepList = this.createLineList();
		final var dropCount = this.procSourceToTargetFilesImpl(tableText, keepList, true);
		this.moveSourceToTargetFilesRespond(this.createLineText(keepList), keepList.size(), dropCount);
	}

	@Override
	public void showSourceAndTargetFilesRequest(final String tableText) throws Exception {
		final var linkCount = new int[2];
		final var parentFile = Files.createTempDirectory("file-clone-finder-").toFile();
		this.runItems(this.createLineList(tableText), line -> {
			if (line.size() > 1) {
				final var sourceFile = new File(line.get(0));
				final var targetFile = new File(line.get(1));
				if (sourceFile.isAbsolute() && targetFile.isAbsolute() && sourceFile.isFile() && targetFile.isFile()) {
					linkCount[0]++;
					final var sourceLink = new File(parentFile, linkCount[0] + "-ORIGINAL-" + sourceFile.getName());
					final var targetLink = new File(parentFile, linkCount[0] + "-DUPLIKAT-" + targetFile.getName());
					try {
						Files.createSymbolicLink(sourceLink.toPath(), sourceFile.toPath());
						linkCount[1]++;
					} catch (final Exception ignored) {}
					try {
						Files.createSymbolicLink(targetLink.toPath(), targetFile.toPath());
						linkCount[1]++;
					} catch (final Exception ignored) {}
				}
			}
		}, null);
		Desktop.getDesktop().open(parentFile);
		this.showSourceAndTargetFilesRespond(parentFile.getPath(), linkCount[1]);
	}

	private static final long serialVersionUID = -7889325283531060273L;

}
