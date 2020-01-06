# Popular Movie (Stage 2)

## Overview
This is a project of Udacity Android Developer Nanodegree. The project is divided into 2 stages and this is the second stage of the project.
The app has functionality to show the users the most popular movies at the moment. this app functionalities have been improved from the previous app built of stage 1.

* Users can watch the trailers of the movie (link to youtube app or mobile web browser).
* The app provieds review of selected movie.
* "Add to favorite" button is added into detail activity and user can store their favorite movies locally on the phone.
* Another additional display choice was added to display movie list in the main activity (Favorite).
* The app utilizes the Room presistance library with LiveData for favorited movie.


## Running the app
This app uses the [Movie Database API](https://developers.themoviedb.org/). The API key needs to be obtianed from movieDB website and put in the build.gradel(Modul: app)

```gradle
buildTypes.each {
        it.buildConfigField 'String', 'THE_MOVIEDB_API_KEY', '"YOUR API KEY"'
    }
 ```
 
## App Preview

<p align="center">
 <img src="/images/mainactivity.png" width="40%"> <img src="/images/detail_info.png" width="40%">
  <img src="/images/link_trailer.png" width="40%"> <img src="/images/displayorder.png" width="40%">
</p>

