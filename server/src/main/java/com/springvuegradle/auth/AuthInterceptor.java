package com.springvuegradle.auth;

import java.time.Instant;
import java.time.ZoneOffset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.AdminRepository;
import com.springvuegradle.model.repository.SessionRepository;

/**
 * Intercept HTTP requests and attempt to authenticate the user
 * 
 * @author Alex Hobson
 *
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

	/**
	 * JPA Session Repository
	 */
	@Autowired
	private SessionRepository sessionRepo;

	/**
	 * JPA Admin Repository
	 */
	@Autowired
	private AdminRepository adminRepo;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// response.setHeader("Access-Control-Allow-Origin", "http://localhost:9500");
		// response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE,
		// OPTIONS");
		// response.setHeader("Access-Control-Allow-Headers",
		// "content-type,DNT,x-auth-token");
		// response.setHeader("Access-Control-Allow-Credentials", "true");

		String token = request.getHeader("X-Auth-Token");
		if (token != null)
			if (sessionRepo.existsById(token)) {
				Session session = sessionRepo.findById(token).get();
				if (session.getExpiry().isBefore(Instant.now().atOffset(ZoneOffset.UTC))) {
					sessionRepo.delete(session);
				} else {
					User user = session.getUser();
					long profileId = user.getUserId();
					if (adminRepo.existsById(user.getUserId())) {
						profileId = -1;
					}
					request.setAttribute("authenticateduser", user);
					request.setAttribute("authenticatedid", profileId);
				}
			}

		return true;
	}
}