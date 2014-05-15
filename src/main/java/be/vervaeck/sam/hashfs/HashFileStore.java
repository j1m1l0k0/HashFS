package be.vervaeck.sam.hashfs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

public class HashFileStore extends FileStore {

	@Override
	public Object getAttribute(String arg0) throws IOException {
		return null;
	}

	@Override
	public <V extends FileStoreAttributeView> V getFileStoreAttributeView(
			Class<V> arg0) {
		return null;
	}

	@Override
	public long getTotalSpace() throws IOException {
		return 0;
	}

	@Override
	public long getUnallocatedSpace() throws IOException {
		return 0;
	}

	@Override
	public long getUsableSpace() throws IOException {
		return 0;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public boolean supportsFileAttributeView(
			Class<? extends FileAttributeView> arg0) {
		return false;
	}

	@Override
	public boolean supportsFileAttributeView(String arg0) {
		return false;
	}

	@Override
	public String type() {
		return null;
	}

}
