# Smart Pantry Manager: Testing Record

## 1. Purpose

This document records functional tests performed on the Smart Pantry Manager Android application.

## 2. Functional Testing

| Test                       | Expected Result                                                                           | Result |
| -------------------------- | ----------------------------------------------------------------------------------------- | ------ |
| Add a pantry ingredient    | The ingredient is saved and displayed in the pantry.                                      | Passed |
| View pantry ingredients    | Saved ingredients appear in the pantry list.                                              | Passed |
| Edit a pantry ingredient   | The updated ingredient details are saved and displayed.                                   | Passed |
| Delete a pantry ingredient | The selected ingredient is removed from the pantry.                                       | Passed |
| Validate ingredient inputs | Empty required fields and invalid quantities are rejected.                                | Passed |
| Persist pantry data        | Saved ingredients remain available after reopening the application.                       | Passed |
| Configure expiry alerts    | The expiry-alert setting can be enabled or disabled.                                      | Passed |
| Display expiry warnings    | Eligible expired or soon-to-expire ingredients display a warning when alerts are enabled. | Passed |
| Suggest recipes            | Recipes are suggested according to available ingredients and required quantities.         | Passed |
| View recipe details        | The selected recipe displays its ingredients and preparation instructions.                | Passed |

## 3. Testing Environment

* **Platform:** Android
* **Development environment:** Android Studio
* **Programming language:** Java
* **Database:** SQLite
* **Test device:** Android emulator

## 4. Summary

Functional testing covered pantry management, input validation, data persistence, expiry alerts, recipe suggestions, and recipe details.

The results recorded above should reflect tests actually performed on the application.
