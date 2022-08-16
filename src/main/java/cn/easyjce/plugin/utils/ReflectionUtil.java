package cn.easyjce.plugin.utils;

import org.apache.commons.lang3.StringUtils;
import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

/**
 * @Class: ReflectionUtil
 * @Date: 2022/8/16 11:12
 * @author: cuijiufeng
 */
public class ReflectionUtil {
    private static final ReflectionFactory REFLECTION_FACTORY = ReflectionFactory.getReflectionFactory();

    public static <T extends Enum<?>> void addEnum(Class<T> clazz, String name, Object ... params)
            throws ReflectiveOperationException {
        Objects.requireNonNull(clazz, "enum class can't be null");
        if (StringUtils.isBlank(name)) {
            throw new NullPointerException("enum name can't be blank");
        }
        // 获取枚举合成方法和枚举属性类型
        Field[] fields = clazz.getDeclaredFields();
        if (Arrays.stream(fields).anyMatch(f -> f.isEnumConstant() && f.getName().equals(name))) {
            throw new IllegalArgumentException("duplicate enum name");
        }
        //获取枚举属性类型
        Class<?>[] filedTypes = Arrays.stream(fields)
                .filter(f -> !f.isEnumConstant() && !f.isSynthetic())
                .map(Field::getType)
                .toArray(Class[]::new);

        //设置枚举类的$VALUES属性，这个属性是一个枚举默认属性，是枚举对象的数组集合
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        Field valuesField = Arrays.stream(fields).filter(Field::isSynthetic).findFirst().get();
        // 设置属性访问权限
        valuesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        T[] previousValues = (T[]) valuesField.get(clazz);
        // 创建一个新的枚举
        T enumObj = makeEnum(clazz, name, previousValues.length, filedTypes, params);

        //添加一个新的枚举
        T[] enumValues = Arrays.copyOf(previousValues, previousValues.length + 1);
        enumValues[previousValues.length] = enumObj;

        // 设置新$VALUES字段
        setEnumValues(valuesField, null, enumValues);

        // 清理枚举缓存
        // Sun (Oracle?!?) JDK 1.5/6
        blankField(clazz, "enumConstantDirectory");
        // IBM JDK
        blankField(clazz, "enumConstants");
    }

    private static <T extends Enum<?>> T makeEnum(Class<T> clazz, String name, int ordinal, Class<?>[] paramTypes, Object[] paramValues)
            throws ReflectiveOperationException {
        if (paramTypes.length != paramValues.length) {
            throw new IllegalArgumentException("illegal number of parameters");
        }
        Class<?>[] realTypes = new Class[paramTypes.length + 2];
        realTypes[0] = String.class;
        realTypes[1] = int.class;
        System.arraycopy(paramTypes, 0, realTypes, 2, paramTypes.length);
        Object[] realValues = new Object[paramValues.length + 2];
        realValues[0] = name;
        realValues[1] = ordinal;
        System.arraycopy(paramValues, 0, realValues, 2, paramValues.length);
        ConstructorAccessor constructorAccessor = REFLECTION_FACTORY.newConstructorAccessor(clazz.getDeclaredConstructor(realTypes));
        return clazz.cast(constructorAccessor.newInstance(realValues));
    }

    private static <T extends Enum<?>> void setEnumValues(Field valuesField, Object target, T[] enumValues) throws ReflectiveOperationException {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        int modifiers = modifiersField.getInt(valuesField);
        // 清空修饰符 int 中的最后一位
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(valuesField, modifiers);
        FieldAccessor fa = REFLECTION_FACTORY.newFieldAccessor(valuesField, false);
        fa.set(target, enumValues);
    }

    private static void blankField(Class<?> enumClass, String fieldName) throws ReflectiveOperationException {
        for (Field field : Class.class.getDeclaredFields()) {
            if (field.getName().contains(fieldName)) {
                AccessibleObject.setAccessible(new Field[]{field}, true);
                setEnumValues(field, enumClass, null);
                break;
            }
        }
    }
}
