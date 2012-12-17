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

package org.coderebels.tsaenode.core.sync;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class SyncPolicyFactory {

  /**
   * Gets an instance of ISyncPolicy based on desired policy
   * @param policy Synchronization policy
   * @return Synchronization policy instance
   */
  public static ISyncPolicy getPolicy(int policy) {
    ISyncPolicy syncPolicy = null;

    switch (policy) {
      case 1: syncPolicy = new UnicastSyncPolicy();
        break;
    }

    return syncPolicy;
  }

}