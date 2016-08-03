package test.jpa;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class TestBean {
  @Inject
  Element element;
  @Inject
  @PersistenceContext(unitName="test")
  EntityManager em;
  
  public void print(){
    System.out.println(element);
    System.out.println(em);
  }
}
