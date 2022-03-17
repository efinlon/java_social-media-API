package com.cooksys.socialmedia.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = BadRequestException.class)
	public ResponseEntity<Object> handleBadRequestException(BadRequestException badRequestException,
			WebRequest webRequest) {
		return handleExceptionInternal(badRequestException, "The request is missing required information", null,
				HttpStatus.BAD_REQUEST, webRequest);
	}

	@ExceptionHandler(value = UsernameTakenException.class)
	public ResponseEntity<Object> handleUsernameTakenException(UsernameTakenException usernameTakenException,
			WebRequest webRequest) {
		return handleExceptionInternal(usernameTakenException, "The username provided is already taken", null,
				HttpStatus.CONFLICT, webRequest);
	}

	@ExceptionHandler(value = EmailTakenException.class)
	public ResponseEntity<Object> handleEmailTakenException(EmailTakenException emailTakenException,
			WebRequest webRequest) {
		return handleExceptionInternal(emailTakenException, "The email provided is already taken", null,
				HttpStatus.CONFLICT, webRequest);
	}

	@ExceptionHandler(value = UserNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException userNotFoundExcpetion,
			WebRequest webRequest) {
		return handleExceptionInternal(userNotFoundExcpetion, "No user with the provided username could be found", null,
				HttpStatus.NOT_FOUND, webRequest);
	}

	@ExceptionHandler(value = CredentialsInvalidException.class)
	public ResponseEntity<Object> handleCredentialsInvalidException(
			CredentialsInvalidException credentialsInvalidException, WebRequest webRequest) {
		return handleExceptionInternal(credentialsInvalidException, "Unable to authenticate request", null,
				HttpStatus.CONFLICT, webRequest);
	}

	@ExceptionHandler(value = FollowingRelationshipException.class)
	public ResponseEntity<Object> FollowingRelationshipError(
			FollowingRelationshipException followingRelationshipException, WebRequest webRequest) {
		return handleExceptionInternal(followingRelationshipException,
				"Following relationship error between these users", null, HttpStatus.CONFLICT, webRequest);
	}

	@ExceptionHandler(value = TweetNotFoundException.class)
	public ResponseEntity<Object> handleTweetNotFoundException(TweetNotFoundException tweetNotFoundException,
			WebRequest webRequest) {
		return handleExceptionInternal(tweetNotFoundException, "No tweet with that id", null, HttpStatus.NOT_FOUND,
				webRequest);
	}

	@ExceptionHandler(value = HashtagNotFoundException.class)
	public ResponseEntity<Object> handleHashtagNotFoundException(HashtagNotFoundException hashtagNotFoundException,
			WebRequest webRequest) {
		return handleExceptionInternal(hashtagNotFoundException, "That hashtag does not exist", null,
				HttpStatus.NOT_FOUND, webRequest);
	}

}
