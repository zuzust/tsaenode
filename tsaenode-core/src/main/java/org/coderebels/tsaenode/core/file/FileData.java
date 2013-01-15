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

import java.io.Serializable;

import org.coderebels.tsaenode.core.common.Timestamp;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class FileData implements Serializable {

  private String filename;
  private String owner;
  private String path;
  private String uri;
  private Timestamp timestamp;


  public FileData() {}


  public String getFilename() { return filename; }
  public void setFilename(String filename) { this.filename = filename; }

  public String getOwner() { return owner; }
  public void setOwner(String owner) { this.owner = owner; }

  public String getPath() { return path; }
  public void setPath(String path) { this.path = path; }

  public String getURI() { return uri; }
  public void setURI(String uri) { this.uri = uri; }

  public Timestamp getTimestamp() { return timestamp; }
  public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }


  @Override
  public String toString() {
    return String.format( "File[%s,%s]", timestamp.toShortString(), uri );
  }

  @Override
  public boolean equals(Object that) {
    return this.getClass().getName().equals( that.getClass().getName() )
        && this.filename.equals( ((FileData) that).getFilename() )
        && this.owner.equals( ((FileData) that).getOwner() )
        && this.path.equals( ((FileData) that).getPath() )
        && this.uri.equals( ((FileData) that).getURI() )
        && this.timestamp.equals( ((FileData) that).getTimestamp() );
  }

}
