package at.fh.ooe.wea.shop.warehouse;

import java.util.List;

public interface ShopDelegate {

	List<ArticleData> getAllArticles();
	
	ArticleData getArticleById(String id);
	
	
}
