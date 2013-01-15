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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.coderebels.tsaenode.core.INode;
import org.coderebels.tsaenode.core.common.Timestamp;
import org.coderebels.tsaenode.core.operation.Operation;
import org.coderebels.tsaenode.core.operation.IOperationMgr;
import org.coderebels.tsaenode.core.exception.SyncMgrException;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class TSAESynchronizer implements Runnable {

  private static Logger logger = LogManager.getLogger( TSAESynchronizer.class.getName() );
  /**
   * Reference to synchronization peer
   */
  private Peer peer;
  /**
   * Reference to local node OperationMgr
   */
  private IOperationMgr operationMgr;
  /**
   * List of nodes with whom local node is synchronizing
   */
  private SyncMap syncMap;


  public TSAESynchronizer(Peer peer, IOperationMgr operationMgr, SyncMap syncMap) {
    this.peer = peer;
    this.operationMgr = operationMgr;
    this.syncMap = syncMap;
  }


  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    doSynchronize();
  }


  /**
   * Establishes a synchronization conversation involving the local node
   * and a randomly selected node within the group
   * @return true if done successfully; false otherwise
   */
  private boolean doSynchronize() {
    logger.entry( peer );
    logger.debug( "Synchronizing node..." );
    /*
     * 0) Get the peer node stub
     * 1) Retrieve the peer summary vector --> INode#requestSummary
     * 2) Retrieve the peer acknowledgement vector --> INode#requestAckSummary
     * 3) Extract the operations not seen by peer based on its summary vector --> IOperationMgr#extractOperations
     * 4) Request the peer to perform a synchronization session --> INode#performTSAESession
     * 5) Update local node operation log --> IOperationMgr#updateLog
     * 6) Update local node acknowledgement vector --> IOperationMgr#updateAcks
     * 7) Return true if done successfully; false otherwise
     */
    boolean done = false;

    try {
      INode stub = peer.getStub();

      if (stub == null) {
        String mesg = "Unable to contact the peer node";
        String method = "TSAESynchronizer#doSynchronize()";
        throw new SyncMgrException( mesg, method );
      }

      ConcurrentHashMap<String, Timestamp> peerSummary = stub.requestSummary();
      ConcurrentHashMap<String, ConcurrentHashMap<String, Timestamp>> peerAckSummary = stub.requestAckSummary();

      List<Operation> opsToSend = operationMgr.extractOperations( peerSummary );
      List<Operation> opsToExec = stub.performTSAESession( opsToSend, operationMgr.getSummary(), operationMgr.getAcks() );

      done = operationMgr.updateLog( opsToExec );
      done = done && operationMgr.updateAcks( peerAckSummary );
    } catch (Exception e) {
      logger.catching( e );
      done = false;
    } finally {
      syncMap.setAvailable( peer );
    }

    return logger.exit( done );
  }

}