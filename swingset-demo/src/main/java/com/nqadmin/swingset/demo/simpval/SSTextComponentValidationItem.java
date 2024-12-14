/*
 * Copyright 2010-2019 Tim Boudreau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nqadmin.swingset.demo.simpval;

import javax.swing.SwingUtilities;
import org.netbeans.validation.api.ui.*;

/*
import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
*/
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.ui.swing.SwingValidationGroup;

/**
 * Hook into SimpleValidation framework; validate on demand, not as a listener.
 * Useful for the way SwingSet splits validation and decoration.
 * @author Tim Boudreau
 */
// TODO: Make this independent of Document, just use a string?
//		 Set the string on every change to text, after change needs validation?
public class SSTextComponentValidationItem extends ValidationListener<JTextComponent>
        //implements DocumentListener, FocusListener, Runnable
{
    private Validator<Document> validator;
    private boolean hasFatalProblem = false;

    public SSTextComponentValidationItem(JTextComponent component,
										 ValidationStrategy strategy,
										 ValidationUI validationUI,
										 Validator<Document> validator
	) {
        super(JTextComponent.class, validationUI, component);
        this.validator = validator;
        if (strategy == null) {
            throw new NullPointerException("strategy null");
        }
/*
        component.addPropertyChangeListener("enabled", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                performValidation();
            }
        });
        switch (strategy) {
            case DEFAULT:
            case ON_CHANGE_OR_ACTION:
                component.getDocument().addDocumentListener(this);
                break;
            case INPUT_VERIFIER:
                component.setInputVerifier( new InputVerifier() {
                    @Override
                    public boolean verify(JComponent input) {
						performValidation();
						return !hasFatalProblem;
                    }
                });
                break;
            case ON_FOCUS_LOSS:
                component.addFocusListener(this);
                break;
        }
*/
        performValidation(); // Make sure any initial errors are discovered immediately.
    }

	/** just run the validator, no decoration */
	public boolean validate() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> validate());
		}
        JTextComponent component = getTarget();
		Problems ps = new Problems();
        validator.validate(ps, SwingValidationGroup.nameForComponent(component), component.getDocument());
		return !ps.hasFatal();
	}

	protected boolean hasFatalProblem() {
		return hasFatalProblem;
	}

    @Override
    protected final void performValidation(Problems ps){
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> performValidation(ps));
		}
        JTextComponent component = getTarget();
        if (!component.isEnabled()) {
            return;
        }
        validator.validate(ps, SwingValidationGroup.nameForComponent(component), component.getDocument());
        hasFatalProblem = ps.hasFatal();
    }

/*
    @Override
    public void focusLost(FocusEvent e) {
        performValidation();
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        removeUpdate(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        //Documents can be legally updated from another thread,
        //but we will not run validation outside the EDT
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(this);
        } else {
            performValidation();
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        removeUpdate(e);
    }

    // See removeUpdate..
    @Override
    public void run() {
        performValidation();
    }
*/
}
