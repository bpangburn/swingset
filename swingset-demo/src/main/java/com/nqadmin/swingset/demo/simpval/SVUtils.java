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

import com.nqadmin.swingset.SSTextField;
import com.nqadmin.swingset.utils.SSComponentInterface;
import java.util.concurrent.locks.Condition;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.validation.api.AbstractValidator;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ValidatorUtils;
import org.netbeans.validation.api.conversion.Converter;
import org.netbeans.validation.api.ui.ValidationItem;
import org.netbeans.validation.api.ui.ValidationStrategy;
import org.netbeans.validation.api.ui.swing.SwingComponentDecorationFactory;

/**
 * Helpers for working with Simple Validation framework.
 */
public class SVUtils {
	private SVUtils() { }

	/**
	 * Should probably be from a factory.
	 * @param comp
	 * @param validators
	 * @return 
	 */
	@SafeVarargs
	public static SSTextComponentValidationItem createDefaultTextValidator(
			JTextComponent comp, Validator<String>... validators
	) {
		Validator<String> merged = ValidatorUtils.merge(validators);
		Validator<Document> validator = Converter.find(
				String.class, Document.class).convert(merged);
		SSTextComponentValidationItem valItem = new SSTextComponentValidationItem(
				comp, ValidationStrategy.DEFAULT,
				SwingComponentDecorationFactory.getDefault().decorationFor(comp),
				validator);
		return valItem;
	}

	public static StringValidator getStringValidator(Function<String, Boolean> condition,
													 Supplier<String> problem) {
		return new StringValidator() {
			@Override
			public void validate(Problems problems, String compName, String model) {
				if(!condition.apply(model)) {
					problems.append(problem.get());
				}
			}
		};
	}

	public static ValidationItem decorator(JTextComponent jtc, StringValidator sval) {
		SSComponentInterface ssComp = (SSComponentInterface) jtc;
		SSTextComponentValidationItem textVali = SVUtils.createDefaultTextValidator(
				jtc, sval);
		SimpValValidatorDecorator deco = new SimpValValidatorDecorator(textVali);
		ssComp.getSSCommon().setDecorator(deco);
		ssComp.getSSCommon().setValidator(deco.getValidator());
		return textVali;
	}
	
}
