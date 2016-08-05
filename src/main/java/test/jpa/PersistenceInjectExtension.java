package test.jpa;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

public class PersistenceInjectExtension implements Extension {
  Map<Class<?>, List<Field>> pool=new HashMap<>();
  
  public <Type> void test(@Observes ProcessInjectionTarget<Type> a) {
    AnnotatedType<Type> type = a.getAnnotatedType();
    Class<?> clazz=type.getJavaClass();
    if(pool.containsKey(clazz))return;
    List<Field> fields=type.getFields().stream().filter(field -> field.isAnnotationPresent(PersistenceContext.class)).map(AnnotatedField::getJavaMember).collect(Collectors.toList());
    if(fields.isEmpty())return;
    pool.put(clazz,fields);
    final InjectionTarget<Type> injectionTarget = a.getInjectionTarget();
    a.setInjectionTarget(new TrasferedInjector<>(a.getInjectionTarget(),(instance,ctx) ->{
      List<Field> list=pool.get(clazz);
      for (Field field : list) {
        field.setAccessible(true);
        try {
          field.set(instance,Persistence.createEntityManagerFactory("test").createEntityManager());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }));
  }
  
  
  @FunctionalInterface
  interface Injector<T>{
    public void inject(T instance, CreationalContext<T> ctx);
  }
  
  static class TrasferedInjector<T> implements InjectionTarget<T>{
    InjectionTarget<T> it;
    Injector<T> injector;
    public TrasferedInjector(InjectionTarget<T> transferInjectionTarget, Injector<T> injector) {
      it=transferInjectionTarget;
      this.injector=injector;
    }
    @Override
    public T produce(CreationalContext<T> ctx) {
      return it.produce(ctx);
    }

    @Override
    public void dispose(T instance) {
      it.dispose(instance);
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
      return it.getInjectionPoints();
    }

    @Override
    public void inject(T instance, CreationalContext<T> ctx) {
      it.inject(instance, ctx);
      injector.inject(instance, ctx);
    }

    @Override
    public void postConstruct(T instance) {
      it.postConstruct(instance);
    }

    @Override
    public void preDestroy(T instance) {
      it.preDestroy(instance);
    }
    
  }
}
