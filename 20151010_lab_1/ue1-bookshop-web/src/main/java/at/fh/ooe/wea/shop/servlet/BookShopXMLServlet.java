package at.fh.ooe.wea.shop.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.xml.XML;
import org.apache.ecs.xml.XMLDocument;

import at.fh.ooe.wea.shop.ServiceLocator;
import at.fh.ooe.wea.shop.warehouse.ArticleData;

@WebServlet("BookShopXMLServlet")
public class BookShopXMLServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public BookShopXMLServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("id");
		ArticleData article = ServiceLocator.getInstance().getShopDelegate().getArticleById(id);

		XMLDocument doc = new XMLDocument();
		XML xmlArticle = new XML("article");
		if (article != null) {
			XML xmlIsbn = new XML("isbn").addElement(article.getIsbn());
			xmlArticle.addElement(xmlIsbn);

			XML xmlAuthor = new XML("author").addElement(article.getAuthor());
			xmlArticle.addElement(xmlAuthor);

			XML xmlPrice = new XML("price").addElement(article.getPrice());
			xmlArticle.addElement(xmlPrice);

			XML xmlPublisher = new XML("publisher").addElement(article.getPublisher());
			xmlArticle.addElement(xmlPublisher);

			XML xmlTitle = new XML("title").addElement(article.getTitle());
			xmlArticle.addElement(xmlTitle);

			XML xmlYear = new XML("year").addElement(article.getYear());
			xmlArticle.addElement(xmlYear);
		}
		doc.addElement(xmlArticle);

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		doc.output(response.getWriter());
	}
}
