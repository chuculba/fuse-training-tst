package org.jboss.fuse.training.camel;

import org.apache.camel.*;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.*;
import org.apache.camel.spi.RouteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainApp1 {

    private static Logger logger = LoggerFactory.getLogger(MainApp1.class);

    public static void main(String[] args) throws Exception {

        // CamelContext = container where we will register the routes
        DefaultCamelContext camelContext = new DefaultCamelContext();

        // 1. Using RouteDefinition
        RouteDefinition rd = new RouteDefinition();
        rd.setExchangePattern(ExchangePattern.InOnly);
        rd.setAutoStartup("true");
        rd.setErrorHandlerBuilder(new DefaultErrorHandlerBuilder());
        rd.setTrace("true");

        // A From
        FromDefinition from = new FromDefinition("timer://exercise-rd?delay=1000");
        List<FromDefinition> fromDefinitionList = new ArrayList<FromDefinition>();
        fromDefinitionList.add(from);

        // A Log EIP
        List<ProcessorDefinition<?>> processorDefinitions = new ArrayList<ProcessorDefinition<?>>();
        LogDefinition logDefinition = new LogDefinition(">> This is the first Camel exercise - using Route Definition");
        logDefinition.setLoggingLevel(LoggingLevel.INFO );

        // A Simple processor
        ToDefinition to = new ToDefinition();
        to.setUri("log:org.jboss.training.fuse?level=DEBUG");

        processorDefinitions.add(logDefinition);
        processorDefinitions.add(to);

        // Complete Route definition
        rd.setInputs(fromDefinitionList);
        rd.setOutputs(processorDefinitions);

        // 2. Using RouteBuilder
        RouteBuilder routeBuilder = new RouteBuilder() {

            // A Route sending every x seconds a message
            // to the logger processor

            @Override
            public void configure() throws Exception {
                from("timer://exercise-rb?delay=1000")
                        .log(LoggingLevel.INFO, ">> This is the first Camel exercise - using Route Builder")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                logger.info(">> Invoked timer at " + new Date() + " for RouteBuilder");
                            }
                        });
            }
        };

        // Register RouteDefinition & RouteBuilder
        camelContext.addRouteDefinition(rd);
        camelContext.addRoutes(routeBuilder);

        // Start the container
        camelContext.start();

        // give it time to realize it has work to do
        Thread.sleep(20000);

    }
}
