# Dockerfile for spring-todo
FROM node:18
WORKDIR /app
COPY . .
RUN npm install
CMD ["node", "server.js"]