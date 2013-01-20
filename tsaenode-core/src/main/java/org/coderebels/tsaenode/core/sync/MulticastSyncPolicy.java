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
import java.util.Vector;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class MulticastSyncPolicy implements ISyncPolicy {

  private static Logger logger = LogManager.getLogger( MulticastSyncPolicy.class.getName() );


  public MulticastSyncPolicy() {}


  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.sync.ISyncPolicy#selectSyncNodes(java.util.List,
   * org.coderebels.tsaenode.core.sync.SyncMap)
   */
  @Override
  public synchronized List<Peer> selectSyncNodes(List<Peer> nodes, SyncMap syncMap) {
    logger.entry();
    logger.debug( "Selecting nodes to synchronize with..." );
    /*
     * 1) Select randomly a subset of peers available for synchronization: [ log_2(nodes.size) + 1 ] --> [ ( log_e(nodes.size) / log_e(2) ) + 1 ]
     * 2) Return a list of nodes containing the selected peers
     */
    List<Peer> syncNodes = new Vector<Peer>();

    if (!nodes.isEmpty()) {
      boolean connected = false;
      boolean synchronizing = false;

      int idx = 0;
      int n = nodes.size();
      int numSyncNodes = (int) Math.round( (Math.log(n) / Math.log(2)) + 1 );
      int attempts = (int) Math.floor( numSyncNodes * 1.5 );
      int addedNodes = 0;

      Peer node = null;
      Random rnd = new Random();

      do {
        idx = rnd.nextInt( n );
        node = nodes.get( idx );

        connected = node.isConnected();
        synchronizing = !syncMap.isAvailable( node );

        if (connected && !synchronizing) {
          syncMap.setUnavailable( node );
          syncNodes.add( node );
          addedNodes++;
        }

        attempts--;
      } while (addedNodes < numSyncNodes && attempts > 0);
    }

    return logger.exit( syncNodes );
  }

}