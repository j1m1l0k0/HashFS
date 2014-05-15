package be.vervaeck.sam.hashfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.file.AccessMode;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.annotations.Beta;

public class LocalHashFileSystem extends HashFileSystem {

	private final static String TMP_DIR = "tmp";

	protected Path directory;
	protected FileSystem parent;

	private LocalHashFileSystem(HashFileSystemProvider provider, Path directory)
			throws IOException {
		this.provider = provider;
		this.parent = directory.getFileSystem();
		this.directory = directory.toRealPath();
	}

	/**
	 * Loads an existing file system into memory.
	 * 
	 * @param directory
	 * @return
	 * @throws FileNotFoundException
	 *             Thrown when the specified directory does not exist.
	 */
	protected static LocalHashFileSystem open(HashFileSystemProvider provider,
			Path directory) throws IOException {
		try {
			return new LocalHashFileSystem(provider, directory);
		} catch (FileNotFoundException e) {
			throw new FileSystemNotFoundException();
		}
	}
	
	protected static LocalHashFileSystem open(HashFileSystemProvider provider, String directory) throws IOException {
		return open(provider, Paths.get(directory));
	}

	/**
	 * Create a new file system on the parent file system and return it.
	 * 
	 * @param directory
	 * @param attr
	 * @return
	 * @throws FileAlreadyExistsException
	 *             Thrown if the specified directory already exists.
	 */
	protected static LocalHashFileSystem create(
			HashFileSystemProvider provider, Path directory,
			FileAttribute<?>... attr) throws IOException {
		try {
			Files.createDirectory(directory, attr);
		} catch (FileNotFoundException e) {
			throw new FileSystemAlreadyExistsException();
		}
		return new LocalHashFileSystem(provider, directory);
	}

	protected static LocalHashFileSystem create(
			HashFileSystemProvider provider, String directory,
			FileAttribute<?>... attr) throws IOException {
		return create(provider, Paths.get(directory), attr);
	}

	protected Path toStoragePath(Hash hash) {
		Path path = directory;
		String hashString = hash.toString();
		int start = 0;
		for (int offset : offsets) {
			path = path.resolve(hashString.substring(offset, start += offset));
		}
		return path;
	}

	@Override
	public void close() throws IOException {
		parent.close();
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HashPath getPath(String first, String... more) {
		if (more.length > 0) {
			throw new IllegalArgumentException(
					"a hash file system only takes paths of length 1");
		}
		return new HashPath(this, Hash.decode(first));
	}

	@Override
	public PathMatcher getPathMatcher(String pattern) {
		throw new UnsupportedOperationException(
				"a hash file system cannot perform path matching");
	}

	@Override
	public List<Path> getRootDirectories() {
		throw new UnsupportedOperationException("hash file system has no root");
	}

	@Override
	public String getSeparator() {
		return HashPath.SEPARATOR;
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOpen() {
		return parent.isOpen();
	}

	@Override
	public boolean isReadOnly() {
		return parent.isReadOnly();
	}

	@Override
	public WatchService newWatchService() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public HashFileSystemProvider provider() {
		return provider;
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		return new HashSet<String>();
	}

	@Override
	public URI toURI() {
		try {
			return new URI(provider.getScheme(), InetAddress.getLocalHost()
					.getHostName(), directory.toString());
		} catch (UnknownHostException | URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Hash store(Path source) throws IOException {
		Hash hash = Hash.checksum(source);
		Files.copy(source, toStoragePath(hash));
		return hash;
	}

	@Override
	public void delete(Hash hash) throws IOException {
		Files.delete(toStoragePath(hash));
	}

	@Override
	public void retrieve(Hash hash, Path dest) throws IOException {
		Files.copy(toStoragePath(hash), dest);
	}

	@Override
	public SeekableByteChannel newByteChannel() throws IOException {
		return new LocalHashChannel(this, Files.createTempDirectory(
				directory.resolve(TMP_DIR), "channel"));
	}

	@Override
	public SeekableByteChannel newByteChannel(Hash hash) throws IOException {
		return new LocalHashChannel(this, toStoragePath(hash));
	}

	@Override
	public Iterable<HashPath> newDirectoryStream(Hash hash) {
		throw new UnsupportedOperationException();
		// return new
		// HashDirectorySuppplier(Files.newInputStream(toStoragePath(hash)));
	}

	@Override
	public void checkAccess(Hash hash, AccessMode... modes) throws IOException {
		Path path = toStoragePath(hash);
		path.getFileSystem().provider().checkAccess(path, modes);
	}

}
