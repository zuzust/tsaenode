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

import org.coderebels.tsaenode.core.common.Timestamp;
import org.coderebels.tsaenode.core.file.FileData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Unit test for the {@link org.coderebels.tsaenode.core.operation.Operation} class.
 *
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
@RunWith(JUnit4.class)
public class OperationTest {

  private Operation op1, op2;

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

    op1 = new Operation();
    op1.setType( Operation.ADD );
    op1.setFile( fd );
    op1.setTimestamp( ts );

    op2 = new Operation();
    op2.setType( Operation.ADD );
    op2.setFile( fd );
    op2.setTimestamp( ts );
  }

  /**
   * {@inheritDoc}
   */
  @After public void tearDown() {
    op1 = null;
    op2 = null;
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.Operation#equals(java.lang.Object)} method
   * when both are equal.
   */
  @Test public void testEquals() {
    assertThat( "Operations should be equal", op1, equalTo(op2) );
  }

}