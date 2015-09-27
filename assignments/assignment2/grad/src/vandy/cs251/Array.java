package vandy.cs251;

import java.lang.ArrayIndexOutOfBoundsException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Provides a generic dynamically-(re)sized array abstraction.
 */
public class Array<T extends Comparable<T>>
             implements Comparable<Array<T>>,
                        Iterable<T>,
                        Cloneable {
    /**
     * The underlying array of type T.
     */
	private Object[] mArray;

    /**
     * The current size of the array.
     */
    private int mSize;

    /**
     * Default value for elements in the array.
     */
    private T mDefaultValue;

    /**
     * Constructs an array of the given size.
     * @param size Nonnegative integer size of the desired array.
     * @throws NegativeArraySizeException if the specified size is
     *         negative.
     */
    @SuppressWarnings("unchecked")
    public Array(int size) {
    	if (size <0) throw new NegativeArraySizeException();
    	mSize = size;
		mArray = new Object [mSize];
    }

    /**
     * Constructs an array of the given size, filled with the provided
     * default value.
     * @param size Nonnegative integer size of the desired array.
     * @param mDefaultvalue A default value for the array.
     * @throws NegativeArraySizeException if the specified size is
     *         negative.
     */
    @SuppressWarnings("unchecked")
    public Array(int size,
                 T defaultValue) {
    	if (size <0) throw new NegativeArraySizeException();
		mSize = size;
		mArray = new Object [mSize];
		mDefaultValue = defaultValue;
		Arrays.fill(mArray,mDefaultValue);

    }

    /**
     * Copy constructor; creates a deep copy of the provided array.
     * @param s The array to be copied.
     */
    @SuppressWarnings("unchecked")
    public Array(Array<T> s) {
		mSize = s.mSize;
		mDefaultValue = s.mDefaultValue;
		mArray = Arrays.copyOf(s.mArray, mSize);
    }

    /**
     * Creates a deep copy of this Array.  Implements the
     * Prototype pattern.
     */
	@Override
    public Object clone() {
    	Array<T> n = new Array<T>(mSize);
		n.mDefaultValue = mDefaultValue;
		n.mArray= Arrays.copyOf(mArray, mSize);
		return n;
    }

    /**
     * @return The current size of the array.
     */
    public int size() {
    	return mSize;
    }

    /**
     * @return The current maximum capacity of the array withough
     */
    public int capacity() {
    	return mArray.length;
    }

    /**
     * Resizes the array to the requested size.
     *
     * Changes the capacity of this array to hold the requested number of elements.
     * Note the following optimizations/implementation details:
     * <ul>
     *   <li> If the requests size is smaller than the current maximum capacity, new memory
     *   is not allocated.
     *   <li> If the array was constructed with a default value, it is used to populate
     *   uninitialized fields in the array.
     * </ul>
     * @param size Nonnegative requested new size.
     */
    public void resize(int size) {
		if (size > mArray.length){
			mArray = Arrays.copyOf(mArray, size);
		}
		
		if ((size > mSize)){
			Arrays.fill(mArray, mSize, size, mDefaultValue);
		}
		
		mSize = size;
    }

    /**
     * @return the element at the requested index.
     * @param index Nonnegative index of the requested element.
     * @throws ArrayIndexOutOfBoundsException If the requested index is outside the
     * current bounds of the array.
     */
    @SuppressWarnings("unchecked")
	public T get(int index) {
    	rangeCheck(index);
		return (T) mArray[index];
    }

    /**
     * Sets the element at the requested index with a provided value.
     * @param index Nonnegative index of the requested element.
     * @param value A provided value.
     * @throws ArrayIndexOutOfBoundsException If the requested index is outside the
     * current bounds of the array.
     */
    public void set(int index, T value) {
    	rangeCheck(index);
		mArray[index]  = value;
    }

    /**
     * Removes the element at the specified position in this Array.
     * Shifts any subsequent elements to the left (subtracts one from
     * their indices).  Returns the element that was removed from the
     * Array.
     *
     * @throws ArrayIndexOutOfBoundsException if the index is out of range.
     * @param index the index of the element to remove
     * @return element that was removed
     */
    @SuppressWarnings({ "unchecked" })
	public T remove(int index) {
    	rangeCheck(index);
    	T item = ((T)mArray[index]);
    	if (index < mSize - 1){
    		int left = index;
    		int right = mSize-1-index;
    		if (left ==0){
    			mArray = Arrays.copyOfRange(mArray, index+1, index + right+1);
    		} else {
    			Object[] leftArray = Arrays.copyOfRange(mArray, 0, left);
    			Object[] rightArray = Arrays.copyOfRange(mArray, index+1, index + right+1);
    			mArray = ArrayUtils.addAll(leftArray, rightArray);
    		}
    	}
    	mSize--;
        return item;

    }

    /**
     * Compares this array with another array.
     * <p>
     * This is a requirement of the Comparable interface.  It is used to provide
     * an ordering for Array elements.
     * @return a negative value if the provided array is "greater than" this array,
     * zero if the arrays are identical, and a positive value if the
     * provided array is "less than" this array.
     */
    @Override
    public int compareTo(Array<T> s) {
		if (mSize == 0 & s.mSize == 0) return 0;
		if (mSize != 0 & s.mSize == 0) return -1;
		if (mSize == 0 & s.mSize != 0) return 1;
		for (int i=0; i < Math.min(mSize,s.mSize); i++){
			String me = String.valueOf(mArray[i]);
			String them = String.valueOf(s.mArray[i]);
			System.out.println("["+me+","+them+"]"+me.compareTo(them));
			if (me.compareTo(them)!= 0) return me.compareTo(them);
		}
		if (mSize == s.mSize) return 0;
		if (mSize > s.mSize) return 1;
		return -1;
    }

    /** 
     * Throws an exception if the index is out of bound. 
     */
    private void rangeCheck(int index) {
    	if ((index < 0) | (index >= mSize)) throw new ArrayIndexOutOfBoundsException();
    }

    public class ArrayIterator 
           implements java.util.Iterator<T> {
        // Current position in the Array (defaults to 0).
        int index  = 0;

        // Index of last element returned; -1 if no such element.
        int lastElement = -1;

        /** 
         * @return True if the iteration has more elements that
         * haven't been iterated through yet, else false.
         */
        @Override
        public boolean hasNext() {
			if (index < mSize) return true;
			return false;
        }

        /**
         * @return The next element in the iteration.
         */
        @SuppressWarnings("unchecked")
		@Override
        public T next() {
        	lastElement =index;
			index++;
			return (T) mArray[index-1];
        }

        /**
         * Removes from the underlying collection the last element
         * returned by this iterator. This method can be called only
         * once per call to next().
         * @throws IllegalStateException if no last element was
         * returned by the iterator.
         */
        @Override
        public void remove() {
            if (lastElement == -1){
            	throw new IllegalStateException();
            }
            Array.this.remove(lastElement);
            index--;
            lastElement--;
        }
    }

    public class ArraySpliterator 
           implements java.util.Spliterator<T> {
        /** 
         * Current position of this interator. 
         */
        // TODO - you fill in here

        /**
         * The end of this iterator's partition. 
         */
        // TODO - you fill in here

        /**
         * Constructs a Spliterator that covers the entire range of
         * the Array from beginning to end.
         */
        public ArraySpliterator() {
            // TODO - you fill in here
        }

        /**
         * Constructs a Spliterator that covers a given range.
         */
        private ArraySpliterator(int pos, int end) {
            // TODO - you fill in here
        }

        /** Provides the characteristics of this spliterator. */
        @Override
        public int characteristics() {
            return IMMUTABLE | NONNULL | ORDERED | SIZED | SUBSIZED;
        }

        /**
         * Applies the provided action to the next element in the array,
         * if any remain in the partition of this spliterator.
         * @return true if an action was applied, false otherwise.
         */
        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            // TODO - you fill in here (replace return false with the
            // proper value(s)).
            return false;
        }

        /**
         * Attempts to partition the range covered by this iterator.
         * @return null if the range is not partitionable.
         */
        @Override
        public ArraySpliterator trySplit() {
            // TODO - you fill in here (replace return null with
            // proper return values(s)).
            return null;
        }

        /** 
         * Estimates the size of the partition covered by this
         * spliterator.
         */
        @Override
        public long estimateSize() {
            // TODO - you fill in here (replace return 0 with proper
            // return value).
            return 0;
        }
    }

    /**
     * Factory method that returns an Iterator.
     */
    public Iterator<T> iterator() {
        // TODO - you fill in here (replace null with proper return value).
    	return new ArrayIterator();
    }

    /**
     * Factory method that returns a SplitIterator.
     */
    public Spliterator<T> spliterator() {
        // TODO - you fill in here (replace null with proper return value).
        return null;
    }
}
