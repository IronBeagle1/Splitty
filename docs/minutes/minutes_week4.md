| Key | Value |
| --- | --- |
| Date: | 05/03/2024 |
| Time: | 15:45 - 16:30 |
| Location: | Drebbelweg PC Hall 1 'Backroom' |
| Chair | Matei Pânzariu |
| Minute Taker | Kornel Łuszczyk |
| Attendees: | Everyone |
Agenda Items:
- Opening by chair (1 min)
- Check-in: How is everyone doing? (1 min)
- Announcements by the team (1 min)
    - No announcements
- Approval of the agenda - Does anyone have any additions? (1 min)
    - No additions
- Announcements by the TA (2 min)
    - Please read the knockout criteria announcement in Brightspace
    - You don't necessarily have to code in week 5, but will need 7 weeks worth of contributions by the end of the project
- Talking Points: (Inform/brainstorm/decision making/discuss)
- <Agenda-item 1> Checking in with what everybody did the past week (5 min)
    - Glafkos: finished last week's assigned issues and tests, implemented participant edit/add screen, without connection to the backend
    - Iustin: Event screen UI and controller, switching screens in Main controller
    - Maurice: Added invite code on Event UI, editing Event title (done as a new screen instead of popup, will need to be changed later)
    - Kornel: Added all necessary translation features (took 4h10m) architected to be ready to implement live language switch, changed create event endpoint only take event name (1h)
    - Giorgos: Made main screen, spent 10h trying to get frontend tests to work and now they do, fixing bug of duplicate history when rejoining event
    - Matei: Expense screen & functionality
- <Agenda-item 2> Architecture of the frontend, problem with the division of tasks (5 min)
    - Issue: no way to add participants right now, and that is necessary for other parts, Maurice is working on it and will add ASAP
        - Adding participants to event does not currently work on the back-end
    - Issue: some tasks require other tasks to be completed first
        - We should indicate which GitLab Issues depend on which being resolved first
        - Related issues should be done by one person, though this may result in too much work for one person (-Matei)
        - Potentially, additions between 2 dependent tasks should be done incrementally, in step with each other
        - If there is a dependency, you can work on something not started at all, like the admin overview (-Giorgos)
    - Issue: Front-end data architecture
        - Changes to entities are stored locally, the Event object is passed around between controllers
        - There are no live updates or concurrency, code is messy due to passing around Event
        - Solution: any time a new screen is opened, backend should be called to retrieve current data
        - More research into how long polling and websockets are implemented specifically is needed
    - Issue: There are redundant methods to switch between screens, e.g. to open the Event screen
        - Fields in the Expense screen did not properly reset when one of the methods was called, so another was added
        - Currently, this code is in the MainCtrl, but should be in the Expense controller
- <Agenda-item 3> Starting to work on the admin side + Continuing work from last week (3 min)
    - Iustin is working on the admin password Service, password check UI
    - ^ the password check UI can be done later, someone else can make the Admin event list first, add password protection later
    - So, Iustin will only add a button to open the Admin view
- <Agenda-item 4> Getting the tests for the frontend working (they work locally) (10 min)
    - Issue: UI tests not working on the server due to JavaFX dependency
    - Giorgos: initializeJavaFX thread was needed at first
    - Giorgos refactored controller to separate JavaFX segments from non-JavaFX ones, only test what you could, only 35% coverage because most do require UI elements
    - Maurice: instead of FXML, UI elements can be created in Java
    - Kornel: might still need to initialize JavaFX, even if the elements are generated programmatically
    - Giorgos: has not managed to mock UI with Mockito without JavaFX initialization, which does not work
    - TA: **80% line coverage is required for all parts of the application, even the UI**
- <Agenda-item 5> Division of tasks, finishing the first draft of the UI (5 min)
    - Iustin
        - Will fix Event UI problems - participant selector not functioning
        - Admin: password, password protection, event overview
    - Giorgos
        - Already refactored StartupScreenCtrl to make testing possible without JavaFX (on Monday)
        - Admin: JSON dump import & export
    - Matei
        - Event Overview UI - creating a functional expense list
        - Connecting Add/Edit Expense to backend
        - Remove Expense function
    - Rest: currently assigned issues, will assign themselves unassigned issues opened on Monday during unofficial meeting
        - (Additionally, outside of meeting):
        - Kornel:
            - Total Event expenses
            - Share of total per participant
            - Total owed per participant
    - Agreement to decide on major refactoring to fix **Front-end data architecture** issues together, after the meeting
        - Addition: each member is responsible for fixing the Controller that they wrote in Week 3

- <Agenda-item 6> Q&A with the TA  (5 min)
    - Application demo - looks good according to TA, almost all mandatory requirements done (except admin overview)
    - Q: What does RESTfulness mean here? Should hyperlinks be used in response body?
        - (T)A: REST should be implemented similarly to W&DT assignment, but this is not binding, will provide information later
    - Q: Can the agenda template be added to?
        - (T)A: Extra items can be added. The Agenda-item tags should be changed and indented, subpoints can be added (like in this document!). Should keep the header, and bullet point structure
- Summarize action points: Who , what , when? (2 min)
    - See Agenda-item 5
- Feedback round: What went well and what can be improved next time? (1 min)
- Planned meeting duration != actual duration? Where/why did you mis -estimate? (1 min)
    - Meeting ended precisely on time
- Question round: Does anyone have anything to add before the meeting closes? (2 min)
    - No questions
- Closure (1 min)