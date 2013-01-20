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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.coderebels.tsaenode.core.file.FileData;
import org.coderebels.tsaenode.core.common.Timestamp;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class OperationFactory {

  private static Logger logger = LogManager.getLogger( OperationFactory.class.getName() );


  /**
   * Builds operation of specified type associated to supplied file metadata
   * @param nodeId Identifier of node where operation is originated
   * @param type Operation type
   * @param file Metadata of file the operation will apply to
   * @return Operation instance of specified type
   */
  public static synchronized Operation createOperation(String nodeId, int type, FileData file) {
    logger.entry( nodeId, type, file );
    logger.debug( "Creating Operation instance..." );

    Timestamp timestamp = new Timestamp();
    timestamp.setNodeId( nodeId );
    timestamp.setSeqNumber( System.currentTimeMillis() );

    Operation op = new Operation();
    op.setType( type );
    op.setFile( file );
    op.setTimestamp( timestamp );

    return logger.exit( op );
  }

}