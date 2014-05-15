package be.vervaeck.sam.hashfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.google.common.base.Objects;

import static java.util.logging.Level.*;
import static java.nio.file.StandardCopyOption.*;
import static java.nio.file.AccessMode.*;

/**
 * A hashing file system layering on top of another file system.
 * 
 * @author samvv
 * 
 */
public class HashFileSystemProvider extends FileSystemProvider {

	private final static String URI_SCHEME = "hash";
	protected final static String URI_HASH_SEPARATOR = "/";

	protected Logger logger = Logger.getGlobal();

	/**
	 * A map of {@link HashFileSystem}s on the local storage pool.
	 */
	protected Map<URI, HashFileSystem> fileSystems;

	protected HashFileSystemProvider() {

	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException {
		HashPath hp = HashPath.toHashPath(path);
		hp.getFileSystem().checkAccess(hp.hash, modes);
	}

	@Override
	public void copy(Path source, Path dest, CopyOption... options)
			throws IOException {
		HashPath hp = HashPath.toHashPath(source);
		hp.getFileSystem().retrieve(hp.hash, dest);
	}

	@Override
	public void createDirectory(Path path, FileAttribute<?>... options)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Path path) throws IOException {
		HashPath hp = HashPath.toHashPath(path);
		hp.getFileSystem().delete(hp.hash);
	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path arg0,
			Class<V> arg1, LinkOption... arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HashFileStore getFileStore(Path path) throws IOException {
		throw new UnsupportedOperationException();
	}

	private HashFileSystem openFileSystem(URI uri) throws IOException {
		if (uri.getHost() == null) {
			return LocalHashFileSystem.open(this, uri.getPath());
		}

		InetAddress address = InetAddress.getByName(uri.getHost());

		if (address.isLoopbackAddress()) {
			return LocalHashFileSystem.open(this, Paths.get(uri.getPath()));
		} else {
			return RemoteHashFileSystem.open(new InetSocketAddress(address,
					Objects.firstNonNull(uri.getPort(),
							HashFileSystemServer.DEFAULT_PORT)), uri.getPath());
		}
	}

	@Override
	public HashFileSystem getFileSystem(URI uri) {
		if (fileSystems.containsKey(uri)) {
			return fileSystems.get(uri);
		}
		try {
			return fileSystems.put(uri, openFileSystem(uri));
		} catch(FileSystemNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw new FileSystemNotFoundException("corrupt filesystem");
		}
	}

	@Override
	public HashPath getPath(URI uri) {
		String path = uri.getPath();
		int separator = path.indexOf(URI_HASH_SEPARATOR);
		try {
			return getFileSystem(
					new URI(URI_SCHEME, uri.getUserInfo(), uri.getHost(), uri
							.getPort(), path.substring(0, separator), uri
							.getQuery(), uri.getFragment())).getPath(
					path.substring(separator));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getScheme() {
		return URI_SCHEME;
	}

	@Override
	public boolean isHidden(Path path) throws IOException {
		HashPath.toHashPath(path);
		return false;
	}

	@Override
	public boolean isSameFile(Path path1, Path path2) throws IOException {
		return path1.compareTo(path2) == 0;
	}

	@Override
	public void move(Path source, Path dest, CopyOption... arg2) {

	}

	@Override
	public SeekableByteChannel newByteChannel(Path path,
			Set<? extends OpenOption> options, FileAttribute<?>... attrs)
			throws IOException {
		HashPath hp = HashPath.toHashPath(path);
		return hp.getFileSystem().newByteChannel(hp.hash);
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path path,
			Filter<? super Path> options) throws IOException {
		throw new UnsupportedOperationException();
	}

	private HashFileSystem createFileSystem(URI uri) throws IOException {
		if (uri.getHost() == null) {
			return LocalHashFileSystem.create(this, uri.getPath());
		}

		InetAddress address = InetAddress.getByName(uri.getHost());

		if (address.isLoopbackAddress()) {
			return LocalHashFileSystem.create(this, Paths.get(uri.getPath()));
		} else {
			return RemoteHashFileSystem.create(
					new InetSocketAddress(address, Objects.firstNonNull(
							uri.getPort(), HashFileSystemServer.DEFAULT_PORT)),
					uri.getPath());
		}
	}

	@Override
	public HashFileSystem newFileSystem(URI uri, Map<String, ?> env)
			throws IOException {
		if (fileSystems.containsKey(uri)) {
			throw new FileSystemAlreadyExistsException();
		}
		return fileSystems.put(uri, createFileSystem(uri));
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path arg0,
			Class<A> arg1, LinkOption... arg2) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> readAttributes(Path arg0, String arg1,
			LinkOption... arg2) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(Path arg0, String arg1, Object arg2,
			LinkOption... arg3) throws IOException {
		throw new UnsupportedOperationException();
	}

}
