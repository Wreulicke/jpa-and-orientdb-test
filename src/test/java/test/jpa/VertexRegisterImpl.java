package test.jpa;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertexType;

public class VertexRegisterImpl implements VertexRegister{
  public static final Map<Class<?>, OType> classMapping=new HashMap<>();
  static{
    for (OType type : OType.values()) {
      classMapping.put(type.getDefaultJavaType(), type);
    }
  }
  @Override
  public<T extends OrientVertex> void regist(OrientGraph graph, Class<T> vertexClass) {
    String clazzName=vertexClass.getSimpleName();
    OrientVertexType type=graph.getVertexBaseType();
    if(graph.getVertexType(clazzName)==null){
      Class<?> superClazz=vertexClass.getSuperclass();
      String superClazzName=superClazz.equals(OrientVertex.class)?
          OClass.VERTEX_CLASS_NAME :
          superClazz.getSimpleName();
      type=graph.createVertexType(clazzName, superClazzName);
      this.registProperty(type, vertexClass);
      
    }else this.registProperty(type, vertexClass);
  }

  @Override
  public <V extends OrientVertex> void registProperty(OrientVertexType type, Class<V> vertexClass) {
    Field[] fields=vertexClass.getDeclaredFields();
    for (Field field : fields) {
      OProperty annotation=field.getAnnotation(OProperty.class);
      
      if(annotation==null)continue;
      Class<?> fieldType=field.getType();

      System.out.println("test");
      if(fieldType==null)continue;  // TODO should throw exception?
      
      OType oType=annotation.value();
      if(OType.ANY.equals(oType))oType=classMapping.get(fieldType);

      type.createProperty(field.getName(),oType);
      
      System.out.println("test");
    }
  }


}