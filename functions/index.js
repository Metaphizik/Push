//import firebase functions modules
const functions = require('firebase-functions');
//import admin module
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// Listens for new notifications added to notifications/:pushId
exports.pushNotification = functions.database.ref('/notifications/{pushId}').onWrite( event => {

    console.log('Push notification event triggered');

    var valueObject = event.data.val();
        console.log(event.data.key);
        var tokens = Object.keys(valueObject.to);
        console.log(tokens);


  // Create a notification
    const payload = {
        data: {
            title: valueObject.author,
            text: valueObject.text,
            author: valueObject.author,
            date: valueObject.date,
            sound: "default"
        },
    };
console.log("payload", payload);
  //Create an options object that contains the time to live for the notification and the priority
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };

    return admin.messaging().sendToDevice(tokens, payload, options);
});

