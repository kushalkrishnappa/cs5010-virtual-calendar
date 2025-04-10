# Virtual Calendar Application

This project implements a virtual calendar application with core features similar to popular calendar apps like Google Calendar. It supports creating, editing, querying, and exporting calendar events, both single and recurring. The application now supports multiple calendars with timezone capabilities, allowing users to organize events across different timezones and copy events between calendars.

This application uses `Interval Tree` to store the Events for efficient conflict detection. For more details on the Interval Trees checkout this wikipedia page: [Interval tree](https://en.wikipedia.org/wiki/Interval_tree)

## Table of Contents

- [Introduction](#introduction)
- [New Features (Assignment 6)](#new-features-assignment-6)
- [Previous Features](#previous-features)
- [Features Not Implemented](#features-not-implemented)
- [Running the Application](#running-the-application)
- [Team Contributions](#team-contributions)
- [Additional Notes](#additional-notes)

## Introduction
This application is designed to mimic the functionality of a calendar application.  
It supports creating single and recurring events, handling conflicts, editing events, querying the calendar, and exporting the calendar to a CSV file compatible with Google Calendar.

The application can be run in interactive, headless, and GUI modes as explained later.

It supports multiple calendars with timezone management, allowing users to create separate calendars for different purposes (e.g., work, personal) and manage events across different timezones.

The newly added GUI provides a user-friendly interface for calendar operations.

## New Features (Assignment 6)

### Graphical User Interface
- Java Swing-based GUI for intuitive calendar interaction.
- Month view with navigation to previous and next months.
- Day view for displaying and managing events.
- User-friendly dialogs for creating and editing events.

### Calendar Management in GUI
- Create new calendars with specific timezones.
- Switch between different calendars through dropdown menu.
- Display current timezone for better context.
- Edit calendar properties including name and timezone.

### Event Operations via GUI
- View all events for a specific day.
- Create new events (both single and recurring).
- Edit existing events with conflict detection.
- Special support for recurring event modification options.

### Import Functionality
- Import events from CSV files compatible with Google Calendar.
- Visual feedback with number of successful import operations.

## Previous Features

## Assignment 5

### Multiple Calendar Support
- Create and manage multiple calendars with different timezones.
- Switch between calendars during operation.
- Each calendar maintains its own set of events.

```
create calendar --name <calName> --timezone <timezone>
use calendar --name <calName>
```

### Timezone Support
- Each calendar has its own timezone settings.
- Timezone is specified during calendar creation using [IANA Time Zone Database](https://en.wikipedia.org/wiki/List_of_tz_database_time_zones) format (e.g., "America/New_York", "Asia/Kolkata").
- Events are automatically adjusted when timezones change.

### Calendar Editing
- Edit calendar properties including name and timezone.
- When timezone is changed, all events are automatically shifted to maintain the same wall clock time.

```
edit calendar --name <calName> --property name <newName>
edit calendar --name <calName> --property timezone <newTimezone>
```

### Copy Events Between Calendars
Copy events from one calendar to another with the following commands:

```
copy event <eventName> on <sourceDateTime> --target <targetCalendar> to <targetDateTime>
copy events on <sourceDate> --target <targetCalendar> to <targetDate>
copy events between <startDate> and <endDate> --target <targetCalendar> to <targetDate>
```

### Enhanced Export Functionality
- Export functionality has been refactored using the Strategy Pattern.
- Implementation moved to a dedicated service layer.
- IO operations moved from model to controller for better separation of concerns.

## Assignment 4

### Create Single Calendar Event:
- Supports creation of Single Event spanning across days with provided subject, start date and end date.
- Manages event conflicts with `--autoDecline` option.

`create event [--autoDecline] <eventName> from <dateStringTtimeString> to <dateStringTtimeString>`  
Creates a single event.
- `--autoDecline` (optional): Rejects event creation if there's a conflict.
- `<dateString>`: Format `YYYY-MM-DD`.
- `<timeString>`: Format `hh:mm`.

### Create All Day Calendar Event:
- Supports creation of Single All Day Event with provided subject and date.
- Manages event conflicts with `--autoDecline` option.

`create event [--autoDecline] <eventName> on <dateStringTtimeString>`  
Creates an all-day event.


### Create Recurring Calendar Event:
- Supports Spanned or All Day recurring events with specified weekdays, number of occurrences, or end date/time.
- Requires events to start and end on the same day.
- Rejects recurring events that conflict with existing events.

`create event [--autoDecline] <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times`  
Creates a recurring event for `N` occurrences.
- `<weekdays>`: Sequence of `M`, `T`, `W`, `R`, `F`, `S`, `U` (e.g., `MRU` for Monday, Thursday, Sunday).

`create event [--autoDecline] <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>`  
Creates a recurring event until a specific date/time.

`create event <eventName> on <dateString> repeats <weekdays> for <N> times`  
Creates a recurring all-day event for `N` occurrences.

`create event <eventName> on <dateString> repeats <weekdays> until <dateString>`  
Creates a recurring all-day event until a specific date.

### Edit Calendar Events:
- Supports editing single and recurring events.
- Allows modification of independent, single instance of recurring, all events in a series starting at a specific date/time, or all events in a series.
- **Does not support editing recurring details for a single instance of recurring series, as it is unclear whether to update all events in series or the current and following events in the recurring series.**
- **While editing events that may have matching events with similar name, start time and end time, the application edits the first event which was created.  
  This goes for the recurring events as well, it edits the events in series of the first matching instance of the recurring event from the start date (when specified) or first added recurring event.**
- Prevents editing of non-existent events.

Supported properties: `name` `startDateTime` `endDateTime` `description` `location` `isPublic` `occurrences` `weekdays` `untilDateTime`

`edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>`  
Edits a single event's property.

`edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>`  
Edits the property of all events with the same name starting at the date/time.

`edit events <property> <eventName> <NewPropertyValue>`  
Edits the property of all events with the same name.

### Query Calendar:
- Supports querying events on a specific date or within a date range.

`print events on <dateString>`  
Lists events on a specific date.

`print events from <dateStringTtimeString> to <dateStringTtimeString>`  
Lists events within a date/time range.

### Check Availability:
- Provides functionality to check if the user is busy at a specific date/time.

`show status on <dateStringTtimeString>`  
Shows busy/available status.

### Export Calendar:
- Exports the calendar to a CSV file compatible with Google Calendar.
- Displays the absolute filepath of the created CSV file.

### Command Line Interface:
- Supports all specified commands (create, edit, print, export, show status).
- Handles invalid commands with clear error messages.

### Modes of Running:
- `Interactive mode`: User enters commands interactively and errors are displayed back to user. The user exits by using the `exit` command.
- `Headless mode`: Executes commands from a text file. Errors in a command exits the application with error. This mode also expects `exit` as a final command in the file for completion.

## Features Not Implemented
- All features from the assignment descriptions are implemented.

## Running the Application

### Prerequisites

- Java Development Kit (JDK) 11 installed.

### Setup

1. Open the project in an IDE like IntelliJ.
2. Use the Main inside the CalendarApp.java as entrypoint.

### Running

### Using Jar to Run the Appplication
1. Navigate to the folder where the jar exists.

2. Execute the jar file using one of these methods:
    - **GUI Mode (default)**: Simply double-click the JAR file or run:
      ```bash
      java -jar cs5010-virtual-calendar.jar
      ```
    - **Interactive Mode**: Command-line interface:
      ```bash
      java -jar cs5010-virtual-calendar.jar --mode interactive
      ```
    - **Headless Mode**: Execute commands from a script file:
      ```bash
      java -jar cs5010-virtual-calendar.jar --mode headless commands.txt
      ```

#### GUI Mode (default)
1. Run the application without any arguments or select the GUI mode:
   ```bash
   java -jar cs5010-virtual-calendar.jar
   ```
2. The graphical user interface will appear, allowing you to:
    - Navigate through months with previous/next buttons
    - Click on days to view/edit events
    - Create new events with the "New Event" button
    - Manage multiple calendars via the top menu

For more info refer to [USEME.md](./USEME.md) file.

#### Interactive Mode

1.  Run the application in interactive mode using the following arguments:  
    (This can be set in the run configurations)
    ```bash
    --mode interactive
    ```

2.  Type `exit` to terminate the application.

#### Headless Mode

1.  Create a text file (e.g., `commands.txt`) with a list of commands, ending with `exit`.
2.  Run the application in headless mode using the following arguments:  
    (This can be set in the run configurations)
    ```bash
    --mode headless commands.txt
    ```

## Team Contributions

### Assignment 6
- `Kushal K Krishnappa`:
    - Implemented the Swing-based GUI components and layouts
    - Developed the calendar month view and day view
    - Created event creation/editing dialogs
    - Implemented timezone display in the GUI
    - Created calendar management features in the GUI

- `Anirudh Nitin Bakare`:
    - Designed and implemented the Controller adaptor for GUI interaction
    - Refactored multi-mode support (GUI, interactive, headless)
    - Implemented event management through the GUI
    - Developed import/export functionality through the GUI

- `Both Members`:
    - Designed and implemented the MVC pattern for GUI integration
    - Integration of GUI with existing codebase
    - Testing and documentation

### Assignment 5
- `Kushal K Krishnappa`:
    - Implemented copy events functionality
    - Enhanced interval tree with adding balancing feature

- `Anirudh Nitin Bakare`:
    - Implemented multiple calendar support
    - Implemented timezone handling and conversion
    - Enhanced model layer for better separation of concerns
    - Implemented calendar editing commands

- `Both Members`:
    - Refactored export functionality using Strategy Pattern
    - Service layer implementation
    - Architecture refactoring for better maintainability
    - Testing and documentation

### Assignment 4
- `Kushal K Krishnappa`:
    - Implemented core calendar logic (Calendar, Event, RecurringEvent)
    - Implemented date and time handling
    - Implemented CSV export

- `Anirudh Nitin Bakare`:
    - Implemented command processing (Controller)
    - Implemented interactive and headless modes
    - Created and implemented the testing suite for Controller

- `Both Members`:
    - Implementing Interval Tree
    - Views implementation
    - Designing interfaces
    - Debugging, testing, and documentation
    - Class diagram creation

## Architecture Improvements in Assignment 5

1. **Model-Controller Separation**:
    - Controller now uses a supplier/factory for model instances instead of direct instances.
    - Multiple calendars are managed at the controller level.
    - Better separation of concerns with IO operations moved from model to controller.

   **Justification**
    - Using a factory for models lets us swap in different models easily for a specific calendar and compare the functionality.
    - Keeping multiple calendars at the controller level means each model just handles one calendar's events, which is simpler.
    - Moving file operations out of the model keeps the model focused just on business logic i.e, calendar.

2. **Service Layer Introduction**:
    - Added dedicated service layer for export operations and file management.
    - Implemented the Strategy Pattern for export format flexibility.
    - Created interface-based approach for file writing operations.

   **Justification**
    - Having a service layer separates the concerns of performing specialized operations. A model and controller can focus on core logic and user interaction.
    - By using the Strategy Pattern, we can easily add new export formats without changing existing code.
    - The interface-based approach for file writing means we can switch between different file types by just creating new implementations of the interface.

3. **Command Pattern Enhancements**:
    - Added new command classes for the new functionality.
    - Enhanced existing commands to work with multiple calendars.
    - Improved error handling and validation.

   **Justification**
    - New classes are introduces for create, edit and use calendar to keep the design extensible and modular.
    - In the IController interface the `create` method is already present. This is used to extend the functionality for `create event|events` and `create calendar`.
    - Exceptions returned from new concrete implementation of Command classes are handled within the main calling CalendarController class.

4. **Timezone Management**:
    - Implemented full timezone support using Java's ZoneId.
    - Enhanced date/time handling to support timezone conversions.
    - Ensured proper event time adjustment when copying timezones.

   **Justification**
    - Implemented as per the assignemnt5 requirements and specifications.
    - Used Piazza information to cover edge cases and resolve doubts.

## Additional Notes

- The `res/` folder contains a screenshot of the Google Calendar with imported events, a list of valid and invalid commands, and a class diagram.

- **We chose to implement an Interval Tree for event storage to optimize conflict detection. This introduced complexity, leading to time constraints in achieving complete PIT mutation coverage. We prioritized functional correctness and testing of core features.**
