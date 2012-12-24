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

package org.coderebels.tsaenode;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.TimerTask;
import java.util.Random;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.coderebels.tsaenode.core.INode;
import org.coderebels.tsaenode.core.file.FileData;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class Simulation extends TimerTask {

  private static Logger logger = LogManager.getLogger( Simulation.class.getName() );

  private INode node;
  private int connections;
  private int disconnections;
  private int addedFiles;
  private int removedFiles;


  public Simulation(INode node) {
    super();

    this.node = node;
    this.connections    = 0;
    this.disconnections = 0;
    this.addedFiles     = 0;
    this.removedFiles   = 0;
  }


  public int getConnections() { return connections; }
  public int getDisconnections() { return disconnections; }
  public int getAddedFiles() { return addedFiles; }
  public int getRemovedFiles() { return removedFiles; }


  /* (non-Javadoc)
  * @see java.util.TimerTask#run()
  */
  @Override
  public void run() {
    boolean done = false;
    Random rnd = new Random();

    try {
      double n = rnd.nextDouble();
      //
      // Probability of:
      // - disconnection: 0.05
      // - adding a file: 0.25
      // - deleting a file: 0.1
      // - reconnecting: 0.2
      //
      if (node.isConnected()) {
        if (n < 0.05) {
          done = node.disconnect();
          if (done) disconnections++;
        }

        else if (n >= 0.05 && n < 0.30) {
          Path file = Files.createTempFile(null, null);
          done = node.add( file.toString() );
          if (done) addedFiles++;
        }

        else if (n >= 0.30 && n < 0.40) {
          List<FileData> files = node.requestFileIndex();
          if (!files.isEmpty()) {
            FileData file = files.get( 0 );
            done = node.remove( file.getPath() );
            if (done) removedFiles++;
          }
        }
      } else {
        if (n < 0.2) {
          done = node.connect();
          if (done) connections++;
        }
      }
    } catch (Exception e) {
      logger.catching( e );
    }
  }

}
