package br.com.biblioteca.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static String url;
    private static String user;
    private static String password;

 static {
     Properties props = new Properties();

     try {
         props.load(ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties"));
     } catch (IOException e) {
         throw new RuntimeException("Erro ao ler db.properties" , e);
     }

     url = props.getProperty("url");
     user = props.getProperty("user");
     password = props.getProperty("password");

 }

    public static Connection getConnection() throws SQLException{
     return DriverManager.getConnection(url, user, password);
    }
}