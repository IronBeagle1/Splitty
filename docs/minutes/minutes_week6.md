| Key | Value                           |
| --- |---------------------------------|
| Date: | 19/03/2024                      |
| Time: | 15:45 - 16:30                   |
| Location: | Drebbelweg PC Hall 1 'Backroom' |
| Chair | Kornel Łuszczyk                 |
| Minute Taker | Giorgos Christofi               |
| Attendees: | Everyone                        |
Agenda Items:
- Opening by chair **(1 min)**
- Check-in: How is everyone doing? **(1 min)**
  - Everyone is doing well
- Announcements by the team **(1 min)**
    - No announcements
- Approval of the agenda & last week's minutes - Does anyone have any additions? **(1 min)**
    - Approved by everyone, no additions.
- Announcements by the TA **(2 min)**
  - If anyone receives an email from this week about the knockout criteria it may contain bad things e.g. you will be kicked out of the course
  - The HCI assignment has been posted and will be graded this week. Would be nice to include a readme in the application.
  - Next week we should send the product page, formative feedback for the oral exam.
  - If someone is not available for a date for the oral exam please notify the TA.
- Talking Points: (Inform/brainstorm/decision-making/discuss) **(*total* 30-35 min)**
    1. Sprint review `[inform]` **(5 min)**
        - Giorgos: 
          - Implemented JSON import/export functionality (5h30m)
          - Implemented startup screen history update when an event changes.(2h)
        - Iustin:
          - Implemented logging into a password protected overview
          - Implemented show expenses/participants in the event overview 
        - Maurice:
          - Implemented adding a participant (3h)
          - Implemented ordering of events in management overview (7h)
        - Kornel:
          - Resolved the bug with how the event object was generated (16h)
          - Added logic for basic money management (2h30m)
        - Matei:
          - Linked expense screen and expense logic to the backend (1h)
        - Glafkos:
          - Added methods for editing and removing participants
          - Started on separate UI for editing the participant list
          - Implemented saving of email/iban for participants
        
    2. Unresolved GitLab issues from past weeks `[discuss]` **(3 min)**
        - The Expense calculation methods
          - Math is ready for it but the UI is not.
          - Kornel will get on this.
         - Editing an Expense
          - Remove an expense is left to be done
        - Importing/Exporting JSON
          - It is finished and an MR is available
        - The rest of the GitLab issues, assigned and unassigned
          - Generate an empty language template: Will be discussed in the future
    3. Websockets, long polling, and the app data lifecycle `[brainstorm]` **(8 min)**
        - Data flow 
          - The app needs to have up-to-date information shown. We need to implement websockets and long polling. Most teams use web sockets for most stuff, and long polling for a minor feature (like checking if the connection is still active), but both need to be implemented to pass the basic requirements. Methods in the backend that use normal http requests (PUT and DELETE) and do not need to be changed (however GET methods may need to be updated). Websockets are faster and less resource intensive than long polling so it may be better to use them rather than long polling.
          - What happens when two people edit the same object at the same time?
            - We can merge the changes together, e.g. if one client changes the title and the other the date both should be changed. 
            - Error messages should be shown if there are conflicting changes. We could lock changes to an event if the event is being edited by someone but that would impact performance.
        - Implementation of web sockets & long polling - which technology for which part of the app? And how?
          - The person implementing this should update the team.
          - Splitting this task is not a good idea but this task seems to be quite large.
        - The JSON representation problem - two-way relationships, recursion, and custom (de)serializers
          - The participant in the participant list being the same one from an expense could cause recursion. Kornel managed to make this work without a custom deserializer but using annotations. Technology rubric says that you should make spring do it for an excellent grade instead of implicit jackson calls.
    4. Software Maturity + Quality Assurance `[discuss]` **(6 min)**
        - The HCI assignment: Design (e.g. contrast) and CSS, error messages, keyboard navigation
          - HCI is 5% of the grade
          - Formative feedback is this week.
          - Color contrast, mockup of the design would be very helpful. Keyboard shortcuts should be added.
          - Undo action seems very complicated to implement and doesn't seem to be worth it for the grade. Everyone votes that undoing shouldn't be done.
          - There should be error messages. There is an error in the console if the server doesn't start up in time for the client.
          - Each expense should have a participant. There is no validation for this.
          - Things should be visualized with icons, text and color coding.
          - Don't waste too much time on this but if you see anything that is easy to implement do so.
        - Test coverage and issues
          - We currently have 30% test coverage.
          - Our backend is at 100% so if we get the frontend to 60% we should have about 80% test coverage.
          - Any method that include javafx cannot be tested.
          - Test coverage should go up as we code more without javafx.
        - Progress on automated JavaFX testing
          - There is a library (testfx) that can be used to unit test javafx, but it wouldn't work on gitlab server. 
          - Extract logic into another class and inject that logic into the controllers.
        - Ensuring everyone tests their contributions, and knows which parts should work
          - Before sending a merge request test the app to see all the things that worked before still work.
          - We should be a little bit more strict about our MR approvals. We should increase constructive feedback in our MRs. 
          - If someone writes constructive feedback on an MR should the MR be merged and then fixed later? 
            - Everyone agreed that if something is very important for someone to work on a feature, merge it and fix it as soon as possible. Otherwise, make adjustments first before merging.
        - Fully implementing CheckStyle (un-ignoring)
          - All the example classes have been removed, but we still have a lot of warnings that we should fix.
    5. Division of tasks `[discuss]` **(8 min)**
        - Was done before the meeting, check the gitlab issues.
    6. Presentation of the current app to the TA `[inform]` **(5 min)**
- Summarize action points: Who , what , when? **(2 min)**
- Feedback round: What went well and what can be improved next time? **(1 min)**
  - Task distribution should happen during a meeting.
- Planned meeting duration != actual duration? Where/why did you mis -estimate? **(1 min)**
  - Started at: 15:45
  - Ended at: 16:33
- Question round: Does anyone have anything to add before the meeting closes? **(2 min)**
    - Why did we receive negative feedback for not having controllers when we do have controllers?
        - We need to have rest controllers (already implemented) and controllers (used for websockets, yet to be implemented).
    - Does the 80% test coverage refer to line or method coverage?
        - Test coverage 80% is vague, the TA will ask a question and get back to us on mattermost.
- Closure **(1 min)**
