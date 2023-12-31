/**
 * Copyright 2007-2012 Arthur Blake
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.log4jdbc.sql.rdbmsspecifics;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RDBMS specifics for the IBM DB2.
 *
 * @author qxo(qxodream @ gmail.com)
 */
public class Db2RdbmsSpecifics extends RdbmsSpecifics {

    private static final String DATE_FORMAT = "'TIMESTAMP('''yyyy-MM-dd HH:mm:ss.SSS''')'";

    public Db2RdbmsSpecifics() {
        super();
    }

    @Override
    public String formatParameterObject(Object object) {
        if (object instanceof Date) {
            return new SimpleDateFormat(DATE_FORMAT).format((Date) object);
        }
        return super.formatParameterObject(object);
    }
}