package at.fh.ooe.wea.shop.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ecs.Doctype;
import org.apache.ecs.Document;
import org.apache.ecs.html.Body;
import org.apache.ecs.html.Form;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.Table;
import org.apache.ecs.xhtml.br;
import org.apache.ecs.xhtml.form;
import org.apache.ecs.xhtml.h1;
import org.apache.ecs.xhtml.h3;
import org.apache.ecs.xhtml.h4;
import org.apache.ecs.xhtml.table;
import org.apache.ecs.xhtml.td;
import org.apache.ecs.xhtml.th;
import org.apache.ecs.xhtml.tr;

import at.fh.ooe.wea.shop.ServiceLocator;
import at.fh.ooe.wea.shop.ShopCart;
import at.fh.ooe.wea.shop.warehouse.ArticleData;
import at.fh.ooe.wea.shop.warehouse.ShopDelegate;

/**
 * Servlet implementation class BookShopServlet
 */
@WebServlet("/BookShopServlet")
public class BookShopServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_CMD = "home";
	private static final String CMD = "cmd";
	private static final Logger logger = Logger.getLogger(BookShopServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BookShopServlet() {
		super();
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String cmd = DEFAULT_CMD;

		try (final Writer writer = response.getWriter()) {

			final Document document = new Document();
			document.appendTitle("WEA5 BookShop");
			document.setDoctype(new Doctype.Html401Transitional());

			final Body body = new Body();
			document.appendBody(body);
			body.setPrettyPrint(Boolean.TRUE);

			createHeader(body);
			showNavigation(body);
			cmd = Optional.ofNullable(request.getParameter(CMD)).orElse(CMD);
			logger.info("execute command '" + cmd + "'");

			final ShopDelegate delegate = ServiceLocator.getInstance().getShopDelegate();

			switch (cmd) {
			case "home":
				showEntryPage(body);
				break;
			case "browse":
				browseBooks(body, delegate);
			case "details":
				String bookId = request.getParameter("bookid");
				if (bookId != null) {
					showDetails(delegate.getArticleById(bookId), body);
				}
				break;
			case "buy":
				bookId = request.getParameter("bookid");
				ShopCart cart = getShopCard(request);
				cart.addArticleData(delegate.getArticleById(bookId));
				showCart(cart, body);
				break;
			case "cart":
				cart = getShopCard(request);
				showCart(cart, body);
				break;
			case "remove":
				cart = getShopCard(request);
				bookId = Optional.ofNullable(request.getParameter("bookid")).orElse("-1");
				cart.removeArticleWithId(bookId);
				showCart(cart, body);
				cart = getShopCard(request);
				break;

			case "checkout":
				cart = getShopCard(request);
				body.addElement(new h4("Please pay " + cart.getTotalSum() + "€"));
				body.addElement(new h4("Thank you for shopping "));
				request.getSession().invalidate();
				break;
			default:
				break;
			}

			createFooter(body);

			writer.append(document.toString());
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showCart(ShopCart cart, Body body) {
		String headline = "Your shopping cart contains the following " + cart.size() + " items: ";
		body.addElement(new h3(headline));

		table cartTable = new table();
		cartTable.setBorder(1);

		tr tableHeader = new tr();
		tableHeader.addElement(new th("Author"));
		tableHeader.addElement(new th("Title"));
		tableHeader.addElement(new th("Price"));
		cartTable.addElement(tableHeader);

		Iterator<ArticleData> iterator = cart.getIterator();
		while (iterator.hasNext()) {
			ArticleData book = iterator.next();
			tr tr = new tr();
			tr.addElement(new td(book.getAuthor()));
			tr.addElement(new td(book.getTitle()));
			tr.addElement(new td(book.getPrice().toString()));
			cartTable.addElement(tr);

			td td = new td();
			td.addElement(getInputBtnAndHiddenField("remove", "bookid", book.getId()));
			tr.addElement(td);
		}

		body.addElement(cartTable);
	}

	private ShopCart getShopCard(HttpServletRequest request) {
		final HttpSession session = request.getSession(Boolean.TRUE);
		ShopCart cart = (ShopCart) session.getAttribute("we5.cart");
		if (cart == null) {
			cart = new ShopCart();
			session.setAttribute("we5.cart", cart);
		}
		return cart;
	}

	private void showDetails(ArticleData book, Body body) {
		body.addElement("Details for book with ID : " + book.getId());

		Table detailsTable = new Table();
		detailsTable.setBorder(1);

		tr tr = new tr();
		tr.addElement(new th("Author"));
		tr.addElement(new th("Title"));
		tr.addElement(new th("Publisher"));
		tr.addElement(new th("Year"));
		tr.addElement(new th("ISBN"));
		tr.addElement(new th("Price"));
		tr.addElement(new th("Buy"));

		detailsTable.addElement(tr);

		tr = new tr();
		tr.addElement(new td(book.getAuthor()));
		tr.addElement(new td(book.getTitle()));
		tr.addElement(new td(book.getPublisher()));
		tr.addElement(new td(book.getYear()));
		tr.addElement(new td(book.getIsbn()));
		tr.addElement(new td(book.getPrice()));
		tr.addElement(new td(getInputBtnAndHiddenField("buy", "bookid", book.getId())));
		// TODO: Buy action.

		detailsTable.addElement(tr);
		body.addElement(detailsTable);
		body.addElement(new br());
	}

	private void browseBooks(Body body, ShopDelegate delegate) {
		Table table = new Table();
		table.setBorder(1);

		tr tableHeaderRow = new tr();
		tableHeaderRow.addElement(new th("Author"));
		tableHeaderRow.addElement(new th("Title"));
		tableHeaderRow.addElement(new th("Details"));
		tableHeaderRow.addElement(new th("Buy"));
		table.addElement(tableHeaderRow);

		for (ArticleData book : delegate.getAllArticles()) {
			tr bookDescRow = new tr();
			bookDescRow.addElement(new td(book.getAuthor()));
			bookDescRow.addElement(new td(book.getTitle()));
			bookDescRow.addElement(new td(getInputBtnAndHiddenField("details", "bookid", book.getId())));
			bookDescRow.addElement(new td(getInputBtnAndHiddenField("buy", "bookid", book.getId())));
			table.addElement(bookDescRow);
		}

		body.addElement(table);
	}

	private form getInputBtnAndHiddenField(String buttonValue, String hiddenFieldName, String hiddenFieldValue) {
		form form = new form();
		form.setMethod("Get");
		form.setAction("");

		// button
		Input btnBuy = new Input();
		btnBuy.setType(Input.submit);
		btnBuy.setName("cmd");
		btnBuy.setValue(buttonValue);
		form.addElement(btnBuy);

		// hidden field
		Input hidden = new Input();
		hidden.setType(Input.hidden);
		hidden.setName(hiddenFieldName);
		hidden.setValue(hiddenFieldValue);
		form.addElement(hidden);

		return form;
	}

	private void showEntryPage(Body body) {
		// TODO Auto-generated method stub

	}

	private void createHeader(Body body) {
		body.addElement(new h1("WEA5 BookShop"));

	}

	private void showNavigation(Body body) {
		final Form form = new Form();
		body.addElement(form);
		form.setMethod("get");

		final Input buttonHome = new Input();
		buttonHome.setType("submit");
		buttonHome.setName("cmd");
		buttonHome.setValue("home");

		final Input buttonBrowse = new Input();
		buttonBrowse.setType("submit");
		buttonBrowse.setName("cmd");
		buttonBrowse.setValue("browse");

		final Input buttonCard = new Input();
		buttonCard.setType("submit");
		buttonCard.setName("cmd");
		buttonCard.setValue("cart");

		final Input buttonCheckout = new Input();
		buttonCheckout.setType("submit");
		buttonCheckout.setName("cmd");
		buttonCheckout.setValue("checkout");

		form.addElement(buttonHome);
		form.addElement(buttonBrowse);
		form.addElement(buttonCard);
		form.addElement(buttonCheckout);

	}

	private void createFooter(Body body) {
		body.addElement(new br());
		body.addElement("Version 2015, page generate at: " + new Date().toString());
	}

}
