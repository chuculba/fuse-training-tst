This Exercises shows how an Camel FTP Endpoint can be used within a Camel Route. This exercise is self-contained
meaning it does not depend on any other component or exercises and therefore can be run standalone or being deployed
within an OSGi container like OSGi.

Deployment:

1) Running it inside an embedded Camel instance:

    mvn clean camel:run

2) Build, Install and Deploy it as OSGi bundle:

    mvn clean install

then please enter this in the Karaf Console:

    features:install camel-csv
    features:install camel-ftp
    osgi:install -s mvn:org.jboss.fuse.training/camel-exercise-ftp/1.0

3) Install it as feature (need to add the features URL ahead of time, please see the camel-exercise-features project)

    features:install camel-exercise-ftp

NOTE:
    There is no need to install the camel-csv and camel-ftp because this dependency is resolved within the feature.
    
Now we need to setup a FTP server with these requirements:

    - user name:            amelia
    - password:             amelia
    - host:                 localhost
    - virtual directory:    /ftp

on Windows FileZilla (http://filezilla-project.org/), on Unix there should be an FTP available and on Mac OS X
a good choice would be PureFTPd Manager (http://jeanmatthieu.free.fr/pureftpd/). Please check with an FTP Client that
everything is working as expected.

Then upload a CSV Payment files (you can take them from 'src/test/resources/camel/in') and you will see the Payment
Amounts being extracted and printed to the log file. You can see it on the Karaf console with:

    log:display -n <number of lines>

where you should see some lines like this:

    6:32:07,518 | INFO  | 16: FtpComponent | PaymentAmountExtractor           | ses.ftp.PaymentAmountExtractor$1   67 |

    Processing payment from '12345467' to '43211234', amount: 10000.0


