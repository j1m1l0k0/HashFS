HashFS
======

A distributed content-addressable file system library written in Java.

Inspired by the storage engines used by Git and Mercurial, this file system
stores blobs of data in a persistent hash table. The application can then retrieve this
data by requesting the specified unique key/hash.

**An example:**

Sometimes examples . Creating a new file system is fairly easy:

    URI location = URI.create("hash:files//path/to/file/storage");
    
    FileSystem fs = FileSystems.create(fsLocation);
    Path root = fs.getRootDirectory();
    
Now let's store some data:
    
    Files.copy(Paths.get("/my/random/local/file.txt"), root);
    Files.copy(Paths.get("http://other-example.com/path/to/remote/file.txt"), root);
    Files.move(Paths.get("zip://some/zip/archive.zip/with/some/file.txt"), root);

Check that the new data is added:

    for(Path hash: fs.getRootDirectories()) {
    	System.out.println(hash);
    }
    
To retrieve stored files:

	Path file = Paths.get("/my/random/file.txt");
	
	Path hash = Files.move(file, root);
	
    System.out.println(hash);
	
    Files.move(hash, hash);
    
You can also share them:

    HashFileSystemServer server = new HashFileSystemServer(myFS);
    server.start();
    
So that others can do:
    
    FileSystem remote = FileSystems.getFileSystem("hash://example.com/served/file/system");

Or via a third-party protocol:

    FileSystem remoteHTTP = FileSystems.getFileSystem("hash:http://example.com/served/file/system");
    
But this can be painfully slow ... let's cache it!

    HashFileSystem cache = HashFileSystem.cache(remote, Paths.get("caches/myhashfs"));
    
If you like you can serve it again as a proxy:
    
    HashFileSystemServer server = new HashFileSystemServer(cache);

## Features

HashFS can be used to efficiently store user data, for example as a storage back-end for a websit that stores file uploads. It was originally created as a storage back-end for a lightweigt version control system, so evidently it is a perfect solution for those scenarios.

 - Data consistency checking (duh!)
 - Supports file system proxying with caching
 - High compatibility with the Java standard library
 - Fairly lightweight

## Installation

This project uses maven. Just checkout the source and add it to your project's dependencies.

## Contributing

