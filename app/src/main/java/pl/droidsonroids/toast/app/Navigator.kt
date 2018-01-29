package pl.droidsonroids.toast.app

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import pl.droidsonroids.toast.R
import pl.droidsonroids.toast.app.events.EventDetailsActivity
import pl.droidsonroids.toast.app.events.TalkDetailsActivity
import pl.droidsonroids.toast.app.photos.PhotosActivity
import pl.droidsonroids.toast.app.photos.PhotosViewerActivity
import pl.droidsonroids.toast.app.speakers.SpeakerDetailsActivity
import pl.droidsonroids.toast.app.speakers.SpeakersSearchActivity
import pl.droidsonroids.toast.app.utils.extensions.disableActivityTransitionAnimations
import pl.droidsonroids.toast.utils.NavigationRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor() {

    fun dispatch(context: Context, navigationRequest: NavigationRequest) {
        when (navigationRequest) {
            is NavigationRequest.SpeakerDetails -> showSpeakerDetails(context, navigationRequest)
            is NavigationRequest.EventDetails -> showEventDetails(context, navigationRequest)
            is NavigationRequest.Photos -> showPhotos(context, navigationRequest)
            is NavigationRequest.Map -> showMap(context, navigationRequest)
        }
    }

    private fun showMap(context: Context, navigationRequest: NavigationRequest.Map) {
        with(navigationRequest) {
            val locationUri = Uri.parse("geo:0,0?q=${coordinatesDto.latitude},${coordinatesDto.longitude}($placeName)")
            val intent = Intent(Intent.ACTION_VIEW).setData(locationUri)
            try {
                context.startActivity(intent)
            } catch (exception: ActivityNotFoundException) {
                Toast.makeText(context, R.string.no_map_app_found, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showSinglePhotoWithSharedAnimation(activity: AppCompatActivity, navigationRequest: NavigationRequest.SinglePhoto, sharedViews: Array<Pair<View, String>>) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *sharedViews).toBundle()
        val intent = PhotosViewerActivity.createIntent(activity, navigationRequest)
        activity.startActivity(intent, options)
    }

    fun showTalkDetailsWithSharedAnimation(activity: AppCompatActivity, navigationRequest: NavigationRequest.EventTalkDetails, sharedViews: Array<Pair<View, String>>) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *sharedViews).toBundle()
        val intent = TalkDetailsActivity.createIntent(activity, navigationRequest)
        activity.startActivity(intent, options)
    }

    fun showSearchSpeakersWithRevealAnimation(activity: Activity, centerCoordinates: kotlin.Pair<Int, Int>) {
        val intent = SpeakersSearchActivity.createIntent(
                activity,
                revealCenterX = centerCoordinates.first,
                revealCenterY = centerCoordinates.second)

        activity.startActivity(intent)
        activity.disableActivityTransitionAnimations()
    }

    private fun showEventDetails(context: Context, navigationRequest: NavigationRequest.EventDetails) {
        val intent = EventDetailsActivity.createIntent(context, navigationRequest)
        context.startActivity(intent)
    }

    private fun showSpeakerDetails(context: Context, navigationRequest: NavigationRequest.SpeakerDetails) {
        val intent = SpeakerDetailsActivity.createIntent(context, navigationRequest)
        context.startActivity(intent)
    }

    private fun showPhotos(context: Context, navigationRequest: NavigationRequest.Photos) {
        val intent = PhotosActivity.createIntent(context, navigationRequest)
        context.startActivity(intent)
    }
}