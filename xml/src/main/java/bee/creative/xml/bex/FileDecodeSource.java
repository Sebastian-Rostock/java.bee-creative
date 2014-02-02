package bee.creative.xml.bex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileDecodeSource implements DecodeSource {

	RandomAccessFile file;

	public FileDecodeSource(final File file) throws FileNotFoundException {
		this(new RandomAccessFile(file, "r"));
	}

	public FileDecodeSource(final String name) throws FileNotFoundException {
		this(new RandomAccessFile(name, "r"));
	}

	public FileDecodeSource(final RandomAccessFile file) {
		this.file = file;
	}

	@Override
	public void read(final byte[] array, final int offset, final int length) throws IOException {
		this.file.readFully(array, offset, length);
	}

	@Override
	public void seek(final long index) throws IOException {
		this.file.seek(index);
	}

	@Override
	public long index() throws IOException {
		return this.file.getFilePointer();
	}

}
