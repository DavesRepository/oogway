package guru.bonacci.oogway.oracle;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = NONE)
public class GemRepositoryTest {

	@Autowired
	GemRepository repo;
	
	@Before
	public void setup() {
		repo.deleteAll();
	}

	@Test
	public void shouldSaveAUnique() {
		assertThat(repo.count(), is(equalTo(0L)));

		repo.saveTheNewOnly(singletonList(new Gem("a")));
		assertThat(repo.count(), is(equalTo(1L)));
	}

	@Test
	public void shouldNotSaveAnExisting() {
		repo.saveTheNewOnly(singletonList(new Gem("a")));
		repo.saveTheNewOnly(singletonList(new Gem("a")));
		assertThat(repo.count(), is(equalTo(1L)));
	}

	@Test
	public void shouldSaveTheNewOnly() {
		repo.saveTheNewOnly(singletonList(new Gem("a")));
		repo.saveTheNewOnly(asList(new Gem("a"), new Gem("b")));
		assertThat(repo.count(), is(equalTo(2L)));
	}

	@Test
	public void shouldFindSimilarGem() {
		Gem gem = new Gem("how are you I am fine");
		repo.save(gem);
		
		Optional<Gem> result = repo.consultTheOracle("hello how are you");
		assertThat(gem, is(equalTo(result.get())));
	}

	@Test
	public void shouldFindSimilarGemMultipleTimes() {
		Gem gem1 = new Gem("how are you I am fine");
		Gem gem2 = new Gem("how are you I am not fine");
		repo.save(asList(gem1, gem2));
		
		Set<Gem> results = new HashSet<>();
		for (int i=0; i<10; i++) 
			results.add(repo.consultTheOracle("hello how are you").get());

		assertThat(results.size(), greaterThan(1));
	}

	@Test
	public void shouldNotFindDifferentGem() {
		Gem gem = new Gem("how are you I am fine");
		repo.save(gem);
		
		Optional<Gem> result = repo.consultTheOracle("something completely different");
		assertThat(true, is(not(result.isPresent())));
	}
}
