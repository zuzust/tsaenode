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
public abstract class AXCBaseException extends Exception {

  private static final long serialVersionUID = 1L;
  protected String triggerMethod;


  /**
   * @param mesg Message explaining the cause of the exception
   * @param triggerMethod Method where the exception occurs
   * @param triggerException Original exception raising the current exception
   */
  public AXCBaseException(String mesg, String triggerMethod, Throwable triggerException) {
    super( mesg, triggerException );
    this.triggerMethod = triggerMethod;
  }


  /**
   * @return Method where the exception occurs
   */
  public String getTriggerMethod() {
    return this.triggerMethod;
  }

  /**
   * @return Original exception raising the current exception; null if not exists
   */
  public Throwable getTriggerException() {
    return super.getCause();
  }

}
