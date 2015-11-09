package at.fh.ooe.wea.shop;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import at.fh.ooe.wea.shop.warehouse.DummyShopDelegate;

@WebListener("SetupServletContextListener")
public class SetupServletContextListener implements ServletContextListener {

	public SetupServletContextListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();

		String dsn = servletContext.getInitParameter("DB_DSN");
		String user = servletContext.getInitParameter("DB_USER");
		String password = servletContext.getInitParameter("DB_PASSWORD");
		String delegateClass = servletContext.getInitParameter("Shop_DELEGATE");

		ServiceLocator.getInstance().init(dsn, user, password, delegateClass);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
