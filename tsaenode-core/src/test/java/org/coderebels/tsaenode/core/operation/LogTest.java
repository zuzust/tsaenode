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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.coderebels.tsaenode.core.common.Timestamp;
import org.coderebels.tsaenode.core.file.FileData;
import org.coderebels.tsaenode.BaseTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.powermock.reflect.Whitebox.*;

/**
 * Unit test for the {@link org.coderebels.tsaenode.core.operation.Log} class.
 *
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
@RunWith(JUnit4.class)
public class LogTest extends BaseTestCase {

  private Operation op;
  private Log theLog;

  /**
   * {@inheritDoc}
   */
  @Before public void setUp() {
    Timestamp ts = new Timestamp();
    ts.setNodeId( "node1" );
    ts.setSeqNumber( 1 );

    FileData fd = new FileData();
    fd.setFilename( "filename" );
    fd.setOwner( "node1" );
    fd.setPath( "/tmp/tsaenode/filename" );
    fd.setURI( "http://node1/tsaenode/filename" );
    fd.setTimestamp( ts );

    op = new Operation();
    op.setType( Operation.ADD );
    op.setFile( fd );
    op.setTimestamp( ts );

    theLog = new Log();
  }

  /**
   * {@inheritDoc}
   */
  @After public void tearDown() {
    theLog = null;
    op = null;
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.Log#add(org.coderebels.tsaenode.core.operation.Operation)} method.
   */
  @Test public void testAdd() {
    theLog.add(op);

    Timestamp ts   = op.getTimestamp();
    String nodeId  = ts.getNodeId();
    long seqNumber = ts.getSeqNumber();
    ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Operation>> data = getInternalState( theLog, "data" );

    assertThat( "should contain node operation log", data.containsKey(nodeId), is(true) );
    assertThat( "should contain operation", data.get(nodeId).get(seqNumber), equalTo(op) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.Log#removeAllPreceding(java.lang.String, org.coderebels.tsaenode.core.common.Timestamp)} method.
   */
  @Test public void testRemoveAllPreceding() {
    theLog.add(op);

    Timestamp ts = op.getTimestamp();
    String nodeId  = ts.getNodeId();
    long seqNumber = ts.getSeqNumber();

    Timestamp lastSeen = new Timestamp();
    lastSeen.setNodeId( nodeId );
    lastSeen.setSeqNumber( seqNumber + 1 );

    theLog.removeAllPreceding( nodeId, lastSeen );    
    ConcurrentHashMap<String, ConcurrentSkipListMap<Long, Operation>> data = getInternalState( theLog, "data" );

    assertThat( "should be empty", data.get(nodeId).isEmpty(), is(true) );
  }

}
