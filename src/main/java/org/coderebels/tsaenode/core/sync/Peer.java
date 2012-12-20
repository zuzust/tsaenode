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

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.coderebels.tsaenode.core.INode;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class Peer implements Serializable {

  private static Logger logger = LogManager.getLogger( Peer.class.getName() );

  private String id;
  private String ip;
  private int port;
  private int rmiPort;


  public Peer() {
    rmiPort = 1099;
  }


  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getIP() { return ip; }
  public void setIP(String ip) { this.ip = ip; }

  public int getPort() { return port; }
  public void setPort(int port) { this.port = port; }

  public int getRmiPort() { return rmiPort; }
  public void setRmiPort(int rmiPort) { this.rmiPort = rmiPort; }

  public INode getStub() {
    logger.entry();

    INode stub = null;

    try {
      Registry registry = LocateRegistry.getRegistry( ip, rmiPort );
      stub = (INode) registry.lookup( "node" + port );
    } catch (Exception e) {
      logger.catching( e );
    }

    return logger.exit( stub );
  }

  public boolean isConnected() {
    return getStub() != null;
  }

  @Override
  public String toString() {
    return String.format( "Node[%s,%d]", id, rmiPort );
  }
}