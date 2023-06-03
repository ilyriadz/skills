/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.telecomdaily;

import ilyriadz.database.DatabaseManager;
import ilyriadz.database.H2Database;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ServiceLoader;

/**
 *
 * @author user
 */
public class Main 
{
    public static void main(String[] args) throws SQLException, ClassNotFoundException 
    {
        ServiceLoader<DatabaseManager> sl = ServiceLoader.load(DatabaseManager.class);
        var dbm = sl.findFirst().get();
        
        if (dbm instanceof H2Database db)
        {
            db.connect("./telecomdailydb");
            db.createNotExistTable("telref", TelecomRef.class);
            
            db.showTable("telref");
            
            /*db.insert("telref", List.of(), "77", "905000", "'1.2.3'",
                "'" + LocalDateTime.now().format(TelecomRef.DATE_TIME_FORMATER) + "'");//*/
            
            System.out.println(db.select("telref", "", TelecomRef.class));
        }
            
    }
}
