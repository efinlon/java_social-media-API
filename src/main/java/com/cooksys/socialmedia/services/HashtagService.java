package com.cooksys.socialmedia.services;

import java.util.List;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;

public interface HashtagService {

	List<HashtagDto> getTags();

	List<TweetResponseDto> getTweetsByLabel(String label);

}
