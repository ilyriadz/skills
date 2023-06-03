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
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * this class is wrapper of Statement object with helpers methods to
 * make database management easier.<br>
 * you must use an implementation of DatabaseManager to use it.<br>
 * the default H2Database is an implementation using H2 database, 
 * for use this default implementation H2 database jar must be in the module path 
 *  or you can pass a driver instance to <code>setDriver(Driver)</code> method<br>
 * you can implements other database systems by implements this class 
 * <code>driver()</code> and 
 * <code>jdbc()</code> methods.
 * 
 * @author Ilyes Sadaoui
 * @version 1.0
 */
public abstract class DatabaseManager 
{
    private Connection connection;
    private Statement statement;
    private Driver driver = null;

    protected DatabaseManager() 
    {
    }
    
    /**
     * this method most return a valid class path
     * @return the database driver to be loaded
     */
    protected abstract String driver();
    
    /**
     * this method most return a valid JDBC 
     * @return the JDBC of this database system
     */
    protected abstract String jdbc();
    
    public void setDriver(Driver driver)
    {
        this.driver = driver;
    }
    
    private void loadDriver() throws ClassNotFoundException
    {
        if (driver == null)
            Class.forName(driver());
    }
    
    private void initConnection(String databaseName, String name, 
        String password) throws SQLException
    {
        if (driver == null)
            connection = DriverManager.getConnection(jdbc().concat(databaseName), name, password);
        else
        {
            Properties props = new Properties();
            props.put("user", name);
            props.put("password", password);
            connection = driver.connect(jdbc().concat(databaseName), props);
        }
    }
    
    private void load(String databaseName, String name, String passworld) throws SQLException, 
        ClassNotFoundException
    {
        loadDriver();
        initConnection(databaseName, name, passworld);
    }
    
    /**
     * connect to the database file named databaseName
     * @param databaseName the database file name
     * @param name the authentication name
     * @param password the authentication password
     * @throws SQLException if SQL exception occurred
     * @throws ClassNotFoundException if driver class not found 
     */
    public void connect(String databaseName, String name, String password) throws SQLException,
        ClassNotFoundException
    {
        load(databaseName, name, password);
        
        statement = connection.createStatement();
    }
    
    /**
     * connect to the database file named databaseName
     * @param databaseName the database file name
     * @throws SQLException if SQL exception occurred
     * @throws ClassNotFoundException if driver class not found 
     */
    public void connect(String databaseName) throws SQLException, ClassNotFoundException
    {
        connect(databaseName, "", "");
    }
    
    /**
     * connect to the database file named databaseName
     * @param databaseName the database file name
     * @param name the authentication name
     * @param password the authentication password
     * @param resultSetType a result set type; one of
     *        {@code ResultSet.TYPE_FORWARD_ONLY},
     *        {@code ResultSet.TYPE_SCROLL_INSENSITIVE}, or
     *        {@code ResultSet.TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency a concurrency type; one of
     *        {@code ResultSet.CONCUR_READ_ONLY} or
     *        {@code ResultSet.CONCUR_UPDATABLE}
     * @throws SQLException if SQL exception occurred
     * @throws ClassNotFoundException if driver class not found 
     */
    public void connect(String databaseName, String name, String password,
        int resultSetType, int resultSetConcurrency) throws SQLException, 
            ClassNotFoundException
    {
        load(databaseName, name, password);
        statement = connection.createStatement(resultSetType, resultSetConcurrency);
    }
    
    /**
     * connect to the database file named databaseName
     * @param databaseName the database file name
     * @param name the authentication name
     * @param password the authentication password
     * @param resultSetType one of the following {@code ResultSet}
     *        constants:
     *         {@code ResultSet.TYPE_FORWARD_ONLY},
     *         {@code ResultSet.TYPE_SCROLL_INSENSITIVE}, or
     *         {@code ResultSet.TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency one of the following {@code ResultSet}
     *        constants:
     *         {@code ResultSet.CONCUR_READ_ONLY} or
     *         {@code ResultSet.CONCUR_UPDATABLE}
     * @param resultSetHoldability one of the following {@code ResultSet}
     *        constants:
     *         {@code ResultSet.HOLD_CURSORS_OVER_COMMIT} or
     *         {@code ResultSet.CLOSE_CURSORS_AT_COMMIT}
     * @throws SQLException if SQL exception occurred
     * @throws ClassNotFoundException if driver class not found
     */
    public void connect(String databaseName, String name, String password,
        int resultSetType, int resultSetConcurrency, int resultSetHoldability) 
            throws SQLException, ClassNotFoundException
    {
        load(databaseName, name, password);
        statement = connection.createStatement(resultSetType, 
            resultSetConcurrency, resultSetHoldability);
    }
    
    /**
     * execute a SQL statement
     * @param sql the SQL statement
     * @throws SQLException if SQL exception occurred
     */
    public final void execute(String sql) throws SQLException
    {
        statement.execute(sql);
    }
    
    /**
     * execute a SQL query
     * @param sql the SQL query
     * @return a ResultSet object
     * @throws SQLException if SQL exception occurred
     */
    public final ResultSet executeQuery(String sql) throws SQLException
    {
        return statement.executeQuery(sql);
    }
    
    /**
     * execute a SQL update
     * @param sql the SQL update
     * @throws SQLException if SQL exception occurred
     */
    public final void executeUpdate(String sql) throws SQLException
    {
        statement.executeUpdate(sql);
    }
    
    /**
     * the Statement object of this DatabaseManager
     * @return Statement object
     */
    public final Statement statement()
    {
        return statement;
    }
    
    private StringBuilder buildString(String... str)
    {
        var builder = new StringBuilder();
        for (var s : str)
            builder.append(s);
        
        return builder;
    }
    
    /**<code>CREATE TABLE</code> statement<br>
     * create a table in the database
     * @param tableName the table name
     * @param columns the table columns<br> &nbsp;&nbsp;&nbsp;example of a column: 
     * <code>id integer primary key</code> 
     * @throws SQLException if SQL exception occurred
     */
    public void createTable(String tableName, String... columns) 
        throws SQLException
    {
        Objects.requireNonNull(tableName);
        
        var builder = buildString(
            "create table ",
            tableName, 
            "(",
            Arrays.asList(columns).stream()
                .collect(Collectors.joining(",")),
            ")");
        
        execute(builder.toString());
    }
    
    /**<code>CREATE TABLE IF NOT EXISTS</code> statement<br>
     * create a table if not exists
     * @param tableName the table name
     * @param columns the table columns.<br> &nbsp;&nbsp;&nbsp;example of a column: 
     * <code>address varchar(255) not null</code>
     * @throws SQLException if SQL exception occurred
     */
    public void createNotExistTable(String tableName, String... columns) 
        throws SQLException
    {
        Objects.requireNonNull(tableName);
        
        var builder = buildString(
            "create table if not exists ",
            tableName, 
            "(",
            Arrays.asList(columns).stream()
                .collect(Collectors.joining(",")),
            ")");
        
        execute(builder.toString());
    }
    
    /**<code>CREATE TABLE IF NOT EXISTS</code> statement<br>
     * create a table if not exists from a class<br>
     * the field name is a column if the field tagged with <code>
     * {@literal @}TypeProperties</code> annotation.<br>
     * warning: if you are using the module system you must open your module or
     *  package to <code>ilyria.database</code> module to use this method properly.<br>
     * DatabaseManager support int, long, float, double, boolean, String types<br>
     * <code>int</code>     represented as SQL <code>INTEGER</code><br>
     * <code>long</code>    represented as SQL <code>BIGINT</code><br>
     * <code>float</code>   represented as SQL <code>FLOAT</code><br>
     * <code>double</code>  represented as SQL <code>DOUBLE</code><br>
     * <code>boolean</code> represented as SQL <code>BOOLEAN</code><br>
     * reference types represented as SQL <code>VARCHAR</code>
     * @param <T> the class type
     * @param tableName the table name
     * @param cls the class to be reflected
     * @throws SQLException if SQL exception occurred
     */
    public <T> void createNotExistTable(String tableName, Class<T> cls) 
        throws SQLException
    {
        Objects.requireNonNull(tableName);
        Objects.requireNonNull(cls);
        
        var columns = Arrays.asList(cls.getDeclaredFields())
                .stream()
                .filter(e -> e.getAnnotation(TypeProperties.class) != null)
                .map(field ->
                {
                    var name = field.getName();
                    var declarationType = field.getAnnotation(TypeProperties.class);
                    
                    var fieldType = switch (field.getType().getSimpleName()
                        .toLowerCase())
                    {
                        case "int", "integer" -> "integer";
                        case "long" -> "bigint";
                        case "float" -> "float";
                        case "double" -> "double";
                        case "boolean" -> "boolean";
                        default -> "varchar";
                    };
                    
                    if (declarationType != null)
                    {
                        name += " ".concat(fieldType).concat(" ").concat(declarationType.value());
                    }
                    
                    return name;
                })
                .toList();
        
        var builder = buildString(
            "create table if not exists ",
            tableName, 
            "(",
            columns.stream()
                .collect(Collectors.joining(",")),
            ")");
        
        execute(builder.toString());
    }
    
    /**
     * <code>SELECT</code> query
     * @param tableName the table name
     * @param other other SQL after table name
     * @return ResultSet object
     * @throws SQLException if SQL exception occurred
     */
    public ResultSet select(String tableName, String other) throws SQLException
    {
        return executeQuery("select * from ".concat(tableName).concat(" ")
            .concat(other));
    }
    
    /**
     * <code>SELECT</code> query<br>
     * warning: the class must have fields with same name of columns and
     *  the type is equivalent:<br>
     * <code>int</code>     represented as SQL <code>INTEGER</code><br>
     * <code>long</code>    represented as SQL <code>BIGINT</code><br>
     * <code>float</code>   represented as SQL <code>FLOAT</code><br>
     * <code>double</code>  represented as SQL <code>DOUBLE</code><br>
     * <code>boolean</code> represented as SQL <code>BOOLEAN</code><br>
     * reference types represented as SQL <code>VARCHAR</code>
     * @param <T> the class type
     * @param tableName the table name
     * @param others other SQL after table name
     * @param cls the reflected class 
     * @see #select(java.lang.String, java.lang.String) 
     * @return a list of T objects assigned with database table row fields.
     * @throws SQLException if SQL exception occurred
     */
    public <T> List<T> select(String tableName, String others,
        Class<T> cls) throws SQLException
    {
        var rs = select(tableName, others);
        var rsmd = rs.getMetaData();
        
        List<T> lst = new ArrayList<>();
        
        while (rs.next())
        {
            try 
            {
                var object = cls.getConstructor().newInstance();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) 
                {
                    var colName = rsmd.getColumnName(i);
                    
                    var field = Arrays.asList(cls.getDeclaredFields())
                        .stream()
                            .filter(e -> e.getName().equalsIgnoreCase(colName))
                            .findAny().get();  
                    field.setAccessible(true);
                    switch (rsmd.getColumnTypeName(i).toLowerCase())
                    {
                        case "integer" -> field.set(object, rs.getInt(i));
                        case "bigint" -> field.set(object, rs.getLong(i));
                        case "float" -> field.set(object, rs.getFloat(i));
                        case "double" -> field.set(object, rs.getDouble(i));
                        case "varchar" -> field.set(object, rs.getString(i));
                        case "boolean" -> field.set(object, rs.getBoolean(i));
                        default -> field.set(object, rs.getString(i));
                    }
                } // end for 
                lst.add(object);
            } catch (InstantiationException | IllegalAccessException | 
                    IllegalArgumentException | InvocationTargetException | 
                        NoSuchMethodException | SecurityException ex) {
                    Logger.getLogger(DatabaseManager.class.getName()).log(
                        Level.SEVERE, null, ex);
                }
        } // end while
        
        return lst;
    }
    
    /**
     * <code>INSERT</code> statement
     * @param tableName the table name
     * @param columns the columns names
     * @param values the values to be inserted
     * @throws SQLException if SQL exception occurred
     */
    public void insert(String tableName, List<String> columns, String... values)
        throws SQLException
    {
        var builder = buildString("insert into ", tableName, " ");
        if (!columns.isEmpty())
        {
            builder.append("(");
            builder.append(columns.stream()
                .collect(Collectors.joining(", ")));
            builder.append(") ");
        } // end if
        
        builder.append("values(")
            .append(Arrays.asList(values).stream()
                .collect(Collectors.joining(", ")));
        builder.append(")");
        
        execute(builder.toString());
    }
    
    /**
     * <code>UPDATE</code> statement
     * @param tableName the table name
     * @param columns the columns names
     * @param values new values to update
     * @param criteria the <code>WHERE</code> clause criteria
     * @throws SQLException if SQL exception occurred
     */
    public void update(String tableName, List<String> columns, List<String> values,
        String criteria) throws SQLException
    {
        
        var builder = buildString("update ", tableName, " set ");
        for (int i = 0; i < columns.size(); i++) 
            builder.append(columns.get(i)).append("=").append(values.get(i))
                .append(" ");
        if (criteria != null && !criteria.isBlank())
        {
            builder.append(" where ");
            builder.append(criteria);
        }
        
        executeUpdate(builder.toString());
    }
    
    /**
     * <code>DELETE</code> statement
     * @param tableName the table name
     * @param criteria the <code>WHERE</code> clause criteria
     * @throws SQLException if SQL exception occurred
     */
    public void delete(String tableName, String criteria) throws SQLException
    {
        executeUpdate("delete from " + tableName + ((criteria == null ? "" :
                (criteria.isBlank() ? "" : " where ".concat(criteria)))));
    }
    
    /**
     * show the table in the standard output stream
     * @param tableName the table name
     * @throws SQLException if SQL exception occurred
     */
    public void showTable(String tableName) throws SQLException
    {
        showTable(tableName, List.of("*"), "");
    }
    
    /**
     * show the table in standard output stream
     * @param tableName the table name
     * @param columns columns names
     * @param others other SQL
     * @throws SQLException if SQL exception occurred
     */
    public void showTable(String tableName, List<String> columns, String others) 
            throws SQLException
    {
        var builder = buildString(
            "select ",
            columns.stream()
                .collect(Collectors.joining(" ", "", " ")),
            "from ".concat(tableName).concat(" "),
            others);
        
        builder.append(others);
        
        var rs = executeQuery(builder.toString());
        
        var rsmd = rs.getMetaData();
        
        for (int i = 1; i <= rsmd.getColumnCount(); i++)
        {
            System.out.printf("%-20s", rsmd.getColumnName(i));
        } // end for
        
        System.out.println();
        
        while (rs.next())
        {
            for (int i = 1; i <= rsmd.getColumnCount(); i++)
            {
                System.out.printf("%-20s", rs.getString(i));
            } // end for
            
            System.out.println();
        } // end while
    }
}
