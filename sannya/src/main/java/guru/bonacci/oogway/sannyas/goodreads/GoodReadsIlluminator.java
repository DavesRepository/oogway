package guru.bonacci.oogway.sannyas.goodreads;


import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.jsoup.Jsoup.connect;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import guru.bonacci.oogway.sannyas.general.PageCache;
import guru.bonacci.oogway.sannyas.general.WebIlluminator;

/**
 * Most popular quote:
 * “Don't cry because it's over, smile because it happened.” ― Dr. Seuss
 */
@Component
public class GoodReadsIlluminator extends WebIlluminator implements PageCache {

	private final Logger logger = getLogger(this.getClass());

	@Value("${web.url.goodreads:http://www.goodreads.com/quotes/tag/}")
	private String url;

	@Override
	public String determineURL(String searchStr) {
		String searchURL = url + searchStr;
		Integer nrOfPages = getNrOfPages(searchURL);
		return searchURL + "?page=" + nextInt(1, nrOfPages + 1);
	}

	@Override
    public Integer getNrOfPages(String searchURL) {
		int pageNr = 1;
		try {
			Document doc = connect(searchURL).get();
			Elements elements = doc.select("span.gap");
			pageNr = Integer.valueOf(elements.first().nextElementSibling().nextElementSibling().text());
		} catch (Exception e) { 
			// Taking the easy way, catching all exceptions.
			// No results or one result: page 1
			// Not enough results for a gap between the pagination numbers: page 1
			// More than two results: page x
			logger.error(e.getMessage());
		}
		return pageNr;
    }

	@Override
	public Elements consultWeb(String searchURL) {
		try {
			logger.info("firing request " + searchURL);
			Document doc = connect(searchURL).get();
			return doc.select("div.quoteText");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return new Elements();
	}

	@Override
	public Element procesElement(Element el) {
		for (Element e : el.children()) 
			e.remove();
		return el;
	}

	@Override
	public String procesText(String str) {
		return str.substring(str.indexOf("“") + 1, str.lastIndexOf("”"));
	}
}
