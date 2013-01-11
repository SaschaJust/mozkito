/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.ioda.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.util.LinkedList;

import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class CachingSocketImpl.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class CachingSocketImpl extends SocketImpl {
	
	/**
	 * Instantiates a new caching socket impl.
	 */
	public CachingSocketImpl() {
		// PRECONDITIONS
		
		try {
			// stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#accept(java.net.SocketImpl)
	 */
	@Override
	protected void accept(final SocketImpl s) throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#available()
	 */
	@Override
	protected int available() throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return 0;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#bind(java.net.InetAddress, int)
	 */
	@Override
	protected void bind(final InetAddress host,
	                    final int port) throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#close()
	 */
	@Override
	protected void close() throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#connect(java.net.InetAddress, int)
	 */
	@Override
	protected void connect(final InetAddress address,
	                       final int port) throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#connect(java.net.SocketAddress, int)
	 */
	@Override
	protected void connect(final SocketAddress address,
	                       final int timeout) throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#connect(java.lang.String, int)
	 */
	@Override
	protected void connect(final String host,
	                       final int port) throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#create(boolean)
	 */
	@Override
	protected void create(final boolean stream) throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getClassName() {
		// PRECONDITIONS
		
		final StringBuilder builder = new StringBuilder();
		
		try {
			final LinkedList<Class<?>> list = new LinkedList<Class<?>>();
			Class<?> clazz = getClass();
			list.add(clazz);
			
			while ((clazz = clazz.getEnclosingClass()) != null) {
				list.addFirst(clazz);
			}
			
			for (final Class<?> c : list) {
				if (builder.length() > 0) {
					builder.append('.');
				}
				
				builder.append(c.getSimpleName());
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
			Condition.notNull(builder,
			                  "Local variable '%s' in '%s:%s'.", "builder", getClass().getSimpleName(), "getHandle"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#getInputStream()
	 */
	@Override
	protected InputStream getInputStream() throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketOptions#getOption(int)
	 */
	@Override
	public Object getOption(final int optID) throws SocketException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#getOutputStream()
	 */
	@Override
	protected OutputStream getOutputStream() throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#listen(int)
	 */
	@Override
	protected void listen(final int backlog) throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketImpl#sendUrgentData(int)
	 */
	@Override
	protected void sendUrgentData(final int data) throws IOException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.net.SocketOptions#setOption(int, java.lang.Object)
	 */
	@Override
	public void setOption(final int optID,
	                      final Object value) throws SocketException {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
}
