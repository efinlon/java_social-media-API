package com.cooksys.socialmedia.services.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.Profile;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.CredentialsInvalidException;
import com.cooksys.socialmedia.exceptions.EmailTakenException;
import com.cooksys.socialmedia.exceptions.FollowingRelationshipException;
import com.cooksys.socialmedia.exceptions.UsernameTakenException;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final CredentialsMapper credentialsMapper;
	private final TweetMapper tweetMapper;
	private final ValidateServiceImpl validateServiceImpl;
	private final UtilityServiceImpl utilityServiceImpl;

	@Override
	public List<UserResponseDto> getUsers() {
		return userMapper.entitiesToDtos(userRepository.findAllByActiveIsTrue());
	}

	@Override
	public UserResponseDto createUser(UserRequestDto userRequestDto) {
		if (userRequestDto.getCredentials().getUsername() == null
				|| userRequestDto.getCredentials().getPassword() == null
				|| userRequestDto.getProfile().getEmail() == null) {
			throw new BadRequestException();
		}
		if (userRepository.findByCredentialsUsername(userRequestDto.getCredentials().getUsername()).isPresent()) {
			if (userRepository.findByCredentialsUsername(userRequestDto.getCredentials().getUsername()).get()
					.isActive()) {
				throw new UsernameTakenException();
			}
			User deActivatedUser = userRepository
					.findByCredentialsUsername(userRequestDto.getCredentials().getUsername()).get();
			if (userRequestDto.getProfile().getEmail().equals(deActivatedUser.getProfile().getEmail())) {
				deActivatedUser.setActive(true);
				return userMapper.entityToDto(userRepository.saveAndFlush(deActivatedUser));
			}
		}
		if (userRepository.findByProfileEmail(userRequestDto.getProfile().getEmail()).isPresent()) {
			throw new EmailTakenException();
		}

		return userMapper.entityToDto(userRepository.saveAndFlush(userMapper.dtoToEntity(userRequestDto)));
	}

	@Override
	public UserResponseDto getUser(String username) {
		User user = utilityServiceImpl.isUserPresentAndActive(username);
		return userMapper.entityToDto(user);
	}

	@Override
	public UserResponseDto updateUser(String username, UserRequestDto userRequestDto) {
		User user = utilityServiceImpl.isUserPresentAndActive(username);
		if (userRequestDto.getCredentials() == null || userRequestDto.getProfile() == null
				|| userRequestDto.getCredentials().getUsername() == null
				|| userRequestDto.getCredentials().getPassword() == null) {
			throw new BadRequestException();
		}

		if (!validateServiceImpl.authenticateUser(user, userMapper.dtoToEntity(userRequestDto).getCredentials())) {
			throw new CredentialsInvalidException();
		}

		Profile userProfile = user.getProfile();
		Profile profileChanges = userMapper.dtoToEntity(userRequestDto).getProfile();

		if (profileChanges.getFirstName() != null && !profileChanges.getFirstName().isEmpty()) {
			userProfile.setFirstName(profileChanges.getFirstName());
		}

		if (profileChanges.getLastName() != null && !profileChanges.getLastName().isEmpty()) {
			userProfile.setLastName(profileChanges.getLastName());
		}

		if (profileChanges.getEmail() != null && !profileChanges.getEmail().isEmpty()) {
			userProfile.setEmail(profileChanges.getEmail());
		}

		if (profileChanges.getPhone() != null && !profileChanges.getPhone().isEmpty()) {
			userProfile.setPhone(profileChanges.getPhone());
		}

		return userMapper.entityToDto(userRepository.saveAndFlush(user));
	}

	@Override
	public UserResponseDto deleteUser(String username, CredentialsDto credentialsDto) {
		User user = utilityServiceImpl.isUserPresentAndActive(username);
		if (credentialsDto.getUsername() == null || credentialsDto.getPassword() == null) {
			throw new BadRequestException();
		}
		if (!validateServiceImpl.authenticateUser(user, credentialsMapper.dtoToEntity(credentialsDto))) {
			throw new CredentialsInvalidException();
		}

		user.setActive(false);
		return userMapper.entityToDto(userRepository.saveAndFlush(user));
	}

	@Override
	public List<UserResponseDto> getUserFollowers(String username) {
		User user = utilityServiceImpl.isUserPresentAndActive(username);

		List<User> followers = utilityServiceImpl.clearDeletedUsers(user.getFollowers());

		return userMapper.entitiesToDtos(followers);
	}

	@Override
	public List<UserResponseDto> getUserFollowing(String username) {
		User user = utilityServiceImpl.isUserPresentAndActive(username);

		List<User> following = utilityServiceImpl.clearDeletedUsers(user.getFollowing());

		return userMapper.entitiesToDtos(following);
	}

	@Override
	public void followUser(String userToBeFollowed, CredentialsDto credentialsDto) {
		if (credentialsDto.getUsername() == null || credentialsDto.getPassword() == null) {
			throw new BadRequestException();
		}
		User followedUser = utilityServiceImpl.isUserPresentAndActive(userToBeFollowed);
		User followingUser = utilityServiceImpl
				.isUserPresentAndActive(credentialsMapper.dtoToEntity(credentialsDto).getUsername());
		if (!validateServiceImpl.authenticateUser(followingUser, credentialsMapper.dtoToEntity(credentialsDto))) {
			throw new CredentialsInvalidException();
		}

		if (followedUser.getFollowers().contains(followingUser)) {
			throw new FollowingRelationshipException();
		}

		followedUser.getFollowers().add(followingUser);
		userRepository.saveAndFlush(followedUser);
	}

	@Override
	public void unfollowUser(String userToBeUnfollowed, CredentialsDto credentialsDto) {
		if (credentialsDto.getUsername() == null || credentialsDto.getPassword() == null) {
			throw new BadRequestException();
		}
		User unfollowedUser = utilityServiceImpl.isUserPresentAndActive(userToBeUnfollowed);
		User unfollowingUser = utilityServiceImpl
				.isUserPresentAndActive(credentialsMapper.dtoToEntity(credentialsDto).getUsername());
		if (!validateServiceImpl.authenticateUser(unfollowingUser, credentialsMapper.dtoToEntity(credentialsDto))) {
			throw new CredentialsInvalidException();
		}

		if (!unfollowedUser.getFollowers().contains(unfollowingUser)) {
			throw new FollowingRelationshipException();
		}

		unfollowedUser.getFollowers().remove(unfollowingUser);
		userRepository.saveAndFlush(unfollowedUser);
	}

	public List<TweetResponseDto> getUserTweets(String username) {
		User user = utilityServiceImpl.isUserPresentAndActive(username);

		List<Tweet> userTweets = utilityServiceImpl.clearDeletedTweets(user.getTweets());
		userTweets.sort(Comparator.comparing(Tweet::getPosted).reversed());

		return tweetMapper.entitiesToDtos(userTweets);
	}

	@Override
	public List<TweetResponseDto> getUserMentions(String username) {
		User user = utilityServiceImpl.isUserPresentAndActive(username);

		List<Tweet> userMentions = utilityServiceImpl.clearDeletedTweets(user.getMentions());
		userMentions.sort(Comparator.comparing(Tweet::getPosted).reversed());

		return tweetMapper.entitiesToDtos(userMentions);
	}

	@Override
	public List<TweetResponseDto> getUserFeed(String username) {
		User user = utilityServiceImpl.isUserPresentAndActive(username);

		List<Tweet> userFeed = utilityServiceImpl.clearDeletedTweets(user.getTweets());

		for (User twit : user.getFollowing()) {
			userFeed.addAll(utilityServiceImpl.clearDeletedTweets(twit.getTweets()));
		}

		userFeed.sort(Comparator.comparing(Tweet::getPosted).reversed());

		return tweetMapper.entitiesToDtos(userFeed);
	}

}
