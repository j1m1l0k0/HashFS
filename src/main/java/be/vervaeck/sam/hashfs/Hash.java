package be.vervaeck.sam.hashfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.util.Arrays;
import java.util.Scanner;

import com.google.common.io.BaseEncoding;

/**
 * A hash of the given
 * 
 * @author samvv
 * 
 */
public class Hash implements Comparable<Hash> {

	private final static MessageDigest digester;

	static {
		try {
			digester = MessageDigest.getInstance(HashFileSystem.CHECKSUM);
		} catch (NoSuchAlgorithmException e) {
			throw new ProviderException(e);
		}
	}

	/**
	 * The hash in raw bytes.
	 */
	protected byte[] hash;

	/**
	 * Hexadecimal string representation of the hash.
	 * 
	 * Calculated lazily by {@link #toString()}.
	 */
	private String hashString;

	private static byte[] toBytesHash(String hash) {
		if (hash.length() != digester.getDigestLength() * 2) {
			throw new IllegalArgumentException("hash is of invalid length");
		}
		return BaseEncoding.base16().lowerCase().decode(hash);
	}

	private static String toStringHash(byte[] hash) {
		if (hash.length != digester.getDigestLength()) {
			throw new IllegalArgumentException("hash is of invalid length");
		}
		return BaseEncoding.base16().lowerCase().encode(hash);
	}

	protected Hash(byte[] hash, String hashString) {
		this.hash = hash;
		this.hashString = hashString;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this)return true;
		if(!(other instanceof Hash)) return false;
		return Arrays.equals(hash, ((Hash)other).hash);
	}

	/**
	 * Creates a new hash from the specified string.
	 * 
	 * @param hash
	 *            The hash in string representation.
	 * @return A new {@link Hash} object representing the string.
	 */
	public static Hash decode(String hash) {
		return new Hash(toBytesHash(hash), hash);
	}

	/**
	 * Reads a new hash from the specified {@link InputStream}.
	 * 
	 * @param in
	 * @return
	 * @throws IOException 
	 */
	public static Hash decode(InputStream in) throws IOException {
		byte[] result = new byte[digester.getDigestLength()];
		in.read(result, 0, digester.getDigestLength());
		return new Hash(result, null);
	}

	/**
	 * Creates a new hash from the specified byte array.
	 * 
	 * @param hash
	 *            The hash in byte array representation.
	 * @return A new {@link Hash} from the specified byte array.
	 */
	public static Hash raw(byte[] hash) {
		return new Hash(hash, toStringHash(hash));
	}
	
	public static Hash hash(String input) {
		return new Hash(digester.digest(input.getBytes()), null);
	}

	/**
	 * Creates a checksum of the given file.
	 * 
	 * @param file
	 *            A path to a file on a file system.
	 * @return A new {@link Hash} containing a checksum for the file.
	 * @throws IOException
	 *             Thrown when the file could not be read.
	 */
	public static Hash checksum(Path file) throws IOException {
		try (InputStream dis = new DigestInputStream(
				Files.newInputStream(file), digester)) {
			while (dis.read() != -1)
				;
			byte[] hash = digester.digest();
			digester.reset();
			return new Hash(hash, null);
		}
	}

	/**
	 * Returns a string representation of the hash.
	 * 
	 * The string is calculated lazily on demand.
	 */
	@Override
	public String toString() {
		if (hashString == null) {
			hashString = toStringHash(hash);
		}
		return hashString;
	}

	@Override
	public int compareTo(Hash other) {
		if (hash.length != other.hash.length) {
			return hash.length - other.hash.length;
		}
		for (int i = 0; i < hash.length; ++i) {
			if (hash[i] != other.hash[i]) {
				return hash[i] - other.hash[i];
			}
		}
		return 0;
	}

}
