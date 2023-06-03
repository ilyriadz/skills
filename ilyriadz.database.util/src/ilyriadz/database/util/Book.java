/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.database.util;

import ilyriadz.database.TypeProperties;

/**
 *
 * @author user
 */
public class Book {
    @TypeProperties("primary key")
            private String isbn;
            
            @TypeProperties("not null")
            private String name;
            
            private int a;

            public Book() {
            }
            
            

            @Override
            public String toString() {
                return "Book(" + "isbn=" + isbn + ", name=" + name + ')';
            }
}
