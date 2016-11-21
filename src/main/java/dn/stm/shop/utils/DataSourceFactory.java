/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dn.stm.shop.utils;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.util.Properties;
import javax.sql.DataSource;

/**
 *
 * @author Kalinchuk
 * 
 * 
 */
public class DataSourceFactory {

    /**
     *
     * @return DataSource object that represents stm data base
     */
    public static DataSource getMySQLDataSource() {
        Properties props = AppPropertiesHandler.getAppProperties();
        MysqlDataSource mysqlDS = new MysqlDataSource();
        mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
        mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
        mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
        mysqlDS.setCharacterEncoding("UTF-8");
        mysqlDS.setEncoding("UTF-8");
        return mysqlDS;
    }
}
