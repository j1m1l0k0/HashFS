package be.vervaeck.sam.hashfs;

import static java.util.logging.Level.INFO;

import java.awt.image.RescaleOp;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

import org.junit.After;

public class Sources {

	public final static Logger logger = Logger.getGlobal();

	protected Path directory;


	protected Sources(String directory) {
		try {
			this.directory = Paths.get(directory).toAbsolutePath();
			Files.createDirectories(this.directory);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public Path get(String path) {
		return directory.resolve(path);
	}
	
	public void clean() throws IOException {
		Files.walkFileTree(directory, new FileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs)
					throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			};
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});
	}

	protected static Sources resources;
	protected static Sources sandbox;
	
	
	static {
		 resources = new Sources("src/test/resources");
		 sandbox = new Sources("src/test/sandbox");
	}
	
}
