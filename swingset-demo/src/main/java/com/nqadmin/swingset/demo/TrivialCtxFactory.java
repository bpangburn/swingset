/* *****************************************************************************
 * Copyright (C) 2023, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Contributors:
 *   Prasanth R. Pasala
 *   Brian E. Pangburn
 *   Diego Gil
 *   Man "Bee" Vo
 *   Ernie R. Rael
 * ****************************************************************************/
package com.nqadmin.swingset.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

/**
 * Naming service that returns a DataSource.
 * This is for a demo/testing situation.
 */
public class TrivialCtxFactory implements InitialContextFactory
{
private Map<String,Object> jndiStuff = new HashMap<>();

public TrivialCtxFactory() throws NamingException
{
}

@Override
public Context getInitialContext(
        @SuppressWarnings("UseOfObsoleteCollectionType")
        java.util.Hashtable<?, ?> env)
throws NamingException
{
    return new NullContext() {
        @Override
        public Object lookup(String name) throws NamingException
        {
			Object obj = jndiStuff.get(name);
			if (obj == null)
				throw new NameNotFoundException(name);
			return obj;
        }

        @Override
		public void bind(String name, Object obj) throws NamingException
        {
			Objects.requireNonNull(name);
			Objects.requireNonNull(obj);
			jndiStuff.put(name, obj);
		}
    };
}

    /** Every {@linkplain Context} interface method throws Unsupported. */
    class NullContext implements Context
    {
        @Override public Object lookup(Name name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public Object lookup(String name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void bind(Name name, Object obj) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void bind(String name, Object obj) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void rebind(Name name, Object obj) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void rebind(String name, Object obj) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void unbind(Name name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void unbind(String name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void rename(Name oldName, Name newName) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void rename(String oldName, String newName) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public NamingEnumeration<NameClassPair> list(Name name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public NamingEnumeration<NameClassPair> list(String name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }
        
        @Override public NamingEnumeration<Binding> listBindings(Name name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public NamingEnumeration<Binding> listBindings(String name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void destroySubcontext(Name name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void destroySubcontext(String name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public Context createSubcontext(Name name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public Context createSubcontext(String name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public Object lookupLink(Name name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public Object lookupLink(String name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public NameParser getNameParser(Name name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public NameParser getNameParser(String name) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public Name composeName(Name name, Name prefix) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public String composeName(String name, String prefix) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public Object addToEnvironment(String propName, Object propVal) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public Object removeFromEnvironment(String propName) throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override@SuppressWarnings("UseOfObsoleteCollectionType")
        public java.util.Hashtable<?, ?> getEnvironment() throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public void close() throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }

        @Override public String getNameInNamespace() throws NamingException
        { throw new UnsupportedOperationException("Not supported yet."); }
    }

    
}
