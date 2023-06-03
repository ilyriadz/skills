/*
 * Copyright 2023 Ilyes Sadaoui.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ilyriadz.database;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.SQLException;

/**
 *
 * @author Ilyes Sadaoui
 */
public class H2DatabaseClient extends H2Database
{

    @Override
    protected String jdbc() {
        return super.jdbc().concat("tcp://");
    }
 
    public static void main(String[] args) throws SQLException, 
            ClassNotFoundException, MalformedURLException,
            NoSuchMethodException, InstantiationException, 
            IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException  
    {
        
        
        /*H2DatabaseClient db = new H2DatabaseClient();
        db.connect("localhost/~/test", "ilyes", "sadaoui");
        db.showTable("test");*/
        
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{
            Paths.get("/home/kiradja/Documents/java/libs/h2/bin/h2-1.4.200.jar").toUri().toURL()});
        var clazz = classLoader.loadClass("org.h2.Driver");
        var driver = (Driver)clazz.getConstructor().newInstance();
        
        H2Database db2 = new H2Database();
        db2.setDriver(driver);
        db2.connect("./testing");
        db2.createNotExistTable("testing", "id int primary key", "name varchar(255)");

        db2.delete("testing", "name like 'bachir'");
        
        db2.showTable("testing");
                
    }//*/
}
