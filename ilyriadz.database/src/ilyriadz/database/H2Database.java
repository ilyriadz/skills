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

/**
 *
 * @author Ilyes Sadaoui
 */
public class H2Database extends DatabaseManager
{

    public H2Database()
    {
    }

    @Override
    protected String driver() 
    {
        return "org.h2.Driver";
    }

    @Override
    protected String jdbc()
    {
        return "jdbc:h2:";
    }
    
}
