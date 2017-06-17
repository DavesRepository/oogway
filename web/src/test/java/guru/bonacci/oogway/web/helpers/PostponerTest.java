package guru.bonacci.oogway.web.helpers;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import guru.bonacci.oogway.web.WebTestConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebTestConfig.class)
public class PostponerTest {

	@Autowired
	Postponer postponer;
	
	@Test
	public void shouldGiveOneOfTheExpectedAnswers() {
		List<String> answers = asList("answer one", "answer two", "answer three");
		for (int i=0; i<10; i++) 
			assertThat(answers, hasItem(postponer.saySomething()));
	}
}