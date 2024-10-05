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
	- all good
- Announcements by the team **(1 min)**
	- Last year theory heavy questions for the oral examination; This year it won’t be as theoretical (supposedly)
	- End of this week -> 	implemented features assignment (Based on reading comprehension)
	- HCI was good, no fail on color contrast
- Approval of the agenda & last week's minutes - Does anyone have any additions? **(1 min)**
- Announcements by the TA **(2 min)**
- Talking Points: (Inform/brainstorm/decision-making/discuss) **(*total* 30-35 min)**
1. Sprint review `[inform]` **(5 min)**
	- Giorgos - applied styling to all classes and improved accessibility of the app. Hover over the toolkit; colors are changed -> took 7:30hr; fixed history reappearing
	- Matei - made expenses editable, x button which deletes the expense (it didn’t work in the backhand, but now it works); implemented buttons for filtering the expenses and needs to integrate after websockets
	- Iustin - delete events admin story, pop-ups, Listview for checkboxes
	- Maurice - Live action switch; Language indicator on start screen controller -> added UI component
	- Kornel - new screen for settle debts which only states how much each person owes, buttons will be implemented this week; Websockets -> each method also sends an update to the client; client side -> app state manager handles everything 
	- Glafkos -  added participant list ui, where you can edit/delete participants for a specific event
2. Unresolved GitLab issues from past weeks `[discuss]` **(2 min)**
	- Language selector indicator (solved)
	- EventList is in Descending order which should be Ascending 
	- Calculating money does not have a UI screen yet (Issue was changed from week 3 to 7)
	- Endpoints must be restful
3. Updates on websockets and long polling `[inform]` **(3 min)**
	- Websockets: 
		- Changes on one client instantly propagate to the other client (admin overview does not have websockets yet); 
		- Event is handled by the app state manager, which decides when the refresh method should be called -> needs to add implement simple refreshable for new controllers. 
		- (Should we deal with edge cases if not in the rubric?? - TA does not know); Start screen event title is not updated yet 
	- Long polling: 
		- Maybe we can implement it for the foreign currency feature
4. Product pitch `[brainstorm]` **(5 min)**
	- We need to verbally present the product 
	- Need a name for the application 
	- Make a script for the presentation or talk based on the presentation -> memorize the main points
	- Matei suggests making a video 
	- We can probably present the application live but we are not sure how it would work (maybe use a projector -> someone talks about the feature and someone else shows it)
5. Optional features `[decision-making]` **(5 min)**
	- Started working on live language switch (generation of empty template for other languages is not implemented yet -> we can try right to left iteration but we’ll see if there is time) and detailed expenses 
	- Iustin wants to create issues for the tags
	- Giorgos proposes working on the email notifications
	- We’ll discuss how we divide the optional features 
	- What features are we going to prioritize?
	- What has already been implemented?
6. Division of tasks `[discuss]` **(6 min)**
	- Giorgos - will start working on configuring email credentials and the rest later
	- Matei will finish the detailed expenses and a different feature maybe, lastly test1
	- Iustin - write tests for delete event screen; First three user stories for the statistics; maybe work with pie charts
	- Maurice - will finish generating empty templates for languages; give money from A to B
	- Kornel - update admin websockets (maybe take expense issues; remove duplicate code)
	- Glafkos - add participant final details; also testing for past methods 
(TA recommends having over 50% test coverage)
7. Presentation of the current app to the TA `[inform]` **(5 min)**
	- attributes do not update if everything is deleted (implement error messages for HCI)
- Summarize action points: Who , what , when? **(2 min)**
- Feedback round: What went well and what can be improved next time? **(1 min)**
	- maybe should’ve spent less time on the presentation so we would have had more time for questions
- Planned meeting duration != actual duration? Where/why did you miss -estimate? **(1 min)**
- Question round: Does anyone have anything to add before the meeting closes? **(3 min)**
	- how do we manage more participants with the same name: suggest modifying the name maybe, but allow them to do whatever
- Where does responsiveness of scenes fall into? -> potentially for HCI, not sure
- Does the admin view need to be synchronized or can we use a refresh button?
- Should time be tracked in the MR or in the issue? -> time should be tracked in issues
- Is the product
	- yes
- Closure **(1 min)**

We need to have **add controller**
Issues splitting/merging in relation to time spent (asked question to teachers)



