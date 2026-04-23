#!/bin/bash

#Clear old frontend stuff
rm -rf ktor/src/main/resources/static
rm ktor/src/main/resources/wordlist1.txt
rm ktor/src/main/resources/wordlist2.txt

cp wordlist1.txt ktor/src/main/resources
cp wordlist2.txt ktor/src/main/resources

grep '^VITE_' .env > react/.env
cd react && npm install && npm run build
cp -r dist/ ../ktor/src/main/resources/static/
cd ../ktor && ./gradlew publishImageToLocalRegistry