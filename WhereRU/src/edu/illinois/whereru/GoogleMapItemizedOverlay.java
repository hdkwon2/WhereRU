package edu.illinois.whereru;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

//TODO if two points are too close replace the one that is close to the new point
public class GoogleMapItemizedOverlay extends ItemizedOverlay{
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private final int mCapacity;
	private int index; 
	
	public GoogleMapItemizedOverlay(Drawable defaultMarker, Context context, int capacity) {
		//center bottom of the image is the marker location.
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		mCapacity = capacity;
		index = 0;
	}

	public void addOverlay(OverlayItem overlay){
		if(mOverlays.size() >= mCapacity){
			mOverlays.set(index, overlay);
			index = ++index % mCapacity;
		}
		else mOverlays.add(overlay);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	//TODO make a balloon that shows the time and user 
	@Override
	protected boolean onTap(int index){
		OverlayItem item = mOverlays.get(index);
		
		return true;
	}
	
}
