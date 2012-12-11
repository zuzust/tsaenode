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

import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class FileIndex {

  private static Logger logger = LogManager.getLogger( FileIndex.class.getName() );

  private List<FileData> data;


  public FileIndex() {
    data = new Vector<FileData>();
  }


  /**
   * Adds or updates a file in the index
   * @param file File to add
   */
  public synchronized void add(FileData file) {
    logger.entry( file );
    logger.debug( "Adding file to Index..." );
    //
    // Atention: indexOf(file) would return the zero-based index if file.equals(previous)
    // but this will never happen, as their timestamp will always be different --> file.timestamp > previous.timestamp
    //
    FileData previous = search( file.getFilename() );

    if (previous != null) {
      data.remove( previous );
    }

    data.add( file );

    logger.exit();
  }

  /**
   * Removes a file from the index if exists
   * @param file File to remove
   */
  public void remove(FileData file) {
    logger.entry( file );
    logger.debug( "Removing file from Index..." );

    data.remove( file );

    logger.exit();
  }

  /**
   * Returns the matching file from the index
   * @param filename Name of the file to search
   * @return FileData associated to file; null otherwise
   */
  public FileData search(String filename) {
    logger.entry( filename );
    logger.debug( "Searching file in Index..." );

    for (FileData fd : data) {
      if (fd.getFilename().equals(filename)) {
        return logger.exit( fd );
      }
    }

    return logger.exit( null );
  }

  /**
   * Gets the file list from the index
   * @return List of file data in index
   */
  public List<FileData> getData() {
    logger.entry();
    logger.debug( "Retrieving index data..." );

    return logger.exit( data );
  }

}
