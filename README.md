# KartoffelPuffer
##### Fachhochschule Dortmund - Mobile App Engineering - SoSe 2022
<br />

### Abstract

Android Application to manage reservations at a given restaurant.<br>
Multi-purpose app -  Customers and Restaurant Manager use the same app.


### Customer View:
- Select desired table from given grid-layout (top-down view of restaurant)
- Pick date, time and number of people attending
- List and cancel own reservations
- (optional) Pre-order dishes from a specified selection


### Restaurant View:
- Create and manage restaurant layouts. Layouts may be given a time they are set as active to select from
- List all customer reservations for a given date and manage them
- Create and manage dishes (name, foto, description, allergies) for the customer to choose from.

### Requirements
- Android 6.0 (API 23) or later
- Backend installed on some server

### Tested devices
###### (Emulated via JetBrains IntelliJ IDEA)
- Google Pixel 5 (1080x2340: 440dpi)
- 

### Backend

Note:<br>
A Backend is required for full functionality, since all data (incl. images for dishes)
are stored on the server.

- My Backend is available here: [KartoffelPuffer Backend](https://github.com/Cyntho/Kartoffelpufferbackend)
- MySQL Database (used here: XAMPP/MySQL)