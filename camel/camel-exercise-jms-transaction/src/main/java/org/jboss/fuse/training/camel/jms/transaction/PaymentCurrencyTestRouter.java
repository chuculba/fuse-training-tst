package org.jboss.fuse.training.camel.jms.transaction;

import com.fusesource.training.payment.Payments;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class PaymentCurrencyTestRouter
    extends SpringRouteBuilder
    implements InitializingBean, DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentCurrencyTestRouter.class);
	
	private String incomingQueue;
	private String outgoingQueue; 
	
	String sourceUri;
	String targetUri;
	
	@Override
	public void configure() throws Exception {
        // Initialize the Jaxb context so that Camel can marshal/unmarshal
        JaxbDataFormat jaxb = new JaxbDataFormat( "com.fusesource.training.payment" );
        jaxb.setPrettyPrint( true );

		errorHandler(noErrorHandler());
		
		// From the incoming queue
		from( sourceUri )
			.convertBodyTo( String.class )
			// Unmarshal the XML content to Java using JAXB
			.unmarshal( jaxb )
			// Do some processing on the message
			.process(
                new Processor() {
                            @Override
				    public void process( Exchange exchange ) throws Exception {
                                LOG.debug("Attempting to process an incoming message..");

                        Payments payments = exchange.getIn().getBody(Payments.class);

                        if (payments.getCurrency().equals("???")) {
                                    LOG.warn("Rejecting payments file with currency '???'");
                            throw new Exception("Rejecting payments file with currency '???'");
                        } else {
                                    LOG.info("Message looks good.");
                        }

                    }
                }
            )
			// Send the result to the target queue
			.marshal( jaxb )
			.convertBodyTo( String.class )
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
}
