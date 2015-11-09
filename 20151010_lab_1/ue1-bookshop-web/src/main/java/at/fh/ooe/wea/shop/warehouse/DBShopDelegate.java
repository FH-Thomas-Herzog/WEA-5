package at.fh.ooe.wea.shop.warehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.fh.ooe.wea.shop.ServiceLocator;

/**
 * This delegate provides test data from the database.
 */
public class DBShopDelegate implements ShopDelegate {

	private static Logger logger = Logger.getLogger(DBShopDelegate.class.getName());

	/**
	 * Returns all articles.
	 */
	public List<ArticleData> getAllArticles() {
		Connection connection = null;
		try {
			connection = ServiceLocator.getInstance().getDBConnection();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString());
		}

		List<ArticleData> articles = new ArrayList<ArticleData>();
		try {
			// Get data from DB
			Statement statement = connection.createStatement();
			// Security issue: SQL injection
			ResultSet resultSet = statement.executeQuery("SELECT * FROM Books");
			while (resultSet.next()) {
				ArticleData a = new ArticleData(resultSet.getString("ID"), resultSet.getString("ISBN"),
						resultSet.getString("Author"), resultSet.getString("Title"), resultSet.getString("Publisher"),
						resultSet.getString("PubYear"), resultSet.getString("Price"));
				articles.add(a);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.toString());
		} finally {
			try {
				connection.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.toString());
			}
		}
		return articles;
	}

	/**
	 * Get specific article by id.
	 */
	public ArticleData getArticleById(String id) {
		return getArticleById_Unsecure(id);
		// return getArticleById_Secure(id);
	}

	/**
	 * This implementation has a problem with SQL injections try this url:
	 * http://localhost:8080/Ue1_BookShop/BookShopXMLServlet?id=1 or '1'='1' and
	 * you will see the all books in the console log instead of book with id 1
	 **/
	public ArticleData getArticleById_Unsecure(String id) {
		if (id == null)
			return null;
		Connection connection = null;
		ArticleData a = null;
		try {
			connection = ServiceLocator.getInstance().getDBConnection();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString());
		}

		try {
			logger.log(Level.INFO, "id : " + id);

			// Get data from DB
			Statement statement = connection.createStatement();
			// Security issue: SQL injection
			// try this url:
			// http://localhost:8080/Ue1_BookShop/BookShopXMLServlet?id=1 or
			// '1'='1'
			// and you will see all books in the console log instead of book
			// with id 1

			logger.log(Level.INFO, "START: Watch out for SQL INJECTION");
			String qry = "SELECT * FROM Books WHERE ID=" + id;

			System.out.println("\"" + qry + "\"");

			logger.log(Level.INFO, "qry : " + qry);
			ResultSet resultSet = statement.executeQuery(qry);
			while (resultSet.next()) {
				a = new ArticleData(resultSet.getString("ID"), resultSet.getString("ISBN"),
						resultSet.getString("Author"), resultSet.getString("Title"), resultSet.getString("Publisher"),
						resultSet.getString("PubYear"), resultSet.getString("Price"));
				logger.log(Level.INFO, "book : " + a);
			}
			logger.log(Level.INFO, "END: Watch out for SQL INJECTION");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.toString());
		} finally {
			try {
				connection.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.toString());
			}
		}
		return a;
	}

	public ArticleData getArticleById_Secure(String id) {
		if (id == null)
			return null;
		Connection connection = null;
		ArticleData a = null;
		try {
			connection = ServiceLocator.getInstance().getDBConnection();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString());
		}

		try {
			logger.log(Level.INFO, "id : " + id);
			// Get data from DB
			String selectStatement = "SELECT * FROM Books WHERE ID = ? ";
			PreparedStatement statement = connection.prepareStatement(selectStatement);
			statement.setString(1, id);
			logger.log(Level.INFO, "START: Watch out for SQL INJECTION");
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				a = new ArticleData(resultSet.getString("ID"), resultSet.getString("ISBN"),
						resultSet.getString("Author"), resultSet.getString("Title"), resultSet.getString("Publisher"),
						resultSet.getString("PubYear"), resultSet.getString("Price"));
				logger.log(Level.INFO, "book : " + a);
			}
			logger.log(Level.INFO, "END: Watch out for SQL INJECTION");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.toString());
		} finally {
			try {
				connection.close();
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.toString());
			}
		}
		return a;
	}
}
