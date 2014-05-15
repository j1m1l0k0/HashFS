package be.vervaeck.sam.hashfs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.FileStore;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.rmi.Remote;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;

@Beta
public class RemoteHashFileSystem extends HashFileSystem {

	protected InetSocketAddress address;
	protected String path;

	protected RemoteHashFileSystem(InetSocketAddress address, String path) {
		this.address = address;
		this.path = path;
	}

	/**
	 * Connects to an existing hash file system.
	 * 
	 * @param address
	 * @return
	 */
	public static RemoteHashFileSystem open(InetSocketAddress addr, String path) {
		return new RemoteHashFileSystem(addr, path);
	}

	public static RemoteHashFileSystem open(URI uri) {
		return open(
				InetSocketAddress.createUnresolved(uri.getHost(), Objects
						.firstNonNull(uri.getPort(),
								HashFileSystemServer.DEFAULT_PORT)),
				uri.getPath());
	}

	/**
	 * Attempts to create a new hash file system on the remote file system.
	 * 
	 * @param address
	 * @return
	 */
	public static RemoteHashFileSystem create(InetSocketAddress addr,
			String path) {
		RemoteHashFileSystem fs = new RemoteHashFileSystem(addr, path);
		return fs;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterable<FileStore> getFileStores() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashPath getPath(String first, String... more) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Path> getRootDirectories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSeparator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public WatchService newWatchService() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashFileSystemProvider provider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI toURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hash store(Path source) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void retrieve(Hash hash, Path dest) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public SeekableByteChannel newByteChannel(Hash hash) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Hash hash) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public SeekableByteChannel newByteChannel() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<HashPath> newDirectoryStream(Hash hash) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkAccess(Hash hash, AccessMode... modes) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
