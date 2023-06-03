/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package ilyriadz.telecomdaily;

import ilyriadz.database.DatabaseManager;
import ilyriadz.database.H2Database;
import ilyriadz.database.util.DatabaseGutil;
import ilyriadz.database.util.PanelData;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author user
 */
public class TelecomDailyApp extends javax.swing.JFrame {
    
    private static DatabaseManager dbm;
    private JTable table = new JTable();
    private PanelData panelData;
    private JPanel buttonPanel;
    private JButton insert, update, delete;
    private JTextField idFld, telFld, referenceFld;
    
    private boolean idValidated, telValidated, refValidated;
    
    static
    {
        try {
            dbm = new H2Database();
            dbm.connect("./telecomdaily", "ilyes", "sadaoui");
            dbm.createNotExistTable("references", TelecomRef.class);
        } catch (SQLException ex) {
            Logger.getLogger(TelecomDailyApp.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TelecomDailyApp.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    /** Creates new form TelecomDailyApp */
    public TelecomDailyApp() {
        initComponents();
        
        setSize(800, 600);
        
        panelData = DatabaseGutil.generatePanelData(TelecomRef.class);
        telFld = panelData.getField("tel");
        idFld = panelData.getField("id");
        referenceFld = panelData.getField("reference");
        
        getContentPane().add(panelData.getPanel(), BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane();
        sp.setViewportView(table);
        getContentPane().add(sp, BorderLayout.CENTER);
        
        init();
        initBottonsPanel();
        initButtonsEvents();
        initFieldsEvents();
        
        if (panelData.getPanel().getLayout() instanceof FlowLayout fl)
        {
            fl.setAlignment(FlowLayout.LEFT);
            panelData.getPanel().doLayout();
        }
        
        panelData.getField("dt").setVisible(false);
        panelData.getLabel("dt").setVisible(false);
        idFld.setEnabled(false);
        panelData.getField("reference").setEnabled(false);
        
        panelData.getPanel().add(buttonPanel);
    }
    
    private void init()
    {   
        final Font font = new Font("", Font.BOLD, 18);
        
        panelData.keys().forEach(key ->
        {
            var fld = panelData.getField(key);
            var lbl = panelData.getLabel(key);
            var panel = panelData.getFieldPanel(key);
            
            if (panel.getLayout() instanceof FlowLayout fl)
            {
                fl.setAlignment(FlowLayout.LEFT);
                panel.doLayout();
            }
            
            fld.setFont(font);
            lbl.setFont(font);
            
            if (key.equals("id"))
                fld.setColumns(12);
        });
    }
    
    private void initBottonsPanel()
    {
        buttonPanel = new JPanel();
        if (buttonPanel.getLayout() instanceof FlowLayout fl)
        {
            fl.setHgap(20);
            fl.setVgap(20);
            fl.setAlignment(FlowLayout.CENTER);
        }
        
        Font font = new Font("", Font.BOLD, 18);

        insert = new JButton("Add");
        insert.setFont(font);
        update = new JButton("Update");
        delete = new JButton("Delete");
        
        JButton[] buttons = {insert, update, delete};
        Color colors[] = {Color.BLUE, Color.ORANGE, Color.RED};
        
        for (int i = 0; i < buttons.length; i++)
        {
            buttons[i].setFont(font);
            buttons[i].setForeground(colors[i]);
            buttons[i].setEnabled(false);
        } // end for
        
        buttonPanel.add(insert);
        buttonPanel.add(update);
        buttonPanel.add(delete);
    }
    
    private void initButtonsEvents()
    {
        insert.addActionListener((evt) ->
        {
            try {
                var list = dbm.select("references", 
                    "where tel=" + panelData.getField("tel").getText() +
                    " and id=" + idFld.getText() +
                    " and reference like '" + panelData.getField("reference").getText()
                 + "' limit 1", TelecomRef.class);
                if (!list.isEmpty())
                {
                    insert.setEnabled(false);
                    return;
                }
                
                dbm.insert("references", List.of("id", "tel", "reference", "dt"),
                        idFld.getText(), 
                        panelData.getField("tel").getText(),
                        "'" + panelData.getField("reference").getText() + "'",
                        "'" + LocalDateTime.now().format(TelecomRef.DATE_TIME_FORMATER) + "'");
                table.setModel(DatabaseGutil.generateTableModel(dbm, "references", 
                    " where tel=" + Integer.parseInt(panelData.getField("tel").getText()),
                    TelecomRef.class));
            } catch (SQLException | NoSuchFieldException | IllegalArgumentException |
                    IllegalAccessException ex) {
                Logger.getLogger(TelecomDailyApp.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex.getMessage(), 
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }});
    }
    
    private void initFieldsEvents()
    {
        final var id = idFld;
        final var tel = telFld;
        final var reference = panelData.getField("reference");
        
        final JTextField[] fields = {id, tel, reference};
        
        final String[] regexes = {
            TelecomRef.ID_REGEX, 
            TelecomRef.TEL_REGEX,
            TelecomRef.REF_REGEX};
        
        Consumer<Boolean> idValidate = (b) -> {
            idValidated = b;
        };
        
        Consumer<Boolean> telValidate = (b) -> {
            telValidated = b;
            idFld.setEnabled(b);
            panelData.getField("reference").setEnabled(b);
            if (b)
            {
                try {
                    var list = dbm.select("references", "where tel=" +
                            telFld.getText() + 
                            " order by dt desc limit 1 ", 
                            TelecomRef.class);
                    if (!list.isEmpty())
                    {
                        var telref = list.get(0);
                        
                        table.setModel(DatabaseGutil.generateTableModel(dbm, "references", 
                            "where tel=" + telref.tel(), TelecomRef.class));
                        idFld.setText(String.valueOf(telref.id()));
                        panelData.getField("reference").setText(String.valueOf(telref.reference()));
                    }
                } catch (SQLException | NoSuchFieldException |
                        IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(TelecomDailyApp.class.getName()).log(Level.SEVERE, null, ex);
                } 
                    
            } // end if
        };
        
        Consumer<Boolean> refValidate = (b) -> {
            refValidated = b;
        };
        
        var validators = List.of(idValidate, telValidate, refValidate);
        
        boolean[] validates = {idValidated, telValidated, refValidated};
        
        
        for (int i = 0; i < fields.length; i++)
        {
            var field = fields[i];
            var b = validates[i];
            var validator = validators.get(i);
            var regex = regexes[i];
            
            field.addKeyListener(new KeyAdapter() 
            {
                @Override
                public void keyReleased(KeyEvent e) 
                {
                    var str = ((JTextField)e.getSource()).getText();
                    
                    if (str.isBlank() || !str.matches(regex))
                    {
                        field.setBackground(new Color(1, 0, 0, 0.3f));
                        validator.accept(false);
                    }
                    else
                    {
                        field.setBackground(null);  
                        validator.accept(true);
                    }
                
                    insert.setEnabled(validated());
                    System.out.println(idValidated + " " + telValidated + " " +
                        refValidated);
                }
            });
        }
    }
    
    private boolean validated()
    {
        return idValidated && telValidated && refValidated;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        setLocation(new java.awt.Point(0, 0));
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelecomDailyApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelecomDailyApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelecomDailyApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelecomDailyApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelecomDailyApp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
