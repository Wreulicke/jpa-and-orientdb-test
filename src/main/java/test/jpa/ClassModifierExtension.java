package test.jpa;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

public class ClassModifierExtension implements Extension {

  public void classModify(@Observes AfterBeanDiscovery event, BeanManager manager) {
    Class found = modify(ModifyTarget.class);
    AnnotatedType<ExtendsTarget> type = manager.createAnnotatedType(ExtendsTarget.class);
    InjectionTarget it = manager.createInjectionTarget(type);
    Bean bean = new Bean() {
      @Override
      public Set<Type> getTypes() {
        return new HashSet<>(Arrays.asList(ExtendsTarget.class, found));
      }

      @Override
      public Set<java.lang.annotation.Annotation> getQualifiers() {
        Set<java.lang.annotation.Annotation> qualifiers = new HashSet<>();
        qualifiers.add(new TargetLiteral());
        return qualifiers;
      }

      @Override
      public Class<? extends java.lang.annotation.Annotation> getScope() {
        return ApplicationScoped.class;
      }

      @Override
      public String getName() {
        return "test";
      }

      @Override
      public Set<Class<? extends java.lang.annotation.Annotation>> getStereotypes() {
        return Collections.emptySet();
      }

      @Override
      public boolean isAlternative() {
        return false;
      }

      @Override
      public Class<?> getBeanClass() {
        return found;
      }

      @Override
      public Set<InjectionPoint> getInjectionPoints() {
        return it.getInjectionPoints();
      }

      @Override
      public boolean isNullable() {
        return false;
      }

      @Override
      public Object create(CreationalContext creationalContext) {
        ExtendsTarget target;
        try {
          target = (ExtendsTarget) found.newInstance();
          it.inject(target, creationalContext);
          it.postConstruct(target);
          return target;
        } catch (InstantiationException | IllegalAccessException e) {
          // TODO Auto-generated catch block
          throw new RuntimeException();
        }
      }

      @Override
      public void destroy(Object instance, CreationalContext creationalContext) {
        it.preDestroy(instance);
        it.dispose(instance);
        creationalContext.release();

      }

    };
    event.addBean(bean);
  }
  class TargetLiteral extends AnnotationLiteral<Target> implements Target{
    @Override
    public String name() {
      return "v";
    }
    
  }
  public <T> Class<T> modify(Class<T> clazz) {

    ClassPool pool = ClassPool.getDefault();
    try {
      CtClass modifyTarget = pool.getCtClass(clazz.getName());
      CtClass extendsTarget = pool.getCtClass("test.jpa.ExtendsTarget");

      CtClass resultClass = pool.makeClass(modifyTarget.getName() + "$$");
      resultClass.setSuperclass(extendsTarget);
      CtMethod[] methods = modifyTarget.getDeclaredMethods();
      CtField[] fields = modifyTarget.getDeclaredFields();
      Arrays.stream(methods).forEach(method -> {
        try {
          resultClass.addMethod(CtNewMethod.copy(method, method.getName(), resultClass, null));
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      Arrays.stream(fields).forEach(field -> {
        try {
          resultClass.addField(new CtField(field.getType(), field.getName(), resultClass));
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      ClassFile file = resultClass.getClassFile();
      ConstPool pool2 = file.getConstPool();
      AnnotationsAttribute attribute = new AnnotationsAttribute(pool2, AnnotationsAttribute.visibleTag);
      Annotation annot = new Annotation("javax.enterprise.context.ApplicationScoped", pool2);
      attribute.setAnnotation(annot);
      resultClass.getClassFile().addAttribute(attribute);
      Class<T> result = resultClass.toClass();
      return result;
    } catch (CannotCompileException | RuntimeException | NotFoundException e) {
      throw new InjectionException("TODO");
    }

  }

  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  @interface Target {
    @Nonbinding public String name() default "x";
  }
}
