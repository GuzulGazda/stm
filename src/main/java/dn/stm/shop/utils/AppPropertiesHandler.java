package dn.stm.shop.utils;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kalinchuk
 *
 * A subclass of the java.util.Properties class that must be initialized from a
 * file on disk. This class implements the singleton pattern.
 */
public class AppPropertiesHandler extends Properties {

    private static final Logger LOGGER = Logger.getLogger(AppPropertiesHandler.class.getName());

    // instance of AppPropertiesHandler object
    private static AppPropertiesHandler instance = null;
    // mutex for lazy initialization
    private static final Object mutex = new Object();
    // path to the .properties file
    private final String PROPERTIES_PATH = "app.properties";

    // properties fields
    public static final String MYSQL_DB_DRIVER_CLASS = "MYSQL_DB_DRIVER_CLASS";
    public static final String MYSQL_DB_URL = "MYSQL_DB_URL";
    public static final String MYSQL_DB_USERNAME = "MYSQL_DB_USERNAME";
    public static final String MYSQL_DB_PASSWORD = "MYSQL_DB_PASSWORD";
    public static final String EXCEL_FILE_NAME = "EXCEL_FILE_NAME";
    private static final String SITE_ADMIN_LOGIN = "SITE_ADMIN_LOGIN";
    private static final String SITE_ADMIN_PASSWORD = "SITE_ADMIN_PASSWORD";

    private AppPropertiesHandler() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(PROPERTIES_PATH)) {
            this.load(inputStream);
            LOGGER.log(Level.INFO, "Application properties is loaded from {0}", PROPERTIES_PATH);
        } catch (Exception ex) {
            loadDefaultProperties();
            LOGGER.log(Level.SEVERE, "Loading properties error: {0}", ex.getMessage());
            LOGGER.log(Level.INFO, "Using default properties");
        }
    }

    /**
     *
     * @return AppPropertiesHandler object
     */
    public static AppPropertiesHandler getAppProperties() {
        synchronized (mutex) {
            if (instance == null) {
                instance = new AppPropertiesHandler();
            }
        }
        return instance;
    }

    private void loadDefaultProperties() {
        // MySQL connection properties
        setProperty(MYSQL_DB_DRIVER_CLASS, "com.mysql.jdbc.Driver");
        setProperty(MYSQL_DB_URL, "jdbc:mysql://localhost:3306/stm");
        setProperty(MYSQL_DB_USERNAME, "tomcat");
        setProperty(MYSQL_DB_PASSWORD, "tomcat");
        // excel file
        setProperty(EXCEL_FILE_NAME, "19102016.XLSX");
        // Site Administrator credentials
        setProperty(SITE_ADMIN_LOGIN, "1");
        setProperty(SITE_ADMIN_PASSWORD, "1");
    }
}
