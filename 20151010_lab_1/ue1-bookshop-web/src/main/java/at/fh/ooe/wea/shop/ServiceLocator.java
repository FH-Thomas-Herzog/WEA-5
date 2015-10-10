package at.fh.ooe.wea.shop;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import at.fh.ooe.wea.shop.warehouse.ShopDelegate;

/**
 * The {@link ServiceLocator} class provides access to all relevant services
 * (e.g. database) and it offers the appropriate delegate object. The
 * {@link ServiceLocator} is based on the singleton pattern.
 */
public class ServiceLocator {

	private static Logger logger = Logger.getLogger(ServiceLocator.class.getName());

	private static final String DERBY_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

	private static ServiceLocator instance;
	private boolean initialized;

	private String dbDsn;
	private String dbUsername;
	private String dbPassword;
	private String delegateClass;

	// Intentionally private: is a singleton.
	private ServiceLocator() {
		initialized = false;
	}

	public static synchronized ServiceLocator getInstance() {
		if (instance == null) {
			instance = new ServiceLocator();
		}
		return instance;
	}

	public void init(String dbDsn, String dbUsername, String dbPassword, String delegateClass) {
		this.dbDsn = dbDsn;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.delegateClass = delegateClass;

		// Register drivers...
		try {
			registerDriverAndInitialize();
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "getDBConnection: " + e);
		}
	}

	public Connection getDBConnection() throws ServletException, ClassNotFoundException {

		if (!initialized) {
			registerDriverAndInitialize();
		}

		try {
			logger.log(Level.INFO, "connecting to " + dbDsn);
			return DriverManager.getConnection(dbDsn, dbUsername, dbPassword);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "getDBConnection: " + e);
			throw new ServletException(e);
		}
	}

	private void registerDriverAndInitialize() throws ClassNotFoundException {
		Class.forName(DERBY_DRIVER);
		logger.log(Level.INFO, "getDBConnection: loading driver class ... ");

		this.initialized = true;
	}

	/**
	 * Get the delegate object that has been defined in the web.xml file
	 * (deployment descriptor). The delegate object is created with the Java
	 * reflection mechanism.
	 */
	@SuppressWarnings("unchecked")
	public ShopDelegate getShopDelegate() throws ServletException {
		Class<ShopDelegate> cls;
		try {
			cls = (Class<ShopDelegate>) Class.forName(this.delegateClass);
			Constructor<ShopDelegate>[] c = (Constructor<ShopDelegate>[]) cls.getConstructors();
			Object[] arguments = {};
			ShopDelegate delegate = (ShopDelegate) c[0].newInstance(arguments);
			return delegate;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "ServiceLocator: " + e);
		}
		return null;
	}
}
