/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

open module ilyriadz.telecomdaily {
    
    requires ilyriadz.database;
    requires static com.h2database;
    requires java.desktop;
    requires java.logging;
    requires ilyriadz.database.util;
    
    uses ilyriadz.database.DatabaseManager;
}
