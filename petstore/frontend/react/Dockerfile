FROM node:25.9.0-bookworm

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY package.json /usr/src/app/
RUN npm install

CMD [ "npm", "start" ]