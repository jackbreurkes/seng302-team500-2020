package com.springvuegradle.auth;

import java.time.Instant;
import java.time.ZoneOffset;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
import com.springvuegradle.model.repository.AdminRepository;
import com.springvuegradle.model.repository.SessionRepository;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Autowired
	private SessionRepository sessionRepo;

	@Autowired
	private AdminRepository adminRepo;

	private final String[] nonAuthenticatedEndpoints = new String[] { "/login", "/createprofile" };

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (!response.containsHeader("Access-Control-Allow-Origin")) {
			response.addHeader("Access-Control-Allow-Origin", "http://localhost:9500");
		}
		if (!response.containsHeader("Access-Control-Allow-Methods")) {
			response.addHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
		}
		if (!response.containsHeader("Access-Control-Allow-Headers")) {
			response.addHeader("Access-Control-Allow-Headers", "content-type,DNT");
		}
		if (!response.containsHeader("Access-Control-Allow-Credentials")) {
			response.addHeader("Access-Control-Allow-Credentials", "true");
		}

		boolean needsAuthentication = true;
		for (String endpoint : this.nonAuthenticatedEndpoints) {
			if (request.getRequestURI().equalsIgnoreCase(endpoint)) {
				needsAuthentication = false;
				break;
			}
		}

		if (needsAuthentication) {
			boolean authenticated = false;
			if (request.getCookies() != null)
				for (Cookie cookie : request.getCookies()) {
					if (cookie.getName().equalsIgnoreCase("sessionid")) {
						String sessionId = cookie.getValue();
						if (sessionRepo.existsById(sessionId)) {
							Session session = sessionRepo.findById(sessionId).get();
							if (session.getExpiry().isBefore(Instant.now().atOffset(ZoneOffset.UTC))) {
								sessionRepo.delete(session);
							} else {
								authenticated = true;
								User user = session.getUser();
								long profileId = user.getUserId();
								if (adminRepo.existsById(user.getUserId())) {
									profileId = -1;
								}
								request.setAttribute("authenticateduser", user);
								request.setAttribute("authenticatedid", profileId);
							}
						}
					}
				}

			if (authenticated) {
				return true;
			} else {
				if (!request.getMethod().equals("OPTIONS")) {
					response.setStatus(401);
					response.setContentType("application/json");
					response.getOutputStream().write("{\"error\": \"Auth you are not\"}".getBytes());
					return false;
				} else {
					return true;
				}
			}
		} else {
			return true;
		}
	}
}