package com.springvuegradle;

import com.springvuegradle.model.repository.EmailRepository;
import com.springvuegradle.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Main class that runs the application
 * @author Alex Hobson
 *
 */
public class Main {

	/**
	 * Main method that is the first piece of code to be executed on runtime
	 * @param args Runtime args passed into the app
	 */

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(RESTApplication.class, args);
		
		//override default handling for a 404 not found exception
		DispatcherServlet dispatcherServlet = (DispatcherServlet)context.getBean("dispatcherServlet");
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
	}
}
