package vandy.cs251;

import java.lang.ArrayIndexOutOfBoundsException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Provides a wrapper facade around primitive char arrays, allowing
 * for dynamic resizing.
 */
public class CharArray implements Comparable<CharArray>,
Iterable<Character>,
Cloneable {
	/**
	 * The underlying array.
	 */
	private char[] mArray;

	/**
	 * The current size of the array.
	 */
	private int mSize;

	/**
	 * Default value for elements in the array.
	 */
	private char mDefaultValue;

	/**
	 * Constructs an array of the given size.
	 *
	 * @param size Non-negative integer size of the desired array.
	 */
	public CharArray(int size) {
		mSize = size;
		mArray = new char [mSize];
	}

	/**
	 * Constructs an array of the given size, filled with the provided
	 * default value.
	 *
	 * @param size Nonnegative integer size of the desired array.
	 * @param mDefaultvalue A default value for the array.
	 */
	public CharArray(int size,
			char mDefaultvalue) {
		mSize = size;
		mArray = new char [mSize];
		mDefaultValue = mDefaultvalue;
		Arrays.fill(mArray,mDefaultvalue);
	}

	/**
	 * Copy constructor; creates a deep copy of the provided CharArray.
	 *
	 * @param s The CharArray to be copied.
	 */
	public CharArray(CharArray s) {
		mSize = s.mSize;
		mDefaultValue = s.mDefaultValue;
		mArray = Arrays.copyOf(s.mArray, mSize);
	}

	/**
	 * Creates a deep copy of this CharArray.  Implements the
	 * Prototype pattern.
	 */
	@Override
	public Object clone() {
		CharArray n = new CharArray(mSize);
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
	 * @return The current maximum capacity of the array.
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
		
		if ((mDefaultValue != '\u0000') & (size > mSize)){
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
	public char get(int index) {
		rangeCheck(index);
		return mArray[index];
	}

	/**
	 * Sets the element at the requested index with a provided value.
	 * @param index Nonnegative index of the requested element.
	 * @param value A provided value.
	 * @throws ArrayIndexOutOfBoundsException If the requested index is outside the
	 * current bounds of the array.
	 */
	public void set(int index, char value) {
		rangeCheck(index);
		mArray[index]  = value;
	}

	/**
	 * Compares this array with another array.
	 * <p>
	 * This is a requirement of the Comparable interface.  It is used to provide
	 * an ordering for CharArray elements.
	 * @return a negative value if the provided array is "greater than" this array,
	 * zero if the arrays are identical, and a positive value if the
	 * provided array is "less than" this array. These arrays should be compred
	 * lexicographically.
	 */
	@Override
	public int compareTo(CharArray s) {
		if (mSize == 0 & s.mSize == 0) return 0;
		if (mSize != 0 & s.mSize == 0) return -1;
		if (mSize == 0 & s.mSize != 0) return 1;
		String me = String.valueOf(mArray).substring(0, mSize);
		String them = String.valueOf(s.mArray).substring(0, s.mSize);
//		System.out.println("["+me+","+them+"]"+me.compareTo(them));
		return me.compareTo(them);
	}

	/**
	 * Throws an exception if the index is out of bound.
	 */
	private void rangeCheck(int index) {
		if ((index < 0) | (index >= mSize)) throw new ArrayIndexOutOfBoundsException();
	}

	/**
	 * Define an Iterator over the CharArray.
	 */
	public class CharArrayIterator
	implements java.util.Iterator<Character> {
		/**
		 * Keeps track of how far along the iterator has progressed.
		 */
		int index;

		/**
		 * Constructor.
		 */
		public CharArrayIterator() {
			index = 0;
		}

		/**
		 * @return true if there are any remaining elements that
		 * haven't been iterated through yet; else false.
		 */
		@Override
		public boolean hasNext() {
			if (index < mSize) return true;
			return false;
		}

		/**
		 * @return The next element in the iteration.
		 */
		@Override
		public Character next() {
			index++;
			return mArray[index-1];
		}
	}

	/**
	 * Factory method that returns an Iterator.
	 */
	public Iterator<Character> iterator () {
		return new CharArrayIterator();
	}
}
