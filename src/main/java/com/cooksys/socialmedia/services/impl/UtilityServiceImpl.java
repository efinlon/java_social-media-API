package com.cooksys.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.TweetNotFoundException;
import com.cooksys.socialmedia.exceptions.UserNotFoundException;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.UtilityService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UtilityServiceImpl implements UtilityService {

	private final UserRepository userRepository;
	private final TweetRepository tweetRepository;

	@Override
	public User isUserPresentAndActive(String username) {
		Optional<User> optionalUser = userRepository.findByCredentialsUsernameAndActiveIsTrue(username);
		if (optionalUser.isEmpty()) {
			throw new UserNotFoundException();
		}
		return optionalUser.get();
	}

	@Override
	public Tweet isTweetPresentAndActive(Long id) {
		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndActiveIsTrue(id);
		if (optionalTweet.isEmpty()) {
			throw new TweetNotFoundException();
		}
		return optionalTweet.get();
	}

	@Override
	public List<Tweet> clearDeletedTweets(List<Tweet> tweetList) {
		List<Tweet> result = new ArrayList<Tweet>();

		for (Tweet tweet : tweetList) {
			if (tweet.isActive()) {
				result.add(tweet);
			}
		}

		return result;
	}

	@Override
	public List<User> clearDeletedUsers(List<User> userList) {
		List<User> result = new ArrayList<User>();

		for (User user : userList) {
			if (user.isActive()) {
				result.add(user);
			}
		}
		return result;
	}
}