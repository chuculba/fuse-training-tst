package org.jboss.fuse.training.camel;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class MyRouteBuilder extends RouteBuilder {

    private static Logger logger = LoggerFactory.getLogger(MyRouteBuilder.class);
    public MyBean myBean;

    public MyRouteBuilder() {
        myBean = new MyBean();
    }

    @Override
    public void configure() throws Exception {

        from("timer://exercise1delay=2000")
                .log(LoggingLevel.INFO, ">> This is the 3rd Camel exercise")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        logger.info(">> Invoked timer at " + new Date());
                    }
                })

                // Add an object to the body
                // using a constant expression
                .setBody().constant(myBean)

                // Set different Headers
                // using ognl & simple expressions
                .setHeader("addition")
                     .ognl("request.body.addition()")

                .setHeader("val1")
                     .simple("${body.getVal1}")
                .setHeader("val2")
                     .simple("${body.getVal2}")

                // Log addition result
                .log(">> ${header.val1} + ${header.val2} = ${header.addition}") ;

    }


    public class MyBean {

        private int val1 = 10;
        private int val2 = 20;

        public int addition() {
            return val1 + val2;
        }


        public int getVal1() {
            return val1;
        }

        public void setVal1(int val1) {
            this.val1 = val1;
        }

        public int getVal2() {
            return val2;
        }

        public void setVal2(int val2) {
            this.val2 = val2;
        }

    }
}
