# TempMail

A fully containerized disposable email service. Users get unique temporary email addresses that receive mail in real time.

<img width="1314" height="467" alt="image" src="https://github.com/user-attachments/assets/f4037624-b239-49d3-8d0a-8f3d9658e813" />

## Features
- Generates a unique email address for every user
- Real-time inbox updating via Postgres notifications and server-side events
- Customizable wordlists and UI texts
- Deployable with Docker compose

## Requirements

For deploying:
- Docker and Docker compose

For building:
- JDK 25
- Node (not sure about minimum version, I am using 20.19.2)

## Deployment

1. Clone the repository
```
git clone https://github.com/Henkkagg/TempMail.git && cd TempMail
```

2. Configure environment variables
```
cp example.env .env
```
Of course change the .env to your needs, but you can start out by trying out the example.env!

3. Build the Ktor container
```
./build.sh
```
This builds the react app, copies its files to Ktor's static directory, and builds the container.

4. Start containers
```
docker compose up -d
```

With the default configuration, only SMTP port 25 is exposed to internet. Postgres is only accessible within the Docker interface. Ktor's port 8080 is open locally. I recommend setting up Nginx for serving Ktor, as you can easily automate TLS certificate renewing with Let'sEncrypt.

## Architecture

The service consists of three containers:
- Postfix, receives emails and delivers them to database
- PostgreSQL, the database
- Ktor, serves react app and reads emails from database

## How things work

### Address generation
Each email address consists of two words and a number 0-999. This means that the number of unique addresses is the wordlists multiplied with each other, multiplied with 1 000. If there are for example billion addresses, every number ranging from 1 to billion can be mapped to a single address.

To create that random number, Linear Congruential Generator (LCG) is used. It's a function which gives a pseudo-random value from a known range, the range depending on values in the function. Whenever the function is run again, it's guaranteed to give a unique value, never repeating the previous ones (until the whole range is exhausted).
It sounds like magic, but for what it does it's surpsisingly simple, especially when combined with Hull-Dobell theorem. I recommend reading this article, as I found it the best at describing it: https://www.bohrium.com/en/sciencepedia/feynman/keyword/hull_dobell_theorem

### Real-time delivery
When user opens the page, SSE connection is established with the Ktor backend. Postfix pipes all incoming emails to a Python script which inserts them into PostgreSQL. The database has a notification channel, and a notification is sent each time a new email arrives. Ktor is listening
to these notifications, and sends any new emails relevant to the established SSEs.

### Session management
Users receive a new email addresses if they revisit the page after 10 minutes of inactivity (and of course when they visit for the first time!). Ktor puts the assigned address into JWT, which is passed as a cookie. The react app refreshes the cookie every 10 seconds with a request to /api/token.
The idea is that for new registration needs, users always get a new email address. However, if they refresh the page or need to revisit their mailbox within 10 minutes of leaving the site, they can still access their old mail.
