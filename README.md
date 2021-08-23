## Installation requirements
1. *nodejs* (version >= 12)
2. *npm* (version >= 5)
3. *ng* (version >= 10)
3. *angular-cli* (version >= 10)
4. *docker* (version up to date)
5. *docker-compose* (version up to date)

## Build Steps
1. Clone repository
2. Build Angular application
3. Build backend services
4. Build and run docker compose
5. Open browser on http://localhost

## Run
1. Build backend, frontend and docker images: `./build.sh`
2. Run docker images: `docker-compose up`
3. Login using credentials:
    - *username*: `user.test`
    - *password*: `password`
