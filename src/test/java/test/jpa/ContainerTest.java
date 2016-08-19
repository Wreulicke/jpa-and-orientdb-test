package test.jpa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.arnx.jsonic.JSON;
import test.jpa.ClassModifierExtension.Target;


@RunWith(WeldJUnit4Runner.class)
public class ContainerTest {
  @PersistenceContext(unitName="test")
  EntityManager em;
  
  @Inject
  TestBean testBean;
  @Inject
  BeanManager manager;
  @Inject
  @Target
  ExtendsTarget test;
  @Inject
  Instance<ExtendsTarget> tes;
  @Inject
  ClassInfoPrinter printer;
  @BeforeClass
  public static void before(){
  }
  @Test
  public void test() throws Exception{

    testBean.print();
    TestBean rawBean=(TestBean) testBean.getClass().getDeclaredMethod("getTargetInstance").invoke(testBean);
    
    System.out.println(rawBean.em);
    System.out.println(em);
    em.getTransaction().begin();
    em.persist(new Element());
    em.persist(new Element());
    em.persist(new Element());
    em.persist(new Element());
    em.persist(new Element());
    em.getTransaction().commit();
    System.out.println(em.createNamedQuery("Element.findAll",Element.class).getResultList().size());
    
  }
  @Test
  public void classModifiedTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
    ExtendsTarget rawBean=(ExtendsTarget) test.getClass().getDeclaredMethod("getTargetInstance").invoke(test);
    printer.print(rawBean);
    printer.print(test);
    printer.print(tes.get());
    printer.print(new ExtendsTarget(){
      private void name() {
        
      }
    });
    JSON json=new JSON(){
      @Override
      protected boolean ignore(Context context, Class<?> target, Member member) {
        return true;
      }
    };
    json.setPrettyPrint(true);
    System.out.println(JSON.encode(test));
  }
}
