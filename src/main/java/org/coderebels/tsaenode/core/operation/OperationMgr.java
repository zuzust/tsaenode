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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.coderebels.tsaenode.core.common.Timestamp;
import org.coderebels.tsaenode.core.common.Summary;
import org.coderebels.tsaenode.core.file.FileData;
import org.coderebels.tsaenode.core.file.IFileMgr;
import org.coderebels.tsaenode.core.exception.InvalidOperationTargetException;
import org.coderebels.tsaenode.core.exception.OperationMgrException;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class OperationMgr implements IOperationMgr {

  private static Logger logger = LogManager.getLogger( OperationMgr.class.getName() );
  /**
   * Local node Identifier
   */
  private String localNodeId;
  /**
   * Reference to local node FileMgr
   */
  private IFileMgr fileMgr;
  /**
   * Operation Log of local node
   */
  private Log log;
  /**
   * Summary vector of local node
   */
  private Summary summary;


  public OperationMgr(IFileMgr fileMgr) {
    this.fileMgr = fileMgr;
    this.log     = new Log();
    this.summary = new Summary();
  }


  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.operation.IOperationMgr#setNodeId(java.lang.String)
   */
  @Override
  public void setNodeId(String nodeId) {
    this.localNodeId = nodeId;
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.operation.IOperationMgr#createOperation(java.lang.Integer, java.lang.String)
   */
  @Override
  public Operation createOperation(int type, String file) throws OperationMgrException {
    logger.entry( type, file );
    logger.info( "Creating operation..." );
    /*
     * 1) If type = ADD --> IFileMgr.createFileData
     *    If type = REMOVE --> IFileMgr.searchFileData
     * 2) Create new Operation --> OperationFactory.createOperation
     */
    Operation op = null;

    try {
      FileData fd  = null;

      switch (type) {
        case Operation.ADD:
          fd = fileMgr.createFileData( file );
          break;
        case Operation.REMOVE:
          fd = fileMgr.searchFileData( file );

          if (fd == null) {
            String mesg   = String.format( "Unable to create the operation: the file doesn't exist -> %s", file );
            String method = String.format( "OperationMgr.createOperation( %1$d, %2$s )", type, file );
            throw new InvalidOperationTargetException( mesg, method );
          }
          break;
      }

      op = OperationFactory.createOperation( localNodeId, type, fd );
    } catch (Exception e) {
      op = null;
      String mesg   = String.format( "An error occurred while creating the operation" );
      String method = String.format( "OperationMgr.createOperation( %1$d, %2$s )", type, file );
      throw new OperationMgrException( mesg, method, e );
    }

    return logger.exit( op );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.operation.IOperationMgr#executeOperation(org.coderebels.tsaenode.core.operation.Operation)
   */
  @Override
  public synchronized boolean executeOperation(Operation op) throws OperationMgrException {
    logger.entry( op );
    logger.info( "Executing operation..." );
    /*
     * 1) If op = ADD --> IFileMgr.addFile
     *    If op = REMOVE --> IFileMgr.removeFile
     * 2) Add operation to Log
     * 3) Update Summary
     * 4) Return true if done successfully
     */
    boolean done = false;

    try {
      done = checkIsAlreadyExecuted( op );

      if (!done) {
        FileData file = op.getFile();
        int type = op.getType();

        switch (type) {
          case Operation.ADD:
            done = fileMgr.addFile( file );
            break;

          case Operation.REMOVE:
            String creator = op.getTimestamp().getNodeId();
            String owner   = op.getFile().getTimestamp().getNodeId();

            if (!creator.equals(owner)) {
              String mesg   = String.format( "Unable to execute the remove operation: the file belongs to another user -> %s", file );
              String method = String.format( "OperationMgr.executeOperation( %s )", op );
              throw new InvalidOperationTargetException( mesg, method );
            }

            done = fileMgr.removeFile( file );
            break;
        }

        if (done) {
          log.add( op );
          summary.update( op.getTimestamp() );
        }
      }
    } catch (Exception e) {
      done = false;
      String mesg   = String.format( "An error occurred while executing the operation -> %s", op );
      String method = String.format( "OperationMgr.executeOperation( %s )", op );
      throw new OperationMgrException( mesg, method, e );
    }

    return logger.exit( done );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.operation.IOperationMgr#getLog()
   */
  @Override
  public List<Operation> getLog() {
    logger.entry();
    logger.info( "Retrieving operation log..." );

    List<Operation> ops = log.getData();

    return logger.exit( ops );
  }


  /**
   * Checks if the operation has already been executed
   * @param op Operation to check
   * @return true if the operation has already been executed; false otherwise
   */
  private boolean checkIsAlreadyExecuted(Operation op) {
    logger.entry( op );
    logger.debug( "Checking operation execution..." );

    boolean isExecuted = false;

    Timestamp ts   = op.getTimestamp();
    String nodeId  = ts.getNodeId();
    Timestamp last = summary.getLast( nodeId );

    if (last != null) {
      int rel = last.compare( ts );
      //
      // Remember:
      // i)   last > ts  --> rel == 1
      // ii)  last == ts --> rel == 0
      // iii) last < ts  --> rel == -1
      //
      isExecuted = rel >= 0;
    }

    return logger.exit( isExecuted );
  }

}
