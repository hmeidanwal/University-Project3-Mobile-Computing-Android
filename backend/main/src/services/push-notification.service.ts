import {injectable, /* inject, */ BindingScope} from '@loopback/core';

import * as admin from 'firebase-admin';
import {readFileSync, existsSync} from 'fs';
import path from 'path';

// Check Render secret file first, then fall back to local
const renderSecretPath = '/etc/secrets/firebase.env';
const localPath = path.resolve(__dirname, '../../src/services/.env');

let serviceAccountPath: string;

if (existsSync(renderSecretPath)) {
  serviceAccountPath = renderSecretPath;
  console.log('Loading Firebase from /etc/secrets/firebase.env');
} else if (existsSync(localPath)) {
  serviceAccountPath = localPath;
  console.log('Loading Firebase from local firebase.env');
} else {
  throw new Error('firebase.env not found in /etc/secrets/ or locally');
}

const serviceAccount: admin.ServiceAccount = JSON.parse(
  readFileSync(serviceAccountPath, 'utf-8'),
);

if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
  });
}

@injectable({scope: BindingScope.TRANSIENT})
export class PushNotificationService {
  constructor(/* Add @inject to inject parameters */) {}

  async sendNotification(
    token: string,
    title: string,
    body: string,
  ): Promise<string> {
    const message = {
      token,
      notification: {title, body},
    };

    try {
      const response = await admin.messaging().send(message);
      console.log('Notification sent successfully:', response);
      return response;
    } catch (error) {
      console.error('Error sending notification:', error);
      throw error;
    }
  }

}
