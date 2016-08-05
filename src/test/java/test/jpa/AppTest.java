package test.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;

import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import test.jpa.VertexRegister.OProperty;

/**
 * Unit test for simple App.
 */
public class AppTest {
  private static EntityManager em;
  private static OrientGraphFactory factory = new OrientGraphFactory("memory:./testdb");

  @BeforeClass
  public static void setup() {
    em = Persistence.createEntityManagerFactory("test").createEntityManager();

  }

  @Test
  public void persistTest() {
    Element element = new Element();
    element.setName("test");
    EntityTransaction tx = em.getTransaction();
    tx.begin();
    em.persist(element);
    tx.commit();
    System.out.println(em.createNamedQuery("Element.findAll", Element.class).getResultList());;
  }

  @Test
  public void test() {
    List<Element> list = em.createNamedQuery("Element.findAll", Element.class).getResultList();
  }

  @Test
  public void createDB() {
    OSchema scheme = factory.getDatabase().getMetadata().getSchema();
    System.out.println(scheme.getClass("X").properties());;
    // scheme.dropClass(X.class.getSimpleName());
    // scheme.createClass(X.class.getSimpleName(),scheme.getClass(OClass.VERTEX_CLASS_NAME));

    // System.out.println();
    OrientGraph graph = factory.getTx();
    // graph.createVertexType("VV","X");
    graph.commit();

  }

  @Test
  public void registerTest() {
    VertexRegister register = new VertexRegisterImpl();

    // factory.getNoTx().dropVertexType("X");
    OrientGraph graph = factory.getTx();
    register.regist(graph, X.class);
    graph.commit();
    System.out.println(graph.getVertexType("X").properties());

  }

  @Test
  public void addVertexTest() {
    // System.out.println(factory.getDatabase().getMetadata().getSchema().getClass(X.class).setStrictMode(true));

    OrientGraph graph = factory.getTx();
    Vertex v = graph.addVertex("class:X");
    v.setProperty("test", "aaa");
    graph.commit();

    for (Vertex vertex : graph.getVerticesOfClass("X", true)) {
      System.out.println((String) vertex.getProperty("test"));
    }
  }


  public static class X extends OrientVertex {
    @OProperty
    public String test;
  }


}
