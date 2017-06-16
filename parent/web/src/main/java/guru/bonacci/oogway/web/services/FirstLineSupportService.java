package guru.bonacci.oogway.web.services;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import guru.bonacci.oogway.es.Gem;
import guru.bonacci.oogway.es.GemRepository;
import guru.bonacci.oogway.web.helpers.Postponer;

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

	@Autowired
	private GemRepository gemRepository;

	@Autowired
	private Postponer postponer;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${spring.activemq.queue.name}")
	private String queue;

	
	public String enquire(String q) {
		if (isEmpty(q))
			return "No question no answer..";

		// Send a message to the world...
		jmsTemplate.send(queue, session -> session.createTextMessage(q));

		// Consult the oracle..
		Optional<Gem> gem = gemRepository.searchForOne(q);
		return gem.map(Gem::getEssence).orElse(postponer.saySomething());
	}
}