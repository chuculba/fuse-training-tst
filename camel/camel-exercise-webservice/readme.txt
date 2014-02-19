This Exercises shows how a Web Service can be exposed in a Camel route using Spring DSL language. This exercise is self-contained
meaning it does not depend on any other component or exercises and therefore can be run standalone or being deployed
within an OSGi container like OSGi. The first camel routes consumes a text file and creates a WebService request to save the customer.
The second camel route exposes the WebService and is able to SaveCustomer, GetCustomerByName or GetAllCustomers.

Deployment:

1) Running it inside an embedded Camel instance:

    mvn clean camel:run

2) Build, Install and Deploy it as OSGi bundle:

    mvn clean install

then please enter this in the Karaf Console:

    osgi:install -s mvn:org.jboss.fuse.training/camel-exercise-webservice/1.0

3) Install it as feature (need to add the features URL ahead of time, please see the camel-exercise-features project)

    features:install camel-exercise-webservice

4) Open your favotite browser and points to the following address to check the wsdl

    http://localhost:9090/training/WebService?wsdl

5) Start soapui and create a project using the previous address. Next, send the following requests

To create a Customer

REQUEST

<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope
   xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
   xmlns:tra="http://training.jboss.org/">
   <soapenv:Body>
      <tra:saveCustomer>
         <customer>
            <name>charles</name>
            <address>yyyyy</address>
            <birthDate>12/12/12</birthDate>
         </customer>
      </tra:saveCustomer>
   </soapenv:Body>
</soapenv:Envelope>

RESPONSE

<ns2:Envelope xmlns:ns2="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns3="http://training.jboss.org/">
   <ns2:Body>
      <ns3:customer>
         <name>charles88</name>
         <address>yyyyy</address>
         <numOrders>86</numOrders>
         <revenue>7170.0</revenue>
         <test>100.0</test>
         <type>PRIVATE</type>
      </ns3:customer>
   </ns2:Body>
</ns2:Envelope>

TO GET A CUSTOMER BY NAME

REQUEST

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tra="http://training.jboss.org/">
   <soapenv:Header/>
   <soapenv:Body>
      <tra:getCustomerByName>
         <name>charles</name>
      </tra:getCustomerByName>
   </soapenv:Body>
</soapenv:Envelope>

RESPONSE

<ns2:Envelope xmlns:ns2="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns3="http://training.jboss.org/">
   <ns2:Body>
      <ns3:getCustomerByNameResponse>
         <return>
            <name>charles</name>
            <address>yyyyy</address>
            <numOrders>86</numOrders>
            <revenue>4393.0</revenue>
            <test>100.0</test>
            <type>PRIVATE</type>
         </return>
      </ns3:getCustomerByNameResponse>
   </ns2:Body>
</ns2:Envelope>


TO GET ALL CUSTOMERS

REQUEST

<soapenv:Envelope
   xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
   xmlns:tra="http://training.jboss.org/">
   <soapenv:Body>
      <tra:getAllCustomers/>
   </soapenv:Body>
</soapenv:Envelope>

RESPONSE  (Your response may vary from this list ...)

<ns2:Envelope xmlns:ns2="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns3="http://training.jboss.org/">
   <ns2:Body>
      <ns3:getAllCustomersResponse>
         <return>
            <name>Fuse Students</name>
            <address>FuseSource Office</address>
            <numOrders>69</numOrders>
            <revenue>2789.0</revenue>
            <test>100.0</test>
            <type>PRIVATE</type>
         </return>
         <return>
            <name>charles</name>
            <address>yyyyy</address>
            <numOrders>86</numOrders>
            <revenue>4393.0</revenue>
            <test>100.0</test>
            <type>PRIVATE</type>
         </return>
         <return>
            <name>charles</name>
            <address>yyyyy</address>
            <numOrders>81</numOrders>
            <revenue>6269.0</revenue>
            <test>100.0</test>
            <type>PRIVATE</type>
         </return>
         <return>
            <name>charles</name>
            <address>yyyyy</address>
            <numOrders>72</numOrders>
            <revenue>8073.0</revenue>
            <test>100.0</test>
            <type>PRIVATE</type>
         </return>
         <return>
            <name>charles</name>
            <address>yyyyy</address>
            <numOrders>10</numOrders>
            <revenue>7985.0</revenue>
            <test>100.0</test>
            <type>PRIVATE</type>
         </return>
         <return>
            <name>charles</name>
            <address>yyyyy</address>
            <numOrders>70</numOrders>
            <revenue>5847.0</revenue>
            <test>100.0</test>
            <type>PRIVATE</type>
         </return>
         <return>
            <name>charles</name>
            <address>yyyyy</address>
            <numOrders>57</numOrders>
            <revenue>5918.0</revenue>
            <test>100.0</test>
            <type>PRIVATE</type>
         </return>
         <return>
            <name>charles</name>
            <address>yyyyy</address>
            <numOrders>79</numOrders>
            <revenue>1994.0</revenue>
            <test>100.0</test>
            <type>PRIVATE</type>
         </return>
      </ns3:getAllCustomersResponse>
   </ns2:Body>
</ns2:Envelope>


6) Alternatively, you can copy/paste the file src/main/resources/data/customer.txt in the target
   directory camel-exercise/webservice and control on the DOS/UNIX console that the following feedback messages appears in the
   log

qtp980604133-39 INFO [org.apache.cxf.interceptor.LoggingInInterceptor] - Inbound Message
----------------------------
ID: 1
Address: http://localhost:9090/training/WebService
Encoding: ISO-8859-1
Http-Method: POST
Content-Type:
Headers: {Content-Length=[568], Content-Type=[null], Host=[localhost:9090], SOAPAction=[http://training.jboss.org/saveCustomer], User-Agent=[Jakarta Commons-HttpClient/3.1]}
Payload: <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:Envelope xmlns:ns2="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns3="http://training.jboss.org/">
    <ns2:Body>
        <ns3:saveCustomer>
            <customer>
                <name>FuseSource</name>
                <address>FuseSource Office</address>
                <numOrders>31</numOrders>
                <revenue>8167.0</revenue>
                <test>100.0</test>
                <type>BUSINESS</type>
            </customer>
        </ns3:saveCustomer>
    </ns2:Body>
</ns2:Envelope>

--------------------------------------
qtp980604133-39 INFO [cxf-to-queue] - SOAP message received : <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:Envelope xmlns:ns2="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns3="http://training.jboss.org/">
    <ns2:Body>
        <ns3:saveCustomer>
            <customer>
                <name>FuseSource</name>
                <address>FuseSource Office</address>
                <numOrders>31</numOrders>
                <revenue>8167.0</revenue>
                <test>100.0</test>
                <type>BUSINESS</type>
            </customer>
        </ns3:saveCustomer>
    </ns2:Body>
</ns2:Envelope>
 and operation type : http://training.jboss.org/saveCustomer
qtp980604133-39 INFO [org.jboss.fuse.training.camel.Enrich] - >> Customer created and added in the array.