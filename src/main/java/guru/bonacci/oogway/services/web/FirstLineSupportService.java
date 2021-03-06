package guru.bonacci.oogway.services.web;

import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import guru.bonacci.oogway.es.Gem;
import guru.bonacci.oogway.es.OracleRepository;
import guru.bonacci.oogway.jms.SmokeSignal;

/**
 * Tier I is the initial support level responsible for basic customer issues. It
 * is synonymous with first-line support, level 1 support, front-end support,
 * support line 1, and various other headings denoting basic level technical
 * support functions. The first job of a Tier I specialist is to gather the
 * customer’s information and to determine the customer’s issue by analyzing the
 * symptoms and figuring out the underlying problem. When analyzing the
 * symptoms, it is important for the technician to identify what the customer is
 * trying to accomplish so that time is not wasted on "attempting to solve a
 * symptom instead of a problem."
 */
@Service
public class FirstLineSupportService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OracleRepository repository;

	@Autowired
	private JmsTemplate jmsTemplate;

	public String index(String q) {
		if (StringUtils.isEmpty(q))
			return "No question no answer..";

		// Send a message to the world...
		jmsTemplate.send(session -> session.createObjectMessage(new SmokeSignal(q)));

		// Consult the oracle..
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery(Gem.ESSENCE, q))
				.build();
		List<Gem> result = repository.search(searchQuery).getContent();
		result.stream().map(Gem::getEssence).forEach(logger::debug);

		return result.size() > 0 ? result.get(RandomUtils.nextInt(0, result.size())).getEssence()
				: "I'm speechless, are you sure?";
	}
}
