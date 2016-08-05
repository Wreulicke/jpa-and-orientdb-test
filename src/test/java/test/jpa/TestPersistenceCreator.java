package test.jpa;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

public class TestPersistenceCreator {
  
//  @Produces
  @PersistenceContext(unitName="")
  public EntityManager create(InjectionPoint ip){
    return Persistence.createEntityManagerFactory(ip.getAnnotated().getAnnotation(PersistenceContext.class).unitName()).createEntityManager();
  }
}
