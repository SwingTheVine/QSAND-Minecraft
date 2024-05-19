package com.SwingTheVine.QSAND.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class FieldsHandler {
	
	// Constructor
	private FieldsHandler() {
    }
    
    public static Object findFieldAndGet(final Class<?> target, final Class<?> fieldType, final Object targetObject, int index) {
        for (final Field field : target.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(fieldType)) {
                if (index == 0) {
                    try {
                        return field.get(targetObject);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                --index;
            }
        }
        return null;
    }
    
    public static Object[] findFieldsAndGet(final Class<?> target, final Class<?> fieldType, final Object targetObject) {
        final List<Object> list = new ArrayList<Object>();
        for (final Field field : target.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(fieldType)) {
                try {
                    list.add(field.get(targetObject));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list.toArray(new Object[0]);
    }
    
    public static Field findField(final Class<?> target, final Class<?> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().isAssignableFrom(fieldType)) {
                if (index == 0) {
                    return field;
                }
                --index;
            }
        }
        return null;
    }
    
    public static Field[] findFields(final Class<?> target, final Class<?> fieldType) {
        return findFields(target, fieldType, 0);
    }
    
    public static Field[] findFields(Class<?> target, final Class<?> fieldType, int depth) {
        final List<Field> list = new ArrayList<Field>();
        while (target != null && target != Object.class) {
            for (final Field field : target.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(fieldType)) {
                    list.add(field);
                }
            }
            target = target.getSuperclass();
            if (depth != -1 && depth-- == 0) {
                break;
            }
        }
        return list.toArray(new Field[0]);
    }
    
    public static <T> Field removeFinal(final Class<? super T> classToAccess, final T instance, final String... fieldNames) {
        final Field field = ReflectionHelper.findField((Class)classToAccess, ObfuscationReflectionHelper.remapFieldNames(classToAccess.getName(), fieldNames));
        try {
            final Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return field;
    }
}
