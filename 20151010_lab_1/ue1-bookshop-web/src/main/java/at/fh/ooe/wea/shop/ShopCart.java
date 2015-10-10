package at.fh.ooe.wea.shop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import at.fh.ooe.wea.shop.warehouse.ArticleData;

public class ShopCart {

	private List<ArticleData> articles = new ArrayList<>();
	private static Logger logger = Logger.getLogger(ShopCart.class.getName());

	public ShopCart() {
	}

	public void addArticleData(ArticleData article) {
		logger.info("Added article: " + article.toString());
		articles.add(article);
	}

	public int size() {
		return articles.size();
	}

	public Iterator<ArticleData> getIterator() {
		return articles.iterator();
	}

	public Double getTotalSum() {
		double sum = 0.0;
		for (ArticleData data : articles) {
			sum += Double.valueOf(data.getPrice());
		}
		return sum;
	}

	public boolean remove(ArticleData article) {
		logger.info("Remove article: " + article.toString());
		return articles.remove(article);
	}

	public boolean removeArticleWithId(String id) {
		logger.info("Remove article by id: " + id);
		for (ArticleData article : articles) {
			if (article.getId().equals(id)) {
				return remove(article);
			}
		}
		return Boolean.FALSE;
	}
}
