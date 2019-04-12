package org.jumpmind.pos.util.model;

import java.io.ObjectStreamException;
import java.util.Set;

/**
 * Convenience abstract implementation for the ITypeCode interface.  Makes it
 * possible to implement a new type code with just a couple of methods and 
 * the static type code values.  E.g., 
 * 
 * <pre>
 *  public static final class MyTypeCode extends AbstractTypeCode {
 *      private static final long serialVersionUID = 1L;
 *         
 *      public static final MyTypeCode CODE1 = new MyTypeCode("CODE1");
 *      public static final MyTypeCode CODE2 = new MyTypeCode("CODE2");
 *      public static final MyTypeCode CODE3 = new MyTypeCode("CODE3");
 *         
 *      public static MyTypeCode of(String value) {
 *          return ITypeCode.make(MyTypeCode.class, value);
 *      }
 *  
 *      private MyTypeCode(String value) {
 *          super(value);
 *      }
 *          
 *  }
 * </pre>  
 *
 */
public abstract class AbstractTypeCode implements ITypeCode {

    private static final long serialVersionUID = 1L;
    
    private String value;
    protected AbstractTypeCode(String value) {
        if (value == null) {
            throw new NullPointerException(String.format("Cannot have null value for instance of %s", this.getClass().getName()));
        }
        
        this.value = value;
        ITypeCodeRegistry.register(this);
    }

    /**
     * This should only be used by subclasses for creating type code instances
     * at runtime whose values should already be expected to exist through
     * a static declaration of the type code value. A type code value will
     * only be returned if it can be found to already exist in the ITypeCodeRegistry.
     * If you need to ensure the type code is created whether or not it already
     * exists in the registry, use the ITypeCode.make() method instead.
     * @param clazz
     * @param value
     * @return
     */
    protected static <T extends ITypeCode> T of(Class<T> clazz, String value) {

        T returnCode = null;
        if (ITypeCodeRegistry.exists(clazz, value)) {
            returnCode = ITypeCode.make(clazz, value);
        }
        return returnCode;
    }
    
    /**
     * Retrieves the set of values for the given ITypeCode subclass
     * @param <T> An ITypeCode subclass
     * @param clazz The subclass
     * @return The set of ITypeCode subclassed values
     */
    protected static <T extends ITypeCode> Set<T> values(Class<T> clazz) {
        return (Set<T>) ITypeCodeRegistry.values(clazz);
    }
    
    @Override
    public String value() {
        return this.toString();
    }

    @Override
    public String toString() {
        return this.value;
    }
    
    @Override
    public boolean equals(Object other) {
        return this.isEqual(other);
    }

    @Override
    public int hashCode() {
        return this.hash();
    }

    /**
     * Hooks into Java deserialization so that we can return the static
     * instance of the TypeCode whenever possible.
     * @return The static TypeCode when possible
     * @throws ObjectStreamException
     */
    protected Object readResolve() throws ObjectStreamException {
        //return deserializeStaticInstance(this.value);
        return of(this.getClass(), this.value);
    }
    
    
}
