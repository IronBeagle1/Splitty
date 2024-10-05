| Key          | Value                           |
|--------------|---------------------------------|
| Date:        | 2/04/2024                       |
| Time:        | 15:45 - 16:30                   |
| Location:    | Drebbelweg PC Hall 1 'Backroom' |
| Chair        | Glafkos Michaelides             |
| Minute Taker | Maurice van de Streek           |
| Attendees:   | Everyone                        |
Agenda Items:
- Opening by chair **(1 min)**
- Check-in: How is everyone doing? **(1 min)**
- Announcements by the team **(1 min)**
- Approval of the agenda & last week's minutes - Does anyone have any additions? **(1 min)**
  - Add item for the Self-Reflection Assignment
- Announcements by the TA **(2 min)**
  - There is a bug on Brightspace: download files instead of viewing the files through the build-in viewer.
  - Presentation schedule is still worked on, so download it regularly.
  - Formative self-reflection assignment is due this week Friday. 
    The points you write about in it, should be personal. 
    A TA shouldn't be able to say that this could apply to anyone.
    The grading for this formative assignment, will again be incredibly strict.
  - Go over the HCI rubrics again. Make sure you pass it next time.
    If you don't pass it, it is considered a failed assignment.
    The TA doesn't know what this means, but let's not find out.
  - TA recommends us to make a MarkDown file where we make sections for every assignment that will be graded for our project.
    Under each section point to places that show that we achieved the rubric and explain why.
    This helps the TA to grade our project better.
- Talking Points: (inform/decision-making/discuss) **(*total* 30-35 min)**
     1. Sprint review `[inform]` **(5 min)**
        - Glafkos
          - Didn't get to contribute code to the project last week.
        - Giorgos:
          - Worked on email functionality. Now the user can submit a test mail to their configured email setup.
          - Has already worked on sending invitation mails to new participants, still needs to be finished.
        - Matei:
          - Implemented filters for the expense overviews
          - Implemented the displaying of the date in the expense overview (not the creation scene)
          - Implemented delete button in the expense overview is now aligned to the right
          - Created Bugfixes regarding the expenses
        - Iustin:
          - Implemented Backend for tags
          - Implemented tests for tags common class
          - Still need to add tests for the endpoints and service methods regarding tags
        - Maurice: 
          - Didn't implement transferring money from A to B and language template generation, because of personal problems.
          - Has figured out how to implement windows instead of changing scenes in primary stage.
        - Kornel:
          - All user stories for open debts are implemented except for the one blocked by transferring money A to B.
          - Refactored code related to switching between scenes, and the loading and showing of images.
          - Implemented string formatting with binding variables for items in expense overview.
          - Found a way to test JavaFX controllers through a library: TestFX.
     2. Unresolved GitLab issues `[discuss]` **(4 min)**
        - Expense from A to B (Maurice will finish it soon)
          - Kornel has an issue that is blocked by this issue. He doesn't need it to be done really fast, so Maurice takes it and tries to finish it on Wednesday.
        - Restful endpoints
          - Easy to fix
        - Management overview is messing with the event list on the start screen
          - Whenever you create two events, delete one in the management overview and then go back to the startup screen, the history list won't have the deleted event removed.
          - Kornel has already fixed this on a branch that is yet to be merged, so this will be dealt with.
        - Screens testing (few issues exist on this topic, will be detailed in point 3)
     3. Testing - fx tests and screens tests in general `[discuss]` **(4 min)**
        - We can use TestFX to test JavaFX controllers now. An example of how to use it has been sent as an open MR on GitLab.
        - It is preferable to have everyone code in the client that they worked on themselves, because they know the ins-and-outs already of their code. If this is too much for anyone, then someone else can take up some part.
     4. Checkstyle `[discuss]` **(3 min)**
        - We need to work on completely satisfying our CheckStyle rules.
        - There are currently approximately 200 CheckStyle warnings.
        - First everyone should put JavaDoc on their own implemented code.
        - Only thereafter we figure out how to make our CheckStyle file work on the build server, because otherwise we need to handle all formatting issues in one MR to make the build pass. 
     5. Status check `[decision-making]` **(5 min)**
        - Optional features -> do we have a clear view on what features we will/will not implement & status
          - For the live translation we need to add the language template generation.
          - Detailed expenses we need to implement transferring money from A to B.
          - For open debts we need to implement the ability to mark debts as settled.
          - The frontend and probably some backend for all the statistics requirements still need to be done.
            - Question: How do we handle expenses without tags?
            - Answer: Give those expenses a default tag.
          - Sending invitation emails that automatically add new participants and open debts reminder emails need to be implemented.
          - Someone can try implementing the foreign currency, however we don't aim on finishing that one.
        - Long polling -> will we implement it through currency exchange?
          - If somebody has the time for it, yes.
          - Anyway, we're going to implement long-polling for a server connection indicator.
     6. Division of tasks `[discuss]` **(5 min)**
        - See action points
     7. Presentation of the current app to the TA `[inform]` **(6 min)**
        - Some functionality didn't work, the client console gave some invocation exceptions.
     8. Self-Reflection Assignment
        - Skipped
- Feedback round: What went well and what can be improved next time? **(1 min)**
  - Everything was amazing.
- Planned meeting duration != actual duration? Where/why did you mis -estimate? **(1 min)**
  - Everyone found it perfect...
- Question round: Does anyone have anything to add before the meeting closes? **(0-2 min)**
  - How does transferring money from A to B work?
    - TA: A money transfer can be modelled as an "inverse" expense, where A owes to B, a negative amount of money.
    - Kornel: You can also model it as a normal expense, where B owes to A, a positive amount of money.
  - Should it be possible to have multiple tags for an expense?
    - TA: The backlog just refers to "tag", so singular is fine.
  - Do we need to submit the self reflection as one file onto the repository or do we need to submit them separately each individually?
    - TA: Everyone needs to merge their own self reflection individually.
- Closure **(1 min)**

### Action Points:
- Glafkos:
  - Implement testing for participant related code.
  - Add the final fields to the participants.
- Matei:
  - Look at how to show images within the combo box of the language indicator.
  - Finish and test everything related to expenses.
- Giorgos:
  - Finish sending invitation mails that automatically add new participants
  - Implement notification mails to settle open debts
- Iustin:
  - Tries to finish up all the statistics requirements frontend. This doesn't include testing.
- Maurice:
  - Implement generation of empty language template
  - Implement transferring money from A to B. Will give a status update for it on Wednesday.
  - Make a beginning on the foreign currency exchange.
- Kornel:
  - Extend the websockets to the management overview.
  - Implement marking settled debts as received.
  - Implement the details for the statistics screen
  - Finish the half-finished MR on handling event deletion
- Everyone:
  - Put JavaDoc on all their implemented code, making sure to resolve all CheckStyle warnings.
  - Go over HCI assignment rubrics and think about what to add to make the assignment pass.
- Not divided yet:
  - Long polling for server status connection indicator.
  - Extract all folders used by the client into the module itself. Maybe into the resources folder?
  - Add error message for when server is down but client is open.
  - Fix CheckStyle script on build server
