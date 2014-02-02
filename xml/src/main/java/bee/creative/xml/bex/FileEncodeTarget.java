package bee.creative.xml.bex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class FileEncodeTarget implements EncodeTarget {

	RandomAccessFile file;

	public FileEncodeTarget(File file) throws FileNotFoundException {
		this(new RandomAccessFile(file, "rw"));
	}

	public FileEncodeTarget(String name) throws FileNotFoundException {
		this(new RandomAccessFile(name, "rw"));
	}

	public FileEncodeTarget(RandomAccessFile file) {
		this.file = file;
	}

	@Override
	public void write(byte[] array, int offset, int length) throws IOException {
		file.write(array, offset, length);
	}

}
