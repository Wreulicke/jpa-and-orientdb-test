package test.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public interface VertexRegister {

  public <V extends OrientVertex> void registProperty(OrientVertexType type, Class<V> vertexClass);
  public <V extends OrientVertex> void regist(OrientGraph factory, Class<V> vertexClass);
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD})
  public @interface OProperty{
    public OType value() default OType.ANY;
  }
  
}
