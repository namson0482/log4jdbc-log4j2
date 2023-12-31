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
import net.sf.log4jdbc.sql.Spy;
import net.sf.log4jdbc.sql.rdbmsspecifics.RdbmsSpecifics;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * Wraps a JDBC Connection and reports method calls, returns and exceptions.
 * <p>
 * This version is for jdbc 3.
 * <p>
 * <h3>Modifications for log4j2: </h3>
 * <ul>
 * <li>Addition of new constructors, to accept a parameter <code>execTime</code>,
 * a <code>long</code> defining the time elapsed to open the connection in ms.
 * (see <code>SpyLogDelegator#connectionOpened(Spy, long)</code> for more details,
 * and modifications in <code>DriverSpy#connect(String, Properties)</code>).
 * <li>Modification of the method <code>close()</code> in order to compute
 * execution time to close the connection (see <code>SpyLogDelegator#connectionClosed(Spy, long),
 * or before an <code>Exception</code> is thrown if a problem occurs. </code>)
 * <li>Addition of a new method <code>ConnectionSpy#reportException(String, SQLException, long)</code>
 * to log execution time before an <code>Exception</code> is thrown when the connection closing failed.
 * </ul>
 *
 * @author Arthur Blake
 * @author Frederic Bastian
 * @author Mathieu Seppey
 */
public class ConnectionSpy implements Connection, Spy {
    /**
     * Contains a Mapping of connectionNumber to currently open ConnectionSpy
     * objects.
     */
    private static final Map<Integer, ConnectionSpy> connectionTracker =
            new HashMap<Integer, ConnectionSpy>();
    private static int lastConnectionNumber = 0;
    private final Integer connectionNumber;
    private Connection realConnection;
    private SpyLogDelegator log;
    private RdbmsSpecifics rdbmsSpecifics;

    /**
     * Create a new ConnectionSpy that wraps a given Connection.
     *
     * @param realConnection &quot;real&quot; Connection that this ConnectionSpy wraps.
     * @param logDelegator   The <code>SpyLogDelegator</code> used by
     *                       this <code>ConnectionSpy</code> and all resources obtained from it
     *                       (<code>StatementSpy</code>s, ...)
     */
    public ConnectionSpy(Connection realConnection, SpyLogDelegator logDelegator) {
        this(realConnection, DriverSpy.defaultRdbmsSpecifics, logDelegator);
    }

    /**
     * Create a new ConnectionSpy that wraps a given Connection.
     *
     * @param realConnection &quot;real&quot; Connection that this ConnectionSpy wraps.
     * @param execTime       a <code>long</code> defining the time in ms
     *                       taken to open the connection to <code>realConnection</code>.
     * @param logDelegator   The <code>SpyLogDelegator</code> used by
     *                       this <code>ConnectionSpy</code> and all resources obtained from it
     *                       (<code>StatementSpy</code>s, ...)
     */
    public ConnectionSpy(Connection realConnection, long execTime, SpyLogDelegator logDelegator) {
        this(realConnection, null, execTime, logDelegator);
    }

    /**
     * Create a new ConnectionSpy that wraps a given Connection.
     *
     * @param realConnection &quot;real&quot; Connection that this ConnectionSpy wraps.
     * @param rdbmsSpecifics the RdbmsSpecifics object for formatting logging appropriate for the Rdbms used.
     * @param logDelegator   The <code>SpyLogDelegator</code> used by
     *                       this <code>ConnectionSpy</code> and all resources obtained from it
     *                       (<code>StatementSpy</code>s, ...)
     */
    public ConnectionSpy(Connection realConnection, RdbmsSpecifics rdbmsSpecifics,
                         SpyLogDelegator logDelegator) {
        this(realConnection, rdbmsSpecifics, -1L, logDelegator);
    }

    /**
     * Create a new ConnectionSpy that wraps a given Connection.
     *
     * @param realConnection &quot;real&quot; Connection that this ConnectionSpy wraps.
     * @param rdbmsSpecifics the RdbmsSpecifics object for formatting logging appropriate for the Rdbms used.
     * @param execTime       a <code>long</code> defining the time in ms
     *                       taken to open the connection to <code>realConnection</code>.
     *                       Should be equals to -1 if not used.
     * @param logDelegator   The <code>SpyLogDelegator</code> used by
     *                       this <code>ConnectionSpy</code> and all resources obtained from it
     *                       (<code>StatementSpy</code>s, ...)
     */
    public ConnectionSpy(Connection realConnection, RdbmsSpecifics rdbmsSpecifics,
                         long execTime, SpyLogDelegator logDelegator) {
        if (rdbmsSpecifics == null) {
            rdbmsSpecifics = DriverSpy.defaultRdbmsSpecifics;
        }
        setRdbmsSpecifics(rdbmsSpecifics);
        if (realConnection == null) {
            throw new IllegalArgumentException("Must pass in a non null real Connection");
        }
        this.realConnection = realConnection;
        log = logDelegator;

        synchronized (connectionTracker) {
            connectionNumber = new Integer(++lastConnectionNumber);
            connectionTracker.put(connectionNumber, this);
        }
        log.connectionOpened(this, execTime);
        reportReturn("new Connection");
    }

    /**
     * Get a dump of how many connections are open, and which connection numbers
     * are open.
     *
     * @return an open connection dump.
     */
    public static String getOpenConnectionsDump() {
        StringBuffer dump = new StringBuffer();
        int size;
        Integer[] keysArr;
        synchronized (connectionTracker) {
            size = connectionTracker.size();
            if (size == 0) {
                return "open connections:  none";
            }
            Set<Integer> keys = connectionTracker.keySet();
            keysArr = keys.toArray(new Integer[keys.size()]);
        }

        Arrays.sort(keysArr);

        dump.append("open connections:  ");
        for (int i = 0; i < keysArr.length; i++) {
            dump.append(keysArr[i]);
            dump.append(" ");
        }

        dump.append("(");
        dump.append(size);
        dump.append(")");
        return dump.toString();
    }

    /**
     * Get the real underlying Connection that this ConnectionSpy wraps.
     *
     * @return the real underlying Connection.
     */
    public Connection getRealConnection() {
        return realConnection;
    }

    /**
     * Get the RdbmsSpecifics object for formatting logging appropriate for the Rdbms used on this connection.
     *
     * @return the RdbmsSpecifics object for formatting logging appropriate for the Rdbms used.
     */
    RdbmsSpecifics getRdbmsSpecifics() {
        return rdbmsSpecifics;
    }

    /**
     * Set the RdbmsSpecifics object for formatting logging appropriate for the Rdbms used on this connection.
     *
     * @param rdbmsSpecifics the RdbmsSpecifics object for formatting logging appropriate for the Rdbms used.
     */
    void setRdbmsSpecifics(RdbmsSpecifics rdbmsSpecifics) {
        this.rdbmsSpecifics = rdbmsSpecifics;
    }

    public Integer getConnectionNumber() {
        return connectionNumber;
    }

    public String getClassType() {
        return "Connection";
    }

    protected void reportException(String methodCall, SQLException exception, String sql) {
        log.exceptionOccured(this, methodCall, exception, sql, -1L);
    }

    protected void reportException(String methodCall, SQLException exception) {
        log.exceptionOccured(this, methodCall, exception, null, -1L);
    }

    protected void reportException(String methodCall, SQLException exception, long execTime) {
        log.exceptionOccured(this, methodCall, exception, null, execTime);
    }

    protected void reportAllReturns(String methodCall, String returnValue) {
        log.methodReturned(this, methodCall, returnValue);
    }

    private boolean reportReturn(String methodCall, boolean value) {
        reportAllReturns(methodCall, "" + value);
        return value;
    }

    private int reportReturn(String methodCall, int value) {
        reportAllReturns(methodCall, "" + value);
        return value;
    }

    private <T> T reportReturn(String methodCall, T value) {
        reportAllReturns(methodCall, "" + value);
        return value;
    }

    private void reportReturn(String methodCall) {
        reportAllReturns(methodCall, "");
    }

    private void reportClosed(long execTime) {
        log.connectionClosed(this, execTime);
    }

    // forwarding methods

    public boolean isClosed() throws SQLException {
        String methodCall = "isClosed()";
        try {
            return reportReturn(methodCall, (realConnection.isClosed()));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        String methodCall = "getWarnings()";
        try {
            return reportReturn(methodCall, realConnection.getWarnings());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Savepoint setSavepoint() throws SQLException {
        String methodCall = "setSavepoint()";
        try {
            return reportReturn(methodCall, realConnection.setSavepoint());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        String methodCall = "releaseSavepoint(" + savepoint + ")";
        try {
            realConnection.releaseSavepoint(savepoint);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        String methodCall = "rollback(" + savepoint + ")";
        try {
            realConnection.rollback(savepoint);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        String methodCall = "getMetaData()";
        try {
            return reportReturn(methodCall, realConnection.getMetaData());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void clearWarnings() throws SQLException {
        String methodCall = "clearWarnings()";
        try {
            realConnection.clearWarnings();
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public Statement createStatement() throws SQLException {
        String methodCall = "createStatement()";
        try {
            Statement statement = realConnection.createStatement();
            return reportReturn(methodCall, new StatementSpy(this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        String methodCall = "createStatement(" + resultSetType + ", " + resultSetConcurrency + ")";
        try {
            Statement statement = realConnection.createStatement(resultSetType, resultSetConcurrency);
            return reportReturn(methodCall, new StatementSpy(this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        String methodCall = "createStatement(" + resultSetType + ", " + resultSetConcurrency + ", " + resultSetHoldability + ")";
        try {
            Statement statement = realConnection.createStatement(resultSetType, resultSetConcurrency,
                    resultSetHoldability);
            return reportReturn(methodCall, new StatementSpy(this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        String methodCall = "prepareStatement(" + sql + ")";
        try {
            PreparedStatement statement = realConnection.prepareStatement(sql);
            return reportReturn(methodCall, new PreparedStatementSpy(sql, this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s, sql);
            throw s;
        }
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        String methodCall = "prepareStatement(" + sql + ", " + autoGeneratedKeys + ")";
        try {
            PreparedStatement statement = realConnection.prepareStatement(sql, autoGeneratedKeys);
            return reportReturn(methodCall, new PreparedStatementSpy(sql, this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s, sql);
            throw s;
        }
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        String methodCall = "prepareStatement(" + sql + ", " + resultSetType + ", " + resultSetConcurrency + ")";
        try {
            PreparedStatement statement = realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
            return reportReturn(methodCall, new PreparedStatementSpy(sql, this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s, sql);
            throw s;
        }
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        String methodCall = "prepareStatement(" + sql + ", " + resultSetType + ", " + resultSetConcurrency + ", " + resultSetHoldability + ")";
        try {
            PreparedStatement statement = realConnection.prepareStatement(sql, resultSetType, resultSetConcurrency,
                    resultSetHoldability);
            return reportReturn(methodCall, new PreparedStatementSpy(sql, this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s, sql);
            throw s;
        }
    }

    public PreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException {
        //todo: dump the array here?
        String methodCall = "prepareStatement(" + sql + ", " + columnIndexes + ")";
        try {
            PreparedStatement statement = realConnection.prepareStatement(sql, columnIndexes);
            return reportReturn(methodCall, new PreparedStatementSpy(sql, this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s, sql);
            throw s;
        }
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        String methodCall = "setSavepoint(" + name + ")";
        try {
            return reportReturn(methodCall, realConnection.setSavepoint(name));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public PreparedStatement prepareStatement(String sql, String columnNames[]) throws SQLException {
        //todo: dump the array here?
        String methodCall = "prepareStatement(" + sql + ", " + columnNames + ")";
        try {
            PreparedStatement statement = realConnection.prepareStatement(sql, columnNames);
            return reportReturn(methodCall, new PreparedStatementSpy(sql, this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s, sql);
            throw s;
        }
    }

    public Clob createClob() throws SQLException {
        return null;
    }

    public Blob createBlob() throws SQLException {
        return null;
    }

    public NClob createNClob() throws SQLException {
        return null;
    }

    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    public Properties getClientInfo() throws SQLException {
        return null;
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    public String getSchema() throws SQLException {
        return null;
    }

    public void setSchema(String schema) throws SQLException {

    }

    public void abort(Executor executor) throws SQLException {

    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    public boolean isReadOnly() throws SQLException {
        String methodCall = "isReadOnly()";
        try {
            return reportReturn(methodCall, realConnection.isReadOnly());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        String methodCall = "setReadOnly(" + readOnly + ")";
        try {
            realConnection.setReadOnly(readOnly);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        String methodCall = "prepareCall(" + sql + ")";
        try {
            CallableStatement statement = realConnection.prepareCall(sql);
            return reportReturn(methodCall, new CallableStatementSpy(sql, this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s, sql);
            throw s;
        }
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        String methodCall = "prepareCall(" + sql + ", " + resultSetType + ", " + resultSetConcurrency + ")";
        try {
            CallableStatement statement = realConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
            return reportReturn(methodCall, new CallableStatementSpy(sql, this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s, sql);
            throw s;
        }
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        String methodCall = "prepareCall(" + sql + ", " + resultSetType + ", " + resultSetConcurrency + ", " + resultSetHoldability + ")";
        try {
            CallableStatement statement = realConnection.prepareCall(sql, resultSetType, resultSetConcurrency,
                    resultSetHoldability);
            return reportReturn(methodCall, new CallableStatementSpy(sql, this, statement, this.log));
        } catch (SQLException s) {
            reportException(methodCall, s, sql);
            throw s;
        }
    }

    public String nativeSQL(String sql) throws SQLException {
        String methodCall = "nativeSQL(" + sql + ")";
        try {
            return reportReturn(methodCall, realConnection.nativeSQL(sql));
        } catch (SQLException s) {
            reportException(methodCall, s, sql);
            throw s;
        }
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        String methodCall = "getTypeMap()";
        try {
            return reportReturn(methodCall, realConnection.getTypeMap());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {
        //todo: dump map??
        String methodCall = "setTypeMap(" + map + ")";
        try {
            realConnection.setTypeMap(map);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public String getCatalog() throws SQLException {
        String methodCall = "getCatalog()";
        try {
            return reportReturn(methodCall, realConnection.getCatalog());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setCatalog(String catalog) throws SQLException {
        String methodCall = "setCatalog(" + catalog + ")";
        try {
            realConnection.setCatalog(catalog);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public boolean getAutoCommit() throws SQLException {
        String methodCall = "getAutoCommit()";
        try {
            return reportReturn(methodCall, realConnection.getAutoCommit());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        String methodCall = "setAutoCommit(" + autoCommit + ")";
        try {
            realConnection.setAutoCommit(autoCommit);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public int getHoldability() throws SQLException {
        String methodCall = "getHoldability()";
        try {
            return reportReturn(methodCall, realConnection.getHoldability());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setHoldability(int holdability) throws SQLException {
        String methodCall = "setHoldability(" + holdability + ")";
        try {
            realConnection.setHoldability(holdability);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public int getTransactionIsolation() throws SQLException {
        String methodCall = "getTransactionIsolation()";
        try {
            return reportReturn(methodCall, realConnection.getTransactionIsolation());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setTransactionIsolation(int level) throws SQLException {
        String methodCall = "setTransactionIsolation(" + level + ")";
        try {
            realConnection.setTransactionIsolation(level);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void commit() throws SQLException {
        String methodCall = "commit()";
        try {
            realConnection.commit();
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void rollback() throws SQLException {
        String methodCall = "rollback()";
        try {
            realConnection.rollback();
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void close() throws SQLException {
        String methodCall = "close()";
        long tstart = System.currentTimeMillis();
        try {
            realConnection.close();
        } catch (SQLException s) {
            reportException(methodCall, s, System.currentTimeMillis() - tstart);
            throw s;
        } finally {
            synchronized (connectionTracker) {
                connectionTracker.remove(connectionNumber);
            }
            reportClosed(System.currentTimeMillis() - tstart);
        }
        reportReturn(methodCall);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}