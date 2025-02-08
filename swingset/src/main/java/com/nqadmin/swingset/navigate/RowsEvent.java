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
package com.nqadmin.swingset.navigate;

import java.util.EnumSet;
import java.util.EventObject;
import java.util.Set;

import javax.sql.RowSet;

import com.nqadmin.swingset.datasources.RSC;
import com.nqadmin.swingset.navigate.RowsModelEventHandling.RowsEventSource;

import static com.nqadmin.swingset.navigate.RowsModel.logger;
import static com.nqadmin.swingset.utils.SSUtils.isJunit;
import static com.nqadmin.swingset.utils.SSUtils.objectID;
import static com.nqadmin.swingset.utils.SSUtils.sf;
import static java.lang.System.Logger.Level.*;

/**
 * An Event object that bundles events generated through  one or more operatrions
 * on a {@link javax.sql.RowSet}.
 * The event can originate from a {@link RowsAction}, from an
 * {@link RSC}/SSComponent, or
 * from something else, use {@link #getKindOperator} to determine the kind;
 * and use {@link #getOperComponent()} or {@link #getOperAct()} for
 * the specific operator.
 * The property {@link RowSetEventType} indicates which RowSet events are bundled.
 * Use the RowsModel to get the RowSet.
 */
@SuppressWarnings("serial")
public class RowsEvent extends EventObject implements RowsModelEvent
{

	/** The type of NaviagionRowSetEvent events */
	public enum RowSetEventType {
		/** See {@link javax.sql.RowSetListener#cursorMoved(javax.sql.RowSetEvent) } */
		CURSOR_MOVED,
		/** See {@link javax.sql.RowSetListener#rowChanged(javax.sql.RowSetEvent)} */
		ROW_CHANGED,
		/** See {@link javax.sql.RowSetListener#rowSetChanged(javax.sql.RowSetEvent)} */
		ROW_SET_CHANGED,
	}

	/** The type of RowSet operation which caused this event. */
	// TODO: originatingComponentType
	// TODO: KindOperator ???
	public enum OperatorKind {
		/** A {@link RowsAction}, typically a button push, generated this event */
		ACTION,
		/** An SSComponent's action generated this event, like user input. */
		COMPONENT,
		/** Other bracketed event, like SSSyncManger. */
		OTHER,
		/** Direct RowSet operation; not bracketed.
		 * Alternate SSSyncManager handling or outside of SS. */
		ANON,
		/** Initialization that should never show up in use. */
		UNKNOWN,
	}

	private static int generation;

	/** NOTE: rowSetEventTypes may "grow" before dispatch */
	private final Set<RowSetEventType> rowSetEventTypes;
	private final int gen;

	/**
	 * An Event object generated for a RowsAction on a RowSet.
	 * @param source
	 * @param rs
	 * @param operatorKind
	 * @param operator Either a RowsAction or RSC/component; may be null
	 * @param rowSetEventTypes
	 */
	RowsEvent(RowsEventSource source, Set<RowSetEventType> rowSetEventTypes)
	{
		super(source);
		this.rowSetEventTypes = EnumSet.copyOf(rowSetEventTypes);
		this.gen = ++generation;

		switch (source.operatorKind()) {
		case COMPONENT -> {
			if (!(source.operator() instanceof RSC))
				throw new IllegalArgumentException(sf("%s must have a component"));
		}
		case ACTION -> {
			if (!(source.operator() instanceof RowsAction))
				throw new IllegalArgumentException(sf("%s must have an action"));
		}
		case null,default -> {}
		}
	}
	RowsEvent(RowsEventSource source, RowSetEventType rowSetEventType)
	{
		this(source, EnumSet.of(rowSetEventType));
	}

	boolean absorb(RowsEvent rev)
	{
		// UNKNOWN never matches
		if (getSource().operatorKind() == OperatorKind.UNKNOWN
				|| !getSource().equals(rev.getSource())) {
			return false;
		}

		if (isJunit())
			System.out.println("absorb: " + rev.rowSetEventTypes);
		else
			logger.log(TRACE, () -> sf("absorb: %s", rev.rowSetEventTypes));
		rowSetEventTypes.addAll(rev.rowSetEventTypes);
		return true;
	}

	// TODO: Other versions of absorb? Or maybe can absorb?

	/** {@inheritDoc } */
	@Override
	@SuppressWarnings("NonPublicExported")
	public final RowsEventSource getSource() {
		return (RowsEventSource) source;
	}

	/**
	 * A RowsModel.
	 * @return RowsModel that issued the event
	 */
	@Override
	public RowsModel getRowsModel() {
		return getSource().rowsModel();
	}

	/**
	 * Return the RowSet associated with these events.
	 * @return
	 */
	public RowSet getRowSet()
	{
		return getSource().rs();
	}

	/**
	 * The events generated by the navigation action.
	 * @return the events generated by the navigation action.
	 */
	public Set<RowSetEventType> getEventTypes()
	{
		return rowSetEventTypes;
	}

	/**
	 * The actions on the rowset that generated the events.
	 * Null if the OperatorKind is not a NavAction.
	 * 
	 * @return the RowsAction that generated the events.
	 */
	public RowsAction getOperAct()
	{
		if (getSource().operatorKind() != OperatorKind.ACTION)
			return null;
		return (RowsAction) getSource().operator();
	}

	/**
	 * The {@link RSC} that originated the event.
	 * Null if the OperatorKind is not an SSComponent.
	 * @return the component that originated the events
	 */
	public RSC getOperComponent()
	{
		if (getSource().operatorKind() != OperatorKind.COMPONENT)
			return null;
		return (RSC) getSource().operator();
	}

	/**
	 * The operator that originated the event;
	 * use {@link #getKindOperator() } to determine the kind.
	 * This is the operator no matter the OperatorKind.
	 * @return the object that originated the events
	 */
	public Object getOperAny()
	{
		return getSource().operator();
	}

	/**
	 * The source of the rowset operatorKind that generated the events.
	 * @return 
	 */
	public OperatorKind getKindOperator()
	{
		return getSource().operatorKind();
	}

	/** {@inheritDoc} */
	@Override
	public String toString()
	{
		return sf("RowsEvent{%d, %s, %s, %s, %s, %s}",
				gen,
				objectID(getSource().rowsModel()),
				objectID(getSource().rs()),
				getSource().operatorKind(),
				getSource().operator() instanceof RowsAction
						? getSource().operator() : objectID(getSource().operator()),
				rowSetEventTypes);
	}
}
