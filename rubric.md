# Relevant Rubric Code Pointers

## Technology

### Dependency Injection

The client and server both use DI everywhere. Examples:

Client: [ManagementOverviewScreenCtrl](/client/src/main/java/client/scenes/ManagementOverviewScreenCtrl.java)

Server: [EventController](/server/src/main/java/server/api/EventController.java)

**Example of binding external types**:
External type used in [AdminPasswordService](/server/src/main/java/server/api/AdminPasswordService.java), 
bound by [RandomConfig](/server/src/main/java/server/config/RandomConfig.java)

### Spring Boot

@Controller: [LPController](/server/src/main/java/server/api/LPController.java)

@RestController: [ExpenseController](/server/src/main/java/server/api/ExpenseController.java)

Repository: [ExpenseRepository](/server/src/main/java/server/database/ExpenseRepository.java)

@Services: [ParticipantService](/server/src/main/java/server/api/ParticipantService.java)

### JavaFX

Images: [EventScreenCtrl](/client/src/main/java/client/scenes/EventScreenCtrl.java)

Dependency Injection: Client: [ManagementOverviewScreenCtrl](/client/src/main/java/client/scenes/ManagementOverviewScreenCtrl.java)

### Communication

#### REST Requests

Server: [ExpenseController](/server/src/main/java/server/api/ExpenseController.java) (and of course all the other Controllers that don't use WebSockets or Long Polling)

Client (using Jersey): [ServerUtils](/client/src/main/java/client/utils/ServerUtils.java) (used for one-time GET requests and all POST and PUT)

#### Long Polling

Server: [LPController](/server/src/main/java/server/api/LPController.java)

Client (using Jersey): [LPUtils](/client/src/main/java/client/utils/LPUtils.java), which is called in [AppStateManager](/client/src/main/java/client/utils/AppStateManager.java) (used for updating event names in the startup screen)

#### WebSockets

Server: [WebSocketService](/server/src/main/java/server/websockets/WebSocketService.java)

Client (using Stomp): [WebSocketUtils](/client/src/main/java/client/utils/WebSocketUtils.java), which is called in [AppStateManager](/client/src/main/java/client/utils/AppStateManager.java) (used for updating event data in any part of the client where one single event is opened, such as an event's overview)

### Data Transfer

Used implicitly everywhere in the client that deals with connections: in [ServerUtils](/client/src/main/java/client/utils/ServerUtils.java), [LPUtils](/client/src/main/java/client/utils/LPUtils.java), and [WebSocketUtils](/client/src/main/java/client/utils/WebSocketUtils.java), and in the entire server, e.g. [ExpenseController](/server/src/main/java/server/api/ExpenseController.java)

## Testing

### Unit Testing

Configurable Subclass: [TestEventRepository](/server/src/test/java/server/api/TestEventRepository.java)

Mockito: [SettleDebtsScreenCtrlTest](/client/src/test/java/client/scenes/SettleDebtsScreenCtrlTest.java)

### Indirection

[SettleDebtsScreenCtrlTest](/client/src/test/java/client/scenes/SettleDebtsScreenCtrlTest.java), [EventControllerTest](/server/src/test/java/server/api/EventControllerTest.java)

### Endpoint Testing

[ParticipantControllerTest](/server/src/test/java/server/api/ParticipantControllerTest.java), [EventControllerTest](/server/src/test/java/server/api/EventControllerTest.java)
