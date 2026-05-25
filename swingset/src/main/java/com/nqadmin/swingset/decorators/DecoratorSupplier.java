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
package com.nqadmin.swingset.decorators;

import java.util.function.Supplier;

/**
 * Like a factory for Decorator.
 */
public class DecoratorSupplier
{
	private final Supplier<Decorator> supplier;
	private final Decorator.DecoratorStyle style;

	/** create a decorator supplier
	 * @param supplier */
	public DecoratorSupplier(Supplier<Decorator> supplier)
	{
		this(supplier, supplier.get().getStyle());
	}

	/** create a decorator supplier
	 * @param supplier
	 * @param style
	 */
	public DecoratorSupplier(Supplier<Decorator> supplier, Decorator.DecoratorStyle style)
	{
		this.supplier = supplier;
		this.style = style;
	}
	
	/**
	 * Create and return a decorator.
	 * @return decorator
	 */
	public Decorator get() {
		return supplier.get();
	}

	/**
	 * Decorator style.
	 * @return decorator style
	 */
	public Decorator.DecoratorStyle getStyle() {
		return style;
	}
}
