package com.cooksys.socialmedia.services;

import java.util.List;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;

public interface TweetService {

	List<TweetResponseDto> getTweets();

	TweetResponseDto getTweetById(Long id);

	TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto);

	TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);

	List<TweetResponseDto> getTweetReposts(Long id);

	TweetResponseDto postReply(Long id, TweetRequestDto tweetRequestDto);

	void likeTweet(Long id, CredentialsDto credentialsDto);

	ContextDto getContext(Long id);

	List<HashtagDto> getTags(Long id);

	List<UserResponseDto> getMentions(Long id);

	List<UserResponseDto> getUsersWhoLiked(Long id);

	List<TweetResponseDto> getReplies(Long id);

	TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto);

}
