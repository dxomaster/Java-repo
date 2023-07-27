package reflection.api;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public class Reflector implements Investigator {
    private Object m_suspect;

    @Override
    public void load(Object anInstanceOfSomething) {
        this.m_suspect = anInstanceOfSomething;
    }

    @Override
    public int getTotalNumberOfMethods() {
        Method[] allMethods = m_suspect.getClass().getDeclaredMethods();
        return allMethods.length;
    }

    @Override
    public int getTotalNumberOfConstructors() {
        Constructor<?>[] constructors = m_suspect.getClass().getDeclaredConstructors();
        return constructors.length;
    }

    @Override
    public int getTotalNumberOfFields() {
        Field[] fields = m_suspect.getClass().getDeclaredFields();
        return fields.length;
    }

    @Override
    public Set<String> getAllImplementedInterfaces() {
        Set<String> interfaceSimpleNames = new HashSet<>();
        Class<?>[] interfaces = m_suspect.getClass().getInterfaces();
        for (Class<?> currentInterface : interfaces) {
            interfaceSimpleNames.add(currentInterface.getSimpleName());
        }
        return interfaceSimpleNames;
    }

    @Override
    public int getCountOfConstantFields() {
        int counter = 0;
        Field[] fields = m_suspect.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers()))
                counter++;

        }
        return counter;
    }

    @Override
    public int getCountOfStaticMethods() {
        int counter = 0;
        Method[] methods = m_suspect.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers()))
                counter++;
        }
        return counter;
    }

    @Override
    public boolean isExtending() {
        return m_suspect.getClass().getSuperclass() != Object.class;
    }

    @Override
    public String getParentClassSimpleName() {
        Class<?> superclass = m_suspect.getClass().getSuperclass();
        return superclass == null ? null : superclass.getSimpleName();
    }

    @Override
    public boolean isParentClassAbstract() {
        Class<?> superclass = m_suspect.getClass().getSuperclass();
        return superclass != null && Modifier.isAbstract(superclass.getModifiers());
    }

    @Override
    public Set<String> getNamesOfAllFieldsIncludingInheritanceChain() {
        Set<String> allFields = new HashSet<>();
        Class<?> currentClass = m_suspect.getClass();

        while (currentClass != null) {
            Field[] fields = currentClass.getDeclaredFields();
            for (Field field : fields) {
                allFields.add(field.getName());
            }
            currentClass = currentClass.getSuperclass();
        }

        return allFields;
    }

    @Override
    public int invokeMethodThatReturnsInt(String methodName, Object... args) {
        Method[] methods = m_suspect.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                try {
                    method.setAccessible(true);
                    return (int) method.invoke(m_suspect, args);
                } catch (IllegalAccessException | InvocationTargetException ignored) {

                }
            }
        }
        return 0; //shouldn't happen since we are gurnteed the method exists

    }

    @Override
    public Object createInstance(int numberOfArgs, Object... args) {

        Constructor<?>[] constructors = m_suspect.getClass().getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == numberOfArgs) {
                constructor.setAccessible(true);
                try {
                    return constructor.newInstance(args);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args) {
        try {
            Method method = m_suspect.getClass().getDeclaredMethod(name, parametersTypes);
            method.setAccessible(true);
            return method.invoke(m_suspect, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    @Override
    public String getInheritanceChain(String delimiter) {
        StringBuilder sb = new StringBuilder();
        Class<?> currentClass = m_suspect.getClass();

        while (currentClass != null) {
            if (currentClass.getSimpleName().equals("Object")) {
                sb.insert(0, currentClass.getSimpleName());
            } else
                sb.insert(0, delimiter + currentClass.getSimpleName());
            currentClass = currentClass.getSuperclass();
        }
        return sb.toString();
    }
}
