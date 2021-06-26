# Book Management System

A simple management system where it serves admin's requests to create/update/delete/get book/s. Users could also register, and borrow/return a book/s.

## Getting Started
### Prerequisite/s
- Java 8
- Maven

### Properties

One can customize the configurations by editing the properties defined in `application.yml` file.

#### Embedded Redis

The application will use Redis as its data storage. It can use an actual Redis connection running on a server, or use the embedded Redis within the application.

To use the embedded Redis, the property below must be set to true.
```yml
embedded:
   redis: true
```
Do note that embedded Redis will not persist any data, so everytime the application is restarted previous data will be gone.

#### Non-embedded Redis

To use an actual instance of Redis server, update the properties below.
```yml
spring:
   application:
      name: BookingManagementSystem
   redis:
      host: localhost
      port: 6379
      password:
      timeout: 60000
```

Set the embedded Redis property to false.
```yml
embedded:
  redis: false
```

#### App Configurations

The borrow expiry date, and number of books a user can borrow are configurable and can be set thru the properties below.
```yml
book:
  maximumBorrowedByUser: 5
  borrowDuration: 30
```

#### Logging

Logging level, and writing to a file can be set thru the properties below.
```yml
logging:
  level:
    org:
      springframework: INFO
    io:
      app: TRACE
  file:
    name: application.log
```

### Running the application
1. Build the project by executing the command below.
    ```sh
    mvn clean package
    ```
4. After finishing the build, execute the command below to run the application.
    ```sh
    java -jar /target/BookManagementSystem-1.0-SNAPSHOT.jar
    ```

## BookingManagementSystem Application

### User
#### Create User
```sh
POST /api/v1/users

Request Body:
{
    "id": "tonystark",
    "name": "Tony Stark"
}
```

The endpoint will return an `HTTP Status 400` in case the user ID is already existing.

`Curl Command`:
```
curl -d '{"id":"tonystark", "name":"Tony Stark"}' -H "Content-Type: application/json" -X POST http://localhost:8080/api/v1/users
```

### Book
#### Get Book

The endpoint will have 2 parameters named as `name` and `isbn`. These are query parameters if one wants to search a specific book. Do note that for this iteration of the application, one must indicate the `complete` name or isbn to get a match. If the parameters are not defined, then it will just query all books in the system.

```sh
GET /api/v1/books

Response Body:
[
    {
        "id": "524a71c6-9d52-41cc-8e83-7d3ed3a409a9",
        "isbn": "61-616-01-4",
        "name": "Harry Potter and the Sorcerer's Stone",
        "author": "JK Rowling",
        "publishDate": "1997-06-26T00:00:00Z",
        "summary": "Lorem Ipsum"
    }
]
```

`Curl Command`:
```
curl http://localhost:8080/api/v1/books
```

`What can be improved`:
- Add a pagination
- Make the query be able to do a match of keywords for search results

#### Create Book
```sh
POST /api/v1/books

Request Body:
{
    "name": "Harry Potter and the Sorcerer's Stone",
    "author": "JK Rowling",
    "publishDate": "1997-06-26T00:00:00Z",
    "summary": "Lorem Ipsum"
}
```

`Curl Command`:
```
curl -d '{"name":"Harry Potter and the Sorcerer's Stone", "author":""JK Rowling"", "publishDate": ""1997-06-26T00:00:00Z"", "summary": "Lorem Ipsum"}' -H "Content-Type: application/json" -X POST http://localhost:8080/api/v1/books
```

#### Update Book

```sh
PUT /api/v1/books

Request Body:
{
    "id": "524a71c6-9d52-41cc-8e83-7d3ed3a409a9",
    "isbn": "61-616-01-4",
    "name": "Harry Potter and the Sorcerer's Stone",
    "author": "JK Rowling",
    "publishDate": "1997-06-26T00:00:00Z",
    "summary": "Lorem Ipsum 2"
}
```
The endpoint will return an `HTTP 404` when the book ID is not existing. It will also return an `HTTP 400` when the request body doesn't have the `bookId` field.

`Curl Command`:
```
curl -d '{"id": "524a71c6-9d52-41cc-8e83-7d3ed3a409a9", "isbn": "61-616-01-4", "name":"Harry Potter and the Sorcerer's Stone", "author":""JK Rowling"", "publishDate": ""1997-06-26T00:00:00Z"", "summary": "Lorem Ipsum"}' -H "Content-Type: application/json" -X PUT http://localhost:8080/api/v1/books
```

#### Delete Book

```sh
DELETE /api/v1/books/{id}
```
The endpoint will return an `HTTP 404` when the book ID is not existing.

`Curl Command`:
```
curl -X DELETE http://localhost:8080/api/v1/users
```

#### Borrow a Book

`Rules`:
- A user can only borrow up to a maximum of 5(configurable) books.
- A borrowed book has a duration of 30(configurable) days for it to be returned.
- User must be registered, otherwise it will throw an `HTTP 400`
- A book can only be lent out to a single user
- Book ID must exist
- A maximum of 5(configurable) books can be borrowed in one API request.
- In the book payload of the response, it will include the date the book is to be returned.

```sh
PUT /api/v1/books/operations

Request Body:
{
    "userId": "tonystark",
    "operation": "borrow",
    "bookIds": ["a0de1c74-e759-4493-9980-9e9b6edb2ab5"]
}

Response Body:
{
    "userId": "tonystark",
    "bookResponseList": [
        {
            "bookId": "a0de1c74-e759-4493-9980-9e9b6edb2ab5",
            "bookName": "Harry Potter and the Sorcerer's Stone",
            "toBeReturnedDate": "2021-04-15T23:16:49.967"
        }
    ]
}
```

#### Return a Book

`Rules`:
- User must be registered, otherwise it will throw an `HTTP 400`
- Book ID must exist
- In the book payload of the response, it will include the date it was returned.

```sh
PUT /api/v1/books/operations

Request Body:
{
    "userId": "tonystark",
    "operation": "return",
    "bookIds": ["a0de1c74-e759-4493-9980-9e9b6edb2ab5"]
}

Response Body:
{
    "userId": "tonystark",
    "bookResponseList": [
        {
            "bookId": "a0de1c74-e759-4493-9980-9e9b6edb2ab5",
            "bookName": "Harry Potter and the Sorcerer's Stone",
            "returnedDate": "2021-04-15T23:16:49.967"
        }
    ]
}
```

## Some improvements that can be done
- Add a penalty logic when the user wasn't able to return the book on its expiry date.
- Add JUnit

## Tech Stack
- Java 8
- Springboot
- Spring Data
- Redis
- Maven
