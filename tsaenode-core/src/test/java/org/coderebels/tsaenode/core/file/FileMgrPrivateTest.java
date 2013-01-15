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
import org.coderebels.tsaenode.core.exception.FileMgrException;
import org.coderebels.tsaenode.BaseTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.mockito.Spy;
import org.mockito.Mock;

import org.powermock.modules.junit4.rule.PowerMockRule;
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
@RunWith(JUnit4.class)
@PrepareForTest(FileMgr.class)
@PowerMockIgnore("org.apache.logging.log4j.*")
public class FileMgrPrivateTest extends BaseTestCase {

  @Rule public PowerMockRule rule = new PowerMockRule();
  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  private File file, pubFolder;
  private FileData fd;

  @Mock private FileIndex fileIndex;
  @Spy private FileMgr theFileMgr = new FileMgr();

  /**
   * {@inheritDoc}
   */
  @Before public void setUp() throws Exception {
    file      = tempFolder.newFile( "filename" );
    pubFolder = tempFolder.newFolder( "tsaenode" );

    Timestamp ts = new Timestamp();
    ts.setNodeId( "localnode" );
    ts.setSeqNumber( 10 );

    fd = new FileData();
    fd.setFilename( file.getName() );
    fd.setOwner( "localnode" );
    fd.setPath( file.getAbsolutePath() );
    fd.setTimestamp( ts );

    setInternalState( theFileMgr, "localNodeId", "localnode" );
    setInternalState( theFileMgr, "pubFolderPath", pubFolder.getAbsolutePath() );
    setInternalState( theFileMgr, "pubFolderURI", "http://localnode/tsaenode" );
    setInternalState( theFileMgr, fileIndex );
  }

  /**
   * {@inheritDoc}
   */
  @After public void tearDown() {
    theFileMgr = null;
    fileIndex  = null;
    fd = null;
    file = null;
    pubFolder = null;
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileMgr#doAddFile(org.coderebels.tsaenode.core.file.FileData)} method
   * when file exists.
   *
   * @throws java.lang.Exception
   */
  @Test public void testDoAddFile_existingFile() throws Exception {
    invokeMethod( theFileMgr, "doAddFile", fd );

    String pubFolderPath = getInternalState( theFileMgr, "pubFolderPath" );
    File file = new File( pubFolderPath + File.separator + fd.getFilename() );

    assertTrue( "File couldn't be added", file.exists() );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileMgr#doAddFile(org.coderebels.tsaenode.core.file.FileData)} method
   * when file doesn't exist.
   *
   * @throws java.lang.Exception
   */
  @Test public void testDoAddFile_nonexistentFile() throws Exception {
    fd.setPath( "/non/existent/filename" );

    try {
      invokeMethod( theFileMgr, "doAddFile", fd );
      fail( "Expected a FileMgrException to be thrown" );
    } catch (Exception e) {
      assertThat( "FileMgr didn't throw a FileMgrException", e, is(instanceOf(FileMgrException.class)) );
    }
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileMgr#doRemoveFile(org.coderebels.tsaenode.core.file.FileData)} method
   * when file exists.
   *
   * @throws java.lang.Exception
   */
  @Test public void testDoRemoveFile_existingFile() throws Exception {
    File file = new File( pubFolder.getAbsolutePath() + File.separator + "filename" );
    file.createNewFile();
    fd.setPath( file.getAbsolutePath() );

    invokeMethod( theFileMgr, "doRemoveFile", fd );

    assertFalse( "File couldn't be removed", file.exists() );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.file.FileMgr#doRemoveFile(org.coderebels.tsaenode.core.file.FileData)} method
   * when file doesn't exist.
   *
   * @throws java.lang.Exception
   */
  @Test public void testDoRemoveFile_nonexistentFile() throws Exception {
    fd.setPath( "/non/existent/filename" );

    try {
      invokeMethod( theFileMgr, "doRemoveFile", fd );
      fail( "Expected a FileMgrException to be thrown" );
    } catch (Exception e) {
      assertThat( "FileMgr didn't throw a FileMgrException", e, is(instanceOf(FileMgrException.class)) );
    }
  }

}
