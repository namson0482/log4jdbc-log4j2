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
package net.sf.log4jdbc.sql.jdbcapi;

import net.sf.log4jdbc.log.SpyLogDelegator;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * Wraps a CallableStatement and reports method calls, returns and exceptions.
 *
 * @author Arthur Blake
 * @author Mathieu Seppey
 */
public class CallableStatementSpy extends PreparedStatementSpy implements CallableStatement {
    /**
     * The real underlying CallableStatement that this CallableStatementSpy wraps.
     */
    private CallableStatement realCallableStatement;

    /**
     * Create a CallableStatementSpy (JDBC 4 version) to spy upon a CallableStatement.
     *
     * @param sql                   The SQL used for this CallableStatement
     * @param connectionSpy         The ConnectionSpy which produced this CallableStatementSpy
     * @param realCallableStatement The real CallableStatement that is being spied upon
     * @param logDelegator          The <code>SpyLogDelegator</code> used by
     *                              this <code>CallableStatementSpy</code> and all resources obtained
     *                              from it (<code>ResultSet</code>s)
     */
    public CallableStatementSpy(String sql, ConnectionSpy connectionSpy,
                                CallableStatement realCallableStatement, SpyLogDelegator logDelegator) {
        super(sql, connectionSpy, realCallableStatement, logDelegator);
        this.realCallableStatement = realCallableStatement;
    }

    protected void reportAllReturns(String methodCall, String msg) {
        log.methodReturned(this, methodCall, msg);
    }

    /**
     * Get the real underlying CallableStatement that this CallableStatementSpy wraps.
     *
     * @return the real underlying CallableStatement.
     */
    public CallableStatement getRealCallableStatement() {
        return realCallableStatement;
    }

    public String getClassType() {
        return "CallableStatement";
    }

    // forwarding methods
    public Date getDate(int parameterIndex) throws SQLException {
        String methodCall = "getDate(" + parameterIndex + ")";
        try {
            return (Date) reportReturn(methodCall, realCallableStatement.getDate(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        String methodCall = "getDate(" + parameterIndex + ", " + cal + ")";
        try {
            return (Date) reportReturn(methodCall, realCallableStatement.getDate(parameterIndex, cal));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Ref getRef(String parameterName) throws SQLException {
        String methodCall = "getRef(" + parameterName + ")";
        try {
            return (Ref) reportReturn(methodCall, realCallableStatement.getRef(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Time getTime(String parameterName) throws SQLException {
        String methodCall = "getTime(" + parameterName + ")";
        try {
            return (Time) reportReturn(methodCall, realCallableStatement.getTime(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setTime(String parameterName, Time x) throws SQLException {
        String methodCall = "setTime(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setTime(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public Blob getBlob(int i) throws SQLException {
        String methodCall = "getBlob(" + i + ")";
        try {
            return (Blob) reportReturn(methodCall, realCallableStatement.getBlob(i));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Clob getClob(int i) throws SQLException {
        String methodCall = "getClob(" + i + ")";
        try {
            return (Clob) reportReturn(methodCall, realCallableStatement.getClob(i));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Array getArray(int i) throws SQLException {
        String methodCall = "getArray(" + i + ")";
        try {
            return (Array) reportReturn(methodCall, realCallableStatement.getArray(i));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public byte[] getBytes(int parameterIndex) throws SQLException {
        String methodCall = "getBytes(" + parameterIndex + ")";
        try {
            return (byte[]) reportReturn(methodCall, realCallableStatement.getBytes(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public double getDouble(int parameterIndex) throws SQLException {
        String methodCall = "getDouble(" + parameterIndex + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getDouble(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public int getInt(int parameterIndex) throws SQLException {
        String methodCall = "getInt(" + parameterIndex + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getInt(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public boolean wasNull() throws SQLException {
        String methodCall = "wasNull()";
        try {
            return reportReturn(methodCall, realCallableStatement.wasNull());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Time getTime(int parameterIndex) throws SQLException {
        String methodCall = "getTime(" + parameterIndex + ")";
        try {
            return (Time) reportReturn(methodCall, realCallableStatement.getTime(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        String methodCall = "getTime(" + parameterIndex + ", " + cal + ")";
        try {
            return (Time) reportReturn(methodCall, realCallableStatement.getTime(parameterIndex, cal));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        String methodCall = "getTimestamp(" + parameterName + ")";
        try {
            return (Timestamp) reportReturn(methodCall, realCallableStatement.getTimestamp(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        String methodCall = "setTimestamp(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setTimestamp(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public String getString(int parameterIndex) throws SQLException {
        String methodCall = "getString(" + parameterIndex + ")";
        try {
            return (String) reportReturn(methodCall, realCallableStatement.getString(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        String methodCall = "registerOutParameter(" + parameterIndex + ", " + sqlType + ")";
        argTraceSet(parameterIndex, null, "<OUT>");
        try {
            realCallableStatement.registerOutParameter(parameterIndex, sqlType);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        String methodCall = "registerOutParameter(" + parameterIndex + ", " + sqlType + ", " + scale + ")";
        argTraceSet(parameterIndex, null, "<OUT>");
        try {
            realCallableStatement.registerOutParameter(parameterIndex, sqlType, scale);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
        String methodCall = "registerOutParameter(" + paramIndex + ", " + sqlType + ", " + typeName + ")";
        argTraceSet(paramIndex, null, "<OUT>");
        try {
            realCallableStatement.registerOutParameter(paramIndex, sqlType, typeName);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public byte getByte(String parameterName) throws SQLException {
        String methodCall = "getByte(" + parameterName + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getByte(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public double getDouble(String parameterName) throws SQLException {
        String methodCall = "getDouble(" + parameterName + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getDouble(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public float getFloat(String parameterName) throws SQLException {
        String methodCall = "getFloat(" + parameterName + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getFloat(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public int getInt(String parameterName) throws SQLException {
        String methodCall = "getInt(" + parameterName + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getInt(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public long getLong(String parameterName) throws SQLException {
        String methodCall = "getLong(" + parameterName + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getLong(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public short getShort(String parameterName) throws SQLException {
        String methodCall = "getShort(" + parameterName + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getShort(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public boolean getBoolean(String parameterName) throws SQLException {
        String methodCall = "getBoolean(" + parameterName + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getBoolean(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        String methodCall = "getBytes(" + parameterName + ")";
        try {
            return (byte[]) reportReturn(methodCall, realCallableStatement.getBytes(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setByte(String parameterName, byte x) throws SQLException {
        String methodCall = "setByte(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setByte(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setDouble(String parameterName, double x) throws SQLException {
        String methodCall = "setDouble(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setDouble(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setFloat(String parameterName, float x) throws SQLException {
        String methodCall = "setFloat(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setFloat(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        String methodCall = "registerOutParameter(" + parameterName + ", " + sqlType + ")";
        try {
            realCallableStatement.registerOutParameter(parameterName, sqlType);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setInt(String parameterName, int x) throws SQLException {
        String methodCall = "setInt(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setInt(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setNull(String parameterName, int sqlType) throws SQLException {
        String methodCall = "setNull(" + parameterName + ", " + sqlType + ")";
        try {
            realCallableStatement.setNull(parameterName, sqlType);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        String methodCall = "registerOutParameter(" + parameterName + ", " + sqlType + ", " + scale + ")";
        try {
            realCallableStatement.registerOutParameter(parameterName, sqlType, scale);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setLong(String parameterName, long x) throws SQLException {
        String methodCall = "setLong(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setLong(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setShort(String parameterName, short x) throws SQLException {
        String methodCall = "setShort(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setShort(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBoolean(String parameterName, boolean x) throws SQLException {
        String methodCall = "setBoolean(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setBoolean(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBytes(String parameterName, byte[] x) throws SQLException {
        //todo: dump byte array?
        String methodCall = "setBytes(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setBytes(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        String methodCall = "getBoolean(" + parameterIndex + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getBoolean(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        String methodCall = "getTimestamp(" + parameterIndex + ")";
        try {
            return (Timestamp) reportReturn(methodCall, realCallableStatement.getTimestamp(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        String methodCall = "setAsciiStream(" + parameterName + ", " + x + ", " + length + ")";
        try {
            realCallableStatement.setAsciiStream(parameterName, x, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        String methodCall = "setBinaryStream(" + parameterName + ", " + x + ", " + length + ")";
        try {
            realCallableStatement.setBinaryStream(parameterName, x, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        String methodCall = "setCharacterStream(" + parameterName + ", " + reader + ", " + length + ")";
        try {
            realCallableStatement.setCharacterStream(parameterName, reader, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public Object getObject(String parameterName) throws SQLException {
        String methodCall = "getObject(" + parameterName + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getObject(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setObject(String parameterName, Object x) throws SQLException {
        String methodCall = "setObject(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setObject(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        String methodCall = "setObject(" + parameterName + ", " + x + ", " + targetSqlType + ")";
        try {
            realCallableStatement.setObject(parameterName, x, targetSqlType);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        String methodCall = "setObject(" + parameterName + ", " + x + ", " + targetSqlType + ", " + scale + ")";
        try {
            realCallableStatement.setObject(parameterName, x, targetSqlType, scale);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        String methodCall = "getTimestamp(" + parameterIndex + ", " + cal + ")";
        try {
            return (Timestamp) reportReturn(methodCall, realCallableStatement.getTimestamp(parameterIndex, cal));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        String methodCall = "getDate(" + parameterName + ", " + cal + ")";
        try {
            return (Date) reportReturn(methodCall, realCallableStatement.getDate(parameterName, cal));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        String methodCall = "getTime(" + parameterName + ", " + cal + ")";
        try {
            return (Time) reportReturn(methodCall, realCallableStatement.getTime(parameterName, cal));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        String methodCall = "getTimestamp(" + parameterName + ", " + cal + ")";
        try {
            return (Timestamp) reportReturn(methodCall, realCallableStatement.getTimestamp(parameterName, cal));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        String methodCall = "setDate(" + parameterName + ", " + x + ", " + cal + ")";
        try {
            realCallableStatement.setDate(parameterName, x, cal);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        String methodCall = "setTime(" + parameterName + ", " + x + ", " + cal + ")";
        try {
            realCallableStatement.setTime(parameterName, x, cal);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        String methodCall = "setTimestamp(" + parameterName + ", " + x + ", " + cal + ")";
        try {
            realCallableStatement.setTimestamp(parameterName, x, cal);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public short getShort(int parameterIndex) throws SQLException {
        String methodCall = "getShort(" + parameterIndex + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getShort(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public long getLong(int parameterIndex) throws SQLException {
        String methodCall = "getLong(" + parameterIndex + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getLong(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public float getFloat(int parameterIndex) throws SQLException {
        String methodCall = "getFloat(" + parameterIndex + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getFloat(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Ref getRef(int i) throws SQLException {
        String methodCall = "getRef(" + i + ")";
        try {
            return (Ref) reportReturn(methodCall, realCallableStatement.getRef(i));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    /**
     * @deprecated
     */
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        String methodCall = "getBigDecimal(" + parameterIndex + ", " + scale + ")";
        try {
            return (BigDecimal) reportReturn(methodCall, realCallableStatement.getBigDecimal(parameterIndex, scale));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public URL getURL(int parameterIndex) throws SQLException {
        String methodCall = "getURL(" + parameterIndex + ")";
        try {
            return (URL) reportReturn(methodCall, realCallableStatement.getURL(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }

    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        String methodCall = "getBigDecimal(" + parameterIndex + ")";
        try {
            return (BigDecimal) reportReturn(methodCall, realCallableStatement.getBigDecimal(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public byte getByte(int parameterIndex) throws SQLException {
        String methodCall = "getByte(" + parameterIndex + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getByte(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Object getObject(int parameterIndex) throws SQLException {
        String methodCall = "getObject(" + parameterIndex + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getObject(parameterIndex));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        String methodCall = "getObject(" + i + ", " + map + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getObject(i, map));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public String getString(String parameterName) throws SQLException {
        String methodCall = "getString(" + parameterName + ")";
        try {
            return (String) reportReturn(methodCall, realCallableStatement.getString(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        String methodCall = "registerOutParameter(" + parameterName + ", " + sqlType + ", " + typeName + ")";
        try {
            realCallableStatement.registerOutParameter(parameterName, sqlType, typeName);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        String methodCall = "setNull(" + parameterName + ", " + sqlType + ", " + typeName + ")";
        try {
            realCallableStatement.setNull(parameterName, sqlType, typeName);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setString(String parameterName, String x) throws SQLException {
        String methodCall = "setString(" + parameterName + ", " + x + ")";

        try {
            realCallableStatement.setString(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        String methodCall = "getBigDecimal(" + parameterName + ")";
        try {
            return (BigDecimal) reportReturn(methodCall, realCallableStatement.getBigDecimal(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        String methodCall = "getObject(" + parameterName + ", " + map + ")";
        try {
            return reportReturn(methodCall, realCallableStatement.getObject(parameterName, map));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        String methodCall = "setBigDecimal(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setBigDecimal(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public URL getURL(String parameterName) throws SQLException {
        String methodCall = "getURL(" + parameterName + ")";
        try {
            return (URL) reportReturn(methodCall, realCallableStatement.getURL(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public RowId getRowId(int parameterIndex) throws SQLException {
        return null;
    }

    public RowId getRowId(String parameterName) throws SQLException {
        return null;
    }

    public void setRowId(String parameterName, RowId x) throws SQLException {

    }

    public void setNString(String parameterName, String value) throws SQLException {

    }

    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {

    }

    public void setNClob(String parameterName, NClob value) throws SQLException {

    }

    public void setClob(String parameterName, Reader reader, long length) throws SQLException {

    }

    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {

    }

    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {

    }

    public NClob getNClob(int parameterIndex) throws SQLException {
        return null;
    }

    public NClob getNClob(String parameterName) throws SQLException {
        return null;
    }

    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {

    }

    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return null;
    }

    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return null;
    }

    public String getNString(int parameterIndex) throws SQLException {
        return null;
    }

    public String getNString(String parameterName) throws SQLException {
        return null;
    }

    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return null;
    }

    public Reader getNCharacterStream(String parameterName) throws SQLException {
        return null;
    }

    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        return null;
    }

    public Reader getCharacterStream(String parameterName) throws SQLException {
        return null;
    }

    public void setBlob(String parameterName, Blob x) throws SQLException {

    }

    public void setClob(String parameterName, Clob x) throws SQLException {

    }

    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {

    }

    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {

    }

    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {

    }

    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {

    }

    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {

    }

    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {

    }

    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {

    }

    public void setClob(String parameterName, Reader reader) throws SQLException {

    }

    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {

    }

    public void setNClob(String parameterName, Reader reader) throws SQLException {

    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        return null;
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        return null;
    }

    public void setURL(String parameterName, URL val) throws SQLException {
        String methodCall = "setURL(" + parameterName + ", " + val + ")";
        try {
            realCallableStatement.setURL(parameterName, val);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public Array getArray(String parameterName) throws SQLException {
        String methodCall = "getArray(" + parameterName + ")";
        try {
            return (Array) reportReturn(methodCall, realCallableStatement.getArray(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Blob getBlob(String parameterName) throws SQLException {
        String methodCall = "getBlob(" + parameterName + ")";
        try {
            return (Blob) reportReturn(methodCall, realCallableStatement.getBlob(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Clob getClob(String parameterName) throws SQLException {
        String methodCall = "getClob(" + parameterName + ")";
        try {
            return (Clob) reportReturn(methodCall, realCallableStatement.getClob(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Date getDate(String parameterName) throws SQLException {
        String methodCall = "getDate(" + parameterName + ")";
        try {
            return (Date) reportReturn(methodCall, realCallableStatement.getDate(parameterName));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setDate(String parameterName, Date x) throws SQLException {
        String methodCall = "setDate(" + parameterName + ", " + x + ")";
        try {
            realCallableStatement.setDate(parameterName, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }
}
