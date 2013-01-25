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

import java.util.Vector;

import org.coderebels.tsaenode.core.common.Timestamp;
import org.coderebels.tsaenode.BaseTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.mockito.Spy;
import org.mockito.InOrder;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.reflect.Whitebox.*;

/**
 * Unit test for the {@link org.coderebels.tsaenode.core.file.FileIndex} class.
 *
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
@RunWith(JUnit4.class)
public class FileIndexTest extends BaseTestCase {

  private FileData fd;
  @Spy private Vector<FileData> data;
  @Spy private FileIndex theFileIndex;

  /**
   * {@inheritDoc}
   */
  @Before public void setUp() {
    Timestamp ts = new Timestamp();
    ts.setNodeId( "node1" );
    ts.setSeqNumber( 1 );

    fd = new FileData();
    fd.setFilename( "filename" );
    fd.setOwner( "node1" );
    fd.setPath( "/tmp/tsaenode/filename" );
    fd.setURI( "http://node1/tsaenode/filename" );
    fd.setTimestamp( ts );

    setInternalState( theFileIndex, data );
  }

  /**
   * {@inheritDoc}
   */
  @After public void tearDown() {
    theFileIndex = null;
    data = null;
    fd = null;
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileIndex#add(org.coderebels.tsaenode.core.file.FileData)} method
   * when file is not indexed yet.
   */
  @Test public void testAdd_fileNotIndexedYet() {
    String filePath = fd.getPath();
    doReturn(null).when(theFileIndex).search(filePath);

    theFileIndex.add( fd );

    InOrder inOrder = inOrder( theFileIndex, data );
    inOrder.verify(theFileIndex).search(filePath);
    inOrder.verify(data, never()).remove(fd);
    inOrder.verify(data).add(fd);

    assertThat( "FileIndex should contain file", data, hasItem(fd) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileIndex#add(org.coderebels.tsaenode.core.file.FileData)} method
   * when file is already indexed.
   */
  @Test public void testAdd_fileAlreadyIndexed() {
    String filePath = fd.getPath();
    doReturn(fd).when(theFileIndex).search(filePath);
    doReturn(true).when(data).remove(fd);

    theFileIndex.add( fd );

    InOrder inOrder = inOrder( theFileIndex, data );
    inOrder.verify(theFileIndex).search(filePath);
    inOrder.verify(data).remove(fd);
    inOrder.verify(data).add(fd);

    assertThat( "FileIndex should contain file", data, hasItem(fd) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileIndex#search(java.lang.String)} method
   * when file is present.
   */
  @Test public void testSearch_fileIsPresent() {
    theFileIndex.add( fd );

    FileData fdd = theFileIndex.search( fd.getPath() );

    assertThat( "FileIndex search should return file", fdd, equalTo(fd) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileIndex#search(java.lang.String)} method
   * when file is absent.
   */
  @Test public void testSearch_fileIsAbsent() {
    FileData fdd = theFileIndex.search( fd.getFilename() );

    assertThat( "FileIndex search should return null", fdd, nullValue() );
  }
}
