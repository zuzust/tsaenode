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

package org.coderebels.tsaenode.core.exception;

/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class SyncMgrException extends AXCBaseException {

  private static final long serialVersionUID = 1L;


  /**
   * @param mesg Message explaining the cause of the exception
   * @param triggerMethod Method where the exception occurs
   */
  public SyncMgrException(String mesg, String triggerMethod) {
    super( mesg, triggerMethod, null );
  }

  /* (non-Javadoc)
   * @see org.coderebels.tsaenode.core.exception.AXCBaseException#AXCBaseException(java.lang.String, java.lang.String, java.lang.Throwable)
   */
  public SyncMgrException(String mesg, String triggerMethod, Throwable triggerException) {
    super( mesg, triggerMethod, triggerException );
  }

}
