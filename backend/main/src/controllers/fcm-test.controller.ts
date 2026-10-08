/**
 * This controller is only for example purposes.
 * It provides a test endpoint for sending push notifications via FCM.
 */
import {inject} from '@loopback/core';
import {post, requestBody, response} from '@loopback/rest';
import {PushNotificationService} from '../services/push-notification.service';

export class FcmTestController {
  constructor(
    @inject('services.PushNotificationService')
    public pushNotificationService: PushNotificationService,
  ) {}

  @post('/fcm-test')
  @response(200, {
    description: 'FCM test endpoint response',
    content: {
      'application/json': {
        schema: {
          type: 'object',
          properties: {
            message: {type: 'string'},
          },
        },
      },
    },
  })
  async logRequestBody(
    @requestBody() body: {token: string},
  ): Promise<{message: string}> {
    console.log('FcmTestController received body:', body);
    try {
      await this.pushNotificationService.sendNotification(
        body.token,
        'Test Notification',
        'This is a test notification',
      );
      return {message: 'Request body logged and notification sent'};
    } catch (error) {
      console.error('Error sending notification:', error);
      return {message: 'Request body logged but notification failed'};
    }
  }
}

