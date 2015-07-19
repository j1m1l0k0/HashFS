HashFS
======

[![Project Status](https://stillmaintained.com/samvv/HashFS.png)](https://stillmaintained.com/samvv/HashFS)

A distributed content-addressable file system library written in Java.

Inspired by the storage engines used by Git and Mercurial, this file system
stores blobs of data in a persistent hash table. The application can then retrieve this
data by requesting the specified unique key/hash.

## Features

HashFS can be used to efficiently store user data, for example it can be used as a storage back-end for user file uploads on a website. It was originally created as a storage back-end for a lightweigt version control system, so it should be fairly easy to roll out your own SCM-program using this library.

 - Data consistency checking (duh!)
 - Supports file system proxying with caching
 - High compatibility with the Java standard library
 - Fairly lightweight

## Installation

This project uses maven. Just checkout the source and add it to your project's dependencies.

## Contributing

Contributions are always welcome. Please use the issue tracker to request a feature or send me a pull request. I will maintain this project depending how much it is used, so don't be afraid to tell me if you do.


## An example

Creating a new file system is fairly easy:

```java
URI location = URI.create("hash:files//path/to/file/storage");

FileSystem fs = FileSystems.create(location);
Path root = Paths.get(location);
```
    
Now let's store some data:
    
```java
Files.copy(Paths.get("/my/random/local/file.txt"), root);
Files.copy(Paths.get("http://other-example.com/path/to/remote/file.txt"), root);
Files.move(Paths.get("zip://some/zip/archive.zip/with/some/file.txt"), root);
```

We now have a few hashed data entries in our storage system:

```java
for(Path hash: fs.getRootDirectories()) {
	System.out.println(hash);
}
```
    
To retrieve a file:

```java
Path file = Paths.get("/my/random/file.txt");
Path hash = Files.move(file, root);
System.out.println(hash);
Files.move(hash, file);
```
    
You can also share the stored items:

```java
HashFileSystemServer server = new HashFileSystemServer(myFS);
server.start();
```
    
So that others can do:
    
```java
FileSystem remote = FileSystems.getFileSystem("hash://example.com/served/file/system");
```

Or via a third-party protocol:

```java
FileSystem remoteHTTP = FileSystems.getFileSystem("hash:http://example.com/served/file/system");
```

But this can be painfully slow ... let's cache it!

```java
HashFileSystem cache = HashFileSystem.cache(remote, Paths.get("caches/myhashfs"));
```
    
If you like you can now proxy the new file system:

```java
HashFileSystemServer server = new HashFileSystemServer(cache);
```
