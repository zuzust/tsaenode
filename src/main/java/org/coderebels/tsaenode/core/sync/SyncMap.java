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

import java.util.concurrent.ConcurrentHashMap;


/**
 * Map of nodes involved in running synchronization sessions
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class SyncMap {

  private ConcurrentHashMap<String, Boolean> data;


  public SyncMap() {
    data = new ConcurrentHashMap<String, Boolean>();
  }


  /**
   * Checks if specified node is available for a new synchronization session
   * @param node Node to check
   * @return true if the specified node isn't involved in a running synchronization session; false otherwise
   */
  public boolean isAvailable(Peer node) {
    String nodeId = node.getId();
    return data.containsKey( nodeId ) ? data.get( nodeId ) : true;
  }

  public void setAvailable(Peer node) {
    String nodeId = node.getId();
    data.put( nodeId, true );
  }

  public void setUnavailable(Peer node) {
    String nodeId = node.getId();
    data.put( nodeId, false );
  }

}