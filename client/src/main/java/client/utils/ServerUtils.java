/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import java.util.List;
import java.util.Set;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;


public class ServerUtils {

    @Inject
    @Named("connection.URL")
    private String serverURL;

    /**
     * Gets the event from the server based on the invite code
     *
     * @param inviteCode the invite code of the event
     * @return the event
     */
    public Event getEvent(String inviteCode) {
        String path = "api/events/" + inviteCode;
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverURL).path(path) //invite code is the ID
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Event.class);
    }

    /**
     * Creates an event with the title given
     *
     * @param title the title of the event
     * @return the event created
     */
    public Event createEvent(String title) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverURL).path("api/events/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(title, APPLICATION_JSON), Event.class);
    }

    /**
     * Sends a PUT request to change the event with the given ID the given title.
     *
     * @param id    Invitation code of the event
     * @param title Title of the event
     * @return Event with a new title
     */
    public Event editTitle(String id, String title) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(serverURL).path("api/events/" + id) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .put(Entity.entity(title, APPLICATION_JSON), Event.class);
    }

    /**
     * Requests the server to add a participant.
     *
     * @param id          ID of the event
     * @param participant The Participant to add
     * @return The participant added by the server
     */
    public Participant addParticipant(String id, Participant participant) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(serverURL).path("api/events/" + id + "/participants") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(participant, APPLICATION_JSON), Participant.class);
    }

    /**
     * method for removing participant from event and server
     *
     * @param id            id of the event
     * @param participantId id of the participant to be removed
     */
    public void removeParticipant(String id, long participantId) {
        Response response = ClientBuilder.newClient()
                .target(serverURL)
                .path("api/events/" + id + "/participants/" + participantId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("removed");
        } else {
            System.out.println("not removed " + response.getStatus());
        }
    }

    /**
     * edits the participant info in the server
     *
     * @param eventId       the id of the event
     * @param participant   the participant to be edited
     * @param participantId the id of the participant to be edited
     */
    public void editParticipant(String eventId, long participantId, Participant participant) {
        ClientBuilder.newClient()
                .target(serverURL)
                .path("api/events/" + eventId + "/participants/" + participantId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(participant, APPLICATION_JSON), Participant.class);
    }

    /**
     * Sends a POST request to add an expense to a specific event
     *
     * @param eventId the id of the specific event
     * @param expense the expense to be added
     * @return the expense added
     */
    public Expense addExpense(String eventId, Expense expense) throws WebApplicationException {
        ClientBuilder.newClient()
                .target(serverURL)
                .path("api/events/" + eventId + "/expenses")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(expense, APPLICATION_JSON), Expense.class);
        return expense;
    }

    /**
     * Sends a GET request to get a list of expenses for a specific event
     *
     * @param eventId the id of the event for which we want to get the expenses
     * @return the list of expenses for the specific event
     */
    public Set<Expense> getExpensesForEvent(String eventId) {
        return ClientBuilder.newClient()
                .target(serverURL).path("api/events/" + eventId + "/expenses")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Set<Expense>>() {
                });
    }

    /**
     * Sends a DELETE request to delete the specific expense
     *
     * @param eventId   the id of the event to delete the Expense from
     * @param expenseId the id of the expense we want to delete
     */
    public void deleteExpenseForEvent(String eventId, Long expenseId) {
        Response response = ClientBuilder.newClient()
                .target(serverURL)
                .path("api/events/" + eventId + "/expenses/" + expenseId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Expense deleted successfully.");
        } else {
            System.out.println("Failed to delete expense. Status code: " + response.getStatus());
        }
    }

    /**
     * Sends a PUT request to modify an existing expense
     *
     * @param eventId   the id of the Event tied to the expense
     * @param expense   the expense we want the current expense to be updated to
     * @param expenseId the id of the expense we want to update
     * @return the new expense
     */
    public Expense editExpense(String eventId, long expenseId, Expense expense) {
        return ClientBuilder.newClient()
                .target(serverURL)
                .path("api/events/" + eventId + "/expenses/" + expenseId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(expense, APPLICATION_JSON), Expense.class);
    }

    //TODO Test weather or not the methods actually work in cae of problems like(expense doesn't exist)

    /**
     * checks if a password matches with the one randomly generated
     *
     * @param inputPassword the password the user inputs to log in to the management overview
     * @return a boolean, true or false whether the password matches or not
     */
    public Boolean checkPassword(String inputPassword) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverURL).path("api/password/" + inputPassword)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Boolean.class);
    }

    /**
     * retrieves all events from the server
     *
     * @return all the events from the server
     */
    public List<Event> retrieveAllEvents() {
        List<Event> events = ClientBuilder.newClient(new ClientConfig())
                .target(serverURL).path("api/events/all")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Event>>() {
                });
        return events;
    }

    /**
     * Sends an event to be added to the database
     *
     * @param event the event to be added
     * @return the event added
     */
    public Event addEvent(Event event) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(serverURL).path("api/events/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(event, APPLICATION_JSON), Event.class);
    }

    /**
     * delete an event using it's ID
     *
     * @param eventId the id of the event we want to delete
     */
    public void deleteEvent(String eventId) {
        Response response = ClientBuilder.newClient()
                .target(serverURL)
                .path("api/events/remove/" + eventId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Event deleted successfully.");
        } else {
            System.out.println("Failed to delete event. Status code: " + response.getStatus());
        }
    }

    /**
     * delete all the events in the database
     */
    public void deleteAllEvents() {
        Response response = ClientBuilder.newClient()
                .target(serverURL)
                .path("api/events/delete/all")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("All events deleted successfully.");
        } else {
            System.out.println("Failed to delete all events. Status code: " + response.getStatus());
        }
    }

    /**
     * this adds a tag to an event, connecting the front-end with the back-end
     *
     * @param eventId   the ID of the event to which we add the tag
     * @param tagName   the tag name of the tag we want to add
     * @param colorCode the color code of the tag that we want to add
     */
    public void addTagToEvent(String eventId, String tagName, String colorCode) {
        Entity<String> entity = Entity.entity(colorCode, APPLICATION_JSON);
        Response response = ClientBuilder.newClient()
                .target(serverURL)
                .path("api/events/" + eventId + "/tag/" + tagName)
                .request(APPLICATION_JSON)
                .post(entity);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode())
            System.out.println("Tag added successfully");
        else
            System.out.println("Tag was not added");
    }

    /**
     * Requests the server to edit the tag
     * @param eventId Event ID of the event related to this tag
     * @param tagId ID of this tag
     * @param tag Tag that will be edited
     * @return Edited tag
     */
    public Tag editTag(String eventId, String tagId, Tag tag) {
        return ClientBuilder.newClient()
                .target(serverURL)
                .path("api/events/" + "tags/" + eventId + "/" + tagId)
                .request(APPLICATION_JSON)
                .put(Entity.entity(tag, APPLICATION_JSON), Tag.class);
    }

    /**
     * Requests the server to delete a tag
     * @param eventId ID of the event the tag is attached to
     * @param tagId ID of the tag
     */
    public void deleteTag(String eventId, String tagId) {
        ClientBuilder.newClient()
                .target(serverURL)
                .path("api/events/tags/" + eventId + "/" + tagId)
                .request(APPLICATION_JSON)
                .delete();
    }
}
