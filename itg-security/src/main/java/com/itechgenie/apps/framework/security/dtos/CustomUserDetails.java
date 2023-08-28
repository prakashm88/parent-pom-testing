package com.itechgenie.apps.framework.security.dtos;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MultiValueMap;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1000101001L;

	public CustomUserDetails(String username, Collection<? extends GrantedAuthority> authorities, 
			String sessionId, String clientId,
			MultiValueMap<String, Object> requestHeaders) {
		super();
		this.username = username;
		this.authorities = authorities;
		this.sessionId = sessionId;
		this.clientId = clientId;
		this.requestHeaders = requestHeaders;
	}

	private MultiValueMap<String, Object> requestHeaders;
	private String sessionId;
	private String clientId;

	/**
	 * Returns the authorities granted to the user. Cannot return <code>null</code>.
	 * 
	 * @return the authorities, sorted by natural key (never <code>null</code>)
	 */
	Collection<? extends GrantedAuthority> authorities;

	/**
	 * Returns the password used to authenticate the user.
	 * 
	 * @return the password
	 */
	String password;

	/**
	 * Returns the username used to authenticate the user. Cannot return
	 * <code>null</code>.
	 * 
	 * @return the username (never <code>null</code>)
	 */
	String username;

	/**
	 * Indicates whether the user's account has expired. An expired account cannot
	 * be authenticated.
	 * 
	 * @return <code>true</code> if the user's account is valid (ie non-expired),
	 *         <code>false</code> if no longer valid (ie expired)
	 */
	boolean accountNonExpired;

	/**
	 * Indicates whether the user is locked or unlocked. A locked user cannot be
	 * authenticated.
	 * 
	 * @return <code>true</code> if the user is not locked, <code>false</code>
	 *         otherwise
	 */
	boolean accountNonLocked;

	/**
	 * Indicates whether the user's credentials (password) has expired. Expired
	 * credentials prevent authentication.
	 * 
	 * @return <code>true</code> if the user's credentials are valid (ie
	 *         non-expired), <code>false</code> if no longer valid (ie expired)
	 */
	boolean credentialsNonExpired;

	/**
	 * Indicates whether the user is enabled or disabled. A disabled user cannot be
	 * authenticated.
	 * 
	 * @return <code>true</code> if the user is enabled, <code>false</code>
	 *         otherwise
	 */
	boolean enabled;

}
