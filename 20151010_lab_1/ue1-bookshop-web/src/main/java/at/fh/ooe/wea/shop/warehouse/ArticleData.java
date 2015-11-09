package at.fh.ooe.wea.shop.warehouse;

public class ArticleData {

	private String id;
	private String isbn;
	private String author;
	private String title;
	private String publisher;
	private String year;
	private String price;

	public ArticleData() {
		super();
	}

	public ArticleData(String id, String isbn, String author, String title, String publisher, String year,
			String price) {
		super();
		this.id = id;
		this.isbn = isbn;
		this.author = author;
		this.title = title;
		this.publisher = publisher;
		this.year = year;
		this.price = price;
	}

	// ##################################################
	// Getter and Setter
	// ##################################################
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "ArticleData [id=" + id + ", isbn=" + isbn + ", author=" + author + ", title=" + title + ", publisher="
				+ publisher + ", year=" + year + ", price=" + price + "]";
	}

}
