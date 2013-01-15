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
import java.util.concurrent.ConcurrentHashMap;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.coderebels.tsaenode.core.common.Timestamp;
import org.coderebels.tsaenode.core.operation.Operation;
import org.coderebels.tsaenode.core.operation.IOperationMgr;
import org.coderebels.tsaenode.core.exception.SyncMgrException;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class TSAESyncMgr implements ISyncMgr {

  private static Logger logger = LogManager.getLogger( TSAESyncMgr.class.getName() );
  /**
   * Lock for concurrency management
   */
  private final Object lock = new Object();
  /**
   * Local node identifier
   */
  private String localNodeId;
  /**
   * List of peers joinning the group
   */
  private List<Peer> peers;
  /**
   * Map of peers with whom local node is synchronizing
   */
  private SyncMap syncMap;
  /**
   * Synchronization policy
   */
  private ISyncPolicy syncPolicy;
  /**
   * Reference to local node OperationMgr
   */
  private IOperationMgr operationMgr;


  public TSAESyncMgr(IOperationMgr operationMgr) throws ConfigException {
    ConfigFactory.invalidateCaches();
    Config conf = ConfigFactory.load();

    this.localNodeId  = conf.getString( "nodeId" );
    this.peers        = setUpGroup( conf );
    this.syncMap      = new SyncMap();
    this.syncPolicy   = SyncPolicyFactory.getPolicy( conf.getInt("policy") );
    this.operationMgr = operationMgr;
  }


  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.sync.ISyncMgr#startSession()
   */
  @Override
  public boolean startSession() throws SyncMgrException {
    logger.entry();
    logger.debug( "Starting synchronization..." );
    /*
     * 1) Start synchronization process within the group --> this.doSynchronize
     * 2) Purge the operation log --> OperationMgr#purgeLog
     * 3) Return true if done successfully; false otherwise
     */
    boolean done = false;

    try {
      done = doSynchronize();

      synchronized (lock) {
        int groupSize = 1 + peers.size(); // Recall that we didn't add localnode to peers
        done = done && operationMgr.purgeLog( groupSize );
      }
    } catch (Exception e) {
      done = false;
      String mesg   = String.format( "An error occurred while starting the synchronization session" );
      String method = String.format( "TSAESyncMgr#startSession()" );
      throw new SyncMgrException( mesg, method, e );
    }

    return logger.exit( done );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.sync.ISyncMgr#performSession(java.util.List,
   * java.util.concurrent.ConcurrentHashMap,
   * java.util.concurrent.ConcurrentHashMap)
   */
  @Override
  public List<Operation> performSession(List<Operation> ops,
                        ConcurrentHashMap<String, Timestamp> summary,
                        ConcurrentHashMap<String, ConcurrentHashMap<String, Timestamp>> acks)
    throws SyncMgrException {
    logger.entry( ops, summary, acks );
    logger.debug( "Performing synchronization..." );
    /*
     * 1) Retrieve operations unknown by fellow node based on its summary --> IOperationMgr#extractOperations
     * 2) Update operation log --> IOperationMgr#updateLog
     * 3) Update acknowledgement vector --> IOperationMgr#updateAcks
     * 4) Return operations from 1)
     */
    List<Operation> opsToSend = null;

    try {
      opsToSend = operationMgr.extractOperations( summary );

      synchronized (lock) {
        boolean done = operationMgr.updateLog( ops );
        done = done && operationMgr.updateAcks( acks );
      }
    } catch (Exception e) {
      opsToSend = null;
      String mesg   = String.format( "An error occurred while performing the synchronization session" );
      String method = String.format( "TSAESyncMgr#performSession( %s, %s, %s )", ops, summary, acks );
      throw new SyncMgrException( mesg, method, e );
    }

    return logger.exit( opsToSend );
  }


  /**
   * Indexes the peers of the group
   * @param conf Config settings
   * @return List of peers of the group
   * @throws com.typesafe.config.ConfigException
   */
  private List<Peer> setUpGroup(Config conf) throws ConfigException {
    logger.entry();
    logger.debug( "Indexing group..." );

    List<Peer> peers = new Vector<Peer>();

    Config group = conf.getConfig( "group" );
    List<String> ns = group.getStringList( "nodes" );

    for(String n : ns) {
      Config nc = group.getConfig( n );
      String nodeId = String.format("%s:%s", nc.getString("ip"), nc.getInt("port"));

      if (!nodeId.equals( localNodeId )) {
        Peer node = new Peer();
        node.setId( nodeId );
        node.setIP( nc.getString("ip") );
        node.setPort( nc.getInt("port") );
        node.setRmiPort( conf.getInt("rmiPort") );

        peers.add( node );
      }
    }

    return logger.exit( peers );
  }

  /**
   * Synchronizes the local node and a selected subset of peers
   * @return true if done successfully; false otherwise
   * @throws org.coderebels.tsaenode.core.exception.SyncMgrException
   */
  private boolean doSynchronize() throws SyncMgrException {
    logger.entry();
    logger.debug( "Synchronizing nodes..." );
    /*
     * 1) Select the subset of peers with whom local node will synchronize
     * 2) For each fellow node in the subset start a new synchronization session using a SyncAgent
     * 3) Return true if done successfully; false otherwise
     */
    boolean done = false;

    try {
      List<Peer> syncNodes = syncPolicy.selectSyncNodes( peers, syncMap );
      //
      // Increase concurrency at this point
      //
      List<Thread> syncThreads = new Vector<Thread>();

      for (Peer peer : syncNodes) {
        Runnable synchronizer = new TSAESynchronizer( peer, operationMgr, syncMap );
        Thread syncThread = new Thread( synchronizer );
        syncThreads.add( syncThread );
        syncThread.start();
      }

      for (Thread syncThread : syncThreads) {
        syncThread.join();
      }

      done = true;
    } catch (Exception e) {
      done = false;
      String mesg   = String.format( "An error occurred while synchronizing the local node" );
      String method = String.format( "TSAESyncMgr#doSynchronize()" );
      throw new SyncMgrException( mesg, method, e );
    }

    return logger.exit( done );
  }

}