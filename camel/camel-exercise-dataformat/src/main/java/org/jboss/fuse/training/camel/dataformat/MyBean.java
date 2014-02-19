package org.jboss.fuse.training.camel.dataformat;

import org.apache.camel.Exchange;
import org.jboss.fuse.training.camel.dataformat.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public class MyBean {

    private static final Logger logger = LoggerFactory.getLogger(MyBean.class);

    public Student process(Exchange exchange) throws Exception {

        List<?> list = (List<?>) exchange.getIn().getBody();
        HashMap<String, Object> map = (HashMap<String, Object>) list.get(0);
        String modelKey = Student.class.getName();
        Student student = (Student) map.get(modelKey);

        student.setStatus("true");
        student.setRoom("jboss-fuse-0123");

        return student;

        // exchange.getIn().setBody(student);
    }

}
