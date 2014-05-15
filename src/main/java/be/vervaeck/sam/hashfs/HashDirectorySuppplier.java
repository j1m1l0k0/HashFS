package be.vervaeck.sam.hashfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.google.common.base.Supplier;

public class HashDirectorySuppplier implements Supplier<Hash> {

	InputStream in;
	
	public HashDirectorySuppplier(InputStream in) {
		this.in = in;
	}

	@Override
	public Hash get() {
		try {
			return Hash.decode(in);
		} catch (IOException e) {
			throw new NoSuchElementException();
		}
	}
	
}
