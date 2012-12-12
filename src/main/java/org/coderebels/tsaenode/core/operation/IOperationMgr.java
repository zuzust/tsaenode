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

import org.coderebels.tsaenode.core.exception.OperationMgrException;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public interface IOperationMgr {

  /**
   * Creates an operation instance of the specified type
   * @param type Type of operation
   * @param file File the operation will apply to
   * @return Operation instance of the specified type that will apply
   * @throws OperationMgrException
   */
  public Operation createOperation(int type, String file) throws OperationMgrException;

  /**
   * Executes the specified operation
   * @param op Operation to execute
   * @return true if done successfully; false otherwise
   * @throws OperationMgrException
   */
  public boolean executeOperation(Operation op) throws OperationMgrException;

  /**
   * Gets the operation log managed by the local node
   * @return Operation log of the local node
   */
  public List<Operation> getLog();

}
