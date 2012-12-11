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

import java.util.concurrent.ConcurrentHashMap;

import org.coderebels.tsaenode.BaseTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.mockito.Spy;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.reflect.Whitebox.*;

/**
 * Unit test for the {@link org.coderebels.tsaenode.core.common.Summary} class.
 *
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
@RunWith(JUnit4.class)
public class SummaryTest extends BaseTestCase {

  private Timestamp ts;
  @Spy private ConcurrentHashMap<String, Timestamp> data;
  private Summary theSummary;

  /**
   * {@inheritDoc}
   */
  @Before public void setUp() {
    ts = new Timestamp();
    ts.setNodeId( "node1" );
    ts.setSeqNumber( 10 );

    theSummary = new Summary();

    setInternalState(theSummary, data);
  }

  /**
   * {@inheritDoc}
   */
  @After public void tearDown() {
    theSummary = null;
    data = null;
    ts = null;
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.common.Summary#update(org.coderebels.tsaenode.core.common.Timestamp)} method
   * when timestamp has not been summarized yet
   */
  @Test public void testUpdate_timestampNotSummarizedYet() {
    String nodeId = ts.getNodeId();

    Timestamp last = new Timestamp();
    last.setNodeId( nodeId );
    last.setSeqNumber( 1 );

    doReturn(last).when(data).get(nodeId);

    theSummary.update( ts );

    verify(data).put(nodeId, ts);
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.common.Summary#update(org.coderebels.tsaenode.core.common.Timestamp)} method
   * when timestamp has already been summarized
   */
  @Test public void testUpdate_timestampAlreadySummarized() {
    String nodeId = ts.getNodeId();

    Timestamp last = new Timestamp();
    last.setNodeId( nodeId );
    last.setSeqNumber( 100 );

    doReturn(last).when(data).get(nodeId);

    theSummary.update( ts );

    verify(data, never()).put(nodeId, ts);
  }

}
