package test.jpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(WeldJUnit4Runner.class)
public class ContainerTest {
  // TODO fix annotation
  @Inject
  @PersistenceContext(unitName="test")
  EntityManager em;
  
  @Inject
  TestBean testBean;
  
  @BeforeClass
  public static void before(){
  }
  @Test
  public void test() throws Exception{

    testBean.print();
    TestBean rawBean=(TestBean) testBean.getClass().getDeclaredMethod("getTargetInstance").invoke(testBean);
    System.out.println(rawBean.em);
    System.out.println(em);
  }
}
