/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jboss.fuse.training.camel;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.jboss.training.Customer;
import org.jboss.training.CustomerService;
import org.jboss.training.CustomerService_Service;
import org.jboss.training.GetAllCustomersResponse;

public final class Client {

	private Client() {
	}

	public static void main(String args[]) throws Exception {
		try {
			System.out.println("Starting  with  Client");
			SpringBusFactory bf = new SpringBusFactory();
			URL busFile = Client.class.getResource("/client.xml");
			Bus bus = bf.createBus(busFile.toString());
			BusFactory.setDefaultBus(bus);

			bus.getOutInterceptors().add(new MessageLossSimulator());
			CustomerService_Service service;
			if (args.length != 0 && args[0].length() != 0) {
				File wsdlFile = new File(args[0]);
				URL wsdlURL;
				if (wsdlFile.exists()) {
					wsdlURL = wsdlFile.toURI().toURL();
				} else {
					wsdlURL = new URL(args[0]);
				}
				service = new CustomerService_Service(wsdlURL);
			} else {
				service = new CustomerService_Service();
			}
			CustomerService port = service.getCustomerServicePort();

			// make a sequence of invocations
			for (int i = 0; i < 10; i++) {
				System.out.println("Call to remote service:" + i);
				GetAllCustomersResponse customers = port.getAllCustomers();
				customers.getReturn();
				for (int j = 0; i < customers.getReturn().size(); j++) {
					Customer c = customers.getReturn().get(j);
					System.out.println(c.getName() + " " + c.getSurnameB());
				}

			}

			// allow aynchronous resends to occur
			Thread.sleep(30 * 1000);

			if (port instanceof Closeable) {
				((Closeable) port).close();
			}
			bus.shutdown(true);
			System.out.println("Done");

		} catch (UndeclaredThrowableException ex) {
			ex.getUndeclaredThrowable().printStackTrace();
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
}
