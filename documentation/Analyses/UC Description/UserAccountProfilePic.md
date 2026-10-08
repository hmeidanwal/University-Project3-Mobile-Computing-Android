| Field             | Details |
|-------------------|---------|
| **Name**          | Upload image |
| **Actor**         | User |
| **Description**   | As a user, I want to upload image (Profile picture) so that I can personalize my profile.|
| **Pre-condition** | Actor is logged in |
| **Scenario**      | 1. Actor taps the profile picture icon on the Profile page.  <br>  2. System opens the front camera capture UI.  <br> 3. Actor positions face and taps Capture. <br> 4. System shows a Preview with 'Retake' and 'Use Photo'. <br> 5. Actor taps 'Use photo' <br> 6. System uploads the image and sets it as the new profile picture. |
| **Result**        | A (new) profile picture is succesfully uploaded. |
| **Exceptions**    | 2.1 CameraPermissionDeniedException – The actor denied camera permission; system cannot open camera. <br> |


