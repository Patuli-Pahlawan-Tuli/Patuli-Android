
# Patuli Mobile App Documentation

- ## About
    Patuli (Pahlawan Tuli) is an application designed to serve as a platform for learning and practicing Bisindo sign language. Our aim is to help users who are interested in learning sign language to develop a better understanding and eventually communicate with the deaf and hard of hearing community. 
   
    We hope that our application will empower users to connect with and support individuals with hearing impairments.

- ### Features
    * Auto update model for sign language detection
    * Object detection for sign language using front and back Camera
    * Exercises to hone sign language skills with the help of camera detection
    * Provides a wide variety of sign language detection models, ranging from alphabets, numbers to words.
    * Provides UI to accommodate camera translation results, so you won't forget the results that have been generated by our AI.
    * Provides gamification that makes users more excited to spell and practice sign language.
    * edit profile according to your preference

 - ### Application Screenshots
   <img src="https://github.com/Patuli-Pahlawan-Tuli/Patuli-Android/assets/61315456/daca1e69-f6ed-4dd5-9ecc-e1d6e513cc40" width="300" />
   <img src="https://github.com/Patuli-Pahlawan-Tuli/Patuli-Android/assets/61315456/f1cb79d5-a1b1-4955-adb5-ffb96d1193c9" width="300" />
   <img src="https://github.com/Patuli-Pahlawan-Tuli/Patuli-Android/assets/61315456/1062c07b-33fc-4f6b-b992-488360dfad75" width="300" />
   <img src="https://github.com/Patuli-Pahlawan-Tuli/Patuli-Android/assets/61315456/9af0ab8d-06bb-42c9-a5df-ef01d2d262af" width="300" />
   <img src="https://github.com/Patuli-Pahlawan-Tuli/Patuli-Android/assets/61315456/b972a136-ac8b-4446-a4d5-7b93b36f9140" width="300" />
   <img src="https://github.com/Patuli-Pahlawan-Tuli/Patuli-Android/assets/61315456/ec458c05-fcbb-4d49-a822-be02aacd62cd" width="300" />

- ### Video Demonstration
  [Link To App demonstration](https://www.youtube.com/watch?v=EL9E-cWJXW0)
   
   


- ### Installation
    - Clone this repository
    - After that you need to place this URL list inside the local.properties project file.
    ``` 
    api.url ="https://master-dot-patuli-project.et.r.appspot.com/api/v1/"
    abjad.url = "https://storage.googleapis.com/patuli-storage/ML-Model/abjad.tflite"
    angka.url = "https://storage.googleapis.com/patuli-storage/ML-Model/angka.tflite"
    kata.url = "https://storage.googleapis.com/patuli-storage/ML-Model/kata.tflite"
    abjadlite.url = "https://storage.googleapis.com/patuli-storage/ML-Model/abjad_lite.tflite"
    angkalite.url = "https://storage.googleapis.com/patuli-storage/ML-Model/angka_lite.tflite"
    katalite.url = "https://storage.googleapis.com/patuli-storage/ML-Model/kata_lite.tflite"
    ```


## Dependencies
- [Android ktx](https://developer.android.com/kotlin/coroutines) 
- [Lifecycle & Livedata](https://developer.android.com/jetpack/androidx/releases/lifecycle)
- [Material Design 3](https://github.com/material-components) 
- [Coroutines](https://developer.android.com/kotlin/coroutines) 
- [TFlite](https://github.com/tensorflow/examples/tree/master/lite/examples/object_detection/android) 
- [Koin Dependencies Injection](https://insert-koin.io/)    
- [Datastore](https://developer.android.com/kotlin/coroutines) 
- [CameraX](https://developer.android.com/training/camerax) 
- [Chucker](https://github.com/ChuckerTeam/chucker) 
- [Retrofit 2](https://square.github.io/retrofit/)    
- [Glide](https://github.com/bumptech/glide)      
- [Ok Http 3](https://square.github.io/okhttp/) 
- [Air Bnb Lottie](https://airbnb.io/lottie/#/) 

## Tools
- Android Studio Electric Eel | 2022.1.1 Patch 2
- Postman
- Figma


