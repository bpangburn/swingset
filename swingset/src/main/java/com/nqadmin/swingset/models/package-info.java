/*******************************************************************************
 * Copyright (C) 2003-2020, Prasanth R. Pasala, Brian E. Pangburn, & The Pangburn Group
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
 ******************************************************************************/

// package-info.java
//
// SwingSet - Open Toolkit For Making Swing Controls Database-Aware

/**
 * This package contains some very different kinds of model.
 * <ul>
 *	<li>models which manage a collection of objects to/from a database</li>
 *	<li>models, some derived from java.swing, for use with UI components</li>
 * </ul>
 * <h2>Database Collection Models</h2>
 * These models abstract away the storage specifics; they all 
 * implement {@link com.nqadmin.swingset.models.SSCollectionModel}.
 * they generally read and write to database.
 * There are subclasses that present data
 * as an array or set independent of how it might be stored in a data.
 * A model
 * could represent data from a single field or data resulting from a join.
 * <h2>UI support Models</h2>
 * These models, sub-classes of
 * {@link com.nqadmin.swingset.models.AbstractComboBoxListSwingModel},
 * generally revolve around managing
 * {@link com.nqadmin.swingset.models.SSListItem}s. An {@code SSListItem}
 * is generally the item which is visible in a {@code JComboBox}
 * or {@code JList}. Additionally these classes implement ListModel
 * and MutableComboBoxModel; they can be installed into either.
 * {@link com.nqadmin.swingset.models.GlazedListsOptionMappingInfo}
 * works with {@code GlazedLists} auto completion.
 * 
 * @since 4.0.0
 */

package com.nqadmin.swingset.models;
