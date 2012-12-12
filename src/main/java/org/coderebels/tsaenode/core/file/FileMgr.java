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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.coderebels.tsaenode.core.exception.FileMgrException;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class FileMgr implements IFileMgr {

  private static Logger logger = LogManager.getLogger( FileMgr.class.getName() );
  /**
   * Local node identifier
   */
  private String localNodeId;
  /**
   * Publication folder path in local node
   */
  private String pubFolderPath;
  /**
   * Publication folder URI in local node
   */
  private String pubFolderURI;
  /**
   * Index of files shared within the group
   */
  private FileIndex fileIndex;


  public FileMgr() {
    ConfigFactory.invalidateCaches();
    Config conf = ConfigFactory.load();

    localNodeId   = conf.getString( "nodeId" );
    pubFolderPath = conf.getString( "pubFolderPath" );
    pubFolderURI  = conf.getString( "pubFolderURI" );
    fileIndex     = new FileIndex();
  }


  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.file.IFileMgr#createFileData(java.lang.String)
   */
  @Override
  public FileData createFileData(String file) {
    logger.entry( file );
    logger.debug( "Creating file data..." );
    /*
     * 1) Create new FileData instance --> FileDataFactory.createFileData
     */
    FileData fd = FileDataFactory.createFileData( localNodeId, file );

    return logger.exit( fd );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.file.IFileMgr#searchFileData(java.lang.String)
   */
  @Override
  public FileData searchFileData(String file) throws Exception {
    logger.entry( file );
    logger.debug( "Searching file data..." );
    /*
     * 1) Search for file in FileIndex and return the corresponding FileData
     *    or null if not found
     */
    FileData fd = fileIndex.search( basename(file) );

    return logger.exit( fd );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.file.IFileMgr#addFile(org.coderebels.tsaenode.core.file.FileData)
   */
  @Override
  public boolean addFile(FileData file) throws FileMgrException {
    logger.entry( file );
    logger.debug( "Adding file..." );
    /*
     * 1) If file belongs to local node --> file.timestamp.nodeId == this.localNodeId
     * 1.1) Add file to publication folder --> FileMgr.doAddFile
     * 2) Update file index adding the passed in FileData instance
     * 3) Return true if done successfully
     */
    boolean done = false;

    try {
      String filename = basename( file.getPath() );

      if (file.getTimestamp().getNodeId().equals(localNodeId)) {
        doAddFile( file );
        file.setPath( pubFolderPath + File.separator + filename );
        file.setURI( pubFolderURI + "/" + filename );
      }

      file.setFilename( filename );
      fileIndex.add( file );

      done = true;
    } catch (Exception e) {
      done = false;
      String mesg = String.format( "An error occurred while adding the file -> %s", file );
      String method = String.format( "FileMgr#addFile( %s )", file );
      throw new FileMgrException( mesg, method, e );
    }

    return logger.exit( done );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.file.IFileMgr#removeFile(org.coderebels.tsaenode.core.file.FileData)
   */
  @Override
  public boolean removeFile(FileData file) throws FileMgrException {
    logger.entry( file );
    logger.debug( "Removing file..." );
    /*
     * 1) Check the file belongs to the local node --> file.timestamp.nodeId == this.localNodeId
     * 2) If true
     * 2.1) Delete the file from the publication folder --> FileMgr.doRemoveFile
     * 3) Update file index removing the passed in FileData instance
     * 4) Return true if done successfully
     */
    boolean done = false;

    try {
      if (file.getTimestamp().getNodeId().equals(localNodeId)) {
        doRemoveFile( file );
      }

      fileIndex.remove( file );

      done = true;
    } catch (Exception e) {
      done = false;
      String mesg = String.format( "An error occurred while removing the file -> %s", file );
      String method = String.format( "FileMgr#removeFile( %s )", file );
      throw new FileMgrException( mesg, method, e );
    }

    return logger.exit( done );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.file.IFileMgr#getFileIndex()
   */
  @Override
  public List<FileData> getFileIndex() {
    logger.entry();
    logger.debug( "Retrieving file index..." );

    List<FileData> index = fileIndex.getData();

    return logger.exit( index );
  }


  /**
   * Copies file to local publication folder
   * @param file File to copy
   * @return true if done successfully; false otherwise
   * @throws java.lang.Exception
   */
  private boolean doAddFile(FileData file) throws Exception {
    logger.entry( file );
    logger.debug( "Copying file to publication folder..." );
    /*
     * 1) Copy file to local publication folder
     */
    String pathToFile = file.getPath();
    File fin = new File( pathToFile );

    if (!fin.exists()) {
      String mesg   = String.format( "The file to add does not exist -> %s", file );
      String method = String.format( "FileMgr#doAddFile( %s )", file );
      throw new FileMgrException( mesg, method );
    }

    String newPathToFile = pubFolderPath + File.separator + basename( pathToFile );
    File fout = new File( newPathToFile );

    if (!fout.exists()) {
      Files.copy( fin.toPath(), fout.toPath() );
    }

    return logger.exit( true );
  }

  /**
   * Deletes the specified file from local publication folder
   * @param file File to delete
   * @return true if done successfully; false otherwise
   * @throws java.lang.Exception
   */
  private boolean doRemoveFile(FileData file) throws Exception {
    logger.entry( file );
    logger.debug( "Deleting file from publication folder..." );
    /*
     * 1) Delete file from local publication folder
     */
    boolean done = false;

    String pathToFile = pubFolderPath + File.separator + file.getFilename();
    File f = new File( pathToFile );

    if (!f.exists()) {
      String mesg   = String.format( "The file to delete does not exist -> %s", file);
      String method = String.format( "FileMgr#doRemoveFile( %s )", file );
      throw new FileMgrException( mesg, method );
    }

    done = f.delete();
    return logger.exit( done );
  }

  /**
   * Gets the basename of the file
   * @param pathToFile Absolute path to file
   * @return Basename of the file
   */
  private String basename(String pathToFile) throws Exception {
    File f = new File( pathToFile );
    return f.getName();
  }

}