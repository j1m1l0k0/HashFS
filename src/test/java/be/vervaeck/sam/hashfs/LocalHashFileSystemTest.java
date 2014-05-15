package be.vervaeck.sam.hashfs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Test;

public class LocalHashFileSystemTest {

	final static Logger logger = Logger.getGlobal();
	
	final static Path directory = Sources.sandbox.get("test1");

	@Test
	public void testCreation() throws IOException {
		LocalHashFileSystem fs = LocalHashFileSystem.create(null, directory);
		assertTrue(Files.exists(directory));
	}
	
	@Test
	public void testOpen() throws IOException {
		LocalHashFileSystem.open(null, directory);
	}
	
	@Test
	public void testStorage() 

	@After public void finish() throws IOException {
		Sources.sandbox.clean();
	}

}
