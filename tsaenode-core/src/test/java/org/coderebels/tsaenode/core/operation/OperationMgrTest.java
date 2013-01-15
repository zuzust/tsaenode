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
import org.coderebels.tsaenode.core.common.Summary;
import org.coderebels.tsaenode.core.file.FileData;
import org.coderebels.tsaenode.core.file.IFileMgr;
import org.coderebels.tsaenode.core.exception.OperationMgrException;
import org.coderebels.tsaenode.BaseTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Spy;
import org.mockito.Mock;

import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyString;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.reflect.Whitebox.*;

/**
 * Unit test for the {@link org.coderebels.tsaenode.core.operation.OperationMgr} class.
 *
 * @author carles.ml.dev@gmail.com (Carles Muiños)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(OperationMgr.class)
@PowerMockIgnore("org.apache.logging.log4j.*")
public class OperationMgrTest extends BaseTestCase {

  private Timestamp ts;
  private FileData fd;

  @Mock private IFileMgr fileMgr;
  @Mock private Log log;
  @Mock private Summary summary;
  @Spy private OperationMgr theOperationMgr = new OperationMgr(null);

  /**
   * {@inheritDoc}
   */
  @Before public void setUp() {
    ts = new Timestamp();
    ts.setNodeId( "node1" );
    ts.setSeqNumber( 10 );

    fd = new FileData();
    fd.setFilename( "filename" );
    fd.setOwner( "node1" );
    fd.setPath( "/tmp/tsaenode/filename" );
    fd.setURI( "http://node1/tsaenode/filename" );
    fd.setTimestamp( ts );

    setInternalState( theOperationMgr, fileMgr );
    setInternalState( theOperationMgr, log );
    setInternalState( theOperationMgr, summary );
  }

  /**
   * {@inheritDoc}
   */
  @After public void tearDown() {
    theOperationMgr = null;
    summary = null;
    log     = null;
    fileMgr = null;
    fd = null;
    ts = null;
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.OperationMgr#createOperation(java.lang.Integer, java.lang.String)} method
   * when operation is of type ADD
   *
   * @throws java.lang.Exception
   */
  @Test public void testCreateOperation_validAddOperation() throws Exception {
    when(fileMgr.createFileData(anyString())).thenReturn(fd);

    Operation op = theOperationMgr.createOperation( Operation.ADD, fd.getPath() );

    verify(fileMgr).createFileData(fd.getPath());
    assertThat( op, is(notNullValue()) );
    assertThat( "An instance of Operation should be created", op, is(instanceOf(Operation.class)) );
    assertThat( "Operation should be of type ADD", op.getType(), is(Operation.ADD) );
    assertThat( "Operation should wrap the target file data", op.getFile(), is(equalTo(fd)) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.OperationMgr#createOperation(java.lang.Integer, java.lang.String)} method
   * when operation is of type REMOVE
   *
   * @throws java.lang.Exception
   */
  @Test public void testCreateOperation_validRemoveOperation() throws Exception {
    when(fileMgr.searchFileData(anyString())).thenReturn(fd);

    Operation op = theOperationMgr.createOperation( Operation.REMOVE, fd.getPath() );

    verify(fileMgr).searchFileData(fd.getPath());
    assertThat( op, is(notNullValue()) );
    assertThat( "An instance of Operation should be created", op, is(instanceOf(Operation.class)) );
    assertThat( "Operation should be of type REMOVE", op.getType(), is(Operation.REMOVE) );
    assertThat( "Operation should wrap the target file data", op.getFile(), is(equalTo(fd)) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.OperationMgr#createOperation(java.lang.Integer, java.lang.String)} method
   * when operation is of type REMOVE and target file doesn't exist
   *
   * @throws java.lang.Exception
   */
  @Test public void testCreateOperation_invalidRemoveOperation() throws Exception {
    when(fileMgr.searchFileData(anyString())).thenReturn(null);

    Operation op = null;
    try {
      op = theOperationMgr.createOperation( Operation.REMOVE, "/non/existent/filename" );
      fail( "Expected an OperationMgrException to be thrown" );
    } catch (Exception e) {
      assertThat( "OperationMgr should throw an OperationMgrException", e, is(instanceOf(OperationMgrException.class)) );
      assertThat( "Operation creation should return null", op, is(nullValue()) );
    }
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.OperationMgr#executeOperation(org.coderebels.tsaenode.core.operation.Operation)} method
   * when operation has already been executed
   *
   * @throws java.lang.Exception
   */
  @Test public void testExecuteOperation_alreadyExecuted() throws Exception {
    Operation op = mock( Operation.class );
    doReturn(true).when(theOperationMgr, "checkIsAlreadyExecuted", op);

    boolean done = theOperationMgr.executeOperation( op );

    verifyPrivate(theOperationMgr).invoke("checkIsAlreadyExecuted", op);
    verifyZeroInteractions( fileMgr, log, summary );
    assertThat( "Operation execution should return true", done, is(true) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.OperationMgr#executeOperation(org.coderebels.tsaenode.core.operation.Operation)} method
   * when valid ADD operation to be executed
   *
   * @throws java.lang.Exception
   */
  @Test public void testExecuteOperation_validAddOperation() throws Exception {
    Operation op = new Operation();
    op.setType( Operation.ADD );
    op.setFile( fd );
    op.setTimestamp( ts );

    doReturn(false).when(theOperationMgr, "checkIsAlreadyExecuted", op);
    when(fileMgr.addFile(fd)).thenReturn(true);

    boolean done = theOperationMgr.executeOperation( op );

    verify(fileMgr).addFile(fd);
    verify(log).add(op);
    verify(summary).update(ts);
    assertThat( "Operation execution should return true", done, is(true) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.OperationMgr#executeOperation(org.coderebels.tsaenode.core.operation.Operation)} method
   * when valid REMOVE operation to be executed
   *
   * @throws java.lang.Exception
   */
  @Test public void testExecuteOperation_validRemoveOperation() throws Exception {
    Operation op = new Operation();
    op.setType( Operation.REMOVE );
    op.setFile( fd );
    op.setTimestamp( ts );

    doReturn(false).when(theOperationMgr, "checkIsAlreadyExecuted", op);
    when(fileMgr.removeFile(fd)).thenReturn(true);

    boolean done = theOperationMgr.executeOperation( op );

    verify(fileMgr).removeFile(fd);
    verify(log).add(op);
    verify(summary).update(ts);
    assertThat( "Operation execution should return true", done, is(true) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.OperationMgr#executeOperation(org.coderebels.tsaenode.core.operation.Operation)} method
   * when invalid REMOVE operation to be executed
   *
   * @throws java.lang.Exception
   */
  @Test public void testExecuteOperation_invalidRemoveOperation() throws Exception {
    Timestamp its = new Timestamp();
    its.setNodeId( "node2" );
    its.setSeqNumber( 2 );

    Operation op = new Operation();
    op.setType( Operation.REMOVE );
    op.setFile( fd );
    op.setTimestamp( its );

    doReturn(false).when(theOperationMgr, "checkIsAlreadyExecuted", op);

    boolean done = false;

    try {
      done = theOperationMgr.executeOperation( op );
      fail( "Expected an OperationMgrException to be thrown" );
    } catch (Exception e) {
      verifyZeroInteractions( fileMgr, log, summary );
      assertThat( "OperationMgr should throw an OperationMgrException", e, is(instanceOf(OperationMgrException.class)) );
      assertThat( "Operation execution should return false", done, is(false) );
    }
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.OperationMgr#checkIsAlreadyExecuted(org.coderebels.tsaenode.core.operation.Operation)} method
   * when operation has already been executed
   *
   * @throws java.lang.Exception
   */
  @Test public void testCheckIsAlreadyExecuted_alreadyExecuted() throws Exception {
    Timestamp actual = new Timestamp();
    actual.setNodeId( "node1" );
    actual.setSeqNumber( 100 );

    Summary summary = new Summary();
    summary.update( actual );
    setInternalState( theOperationMgr, summary );

    Operation op = new Operation();
    op.setType( Operation.ADD );
    op.setFile( fd );
    op.setTimestamp( ts );

    boolean executed = invokeMethod( theOperationMgr, "checkIsAlreadyExecuted", op );

    assertThat( "Operation execution check should return true", executed, is(true) );
  }

  /**
   * Test for the {@link org.coderebels.tsaenode.core.operation.OperationMgr#checkIsAlreadyExecuted(org.coderebels.tsaenode.core.operation.Operation)} method
   * when operation not yet executed
   *
   * @throws java.lang.Exception
   */
  @Test public void testCheckIsAlreadyExecuted_notYetExecuted() throws Exception {
    Timestamp actual = new Timestamp();
    actual.setNodeId( "node1" );
    actual.setSeqNumber( 1 );

    Summary summary = new Summary();
    summary.update( actual );
    setInternalState( theOperationMgr, summary );

    Operation op = new Operation();
    op.setType( Operation.ADD );
    op.setFile( fd );
    op.setTimestamp( ts );

    boolean executed = invokeMethod( theOperationMgr, "checkIsAlreadyExecuted", op );

    assertThat( "Operation execution check should return false", executed, is(false) );
   }

}
