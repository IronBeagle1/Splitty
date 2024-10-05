# Splitty

## Configuration

The configuration file is found at ```client/splitty.properties```

# Guide to non-obvious UI rubric requirements

## Non-obvious Basic Requirements

To connect without recompilation, edit the line ```connection.URL=http://localhost:8080/``` in the configuration to your preferred URL

To edit an expense, in the event screen press on the expense you wish to edit and that will take you to the expense edit screen

The total cost of all expenses, and the share per person (deliberately not including direct money transfers, though this can be changed with a single boolean) are available in the statistics screen

The amount owed by each participant is under the settle debts screen, extended into N-1 transfer instructions for N participants

## Using the admin overview
* You can find the password to the admin overview in the console under "server.api.AdminPasswordService".
* To export/import an event type its ID (e.g. ABCDEF) in the text field and click export/import. The saved file can be found under client/backups
* To view participants and expenses of an event click on it from the list, and they should appear in the relevant text areas.
* To delete events, click on "delete selected events" and then tick the ones you want to delete. After you have selected all of them click "delete selected events".


## Live Language Switch

### How to add another language

This can be done in through the drop-down UI that displays the current language.

Language should be in format: {languageCode}_{countryCode} (e.g. en_GB).

languageCode must be a ISO 639 alpha-2 language code.

countryCode must be ISO 3166 alpha-2 country code.

## Detailed Expenses

Giving money from A to B is done by clicking the Transfer Money button in the event overview

To sort the expenses to be from X or including X, select the participant in the dropdown box under the "Expenses" label in the event screen and the From and Including buttons will be updated accordingly. When pressed, they will filter by the specified participant

## Open Debts

Payment instructions: available on the Settle Debts screen, accessible by pressing Settle Debts on the Event overview

Marking money as received: the Mark Received button next to debt instructions on the Settle Debts adds a new transfer expense settling a debt

Only see participants who owe money: only the generated minimum amount of transfers show up on the Settle Debts

Add bank accounts: fields are available when adding or editing participant information

Short summary and expandable/collapsable details: On the Settle Debts screen, transfer instructions are shown as a short X pays Y to Z instruction label, and a larger pane that can be opened by pressing the [>] button next to it

## Statistics

To edit a tag, go to the add / edit expense screen, select a tag from the dropdown, and click the edit icon

To delete a tag, go to the add / edit expense screen, select a tag from the dropdown, and click the delete icon

## Email Notification

### How to configure your email credentials

* spring.mail.host = {The host of your email server e.g. smtp.gmail.com}

* spring.mail.password = {your password, for gmail an app password can be used that is configured in your Google account under "app password"}

* spring.mail.port = {The port of your email server e.g. 587 for gmail}
* spring.mail.username= {add your email here e.g. myemail@gmail.com}

_**These default values work for gmail:**_

* spring.mail.properties.mail.smtp.auth=true (use basic authentication)

* spring.mail.properties.mail.smtp.starttls.enable=true (use TLS)

## HCI

### Keyboard Shortcuts

Are visible by hovering over buttons in the Main Menu or Event Overview

### Main Menu:
* Ctrl + A: Goes to admin overview password
* Ctrl + E: Joins the most recently joined event from the history

### Event Overview:
* Ctrl + T: Test email invite
* Ctrl + S: Switch to statistics screen
* Ctrl + W: Edit the title of the event
* Ctrl + Q: Add a new expense
* Ctrl + P: Add a new participant
* Ctrl + I: Invite by email
* Ctrl + B: Go to main screen
* Ctrl + F: Add tag
* Ctrl + D: Transfer movie
* Ctrl + G: Settle debts

### Keyboard Navigation

Press Tab to cycle through buttons, Enter to press one
