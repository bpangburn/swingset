/* *****************************************************************************
 * Copyright (C) 2024, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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

package com.nqadmin.swingset.decorators;

import com.nqadmin.swingset.utils.SSComponentInterface;

/**
 * Component decorator gives a visual indication of the component state.
 * Typically both component and its validator are checked.
 * <p>
 * TODO: eventually define a factory to create these
 */
public interface Decorator
{
	/** Decorate the component using current state.
	 * The current state is typically obtained by getComponent().isDataValid()
	 * @return true if the data is valid
	 */
	boolean decorate();

	// TODO: this method would be useful to only calculate isValid once
	// /** Decorate the component using isValid state.
	//  * The default ignores the argument and calls decorate.
	//  * @param isValid true is component is valid
	//  */
	// default void decorate(boolean isValid) { decorate(); }

	/** Install this decorator into the component. Installs listeners
	 * @param component the componenet
	 */
	void install(SSComponentInterface component);

	/** Remove decorator/listeners from component. */
	void uninstall();
    
	/**
	 * A decorator that does nothing.
	 */
	public static Decorator nullDecorator = new Decorator() {
		/** {@inheritDoc} */
		@Override public boolean decorate() { return true; }

		/** {@inheritDoc} */
		@Override public void install(SSComponentInterface comp) { }

		/** {@inheritDoc} */
		@Override public void uninstall() { }
	};
}
