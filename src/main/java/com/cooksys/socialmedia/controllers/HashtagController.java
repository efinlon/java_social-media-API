package com.cooksys.socialmedia.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.services.HashtagService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/tags")
public class HashtagController {

	private final HashtagService hashtagService;

	@GetMapping
	public List<HashtagDto> getTags() {
		return hashtagService.getTags();
	}

	@GetMapping("/{label}")
	public List<TweetResponseDto> getTweetsByLabel(@PathVariable String label) {
		return hashtagService.getTweetsByLabel(label);
	}

}
