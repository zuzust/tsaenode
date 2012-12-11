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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.coderebels.tsaenode.core.file.FileData;
import org.coderebels.tsaenode.core.operation.Operation;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public interface INode extends Remote {

  /**
   * Adds a file to publication folder, becoming shareable within the group
   * @param file File to add to publication folder
   * @return true if done successfully; false otherwise
   * @throws java.rmi.RemoteException
   */
  public boolean add(String file) throws RemoteException;

  /**
   * Removes a file from publication folder, becoming unshareable within the group
   * @param file File to remove from publication folder
   * @return true if done successfully; false otherwise
   * @throws java.rmi.RemoteException
   */
  public boolean remove(String file) throws RemoteException;

  /**
   * Gets the index of files shared by the node
   * @return File index
   * @throws java.rmi.RemoteException
   */
  public List<FileData> getIndex() throws RemoteException;

  /**
   * Gets the operation log managed by the node
   * @return Operation log managed by node
   * @throws java.rmi.RemoteException
   */
  public List<Operation> getOperationLog() throws RemoteException;

}