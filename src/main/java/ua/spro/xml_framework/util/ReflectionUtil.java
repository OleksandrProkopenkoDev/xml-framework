package ua.spro.xml_framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ReflectionUtil {

  private static final Logger log = Logger.getLogger(ReflectionUtil.class.getName());

  /**
   * Get all fields from the given class that are annotated with the given annotation.
   *
   * @param clazz The class to inspect.
   * @param annotationClass The annotation to look for.
   * @return A list of fields that are annotated with the given annotation.
   */
  public static List<Field> getAnnotatedFields(
      Class<?> clazz, Class<? extends Annotation> annotationClass) {
    return Arrays.stream(clazz.getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(annotationClass))
        .toList();
  }

  /**
   * Get the value of the 'name' property of an annotation on a field.
   *
   * @param field The field to inspect.
   * @param annotationClass The annotation class to look for.
   * @return The value of the 'name' property of the annotation, or an empty string if not found.
   */
  public static String getAnnotationValue(
      Field field, Class<? extends Annotation> annotationClass) {
    Annotation annotation = field.getAnnotation(annotationClass);
    if (annotation != null) {
      try {
        return (String) annotationClass.getMethod("name").invoke(annotation);
      } catch (Exception e) {
        log.severe(e.getMessage());
      }
    }
    return "";
  }
}
