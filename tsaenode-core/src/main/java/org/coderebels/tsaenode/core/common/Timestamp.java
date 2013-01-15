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

package org.coderebels.tsaenode.core.common;

import java.io.Serializable;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class Timestamp implements Serializable {

  private String nodeId;
  private long seqNumber;


  public Timestamp() {}


  public String getNodeId() { return nodeId; }
  public void setNodeId(String nodeId) { this.nodeId = nodeId; }

  public long getSeqNumber() { return seqNumber; }
  public void setSeqNumber(long seqNumber) { this.seqNumber = seqNumber; }


  public int compare(Timestamp that) {
    if (that == null) {
      return 1;
    }

    if (this.seqNumber > that.getSeqNumber()) {
      return 1;
    }

    if (this.seqNumber < that.getSeqNumber()) {
      return -1;
    }

    return 0;
  }

  public String toShortString() {
    return String.format( "[%1$s,%2$d]", nodeId, seqNumber );
  }

  @Override
  public String toString() {
    return String.format( "Timestamp%s", toShortString() );
  }

  @Override
  public boolean equals(Object that) {
    return this.getClass().getName().equals(that.getClass().getName())
        && this.seqNumber == ((Timestamp) that).getSeqNumber()
        && this.nodeId.equals( ((Timestamp) that).getNodeId() );
  }
  
}
