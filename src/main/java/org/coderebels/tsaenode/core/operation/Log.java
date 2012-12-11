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

}
