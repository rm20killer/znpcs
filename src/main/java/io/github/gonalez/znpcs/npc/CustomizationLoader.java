package io.github.gonalez.znpcs.npc;

import com.google.common.collect.Iterables;
import io.github.gonalez.znpcs.cache.TypeCache;
import io.github.gonalez.znpcs.cache.CachePackage;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class CustomizationLoader {
  private final Class<? extends Entity> entityClass;
  
  private final Map<String, Method> methods;
  
  public CustomizationLoader(EntityType entityType, Iterable<String> methodsName) {
    this(entityType.getEntityClass(), methodsName);
  }
  
  protected CustomizationLoader(Class<? extends Entity> entityClass, Iterable<String> methodsName) {
    this.entityClass = entityClass;
    this.methods = loadMethods(methodsName);
  }
  
  protected Map<String, Method> loadMethods(Iterable<String> iterable) {
    Map<String, Method> builder = new HashMap<>();
    for (Method method : this.entityClass.getMethods()) {
      if (!builder.containsKey(method.getName()) && 
        Iterables.contains(iterable, method.getName())) {
        for (Class<?> parameter : method.getParameterTypes()) {
          TypeProperty typeProperty = TypeProperty.forType(parameter);
          if (typeProperty == null && parameter.isEnum())
            (new TypeCache.BaseCache.EnumLoader((new TypeCache.CacheBuilder(CachePackage.DEFAULT))
                .withClassName(parameter.getTypeName()))).load(); 
        } 
        builder.put(method.getName(), method);
      } 
    } 
    return builder;
  }
  
  public boolean contains(String name) {
    return this.methods.containsKey(name);
  }
  
  public Map<String, Method> getMethods() {
    return this.methods;
  }
}
