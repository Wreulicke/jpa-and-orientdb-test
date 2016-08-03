package test.jpa;

import javax.persistence.EntityTransaction;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

public class MockUserTransaction implements UserTransaction {
  EntityTransaction transaction;
  int status;
  private MockUserTransaction(EntityTransaction transaction) {
    this.transaction=transaction;
  }
  @Override
  public void begin() throws NotSupportedException, SystemException {
    status=Status.STATUS_PREPARING;
    transaction.begin();
    status=Status.STATUS_ACTIVE;
  }

  @Override
  public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException,
      SystemException {
    status=Status.STATUS_COMMITTING;
    transaction.commit();
    status=Status.STATUS_NO_TRANSACTION;
  }

  @Override
  public void rollback() throws IllegalStateException, SecurityException, SystemException {
    status=Status.STATUS_ROLLEDBACK;
    transaction.rollback();
    status=Status.STATUS_NO_TRANSACTION;
  }

  @Override
  public void setRollbackOnly() throws IllegalStateException, SystemException {
    transaction.setRollbackOnly();
  }

  @Override
  public int getStatus() throws SystemException {
    return status;
  }

  @Override
  public void setTransactionTimeout(int seconds) throws SystemException {
    // TODO implement?
  }
  public static UserTransaction createMock(EntityTransaction transaction){
    return new MockUserTransaction(transaction);
  }

}
