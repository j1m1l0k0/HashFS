package be.vervaeck.sam.hashfs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;

/**
 * A server for the hashed file system.
 * 
 * @author samvv
 *
 */
public class HashFileSystemServer {

	protected final static Logger logger = Logger.getGlobal();
	
	public final static int DEFAULT_PORT = 9419;
	
	protected int port;
	
	private boolean running = false;
	
	ServerSocket serverSocket;
	
	public HashFileSystemServer(int port) throws IOException {
		this.port = port;
		this.serverSocket = new ServerSocket(port);
	}
	
	public void accept() throws IOException {
		try(Socket clientSocket = serverSocket.accept()) {
			
		}
	}
	
	public void start() {
		new Thread() {
			@Override
			public void run() {
				while(running) {
					try {
						accept();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		};
		running = true;
	}
	
	public void stop() {
		running = false;
	}
	
}
