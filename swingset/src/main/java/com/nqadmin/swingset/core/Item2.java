/* *****************************************************************************
 * Copyright (C) 2025, Ernie R Rael. All rights reserved.
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
package com.nqadmin.swingset.core;

import java.util.Objects;

import com.nqadmin.swingset.models.SSListItem;

/**
 * An item that acts kind of like a standalone SSListItem.
 *
 * @param <K>
 * @param <D>
 * @param <D2> 
 */
public class Item2<K,D,D2>
{
	private final K key;
	private final D displayValue;
	private final D2 displayValue2;

	private static final Object NO_D2 = new Object();

	/**
	 * Create immutable item.
	 * @param key
	 * @param displayValue
	 * @param displayValue2 
	 */
	public Item2(K key, D displayValue, D2 displayValue2)
	{
		this.key = key;
		this.displayValue = displayValue;
		this.displayValue2 = displayValue2;
	}

	/**
	 * Create immutable item which does not have a displayValue2.
	 * @param key
	 * @param displayValue 
	 */
	@SuppressWarnings("unchecked")
	Item2(K key, D displayValue)
	{
		this(key, displayValue, (D2)NO_D2);
	}

	/**
	 * Create immutable item based on SSListItem.
	 * @param listItem
	 * @param hasDisplayValue2 
	 */
	@SuppressWarnings("unchecked")
	Item2(SSListItem listItem, boolean hasDisplayValue2)
	{
		// li = (ListItem0)listItem;
		// key = (K)li.getElem(OptionMappingSwingModel.KEY_IDX);
		// displayValue = (D)li.getElem(OptionMappingSwingModel.DISP_IDX);
		// handle D2
		this(null, null, null);
	}

	/**
	 * key getter.
	 * @return 
	 */
	public K getKey()
	{
		return key;
	}

	/**
	 * displayValue getter.
	 * @return 
	 */
	public D getDisplayValue()
	{
		return displayValue;
	}

	/**
	 * displayValue2 getter.
	 * Exception if does not have a displayValue2
	 * @return 
	 */
	public D2 getDisplayValue2()
	{
		if (displayValue2 == NO_D2)
			throw new IllegalStateException("Item does not have displayValue2");
		return displayValue2;
	}

	/**
	 * Check if this has a displayValue2
	 * @return true if there is a displayValue2
	 */
	public boolean hasDisplayValue2()
	{
		return displayValue2 != NO_D2;
	}

	/** hashCode.
	 * @return 
	 */
	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this.key);
		hash = 67 * hash + Objects.hashCode(this.displayValue);
		hash = 67 * hash + Objects.hashCode(this.displayValue2);
		return hash;
	}

	/**
	 * equals.
	 * @param obj
	 * @return 
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Item2<?, ?, ?> other = (Item2<?, ?, ?>) obj;
		if (!Objects.equals(this.key, other.key))
			return false;
		if (!Objects.equals(this.displayValue, other.displayValue))
			return false;
		return Objects.equals(this.displayValue2, other.displayValue2);
	}

}

/*
public record Item2<K,D,D2>(K getKey, D getDisplayValue, D2 getDisplayValue2)
{
	private static final Object NO_D2 = new Object();

	@SuppressWarnings("unchecked")
	public Item2(K getKey, D getDisplayValue)
	{
		this(getKey, getDisplayValue, (D2)NO_D2);
	}

	@SuppressWarnings("unchecked")
	Item2(SSListItem listItem, boolean hasDisplayValue2)
	{
		// li = (ListItem0)listItem;
		// key = (K)li.getElem(OptionMappingSwingModel.KEY_IDX);
		// displayValue = (D)li.getElem(OptionMappingSwingModel.DISP_IDX);
		// handle D2
		this(null, null, null);
	}

	@Override
	public D2 getDisplayValue2()
	{
		if (getDisplayValue2 == NO_D2)
			throw new IllegalStateException("Item2 does not have displayValue2");
		return getDisplayValue2;
	}

	public boolean hasDisplayValue2()
	{
		return getDisplayValue2 != NO_D2;
	}
}
 */
