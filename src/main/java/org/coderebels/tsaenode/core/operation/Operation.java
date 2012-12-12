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

import java.io.Serializable;

import org.coderebels.tsaenode.core.file.FileData;
import org.coderebels.tsaenode.core.common.Timestamp;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class Operation implements Serializable {

  public static final int ADD = 0;
  public static final int REMOVE = 1;

  private int type;
  private FileData file;
  private Timestamp timestamp;


  public Operation() {}


  public int getType() { return type; }
  public void setType(int type) { this.type = type; }

  public FileData getFile() { return file; }
  public void setFile(FileData file) { this.file = file; }

  public Timestamp getTimestamp() { return timestamp; }
  public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }


  @Override
  public String toString() {
    String type = "";

    switch (this.type) {
      case ADD:
        type = "add";
        break;

      case REMOVE:
        type = "remove";
        break;

      default:
        type = "unknown";
    }

    return String.format( "Operation[%s,%s,%s]", timestamp.toShortString(), type, file.getURI() );
  }

  @Override
  public boolean equals(Object that) {
    return this.getClass().getName().equals( that.getClass().getName() )
        && this.type == ((Operation) that).getType()
        && this.file.equals( ((Operation) that).getFile() )
        && this.timestamp.equals( ((Operation) that).getTimestamp() );
  }

}
