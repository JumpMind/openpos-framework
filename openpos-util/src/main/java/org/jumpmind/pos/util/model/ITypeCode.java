package org.jumpmind.pos.util.model;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.jumpmind.pos.util.ITypeCodeDeserializer;
import org.jumpmind.pos.util.ITypeCodeSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * Provides a base for implementing "TypeCodes" which are like extensible enums.
 * 
 * Implementors of this interface will define a finite set of values for the
 * type and implement the <code>value()</code>, <code>toString()</code>, 
 * <code>equals()</code>, and <code>hashCode()</code> methods in the class like so:
 * 
 * <pre>
 * public final class MyTypeCode implements ITypeCode {
 *
 *   public static final MyTypeCode CODE1 = new MyTypeCode("CODE1");
 *   public static final MyTypeCode CODE2 = new MyTypeCode("CODE2");
 *   public static final MyTypeCode CODE3 = new MyTypeCode("CODE3");
 *   // Define more codes here
 *      
 *   private String value;
 *      
 *   public static MyTypeCode of(String value) {
 *       return ITypeCode.make(MyTypeCode.class, value);
 *   }
 *  
 *   private MyTypeCode(String value) {
 *       this.value = value;
 *   }
 *      
 *   {@literal @}Override
 *   public String value() {
 *       return this.toString();
 *   }
 *  
 *   {@literal @}Override
 *   public String toString() {
 *       return this.value;
 *   }
 *  
 *   {@literal @}Override
 *   public boolean equals(Object other) {
 *       return this.isEqual(other);
 *   }
 *  
 *   {@literal @}Override
 *   public int hashCode() {
 *       return this.hash();
 *   }
 *      
 * }
 * </pre>
 * 
 * This will make the following possible:
 * 
 * <pre>
 *     MyTypeCode codeConst = MyTypeCode.CODE1;
 *     MyTypeCode codeAtRuntime = MyTypeCode.of("CODE1");
 *     MyTypeCode newTypeCode = MyTypeCode.of("NEW_CODE");
 *       
 *     // Objects are equal
 *     assertEquals(codeConst, codeAtRuntime);
 *     
 *     // Objects are the same
 *     assertSame(codeConst, codeAtRuntime);
 *
 *     // Supports adding new values
 *     assertEquals("NEW_CODE", newTypeCode.value());
 *     assertEquals("NEW_CODE", newTypeCode.toString());
 *     
 * </pre>
 * 
 * Implementors must provide a constructor which accepts a String argument, which
 * is the TypeCode value.
 * 
 * Implementors should also supply the following method:
 * 
 * <pre>
 *   public static MyTypeCode of(String value) {
 *       return ITypeCode.make(MyTypeCode.class, value);
 *   }
 * </pre>
 * 
 * Where MyTypeCode is the type of your ITypeCode class.  The advantage to using 
 * the 'of' method to users of the class is that it provides a shorthand way to
 * create an instance of the TypeCode without having to do an explicit 'new' operation.
 * Also, if you use the ITypeCode.make() method, static instances of your TypeCode
 * will be returned if they exist.
 * 
 */
@JsonDeserialize(using = ITypeCodeDeserializer.class)
@JsonSerialize(using = ITypeCodeSerializer.class)
public interface ITypeCode extends Serializable, Comparable<ITypeCode>  {
    
    public String value();
    
    /**
     * Returns the same result as value().  This is a convenience method
     * to replicate similar results that you would get with the {@link java.lang.Enum#name()} method.
     * @return The value of the ITypeCode instance.
     */
    default public String name() {
        return value();
    }

    /**
     * Override this method for the case when an ITypeCode implementation class
     * has moved from one package to another between releases of code or if for some reason a different class should
     * be used to deserialize the ITypeCode value into.  The returned list should contain a list
     * of fully qualified class names that the {@link ITypeCodeDeserializer} will use when attempting to locate the correct
     * ITypeCode implementation class.  The first element in the returned array will also be used by the
     * {@link ITypeCodeSerializer} as the default class to be used for deserialization on the receiving side.  I
     * @return The list of fully qualified classes to attempt to deserialize the ITypeCode value into.  If {@code null},
     * or empty, the ITypeCode implementation class itself will be assumed to be the class to be used for deserialization.
     */
    default public String[] getDeserializationSearchClasses() { return null; }

    default public boolean isDeserializableFrom(String clazz) {
        if (this.getClass().getName().equals(clazz)) {
            return true;
        }

        String[] deserializationSearchClasses = this.getDeserializationSearchClasses();
        if (deserializationSearchClasses != null) {
            return Arrays.asList(deserializationSearchClasses).stream().anyMatch(c -> c.equals(clazz));
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ITypeCode> T make(Class<T> typeCodeClass, String value) {
        if (value == null) {
            return null;
        }
        
        if (typeCodeClass == null) {
            throw new NullPointerException("Cannot make ITypeCode instance with null typeCodeClass");
        }
        
        Field[] declaredFields = typeCodeClass.getDeclaredFields();
        List<Field> staticFields = new ArrayList<Field>();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                staticFields.add(field);
            }
        }
        T madeTypeCode = null;
        try {
            Constructor<T> c = typeCodeClass.getDeclaredConstructor((String.class));
            c.setAccessible(true);
            madeTypeCode = c.newInstance(value);
            if (ITypeCodeRegistry.isNotRegistered(madeTypeCode)) {
                ITypeCodeRegistry.register(madeTypeCode);
            }
            List<ITypeCode> matchingStaticTypeCodes = new ArrayList<>();
            List<Field> matchingStaticTypeCodesFields = new ArrayList<>();
            
            for (Field staticField : staticFields) {
                if (staticField.getType() == typeCodeClass) {
                    ITypeCode staticTypeCode = (ITypeCode) staticField.get(null);
                    // Find all static type codes with the same value.  Hopefully
                    // there's not more than 1.
                    if (madeTypeCode.equals(staticTypeCode)) {
                        matchingStaticTypeCodes.add(staticTypeCode);
                        matchingStaticTypeCodesFields.add(staticField);
                    }
                }
            }
            if (matchingStaticTypeCodes.size() == 1) {
                return (T) matchingStaticTypeCodes.get(0);
            } else if (matchingStaticTypeCodes.size() > 1) {
                matchingStaticTypeCodesFields.stream().forEach(f -> {
                    LogFactory.getLog(typeCodeClass).warn(
                            String.format(typeCodeClass.toString() + 
                            " instance with value '" + value + "' and created " +
                            "with make() has the same value as a " + 
                            "static field with name '" + f.getName() +
                            "' in that same class. Consider reviewing the class " +
                            "and eliminating duplicates."));
                });
                
                // Then just proceed and return the new made instance, since
            }
            
        } catch (Exception ex) {
            LogFactory.getLog(typeCodeClass).error(
                String.format("Failed to make " + 
                typeCodeClass.getClass().getSimpleName() + 
                " instance with value '" + value + "'"), ex);
        }

        return madeTypeCode;
    }

    default public boolean isEqual(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (getClass() != other.getClass()) {
            return false;
        }

        ITypeCode otherTypeCode = (ITypeCode) other;
        return this.value().equals(otherTypeCode.value());
    }

    default public int hash() {
        return this.value().hashCode();
    }
    
    default int compareTo(ITypeCode o) {
        return this.value().compareTo(o.value());
    }
}
