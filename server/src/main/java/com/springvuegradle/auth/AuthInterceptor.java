package com.springvuegradle.auth;

import java.time.Instant;
import java.time.ZoneOffset;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

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
		boolean needsAuthentication = true;
		for (String endpoint : this.nonAuthenticatedEndpoints) {
			if (request.getRequestURI().equalsIgnoreCase(endpoint)) {
				needsAuthentication = false;
				break;
			}
		}

		if (needsAuthentication) {
			boolean authenticated = false;
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

			return authenticated;
		} else {
			return true;
		}
	}
}