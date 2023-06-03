/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.database.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author user
 */
public class PanelData
{
    private final Map<String, JTextField> fields = new HashMap<>();
    private final Map<String, JLabel> labels = new HashMap<>();
    private final Map<String, JPanel> panels = new HashMap<>();
    private final Map<String, FieldValidator> validations = new HashMap<>();
    
    private final List<String> keys = new ArrayList<>();
    private final Class<?> cls;
    
    private final JPanel panel = new JPanel();

    public PanelData(Class<?> cls) 
    {
        this.cls = cls;
        BoxLayout bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(bl);
    }
    
    public void put(String key, JLabel label, FieldValidator validation)
    {
        Objects.nonNull(key);
        
        labels.put(key, label);
        
        JTextField field = new JTextField(32);
        fields.put(key, field);
        
        keys.add(key);
        
        JPanel fieldPanel = new JPanel();
        fieldPanel.add(label);
        fieldPanel.add(field);
        
        panels.put(key, fieldPanel);
        
        panel.add(fieldPanel);
        
        validations.put(key, validation);
    }
    
    public JLabel getLabel(String key)
    {
        return labels.get(key);
    }
    
    public JTextField getField(String key)
    {
        return fields.get(key);
    }
    
    public JPanel getPanel()
    {
        return panel;
    }
    
    public JPanel getFieldPanel(String key)
    {
        return panels.get(key);
    }
    
    public List<String> keys()
    {
        return Collections.unmodifiableList(keys);
    }
    
    public FieldValidator getValidator(String key)
    {
        System.out.println(validations);
        return validations.get(key);
    }
}
