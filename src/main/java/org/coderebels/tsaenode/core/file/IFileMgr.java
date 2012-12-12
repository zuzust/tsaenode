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

import org.coderebels.tsaenode.core.exception.FileMgrException;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public interface IFileMgr {

  /**
   * Creates a file metadata instance from the specified file
   * @param file File from which metadata is created
   * @return Metadata instance from file
   */
  public FileData createFileData(String file);

  /**
   * Searches file in local node file index
   * @param file File to search
   * @return Metadata instance from file if found; null otherwise
   */
  public FileData searchFileData(String file) throws Exception;

  /**
   * Adds a new file to publication folder
   * @param file Metadata of the file to add
   * @return true if done successfully; false otherwise
   * @throws org.coderebels.tsaenode.core.exception.FileMgrException
   */
  public boolean addFile(FileData file) throws FileMgrException;

  /**
   * Removes file from publication folder
   * @param file Metadata of the file to remove
   * @return true if done successfully; false otherwise
   * @throws org.coderebels.tsaenode.core.exception.FileMgrException
   */
  public boolean removeFile(FileData file) throws FileMgrException;

  /**
   * Gets the index of files shared by the node
   * @return File index
   */
  public List<FileData> getFileIndex();

}
