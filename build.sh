#!/bin/bash

#Clear old frontend stuff
rm -rf ktor/src/main/resources/static
rm ktor/src/main/resources/adjectives.txt
rm ktor/src/main/resources/nouns.txt

cp adjectives.txt ktor/src/main/resources
cp nouns.txt ktor/src/main/resources

grep '^VITE_' .env > react/.env
cd react && npm run build
cp -r dist/ ../ktor/src/main/resources/static/
cd ../ktor && ./gradlew publishImageToLocalRegistry