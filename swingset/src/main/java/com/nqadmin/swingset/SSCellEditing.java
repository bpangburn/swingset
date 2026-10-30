/*
 * Portions created by Ernie Rael are
 * Copyright (C) 2026 Ernie Rael.  All Rights Reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Contributor(s): Ernie Rael <errael@raelity.com>
 */

package com.nqadmin.swingset;

import java.io.Serializable;

import dev.visdb.seesaw.table.SsCellEditing;

/**
 * The SsCellEditing interface specifies the methods the SSTableModel will use
 * to determine whether or not a given cell can be edited or if a user-specified
 * value for a cell is valid or invalid.
 */
public interface SSCellEditing extends SsCellEditing, Serializable {
  long serialVersionUID = -8081589343308592606L;
}
