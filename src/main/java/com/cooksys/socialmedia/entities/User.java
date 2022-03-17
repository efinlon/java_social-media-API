package com.cooksys.socialmedia.entities;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_table")
@NoArgsConstructor
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "is_active", nullable = false)
	@ColumnDefault("true")
	private boolean active = true;

	@Embedded
	private Credentials credentials;

	@Embedded
	private Profile profile;

	@CreationTimestamp
	private Timestamp joined;

	@OneToMany(mappedBy = "author")
	private List<Tweet> tweets;

	@ManyToMany(mappedBy = "mentionedUsers")
	private List<Tweet> mentions;

	@ManyToMany
	@JoinColumn
	private List<User> followers;

	@ManyToMany(mappedBy = "followers")
	private List<User> following;

	@ManyToMany
	@JoinColumn
	private List<Tweet> likedTweets;

}
