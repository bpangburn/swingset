/* *****************************************************************************
 * Copyright (C) 2026, Ernie R Rael. All rights reserved.
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
 * ****************************************************************************/
package com.nqadmin.swingset.utils;

import com.nqadmin.swingset.decorators.BackgroundDecorator;
import com.nqadmin.swingset.decorators.BorderDecorator;
import com.nqadmin.swingset.decorators.Decorator;
import com.nqadmin.swingset.decorators.DecoratorSupplier;

/**
 * This class is used internally initialize CentralLookup defaults.
 * There is no need to use this outside of the library.
 * The first access to the library could be something like:
 * {@snippet lang="java":
 *     // Add application defaults to CentralLookup.
 *     CentralLookup lkup = CentralLookup.getDefault();
 *     lkup.add(new DefaultSSDBSupport(dbConnection));
 *     // keep adding stuff as needed
 * }
 */
public class LookupDefaults
{
	private LookupDefaults() { }

	static boolean initialized;
	/**
	 * This is automatically called around first library use,
	 * not including CentralLookup, to initialize
	 * default CentralLookup elements that are required by the library
	 * and are not already present or setup by the application.
	 */
	public static void init() {
		if (initialized)
			return;
		CentralLookup lkup = CentralLookup.getDefault();

		//
		// There should be a DecoratorStyle.
		//
		Decorator.DecoratorStyle style = lkup.lookup(Decorator.DecoratorStyle.class);
		if (style == null)
			lkup.add(Decorator.DecoratorStyle.BORDER);

		//
		// There should be BORDER and BACKGROUND decorators.
		//
		var decos = lkup.lookupAll(DecoratorSupplier.class);

		boolean hasBorder = false;
		boolean hasBackground = false;
		for (var deco : decos) {
			if (deco.getStyle().equals(Decorator.DecoratorStyle.BORDER))
				hasBorder = true;
			if (deco.getStyle().equals(Decorator.DecoratorStyle.BACKGROUND))
				hasBackground = true;
		}
		if (!hasBorder)
			lkup.add(new DecoratorSupplier(() -> {return new BorderDecorator();}));
		if (!hasBackground)
			lkup.add(new DecoratorSupplier(() -> {return new BackgroundDecorator();}));

		//
		// There should be a BorderDecoratorPaint.
		//
		if (lkup.lookup(BorderDecorator.BorderDecoratorPaint.class) == null)
			lkup.add(new BorderDecorator.BorderDecoratorPaint());

		initialized = true;
	}
}