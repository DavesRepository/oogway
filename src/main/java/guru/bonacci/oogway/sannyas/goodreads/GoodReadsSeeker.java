package guru.bonacci.oogway.sannyas.goodreads;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import guru.bonacci.oogway.sannyas.PageCache;
import guru.bonacci.oogway.sannyas.Sannyasin;
import guru.bonacci.oogway.sannyas.filters.LengthFilter;
import guru.bonacci.oogway.sannyas.steps.KeyPhraser;
import guru.bonacci.oogway.util.RandomUtils;

@Component
public class GoodReadsSeeker implements Sannyasin {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String URL = "http://www.goodreads.com/quotes/tag/";

	@Autowired
	private KeyPhraser keyPhraser;

	@Autowired
	private LengthFilter lengthFilter;

	@Resource(name = "goodReadsPageCache")
	public PageCache pageCache;

	@Override
	public List<Function<String, String>> preproces() {
		return Arrays.asList(keyPhraser::apply);
	}

	@Override
	public List<Predicate<String>> postfilters() {
		return Arrays.asList(lengthFilter);
	}

	@Override
	public List<String> seek(String tagsAsString) {
		String[] tags = StringUtils.split(tagsAsString);
		return Arrays.stream(tags)
					.map(this::determinePagedURL)
					.map(this::consult)
					.flatMap(Elements::stream)
					.map(this::cleanDiv)
					.map(Element::text)
					.map(this::strip)
					.collect(Collectors.toList());
	}

	public String determinePagedURL(String searchStr) {
		String searchURL = URL + searchStr;
		Integer nrOfPages = pageCache.getNrOfPages(searchURL);
		return searchURL + "?page=" + RandomUtils.fromOneInclTo(nrOfPages);
	}

	public Elements consult(String searchURL) {
		try {
			Document doc = Jsoup.connect(searchURL).get();
			return doc.select("div.quoteText");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return new Elements();
	}

	private Element cleanDiv(Element el) {
		for (Element e : el.children()) 
			e.remove();
		return el;
	}

	private String strip(String str) {
		return str.substring(str.indexOf("“") + 1, str.lastIndexOf("”"));
	}
}
