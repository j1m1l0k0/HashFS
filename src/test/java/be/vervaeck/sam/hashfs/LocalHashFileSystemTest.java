package be.vervaeck.sam.hashfs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocalHashFileSystemTest {

	@BeforeClass public static void prepare() throws IOException {
		Sources.provision("guava-libraries");
	}
	
	@AfterClass public static void finish() throws IOException {
		System.err.println("cleaning up");
		Sources.sandbox.clean();
	}
	
	final static Logger logger = Logger.getGlobal();
	
	final static Path directory = Sources.sandbox.get("test1");

	@Test
	public void testCreation() throws IOException {
		assertTrue(Files.exists(directory));
	}
	
	@Test
	public void testOpen() throws IOException {
		LocalHashFileSystem.open(null, directory);
	}
	
	@Test
	public void testStorage() throws IOException {
		Path files = Sources.resources.get("guava-libraries");
		Path dir = Sources.sandbox.get("guava-libraries-hashfs");
		Path file = files.resolve("COPYING");
		LocalHashFileSystem fs = LocalHashFileSystem.open(null, directory);
		fs.store(file);
		assertTrue(Files.exists(file));
	}

}
