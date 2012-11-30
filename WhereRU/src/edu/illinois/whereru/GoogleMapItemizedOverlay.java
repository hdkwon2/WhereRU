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

import java.util.ArrayList;
import java.util.Comparator;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

//TODO if two points are too close replace the one that is close to the new point
public class GoogleMapItemizedOverlay extends ItemizedOverlay<OverlayItem>{
	
	static class OverlayComparator implements Comparator<OverlayItem>{

		public int compare(OverlayItem lhs, OverlayItem rhs) {
			return lhs.getSnippet().compareTo(rhs.getSnippet());
		}
			
	}
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private final int capacity; 
	
	public GoogleMapItemizedOverlay(Drawable defaultMarker, int capacity) {
		//center bottom of the image is the marker location.
		super(boundCenterBottom(defaultMarker));
		this.capacity = capacity;
	}

	/**
	 * Adds a new overlay item, keeping the list sorted by timestamp of the item.
	 * Keeping the list sorted makes it easier to change the number of markers to be shown.
	 * First x markers are the latest x markers 
	 * 
	 * @param item a new marker to be added
	 */
	public void addOverlay(OverlayItem item){
		OverlayComparator comparator = new OverlayComparator();
		int index = 0;
		for(int i=0; i < mOverlays.size(); i++){
			if(comparator.compare(item, mOverlays.get(i)) > 0){
				index = i;
				break;
			}
		}
		mOverlays.add(index, item);
		// If the size gets larger than the capacity, trim it down
		if(mOverlays.size() > capacity){
			mOverlays.remove(capacity);
		}
		setLastFocusedIndex(-1);
		populate();
	}
	
	/**
	 * Sets the drawable of OverlayItem.
	 * @param marker new drawable to be used
	 */
	public void setMarker(Drawable marker){
		
		// Need to set the bound, otherwise doesn't show up
		marker.setBounds(0, 0,marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		
		for(OverlayItem item : mOverlays){
			item.setMarker(marker);
		}
		
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	
	
}
