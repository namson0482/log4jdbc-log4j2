package net.sf.log4jdbc.log;

import net.sf.log4jdbc.TestAncestor;
import net.sf.log4jdbc.sql.jdbcapi.DriverSpy;
import net.sf.log4jdbc.sql.jdbcapi.MockDriverUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.sql.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResultSetPrintTest extends TestAncestor {
    private final static Logger log = LogManager.getLogger(ResultSetPrintTest.class.getName());

    @Override
    protected Logger getLogger() {
        return log;
    }


    /**
     * Regression test for
     * <a href='http://code.google.com/p/log4jdbc-log4j2/issues/detail?id=9'>
     * issue #9</a>.
     *
     * @throws SQLException
     */
    @Test
    public void testEmptyResultSet() throws SQLException, ClassNotFoundException {
        //with JDBC 3 we need to load the DriverSpy manually
        Class.forName(DriverSpy.class.getName());

        MockDriverUtils mock = new MockDriverUtils();
        PreparedStatement mockPrep = mock(PreparedStatement.class);
        ResultSet mockResu = mock(ResultSet.class);
        String query = "SELECT * FROM Test";

        when(mock.getMockConnection().prepareStatement(query))
                .thenReturn(mockPrep);
        when(mockPrep.executeQuery()).thenReturn(mockResu);
        when(mockResu.getMetaData()).thenReturn(null);

        Connection conn = DriverManager.getConnection("jdbc:log4" + MockDriverUtils.MOCKURL);
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet resu = ps.executeQuery();

        when(mockResu.next()).thenReturn(true);
        resu.next();
        resu.getString(1);

        when(mockResu.next()).thenReturn(false);
        //here, the next should trigger the print
        resu.next();

        mock.deregister();
        removeLogFile();
    }

    /**
     * Regression test for
     * <a href='http://code.google.com/p/log4jdbc-log4j2/issues/detail?id=9'>
     * issue #9</a>.
     *
     * @throws SQLException
     */
    @Test
    public void testResultSetClosedWhenEmpty() throws SQLException, ClassNotFoundException {
        //with JDBC 3 we need to load the DriverSpy manually
        Class.forName(DriverSpy.class.getName());

        MockDriverUtils mock = new MockDriverUtils();
        PreparedStatement mockPrep = mock(PreparedStatement.class);
        ResultSet mockResu = mock(ResultSet.class);
        String query = "SELECT * FROM Test";

        when(mock.getMockConnection().prepareStatement(query))
                .thenReturn(mockPrep);
        when(mockPrep.executeQuery()).thenReturn(mockResu);

        Connection conn = DriverManager.getConnection("jdbc:log4" + MockDriverUtils.MOCKURL);
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet resu = ps.executeQuery();

        when(mockResu.next()).thenReturn(false);
        when(mockResu.getMetaData()).thenThrow(
                new SQLException("FYI, there is no isClosed method in JDBC3, so the expected " +
                        "behavior is to assume that the ResultSet is closed if a SQLException " +
                        "is thrown."));

        resu.next();

        mock.deregister();
        removeLogFile();
    }

    /**
     * Regression test for
     * <a href='http://code.google.com/p/log4jdbc-log4j2/issues/detail?id=14'>
     * issue #14</a>.
     *
     * @throws SQLException
     */
    @Test
    public void testTableColumnNameCollector() throws SQLException {
        MockDriverUtils mock = new MockDriverUtils();
        PreparedStatement mockPrep = mock(PreparedStatement.class);
        ResultSet mockResu = mock(ResultSet.class);
        ResultSetMetaData mockRsmd = mock(ResultSetMetaData.class);

        String query = "SELECT * FROM Test";

        when(mock.getMockConnection().prepareStatement(query))
                .thenReturn(mockPrep);
        when(mockPrep.executeQuery()).thenReturn(mockResu);

        when(mockRsmd.getColumnCount()).thenReturn(1);
        when(mockRsmd.getColumnName(1)).thenReturn("column 1");
        when(mockRsmd.getColumnLabel(1)).thenReturn("column 1 renamed");
        when(mockRsmd.getTableName(1)).thenReturn("mytable");
        when(mockResu.getMetaData()).thenReturn(mockRsmd);

        Connection conn = DriverManager.getConnection("jdbc:log4" + MockDriverUtils.MOCKURL);
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet resu = ps.executeQuery();

        when(mockResu.next()).thenReturn(true);
        resu.next();
        //call as usual
        resu.getString("column 1");
        //following the fix, no exception should be thrown here
        resu.getString("mytable.column 1");
        //test that an exception is correctly thrown with incorrect table name
        try {
            resu.getString("myFaketable.column 1");
            //exception should have been thrown, if we reach that point, test failed
            throw new AssertionError("an exception should have been thrown");
        } catch (RuntimeException e) {
            //test passed
        }

        mock.deregister();
        removeLogFile();
    }
}
