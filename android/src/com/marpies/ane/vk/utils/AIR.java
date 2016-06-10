/**
 * Copyright 2016 Marcel Piestansky (http://marpies.com)
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

package com.marpies.ane.vk.utils;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.marpies.ane.vk.VKExtensionContext;
import com.marpies.ane.vk.data.AIRVKEvent;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;

public class AIR {

	private static final String TAG = "VK";
	private static boolean mLogEnabled = false;

	private static VKExtensionContext mContext;
	private static VKAccessTokenTracker mTokenTracker;

	public static void log( String message ) {
		if( mLogEnabled ) {
			Log.i( TAG, message );
		}
	}

	public static void dispatchEvent( String eventName ) {
		dispatchEvent( eventName, "" );
	}

	public static void dispatchEvent( String eventName, String message ) {
		mContext.dispatchStatusEventAsync( eventName, message );
	}

	public static void startActivity( Class<?> activityClass, Bundle extras ) {
		Intent intent = new Intent( mContext.getActivity().getApplicationContext(), activityClass );
		ResolveInfo info = mContext.getActivity().getPackageManager().resolveActivity( intent, 0 );
		if( info == null ) {
			log( "Activity " + activityClass.getSimpleName() + " could not be started. Make sure you specified the activity in the android manifest." );
			return;
		}
		if( extras != null ) {
			intent.putExtras( extras );
		}
		mContext.getActivity().startActivity( intent );
	}

	public static void notifyTokenChange( VKAccessToken newToken ) {
		dispatchTokenChange( null, newToken );
	}

	public static void startAccessTokenTracker() {
		if( mTokenTracker == null ) {
			mTokenTracker = new VKAccessTokenTracker() {
				@Override
				public void onVKAccessTokenChanged( @Nullable VKAccessToken oldToken, @Nullable VKAccessToken newToken ) {
					dispatchTokenChange( oldToken, newToken );
				}
			};
			mTokenTracker.startTracking();
		}
	}

	public static void stopAccessTokenTracker() {
		if( mTokenTracker != null ) {
			mTokenTracker.stopTracking();
			mTokenTracker = null;
		}
	}

	private static void dispatchTokenChange( @Nullable VKAccessToken oldToken, @Nullable VKAccessToken newToken ) {
		AIR.log( "VKAccessTokenTracker::onVKAccessTokenChanged() new: " + newToken + " old: " + oldToken );
		String tokenJSON = (newToken == null) ? "{}" : VKAccessTokenUtils.toJSON( newToken );
		AIR.dispatchEvent( AIRVKEvent.VK_TOKEN_UPDATE, tokenJSON );
	}

	/**
	 *
	 *
	 * Getters / Setters
	 *
	 *
	 */

	public static VKExtensionContext getContext() {
		return mContext;
	}
	public static void setContext( VKExtensionContext context ) {
		mContext = context;
	}

	public static Boolean getLogEnabled() {
		return mLogEnabled;
	}
	public static void setLogEnabled( Boolean value ) {
		mLogEnabled = value;
	}
	
}
