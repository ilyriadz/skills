/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ilyriadz.database.util;

import ilyriadz.database.DatabaseManager;
import ilyriadz.database.H2Database;
import ilyriadz.database.TypeProperties;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author sadaoui ilyes
 */
public class DatabaseGutil {
    
    public static <T> JTable generateTable(DatabaseManager dbm, 
        String tableName, String others, Class<T> cls) throws SQLException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        var fields = Arrays.asList(cls.getDeclaredFields())
            .stream()
            .filter(e -> e.getAnnotation(TypeProperties.class) != null)
            .map(e -> e.getName())
            .toList();
        
        var columnNames = new String[fields.size()];
        fields.toArray(columnNames);
        
        var select = dbm.select(tableName, others, cls);
        
        Object[][] data = null;
        
        if (!select.isEmpty())
        {
            data = new Object[select.size()][];
            
            for (int i = 0; i < select.size(); i++)
            {
                Object[] record = new Object[columnNames.length];
                for (int j = 0; j < columnNames.length; j++) 
                {
                    Field f = cls.getDeclaredField(columnNames[j]);
                    f.setAccessible(true);
                    record[j] = f.get(select.get(i));
                }
                
                data[i] = record;
            }
        } // end if
            
        System.out.println(Arrays.toString(columnNames));
        
        JTable table = new JTable(data, columnNames);
        table.setShowGrid(true);
        
        return table;
    }
    
    public static <T> TableModel generateTableModel(DatabaseManager dbm, 
        String tableName, String others, Class<T> cls) throws SQLException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        var fields = Arrays.asList(cls.getDeclaredFields())
            .stream()
            .filter(e -> e.getAnnotation(TypeProperties.class) != null)
            .map(e -> e.getName())
            .toList();
        
        var columnNames = new String[fields.size()];
        fields.toArray(columnNames);
        
        var select = dbm.select(tableName, others, cls);
        
        Object[][] data = null;
        
        if (!select.isEmpty())
        {
            data = new Object[select.size()][];
            
            for (int i = 0; i < select.size(); i++)
            {
                Object[] record = new Object[columnNames.length];
                for (int j = 0; j < columnNames.length; j++) 
                {
                    Field f = cls.getDeclaredField(columnNames[j]);
                    f.setAccessible(true);
                    record[j] = f.get(select.get(i));
                }
                
                data[i] = record;
            }
        } // end if
        
        return new DefaultTableModel(data, columnNames);
    }
    
    public static <T> PanelData generatePanelData(Class<T> cls)
    {
        var labels = Arrays.asList(cls.getDeclaredFields()).stream()
                .filter(e -> e.getAnnotation(TypeProperties.class) != null)
                .map(e -> new JLabel(e.getName()))
                .toList();
        
        final PanelData panelData = new PanelData(cls);

        labels.forEach(label -> {
            FieldValidator validator = () -> {
                return true;
            };
            
            try {
                Field fld = cls.getDeclaredField(label.getText());
                var validation = fld.getDeclaredAnnotation(Validation.class);
                if (validation != null)
                {
                    var method = cls.getDeclaredMethod(validation.value(), Object.class);
                    method.setAccessible(true);
                    validator = () -> {
                        try {
                            boolean b = (boolean) 
                                method.invoke(null, panelData.getField(fld.getName().toLowerCase()));
                            return b;
                        } catch (IllegalAccessException | 
                                IllegalArgumentException | 
                                InvocationTargetException ex) {
                            Logger.getLogger(DatabaseGutil.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        return false;
                    };
                }
            } catch (NoSuchFieldException | SecurityException | 
                    NoSuchMethodException ex) {
                Logger.getLogger(DatabaseGutil.class.getName()).log(Level.SEVERE, null, ex);
                
            }
            
            panelData.put(label.getText(), label, validator);
        });
        
        return panelData;
    }
    
    public static <T> List<Field> fields(Class<T> cls)
    {
        return Arrays.asList(cls.getDeclaredFields()).stream()
                .filter(e -> e.getAnnotation(TypeProperties.class) != null)
                .toList();
    }
    
    public static <T> List<String> columns(Class<T> cls)
    {
        return Arrays.asList(cls.getDeclaredFields()).stream()
                .filter(e -> e.getAnnotation(TypeProperties.class) != null)
                .map(Field::getName)
                .toList();
    }
    
    public static <T> List<String> values(Class<T> cls, 
        PanelData panelData)
    {
        var keys = fields(cls);           
        var columns = columns(cls);
        
        List<String> values = new ArrayList<>();
            
            for (int i = 0; i < keys.size(); i++) 
            {
                var key = columns.get(i);
                
                Objects.nonNull(key);
                
                var validated = panelData.getValidator(key);
                if (validated != null && !validated.validate())
                    throw new ValidatorException("");
                
                switch (keys.get(i).getName().toLowerCase())
                {
                    case "int", "double", "float", "long", "boolean" ->
                    {
                        values.add(panelData.getField(key).getText());
                    } // end case
                    default -> values.add("'" + panelData.getField(key).getText() + "'");
                }
            } // end for
            
            return values;
    }
    
    public static <T> ActionListener generateInsertAction(
        DatabaseManager dbm, String tableName, PanelData panelData, Class<T> cls,
        Runnable afterInsertionAction)
    {
        return (evt) -> 
        {
            var columns = columns(cls);
            
            List<String> values = values(cls, panelData);
            
            String[] array = new String[values.size()];
            values.toArray(array);
            
            try {
                dbm.insert(tableName, columns, array);
                
                if (afterInsertionAction != null)
                    afterInsertionAction.run();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseGutil.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
    }
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        
        DatabaseManager dbm = new H2Database();
        dbm.connect("./books");
        dbm.createNotExistTable("books", Book.class);
        
        
        //dbm.update("books", List.of("name"), List.of("'android for programmers'"), "");
        dbm.showTable("books");
        
        var list = dbm.select("books", "", Book.class);
        System.out.println(list);
        
        try {
            JScrollPane pane = new JScrollPane();
            var table = generateTable(dbm, "books", "", Book.class);
            table.setFont(new Font("monospace", Font.BOLD, 26));
            table.setRowHeight(table.getRowHeight() + 15);
            
            pane.setViewportView(table);
            JFrame frame = new JFrame("Books");
            frame.setSize(800, 600);
            frame.getContentPane().add(pane);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        } catch (NoSuchFieldException | IllegalArgumentException | 
                IllegalAccessException ex) {
            Logger.getLogger(DatabaseGutil.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // end method
    
    
    
}
