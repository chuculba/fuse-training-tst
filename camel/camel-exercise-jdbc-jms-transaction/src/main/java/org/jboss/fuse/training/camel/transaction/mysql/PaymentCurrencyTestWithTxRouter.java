package org.jboss.fuse.training.camel.transaction.mysql;

import com.fusesource.training.payment.Payment;
import com.fusesource.training.payment.Payments;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

public class PaymentCurrencyTestWithTxRouter
    extends SpringRouteBuilder
        implements InitializingBean, DisposableBean {
	
    private static final Logger LOG = LoggerFactory.getLogger(PaymentCurrencyTestWithTxRouter.class);

	private String incomingQueue;
	private String outgoingQueue; 
	
	String sourceUri;
	String targetUri;

    private JdbcTemplate jdbcTemplate;

	@Override
	public void configure() throws Exception {
        SpringTransactionPolicy txnPropagationRequired = new SpringTransactionPolicy(
            lookup( "POLICY_PROPAGATION_REQUIRED", TransactionTemplate.class )
        );
        errorHandler(
            transactionErrorHandler( txnPropagationRequired ).redeliverDelay( 5000 )
        );

        IdempotentRepository<String> idempotentRepository = lookup( "processedMessagesRepository", IdempotentRepository.class);
		
        // Initialize the Jaxb context so that Camel can marshal/unmarshal
        JaxbDataFormat jaxb = new JaxbDataFormat( "com.fusesource.training.payment" );
        jaxb.setPrettyPrint( true );
        
		// From the incoming queue
		from( sourceUri )
			// Create a JDBC transaction, and enforce transaction propagation.
            .policy(txnPropagationRequired)
            .transacted()
            //.transacted("POLICY_PROPAGATION_REQUIRED")
            .convertBodyTo( String.class )
            .log( "Received Message ${body}" )
            .idempotentConsumer(
            		header(Exchange.FILE_NAME_ONLY) ,
            		//MemoryIdempotentRepository.memoryIdempotentRepository(200)
            		idempotentRepository
            )
            
            // Unmarshal the XML content to Java using JAXB
            .log( "Unmarshal Message ${body}" )
            .unmarshal( jaxb )
			// Do some processing on the message
			.process(
                new Processor() {
                            @Override
				    public void process( Exchange exchange ) throws Exception {
                                LOG.debug("Attempting to process an incoming message..");

                        Payments payments = exchange.getIn().getBody(Payments.class);

                                LOG.info("Message looks good -> save it to the DB");
                        for ( Payment payment : payments.getPayment()) {
                            jdbcTemplate.update(
                              "insert into fuse_demo.Payments ( `from`, `to`, `amount`, `currency` ) values ('"
                                    + payment.getFrom()
                                    + "', '"
                                    + payment.getTo()
                                    + "', '"
                                    + payment.getAmount()
                                    + "', '"
                                    + payments.getCurrency()
                                    + "');"
                            );
                        }
                        if (payments.getCurrency().equals("???")) {
                                    LOG.warn("Rejecting payments file with currency '???'");
                            throw new Exception("Rejecting payments file with currency '???'");
                        }
                    }
                }
            )
			// Send the result to the target queue
			.marshal( jaxb )
			.convertBodyTo( String.class )
            .log( "Save Message ${body}" )
			.to( targetUri );
        LOG.info("Configured Tx MS Message Processor for Source URI: '" + sourceUri + "'");
	}

    @Override
	public void afterPropertiesSet() throws Exception {
		if ((incomingQueue == null) || (incomingQueue.equals(""))) { 
			throw new BeanInitializationException("You must set a value for incomingQueue");
		}
		
		if ((outgoingQueue == null) || (outgoingQueue.equals(""))) { 
			throw new BeanInitializationException("You must set a value for outgoingQueue");
		}
		
		sourceUri = "activemq:queue:" + incomingQueue;
		targetUri = "activemq:queue:" + outgoingQueue;
	}

    @Override
	public void destroy() throws Exception {
	}

	public void setIncomingQueue(String incomingQueue) {
		this.incomingQueue = incomingQueue;
	}

	public void setOutgoingQueue(String outgoingQueue) {
		this.outgoingQueue = outgoingQueue;
	}

    public void setDataSource( DataSource dataSource) {
         jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
