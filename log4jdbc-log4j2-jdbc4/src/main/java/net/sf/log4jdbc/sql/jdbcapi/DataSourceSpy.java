/**
 * Copyright 2010 Tim Azzopardi
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
import net.sf.log4jdbc.log.SpyLogFactory;
import net.sf.log4jdbc.sql.Spy;
import net.sf.log4jdbc.sql.rdbmsspecifics.RdbmsSpecifics;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;


/**
 * A proxy datasource that can be used to wrap a real data source allowing log4jdbc to do its work on the real
 * data source.
 * Inspired by http://groups.google.com/group/log4jdbc/browse_thread/thread/0706611d1b85e210
 * <p>
 * This can be useful in a Spring context. Imagine your spring context includes this datasource definition
 *
 * <pre>{@code
 *  <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
 *      <property name="driverClass" value="${datasource.driverClassName}"/>
 *       <property name="jdbcUrl" value="${datasource.url}"/>
 *       <property name="user" value="${datasource.username}"/>
 *       <property name="password" value="${datasource.password}"/>
 *       <property name="initialPoolSize" value="${datasource.initialPoolSize}" />
 *       <property name="minPoolSize" value="${datasource.minPoolSize}" />
 *       <property name="maxPoolSize" value="${datasource.maxPoolSize}" />
 *       <property name="maxStatements" value="${datasource.maxStatements}" />
 *   </bean>
 * }</pre>
 * <p>
 * You can get log4jdbc to work on this using the following config changes
 *
 * <pre>{@code
 *  <bean id="dataSourceSpied" class="com.mchange.v2.c3p0.ComboPooledDataSource">
 *      <property name="driverClass" value="${datasource.driverClassName}"/>
 *       <property name="jdbcUrl" value="${datasource.url}"/>
 *       <property name="user" value="${datasource.username}"/>
 *       <property name="password" value="${datasource.password}"/>
 *       <property name="initialPoolSize" value="${datasource.initialPoolSize}" />
 *       <property name="minPoolSize" value="${datasource.minPoolSize}" />
 *       <property name="maxPoolSize" value="${datasource.maxPoolSize}" />
 *       <property name="maxStatements" value="${datasource.maxStatements}" />
 *   </bean>
 *
 *   <bean id="dataSource" class="net.sf.log4jdbc.Log4jdbcProxyDataSource">
 *     <constructor-arg ref="dataSourceSpied" />
 *   </bean>
 * }</pre>
 * <p>
 * You can also specify custom SQL formatting details,
 * by providing a custom {@code RdmsSpecifics}:
 * <pre>
 * {@code
 * <bean id="dataSourceSpied" class="...">
 * ...
 * </bean>
 *
 * <bean id="slf4jDelegator" class="net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator"/>
 *
 * <bean id="rdbmsSpecifics" class="net.sf.log4jdbc.sql.rdbmsspecifics.OracleRdbmsSpecifics"/>
 *
 * <bean id="dataSource" class="net.sf.log4jdbc.Log4jdbcProxyDataSource">
 * <constructor-arg ref="dataSourceSpied" />
 * <constructor-arg ref="slf4jDelegator" />
 * <constructor-arg ref="rdbmsSpecifics" />
 * </bean>
 * }
 * </pre>
 *
 * @author tim.azzopardi Log4jdbcProxyDataSource.java
 * @author Mathieu Seppey renamed and modified
 * @author pdjohe - custom RdbmsSpecifics
 */
public class DataSourceSpy implements DataSource, Spy {
    private DataSource realDataSource;
    /**
     * The <code>SpyLogDelegator</code> used by this <code>DataSource</code>
     * and used by all resources obtained starting from this <code>DataSource</code>
     * (<code>Connection</code>s, <code>ResultSet</code>s, ...).
     */
    private SpyLogDelegator spyLogDelegator;

    /**
     * A {@code RdbmsSpecifics} that can be set by users to use
     * their own custom formatting details.
     */
    private RdbmsSpecifics rdbmsSpecifics;

    /**
     * Constructor.
     *
     * @param realDataSource the real {@code javax.sql.DataSource} wrapped.
     */
    public DataSourceSpy(DataSource realDataSource) {
        this(realDataSource, SpyLogFactory.getSpyLogDelegator());
    }

    /**
     * Constructor.
     *
     * @param realDataSource the real {@code javax.sql.DataSource} wrapped.
     * @param logDelegator   the {@code SpyLogDelegator} to perform logging.
     */
    public DataSourceSpy(DataSource realDataSource, SpyLogDelegator logDelegator) {
        this(realDataSource, logDelegator, null);
    }

    /**
     * Constructor.
     *
     * @param realDataSource the real {@code javax.sql.DataSource} wrapped.
     * @param logDelegator   the {@code SpyLogDelegator} to perform logging.
     * @param rdbmsSpecifics the {@code RdbmsSpecifics} to format the SQL before logging.
     */
    public DataSourceSpy(DataSource realDataSource, SpyLogDelegator logDelegator,
                         RdbmsSpecifics rdbmsSpecifics) {
        if (realDataSource == null) {
            throw new IllegalStateException("realDataSource must not be null");
        }
        if (logDelegator == null) {
            throw new IllegalStateException("logDelegator must not be null");
        }
        this.realDataSource = realDataSource;
        this.spyLogDelegator = logDelegator;
        this.rdbmsSpecifics = rdbmsSpecifics;
    }

    /**
     * Deprecated to match Bean standard getter name.
     *
     * @see #getSpyLogDelegator
     */
    @Deprecated
    public SpyLogDelegator getLogDelegator() {
        return this.getSpyLogDelegator();
    }

    /**
     * Deprecated to match Bean standard setter name.
     *
     * @see #setSpyLogDelegator(SpyLogDelegator)
     */
    @Deprecated
    public void setLogDelegator(SpyLogDelegator spyLogDelegator) {
        this.setSpyLogDelegator(spyLogDelegator);
    }

    /**
     * Get the <code>SpyLogDelegator</code> that are used by all resources
     * obtained starting from this <code>DataSource</code>
     * (<code>Connection</code>s, <code>ResultSet</code>s, ...).
     *
     * @return The <code>SpyLogDelegator</code> currently used
     * by this <code>DataSource</code>.
     */
    public SpyLogDelegator getSpyLogDelegator() {
        return this.spyLogDelegator;
    }

    /**
     * Set a custom <code>SpyLogDelegator</code> to be used by all resources
     * provided by this <code>DataSource</code>, rather than the default
     * <code>SpyLogDelegator</code> returned by
     * {@link net.sf.log4jdbc.log.SpyLogFactory#getSpyLogDelegator()
     * SpyLogFactory#getSpyLogDelegator()}.
     *
     * @param spyLogDelegator The <code>SpyLogDelegator</code> to be used by all resources
     *                        obtained starting from this <code>DataSource</code>
     *                        (<code>Connection</code>s, <code>ResultSet</code>s, ...).
     */
    public void setSpyLogDelegator(SpyLogDelegator spyLogDelegator) {
        this.spyLogDelegator = spyLogDelegator;
    }

    /**
     * Report to the logger all exceptions which have to be reported by this class
     *
     * @param methodCall the method which threw an exception
     * @param exception  the thrown exception
     */
    protected void reportException(String methodCall, SQLException exception) {
        spyLogDelegator.exceptionOccured(this, methodCall, exception, null, -1L);
    }

    /**
     * Report to the logger all returns which have to be reported by this class
     * and which are not exceptions
     *
     * @param methodCall the method which report the value
     * @param value      the reported value
     * @return Object The object originally returned by the method called
     */
    private Object reportReturn(String methodCall, Object value) {
        spyLogDelegator.methodReturned(this, methodCall, "");
        return value;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String methodCall = "getConnection()";
        long tstart = System.currentTimeMillis();
        try {
            final Connection connection = realDataSource.getConnection();
            if (spyLogDelegator.isJdbcLoggingEnabled()) {
                return (Connection) reportReturn(methodCall,
                        new ConnectionSpy(connection, this.getRdbmsSpecifics(connection),
                                System.currentTimeMillis() - tstart, this.spyLogDelegator));
            }
            //if logging is not enable, return the real connection,
            //so that there is no useless costs
            return connection;
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {

        String methodCall = "getConnection(" + username + ", password***)";
        long tstart = System.currentTimeMillis();
        try {
            final Connection connection = realDataSource.getConnection(username, password);
            if (spyLogDelegator.isJdbcLoggingEnabled()) {
                return (Connection) reportReturn(methodCall,
                        new ConnectionSpy(connection, this.getRdbmsSpecifics(connection),
                                System.currentTimeMillis() - tstart, this.spyLogDelegator));
            }
            //if logging is not enable, return the real connection,
            //so that there is no useless costs
            return connection;
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        String methodCall = "getLoginTimeout()";
        try {
            return (Integer) reportReturn(methodCall, realDataSource.getLoginTimeout());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        String methodCall = "setLoginTimeout(" + seconds + ")";
        try {
            realDataSource.setLoginTimeout(seconds);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        String methodCall = "getLogWriter()";
        try {
            return (PrintWriter) reportReturn(methodCall, realDataSource.getLogWriter());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        String methodCall = "setLogWriter(" + out + ")";
        try {
            realDataSource.setLogWriter(out);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        String methodCall = "isWrapperFor(" + iface + ")";
        try {
            return (Boolean) reportReturn(methodCall, realDataSource.isWrapperFor(iface));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        String methodCall = "unwrap(" + iface + ")";
        try {
            return (T) reportReturn(methodCall, realDataSource.unwrap(iface));

        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    @Override
    public String getClassType() {
        return "DataSource";
    }

    @Override
    public Integer getConnectionNumber() {
        // No connection number in this case
        return null;
    }

    /**
     * Returns either the custom SQL formatting provided by the user at instantiation,
     * or the default {@code RdbmsSpecifics} for the current {@code Connection},
     * if no custom {@code RdbmsSpecifics} was provided.
     *
     * @param connection The {@code Connection} allowing to acquire its default
     *                   {@code RdbmsSpecifics}.
     * @return The {@code RdbmsSpecifics} to be used for logging SQL
     * from this {@code DataSourceSpy}.
     */
    private RdbmsSpecifics getRdbmsSpecifics(Connection connection) {
        return this.rdbmsSpecifics == null ?
                DriverSpy.getRdbmsSpecifics(connection) : this.rdbmsSpecifics;
    }

}
