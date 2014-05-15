package be.vervaeck.sam.hashfs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.DefaultBoundedRangeModel;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;

/**
 * A distributed hashing filesystem.
 * 
 * Similar to the storage engines used by Git and Mercurial, this file system
 * stores blobs of data in a index of hashes. The user can later retrieve the
 * data by requesting the specified unique key.
 * 
 * @author samvv
 */
public abstract class HashFileSystem extends FileSystem {

	final static int[] offsets = { 0, 2 };
	final static String CHECKSUM = "SHA1";

	protected HashFileSystemProvider provider;

	private final static MessageDigest digester;

	static {
		try {
			digester = MessageDigest.getInstance(CHECKSUM);
		} catch (NoSuchAlgorithmException e) {
			throw new ProviderException(e);
		}
	}

	/**
	 * Attempt to convert the given {@link FileSystem} to a
	 * {@link HashFileSystem}.
	 * 
	 * @param obj
	 *            The object to convert.
	 * @return The converted object upon success.
	 */
	static HashFileSystem toHashFileSystem(FileSystem obj) {
		if (obj == null)
			throw new NullPointerException();
		if (!(obj instanceof HashFileSystem))
			throw new ProviderMismatchException();
		return (HashFileSystem) obj;
	}

	/**
	 * Stores a file from another file system into this file system.
	 * 
	 * @throws IOException
	 *             Thrown when an error occurred during transfer.
	 */
	public abstract Hash store(Path source) throws IOException;

	/**
	 * Retrieve a file from the file system and copy its content to the
	 * specified path.
	 * 
	 * @throws IOException
	 *             Thrown when an error occurred during transfer.
	 */
	public abstract void retrieve(Hash hash, Path dest) throws IOException;

	/**
	 * Opens an existing file on the system.
	 * 
	 * @param hash
	 * @return
	 * @throws IOException
	 */
	public abstract SeekableByteChannel newByteChannel(Hash hash)
			throws IOException;

	/**
	 * 
	 * @param hash
	 * @throws IOException
	 */
	public abstract Iterable<HashPath> newDirectoryStream(Hash hash) throws IOException;

	/**
	 * Creates a new byte channel.
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract SeekableByteChannel newByteChannel() throws IOException;

	/**
	 * Deletes the given file from the system.
	 * 
	 * @param hash
	 *            The checksum of the file to delete.
	 * @throws IOException
	 */
	public abstract void delete(Hash hash) throws IOException;

	/**
	 * 
	 * @param path
	 * @param modes
	 */
	public abstract void checkAccess(Hash hash, AccessMode... modes) throws IOException;
	
	// @Override
	// public abstract Iterable<HashFileStore> getFileStores();

	@Override
	public HashFileSystemProvider provider() {
		return provider;
	};
	
	@Override
	public abstract HashPath getPath(String first, String ... more);

	/**
	 * Generates a URI linking to this file system.
	 * 
	 * @return A URI identifying this file system on the network.
	 */
	public abstract URI toURI();
}
