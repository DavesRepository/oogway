package guru.bonacci.oogway.jms;

import java.util.Arrays;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import guru.bonacci.oogway.sannyas.SmokeSignalController;

@Configuration
@ComponentScan(basePackages="bonacci.oogway.jms")
public class JMSConfig {

	private static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";
	
	private static final String QUEUE = "winnetou";
	
	@Autowired
	private SmokeSignalController messageReceiver;
	
	@Bean
	public ConnectionFactory connectionFactory(){
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
		connectionFactory.setTrustedPackages(Arrays.asList("guru.bonacci.oogway.jms"));
		return connectionFactory;
	}

	@Bean
	public ConnectionFactory cachingConnectionFactory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setTargetConnectionFactory(connectionFactory());
		connectionFactory.setSessionCacheSize(10);
		return connectionFactory;
	}
	
	/*
	 * Message listener container, used for invoking messageReceiver.onMessage on message reception.
	 */
	@Bean
	public MessageListenerContainer getContainer(){
		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setDestinationName(QUEUE);
		container.setMessageListener(messageReceiver);
		return container;
	}

	/*
	 * Used here for Sending Messages.
	 */
	@Bean 
	public JmsTemplate jmsTemplate(){
		JmsTemplate template = new JmsTemplate();
		template.setConnectionFactory(connectionFactory());
		template.setDefaultDestinationName(QUEUE);
		return template;
	}
	
	
	@Bean 
	MessageConverter converter(){
		return new SimpleMessageConverter();
	}
}
