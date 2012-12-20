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

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import org.coderebels.tsaenode.core.INode;
import org.coderebels.tsaenode.core.TSAEnode;
import org.coderebels.tsaenode.core.common.Timestamp;
import org.coderebels.tsaenode.core.file.FileData;
import org.coderebels.tsaenode.core.operation.Operation;
import org.coderebels.tsaenode.core.sync.Peer;


/**
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
public class Main {

  private static Logger logger = LogManager.getLogger( Main.class.getName() );

  
  public static void main(String[] args) {

    // Read the Node Port from the command line (if none supplied, use 2010)
    String nodePort = "2010";
    try {
      nodePort = args[0];
    } catch (Exception e) {}

    System.setProperty( "nodePort", nodePort );

    // Start the program
    new Main().start();
  }


  public Main() {}


  public void start() {
    // Register Node
    INode node = registerNode();

    // Read, Evaluate and Print
    repl( node );

    // Unegister Node and Exit
    unregisterNode( node );
  }
  

  private INode registerNode() {
    INode node = null;

    try {
      node = new TSAEnode();
      boolean done = node.connect();

      if (!done) {
        System.out.println( "An error occurred while registering the node." );
        System.exit( -1 );
      }
    } catch (Exception e) {
      logger.catching( e );
      System.out.println( "An error occurred while registering the node." );
      System.exit( -1 );
    }

    return node;
  }

  private void unregisterNode(INode node) {
    try {
      boolean done = node.disconnect();

      if (done) {
        System.out.println( "Bye bye!" );
        System.exit( 0 );
      } else {
        System.out.println( "An error occurred while unregistering the node." );
        System.exit( -1 );
      }
    } catch (Exception e) {
      logger.catching( e );
      System.out.println( "An error occurred while unregistering the node." );
      System.exit( -1 );
    }
  }

  private void repl(INode node) {
    boolean exit = false;
    int action = 0;

    try {
      StringWriter writer = prepareOptions();

      while (!exit) {
        printOptions( writer );
        action = readAction();
        exit = evaluateAction( action, node );
      }
    } catch (Exception e) {
      logger.catching( e );
      System.out.println( "An error occurred while interacting with the node." );
    }
  }

  private StringWriter prepareOptions() throws Exception {
    VelocityContext context = new VelocityContext();
    StringWriter writer = prepareTemplate( "options.vm", context );

    return writer;
  }

  private StringWriter prepareTemplate(String template, VelocityContext context) throws Exception {
    String templatePath = "templates" + File.separator + template;
    
    Reader reader = new InputStreamReader( getClass().getClassLoader().getResourceAsStream(templatePath) );
    StringWriter writer = new StringWriter();
    Velocity.evaluate( context, writer, "", reader );

    return writer;
  }

  private void printOptions(StringWriter writer) {
    System.out.println( writer.toString() );
  }

  private int readAction() throws Exception {
    return Integer.parseInt( read() );
  }

  private String read() throws Exception {
    BufferedReader reader = new BufferedReader( new InputStreamReader(System.in) );
    return reader.readLine();
  }

  private String readFilename() throws Exception {
    System.out.println("Enter the absolute path of the file:");
    return read();
  }

  private boolean evaluateAction(int action, INode node) throws Exception {
    boolean exit = false;

    switch (action) {
      case 0: showProfile( node );
        break;

      case 1: addFile( node );
        break;

      case 2: removeFile( node );
        break;

      case 3: showFileIndex( node );
        break;

      case 4: showLog( node );
        break;

      case 5: showSummary( node );
        break;

      case 6: showAckSummary( node );
        break;

      case 7: runSyncSession( node );
        break;

      default: exit = true;
        break;
    }

    return exit;
  }

  private void showProfile(INode node) throws Exception {
    Peer profile = node.requestProfile();

    VelocityContext context = new VelocityContext();
    context.put( "profile", profile );
    StringWriter writer = prepareTemplate( "peer.vm", context );

    System.out.println( writer.toString() );
  }

  private void addFile(INode node) throws Exception {
    String file  = readFilename();
    boolean done = node.add( file );

    if (done) {
      System.out.println( "File added succesfully." );
    } else {
      System.out.println( "An error occurred while adding the file." );
    }
  }

  private void removeFile(INode node) throws Exception {
    String file  = readFilename();
    boolean done = node.remove( file );

    if (done) {
      System.out.println( "File removed succesfully." );
    } else {
      System.out.println( "An error occurred while removing the file." );
    }
  }

  private void showFileIndex(INode node) throws Exception {
    List<FileData> files = node.requestFileIndex();

    VelocityContext context = new VelocityContext();
    context.put( "files", files );
    StringWriter writer = prepareTemplate( "fileIndex.vm", context );

    System.out.println( writer.toString() );
  }

  private void showLog(INode node) throws Exception {
    List<Operation> ops = node.requestLog();

    VelocityContext context = new VelocityContext();
    context.put( "ops", ops );
    StringWriter writer = prepareTemplate( "log.vm", context );

    System.out.println( writer.toString() );
  }

  private void showSummary(INode node) throws Exception {
    ConcurrentHashMap<String, Timestamp> summary = node.requestSummary();

    VelocityContext context = new VelocityContext();
    context.put( "summary", summary );
    StringWriter writer = prepareTemplate( "summary.vm", context );

    System.out.println( writer.toString() );
  }

  private void showAckSummary(INode node) throws Exception {
    ConcurrentHashMap<String, ConcurrentHashMap<String, Timestamp>> ackSummary = node.requestAckSummary();

    VelocityContext context = new VelocityContext();
    context.put( "ackSummary", ackSummary );
    StringWriter writer = prepareTemplate( "ackSummary.vm", context );

    System.out.println( writer.toString() );
  }

  private void runSyncSession(INode node) throws Exception {
    boolean done = node.startTSAESession();

    if (done) {
      System.out.println( "Synchronization run succesfully." );
    } else {
      System.out.println( "An error occurred while running synchronization session." );
    }
  }

}