package guru.bonacci.oogway.sannyas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import guru.bonacci.oogway.es.Jewel;
import guru.bonacci.oogway.es.MyRepository;
import guru.bonacci.oogway.sannyas.filters.profanity.ProfanityFilter;

/**
 * A manager alone cannot perform all the tasks assigned to him. In order to
 * meet the targets, the manager should delegate authority. Delegation of
 * Authority means division of authority and powers downwards to the
 * subordinate. Delegation is about entrusting someone else to do parts of your
 * job. Delegation of authority can be defined as subdivision and sub-allocation
 * of powers to the subordinates in order to achieve effective results.
 */
@Component
public class MyManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private MyRepository repository;

	@Autowired
	private ProfanityFilter profanityFilter;

    @PostConstruct
    private void fill() {
    	repository.deleteAll();
    }

	public void delegate(String input) {
		logger.info("About to analyzer input: '" + input + "'");

		// We take a random Sannyasin
		List<Sannyasin> sannyas = new ArrayList<>(applicationContext.getBeansOfType(Sannyasin.class).values());
		Sannyasin sannya = sannyas.get(RandomUtils.nextInt(0, sannyas.size()));

		// Seeking consists of four steps
		// pre-proces the input
		Function<String,String> preprocessing = sannya.preprocessingSteps().stream()
																		   .reduce(Function.identity(), Function::andThen);
		String preprocessedInput = preprocessing.apply(input);
		logger.info(sannya.getClass() + "- Preprocessed input: '" + preprocessedInput + "'");

		// acquire wisdom
		List<String> found = sannya.seek(preprocessedInput);

		// filter the wisdom..
		Predicate<String> postfiltering = sannya.postfilteringStep().stream()
															  		.reduce(p -> true, Predicate::and);
		Stream<Jewel> postfiltered = found.stream()
			 .filter(postfiltering)
			 .filter(profanityFilter) // always execute the profanity-filter
			 .peek(f -> logger.info("Indexing wisdom: '" + f + "'")) 
			 .map(Jewel::new);

		// ..and bulk persist it
		repository.save(postfiltered::iterator);
	}
}
