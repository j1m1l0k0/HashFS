package be.vervaeck.sam.hashfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;

/**
 * A path identifying a unique chunk of data.
 * 
 * Unlike many other paths, a {@link HashPath} is not a tree-like structure.
 */
public class HashPath implements Path {

	final static String SEPARATOR = "";

	/**
	 * The hash that is used to identify the file.
	 */
	Hash hash;
	
	/**
	 * The file system this path is connected to.
	 * 
	 * For use with the {@link #getFileSystem()} method.
	 */
	protected HashFileSystem fs;
	
	/**
	 * Returns the hash part of this path.
	 */
	public Hash getHash() {
		return hash;
	}

	/**
	 * Attempts to convert a generic {@link Path} to a {@link HashPath}.
	 * 
	 * @param obj
	 *            The object to convert
	 * @return A {@link HashPath} referencing the original object.
	 */
	protected static HashPath toHashPath(Path obj) {
		if (obj == null)
			throw new NullPointerException();
		if (!(obj instanceof HashPath))
			throw new ProviderMismatchException();
		return (HashPath) obj;
	}

	protected HashPath(HashFileSystem fs, Hash hash) {
		this.fs = fs;
		this.hash = hash;
	}

	@Override
	public int compareTo(Path other) {
		return hash.compareTo(toHashPath(other).hash);
	}

	@Override
	public boolean endsWith(Path other) {
		return compareTo(other) == 0;
	}

	@Override
	public boolean endsWith(String other) {
		return hash.toString().endsWith(other);
	}

	@Override
	public Path getFileName() {
		return this;
	}

	@Override
	public HashFileSystem getFileSystem() {
		return fs;
	}

	@Override
	public Path getName(int index) {
		switch (index) {
		case 0:
			return this;
		default:
			throw new IllegalArgumentException("index " + index
					+ " out of range");
		}
	}

	@Override
	public int getNameCount() {
		return 1;
	}

	@Override
	public Path getParent() {
		// A hash file system path does not have a parent
		throw new UnsupportedOperationException();
	}

	@Override
	public Path getRoot() {
		// A hash does not have a root
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAbsolute() {
		// Every hash is absolute
		return true;
	}

	@Override
	public Iterator<Path> iterator() {
		List<Path> list = new LinkedList<Path>();
		list.add(this);
		return list.iterator();
	}

	@Override
	public Path normalize() {
		return this;
	}

	@Override
	public WatchKey register(WatchService arg0, Kind<?>... arg1)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public WatchKey register(WatchService arg0, Kind<?>[] arg1,
			Modifier... arg2) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Path relativize(Path other) {
		return other;
	}

	@Override
	public Path resolve(Path other) {
		return other;
	}

	@Override
	public Path resolve(String other) {
		return new HashPath(fs, Hash.decode(other));
	}

	@Override
	public Path resolveSibling(Path other) {
		return other;
	}

	@Override
	public Path resolveSibling(String other) {
		return new HashPath(fs, Hash.decode(other));
	}

	@Override
	public boolean startsWith(Path other) {
		return compareTo(toHashPath(other)) == 0;
	}

	@Override
	public boolean startsWith(String other) {
		return toString().startsWith(other);
	}

	@Override
	public Path subpath(int start, int end) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Path toAbsolutePath() {
		return this;
	}

	@Override
	public File toFile() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Path toRealPath(LinkOption... options) {
		return this;
	}

	@Override
	public String toString() {
		return hash.toString();
	}

	@Override
	public URI toUri() {
		return fs.toURI().resolve(toString());
	}
}
