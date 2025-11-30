# userapi

Spring Boot API - registration and retrieval of users (H2 embedded database)

## Build

Prerequisites: Java 17, Maven

mvn spring-boot:runApp on http://localhost:8078

H2 console: http://localhost:8078/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## Endpoints

### POST /api/users
Create a new user

**Request Body:**
{
"username": "JeanDupont",
"birthdate": "1990-05-15",
"country": "France",
"phone": "0123456789",
"gender": "MALE"
}**Response:** 201 Created
{
"id": 1,
"username": "JeanDupont",
"birthdate": "1990-05-15",
"country": "France",
"phone": "0123456789",
"gender": "MALE"
}### GET /api/users/{id}
Get user by ID

**Response:** 200 OK
{
"id": 1,
"username": "JeanDupont",
"birthdate": "1990-05-15",
"country": "France",
"phone": "0123456789",
"gender": "MALE"
}**Response:** 404 Not Found (if user doesn't exist)son
{
"error": "Utilisateur non trouvé"
}## Validation Rules

- **Username**: Required (not blank)
- **Birthdate**: Required, must be in the past, user must be >= 18 years old
- **Country**: Required, must contain "fr" (France only)
- **Phone**: Optional, must match French format: `0XXXXXXXXX` or `+33XXXXXXXXX`
- **Gender**: Optional, must be one of: `MALE`, `FEMALE`, `OTHER`

## Error Responses

### 400 Bad Request - Validation Errors
{
"username": "username obligatoire",
"country": "Seuls les résidents français peuvent s'inscrire",
"birthdate": "L'utilisateur doit être majeur (>=18 ans)"
}### 400 Bad Request - Invalid Gender
{
"error": "Gender invalide : INVALID"
}
### 404 Not Found
{
"error": "Utilisateur non trouvé"
}
## Tests

## Tests

### Run all testsash
mvn test
### Run specific test class
mvn test -Dtest=UserServiceValidationTest
mvn test -Dtest=UserControllerIntegrationTest### Run tests with coverage
mvn clean test### Test Structure
- **Unit tests** (`UserServiceValidationTest.java`): Test service layer with mocked repository
    - Validation tests (age, country, date, gender)
    - Success cases
    - Error handling

- **Integration tests** (`UserControllerIntegrationTest.java`): Test full HTTP requests
    - POST /api/users (create user)
    - GET /api/users/{id} (get user)
    - Validation error responses
    - Not found responses

### Test Results
After running tests, results are available in:
- Console output

## Features

- ✅ H2 in-memory database
- ✅ Jakarta Validation with custom validators
- ✅ AOP logging (automatic method execution logging)
- ✅ Global exception handling
- ✅ Unit and integration tests
- ✅ RESTful API design

## Notes

- Only residents of France (country must contain "fr") and >=18 years old can create an account.
- Validation errors return 400 with the list of field errors.
- Phone number is optional but must be in French format if provided.
- Gender is optional but must be valid enum value if provided.
- All method calls are automatically logged via AOP.