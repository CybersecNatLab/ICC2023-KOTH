FROM node:14-alpine

COPY package.json /
COPY package-lock.json /
WORKDIR /
RUN npm i

RUN mkdir /frontend
COPY frontend/package.json /frontend/package.json
COPY frontend/package-lock.json /frontend/package-lock.json
COPY frontend/tsconfig.json /frontend/tsconfig.json
WORKDIR /frontend
RUN npm i

COPY frontend/src /frontend/src
COPY frontend/public /frontend/public
RUN npm run build
RUN cp -r /frontend/build /static

WORKDIR /
RUN rm -rf /frontend

COPY backend/index.js /


ENTRYPOINT ["node", "index.js"]
