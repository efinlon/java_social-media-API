package com.cooksys.socialmedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.entities.Tweet;

@Mapper(componentModel = "spring", uses = { UserMapper.class, CredentialsMapper.class })
public interface TweetMapper {

	TweetResponseDto entityToDto(Tweet entity);

	Tweet dtoToEntity(TweetRequestDto requestDto);

	List<TweetResponseDto> entitiesToDtos(List<Tweet> entities);

}
