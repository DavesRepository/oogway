package bonacci.oogway.services.sannyas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import bonacci.oogway.es.ESConfig;
import bonacci.oogway.jms.JMSConfig;
import bonacci.oogway.sannyas.SannyasConfiguration;

@EnableAutoConfiguration
@EnableDiscoveryClient
@Import({JMSConfig.class, SannyasConfiguration.class, ESConfig.class})
public class SannyasServer {

	public static void main(String[] args) {
		System.setProperty("spring.config.name", "sannyas-server");
		SpringApplication.run(SannyasServer.class, args);
	}
}
