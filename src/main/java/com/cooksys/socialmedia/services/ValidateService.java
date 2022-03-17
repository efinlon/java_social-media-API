package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.entities.Credentials;
import com.cooksys.socialmedia.entities.User;

public interface ValidateService {

	boolean hashtagExists(String label);

	boolean usernameExistsAndIsActive(String username);

	boolean authenticateUser(User user, Credentials credentials);

	boolean tweetExistsAndIsActive(Long id);

	boolean usernameExists(String username);

	boolean usernameAvailable(String username);

}