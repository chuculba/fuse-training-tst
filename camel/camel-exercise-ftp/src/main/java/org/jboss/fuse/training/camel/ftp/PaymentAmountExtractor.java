package org.jboss.fuse.training.camel.ftp;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class PaymentAmountExtractor
    extends RouteBuilder
        implements InitializingBean, DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentAmountExtractor.class);

    private String ftpHostOrIPAddress;
    private String ftpUserName;
    private String ftpPassword;
    private String ftpDirectoryWithEndingSlash;

    public void setFtpHostOrIPAddress( String pFtpHostOrIPAddress ) {
        ftpHostOrIPAddress = pFtpHostOrIPAddress;
    }

    public void setFtpPassword( String pFtpPassword ) {
        ftpPassword = pFtpPassword;
    }

    public void setFtpUserName( String pFtpUserName ) {
        ftpUserName = pFtpUserName;
    }

    public void setFtpDirectoryWithEndingSlash(String ftpDirectory) {
        this.ftpDirectoryWithEndingSlash = ftpDirectory;
    }

    String sourceUri;
	
    @Override
	public void configure() throws Exception {		
        LOG.debug("PaymentAmountExtractor being configured; listening using uri '" + sourceUri + "'");
		from( sourceUri )
            .convertBodyTo(String.class)
            .to( "log:" + PaymentAmountExtractor.class.getPackage() + "?level=DEBUG&showProperties=true&showHeaders=true" )
            .idempotentConsumer(
                simple( "file:name" ),
                MemoryIdempotentRepository.memoryIdempotentRepository( 1024 )
            )
            .unmarshal().csv()
            .process(
                new Processor() {
                            @Override
                    public void process( Exchange exchange )
                                    throws Exception {
                        List<List<String>> lines = (List<List<String>>) exchange.getIn().getBody();
                        for( List<String> line: lines ) {
                            String from = line.get( 0 );

                            String to = line.get( 1 );
                            float amount = Float.parseFloat( line.get( 2 ) );
                                    LOG.info("\n\nProcessing payment from '" + from + "' to '" + to + "', amount: " + amount + "\n\n");
        //					throw new Exception ("messing around!");
                        }
                    }
                }
            );

        LOG.debug("PaymentAmountExtractor configured; listening using uri '" + sourceUri + "'");
	}

    @Override
    public void afterPropertiesSet() throws Exception {
		assertNotNullOrEmpty( ftpHostOrIPAddress, "ftpHostOrIPAddress" );
		assertNotNullOrEmpty( ftpUserName, "ftpUserName" );
		assertNotNullOrEmpty( ftpPassword, "ftpPassword" );
        assertNotNullOrEmpty( ftpDirectoryWithEndingSlash, "ftpDirectory" );

		sourceUri = "ftp://" + ftpUserName + "@" + ftpHostOrIPAddress + "/" + ftpDirectoryWithEndingSlash
			+ "?password=" + ftpPassword
//            + "&directory=true"
//            + "&delete=true"
                + "&delay=5000"
        ;
	}
	
	private void assertNotNullOrEmpty( String value, String propertyName )
            throws BeanInitializationException {
		if( value == null || "".equals( value.trim() ) ) {
			throw new BeanInitializationException( "You must specify a value for property '" + propertyName + "'" );
		}
	}

    @Override
	public void destroy() throws Exception {
        LOG.debug("PaymentAmountExtractor shutting down.");
	}
}
