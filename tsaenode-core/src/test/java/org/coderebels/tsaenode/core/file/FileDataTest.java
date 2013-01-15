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

import org.coderebels.tsaenode.core.common.Timestamp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Unit test for the {@link org.coderebels.tsaenode.core.file.FileData} class.
 *
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
@RunWith(JUnit4.class)
public class FileDataTest {

  private FileData fd1, fd2;

  /**
   * {@inheritDoc}
   */
  @Before public void setUp() {
    Timestamp ts = new Timestamp();
    ts.setNodeId( "node1" );
    ts.setSeqNumber( 1 );

    fd1 = new FileData();
    fd1.setFilename( "filename" );
    fd1.setOwner( "node1" );
    fd1.setPath( "/tmp/tsaenode/filename" );
    fd1.setURI( "http://node1/tsaenode/filename" );
    fd1.setTimestamp( ts );

    fd2 = new FileData();
    fd2.setFilename( "filename" );
    fd2.setOwner( "node1" );
    fd2.setPath( "/tmp/tsaenode/filename" );
    fd2.setURI( "http://node1/tsaenode/filename" );
    fd2.setTimestamp( ts );
  }

  /**
   * {@inheritDoc}
   */
  @After public void tearDown() {
    fd1 = null;
    fd2 = null;
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileData#equals(java.lang.Object)} method
   * when both are equal.
   */
  @Test public void testEquals() {
    assertThat( "FileDatas should be equal", fd1, equalTo(fd2) );
  }
}
