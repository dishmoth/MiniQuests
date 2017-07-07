/*
 *  BitBuffer.java
 *  Copyright Simon Hern 2012
 *  Contact: dishmoth@yahoo.co.uk, www.dishmoth.com
 */

package com.dishmoth.miniquests.game;

import java.util.Arrays;

// simple utility to pack small integers into a byte array
public class BitBuffer {

  // initial bytes in the buffer 
  private static final int kCapacity = 32;
  
  // buffer for storing the bits
  private byte mBuffer[];

  // number of bits currently in the buffer (read or unread)
  private int mNumBits;
  
  // next bit to read or write (non-negative index)
  private int mIndex;

  // constructor (empty)
  public BitBuffer() {
    
    mBuffer = new byte[kCapacity];
    mNumBits = 0;
    mIndex = 0;
    
  } // constructor

  // constructor (from bytes)
  public BitBuffer(byte bytes[]) {
    
    mBuffer = new byte[kCapacity];
    fromBytes(bytes);
    
  } // constructor

  // constructor (from string)
  public BitBuffer(String str) {
    
    mBuffer = new byte[kCapacity];
    fromString(str);
    
  } // constructor

  // copy constructor
  public BitBuffer(BitBuffer other) {
    
    mBuffer = other.toBytes();
    mNumBits = other.mNumBits;
    mIndex = other.mIndex;
    
  } // constructor
  
  // empty the buffer
  public void clear() {
    
    Arrays.fill(mBuffer, (byte)0);
    mNumBits = 0;
    mIndex = 0;
    
  } // clear()
  
  // move the buffer's read index back to the start (ready to read)
  public void toStart() {
    
    mIndex = 0;
    
  } // toStart()

  // move the buffer's read index to the end (ready to write)
  public void toEnd() {
    
    mIndex = mNumBits;
    
  } // toEnd()

  // how many bits remain in the buffer for reading
  public int numBitsToRead() { return (mNumBits - mIndex); }
  
  // how many bits are in the buffer, read or unread
  public int numBits() { return mNumBits; }
  
  // reads a positive integer (of specified number of bits) from the buffer 
  // (returns -1 if the bits run out)
  public int read(int numBits) {
    
    assert( numBits >= 1 && numBits <= 31);
    
    if ( mIndex + numBits > mNumBits ) {
      return -1;
    }
    
    int value = 0;
    for ( int k = 0 ; k < numBits ; k++ ) {
      int byteIndex = (mIndex >> 3),
          bitIndex  = 7 - (mIndex & 0x07);
      int b = (mBuffer[byteIndex] >> bitIndex) & 0x01;
      value = ( (value<<1) | b );
      mIndex++;
    }
    return value;
    
  } // read()
  
  // write the value to the buffer, assuming it is the specified number of bits
  // (the current index must already be at the end of the buffer)
  public void write(int value, int numBits) {
    
    assert( numBits >= 1 && numBits <= 31 );
    assert( value >= 0 && value < (1<<numBits) );
    
    assert( mIndex == mNumBits );
    
    mNumBits += numBits;
    if ( mNumBits > (mBuffer.length << 3) ) {
      mBuffer = copyOf(mBuffer, mBuffer.length+kCapacity);
    }
    
    for ( int k = 0 ; k < numBits ; k++ ) {
      int b = (value >> (numBits-k-1)) & 0x01;
      if ( b != 0 ) {
        int byteIndex = (mIndex >> 3),
            bitIndex  = 7 - (mIndex & 0x07);
        mBuffer[byteIndex] |= (1<<bitIndex);
      }
      mIndex++;
    }
    
  } // write()

  // read a single boolean value from the buffer
  // (returns false if end of buffer is reached)
  public boolean readBit() {
    
    if ( mIndex == mNumBits ) return false;
    return (read(1) == 1); 
    
  } // readBit() 
  
  // write a single boolean value to the buffer
  public void writeBit(boolean value) { write( (value?1:0), 1); }
  
  // write the (unread) bits from another buffer onto the end of this one
  // (both buffers are left read to the end)
  public void append(BitBuffer other) {

    assert( mIndex == mNumBits );

    int numBytes = (mNumBits + other.mNumBits + 7)/8;
    if ( mBuffer.length < numBytes ) {
      mBuffer = copyOf(mBuffer, numBytes+kCapacity);
    }
    
    while ( other.numBitsToRead() > 0 ) {
      int num = Math.min(other.numBitsToRead(), 16);
      int val = other.read(num);
      write(val, num);
    }
    
  } // append()
  
  // copy the array, with truncation or zero-padding as needed
  // (this is a function in Arrays in Java 1.6)
  static private byte[] copyOf(byte[] original, int newLength) {
    
    assert( original != null );
    assert( newLength > 0 );
    
    byte result[] = new byte[newLength];
    Arrays.fill(result, (byte)0);
    
    int len = Math.min(newLength, original.length);
    for ( int k = 0 ; k < len ; k++ ) result[k] = original[k];
    
    return result;
    
  } // copyOf()

  // return a copy of the current buffer
  public byte[] toBytes() {

    if ( mNumBits == 0 ) return null;
    
    final int numBytes = (mNumBits+7)/8;
    return copyOf(mBuffer, numBytes);
    
  } // toBytes()

  // replace the buffer with specified bytes
  public void fromBytes(byte bytes[]) {
    
    mIndex = 0;
    Arrays.fill(mBuffer, (byte)0);
    
    if ( bytes == null ) {
      mNumBits = 0;
      return;
    }
    
    mNumBits = 8*bytes.length;
    
    if ( bytes.length > mBuffer.length ) {
      mBuffer = copyOf(bytes, bytes.length);
      return;
    }
    
    for ( int k = 0 ; k < bytes.length ; k++ ) mBuffer[k] = bytes[k];
    
  } // fromBytes()
  
  // convert to a hex string
  public String toString() {

    if ( mNumBits == 0 ) return "";
    
    final int numBytes = (mNumBits+7)/8;
    StringBuilder str = new StringBuilder(numBytes);
    
    for ( int k = 0 ; k < 2*numBytes ; k++ ) {
      int val = mBuffer[k/2];
      if ( k%2 == 0 ) val = (val>>4) & 0x0F;
      else            val = val & 0x0F;
      if ( val < 10 ) str.append((char)('0'+val));
      else            str.append((char)('A'+(val-10)));
    }
    
    return str.toString();
    
  } // toString()
  
  // replace with the specified hex string
  public void fromString(String str) {
    
    mIndex = 0;
    Arrays.fill(mBuffer, (byte)0);
    
    if ( str == null || str.length() == 0) {
      mNumBits = 0;
      return;
    }
    
    mNumBits = 4*str.length();
    
    if ( str.length() > mBuffer.length ) {
      mBuffer = new byte[ str.length() ];
    }
    
    for ( int k = 0 ; k < str.length() ; k++ ) {
      char ch = str.charAt(k);
      int val;
      if      ( ch >= '0' && ch <= '9' ) val = (int)(ch - '0');
      else if ( ch >= 'A' && ch <= 'F' ) val = (int)(ch - 'A') + 10;
      else if ( ch >= 'a' && ch <= 'f' ) val = (int)(ch - 'a') + 10;
      else                               val = 15;
      assert( val >= 0 && val <= 15 );
      if ( k%2 == 0 ) val = (val<<4);
      mBuffer[k/2] |= val;
    }
    
  } // fromString()
  
} // class BitBuffer
