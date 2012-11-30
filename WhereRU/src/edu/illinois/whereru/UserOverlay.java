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

import android.content.Context;

import com.google.android.maps.OverlayItem;

public class UserOverlay extends GoogleMapItemizedOverlay{

	public UserOverlay(Context context) {
		super(context.getResources().getDrawable(
				R.drawable.ic_default_user_marker), 1);		
	}
	
	public void setOverlay(OverlayItem overlay){
		addOverlay(overlay);
	}
	
	@Override
	public int size(){
		if(1 < super.size())
			return 1;
		else return super.size();
	}
}
