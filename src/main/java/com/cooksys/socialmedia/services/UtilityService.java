package com.cooksys.socialmedia.services;

import java.util.List;

import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;

public interface UtilityService {

	User isUserPresentAndActive(String username);

	Tweet isTweetPresentAndActive(Long id);

	List<Tweet> clearDeletedTweets(List<Tweet> tweetList);

	List<User> clearDeletedUsers(List<User> userList);

}