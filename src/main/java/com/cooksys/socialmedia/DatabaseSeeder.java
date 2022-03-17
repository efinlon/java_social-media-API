package com.cooksys.socialmedia;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.cooksys.socialmedia.entities.Credentials;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Profile;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

	private HashtagRepository hashtagRepository;
	private TweetRepository tweetRepository;
	private UserRepository userRepository;

	@Override
	public void run(String... args) throws Exception {

		Hashtag hashtag = new Hashtag();
		hashtag.setLabel("testing");
		Hashtag h2 = new Hashtag();
		h2.setLabel("test2");
		Hashtag h3 = new Hashtag();
		h3.setLabel("no!");

		Tweet tweet1 = new Tweet();
		tweet1.setContent("This is my first tweet.");
		List<Hashtag> hashtags = new ArrayList();
		hashtags.add(hashtag);
		hashtags.add(h2);
		tweet1.setHashtags(hashtags);

		Tweet tweet2 = new Tweet();
		tweet2.setContent("This is my second tweet.");
		List<Hashtag> hashtags2 = new ArrayList<Hashtag>();
		hashtags2.add(h2);
		tweet2.setHashtags(hashtags2);

		hashtagRepository.saveAndFlush(hashtag);
		hashtagRepository.saveAndFlush(h2);
		hashtagRepository.saveAndFlush(h3);
		tweetRepository.saveAndFlush(tweet1);
		tweetRepository.saveAndFlush(tweet2);

		User userDD = new User();
		userDD.setActive(true);
		Credentials userDDCredentials = new Credentials();
		Profile userDDProfile = new Profile();
		userDDCredentials.setUsername("codingGuru");
		userDDCredentials.setPassword("password");
		userDDProfile.setFirstName("Dorian");
		userDDProfile.setLastName("Develops");
		userDDProfile.setEmail("codingrocks@hotmale.com");
		userDDProfile.setPhone("901-867-5309");
		userDD.setCredentials(userDDCredentials);
		userDD.setProfile(userDDProfile);
		userDD.setJoined(new Timestamp(System.currentTimeMillis()));

		userRepository.saveAndFlush(userDD);

		List<User> DDFollowing = new ArrayList<User>();
		List<User> DDFollowers = new ArrayList<User>();

		User userHG = new User();
		userHG.setActive(true);
		Credentials userHGCredentials = new Credentials();
		Profile userHGProfile = new Profile();
		userHGCredentials.setUsername("herminia");
		userHGCredentials.setPassword("spew");
		userHGProfile.setFirstName("Hermione");
		userHGProfile.setLastName("Granger");
		userHGProfile.setEmail("mugglebornproud@wizardy.com");
		userHGProfile.setPhone("901-579-5239");
		userHG.setCredentials(userHGCredentials);
		userHG.setProfile(userHGProfile);
		userHG.setJoined(new Timestamp(System.currentTimeMillis()));

		userRepository.saveAndFlush(userHG);

		List<User> HGFollowing = new ArrayList<User>();
		List<User> HGFollowers = new ArrayList<User>();

		User userHP = new User();
		userHP.setActive(true);
		Credentials userHPCredentials = new Credentials();
		Profile userHPProfile = new Profile();
		userHPCredentials.setUsername("theBoyWhoLived");
		userHPCredentials.setPassword("hedwig");
		userHPProfile.setFirstName("Harry");
		userHPProfile.setLastName("Potter");
		userHPProfile.setEmail("harry.potter@hogwarts.edu");
		userHPProfile.setPhone("901-555-1234");
		userHP.setCredentials(userHPCredentials);
		userHP.setProfile(userHPProfile);
		userHP.setJoined(new Timestamp(System.currentTimeMillis()));
		userRepository.saveAndFlush(userHP);

		List<User> HPFollowing = new ArrayList<User>();
		List<User> HPFollowers = new ArrayList<User>();

		userRepository.save(userDD);
		userRepository.save(userHG);
		userRepository.save(userHP);

		HGFollowers.add(userDD);
		HPFollowers.add(userDD);

		HPFollowers.add(userHG);

		DDFollowers.add(userHP);
		HGFollowers.add(userHP);

		userDD.setFollowers(DDFollowers);
		userHG.setFollowers(HGFollowers);
		userHP.setFollowers(HPFollowers);

		userRepository.saveAndFlush(userDD);
		userRepository.saveAndFlush(userHG);
		userRepository.saveAndFlush(userHP);

		tweet1.setAuthor(userDD);
		tweetRepository.saveAndFlush(tweet1);
	}

}
