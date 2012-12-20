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

package org.coderebels.tsaenode.core.operation;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentNavigableMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.coderebels.tsaenode.core.common.Timestamp;


/**
 * Operations known by a node
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class Log {

  private static Logger logger = LogManager.getLogger( Log.class.getName() );

  private ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Operation>> data;


  public Log() {
    this.data = new ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Operation>>();
  }


  /**
   * Gets the operation log data
   * @return Operation list from log
   */
  public List<Operation> getData() {
    logger.entry();
    logger.debug( "Retrieving log data..." );

    List<Operation> ops = new Vector<Operation>();

    for (ConcurrentSkipListMap<Long, Operation> opsMap : data.values()) {
      ops.addAll( opsMap.values() );
    }

    return logger.exit( ops );
  }

  /**
   * Adds an operation to the log
   * @param op Operation to add
   */
  public synchronized void add(Operation op) {
    logger.entry( op );
    logger.debug( "Adding operation to log..." );

    Timestamp timestamp = op.getTimestamp();
    String nodeId = timestamp.getNodeId();

    ConcurrentSkipListMap<Long, Operation> nodeOps = data.get( nodeId );

    if (nodeOps == null) {
      nodeOps = new ConcurrentSkipListMap<Long, Operation>();
      data.put( nodeId, nodeOps );
    }

    nodeOps.put( timestamp.getSeqNumber(), op );

    logger.exit();
  }

  /**
   * Gets the first operation of the specified node in the log
   * @param nodeId Node identifier
   * @return First operation of node in the log
   */
  public synchronized Operation getFirst(String nodeId) {
    logger.entry( nodeId );
    logger.debug( "Retrieving first operation from log..." );

    Operation op = null;

    ConcurrentSkipListMap<Long, Operation> nodeOps = data.get( nodeId );

    if (nodeOps != null && !nodeOps.isEmpty()) {
      op = nodeOps.get( nodeOps.firstKey() );
    }

    return logger.exit( op );
  }

  /**
   * Gets the list of operations between the specified timestamps
   * @param nodeId Node identifier
   * @param first Initial timestamp
   * @param last Last timestamp (included)
   * @param incFirst True if first operation from range must be included; false otherwise
   * @return List of operations from first to last (included)
   */
  public List<Operation> extract(String nodeId, Timestamp first, Timestamp last, boolean incFirst) {
    logger.entry( nodeId, first, last, incFirst );
    logger.debug( "Extracting operations from log..." );

    List<Operation> ops = new Vector<Operation>();
    ConcurrentSkipListMap<Long, Operation> nodeOps = data.get( nodeId );

    if (nodeOps != null && !nodeOps.isEmpty()) {
      boolean incLast = true;
      ConcurrentNavigableMap<Long, Operation> subset = nodeOps.subMap( first.getSeqNumber(), incFirst, last.getSeqNumber(), incLast);

      ops.addAll( subset.values() );
    }

    return logger.exit( ops );
  }

  /**
   * Remove from log operations timestamped before than specified timestamp
   * @param nodeId Identifier of node responsible of operations to remove
   * @param lastSeen Timestamp of last executed operation of specified node seen by all nodes within the group
   */
  public synchronized void removeAllPreceding(String nodeId, Timestamp lastSeen) {
    ConcurrentSkipListMap<Long, Operation> nodeOps = data.get( nodeId );

    if (nodeOps != null && !nodeOps.isEmpty()) {
      long seqNum = nodeOps.firstKey();

      while (!nodeOps.isEmpty() && seqNum <= lastSeen.getSeqNumber()) {
        nodeOps.remove( seqNum );
        seqNum++;
      }
    }
  }

}
