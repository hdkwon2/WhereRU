/*
* 
* Copyright (C) 2012 Hyuk Don Kwon
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package edu.illinois.whereru;

/**
 * 
 * Class representing the profile of a user.
 * @author don
 *
 */
public class Profile {
	
	private final String id;
	private String nickname;

	public Profile(String id, String nickname){
		this.id = id;
		this.nickname = nickname;
	}
	
	/**
	 * Returns the unique id representing the user
	 * @return the id of the user
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * Returns the current nickname of the user
	 * @return the nickname of the user
	 */
	public String getNickname(){
		return nickname;
	}
	
	/**
	 * Sets the nickname of the user
	 * @param newNickname a new nickname for the user
	 */
	public void setNickName(String newNickname){
		this.nickname = newNickname;
	}
	
	@Override
	public boolean equals(Object profile){
		return this.id.equals(((Profile)profile).getId());
	}
	
	@Override
	public String toString(){
		return id+", "+nickname;
	}
}
