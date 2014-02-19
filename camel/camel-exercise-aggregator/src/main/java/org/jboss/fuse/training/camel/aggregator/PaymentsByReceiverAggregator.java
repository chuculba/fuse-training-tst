package org.jboss.fuse.training.camel.aggregator;

import java.io.File;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * This class defines a Camel Route that takes all files from a given directory
 * reads out their XML payments and splits them into single payments. Then it
 * will aggregate the individual payments into a new file ordered by the 
 * receiver of the payment within the given timeout period. 
 */
public class PaymentsByReceiverAggregator
    extends RouteBuilder
        implements InitializingBean, DisposableBean {
	
    private static final Logger LOG = LoggerFactory.getLogger(PaymentsByReceiverAggregator.class);

    private String sourceDirectory;
    private String sinkDirectory;
    private int aggregateTimeoutPeriodInSeconds;
    private boolean createDirectories;

	private String sourceUri;
	private String sinkUri;

    public void setSinkDirectory( String pSinkDirectory ) {
        sinkDirectory = pSinkDirectory;
    }

    public void setSourceDirectory( String pSourceDirectory ) {
        sourceDirectory = pSourceDirectory;
    }

    public void setAggregateTimeoutPeriodInSeconds( int pAggregateTimeoutPeriodInSeconds ) {
        aggregateTimeoutPeriodInSeconds = pAggregateTimeoutPeriodInSeconds;
    }

    public void setCreateDirectories( boolean pCreateDirectories ) {
        createDirectories = pCreateDirectories;
    }

    @Override
	public void configure() throws Exception {
        // This route is doing the following:
        // - reading files from the given source directory
        // - splitting them apart into single payments
        // - aggregate them into groups by the receiver value
        // - store them into the given sink directory and file

    	// Define the namespace for the Payment XML
        Namespaces ns = new Namespaces("p", "http://www.fusesource.com/training/payment")
            .add("xsd", "http://www.w3.org/2001/XMLSchema");

        from( sourceUri )
			// This could also be written like this:
			//            .split(
			//                xpath( "/p:Payments/p:Payment" )
			//				    .namespace( "p", "http://www.fusesource.com/training/payment" )
        	//            )
            .split()
                .xpath( "/p:Payments/p:Payment", ns )
            .log( "\nGot separated payment with this content: \n${body}\nwhich is now being handed over to the aggregator\n" )
            .convertBodyTo( String.class )
			.aggregate(new BodyAppenderAggregator())
				// XPath Expression returns a Node List which is unique for each request so there will be no
				// no aggregation. Here we convert this to a String such that the text from the to element
            	// is used as the correlation expression.
                .xpath( "/p:Payment/p:to", String.class, ns )
                .completionTimeout(aggregateTimeoutPeriodInSeconds * 1000)
                .completionSize(0)
            .log( "\nGot aggregated payments with this content: \n${body}\n\nwhich is now being sent to the sink endpoint\n" )
            .to( sinkUri );
	}
	
    @Override
	public void afterPropertiesSet() throws Exception {
		// Check the given source directory to make sure it is usable and eventually create the source URI from it
        if( sourceDirectory == null || "".equals( sourceDirectory.trim() ) ) {
            throw new BeanInitializationException(
                "You must specify a value for source directory (sourceDirectory)"
            );
        }
        File lSourceDirectory = new File( sourceDirectory );
        if( !lSourceDirectory.exists() ) {
            boolean lNoDirectory = false;
            if( createDirectories ) {
                lNoDirectory = lSourceDirectory.mkdirs();
            }
            if( !lNoDirectory ) {
                throw new BeanInitializationException( "Given source directory: '" + sourceDirectory + "' is not a directory" );
            }
        }
        if( !lSourceDirectory.canRead() ) {
            throw new BeanInitializationException( "Given source directory: '" + sourceDirectory + "' cannot be read" );
        }
        sourceUri = "file:" + sourceDirectory;

        // Check the given sink directory to make sure it is usable and eventually create the sink URI from it
        if( sinkDirectory == null || "".equals( sinkDirectory.trim() ) ) {
            throw new BeanInitializationException(
                "You must specify a value for sink directory (sinkDirectory)"
            );
        }
        File lSinkDirectory = new File( sinkDirectory );
        if( !lSinkDirectory.exists() ) {
            boolean lNoDirectory = false;
            if( createDirectories ) {
                lNoDirectory = lSinkDirectory.mkdirs();
            }
            if( !lNoDirectory ) {
                throw new BeanInitializationException( "Given sink directory: '" + sinkDirectory + "' is not a directory" );
            }
        }
        if( !lSinkDirectory.canWrite() ) {
            throw new BeanInitializationException( "Given sink directory: '" + sinkDirectory + "' cannot be written to" );
        }
        sinkUri = "file:" + sinkDirectory + "/?fileName=output-${id}.xml";
	}

    @Override
	public void destroy() throws Exception {
        LOG.info("Shutting down " + PaymentsByReceiverAggregator.class.getSimpleName());
	}
}
