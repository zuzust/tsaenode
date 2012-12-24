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
import java.util.concurrent.ConcurrentHashMap;

import org.coderebels.tsaenode.core.common.Timestamp;
import org.coderebels.tsaenode.core.file.FileData;
import org.coderebels.tsaenode.core.operation.Operation;
import org.coderebels.tsaenode.core.sync.Peer;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public interface INode extends Remote {

  /**
   * Sets up node data structures and joins the group of nodes
   * @return true if done successfully; false otherwise
   * @throws java.rmi.RemoteException
   */
  public boolean connect() throws RemoteException;

  /**
   * Leaves the group of nodes
   * @return true if done successfully; false otherwise
   * @throws java.rmi.RemoteException
   */
  public boolean disconnect() throws RemoteException;

  /**
   * Checks if node is still joinning the group
   * @return true if node is connected; false otherwise
   * @throws java.rmi.RemoteException
   */
  public boolean isConnected() throws RemoteException;

  /**
   * Gets node profile (id, ip, port, ...)
   * @return Node profile encapsulation
   * @throws java.rmi.RemoteException
   */
  public Peer requestProfile() throws RemoteException;

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
  public List<FileData> requestFileIndex() throws RemoteException;

  /**
   * Gets the operation log managed by the node
   * @return Operation log managed by node
   * @throws java.rmi.RemoteException
   */
  public List<Operation> requestLog() throws RemoteException;

  /**
   * Gets the summary vector managed by the node
   * @return Summary vector managed by node
   * @throws java.rmi.RemoteException
   */
  public ConcurrentHashMap<String, Timestamp> requestSummary() throws RemoteException;

  /**
   * Gets the acknowledgement vector managed by node
   * @return Acknowledgement vector managed by node
   * @throws java.rmi.RemoteException
   */
  public ConcurrentHashMap<String, ConcurrentHashMap<String, Timestamp>> requestAckSummary()
    throws RemoteException;

  /**
   * Starts a new synchronization session
   * @return true if done successfully; false otherwise
   * @throws java.rmi.RemoteException
   */
  public boolean startTSAESession() throws RemoteException;

  /**
   * Takes necessary actions to get the node synchronized
   * @param ops List of operations to be executed by node
   * @param summary Summary vector of fellow node with whom synchronization session is established
   * @param acks Acknowledgement vector of fellow node with whom synchronization session is established
   * @return List of operations not known by fellow node with whom synchronization session is established
   * @throws java.rmi.RemoteException
   */
  public List<Operation> performTSAESession(List<Operation> ops,
                        ConcurrentHashMap<String, Timestamp> summary,
                        ConcurrentHashMap<String, ConcurrentHashMap<String, Timestamp>> acks)
    throws RemoteException;

}