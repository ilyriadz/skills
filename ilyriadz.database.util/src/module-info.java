/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

module ilyriadz.database.util {
    requires ilyriadz.database;
    
    requires java.desktop;
    requires java.logging;
    
    opens ilyriadz.database.util;
    exports ilyriadz.database.util;
}
