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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Unit test for the {@link org.coderebels.tsaenode.core.common.Timestamp} class.
 *
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
@RunWith(JUnit4.class)
public class TimestampTest {

  private Timestamp ts1, ts2;

  /**
   * {@inheritDoc}
   */
  @Before public void setUp() {
    ts1 = new Timestamp();
    ts1.setNodeId( "node1" );
    ts1.setSeqNumber( 1 );

    ts2 = new Timestamp();
    ts2.setNodeId( "node2" );
    ts2.setSeqNumber( 2 );
  }

  /**
   * {@inheritDoc}
   */
  @After public void tearDown() {
    ts1 = null;
    ts2 = null;
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.common.Timestamp#compare(org.coderebels.tsaenode.core.common.Timestamp)} method
   * when receiver is previous.
   */
  @Test public void testCompare() {
    int comparision = ts1.compare( ts2 );

    assertThat( "Timestamp comparision should return -1", comparision, is(-1) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.common.Timestamp#equals(java.lang.Object)} method
   * when both are equal.
   */
  @Test public void testEquals() {
    Timestamp ts = new Timestamp();
    ts.setNodeId( "node1" );
    ts.setSeqNumber( 1 );

    assertThat( "Timestamps should be equal", ts1, equalTo(ts) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.common.Timestamp#equals(java.lang.Object)} method
   * when both are different.
   */
  @Test public void testNotEquals() {
    assertThat( "Timestamps should not be equal", ts1, not(equalTo(ts2)) );
  }
}
