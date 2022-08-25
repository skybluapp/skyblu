# skyblu
This is a app for skydivers to track and upload their skydiving data to a Firebase backend
Tracking is done using a barometric pressure sensor and GPS on device, although GPS has shown to be unreliable on some devices when travelling in the plane.

This project is still a work in progress, but demonstrates skills in MVVM architecture, Frontend development with Jetpack compose, Firebase integration and Dependency injection.

To use this app, a Google Maps API key must be provided. This should be put in the local.properties file as follows:
    GOOGLE_MAPS_API_KEY = [Your Google Maps Api Key]
    
A google play services JSON file containing a Firebase API key is also required forfirebase integration. The app will not run properly without this! 

Thank you for taking the time to check out this project :) 
