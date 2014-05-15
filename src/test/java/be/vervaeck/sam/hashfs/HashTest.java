package be.vervaeck.sam.hashfs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.Test;

import static java.util.logging.Level.*;

public class HashTest {

	final static Logger logger = Logger.getGlobal();
	
	@Test
	public void testEncoding() {
		Hash hash = Hash.hash("test");
		assertEquals("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", hash.toString());
		Hash hash2 = Hash.hash("#0gksNbcRE6#@PWQ");
		assertEquals("fbf2591820d8b1a542fb5c2bc4448f23c8abaf4f", hash2.toString());
		Hash hash3 = Hash.hash("#0gksNbcRE6#x8gj6Bd3EocB^W6w%^TkB2j7gaDNSrl8t$NiL2YNOJPU#PWQ");
		assertEquals("18fcbc207d4353159a85b27f7a21398fb465063b", hash3.toString());
	}
	
	@Test
	public void testEquality() {
		Hash hash1 = Hash.hash("test");
		assertEquals(hash1, hash1);
		Hash hash2 = Hash.hash("test");
		assertEquals(hash1, hash2);
		Hash hash3 = Hash.hash("test-other");
		assertNotEquals(hash1, hash3);
	}
	
	@Test
	public void testConverters() {
		Hash hash1 = Hash.hash("test-hash");
		Hash hash2 = Hash.decode(hash1.toString());
		assertEquals(hash1, hash2);
		Hash hash3 = Hash.raw(hash2.hash);
		assertEquals(hash2, hash3);
	}
	
	@Test
	public void testChecksum() throws IOException {
		Hash hash = Hash.checksum(Sources.resources.get("loremipsum.txt"));
		assertEquals("1930bf49fd2c7cb7c38c28ca303541aaf955686e", hash.toString());
	}

}
