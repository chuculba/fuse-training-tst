This Exercises shows how to use JMS Transaction to make sure that either all JMS messages are read and written
or none of them meaning reads are pushed back onto the destination and writes are undone.

Deployment:

1) Running it inside an embedded Camel and ActiveMQ instance:

a) Start the ActiveMQ broker using:

    mvn activemq:run

b) In another command line console start Camel using:

    mvn camel:run

You will find the output of the routes inside:

    ./target/test-classes/camel/out

and you can add new Payment XML files into:

    ./target/test-classes/camel/in

2) Build, Install and Deploy it as OSGi bundle:

    mvn clean install

then please enter this in the Karaf Console:

    features:install camel-jms
    features:install camel-jaxb

    osgi:install -s mvn:com.fusesource.training/camel-exercise-jms-transaction/1.0

ATTENTION: in case of errors and a rebuild inside maven (mvn install) you need to update the project using
           the bundle number given by the original install because a subsequent install won't do anything good:

    osgi:update <bundle number>

also make sure with osgi:list that the application is started and if not then

    osgi:start <bundle number>

3) Install it as feature (need to add the features URL ahead of time, please see the camel-exercise-features project)

    features:install camel-exercise-jms-transaction

In order to make the route do something you can copy an XML Payment file into '/camel-exercise/jms-transactions/in'
directory:

    cp src/test/resources/camel/in/VariousUSPayments.xml /camel-exercise/jms-transactions/in/