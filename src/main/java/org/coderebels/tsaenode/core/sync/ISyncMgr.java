// Copyright (C) 2012 Carles Muiños
//
// This file is part of TSAEnode.
//
// TSAEnode is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// TSAEnode is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with TSAEnode.  If not, see <http://www.gnu.org/licenses/>.

package org.coderebels.tsaenode.core.sync;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.coderebels.tsaenode.core.common.Timestamp;
import org.coderebels.tsaenode.core.operation.Operation;
import org.coderebels.tsaenode.core.exception.SyncMgrException;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public interface ISyncMgr {

  /**
   * Starts a new anti-entropy session to synchronize all nodes within the group
   * @return true if done succesfully; false otherwise
   * @throws org.coderebels.tsaenode.core.exception.SyncMgrException
   */
  public boolean startSession() throws SyncMgrException;

  /**
   * Takes necessary actions to get the node synchronized
   * @param ops List of operations to be executed by local node
   * @param summary Summary vector of fellow node with whom synchronization session is established
   * @param acks Acknowledgement vector of fellow node with whom synchronization session is established
   * @return List of operations not known by fellow node with whom synchronization session is established
   * @throws org.coderebels.tsaenode.core.exception.SyncMgrException
   */
  public List<Operation> performSession(List<Operation> ops,
                        ConcurrentHashMap<String, Timestamp> summary,
                        ConcurrentHashMap<String, ConcurrentHashMap<String, Timestamp>> acks)
    throws SyncMgrException;

}