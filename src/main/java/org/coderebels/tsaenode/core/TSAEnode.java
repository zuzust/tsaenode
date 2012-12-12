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

package org.coderebels.tsaenode.core;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.coderebels.tsaenode.core.file.FileData;
import org.coderebels.tsaenode.core.file.IFileMgr;
import org.coderebels.tsaenode.core.file.FileMgr;
import org.coderebels.tsaenode.core.operation.Operation;
import org.coderebels.tsaenode.core.operation.IOperationMgr;
import org.coderebels.tsaenode.core.operation.OperationMgr;
import org.coderebels.tsaenode.core.exception.AXCBaseException;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class TSAEnode extends UnicastRemoteObject implements INode {

  private static Logger logger = LogManager.getLogger( TSAEnode.class.getName() );

  private final Object lock = new Object();

  private String nodeId;
  private String nodeIP;
  private String nodePort;
  private String rmiPort;
  private String pubFolderPath;

  private IFileMgr fileMgr;
  private IOperationMgr operationMgr;


  public TSAEnode() throws RemoteException, UnknownHostException, ConfigException {
    super();

    Config conf = ConfigFactory.load();

    nodeIP   = InetAddress.getLocalHost().getHostAddress();
    nodePort = conf.getString( "nodePort" );
    nodeId   = String.format( "%s:%s", nodeIP, nodePort );
    rmiPort  = conf.getString( "rmiPort" );
    pubFolderPath = conf.getString( "pubFolderPath" );

    String pubFolderURI  = String.format( "http://%s/tsaenode/%s", nodeIP, nodePort );

    System.setProperty( "nodeId", nodeId );
    System.setProperty( "pubFolderPath", pubFolderPath );
    System.setProperty( "pubFolderURI", pubFolderURI );

    fileMgr = new FileMgr();
    operationMgr = new OperationMgr( fileMgr );
  }


  /**
   * @return true if done successfully; false otherwise
   */
  public boolean connect() throws RemoteException {
    logger.entry();
    logger.info( "Connecting..." );

    boolean done = setUp();

    return logger.exit( done );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.INode#add(java.lang.String)
   */
  @Override
  public boolean add(String file) throws RemoteException {
    logger.entry( file );
    logger.info( "Adding file..." );

    boolean done = applyOperation( Operation.ADD, file );

    return logger.exit( done );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.INode#remove(java.lang.String)
   */
  @Override
  public boolean remove(String file) throws RemoteException {
    logger.entry( file );
    logger.info( "Removing file..." );

    boolean done = applyOperation( Operation.REMOVE, file );

    return logger.exit( done );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.INode#getIndex()
   */
  @Override
  public List<FileData> getIndex() throws RemoteException {
    logger.entry();
    logger.info( "Retrieving node file index..." );
    /*
     * 1) Delegate call to FileMgr through its interface --> IFileMgr.getFileIndex
     */
    List<FileData> index = fileMgr.getFileIndex();

    return logger.exit( index );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.INode#getOperationLog()
   */
  @Override
  public List<Operation> getOperationLog() throws RemoteException {
    logger.entry();
    logger.info( "Retrieving node operation log..." );
    /*
     * 1) Delegate call to OperationMgr through its interface --> IOperationMgr.getLog
     */
    List<Operation> ops = operationMgr.getLog();

    return logger.exit( ops );
  }


  /**
   * Sets up node data structures
   * @return true if done successfully; false otherwise
   */
  private boolean setUp() {
    logger.entry();
    logger.debug( "Setting up node..." );

    boolean done = true;

    try {
      File pubFolder = new File( pubFolderPath );
      if(!pubFolder.exists()) pubFolder.mkdirs();

      for(File file : pubFolder.listFiles()) {
        done = done && applyOperation( Operation.ADD, file.getAbsolutePath() );
      }
    } catch (Exception e) {
      done = false;
      logger.catching( e );
    }

    return logger.exit( done );
  }

  /**
   * Applies operation of specified type to supplied file
   * @param type Type of operation
   * @param file File the operation will apply to
   * @return true if done successfully; false otherwise
   */
  private boolean applyOperation(int type, String file) {
    logger.entry( type, file );
    logger.debug( "Applying operation..." );
    /*
     * 1) Create a new operation
     * 2) Execute operation --> IOperationMgr.executeOperation
     * 3) Start synchronization session --> ISyncMgr.startSession [ Phase 3 ]
     * 4) Return true if done successfully
     */
    boolean done = false;

    try {
      synchronized (lock) {
        Operation op = operationMgr.createOperation( type, file );
        done = operationMgr.executeOperation( op );
      }
    } catch (AXCBaseException e) {
      done = false;
      logger.catching( e );
    }

    return logger.exit( done );
  }

}