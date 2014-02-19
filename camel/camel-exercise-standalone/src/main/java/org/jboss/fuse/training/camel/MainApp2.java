package org.jboss.fuse.training.camel;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Date;

public class MainApp2 {

    private static Logger logger = LoggerFactory.getLogger(MainApp2.class);

    private Main main;

    public static void main(String[] args) throws Exception {
        MainApp2 app = new MainApp2();
        app.boot();
    }

    public void boot() throws Exception {
        // create a Main instance
        main = new Main();

        // enable hangup support so you can press ctrl + c to terminate the JVM
        main.enableHangupSupport();

        // add routes
        main.addRouteBuilder(new MyRouteBuilder());

        // run until you terminate the JVM
        logger.info("Starting Camel. Use ctrl + c to terminate the JVM.\n");

        main.run();
    }

    private static class MyRouteBuilder extends RouteBuilder {
        @Override
        public void configure() throws Exception {
            from("timer://exercise1delay=2000").routeId("# Timer Exercise 1 #")
                .log(LoggingLevel.INFO, ">> This is the first Camel exercise")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        logger.info(">> Invoked timer at " + new Date());
                    }
                });
        }
    }


}
