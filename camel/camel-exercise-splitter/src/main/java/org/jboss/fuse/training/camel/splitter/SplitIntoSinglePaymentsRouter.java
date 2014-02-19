package org.jboss.fuse.training.camel.splitter;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;

public class SplitIntoSinglePaymentsRouter
    extends RouteBuilder
        implements InitializingBean, DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(SplitIntoSinglePaymentsRouter.class);
	
	private String incomingDestinationName;
	private String outgoingDestinationName;
	private String sourceDirectory;
    private String destinationComponentName;
    
    private boolean createDirectories;
	
    private String sourceFileUri;
	private String sourceJmsUri;
	private String targetJmsUri;

    public void setSourceDirectory( String pSourceDirectory ) {
        sourceDirectory = pSourceDirectory;
    }
	
    public void setIncomingDestinationName(String incomingQueueName) {
        this.incomingDestinationName = incomingQueueName;
    }
    
    public void setOutgoingDestinationName(String outgoingQueueName) {
        this.outgoingDestinationName = outgoingQueueName;
    }

    public void setDestinationComponentName( String pDestinationComponentName ) {
        destinationComponentName = pDestinationComponentName;
    }
    
    public void setCreateDirectories( boolean pCreateDirectories ) {
        createDirectories = pCreateDirectories;
    }

    @Override
	public void configure() throws Exception {
        from(sourceFileUri)
            .to(sourceJmsUri);
    	
		from( sourceJmsUri )
            .split(xpath("/p:Payments/p:Payment").namespace("p", "http://www.fusesource.com/training/payment"))
			.to( targetJmsUri );

        LOG.info("Configured Splitter Route: Source URI: '" + sourceJmsUri + "', Target URI: '" + targetJmsUri + "'");
	}

    @Override
	public void afterPropertiesSet() throws Exception {
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

        sourceFileUri = "file:" + sourceDirectory;
		
        assertNotNullOrEmpty( incomingDestinationName, "incomingDestinationName" );
        assertNotNullOrEmpty( outgoingDestinationName, "outgoingDestinationName" );
        assertNotNullOrEmpty( destinationComponentName, "destinationComponentName" );
		
		sourceJmsUri = destinationComponentName + ":" + incomingDestinationName;
		targetJmsUri = destinationComponentName + ":" + outgoingDestinationName;

        LOG.info("Source URI: '" + sourceJmsUri + "', Target URI: '" + targetJmsUri + "'");
	}

    private void assertNotNullOrEmpty( String value, String propertyName )
            throws BeanInitializationException {
        if( value == null || "".equals( value.trim() ) ) {
            throw new BeanInitializationException( "You must specify a value for property '" + propertyName + "'" );
        }
    }

    @Override
	public void destroy() throws Exception {
        LOG.info("Shutting down " + SplitIntoSinglePaymentsRouter.class.getSimpleName());
	}
}
