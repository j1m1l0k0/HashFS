package be.vervaeck.sam.hashfs;

import com.google.common.base.Supplier;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public abstract class DirectoryTreeSupplier implements Supplier<Path> {

	Stack<Iterator<Path>> parents;
	
	public DirectoryTreeSupplier(Path path) throws IOException {
		this.parents = new Stack<Iterator<Path>>();
		parents.push(Files.newDirectoryStream(path).iterator());
	}

	public Path get() throws NoSuchElementException
	{
		while(!parents.empty())
		{
			if(parents.peek().hasNext())
			{
				Path next = parents.peek().next();
				if (Files.isDirectory(next))
				{
					try {
						parents.push(Files.newDirectoryStream(next).iterator());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return next;
			} else {
				parents.pop();
			}
		}
		
		throw new NoSuchElementException();
	}
}
