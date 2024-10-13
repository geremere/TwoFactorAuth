# Project ReadMe

## Features

- Code regeneration and resending should be available after 5 minutes.
- Code validation.
- Code expiration: the code is valid for 5 minutes and Maximum login attempts: no more than 3 attempts per code. If exceeded, wait for the code to be resent.
  - `spring.cache.code.value: 300000` (300 second or 5 min).
  - `spring.cache.code.limit: 3` 
- Error codes are implemented as 401 and 500.
  - `AuthException` - 401 
  - `BaseException` - 500
- Additional spam protection by IP is implemented:
    - `spring.cache.ip.limit: 100` within a time delta of
    - `spring.cache.ip.value=5000` (5 seconds).
- Also was implemented a controller method to check success authorization `/api/v1/check`
  - There was implemented 3 roles  
    - `ROLE_PRE_AUTHORIZED`
    - `ROLE_AUTHORIZED`
    - `ROLE_UNAUTHORIZED`
  - Also user session will be expired. Request renew expire time.
    - `spring.cache.auth=3600000` (1 hour)
- Codes are sending via Mail smtp yandex server. You could change credentials just for your smtp server
  - `spring.mail.host: smtp.yandex.com`
  - `spring.mail.port: 465`
  - `spring.mail.username: rad.imamow`
  - `spring.mail.password: twwfkkemgzordxzs`
  - `spring.mail.protocol: smtps` please be careful if you change smtp server maybe you will need to change protocol.