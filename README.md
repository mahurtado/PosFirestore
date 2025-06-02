# Point Of Sale with Firestore Demo

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Firebase](https://img.shields.io/badge/firebase-ffca28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=Android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=Kotlin&logoColor=white)](https://kotlinlang.org/)
[![Firestore](https://img.shields.io/badge/Firestore_Database-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/docs/firestore)

This demo shows an implementation of a POS (Point-Of-Sale) device using Firestore as database and Android Kotlin as client device. The application is deployed in a Firebase project.

![Example graph](img/architecture.jpg?raw=true)

The functionality implemented is:

* Authenticated access of users (merchants)
* Show configuration information for each merchant.
* Run credit card operations and save into Firestore
* Simulate errors in transactions and save into Firestore
* Search past operations: last number of operations and range of dates
* Run the device in disconnected mode. Run card transactions disconnected, connect again and see how transactions are synchronized with the server.

# Build

## Prerequisites

* Google Cloud Project created with a billing account 
* Android Studio
* Google Cloud CLI tool (gcloud)
* gzip


## Environment

Open a terminal session in your local environment (not Cloudshell). Run the following, using your own values for the PROJECT and REGION.

```
PROJECT=[YOUR_VALUE_HERE]
REGION=[YOUR_VALUE_HERE]
EXPORT_NAME=2025-02-17T12:54:57_49581

gcloud config set project $PROJECT
PROJECT_ID=$(gcloud projects describe $PROJECT --format="value(projectId)")
gcloud auth application-default set-quota-project $PROJECT_ID
```

## Download repo

```
git clone https://github.com/mahurtado/PosFirestore
```

## Create Firestore database and import data

```
gcloud services enable firestore.googleapis.com
gcloud firestore databases create --location=$REGION
cd PosFirestore/dbexport/
gunzip dbexport.tar.gz
tar xvf dbexport.tar
gcloud storage buckets create gs://$PROJECT --location=$REGION
gcloud storage cp $EXPORT_NAME gs://$PROJECT --recursive 
gcloud firestore import gs://$PROJECT/$EXPORT_NAME

```

Two composite indexes are required:

```
gcloud firestore indexes composite create --collection-group=tx --field-config=field-path=merchantId,order=ascending --field-config=field-path=date,order=descending
gcloud firestore indexes composite create --collection-group=errors --field-config=field-path=merchantId,order=ascending --field-config=field-path=date,order=descending
```

## Firebase setup

Access to the Firebase console: https://console.firebase.google.com/
Select “Create a project“, then “Add Firebase to Google Cloud project”

Select the Google Cloud project already created, then click “Continue”, select Blaze pricing plan click "Confirm and continue”, choose default options,  disable Google Cloud Analytics and click “Add Firebase”
 
The next step is registering an Android App. Click in the Android logo 

Select “com.example.pos” for package name, and “Point Of Sale” as nickname. Then click “Register App”

In the next screen, click “Download google-services.json”. Save the file.

![Checkpoint](img/add_firebase.jpg?raw=true)

Move the file google-services.json to the app downloaded from github. Place it in the directory:
PosFirestore/POS/app/.

Click “Next” in the next screens.

Now we will configure Firebase authentication. Click on "Authentication", then “Get Started”.
Select “Email/Password” provider. Then click “Enable” and “Save”

Now click “Users” and add three users:

user1@mycompany.com
user2@mycompany.com
user3@mycompany.com 

Save the chosen password.

<img src="img/authentication.jpg?raw=true" width="200"/>

![Checkpoint](img/authentication.jpg?raw=true)

## Set security rules on Firestore

Open the Firebase console 
https://console.firebase.google.com/

Select the Firestore database, then “Rules” . Update with this text:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```
Click “Publish”. This rule permits database access to authenticated users only.

## Android Project

Run Android Studio. Open the project on PosFirestore/POS. Click on “Trust Project”

Click “Run App”. The emulator starts and loads the app. [Note: you may need to add an Android device].

*Checkpoint*: 

![Checkpoint](img/client.jpg?raw=true)

# Contributing
Pull requests are welcome. 

## License

Apache License 2.0. See the [LICENSE](LICENSE.txt) file.