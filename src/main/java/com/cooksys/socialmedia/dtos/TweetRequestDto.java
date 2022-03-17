package com.cooksys.socialmedia.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TweetRequestDto {

	private String content;

	private CredentialsDto credentialsDto;

}
