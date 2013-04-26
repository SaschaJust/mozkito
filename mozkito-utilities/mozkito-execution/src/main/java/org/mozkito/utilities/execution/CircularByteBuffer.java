/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.utilities.execution;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implements the Circular Buffer producer/consumer model for bytes. More information about this class is available from
 * <a target="_top" href= "http://ostermiller.org/utils/CircularByteBuffer.html">ostermiller.org</a>.
 * <p>
 * Using this class is a simpler alternative to using a PipedInputStream and a PipedOutputStream. PipedInputStreams and
 * PipedOutputStreams don't support the mark operation, don't allow you to control buffer sizes that they use, and have
 * a more complicated API that requires instantiating two classes and connecting them.
 * <p>
 * This class is thread safe.
 * 
 * @see CircularCharBuffer
 * @see CircularObjectBuffer
 * 
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */
public class CircularByteBuffer {
	
	/**
	 * The Class BufferOverflowException.
	 */
	private static final class BufferOverflowException extends IOException {
		
		/**
		 * Instantiates a new buffer overflow exception.
		 * 
		 * @param message
		 *            the message
		 */
		public BufferOverflowException(final String message) {
			super(message);
			PRECONDITIONS: {
				// none
			}
			
			try {
				// body
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
	}
	
	/**
	 * Class for reading from a circular byte buffer.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	protected class CircularByteBufferInputStream extends InputStream {
		
		/**
		 * Returns the number of bytes that can be read (or skipped over) from this input stream without blocking by the
		 * next caller of a method for this input stream. The next caller might be the same thread or or another thread.
		 * 
		 * @return the number of bytes that can be read from this input stream without blocking.
		 * @throws IOException
		 *             if the stream is closed.
		 * 
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public int available() throws IOException {
			synchronized (CircularByteBuffer.this) {
				if (CircularByteBuffer.this.inputStreamClosed) {
					throw new IOException("InputStream has been closed, it is not ready.");
				}
				return (CircularByteBuffer.this.available());
			}
		}
		
		/**
		 * Close the stream. Once a stream has been closed, further read(), available(), mark(), or reset() invocations
		 * will throw an IOException. Closing a previously-closed stream, however, has no effect.
		 * 
		 * @throws IOException
		 *             never.
		 * 
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public void close() throws IOException {
			synchronized (CircularByteBuffer.this) {
				CircularByteBuffer.this.inputStreamClosed = true;
			}
		}
		
		/**
		 * Mark the present position in the stream. Subsequent calls to reset() will attempt to reposition the stream to
		 * this point.
		 * <p>
		 * The readAheadLimit must be less than the size of circular buffer, otherwise this method has no effect.
		 * 
		 * @param readAheadLimit
		 *            Limit on the number of bytes that may be read while still preserving the mark. After reading this
		 *            many bytes, attempting to reset the stream will fail.
		 * 
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public void mark(final int readAheadLimit) {
			synchronized (CircularByteBuffer.this) {
				// if (inputStreamClosed) throw new
				// IOException("InputStream has been closed; cannot mark a closed InputStream.");
				if ((CircularByteBuffer.this.buffer.length - 1) > readAheadLimit) {
					CircularByteBuffer.this.markSize = readAheadLimit;
					CircularByteBuffer.this.markPosition = CircularByteBuffer.this.readPosition;
				}
			}
		}
		
		/**
		 * Tell whether this stream supports the mark() operation.
		 * 
		 * @return true, mark is supported.
		 * 
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public boolean markSupported() {
			return true;
		}
		
		/**
		 * Read a single byte. This method will block until a byte is available, an I/O error occurs, or the end of the
		 * stream is reached.
		 * 
		 * @return The byte read, as an integer in the range 0 to 255 (0x00-0xff), or -1 if the end of the stream has
		 *         been reached
		 * @throws IOException
		 *             if the stream is closed.
		 * 
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public int read() throws IOException {
			while (true) {
				synchronized (CircularByteBuffer.this) {
					if (CircularByteBuffer.this.inputStreamClosed) {
						throw new IOException("InputStream has been closed; cannot read from a closed InputStream.");
					}
					final int available = CircularByteBuffer.this.available();
					if (available > 0) {
						final int result = CircularByteBuffer.this.buffer[CircularByteBuffer.this.readPosition] & 0xff;
						CircularByteBuffer.this.readPosition++;
						if (CircularByteBuffer.this.readPosition == CircularByteBuffer.this.buffer.length) {
							CircularByteBuffer.this.readPosition = 0;
						}
						ensureMark();
						return result;
					} else if (CircularByteBuffer.this.outputStreamClosed) {
						return -1;
					}
				}
				try {
					Thread.sleep(100);
				} catch (final Exception x) {
					throw new IOException("Blocking read operation interrupted.");
				}
			}
		}
		
		/**
		 * Read bytes into an array. This method will block until some input is available, an I/O error occurs, or the
		 * end of the stream is reached.
		 * 
		 * @param cbuf
		 *            Destination buffer.
		 * @return The number of bytes read, or -1 if the end of the stream has been reached
		 * @throws IOException
		 *             if the stream is closed.
		 * 
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public int read(final byte[] cbuf) throws IOException {
			return read(cbuf, 0, cbuf.length);
		}
		
		/**
		 * Read bytes into a portion of an array. This method will block until some input is available, an I/O error
		 * occurs, or the end of the stream is reached.
		 * 
		 * @param cbuf
		 *            Destination buffer.
		 * @param off
		 *            Offset at which to start storing bytes.
		 * @param len
		 *            Maximum number of bytes to read.
		 * @return The number of bytes read, or -1 if the end of the stream has been reached
		 * @throws IOException
		 *             if the stream is closed.
		 * 
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public int read(final byte[] cbuf,
		                final int off,
		                final int len) throws IOException {
			while (true) {
				synchronized (CircularByteBuffer.this) {
					if (CircularByteBuffer.this.inputStreamClosed) {
						throw new IOException("InputStream has been closed; cannot read from a closed InputStream.");
					}
					final int available = CircularByteBuffer.this.available();
					if (available > 0) {
						final int length = Math.min(len, available);
						final int firstLen = Math.min(length, CircularByteBuffer.this.buffer.length
						        - CircularByteBuffer.this.readPosition);
						final int secondLen = length - firstLen;
						System.arraycopy(CircularByteBuffer.this.buffer, CircularByteBuffer.this.readPosition, cbuf,
						                 off, firstLen);
						if (secondLen > 0) {
							System.arraycopy(CircularByteBuffer.this.buffer, 0, cbuf, off + firstLen, secondLen);
							CircularByteBuffer.this.readPosition = secondLen;
						} else {
							CircularByteBuffer.this.readPosition += length;
						}
						if (CircularByteBuffer.this.readPosition == CircularByteBuffer.this.buffer.length) {
							CircularByteBuffer.this.readPosition = 0;
						}
						ensureMark();
						return length;
					} else if (CircularByteBuffer.this.outputStreamClosed) {
						return -1;
					}
				}
				try {
					Thread.sleep(100);
				} catch (final Exception x) {
					throw new IOException("Blocking read operation interrupted.");
				}
			}
		}
		
		/**
		 * Reset the stream. If the stream has been marked, then attempt to reposition i at the mark. If the stream has
		 * not been marked, or more bytes than the readAheadLimit have been read, this method has no effect.
		 * 
		 * @throws IOException
		 *             if the stream is closed.
		 * 
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public void reset() throws IOException {
			synchronized (CircularByteBuffer.this) {
				if (CircularByteBuffer.this.inputStreamClosed) {
					throw new IOException("InputStream has been closed; cannot reset a closed InputStream.");
				}
				CircularByteBuffer.this.readPosition = CircularByteBuffer.this.markPosition;
			}
		}
		
		/**
		 * Skip bytes. This method will block until some bytes are available, an I/O error occurs, or the end of the
		 * stream is reached.
		 * 
		 * @param n
		 *            The number of bytes to skip
		 * @return The number of bytes actually skipped
		 * @throws IOException
		 *             if the stream is closed.
		 * @throws IllegalArgumentException
		 *             if n is negative.
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public long skip(final long n) throws IOException, IllegalArgumentException {
			while (true) {
				synchronized (CircularByteBuffer.this) {
					if (CircularByteBuffer.this.inputStreamClosed) {
						throw new IOException("InputStream has been closed; cannot skip bytes on a closed InputStream.");
					}
					final int available = CircularByteBuffer.this.available();
					if (available > 0) {
						final int length = Math.min((int) n, available);
						final int firstLen = Math.min(length, CircularByteBuffer.this.buffer.length
						        - CircularByteBuffer.this.readPosition);
						final int secondLen = length - firstLen;
						if (secondLen > 0) {
							CircularByteBuffer.this.readPosition = secondLen;
						} else {
							CircularByteBuffer.this.readPosition += length;
						}
						if (CircularByteBuffer.this.readPosition == CircularByteBuffer.this.buffer.length) {
							CircularByteBuffer.this.readPosition = 0;
						}
						ensureMark();
						return length;
					} else if (CircularByteBuffer.this.outputStreamClosed) {
						return 0;
					}
				}
				try {
					Thread.sleep(100);
				} catch (final Exception x) {
					throw new IOException("Blocking read operation interrupted.");
				}
			}
		}
	}
	
	/**
	 * Class for writing to a circular byte buffer. If the buffer is full, the writes will either block until there is
	 * some space available or throw an IOException based on the CircularByteBuffer's preference.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	protected class CircularByteBufferOutputStream extends OutputStream {
		
		/**
		 * Close the stream, flushing it first. This will cause the InputStream associated with this circular buffer to
		 * read its last bytes once it empties the buffer. Once a stream has been closed, further write() or flush()
		 * invocations will cause an IOException to be thrown. Closing a previously-closed stream, however, has no
		 * effect.
		 * 
		 * @throws IOException
		 *             never.
		 * 
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public void close() throws IOException {
			synchronized (CircularByteBuffer.this) {
				if (!CircularByteBuffer.this.outputStreamClosed) {
					flush();
				}
				CircularByteBuffer.this.outputStreamClosed = true;
			}
		}
		
		/**
		 * Flush the stream.
		 * 
		 * @throws IOException
		 *             if the stream is closed.
		 * 
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public void flush() throws IOException {
			synchronized (CircularByteBuffer.this) {
				if (CircularByteBuffer.this.outputStreamClosed) {
					throw new IOException("OutputStream has been closed; cannot flush a closed OutputStream.");
				}
				if (CircularByteBuffer.this.inputStreamClosed) {
					throw new IOException("Buffer closed by inputStream; cannot flush.");
				}
			}
			// this method needs to do nothing
		}
		
		/**
		 * Write an array of bytes. If the buffer allows blocking writes, this method will block until all the data has
		 * been written rather than throw an IOException.
		 * 
		 * @param cbuf
		 *            Array of bytes to be written
		 * @throws IOException
		 *             if the stream is closed, or the write is interrupted.
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public void write(final byte[] cbuf) throws IOException {
			write(cbuf, 0, cbuf.length);
		}
		
		/**
		 * Write a portion of an array of bytes. If the buffer allows blocking writes, this method will block until all
		 * the data has been written rather than throw an IOException.
		 * 
		 * @param cbuf
		 *            Array of bytes
		 * @param off
		 *            Offset from which to start writing bytes
		 * @param len
		 *            - Number of bytes to write
		 * @throws IOException
		 *             if the stream is closed, or the write is interrupted.
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public void write(final byte[] cbuf,
		                  final int off,
		                  final int len) throws IOException {
			int localOff = off;
			int localLen = len;
			while (localLen > 0) {
				synchronized (CircularByteBuffer.this) {
					if (CircularByteBuffer.this.outputStreamClosed) {
						throw new IOException("OutputStream has been closed; cannot write to a closed OutputStream.");
					}
					if (CircularByteBuffer.this.inputStreamClosed) {
						throw new IOException("Buffer closed by InputStream; cannot write to a closed buffer.");
					}
					int spaceLeft = spaceLeft();
					while (CircularByteBuffer.this.infinite && (spaceLeft < localLen)) {
						resize();
						spaceLeft = spaceLeft();
					}
					if (!CircularByteBuffer.this.blockingWrite && (spaceLeft < localLen)) {
						throw new BufferOverflowException("CircularByteBuffer is full; cannot write " + localLen
						        + " bytes");
					}
					final int realLen = Math.min(localLen, spaceLeft);
					final int firstLen = Math.min(realLen, CircularByteBuffer.this.buffer.length
					        - CircularByteBuffer.this.writePosition);
					final int secondLen = Math.min(realLen - firstLen, CircularByteBuffer.this.buffer.length
					        - CircularByteBuffer.this.markPosition - 1);
					final int written = firstLen + secondLen;
					if (firstLen > 0) {
						System.arraycopy(cbuf, localOff, CircularByteBuffer.this.buffer,
						                 CircularByteBuffer.this.writePosition, firstLen);
					}
					if (secondLen > 0) {
						System.arraycopy(cbuf, localOff + firstLen, CircularByteBuffer.this.buffer, 0, secondLen);
						CircularByteBuffer.this.writePosition = secondLen;
					} else {
						CircularByteBuffer.this.writePosition += written;
					}
					if (CircularByteBuffer.this.writePosition == CircularByteBuffer.this.buffer.length) {
						CircularByteBuffer.this.writePosition = 0;
					}
					localOff += written;
					localLen -= written;
				}
				if (localLen > 0) {
					try {
						Thread.sleep(100);
					} catch (final Exception x) {
						throw new IOException("Waiting for available space in buffer interrupted.");
					}
				}
			}
		}
		
		/**
		 * Write a single byte. The byte to be written is contained in the 8 low-order bits of the given integer value;
		 * the 24 high-order bits are ignored. If the buffer allows blocking writes, this method will block until all
		 * the data has been written rather than throw an IOException.
		 * 
		 * @param c
		 *            number of bytes to be written
		 * @throws IOException
		 *             if the stream is closed, or the write is interrupted.
		 * @since ostermillerutils 1.00.00
		 */
		@Override
		public void write(final int c) throws IOException {
			boolean written = false;
			while (!written) {
				synchronized (CircularByteBuffer.this) {
					if (CircularByteBuffer.this.outputStreamClosed) {
						throw new IOException("OutputStream has been closed; cannot write to a closed OutputStream.");
					}
					if (CircularByteBuffer.this.inputStreamClosed) {
						throw new IOException("Buffer closed by InputStream; cannot write to a closed buffer.");
					}
					int spaceLeft = spaceLeft();
					while (CircularByteBuffer.this.infinite && (spaceLeft < 1)) {
						resize();
						spaceLeft = spaceLeft();
					}
					if (!CircularByteBuffer.this.blockingWrite && (spaceLeft < 1)) {
						throw new BufferOverflowException("CircularByteBuffer is full; cannot write 1 byte");
					}
					if (spaceLeft > 0) {
						CircularByteBuffer.this.buffer[CircularByteBuffer.this.writePosition] = (byte) (c & 0xff);
						CircularByteBuffer.this.writePosition++;
						if (CircularByteBuffer.this.writePosition == CircularByteBuffer.this.buffer.length) {
							CircularByteBuffer.this.writePosition = 0;
						}
						written = true;
					}
				}
				if (!written) {
					try {
						Thread.sleep(100);
					} catch (final Exception x) {
						throw new IOException("Waiting for available space in buffer interrupted.");
					}
				}
			}
		}
	}
	
	/**
	 * The default size for a circular byte buffer.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	private final static int   DEFAULT_SIZE       = 1024;
	/**
	 * A buffer that will grow as things are added.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public final static int    INFINITE_SIZE      = -1;
	/**
	 * The circular buffer.
	 * <p>
	 * The actual capacity of the buffer is one less than the actual length of the buffer so that an empty and a full
	 * buffer can be distinguished. An empty buffer will have the markPostion and the writePosition equal to each other.
	 * A full buffer will have the writePosition one less than the markPostion.
	 * <p>
	 * There are three important indexes into the buffer: The readPosition, the writePosition, and the markPosition. If
	 * the InputStream has never been marked, the readPosition and the markPosition should always be the same. The bytes
	 * available to be read go from the readPosition to the writePosition, wrapping around the end of the buffer. The
	 * space available for writing goes from the write position to one less than the markPosition, wrapping around the
	 * end of the buffer. The bytes that have been saved to support a reset() of the InputStream go from markPosition to
	 * readPosition, wrapping around the end of the buffer.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	protected byte[]           buffer;
	/**
	 * Index of the first byte available to be read.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	protected volatile int     readPosition       = 0;
	/**
	 * Index of the first byte available to be written.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	protected volatile int     writePosition      = 0;
	
	/**
	 * Index of the first saved byte. (To support stream marking.)
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	protected volatile int     markPosition       = 0;
	
	/**
	 * Number of bytes that have to be saved to support mark() and reset() on the InputStream.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	protected volatile int     markSize           = 0;
	/** If this buffer is infinite (should resize itself when full). @since ostermillerutils 1.00.00 */
	protected volatile boolean infinite           = false;
	
	/**
	 * True if a write to a full buffer should block until the buffer has room, false if the write method should throw
	 * an IOException. @since ostermillerutils 1.00.00
	 */
	protected boolean          blockingWrite      = true;
	/**
	 * The InputStream that can empty this buffer.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	protected InputStream      in                 = new CircularByteBufferInputStream();
	
	/** true if the close() method has been called on the InputStream. @since ostermillerutils 1.00.00 */
	protected boolean          inputStreamClosed  = false;
	
	/**
	 * The OutputStream that can fill this buffer.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	protected OutputStream     out                = new CircularByteBufferOutputStream();
	
	/** true if the close() method has been called on the OutputStream. @since ostermillerutils 1.00.00 */
	protected boolean          outputStreamClosed = false;
	
	/**
	 * Create a new buffer with a default capacity. Writing to a full buffer will block until space is available rather
	 * than throw an exception.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public CircularByteBuffer() {
		this(DEFAULT_SIZE, true);
	}
	
	/**
	 * Create a new buffer with a default capacity and given blocking behavior.
	 * 
	 * @param blockingWrite
	 *            true writing to a full buffer should block until space is available, false if an exception should be
	 *            thrown instead.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public CircularByteBuffer(final boolean blockingWrite) {
		this(DEFAULT_SIZE, blockingWrite);
	}
	
	/**
	 * Create a new buffer with given capacity. Writing to a full buffer will block until space is available rather than
	 * throw an exception.
	 * <p>
	 * Note that the buffer may reserve some bytes for special purposes and capacity number of bytes may not be able to
	 * be written to the buffer.
	 * <p>
	 * Note that if the buffer is of INFINITE_SIZE it will neither block or throw exceptions, but rather grow without
	 * bound.
	 * 
	 * @param size
	 *            desired capacity of the buffer in bytes or CircularByteBuffer.INFINITE_SIZE.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public CircularByteBuffer(final int size) {
		this(size, true);
	}
	
	/**
	 * Create a new buffer with the given capacity and blocking behavior.
	 * <p>
	 * Note that the buffer may reserve some bytes for special purposes and capacity number of bytes may not be able to
	 * be written to the buffer.
	 * <p>
	 * Note that if the buffer is of INFINITE_SIZE it will neither block or throw exceptions, but rather grow without
	 * bound.
	 * 
	 * @param size
	 *            desired capacity of the buffer in bytes or CircularByteBuffer.INFINITE_SIZE.
	 * @param blockingWrite
	 *            true writing to a full buffer should block until space is available, false if an exception should be
	 *            thrown instead.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public CircularByteBuffer(final int size, final boolean blockingWrite) {
		if (size == INFINITE_SIZE) {
			this.buffer = new byte[DEFAULT_SIZE];
			this.infinite = true;
		} else {
			this.buffer = new byte[size];
			this.infinite = false;
		}
		this.blockingWrite = blockingWrite;
	}
	
	/**
	 * Bytes available for reading.
	 * 
	 * @return the int
	 * @since ostermillerutils 1.00.00
	 */
	private int available() {
		if (this.readPosition <= this.writePosition) {
			// any space between the first read and
			// the first write is available. In this case i
			// is all in one piece.
			return (this.writePosition - this.readPosition);
		}
		// space at the beginning and end.
		return (this.buffer.length - (this.readPosition - this.writePosition));
	}
	
	/**
	 * Make this buffer ready for reuse. The contents of the buffer will be cleared and the streams associated with this
	 * buffer will be reopened if they had been closed.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public void clear() {
		synchronized (this) {
			this.readPosition = 0;
			this.writePosition = 0;
			this.markPosition = 0;
			this.outputStreamClosed = false;
			this.inputStreamClosed = false;
		}
	}
	
	/**
	 * If we have passed the markSize reset the mark so that the space can be used.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	private void ensureMark() {
		if (marked() > this.markSize) {
			this.markPosition = this.readPosition;
			this.markSize = 0;
		}
	}
	
	/**
	 * Get number of bytes that are available to be read.
	 * <p>
	 * Note that the number of bytes available plus the number of bytes free may not add up to the capacity of this
	 * buffer, as the buffer may reserve some space for other purposes.
	 * 
	 * @return the size in bytes of this buffer
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public int getAvailable() {
		synchronized (this) {
			return available();
		}
	}
	
	/**
	 * Retrieve a InputStream that can be used to empty this buffer.
	 * <p>
	 * This InputStream supports marks at the expense of the buffer size.
	 * 
	 * @return the consumer for this buffer.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public InputStream getInputStream() {
		return this.in;
	}
	
	/**
	 * Retrieve a OutputStream that can be used to fill this buffer.
	 * <p>
	 * Write methods may throw a BufferOverflowException if the buffer is not large enough. A large enough buffer size
	 * must be chosen so that this does not happen or the caller must be prepared to catch the exception and try again
	 * once part of the buffer has been consumed.
	 * 
	 * 
	 * @return the producer for this buffer.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public OutputStream getOutputStream() {
		return this.out;
	}
	
	/**
	 * Get the capacity of this buffer.
	 * <p>
	 * Note that the number of bytes available plus the number of bytes free may not add up to the capacity of this
	 * buffer, as the buffer may reserve some space for other purposes.
	 * 
	 * @return the size in bytes of this buffer
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public int getSize() {
		synchronized (this) {
			return this.buffer.length;
		}
	}
	
	/**
	 * Get the number of bytes this buffer has free for writing.
	 * <p>
	 * Note that the number of bytes available plus the number of bytes free may not add up to the capacity of this
	 * buffer, as the buffer may reserve some space for other purposes.
	 * 
	 * @return the available space in bytes of this buffer
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	public int getSpaceLeft() {
		synchronized (this) {
			return spaceLeft();
		}
	}
	
	/**
	 * Bytes saved for supporting marks.
	 * 
	 * @return the int
	 * @since ostermillerutils 1.00.00
	 */
	private int marked() {
		if (this.markPosition <= this.readPosition) {
			// any space between the markPosition and
			// the first write is marked. In this case i
			// is all in one piece.
			return (this.readPosition - this.markPosition);
		}
		// space at the beginning and end.
		return (this.buffer.length - (this.markPosition - this.readPosition));
	}
	
	/**
	 * double the size of the buffer.
	 * 
	 * @since ostermillerutils 1.00.00
	 */
	private void resize() {
		final byte[] newBuffer = new byte[this.buffer.length * 2];
		final int marked = marked();
		final int available = available();
		if (this.markPosition <= this.writePosition) {
			// any space between the mark and
			// the first write needs to be saved.
			// In this case it is all in one piece.
			final int length = this.writePosition - this.markPosition;
			System.arraycopy(this.buffer, this.markPosition, newBuffer, 0, length);
		} else {
			final int length1 = this.buffer.length - this.markPosition;
			System.arraycopy(this.buffer, this.markPosition, newBuffer, 0, length1);
			final int length2 = this.writePosition;
			System.arraycopy(this.buffer, 0, newBuffer, length1, length2);
		}
		this.buffer = newBuffer;
		this.markPosition = 0;
		this.readPosition = marked;
		this.writePosition = marked + available;
	}
	
	/**
	 * Space available in the buffer which can be written.
	 * 
	 * @return the int
	 * @since ostermillerutils 1.00.00
	 */
	private int spaceLeft() {
		if (this.writePosition < this.markPosition) {
			// any space between the first write and
			// the mark except one byte is available.
			// In this case it is all in one piece.
			return (this.markPosition - this.writePosition - 1);
		}
		// space at the beginning and end.
		return ((this.buffer.length - 1) - (this.writePosition - this.markPosition));
	}
}
