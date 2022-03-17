package com.cooksys.socialmedia.dtos;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ContextDto {

	private TweetResponseDto target;

	private List<TweetResponseDto> before;

	private List<TweetResponseDto> after;

}
