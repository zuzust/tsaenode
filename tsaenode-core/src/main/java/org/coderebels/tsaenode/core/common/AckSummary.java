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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * Summary of received messages of each node in the group
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class AckSummary {

  private static Logger logger = LogManager.getLogger( AckSummary.class.getName() );

  private ConcurrentHashMap<String, Summary> data;


  public AckSummary() {
    data = new ConcurrentHashMap<String, Summary>();
  }


  /**
   * Gets data from acknowledgement vector
   * @return Map of summary vectors of nodes in the group
   */
  public ConcurrentHashMap<String, ConcurrentHashMap<String, Timestamp>> getData() {
    logger.entry();
    logger.debug( "Retrieving acknowledgement vector data...");

    ConcurrentHashMap<String, ConcurrentHashMap<String, Timestamp>> ackData =
        new ConcurrentHashMap<String, ConcurrentHashMap<String, Timestamp>>();
    Summary summary = null;

    for (String nodeId : data.keySet()) {
      summary = data.get( nodeId );
      ackData.put( nodeId, summary.getData() );
    }

    return logger.exit( ackData );
  }

  /**
   * Add summary vector of the specified node
   * @param nodeId Node identifier
   * @param summary Summary vector of specified node
   */
  public void add(String nodeId, Summary summary) {
    data.put( nodeId, summary );
  }

  /**
   * Get summary vector of the specified node
   * @param nodeId Node identifier
   * @return Summary vector of specified node
   */
  public Summary get(String nodeId) {
    return data.get( nodeId );
  }

  /**
   * Update acknowledgement vector based on another ack vector information
   * @param acks Acknowledgement vector
   */
  public synchronized void update(ConcurrentHashMap<String, ConcurrentHashMap<String, Timestamp>> acks) {
    logger.entry( acks );
    logger.debug( "Updating acknowledgement vector data..." );
    /*
     * 1) For each summary in acks update local acks with summary
     */
    for (String nodeId : acks.keySet()) {
      Summary peerSummary = new Summary( acks.get(nodeId) );
      Summary summary = data.get( nodeId );

      if (summary != null) {
        summary.update( peerSummary );
      } else {
        data.put( nodeId, peerSummary );
      }
    }

    logger.exit();
  }

  /**
   * Get list of nodes identifiers from which summarized info is available
   * @return List of summarized nodes identifiers
   */
  public Set<String> summarizedNodes() {
    return data.keySet();
  }

}