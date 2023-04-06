# Account Service

### Description

[Hyperskill project](https://hyperskill.org/projects/217) with a focus on Spring Security

### Main features

- Authentication
- Authorization
- Registering security events
- Modern information security standards
- Using the H2 database
- Role model
- Spring Beans, Components and Configurations

### Role model

|                           | Anonymous | User | Accountant | Administrator | Auditor |
|---------------------------|-----------|------|------------|---------------|---------|
| POST api/auth/signup      | +         | +    | +          | +             | +       |
| POST api/auth/changepass  | -         | +    | +          | +             | -       |
| GET api/empl/payment      | -         | +    | +          | -             | -       |
| POST api/acct/payments    | -         | -    | +          | -             | -       |
| PUT api/acct/payments     | -         | -    | +          | -             | -       |
| GET api/admin/user        | -         | -    | -          | +             | -       |
| DELETE api/admin/user     | -         | -    | -          | +             | -       |
| PUT api/admin/user/role   | -         | -    | -          | +             | -       |
| PUT api/admin/user/access | -         | -    | -          | +             | -       |
| GET api/security/events   | -         | -    | -          | -             | +       |

### Installation

To get a local copy up and running follow these simple example steps.

1. Clone repository
    ```shell
    git clone https://github.com/skosarevv/AccountService
    ```
2. Go to project directory
    ```shell
    cd .\AccountService
    ```
3. Start project using Gradle
    ```shell
    gradle bootRun
    ```