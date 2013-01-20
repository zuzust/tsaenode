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

package org.coderebels.tsaenode.core.file;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.coderebels.tsaenode.core.common.Timestamp;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
class FileDataFactory {

  private static Logger logger = LogManager.getLogger( FileDataFactory.class.getName() );


  /**
   * Builds file metadata based on supplied information
   * @param nodeId Local node identifier
   * @param file File absolute path
   * @return File metadata wrapping supplied information
   */
  public static synchronized FileData createFileData(String nodeId, String file) {
    logger.entry( nodeId, file );
    logger.debug( "Creating FileData instance..." );

    Timestamp timestamp = new Timestamp();
    timestamp.setNodeId( nodeId );
    timestamp.setSeqNumber( System.currentTimeMillis() );

    FileData fd = new FileData();
    fd.setOwner( nodeId );
    fd.setPath( file );
    fd.setTimestamp( timestamp );

    return logger.exit( fd );
  }

}
