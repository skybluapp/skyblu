import * as admin  from "firebase-admin";
import * as functions from "firebase-functions";


admin.initializeApp()
export const helloWorld = functions.https.onRequest((request, response) => {
  console.log("Hello from Firebase")
  response.send("Hello from Firebase!");
});

exports.deleteUsersSkydives = functions.auth.user().onDelete((user) => {
    const db = admin.firestore();
    return db.collection("skydives").where("userID", "==", user.uid).get().then(result => {
        result.forEach((doc) => {
            db.collection("skydives").doc(doc.id).delete().then(result => {
                console.log("Deleted Users Skydives!");
            }
            )
        })
    })
});

exports.setupUser = functions.auth.user().onCreate((user) => {
    const db = admin.firestore();
    const doc = db.collection("users").doc(user.uid)
    return doc.set({"friends": Array(user.uid)})

});

exports.setupUser = functions.auth.user().onCreate((user) => {
    const db = admin.firestore();
    const doc = db.collection("users").doc(user.uid)
    return doc.set({"friends": Array(user.uid)})

});

exports.myFunction = functions.firestore.document('skydives/{skydiveID}').onWrite((change, context) => { 
    const db = admin.firestore();
    const FieldValue = require('firebase-admin').firestore.FieldValue;
    //var userID = context.params.userID
    var userID = change.after.get("userID")
    console.log("User ID 2 " + userID)
    return db.collection("users").doc(userID).update({"jumpNumber": FieldValue.increment(1)})
});
