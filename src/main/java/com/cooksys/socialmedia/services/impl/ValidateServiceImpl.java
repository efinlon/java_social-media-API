package com.cooksys.socialmedia.services.impl;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.entities.Credentials;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.ValidateService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ValidateServiceImpl implements ValidateService {

	private final HashtagRepository hashtagRepository;
	private final UserRepository userRepository;
	private final TweetRepository tweetRepository;

	@Override
	public boolean hashtagExists(String label) {
		return hashtagRepository.findByLabelIgnoreCase(label).isPresent();
	}

	@Override
	public boolean usernameExistsAndIsActive(String username) {
		return userRepository.findByCredentialsUsernameAndActiveIsTrue(username).isPresent();
	}

	@Override
	public boolean authenticateUser(User user, Credentials credentials) {
		return user.getCredentials().equals(credentials);
	}

	@Override
	public boolean tweetExistsAndIsActive(Long id) {
		return tweetRepository.findByIdAndActiveIsTrue(id).isPresent();
	}

	@Override
	public boolean usernameExists(String username) {
		return userRepository.findByCredentialsUsername(username).isPresent();
	}

	@Override
	public boolean usernameAvailable(String username) {
		return userRepository.findByCredentialsUsername(username).isEmpty();
	}
}