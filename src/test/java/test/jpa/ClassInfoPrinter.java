package test.jpa;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ClassInfoPrinter {
  public <T> void print(T instance){
    print(instance.getClass());
  }
  public <T> void print(Class<T> clazz){
    System.out.println("====================information====================");
    System.out.println("class : "+clazz.getName());
    System.out.println("super : "+clazz.getSuperclass().getName());
    System.out.println("annotations:");
    Arrays.stream(clazz.getAnnotations())
    .forEach(prefix("  "));
    System.out.println("member : ");
    Stream.concat(
        Stream.of(clazz.getDeclaredMethods()),
        Stream.of(clazz.getDeclaredFields())
    ).forEach(prefix("  "));
    System.out.println("====================information====================");
    if(!clazz.getSuperclass().equals(Object.class)){
      print(clazz.getSuperclass());
    }
  }
  public Consumer<Object> prefix(String prefix){
    return (instance) -> System.out.println(prefix+instance);
  }
  public Consumer<Object> suffix(String suffix){
    return (instance) -> System.out.println(instance+suffix);
  }
}
