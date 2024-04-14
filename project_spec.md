# Split-it

## Table of Contents

1. [App Overview](#App-Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
1. [Build Notes](#Build-Notes)

## App Overview

### Description 

This app will help users split the bill amongst a group, and keep track of their receipt history

### App Evaluation

<!-- Evaluation of your app across the following attributes -->

- **Category:**
    - Finance/Budgeting
- **Mobile:**
    - A simple user interface for splitting bills on the go.
- **Story:**
    - Users can easily split bills with friends or family. It offers a clear solution, making it compelling to users who frequently face this issue.
    - The story of making social gatherings hassle-free by simplifying bill payments
- **Market:**
    - encompassing anyone who dines out or shares expenses with others
    - Targeted towards people college students and groups spending on a budget
- **Habit:**
    - Those who dine out often or have shared expenses might use it multiple times a week
    - A tool for managing and reviewing transactions, with users consuming information about their expenses
- **Scope:**
    - A simple app with features, like simplifying bill splitting, receipt tracking, and tracking shared expenses
    

## Product Spec

### 1. User Features (Required and Optional)

Required Features:

- Enter restaurant name
- Enter bill total
- Enter tax total
- Enter tip total
- Enter number to split
- Save receipt

Stretch Features:

- Use the user's phone camera to extract the above info rather than manual input
- enable the users to create all account to save their receipts
    - Through their accounts, users can enter a code or QR scan to join the Bill Splitter
    - Receipts are saved on each account

- Users can edit the receipt anytime
- Bill splitter will keep track of all expenses and expense types


### 2. Chosen API(s)
Yelp Api
- 	/businesses/search
          - Finding the name of the restaurant and location (autofill using an API or APIs)
  

### 3. User Interaction

Required Feature

- **User enters the restraunt name**
  - => **App references the restaurant name and location using an API**
- **User enters bill total**
  - => **App updates the total bill amount to be split**
- **User enters tax total or tax percentage**
  - => **App adds the tax amount to the total bill**
- **User enters tip total**
  - => **App adds the tip amount to the total bill**

- **User enter the number of people to split the bill**
  - => **App calculates and siplays the amount each person owes**

- **User chooses to save the receipt**
  - => **App saves the details of the current bill for futre reference**


<!-- Add picture of your hand sketched wireframes in this section -->
<img src="https://github.com/CP-AndriodDev/Split-it/assets/122586425/3c6f70bc-bcb3-438d-87f8-310443a3a361" width=600>

### [BONUS] Digital Wireframes & Mockups
<img width="286" alt="Screenshot 2024-04-13 at 10 59 52 PM" src="https://github.com/CP-AndriodDev/Split-it/assets/122586425/31a8feec-cd8c-473e-bd9c-5a7b49a57323">

### [BONUS] Interactive Prototype

## Build Notes

Here's a place for any other notes on the app, it's creation 
process, or what you learned this unit!  

For Milestone 2, include **2+ Videos/GIFs** of the build process here!

## License

Copyright **2024** **HMC**

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
