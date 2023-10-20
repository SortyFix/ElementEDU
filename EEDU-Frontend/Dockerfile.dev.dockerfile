# The Node.js image is extended to have the Chrome browser which is required for some Angular tests.
FROM node:18.10-alpine AS dev-build

# Set the working directory to /app directory.
WORKDIR /app

# Copy package.json and package-lock.json files to the container.
COPY package.json package-lock.json ./

# Install dependencies (along with Angular CLI) in the container.
RUN npm install -g @angular/cli && npm install

# Copy necessary source files to the /app directory in the container.
COPY . .

# This is the port that our Angular app will run on within the container.
EXPOSE 4200

# Start the Angular app.
CMD ["npm", "start"]
