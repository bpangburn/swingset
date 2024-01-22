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
package com.nqadmin.swingset.demo.simpval;

import com.nqadmin.swingset.decorators.Decorator;
import com.nqadmin.swingset.decorators.Validator;
import com.nqadmin.swingset.utils.SSComponentInterface;

/**
 * A combined validator/decorator using the Simple Validation framework.
 */
public class SimpValValidatorDecorator implements Decorator
{
	private final SSTextComponentValidationItem valItem;

	public SimpValValidatorDecorator(SSTextComponentValidationItem valItem) {
		this.valItem = valItem;
		this.validator = () -> valItem.validate();
	}

	@Override
	public boolean decorate() {
		valItem.performValidation();
		return !valItem.hasFatalProblem();
	}

	@Override
	public void install(SSComponentInterface component) {
		// No listeners to install
	}

	@Override
	public void uninstall() {
	}

	// TODO: this does decoration as well. Does SwingSet need a split architecture?
	private final Validator validator;

	/**
	 * Get the SwingSet validator.
	 * TODO: Note that this does decoration as well.
	 * @return 
	 */
	public Validator getValidator() {
		return validator;
	}
	
}
