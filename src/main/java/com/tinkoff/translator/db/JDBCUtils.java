package com.tinkoff.translator.db;
import com.tinkoff.translator.model.Request;
import com.tinkoff.translator.model.Word;

import java.sql.*;


public class JDBCUtils {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:file:D:/translator/translator/db";
    static final String USER = "sa";
    static final String PASSWORD = "password";

    private static final String createTables = "drop table if exists WORDS;drop table if exists REQUESTS;\n" +
            "                                   CREATE TABLE REQUESTS (ID LONG PRIMARY KEY AUTO_INCREMENT,\n" +
            "                                   INPUT VARCHAR,\n" +
            "                                   OUTPUT VARCHAR, DATE DATETIME,LANGUAGES VARCHAR, IP VARCHAR);\n" +
            "                                   CREATE TABLE WORDS (ID LONG, WORD VARCHAR, WORD_TRANSLATED VARCHAR,PRIMARY KEY(ID,WORD),\n" +
            "                                   FOREIGN KEY (ID) REFERENCES REQUESTS(ID));";

    public static Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
    public static void createTables(){
        try(Connection connection = JDBCUtils.getConnection()){
            Statement statement = connection.createStatement();

            statement.execute(createTables);
            connection.close();
            statement.close();
        }catch (SQLException e) {
            // print SQL exception information
            JDBCUtils.printSQLException(e);

        }
    }
    public static void updateRequestQuery(Request request) throws SQLException {
        String sql = "UPDATE REQUESTS SET (OUTPUT) = (?) WHERE (ID) = (?);";
        try(Connection connection = JDBCUtils.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, request.getOutputString());
            statement.setLong(2, request.getId());

            statement.execute();
            connection.close();
            statement.close();
        }

    }
    public static void insertWordsQuery(Word word) throws SQLException {
        String sql = "INSERT INTO WORDS(ID,WORD,WORD_TRANSLATED) VALUES(?,?,?);";
        try(Connection connection = JDBCUtils.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setLong(1, word.getId());
            statement.setString(2, word.getWord());
            statement.setString(3, word.getWordTranslated());

            statement.execute();
            connection.close();
            statement.close();
        }
    }
    public static void insertRequestQuery(Request request){
        String sql = "INSERT INTO REQUESTS (INPUT, OUTPUT, DATE, LANGUAGES, IP) values (?,?,?,?,?);";
        try(Connection connection = JDBCUtils.getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1,request.getInputString());
            statement.setString(2,request.getOutputString());
            statement.setDate(3, request.getDate());
            statement.setString(4,request.getLanguages());
            statement.setString(5,request.getIp());

            statement.execute();

        //TODO:return ID

            long key = -1L;
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                key = rs.getLong(1);
            }
            request.setId(key);
            System.out.println(request.getId());
            connection.close();
            statement.close();
        }catch (SQLException e) {
            // print SQL exception information
            JDBCUtils.printSQLException(e);

        }
    }
    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
