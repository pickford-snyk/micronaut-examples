FROM node:26.3.1-trixie

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY package.json /usr/src/app/
RUN npm install

CMD [ "npm", "start" ]