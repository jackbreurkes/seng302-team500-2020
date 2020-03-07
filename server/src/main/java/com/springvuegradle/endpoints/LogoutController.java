package com.springvuegradle.endpoints;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springvuegradle.model.repository.SessionRepository;
import com.springvuegradle.model.responses.ErrorResponse;

@RestController
public class LogoutController {

	@Autowired
	private SessionRepository sessionRepo;

	private final ResponseEntity<?> authError = ResponseEntity.badRequest()
			.body(new ErrorResponse("Somehow you bypassed authentication. This shouldn't be possible"));

	@DeleteMapping("/logmeout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		String token = null;
		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("sessionid")) {
				token = cookie.getValue();
				break;
			}
		}

		if (token == null) {
			return authError;
		} else {
			if (sessionRepo.existsById(token)) {
				sessionRepo.deleteById(token);
				
				Cookie cookie = new Cookie("sessionid", token);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
				
				return ResponseEntity.noContent().build();
			} else {
				return authError;
			}
		}
	}
}
