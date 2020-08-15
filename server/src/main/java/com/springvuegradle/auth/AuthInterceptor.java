package com.springvuegradle.auth;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.springvuegradle.model.data.Session;
import com.springvuegradle.model.data.User;
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

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String token = request.getHeader("X-Auth-Token");
		if (token != null) {
			Optional<Session> optionalSession = sessionRepo.findById(token);
			if (optionalSession.isPresent()) {
				Session session = optionalSession.get();
				if (session.getExpiry().isBefore(Instant.now().atOffset(ZoneOffset.UTC))) {
					sessionRepo.delete(session);
				} else {
					User user = session.getUser();
					long profileId = user.getUserId();
					request.setAttribute("authenticateduser", user);
					request.setAttribute("authenticatedid", profileId);
					request.setAttribute("permissionLevel", user.getPermissionLevel());
				}
			}
		}

		return true;
	}
}