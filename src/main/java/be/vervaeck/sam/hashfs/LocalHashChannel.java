package be.vervaeck.sam.hashfs;

import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;

import com.google.common.annotations.Beta;

/**
 * A channel that rehashes its path upon {@link close closure}.
 * 
 * @author samvv
 */
@Beta
public class LocalHashChannel implements SeekableByteChannel {

	SeekableByteChannel parent;
	Path file;
	LocalHashFileSystem fs;

	protected LocalHashChannel(LocalHashFileSystem fs, Path file) throws IOException {
		this.file = file;
		this.fs = fs;
		this.parent = Files.newByteChannel(file);
	}

	@Override
	public void close() throws IOException {
		parent.close();
		Files.move(file, fs.toStoragePath(Hash.checksum(file)));
	}

	@Override
	public boolean isOpen() {
		return parent.isOpen();
	}

	@Override
	public long position() throws IOException {
		return parent.position();
	}
	
	@Override
	public SeekableByteChannel position(long newPosition) throws IOException {
		parent.position(newPosition);
		return this;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException {
		return parent.read(dst);
	}

	@Override
	public long size() throws IOException {
		return parent.size();
	}

	@Override
	public SeekableByteChannel truncate(long size) throws IOException {
		parent.truncate(size);
		return this;
	}

	public int write(ByteBuffer src) throws IOException {
		return parent.write(src);
	}
}
