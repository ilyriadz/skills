/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.telecomdaily;

import ilyriadz.database.TypeProperties;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class TelecomRef {
    public final static DateTimeFormatter DATE_TIME_FORMATER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public final static String ID_REGEX = 
        "([0-9]{1}||[1-9][0-9]||[1-9][0-9][0-9]||[1][0-9][0-9][0-9]){1}";
    public final static String TEL_REGEX =
        "(([1-9][0-9][0-9][0-9][0-9][0-9])||([1-9]([0-9]){3,4}))";
    public final static String NODE_NAME_REF_REGEX = 
        "([a-zA-Z0-9^\\s]{0,32}(\\-[a-zA-Z0-9^\\s]{0,32})?:)";
    public final static String HEAD_NAME_REF_REGEX = 
        "([a-zA-Z1-9]{1}[a-zA-z0-9]{0,32})";
    public final static String GROUP_NAME_REF_REGEX = "[1-8]";
    public final static String PAIR_NAME_REF_REGEX = 
        "(([1-9]||[1][0-9]||[2][0-8])?(\\+[1-9]||\\+[1][0-9]||\\+[2][0-8]){0,3})";
    public final static String REF_REGEX = 
        NODE_NAME_REF_REGEX + "?" +
        HEAD_NAME_REF_REGEX + "\\." +
        GROUP_NAME_REF_REGEX + "\\." +
        PAIR_NAME_REF_REGEX + "(\\p{Space}" +
        HEAD_NAME_REF_REGEX + "\\." +
        GROUP_NAME_REF_REGEX + "\\." +
        PAIR_NAME_REF_REGEX + "){0,3}";
    
    @TypeProperties("(9) not null")
    private int tel;
    
    @TypeProperties("(4) not null")
    private int id;
    
    @TypeProperties("(255) not null")
    private String reference;
    
    @TypeProperties("(255) not null")
    private String dt;

    public TelecomRef() {
    }

    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int tel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public String reference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String dt() {
        return dt;
    }   
    
    private boolean validateId(Object obj)
    {
        var cls = obj.getClass();
        try {
            var method = cls.getMethod("getText");
            String text = (String) method.invoke(obj);
            
            return text.matches(ID_REGEX);
        } catch (NoSuchMethodException | SecurityException | 
            IllegalAccessException | IllegalArgumentException | 
            InvocationTargetException ex)
        {
            Logger.getLogger(TelecomRef.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    private boolean validateTel(Object obj)
    {
        var cls = obj.getClass();
        try {
            var method = cls.getMethod("getText");
            String text = (String) method.invoke(obj);
            "".matches(ID_REGEX);
            return text.matches(TEL_REGEX);
        } catch (NoSuchMethodException | SecurityException | 
            IllegalAccessException | IllegalArgumentException | 
            InvocationTargetException ex)
        {
            Logger.getLogger(TelecomRef.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    private boolean validateRef(Object obj)
    {
        var cls = obj.getClass();
        try {
            var method = cls.getMethod("getText");
            String text = (String) method.invoke(obj);
            
            return text.matches(REF_REGEX);
        } catch (NoSuchMethodException | SecurityException | 
            IllegalAccessException | IllegalArgumentException | 
            InvocationTargetException ex)
        {
            Logger.getLogger(TelecomRef.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }

    @Override
    public String toString() {
        return "TelecomRef(" + "id=" + id + ", tel=" + tel + ", reference=" + reference + ", dt=" + dt + ')';
    }
    
    public static void main(String[] args) {
        System.out.println(REF_REGEX);
        
        System.out.println("1+".matches(PAIR_NAME_REF_REGEX));
    }
}
