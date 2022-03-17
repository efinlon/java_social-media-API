package com.cooksys.socialmedia.services.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.CredentialsInvalidException;
import com.cooksys.socialmedia.exceptions.TweetNotFoundException;
import com.cooksys.socialmedia.exceptions.UserNotFoundException;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final CredentialsMapper credentialsMapper;
	private final HashtagRepository hashtagRepository;
	private final HashtagMapper hashtagMapper;
	private final ValidateServiceImpl validateServiceImpl;
	private final UtilityServiceImpl utilityServiceImpl;

	private void setHashtagsList(String str, Tweet tweet) {
		List<Hashtag> hashtags = new ArrayList<>();

		String regEx = ".*?#(\\w+).*?";
		Pattern tagMatcher = Pattern.compile(regEx);
		Matcher m = tagMatcher.matcher(str);
		while (m.find()) {
			String tag = m.group(1);
			if (!validateServiceImpl.hashtagExists(tag)) {
				Hashtag hashtag = new Hashtag();
				hashtag.setLabel(tag);
				hashtagRepository.saveAndFlush(hashtag);
			}
			hashtags.add(hashtagRepository.getByLabelIgnoreCase(tag));
			hashtagRepository.getByLabelIgnoreCase(tag).setLastUsed(new Timestamp(System.currentTimeMillis()));
			hashtagRepository.saveAndFlush(hashtagRepository.getByLabelIgnoreCase(tag));
		}
		tweet.setHashtags(hashtags);
	}

	private void setMentionedUsers(String str, Tweet tweet) {
		List<User> mentionedUsers = new ArrayList<>();

		String regEx = ".*?@(\\w+).*?";
		Pattern tagMatcher = Pattern.compile(regEx);
		Matcher m = tagMatcher.matcher(str);
		while (m.find()) {
			String mention = m.group(1);
			if (validateServiceImpl.usernameExistsAndIsActive(mention)) {
				mentionedUsers.add(userRepository.findByCredentialsUsername(mention).get());
			}
		}
		tweet.setMentionedUsers(mentionedUsers);
	}

	private List<Tweet> getReplyChain(Tweet tweet) {
		List<Tweet> result = tweet.getReplies();
		List<Tweet> resultBuilder = new ArrayList<Tweet>();
		for (Tweet t : result)
			if (t.getReplies().size() > 0)
				resultBuilder.addAll(getReplyChain(t));
		result.addAll(resultBuilder);
		return result;
	}

	@Override
	public List<TweetResponseDto> getTweets() {
		return tweetMapper.entitiesToDtos(tweetRepository.findByActiveIsTrueOrderByPosted());
	}

	@Override
	public TweetResponseDto getTweetById(Long id) {
		Tweet tweet = utilityServiceImpl.isTweetPresentAndActive(id);
		return tweetMapper.entityToDto(tweet);
	}

	@Override
	public TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto) {
		if (credentialsDto == null || credentialsDto.getUsername() == null || credentialsDto.getPassword() == null) {
			throw new BadRequestException();
		}
		Tweet tweetToDelete = utilityServiceImpl.isTweetPresentAndActive(id);
		utilityServiceImpl.isUserPresentAndActive(credentialsMapper.dtoToEntity(credentialsDto).getUsername());
		if (!validateServiceImpl.authenticateUser(tweetToDelete.getAuthor(),
				credentialsMapper.dtoToEntity(credentialsDto))) {
			throw new CredentialsInvalidException();
		}
		tweetToDelete.setActive(false);
		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(tweetToDelete));
	}

	@Override
	public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
		if (tweetRequestDto.getCredentialsDto() == null || tweetRequestDto.getCredentialsDto().getUsername() == null
				|| tweetRequestDto.getCredentialsDto().getPassword() == null || tweetRequestDto.getContent() == null) {
			throw new BadRequestException();
		}
		if (!validateServiceImpl.usernameExistsAndIsActive(tweetRequestDto.getCredentialsDto().getUsername())) {
			throw new UserNotFoundException();
		}
		if (!validateServiceImpl.authenticateUser(
				userRepository.findByCredentialsUsername(tweetRequestDto.getCredentialsDto().getUsername()).get(),
				credentialsMapper.dtoToEntity(tweetRequestDto.getCredentialsDto()))) {
			throw new CredentialsInvalidException();
		}

		Tweet tweetToCreate = tweetMapper.dtoToEntity(tweetRequestDto);
		utilityServiceImpl.isUserPresentAndActive(tweetRequestDto.getCredentialsDto().getUsername());

		setHashtagsList(tweetToCreate.getContent(), tweetToCreate);
		setMentionedUsers(tweetToCreate.getContent(), tweetToCreate);

		tweetToCreate.setAuthor(
				userRepository.findByCredentialsUsername(tweetRequestDto.getCredentialsDto().getUsername()).get());

		User author = tweetToCreate.getAuthor();
		tweetRepository.saveAndFlush(tweetToCreate);
		TweetResponseDto tweetResponse = tweetMapper.entityToDto(tweetToCreate);
		tweetResponse.setAuthor(userMapper.entityToDto(author));
		return tweetResponse;

	}

	@Override
	public TweetResponseDto postReply(Long id, TweetRequestDto tweetRequestDto) {
		if (tweetRequestDto.getCredentialsDto() == null || tweetRequestDto.getCredentialsDto().getUsername() == null
				|| tweetRequestDto.getCredentialsDto().getPassword() == null || tweetRequestDto.getContent() == null) {
			throw new BadRequestException();
		}
		if (!validateServiceImpl.tweetExistsAndIsActive(id)) {
			throw new TweetNotFoundException();
		}
		if (!validateServiceImpl.usernameExistsAndIsActive(tweetRequestDto.getCredentialsDto().getUsername())) {
			throw new UserNotFoundException();
		}
		if (!validateServiceImpl.authenticateUser(
				userRepository.findByCredentialsUsername(tweetRequestDto.getCredentialsDto().getUsername()).get(),
				credentialsMapper.dtoToEntity(tweetRequestDto.getCredentialsDto()))) {
			throw new CredentialsInvalidException();
		}

		Tweet replyToCreate = tweetMapper.dtoToEntity(tweetRequestDto);
		setHashtagsList(replyToCreate.getContent(), replyToCreate);
		setMentionedUsers(replyToCreate.getContent(), replyToCreate);

		replyToCreate.setAuthor(
				userRepository.findByCredentialsUsername(tweetRequestDto.getCredentialsDto().getUsername()).get());
		replyToCreate.setInReplyTo(tweetRepository.getOne(id));
		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(replyToCreate));
	}

	@Override
	public ContextDto getContext(Long id) {
		ContextDto result = new ContextDto();
		Tweet tweet = utilityServiceImpl.isTweetPresentAndActive(id);

		List<Tweet> before = new ArrayList<Tweet>();
		while (tweet.getInReplyTo() != null) {
			before.add(tweet.getInReplyTo());
			tweet = tweet.getInReplyTo();
		}
		before = utilityServiceImpl.clearDeletedTweets(before);
		before.sort(Comparator.comparing(Tweet::getPosted));

		tweet = utilityServiceImpl.isTweetPresentAndActive(id);

		List<Tweet> after = getReplyChain(tweet);
		after = utilityServiceImpl.clearDeletedTweets(after);
		after.sort(Comparator.comparing(Tweet::getPosted));

		result.setTarget(tweetMapper.entityToDto(tweet));
		result.setBefore(tweetMapper.entitiesToDtos(before));
		result.setAfter(tweetMapper.entitiesToDtos(after));

		return result;
	}

	@Override
	public List<TweetResponseDto> getTweetReposts(Long id) {
		Tweet tweet = utilityServiceImpl.isTweetPresentAndActive(id);

		List<Tweet> repostList = tweet.getReposts();
		repostList = utilityServiceImpl.clearDeletedTweets(repostList);

		return tweetMapper.entitiesToDtos(repostList);
	}

	@Override
	public void likeTweet(Long id, CredentialsDto credentialsDto) {
		Tweet tweet = utilityServiceImpl.isTweetPresentAndActive(id);
		User user = utilityServiceImpl.isUserPresentAndActive(credentialsDto.getUsername());
		if (!validateServiceImpl.authenticateUser(
				userRepository.findByCredentialsUsername(credentialsDto.getUsername()).get(),
				credentialsMapper.dtoToEntity(credentialsDto))) {
			throw new CredentialsInvalidException();
		}
		user.getLikedTweets().add(tweet);
		userRepository.saveAndFlush(user);
	}

	@Override
	public List<HashtagDto> getTags(Long id) {
		Tweet tweet = utilityServiceImpl.isTweetPresentAndActive(id);
		return hashtagMapper.entitiesToDtos(tweet.getHashtags());
	}

	@Override
	public List<UserResponseDto> getMentions(Long id) {
		Tweet tweet = utilityServiceImpl.isTweetPresentAndActive(id);

		List<User> mentionedUsers = tweet.getMentionedUsers();
		mentionedUsers = utilityServiceImpl.clearDeletedUsers(mentionedUsers);

		return userMapper.entitiesToDtos(mentionedUsers);
	}

	@Override
	public List<UserResponseDto> getUsersWhoLiked(Long id) {
		Tweet tweet = utilityServiceImpl.isTweetPresentAndActive(id);

		List<User> userLiked = tweet.getUsersWhoLike();
		userLiked = utilityServiceImpl.clearDeletedUsers(userLiked);

		return userMapper.entitiesToDtos(userLiked);
	}

	@Override
	public List<TweetResponseDto> getReplies(Long id) {
		Tweet tweet = utilityServiceImpl.isTweetPresentAndActive(id);
		List<Tweet> replyList = tweet.getReplies();

		replyList = utilityServiceImpl.clearDeletedTweets(replyList);
		return tweetMapper.entitiesToDtos(replyList);
	}

	@Override
	public TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto) {
		Tweet tweet = utilityServiceImpl.isTweetPresentAndActive(id);
		User user = utilityServiceImpl
				.isUserPresentAndActive(credentialsMapper.dtoToEntity(credentialsDto).getUsername());
		if (!validateServiceImpl.authenticateUser(
				userRepository.findByCredentialsUsername(credentialsDto.getUsername()).get(),
				credentialsMapper.dtoToEntity(credentialsDto))) {
			throw new CredentialsInvalidException();
		}
		Tweet retweet = new Tweet();
		retweet.setAuthor(user);
		retweet.setRepostOf(tweet);

		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(retweet));
	}

}
