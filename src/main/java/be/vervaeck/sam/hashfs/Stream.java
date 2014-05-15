package be.vervaeck.sam.hashfs;

import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.google.common.base.Supplier;

@Beta
public abstract class Stream<T> implements Iterable<T>, Iterator<T>, Supplier<T> {

	T nextElement;
	
	protected Stream() {
		next();
	}
	
	@Override
	public boolean hasNext() {
		return nextElement != null;
	}
	
	@Override
	public T next() {
		T currentElement = nextElement;
		nextElement = get();
		return currentElement;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}
}
