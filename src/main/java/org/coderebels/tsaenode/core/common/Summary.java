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

package org.coderebels.tsaenode.core.common;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * Summary of the operations known by a node
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class Summary {

  private static Logger logger = LogManager.getLogger( Summary.class.getName() );

  private ConcurrentHashMap<String, Timestamp> data;


  public Summary() {
    data = new ConcurrentHashMap<String, Timestamp>();
  }


  /**
   * Updates the summary with a new timestamp
   * @param timestamp Timestamp to add if necessary
   */
  public synchronized void update(Timestamp timestamp) {
    logger.entry( timestamp );
    logger.debug( "Summarizing operation timestamp..." );

    String nodeId  = timestamp.getNodeId();
    Timestamp last = data.get( nodeId );

    if (last == null || last.compare(timestamp) < 0) {
      data.put( nodeId, timestamp );
    }

    logger.exit();
  }

  /**
   * Gets the last timestamp of the specified node
   * @param nodeId Node identifier
   * @return Last known timestamp from the specified node
   */
  public Timestamp getLast(String nodeId) {
    return data.get( nodeId );
  }

}
