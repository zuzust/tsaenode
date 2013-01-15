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

import java.io.File;

import org.coderebels.tsaenode.core.common.Timestamp;
import org.coderebels.tsaenode.BaseTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import org.mockito.Spy;
import org.mockito.Mock;

import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.reflect.Whitebox.*;

/**
 * Unit test for the {@link org.coderebels.tsaenode.core.file.FileMgr} class.
 *
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FileMgr.class)
@PowerMockIgnore("org.apache.logging.log4j.*")
public class FileMgrTest extends BaseTestCase {

  private FileData flocal, fremote;

  @Spy private FileIndex fileIndex = new FileIndex();
  @Spy private FileMgr theFileMgr  = new FileMgr();

  /**
   * {@inheritDoc}
   */
  @Before public void setUp() throws Exception {
    Timestamp tlocal = new Timestamp();
    tlocal.setNodeId( "localnode" );
    tlocal.setSeqNumber( 10 );

    flocal = new FileData();
    flocal.setFilename( "filename" );
    flocal.setOwner( "localnode" );
    flocal.setPath( "/tmp/tsaenode/filename" );
    flocal.setURI( "http://localnode/tsaenode/filename" );
    flocal.setTimestamp( tlocal );

    Timestamp tremote = new Timestamp();
    tremote.setNodeId( "remotenode" );
    tremote.setSeqNumber( 10 );

    fremote = new FileData();
    fremote.setFilename( "filename" );
    fremote.setOwner( "remotenode" );
    fremote.setPath( "/tmp/tsaenode/filename" );
    fremote.setURI( "http://remotenode/tsaenode/filename" );
    fremote.setTimestamp( tremote );

    setInternalState( theFileMgr, "localNodeId", "localnode" );
    setInternalState( theFileMgr, "pubFolderPath", "/tmp/tsaenode/" );
    setInternalState( theFileMgr, "pubFolderURI", "http://localnode/tsaenode/" );
    setInternalState( theFileMgr, fileIndex );
  }

  /**
   * {@inheritDoc}
   */
  @After public void tearDown() {
    theFileMgr = null;
    fileIndex  = null;
    flocal  = null;
    fremote = null;
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileMgr#addFile(org.coderebels.tsaenode.core.file.FileData)} method
   * when file is local
   *
   * @throws java.lang.Exception
   */
  @Test public void testAddFile_localFile() throws Exception {
    doReturn(true).when(theFileMgr, "doAddFile", flocal);

    boolean done = theFileMgr.addFile( flocal );

    verifyPrivate(theFileMgr).invoke("doAddFile", flocal);
    verify(fileIndex).add(flocal);
    assertThat( "File should be added", done, is(true) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileMgr#addFile(org.coderebels.tsaenode.core.file.FileData)} method
   * when file is remote
   *
   * @throws java.lang.Exception
   */
  @Test public void testAddFile_remoteFile() throws Exception {
    boolean done = theFileMgr.addFile( fremote );

    verifyPrivate(theFileMgr, never()).invoke("doAddFile", fremote);
    verify(fileIndex).add(fremote);
    assertThat( "File should be added", done, is(true) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileMgr#removeFile(org.coderebels.tsaenode.core.file.FileData)} method
   * when file is local
   *
   * @throws java.lang.Exception
   */
  @Test public void testRemoveFile_localFile() throws Exception {
    doReturn(true).when(theFileMgr, "doRemoveFile", flocal);

    boolean done = theFileMgr.removeFile( flocal );

    verifyPrivate(theFileMgr).invoke("doRemoveFile", flocal);
    verify(fileIndex).remove(flocal);
    assertThat( "File should be removed", done, is(true) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileMgr#removeFile(org.coderebels.tsaenode.core.file.FileData)} method
   * when file is remote
   *
   * @throws java.lang.Exception
   */
  @Test public void testRemoveFile_remoteFile() throws Exception {
    boolean done = theFileMgr.removeFile( fremote );

    verifyPrivate(theFileMgr, never()).invoke("doRemoveFile", fremote);
    verify(fileIndex).remove(fremote);
    assertThat( "File should be removed", done, is(true) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileMgr#createFileData(java.lang.String)} method.
   *
   * @throws java.lang.Exception
   */
  @Test public void testCreateFileData() throws Exception {
    FileData fd = theFileMgr.createFileData( "/tmp/tsaenode/filename" );

    assertThat( fd, is(notNullValue()) );
    assertThat( "An instance of FileData should be created", fd, is(instanceOf(FileData.class)) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileMgr#searchFileData(java.lang.String)} method.
   *
   * @throws java.lang.Exception
   */
  @Test public void testSearchFileData() throws Exception {
    doReturn(true).when(theFileMgr, "doAddFile", flocal);
    theFileMgr.addFile( flocal );

    FileData fd = theFileMgr.searchFileData( flocal.getFilename() );

    assertThat( "FileData should be found", fd, is(equalTo(flocal)) );
  }

}
