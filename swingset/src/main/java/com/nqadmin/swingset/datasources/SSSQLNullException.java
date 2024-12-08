 /* *****************************************************************************
 * The conditions in the above copyright notice apply to this copyright notice.
 * Additions and modifications made by Ernie R. Rael are
 * copyright (C) 2024, Ernie R. Rael. All rights reserved.
 * ****************************************************************************/

package com.nqadmin.swingset.datasources;

/**
 * Null used as database value where not allowed.
 */
@SuppressWarnings(value = "serial")
public class SSSQLNullException extends SSSQLException
{

	/**
	 * Construct an SQLException with given reason.
	 *
	 * @param reason description of the exception
	 */
	public SSSQLNullException(String reason)
	{
		super(reason);
	}
    
}
