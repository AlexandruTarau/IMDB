# IMDB

## Short Description
Implementation of a database IMDB-style using OOP and JAVA.

## Technologies Used
  - Java
  - Swing

## How to Run / Use
  Run IMDB class.

## Implementation details
  - **Data storage**
   The data about users, productions, actors and requests is stored in JSON format.
  - **Functionalities**
   We have implemented 3 types of users (Regular, Contributor, and Admin), each with its role in this application and various functionalities:

   - Viewing productions in the system + filtering
   - Viewing actors in the system + sorting
   - Notification system
   - Searching for a specific movie/series/actor
   - A favorites list where you can add movies/series/actors
   - Ability to write requests to contributors or admins
   - Ability to add movies/series/actors to the system (Contributors and Admins)
   - Updating productions and actors in the system (Contributors and Admins)
   - Adding and deleting users from the system (Admins)
   - Compensation system for activity within the application (experience)

  - **Design Patterns**
   To simplify the implementation of functionalities, we used various Design Patterns:

   - SINGLETON: (IMDB class) Provides the ability to access the same IMDB 
class from anywhere in the project, facilitating the use of data from the database and ensuring the existence of only one class of this type throughout the program.
   - BUILDER: (InformationBuilder) Provides the ability to gradually build an Information class with mandatory and optional attributes, offering flexibility in the initiation process.
   - FACTORY: (UserFactory) Facilitates the instantiation of users in the system based on their type (AccountType).
   - OBSERVER: (Observer) Focuses on managing notifications, connecting different parts of the application.
   - STRATEGY: (ExperienceStrategy) Facilitates the implementation of different ways users receive experience based on their actions.

  - **Graphical Interface**
   The program also offers an easily navigable, intuitive graphical interface that fulfills all the functionalities presented above. To start navigation, the user will need to enter their credentials. Then, they will be sent to the main page, where a few random productions will be recommended. Here, the user can view information, access the actors' page, navigate through recommendations, filter options, add productions to the favorites list, make a request, or add a rating. They can also search for any actor or production using the "Search" bar. Finally, they can access the main menu where all functionalities are located.

